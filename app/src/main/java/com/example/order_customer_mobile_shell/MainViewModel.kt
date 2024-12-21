import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.network.ApiClient
import com.example.order_customer_mobile_shell.network.AuthService
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authService: AuthService,
    private val apiClient: ApiClient = ApiClient(authService)
) : ViewModel() {

    private val _clients = MutableStateFlow<List<ClientRequest>>(emptyList())
    val clients: StateFlow<List<ClientRequest>> = _clients

    var query = MutableStateFlow("")
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    init {
        fetchClients()
    }

    fun fetchClients(startId: Int = 0) {
        viewModelScope.launch {
            apiClient.getClients(startId, query.value) { success, response ->
                if (success && response != null) {
                    _clients.value = parseClientResponse(response)
                }
            }
        }
    }

    fun addClient(client: ClientRequest) {
        viewModelScope.launch {
            apiClient.addClient(client) { success, _ ->
                if (success) fetchClients()
            }
        }
    }

    fun editClient(id: Int, updatedClient: ClientRequest) {
        viewModelScope.launch {
            apiClient.editClient(id, updatedClient) { success, _ ->
                if (success) fetchClients()
            }
        }
    }

    fun deleteClient(id: Int) {
        viewModelScope.launch {
            apiClient.deleteClient(id) { success ->
                if (success) fetchClients()
            }
        }
    }



private fun parseClientResponse(response: String): List<ClientRequest> {
        val type = Types.newParameterizedType(List::class.java, ClientRequest::class.java)
        val adapter = moshi.adapter<List<ClientRequest>>(type)
        return adapter.fromJson(response) ?: emptyList()
    }
}
