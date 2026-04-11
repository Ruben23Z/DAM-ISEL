package damA51388.galeriaaleatoria

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import damA51388.galeriaaleatoria.databinding.ActivityImageDetailsBinding
import damA51388.galeriaaleatoria.model.ImageItem

/**
 * Extension 3 — Image Details Screen
 *
 * Displays detailed information about a selected dog.
 */
class ImageDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_IMAGE_ITEM", ImageItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_IMAGE_ITEM") as? ImageItem
        }

        if (item != null) {
            setupUI(item)
        } else {
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupUI(item: ImageItem) {
        binding.detailBreedText.text = item.displayBreed
        
        val host = Uri.parse(item.url).host ?: "images.dog.ceo"
        binding.detailUrlText.text = getString(R.string.source_label, host)

        Glide.with(this)
            .load(item.url)
            .into(binding.detailImage)

        binding.shareDogButton.setOnClickListener {
            val shareText = getString(R.string.share_text_template, item.displayBreed, item.url)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_full_label)))
        }
    }
}
