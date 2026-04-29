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
 * Classe ImageFeedAdapter: Responsável pela gestão e visualização do fluxo principal de imagens.
 * Implementa a lógica de interação por gestos e carregamento assíncrono de recursos multimédia.
 */
class ImageFeedAdapter(
    // Definição de funções de retorno para eventos de preferência e interação direta
    private val onLikeStateChanged: (ImageItem) -> Unit = {},
    private val onItemClicked: (ImageItem) -> Unit = {}
) : ListAdapter<ImageItem, ImageFeedAdapter.ViewHolder>(DiffCallback) {

    /**
     * Criação de novas instâncias de ViewHolder para os elementos do fluxo.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Insuflação da estrutura visual do cartão de imagem
        val binding = ItemImageCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    /**
     * Associação dos dados de um item específico à sua representação visual.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * Classe interna ViewHolder: Gere a lógica de interação e renderização de um cartão de imagem.
     */
    inner class ViewHolder(
        private val binding: ItemImageCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        // Detetor de gestos para capturar eventos de toque duplo e toque simples
        private val gestureDetector = GestureDetector(binding.root.context, object : GestureDetector.SimpleOnGestureListener() {
            /**
             * Manipula a ocorrência de um toque duplo (Double Tap).
             */
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    // Ativa o estado de "Gosto" se este ainda não estiver ativo
                    if (!item.isLiked) {
                        item.isLiked = true
                        onLikeStateChanged(item)
                    }
                    showHeartAnimation() // Executa a animação visual de feedback
                }
                return true
            }

            /**
             * Manipula a confirmação de um toque simples (Single Tap).
             */
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(position)) // Despoleta a navegação para detalhes
                }
                return true
            }
        })

        /**
         * Estabelece a ligação entre o modelo de dados e os componentes da interface.
         */
        fun bind(item: ImageItem) {
            // Configuração do ouvinte de eventos tácteis para o cartão
            binding.root.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
                v.performClick()
                true
            }

            // Atribuição de metadados textuais aos componentes de visualização
            binding.usernameText.text = item.displayBreed
            binding.descriptionText.text = "A beautiful ${item.displayBreed}"
            binding.tagsText.text = "#dog #${item.breed.replace("-", "")}"

            // Ativação do indicador de progresso durante o carregamento da imagem
            binding.itemLoadingIndicator.visibility = View.VISIBLE

            // Processamento da imagem remota via biblioteca Glide
            Glide.with(binding.imageMain.context)
                .load(item.url)
                .transition(DrawableTransitionOptions.withCrossFade()) // Transição suave por desvanecimento
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Ocultação do indicador em caso de insucesso no carregamento
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
                        // Ocultação do indicador após a conclusão bem-sucedida do carregamento
                        binding.itemLoadingIndicator.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.imageMain)
        }

        /**
         * Orquestra uma animação sequencial de escala e opacidade para feedback de "Gosto".
         */
        private fun showHeartAnimation() {
            binding.likeAnimationHeart.visibility = View.VISIBLE
            binding.likeAnimationHeart.alpha = 1f
            binding.likeAnimationHeart.scaleX = 0f
            binding.likeAnimationHeart.scaleY = 0f

            // Início da animação de expansão
            binding.likeAnimationHeart.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .withEndAction {
                    // Início da animação de contração e desvanecimento
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

    /**
     * Implementação do DiffUtil para otimização da atualização da lista.
     */
    private object DiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(old: ImageItem, new: ImageItem) = old.id == new.id
        override fun areContentsTheSame(old: ImageItem, new: ImageItem) = old == new
    }
}
