package com.example.sysinfoapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Build
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        // Define o layout XML que vamos usar.
        setContentView(R.layout.activity_main)
        
        // Vamos buscar o componente TextView que está no XML (através do ID 'textInfo') para podermos escrever lá texto.
        val textView = findViewById<TextView>(R.id.textInfo)

        // Aqui usamos a classe 'Build' do Android para recolher informações sobre o hardware e o sistema do telemóvel.
        // O triple-quote (""") permite-nos escrever uma string com várias linhas de forma limpa.
        val info = """
        Fabricante: ${Build.MANUFACTURER}
        Modelo: ${Build.MODEL}
        Marca: ${Build.BRAND}
        Dispositivo: ${Build.DEVICE}
        Utilizador: ${Build.USER}
        Base: ${Build.VERSION_CODES.BASE}
        Incremental: ${Build.VERSION.INCREMENTAL}
        Nível da SDK (API): ${Build.VERSION.SDK_INT}
        Versão do Android: ${Build.VERSION.RELEASE}
        Ecrã (Display): ${Build.DISPLAY}
        Hardware: ${Build.HARDWARE}
        Host: ${Build.HOST}
        ID da Build: ${Build.ID}
        """.trimIndent() // O trimIndent remove os espaços extra à esquerda para o texto ficar alinhado.

        // Finalmente, injetamos toda a informação que recolhemos no TextView para que o utilizador a veja.
        textView.text = info
        
        // Este bloco serve para ajustar o design às barras do sistema (estado e navegação).
        // Evita que o nosso conteúdo fique "cortado" ou por baixo dos botões do telemóvel.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
