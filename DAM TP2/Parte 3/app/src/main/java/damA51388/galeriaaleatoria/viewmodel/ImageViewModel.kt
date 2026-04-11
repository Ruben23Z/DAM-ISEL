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
 * ViewModel with Offline Support (Extension 6)
 */
class ImageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageRepository(application)

    private val _images = MutableLiveData<List<ImageItem>>()
    val images: LiveData<List<ImageItem>> = _images

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var isFetching = false

    init {
        loadImages()
    }

    fun loadImages(count: Int = 10, append: Boolean = false) {
        if (isFetching) return

        viewModelScope.launch {
            isFetching = true
            if (!append) _isLoading.value = true
            _errorMessage.value = null

            try {
                val result = repository.fetchRandomImages(count)
                if (result.isEmpty() && !append) {
                    // If API fails and cache is empty
                    _errorMessage.value = "Sem ligação e sem cache disponível."
                }
                
                if (append) {
                    val currentList = _images.value ?: emptyList()
                    _images.value = currentList + result
                } else {
                    _images.value = result
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
                isFetching = false
            }
        }
    }

    fun loadMore() = loadImages(append = true)
    fun refresh() = loadImages(append = false)
}
