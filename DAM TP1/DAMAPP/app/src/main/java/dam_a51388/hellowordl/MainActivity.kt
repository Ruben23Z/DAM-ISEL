package dam_a51388.hellowordl

// Importações para funcionalidades do Android, UI, imagens, gestos e sistema
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import java.io.Serializable
import java.util.Random
import kotlin.math.atan2

// Classe principal que gere o mural de memórias
class MainActivity : AppCompatActivity() {

    private lateinit var mainImageView: ImageView // Imagem de fundo principal
    private lateinit var canvasLayout: ConstraintLayout // Layout onde as fotos são "coladas"
    private val random = Random() // Gerador de números aleatórios para posições iniciais

    // Registo do seletor de media para escolher fotos da galeria
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) { // Se o utilizador escolheu uma foto
            try {
                // [DEEP DIVE] Pedir permissão permanente para manter acesso à foto após fechar a app
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {}
            showCaptionDialog(uri) // Abre o diálogo para pedir a legenda
        }
    }

    // O onCreate é o "coração" da Atividade, onde o Android prepara tudo o que o utilizador vai ver.
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState) // Chama a lógica base do Android para criar janelas e estados.
        
        // Ativa o modo Edge-to-Edge para que o fundo da app se estenda por baixo da barra de horas e botões.
        enableEdgeToEdge() 
        
        // Liga este código ao ficheiro de design XML (activity_main) para construir a interface visual.
        setContentView(R.layout.activity_main) 

        // Atribuímos os componentes do XML às variáveis Kotlin usando o findViewById e o ID único de cada um.
        mainImageView = findViewById(R.id.mainImageView) // A imagem gigante que fica por trás de tudo.
        canvasLayout = findViewById(R.id.canvasLayout) // O "mural" (ConstraintLayout) onde as fotos serão coladas.
        
        // Botões de ação rápida (Floating Action Buttons) e o Interruptor (Switch) para o Dark Mode.
        val btnFabMap = findViewById<FloatingActionButton>(R.id.btnFabMap)
        val btnAddMemory = findViewById<ExtendedFloatingActionButton>(R.id.btnAddMemory)
        val switchDarkMode = findViewById<MaterialSwitch>(R.id.switchDarkMode)

        // Configuração de Insets: Serve para garantir que o texto não fica "tapado" pela barra de sistema.
        // Calculamos o tamanho da barra de horas e da barra de botões e afastamos o conteúdo (Padding).
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Devolvemos os insets para o sistema saber que já foram tratados.
        }

        // Define o estado inicial do interruptor de modo escuro
        switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchDarkMode.setOnCheckedChangeListener { _, isChecked -> // Muda o tema quando clicado
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Listeners para os botões de filtros da imagem de fundo
        findViewById<MaterialButton>(R.id.btnFilterNone).setOnClickListener { applyFilter(null) } // Sem filtro
        findViewById<MaterialButton>(R.id.btnFilterGrey).setOnClickListener { applyFilter(getGreyFilter()) } // Cinzento
        findViewById<MaterialButton>(R.id.btnFilterSepia).setOnClickListener { applyFilter(getSepiaFilter()) } // Sépia
        findViewById<MaterialButton>(R.id.btnFilterInvert).setOnClickListener { applyFilter(getInvertFilter()) } // Inverter

        btnFabMap.setOnClickListener { showMap() } // Botão para abrir o mapa do ISEL
        btnAddMemory.setOnClickListener { // Botão para adicionar nova memória
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // [DEEP DIVE] Recupera e recria as fotos se a App foi reiniciada (ex: rotação)
        if (savedInstanceState != null) {
            val restoredList = savedInstanceState.getSerializable("photo_list") as? ArrayList<PhotoState>
            restoredList?.forEach { state ->
                addPhotoToCanvas(Uri.parse(state.uriString), state.caption, state)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) { // Guarda o estado antes da atividade ser recriada
        super.onSaveInstanceState(outState) // Chama a lógica base do Android para guardar estados.
        val currentStates = ArrayList<PhotoState>() // Lista para guardar os estados das fotos
        for (i in 0 until canvasLayout.childCount) { // Percorre todos os elementos do canvas
            val child = canvasLayout.getChildAt(i)
            if (child is MaterialCardView && child.tag is PhotoState) { // Se for um postal/foto
                val state = child.tag as PhotoState // Lê os dados atuais (posição, rotação, etc)
                state.x = child.x
                state.y = child.y
                state.rotation = child.rotation
                state.scaleX = child.scaleX
                state.scaleY = child.scaleY
                currentStates.add(state) // Adiciona à lista para salvar
            }
        }
        outState.putSerializable("photo_list", currentStates) // Salva a lista no Bundle
    }

    private fun showCaptionDialog(uri: Uri) { // Mostra diálogo para escrever legenda
        val editText = EditText(this).apply { hint = "Adiciona uma legenda..." }
        AlertDialog.Builder(this)
            .setTitle("Nova Memória")
            .setMessage("Escreve uma legenda para a tua foto.")
            .setView(editText)
            .setPositiveButton("Adicionar") { _, _ -> // Se clicar em adicionar...
                addPhotoToCanvas(uri, editText.text.toString()) // Cria a foto no mural
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addPhotoToCanvas(uri: Uri, caption: String, restoredState: PhotoState? = null) {
        // Usa estado restaurado ou gera valores aleatórios para a nova foto
        val state = restoredState ?: PhotoState(
            uriString = uri.toString(),
            caption = caption,
            x = (random.nextInt(300)).toFloat(),
            y = (800 + random.nextInt(300)).toFloat(),
            rotation = (random.nextInt(20) - 10).toFloat()
        )

        // Cria o componente principal (Card) do postal
        val card = MaterialCardView(this).apply {
            radius = 4f
            elevation = 15f
            strokeWidth = 2
            strokeColor = Color.LTGRAY
            setCardBackgroundColor(android.content.res.ColorStateList.valueOf(Color.WHITE))
            layoutParams = ConstraintLayout.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT)
            tag = state // Gancho para guardar/recuperar o estado
        }

        // Layout para empilhar imagem e texto dentro do Card
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(20, 20, 20, 50)
        }

        // Componente da imagem da foto
        val imageView = ImageView(this).apply {
            setImageURI(uri)
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = android.widget.LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
        }

        // Componente do texto da legenda
        val textView = TextView(this).apply {
            text = caption
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(Color.DKGRAY)
            textSize = 16f
            setPadding(0, 24, 0, 0)
            visibility = if (caption.isEmpty()) View.GONE else View.VISIBLE // Esconde se não houver legenda
            alpha = 0.9f
        }

        container.addView(imageView) // Adiciona imagem ao container
        container.addView(textView) // Adiciona texto ao container
        card.addView(container) // Adiciona container ao Card

        canvasLayout.addView(card) // Adiciona o postal ao mural principal
        
        // [DEEP DIVE] Aplica a posição apenas quando a View estiver pronta para ser desenhada
        card.post {
            card.x = state.x
            card.y = state.y
            card.rotation = state.rotation
            card.scaleX = state.scaleX
            card.scaleY = state.scaleY
        }

        setupTouchListener(card) // Ativa os gestos para este postal
        findViewById<View>(R.id.hintText)?.visibility = View.GONE // Esconde a ajuda inicial
    }

    // Modelo de dados para guardar as propriedades de cada foto
    data class PhotoState(
        val uriString: String,
        val caption: String,
        var x: Float = 0f,
        var y: Float = 0f,
        var rotation: Float = 0f,
        var scaleX: Float = 1f,
        var scaleY: Float = 1f
    ) : Serializable

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener(view: View) { // Configura como a foto reage ao toque
        var dX = 0f // Distância X entre o dedo e o canto da foto
        var dY = 0f // Distância Y entre o dedo e o canto da foto
        var lastRotation = 0f // Último ângulo medido (para rotação)

        // Detetor de gesto de pinça (Zoom)
        val scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                view.scaleX *= detector.scaleFactor
                view.scaleY *= detector.scaleFactor
                return true
            }
        })

        // Detetor de toques longos (para eliminar)
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                showDeleteDialog(view) // Pergunta se é para apagar
            }
        })

        // [DEEP DIVE] Lógica principal de coordenadas, arrasto e rotação
        view.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event) // Envia evento para detetor de pressão longa
            scaleDetector.onTouchEvent(event) // Envia evento para detetor de zoom
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { // Início do toque
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    v.bringToFront() // Traz a foto para cima das outras
                }
                MotionEvent.ACTION_POINTER_DOWN -> { // Quando entra um segundo dedo no ecrã
                    if (event.pointerCount == 2) lastRotation = calculateRotation(event)
                }
                MotionEvent.ACTION_MOVE -> { // Arrastar o dedo
                    if (event.pointerCount == 1 && !scaleDetector.isInProgress) {
                        v.x = event.rawX + dX // Move a foto seguindo o dedo
                        v.y = event.rawY + dY
                    } else if (event.pointerCount == 2) { // Dois dedos a rodar
                        val currentRotation = calculateRotation(event)
                        v.rotation += currentRotation - lastRotation // Ajusta ângulo
                        lastRotation = currentRotation
                    }
                }
            }
            true
        }
    }

    // Matemática para calcular o ângulo entre dois pontos no plano cartesiano
    private fun calculateRotation(event: MotionEvent): Float {
        val deltaX = (event.getX(0) - event.getX(1)).toDouble()
        val deltaY = (event.getY(0) - event.getY(1)).toDouble()
        return Math.toDegrees(atan2(deltaY, deltaX)).toFloat()
    }

    private fun showDeleteDialog(view: View) { // Confirmação de remoção de foto
        AlertDialog.Builder(this)
            .setTitle("Eliminar Memória")
            .setMessage("Tens a certeza que queres remover esta foto do teu mural?")
            .setPositiveButton("Sim, eliminar") { _, _ ->
                canvasLayout.removeView(view) // Remove do layout principal
                if (canvasLayout.childCount <= 4) findViewById<View>(R.id.hintText)?.visibility = View.VISIBLE
            }
            .setNegativeButton("Não", null)
            .show()
    }
    
    // Aplica o filtro de cor à imagem de fundo através de uma matriz colorimétrica
    private fun applyFilter(filter: ColorMatrixColorFilter?) { mainImageView.colorFilter = filter }
    
    // Funções geradoras das matrizes de filtro
    private fun getGreyFilter() = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
    private fun getSepiaFilter(): ColorMatrixColorFilter {
        val matrix = ColorMatrix().apply { setSaturation(0f) }
        val sepiaMatrix = ColorMatrix().apply { setScale(1f, 0.95f, 0.82f, 1.0f) }
        matrix.postConcat(sepiaMatrix)
        return ColorMatrixColorFilter(matrix)
    }
    private fun getInvertFilter() = ColorMatrixColorFilter(ColorMatrix(floatArrayOf(
        -1f, 0f, 0f, 0f, 255f, 0f, -1f, 0f, 0f, 255f, 0f, 0f, -1f, 0f, 255f, 0f, 0f, 0f, 1f, 0f
    )))

    // Abre a localização por GPS via Intent (Mapas externos)
    private fun showMap() {
        val address = "Instituto Superior de Engenharia de Lisboa, R. Conselheiro Emídio Navarro 1, 1959-007 Lisboa"
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) startActivity(mapIntent)
        else startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
    }
}