package damA51388.galeriaaleatoria.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import damA51388.galeriaaleatoria.model.ImageItem
import damA51388.galeriaaleatoria.repository.ImageRepository
import kotlinx.coroutines.launch

/**
 * Step 6 – ViewModel (docs/06_architecture.md)
 *
 * Sits between the UI and the Repository.
 * Survives config changes (rotations).
 * The Activity only observes LiveData — zero business logic in Activity.
 */
class ImageViewModel(
    private val repository: ImageRepository = ImageRepository()
) : ViewModel() {

    // ── LiveData exposed to the UI ─────────────────────────────────────────

    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /** Null = no error; non-null = message to show the user. */
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // ── Actions ────────────────────────────────────────────────────────────

    init {
        loadImages()          // fetch on first creation
    }

    /**
     * Loads [count] random images from Dog CEO API.
     * Step 11: calling this again from SwipeRefresh replaces the list.
     * Step 12: manages the loading indicator state.
     * Step 13: catches errors and exposes a message.
     */
    fun loadImages(count: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.fetchRandomImages(count)
                _images.value = result
            } catch (e: Exception) {
                // Step 13 – graceful error handling
                _errorMessage.value = "Erro ao carregar imagens: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Called when the user pulls to refresh (Step 11). */
    fun refresh() = loadImages()
}
