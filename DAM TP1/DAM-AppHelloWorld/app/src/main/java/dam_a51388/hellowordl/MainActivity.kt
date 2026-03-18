package dam_a51388.hellowordl

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// A MainActivity é o ponto de entrada da aplicação Android.
class MainActivity : AppCompatActivity() {

    // Método chamado quando a atividade é criada pela primeira vez.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o modo "Edge-to-Edge", permitindo que o conteúdo da app fique por baixo das barras do sistema.
        enableEdgeToEdge()

        // Define qual o ficheiro XML que contém o design (layout) desta ecrã.
        setContentView(R.layout.activity_main)

        // Configura o tema para mudar automaticamente entre light/dark conforme o horário ou definições do sistema.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_TIME)

        // Atualiza a imagem de fundo dependendo do modo (claro/escuro).
        updateBackground()

        // Procura o botão no layout pelo ID 'btnISEL'.
        val button = findViewById<Button>(R.id.btnISEL)

        // Define o que acontece quando o utilizador clica no botão.
        button.setOnClickListener {
            triggerISELEvent() // Inicia a sequência de eventos (vibra, toca, anima e abre mapa).
        }

        // Ajusta o preenchimento (padding) da vista principal para não ficar "escondida" pelas
        // barras de sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Altera a imagem de fundo (ImageView) com base no modo Noite (Dark Mode).
    private fun updateBackground() {
        val image = findViewById<ImageView>(R.id.backgroundImage)
        if (image == null) return // Proteção caso a imagem não exista

        // Verifica se o modo noite está ativo.
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            image.setImageResource(R.drawable.dark) // Define imagem para modo dark.
        } else {
            image.setImageResource(R.drawable.light) // Define imagem para modo light.
        }
    }

    // Requer que a app tenha permissão de vibração no AndroidManifest.xml.
    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun triggerISELEvent() {
        vibratePhone() // 1º Vibra.
        playSound() // 2º Toca som.
        animateMap() // 3º Faz animação e, no fim, abre o mapa.
    }

    // Faz o telemóvel vibrar por 300ms.
    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibratePhone() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Versões mais recentes do Android (Oreo ou superior) usam VibrationEffect.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Suporte para versões antigas do Android.
            vibrator.vibrate(300)
        }
    }

    // Toca um ficheiro de áudio que esteja na pasta 'res/raw'.
    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.clickaudio)
        mediaPlayer.start() // Inicia a reprodução.
    }

    // Faz uma animação de rotação e escala no ícone do mapa.
    private fun animateMap() {
        val icon = findViewById<ImageView>(R.id.mapIcon)
        icon.animate().apply {
            rotation(360f) // Roda 360 graus.
            scaleX(1.4f) // Aumenta a largura em 40%.
            scaleY(1.4f) // Aumenta a altura em 40%.
            duration = 800 // Duração de 800 milisegundos.

            // Quando a animação terminar, executa a função para abrir o mapa.
            withEndAction { openISELMap() }
        }
    }

    // Usa um Intent para abrir a aplicação de Mapas do telemóvel num local específico.
    private fun openISELMap() {
        val address =
                "Instituto Superior de Engenharia de Lisboa, R. Conselheiro Emídio Navarro 1, 1959-007 Lisboa"

        // Cria um URI com a morada codificada para navegação.
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")

        // Cria o Intent de visualização.
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        // Tenta forçar a abertura especificamente na app do Google Maps.
        mapIntent.setPackage("com.google.android.apps.maps")

        // Inicia a Atividade externa (o Maps).
        startActivity(mapIntent)
    }
}
