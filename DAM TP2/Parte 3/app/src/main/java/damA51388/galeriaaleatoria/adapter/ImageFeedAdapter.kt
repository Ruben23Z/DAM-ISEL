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
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Adapter for the dog image feed.
 */
class ImageFeedAdapter(
    private val onLikeStateChanged: (ImageItem) -> Unit = {},
    private val onItemClicked: (ImageItem) -> Unit = {}
) : ListAdapter<ImageItem, ImageFeedAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemImageCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val gestureDetector = GestureDetector(binding.root.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (!item.isLiked) {
                        item.isLiked = true
                        onLikeStateChanged(item)
                    }
                    showHeartAnimation()
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position))
                }
                return true
            }
        })

        fun bind(item: ImageItem) {
            binding.root.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
                v.performClick()
                true
            }

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

        private fun showHeartAnimation() {
            binding.likeAnimationHeart.visibility = View.VISIBLE
            binding.likeAnimationHeart.alpha = 1f
            binding.likeAnimationHeart.scaleX = 0f
            binding.likeAnimationHeart.scaleY = 0f

            binding.likeAnimationHeart.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction {
                    binding.likeAnimationHeart.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            binding.likeAnimationHeart.visibility = View.GONE
                        }
                        .start()
                }
                .start()
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(old: ImageItem, new: ImageItem) = old.id == new.id
        override fun areContentsTheSame(old: ImageItem, new: ImageItem) = old == new
    }
}
