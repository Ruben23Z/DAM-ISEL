package pt.ipl.dam.tabletennisscore.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.databinding.ActivityMainBinding

// @AndroidEntryPoint: Necessário para o Hilt poder injetar dependências em componentes do Android como Activities
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // View Binding para aceder aos elementos do XML
    private lateinit var navController: NavController // Controlador de navegação do Jetpack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o Fragmento de Hospedagem (NavHost) definido no XML
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Obtém o controlador que gere a troca de ecrãs
        navController = navHostFragment.navController

        // Liga a barra de navegação inferior (BottomNavigation) ao controlador de navegação.
        // Isto faz com que o Android mude de ecrã automaticamente ao carregar nos ícones.
        binding.bottomNavigation.setupWithNavController(navController)
    }
}