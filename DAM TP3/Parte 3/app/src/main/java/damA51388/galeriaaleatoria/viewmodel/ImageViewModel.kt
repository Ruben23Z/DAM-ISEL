package damA51388.galeriaaleatoria.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import damA51388.galeriaaleatoria.model.ImageItem
import damA51388.galeriaaleatoria.repository.ImageRepository
import kotlinx.coroutines.launch

/**
 * Classe ImageViewModel: Atua como o mediador entre a camada de dados e a interface de utilizador.
 * Providencia o suporte para persistência de estado e gestão de operações assíncronas no ciclo de vida da aplicação.
 */
class ImageViewModel(application: Application) : AndroidViewModel(application) {

    // Instanciação do repositório para centralizar o acesso às fontes de dados (Rede e Cache)
    private val repository = ImageRepository(application)

    // Fluxo de dados reativo (LiveData) para a coleção de imagens exposta à UI
    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    // Indicador reativo do estado de carregamento para controlo de componentes de progresso
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Canal para propagação de mensagens de erro ou notificações de exceção
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Sentinela para evitar a execução concorrente e redundante de pedidos de rede
    private var isFetching = false

    init {
        // Invocação primordial para o povoamento inicial do fluxo de imagens
        loadImages()
    }

    /**
     * Orquestra a obtenção de imagens, permitindo a renovação total ou o incremento da lista existente.
     */
    fun loadImages(count: Int = 10, append: Boolean = false) {
        // Interrupção precoce se já existir uma operação de obtenção em curso
        if (isFetching) return

        // Execução da lógica de obtenção num contexto de corrotina associado ao ciclo de vida do ViewModel
        viewModelScope.launch {
            isFetching = true
            // Ativação do indicador visual apenas em carregamentos não incrementais
            if (!append) _isLoading.value = true
            _errorMessage.value = null // Reinicialização do estado de erro

            try {
                // Solicitação de dados ao repositório (abstrai a origem: remota ou local)
                val result = repository.fetchRandomImages(count)

                // Verificação de vacuidade de dados para emissão de alerta de indisponibilidade
                if (result.isEmpty() && !append) {
                    _errorMessage.value = "Inexistência de conectividade e de registos em cache."
                }

                // Estratégia de atualização: anexação ao final da lista ou substituição integral
                if (append) {
                    val currentList = _images.value ?: emptyList()
                    _images.value = currentList + result
                } else {
                    _images.value = result
                }
            } catch (e: Exception) {
                // Captura e reporte de anomalias ocorridas durante o processamento
                _errorMessage.value = "Ocorreu uma anomalia: ${e.localizedMessage}"
            } finally {
                // Reposição dos estados de controlo após a conclusão (sucesso ou falha)
                _isLoading.value = false
                isFetching = false
            }
        }
    }

    /**
     * Método de conveniência para despoletar o carregamento incremental (Paginação).
     */
    fun loadMore() = loadImages(append = true)

    /**
     * Método de conveniência para despoletar a renovação integral dos dados.
     */
    fun refresh() = loadImages(append = false)
}
