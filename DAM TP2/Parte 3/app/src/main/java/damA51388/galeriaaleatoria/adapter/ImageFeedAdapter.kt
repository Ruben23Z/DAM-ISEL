package damA51388.galeriaaleatoria.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import damA51388.galeriaaleatoria.databinding.ItemImageCardBinding
import damA51388.galeriaaleatoria.model.ImageItem

/**
 * Adapter for the dog image feed.
 */
class ImageFeedAdapter : ListAdapter<ImageItem, ImageFeedAdapter.ImageViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(
        private val binding: ItemImageCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageItem) {
            // Breed name instead of username
            binding.usernameText.text = item.displayBreed
            binding.descriptionText.text = "A beautiful ${item.displayBreed}"
            binding.tagsText.text = "#dog #${item.breed.replace("-", "")}"

            binding.itemLoadingIndicator.visibility = View.VISIBLE

            Glide.with(binding.imageMain.context)
                .load(item.url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.itemLoadingIndicator.visibility = View.GONE
                        return false
                    }
                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.itemLoadingIndicator.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.imageMain)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(old: ImageItem, new: ImageItem) = old.id == new.id
        override fun areContentsTheSame(old: ImageItem, new: ImageItem) = old == new
    }
}
