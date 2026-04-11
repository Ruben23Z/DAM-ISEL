package damA51388.galeriaaleatoria

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import damA51388.galeriaaleatoria.adapter.FavoritesAdapter
import damA51388.galeriaaleatoria.adapter.ImageFeedAdapter
import damA51388.galeriaaleatoria.databinding.ActivityMainBinding
import damA51388.galeriaaleatoria.model.ImageItem
import damA51388.galeriaaleatoria.network.NetworkMonitor
import damA51388.galeriaaleatoria.storage.FavoritesManager
import damA51388.galeriaaleatoria.viewmodel.ImageViewModel
import kotlinx.coroutines.launch

/**
 * MainActivity
 *
 * Responsibilities:
 * • Inflate layout via ViewBinding
 * • Set up ViewPager2 with ImageFeedAdapter
 * • Set up Favorites Bar (Extension 4)
 * • Monitor network connectivity (Extension 6)
 * • Navigate to Details (Extension 3)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ImageViewModel by viewModels()
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var networkMonitor: NetworkMonitor
    
    private val feedAdapter = ImageFeedAdapter(
        onLikeStateChanged = { item ->
            updateLikeUI(item)
        },
        onItemClicked = { item ->
            openDetails(item)
        }
    )

    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootContainer) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        favoritesManager = FavoritesManager(this)
        networkMonitor = NetworkMonitor(this)

        setupFavoritesBar()
        setupViewPager()
        setupSwipeRefresh()
        setupActionButtons()
        observeViewModel()
        observeNetwork()
    }

    private fun setupFavoritesBar() {
        favoritesAdapter = FavoritesAdapter { favoriteItem: ImageItem ->
            openDetails(favoriteItem)
        }
        // Use safe call ?. as the view might be null in some layout configurations
        binding.recyclerViewFavorites?.adapter = favoritesAdapter
        refreshFavoritesUI()
    }

    private fun refreshFavoritesUI() {
        val favs = favoritesManager.getFavorites()
        favoritesAdapter.submitList(favs)
        binding.recyclerViewFavorites?.visibility = if (favs.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun observeNetwork() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkMonitor.isOnline.collect { isOnline ->
                    binding.offlineBanner.visibility = if (isOnline) View.GONE else View.VISIBLE
                    if (isOnline && (viewModel.images.value.isNullOrEmpty())) {
                        viewModel.refresh()
                    }
                }
            }
        }
    }

    private fun setupActionButtons() {
        binding.likeContainer.setOnClickListener {
            val position = binding.viewPagerFeed.currentItem
            if (position in 0 until feedAdapter.itemCount) {
                val item = feedAdapter.currentList[position]
                item.isLiked = !item.isLiked
                updateLikeUI(item)
            }
        }

        binding.saveContainer.setOnClickListener {
            val position = binding.viewPagerFeed.currentItem
            if (position in 0 until feedAdapter.itemCount) {
                val item = feedAdapter.currentList[position]
                val isNowFav = favoritesManager.toggleFavorite(item)
                
                updateSaveUI(isNowFav)
                refreshFavoritesUI()
                
                val message = if (isNowFav) {
                    getString(R.string.saved_ok_message)
                } else {
                    getString(R.string.remove_label)
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.downloadContainer.setOnClickListener {
            val position = binding.viewPagerFeed.currentItem
            if (position in 0 until feedAdapter.itemCount) {
                val item = feedAdapter.currentList[position]
                downloadImage(item.url, "Dog_${item.breed}_${item.id}.jpg")
            }
        }
    }

    private fun openDetails(item: ImageItem) {
        val intent = Intent(this, ImageDetailsActivity::class.java).apply {
            putExtra("EXTRA_IMAGE_ITEM", item)
        }
        startActivity(intent)
    }

    private fun updateLikeUI(item: ImageItem) {
        binding.likeButton.setImageResource(if (item.isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
        binding.likeCountText.text = if (item.isLiked) "1" else "0"
    }

    private fun updateSaveUI(isFavorite: Boolean) {
        binding.saveButton.setImageResource(
            if (isFavorite) android.R.drawable.btn_star_big_on 
            else R.drawable.ic_star_outline
        )
    }

    private fun downloadImage(url: String, filename: String) {
        Toast.makeText(this, "A transferir...", Toast.LENGTH_SHORT).show()
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveBitmapToGallery(resource, filename)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Required override
                }
            })
    }

    private fun saveBitmapToGallery(bitmap: Bitmap, filename: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/DogFeed")
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            try {
                contentResolver.openOutputStream(it).use { stream ->
                    if (stream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        Toast.makeText(this, R.string.download_ok_message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao guardar imagem", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewPager() {
        binding.viewPagerFeed.adapter = feedAdapter
        binding.viewPagerFeed.offscreenPageLimit = 2

        binding.viewPagerFeed.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val totalItems = feedAdapter.itemCount
                if (position in 0 until totalItems) {
                    val item = feedAdapter.currentList[position]
                    updateLikeUI(item)
                    updateSaveUI(favoritesManager.isFavorite(item.id))
                    
                    if (position >= totalItems - 3 && totalItems > 0) {
                        viewModel.loadMore()
                    }
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.tabForYou.setOnClickListener {
            viewModel.refresh()
            Toast.makeText(this, "A atualizar feed...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.images.observe(this) { images ->
            feedAdapter.submitList(images)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
