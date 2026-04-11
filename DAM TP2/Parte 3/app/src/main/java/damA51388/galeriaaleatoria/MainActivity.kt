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
 * Classe MainActivity: Atua como o controlador primordial da interface de utilizador.
 * Esta classe é responsável pela coordenação entre a lógica de negócio (ViewModel) e a camada de apresentação.
 */
class MainActivity : AppCompatActivity() {

    // Declaração de propriedades com inicialização diferida para gestão de componentes e estado
    private lateinit var binding: ActivityMainBinding // Gestão de referências da interface via ViewBinding
    private val viewModel: ImageViewModel by viewModels() // Delegação da gestão de dados ao ViewModel
    private lateinit var favoritesManager: FavoritesManager // Mecanismo de persistência de itens prediletos
    private lateinit var networkMonitor: NetworkMonitor // Monitor de estado da conetividade de rede
    
    // Instanciação do adaptador para o fluxo de imagens com definição de callbacks para interação
    private val feedAdapter = ImageFeedAdapter(
        onLikeStateChanged = { item ->
            updateLikeUI(item) // Sincronização da interface após alteração do estado de preferência
        },
        onItemClicked = { item ->
            openDetails(item) // Transição para o ecrã de especificações do item
        }
    )

    private lateinit var favoritesAdapter: FavoritesAdapter // Adaptador para a listagem de favoritos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ativação da renderização imersiva (Edge-to-Edge)

        // Instanciação da hierarquia de vistas através do ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste dinâmico do preenchimento (padding) para acomodar as barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootContainer) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Inicialização dos componentes de suporte à lógica de aplicação
        favoritesManager = FavoritesManager(this)
        networkMonitor = NetworkMonitor(this)

        // Procedimentos de configuração dos subsistemas da interface
        setupFavoritesBar()
        setupViewPager()
        setupSwipeRefresh()
        setupActionButtons()
        observeViewModel()
        observeNetwork()
    }

    /**
     * Configuração do componente de visualização de favoritos.
     */
    private fun setupFavoritesBar() {
        favoritesAdapter = FavoritesAdapter { favoriteItem: ImageItem ->
            openDetails(favoriteItem) // Invocação dos detalhes a partir da barra de favoritos
        }
        binding.recyclerViewFavorites?.adapter = favoritesAdapter
        refreshFavoritesUI() // Atualização da representação visual dos favoritos
    }

    /**
     * Atualiza a lista de favoritos e ajusta a visibilidade do componente.
     */
    private fun refreshFavoritesUI() {
        val favs = favoritesManager.getFavorites() // Recuperação de dados persistidos
        favoritesAdapter.submitList(favs) // Atualização do adaptador
        binding.recyclerViewFavorites?.visibility = if (favs.isEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Monitorização reativa da disponibilidade de rede.
     */
    private fun observeNetwork() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkMonitor.isOnline.collect { isOnline ->
                    // Gestão da visibilidade do aviso de estado offline
                    binding.offlineBanner.visibility = if (isOnline) View.GONE else View.VISIBLE
                    // Despoleta atualização de dados se a conetividade for restabelecida
                    if (isOnline && (viewModel.images.value.isNullOrEmpty())) {
                        viewModel.refresh()
                    }
                }
            }
        }
    }

    /**
     * Parametrização dos ouvintes de eventos para os controlos de ação.
     */
    private fun setupActionButtons() {
        // Manipulação do evento de "Gosto"
        binding.likeContainer.setOnClickListener {
            val position = binding.viewPagerFeed.currentItem
            if (position in 0 until feedAdapter.itemCount) {
                val item = feedAdapter.currentList[position]
                item.isLiked = !item.isLiked // Alteração do estado booleano
                updateLikeUI(item) // Reflexão visual imediata
            }
        }

        // Manipulação do evento de salvaguarda em favoritos
        binding.saveContainer.setOnClickListener {
            val position = binding.viewPagerFeed.currentItem
            if (position in 0 until feedAdapter.itemCount) {
                val item = feedAdapter.currentList[position]
                val isNowFav = favoritesManager.toggleFavorite(item) // Inversão do estado de persistência
                
                updateSaveUI(isNowFav) // Atualização do ícone representativo
                refreshFavoritesUI() // Sincronização da barra superior
                
                // Emissão de feedback informativo ao utilizador
                val message = if (isNowFav) {
                    getString(R.string.saved_ok_message)
                } else {
                    getString(R.string.remove_label)
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        // Manipulação do evento de descarregamento de ficheiro multimédia
        binding.downloadContainer.setOnClickListener {
            val position = binding.viewPagerFeed.currentItem
            if (position in 0 until feedAdapter.itemCount) {
                val item = feedAdapter.currentList[position]
                downloadImage(item.url, "Dog_${item.breed}_${item.id}.jpg")
            }
        }
    }

    /**
     * Efetua a transição para a atividade de detalhes.
     */
    private fun openDetails(item: ImageItem) {
        val intent = Intent(this, ImageDetailsActivity::class.java).apply {
            putExtra("EXTRA_IMAGE_ITEM", item) // Encapsulamento do objeto serializável
        }
        startActivity(intent)
    }

    /**
     * Sincroniza os elementos visuais com o estado de preferência do item.
     */
    private fun updateLikeUI(item: ImageItem) {
        binding.likeButton.setImageResource(if (item.isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
        binding.likeCountText.text = if (item.isLiked) "1" else "0"
    }

    /**
     * Sincroniza o ícone de estado de favorito.
     */
    private fun updateSaveUI(isFavorite: Boolean) {
        binding.saveButton.setImageResource(
            if (isFavorite) android.R.drawable.btn_star_big_on 
            else R.drawable.ic_star_outline
        )
    }

    /**
     * Inicia o processamento assíncrono para obtenção da imagem em formato Bitmap.
     */
    private fun downloadImage(url: String, filename: String) {
        Toast.makeText(this, "A transferir...", Toast.LENGTH_SHORT).show()
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    saveBitmapToGallery(resource, filename) // Escrita no sistema de ficheiros
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Libertação de referências se necessário
                }
            })
    }

    /**
     * Persiste o conteúdo binário da imagem na galeria do sistema.
     */
    private fun saveBitmapToGallery(bitmap: Bitmap, filename: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Tratamento de permissões e caminhos para versões recentes do Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/DogFeed")
            }
        }

        // Inserção metódica no provedor de conteúdos de media
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

    /**
     * Parametrização do componente ViewPager2 para navegação por deslizamento vertical.
     */
    private fun setupViewPager() {
        binding.viewPagerFeed.adapter = feedAdapter
        binding.viewPagerFeed.offscreenPageLimit = 2 // Otimização de desempenho por pré-carregamento

        // Definição de callbacks para monitorização de transição entre páginas
        binding.viewPagerFeed.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val totalItems = feedAdapter.itemCount
                if (position in 0 until totalItems) {
                    val item = feedAdapter.currentList[position]
                    updateLikeUI(item)
                    updateSaveUI(favoritesManager.isFavorite(item.id))
                    
                    // Estratégia de carregamento antecipado (Lazy Loading) ao atingir o limiar da lista
                    if (position >= totalItems - 3 && totalItems > 0) {
                        viewModel.loadMore()
                    }
                }
            }
        })
    }

    /**
     * Configuração da ação de renovação do fluxo de dados.
     */
    private fun setupSwipeRefresh() {
        binding.tabForYou.setOnClickListener {
            viewModel.refresh() // Invocação da renovação de dados no ViewModel
            Toast.makeText(this, "A atualizar feed...", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Estabelecimento de observadores para os fluxos de dados reativos.
     */
    private fun observeViewModel() {
        // Monitorização da coleção de imagens
        viewModel.images.observe(this) { images ->
            feedAdapter.submitList(images)
        }

        // Monitorização do estado de processamento
        viewModel.isLoading.observe(this) { loading ->
            binding.loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Monitorização e reporte de anomalias ou erros
        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
