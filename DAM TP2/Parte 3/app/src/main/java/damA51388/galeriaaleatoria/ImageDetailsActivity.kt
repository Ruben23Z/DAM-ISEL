package damA51388.galeriaaleatoria

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import damA51388.galeriaaleatoria.databinding.ActivityImageDetailsBinding
import damA51388.galeriaaleatoria.model.ImageItem

/**
 * Classe ImageDetailsActivity: Responsável pela apresentação detalhada de um espécime canino selecionado.
 * Implementa a lógica de visualização minuciosa e partilha de metadados associados à imagem.
 */
class ImageDetailsActivity : AppCompatActivity() {

    // Referência para a vinculação da interface de utilizador (View Binding)
    private lateinit var binding: ActivityImageDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicialização da infraestrutura visual e definição do conteúdo da atividade
        binding = ActivityImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperação do objeto de dados (ImageItem) proveniente do Intent, com verificação de versão da API
        val item = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Utilização de método tipado introduzido no Android 13 para maior segurança de tipos
            intent.getSerializableExtra("EXTRA_IMAGE_ITEM", ImageItem::class.java)
        } else {
            // Recurso ao método legado para compatibilidade com versões anteriores à API 33
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_IMAGE_ITEM") as? ImageItem
        }

        // Validação da integridade dos dados recebidos antes da instanciação dos componentes visuais
        if (item != null) {
            setupUI(item)
        } else {
            // Encerramento da atividade em caso de ausência de dados vitais para a visualização
            finish()
        }

        // Configuração do mecanismo de retrocesso para a atividade precedente
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Procede à configuração dos elementos da interface com base nos atributos do ‘item’.
     */
    private fun setupUI(item: ImageItem) {
        // Atribuição do nome da raça ao componente de texto correspondente
        binding.detailBreedText.text = item.displayBreed
        
        // Extração e formatação da origem (host) da imagem para exibição informativa
        val host = Uri.parse(item.url).host ?: "images.dog.ceo"
        binding.detailUrlText.text = getString(R.string.source_label, host)

        // Invocação da biblioteca Glide para o carregamento assíncrono e renderização da imagem
        Glide.with(this)
            .load(item.url)
            .into(binding.detailImage)

        // Configuração da funcionalidade de disseminação (partilha) de informação
        binding.shareDogButton.setOnClickListener {
            // Elaboração da mensagem de partilha baseada num modelo pré-definido
            val shareText = getString(R.string.share_text_template, item.displayBreed, item.url)
            
            // Instanciação de um Intent implícito para a transmissão de dados textuais
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            
            // Apresentação do seletor de aplicações do sistema para conclusão da ação
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_full_label)))
        }
    }
}
