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
 * Classe FavoritesAdapter: Implementação do adaptador para a componente de listagem horizontal de favoritos.
 * Utiliza o paradigma ListAdapter para uma gestão eficiente de diferenciação de dados.
 */
class FavoritesAdapter(
    // Função de callback para propagar eventos de clique sobre os itens
    private val onItemClick: (ImageItem) -> Unit
) : ListAdapter<ImageItem, FavoritesAdapter.ViewHolder>(DiffCallback) {

    /**
     * Instanciação do contentor de visualização (ViewHolder) para cada elemento da lista.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Insuflação da estrutura visual (layout) específica para a miniatura do favorito
        val binding = ItemFavoriteThumbnailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    /**
     * Vinculação dos dados do modelo à representação visual correspondente à posição atual.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)) // Associação do item de dados ao ViewHolder
    }

    /**
     * Classe interna ViewHolder: Encapsula as referências aos componentes visuais de um único item.
     */
    inner class ViewHolder(private val binding: ItemFavoriteThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Associa as propriedades do objeto ImageItem aos elementos da interface.
         */
        fun bind(item: ImageItem) {
            // Processamento e renderização da imagem com recorte circular utilizando a biblioteca Glide
            Glide.with(binding.favThumbnail.context).load(item.url)
                .circleCrop() // Aplicação de máscara circular para conformidade estética
                .into(binding.favThumbnail)

            // Definição do ouvinte de eventos de interação para o item
            binding.root.setOnClickListener {
                onItemClick(item) // Invocação da função de retorno definida no construtor
            }
        }
    }

    /**
     * Objeto de apoio DiffCallback: Algoritmo de diferenciação para otimização de atualizações da lista.
     */
    private object DiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        // Verificação de identidade biunívoca baseada no identificador único
        override fun areItemsTheSame(old: ImageItem, new: ImageItem) = old.id == new.id

        // Verificação de igualdade estrutural do conteúdo dos objetos
        override fun areContentsTheSame(old: ImageItem, new: ImageItem) = old == new
    }
}
