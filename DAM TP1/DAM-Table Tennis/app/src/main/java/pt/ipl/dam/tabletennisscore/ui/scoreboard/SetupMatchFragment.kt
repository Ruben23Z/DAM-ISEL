package pt.ipl.dam.tabletennisscore.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.databinding.FragmentSetupMatchBinding

// [Fragmento] Responsável pela configuração inicial da partida (nomes, formato, pontos)
@AndroidEntryPoint
class SetupMatchFragment : Fragment() {

    private var _binding: FragmentSetupMatchBinding? = null
    private val binding get() = _binding!!
    
    // activityViewModels(): Partilha o mesmo ViewModel com o ScoreboardFragment 
    // para que os dados da configuração cheguem ao ecrã do jogo.
    private val viewModel: ScoreboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetupMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botão para iniciar a partida
        binding.btnStartMatch.setOnClickListener {
            // Obtém os nomes dos jogadores ou usa nomes por defeito se estiverem vazios
            val p1 = binding.etPlayer1.text?.toString()?.trim().takeIf { !it.isNullOrBlank() } ?: getString(R.string.player_1)
            val p2 = binding.etPlayer2.text?.toString()?.trim().takeIf { !it.isNullOrBlank() } ?: getString(R.string.player_2)

            // Determina o formato da partida (Melhor de 3, 5 ou 7 sets)
            val bestOf = when (binding.rgBestOf.checkedRadioButtonId) {
                R.id.rb_best_of_3 -> 3
                R.id.rb_best_of_7 -> 7
                else -> 5
            }
            // Determina a pontuação por set (11 ou 21 pontos)
            val pointsPerSet = when (binding.rgPoints.checkedRadioButtonId) {
                R.id.rb_21_points -> 21
                else -> 11
            }

            // Envia as configurações para o ViewModel
            viewModel.setupMatch(p1, p2, bestOf, pointsPerSet)
            // Navega para o ecrã do marcador (Scoreboard)
            findNavController().navigate(R.id.action_setup_to_scoreboard)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evita fugas de memória (memory leaks)
    }
}

