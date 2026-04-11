package damA51388.galeriaaleatoria.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import damA51388.galeriaaleatoria.databinding.ItemFavoriteThumbnailBinding
import damA51388.galeriaaleatoria.model.ImageItem

/**
 * Adapter for the top Favorites bar (Extension 4)
 */
class FavoritesAdapter(
    private val onItemClick: (ImageItem) -> Unit
) : ListAdapter<ImageItem, FavoritesAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteThumbnailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemFavoriteThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImageItem) {
            Glide.with(binding.favThumbnail.context)
                .load(item.url)
                .circleCrop()
                .into(binding.favThumbnail)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(old: ImageItem, new: ImageItem) = old.id == new.id
        override fun areContentsTheSame(old: ImageItem, new: ImageItem) = old == new
    }
}
