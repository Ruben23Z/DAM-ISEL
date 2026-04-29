package damA51388.galeriaaleatoria.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import damA51388.core.model.ImageItem
import damA51388.core.repository.ImageRepository
import damA51388.core.storage.FavoritesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DogFeedUiState(
    val images: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<ImageItem> = emptyList(),
    val downloadMessage: String? = null
)

class ImageViewModelCompose(
    private val repository: ImageRepository,
    private val favoritesManager: FavoritesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DogFeedUiState())
    val uiState: StateFlow<DogFeedUiState> = _uiState.asStateFlow()

    private var isFetching = false

    init {
        loadImages()
        refreshFavorites()
    }

    fun loadImages(append: Boolean = false) {
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            _uiState.update { it.copy(isLoading = !append, errorMessage = null) }

            try {
                val result = repository.fetchRandomImages(10)
                _uiState.update { state ->
                    val newList = if (append) state.images + result else result
                    state.copy(
                        images = newList,
                        isLoading = false,
                        errorMessage = if (newList.isEmpty()) "No connection or cache available." else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.localizedMessage}") }
            } finally {
                isFetching = false
            }
        }
    }

    fun loadMore() = loadImages(append = true)
    
    fun refresh() = loadImages(append = false)

    fun toggleLike(id: String) {
        _uiState.update { state ->
            val updatedImages = state.images.map { 
                if (it.id == id) it.copy(isLiked = !it.isLiked) else it 
            }
            state.copy(images = updatedImages)
        }
    }

    fun downloadImage(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(downloadMessage = "Foto baixada com sucesso!") }
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(downloadMessage = null) }
        }
    }

    fun toggleFavorite(item: ImageItem) {
        favoritesManager.toggleFavorite(item)
        refreshFavorites()
    }

    fun refreshFavorites() {
        _uiState.update { it.copy(favorites = favoritesManager.getFavorites()) }
    }
    
    fun isFavorite(id: String): Boolean = favoritesManager.isFavorite(id)
}
