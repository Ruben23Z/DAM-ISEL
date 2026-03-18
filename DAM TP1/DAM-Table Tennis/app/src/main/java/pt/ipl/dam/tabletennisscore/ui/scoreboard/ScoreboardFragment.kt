package pt.ipl.dam.tabletennisscore.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.databinding.FragmentScoreboardBinding

// [Fragmento] O ecrã principal do jogo, onde os pontos são marcados em tempo real
@AndroidEntryPoint
class ScoreboardFragment : Fragment() {

    private var _binding: FragmentScoreboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScoreboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScoreboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners() // Configura os toques nos botões
        observeState() // Começa a ouvir as mudanças de pontuação no ViewModel
    }

    private fun setupClickListeners() {
        // Envia eventos para o ViewModel quando o utilizador toca nos botões de pontuação
        binding.btnScore1.setOnClickListener { viewModel.onEvent(ScoreEvent.AddPoint(0)) }
        binding.btnScore2.setOnClickListener { viewModel.onEvent(ScoreEvent.AddPoint(1)) }
        // Botão Undo: permite anular o último ponto marcado (em caso de erro)
        binding.btnUndo.setOnClickListener { viewModel.onEvent(ScoreEvent.UndoLastPoint) }
        // Botão para desistir da partida atual e voltar ao início
        binding.btnNewMatch.setOnClickListener {
            findNavController().navigate(R.id.action_scoreboard_to_setup)
        }
    }

    private fun observeState() {
        // lifecycleScope garante que a observação só acontece enquanto o Fragmento está "vivo"
        viewLifecycleOwner.lifecycleScope.launch {
            // Coletamos o estado (ScoreState) e atualizamos a interface (renderState)
            viewModel.state.collect { state ->
                renderState(state)
            }
        }
    }

    /** Atualiza todos os elementos visuais do XML com base no estado atual do jogo */
    private fun renderState(state: ScoreState) {
        binding.tvPlayer1Name.text = state.player1Name
        binding.tvPlayer2Name.text = state.player2Name
        binding.tvScore1.text = state.score1.toString()
        binding.tvScore2.text = state.score2.toString()
        binding.tvSets1.text = state.sets1.toString()
        binding.tvSets2.text = state.sets2.toString()
        binding.tvSetNumber.text = getString(R.string.set_number, state.currentSet)

        // Indicador visual de quem está a servir (bola preta/branca)
        val serverIndicator1 = if (state.server == 0) "●" else "○"
        val serverIndicator2 = if (state.server == 1) "●" else "○"
        binding.tvServer1.text = serverIndicator1
        binding.tvServer2.text = serverIndicator2

        // Mostra o aviso de "Deuce" se o set estiver empatado acima do limite
        binding.tvDeuce.visibility = if (state.isDeuce) View.VISIBLE else View.INVISIBLE

        // [UX] Se um set acabou, mostramos o overlay de parabéns/animação
        if (state.setJustFinished && !state.matchFinished) {
            showSetWinAnimation(state.setWinnerName)
        }

        // [UX] Se a partida acabou totalmente, mostramos a animação final e opção de guardar
        if (state.matchFinished) {
            showMatchWinAnimation(state.matchWinnerName)
        }
    }

    /** Mostra uma animação Lottie quando alguém ganha um Set */
    private fun showSetWinAnimation(winnerName: String) {
        binding.lottieSetWin.visibility = View.VISIBLE
        binding.lottieSetWin.playAnimation()
        binding.tvSetWinnerOverlay.text = getString(R.string.set_winner, winnerName)
        binding.cardSetWinOverlay.visibility = View.VISIBLE
        binding.btnNextSet.setOnClickListener {
            binding.lottieSetWin.cancelAnimation()
            binding.lottieSetWin.visibility = View.GONE
            binding.cardSetWinOverlay.visibility = View.GONE
            // Informa o ViewModel que o utilizador já viu a animação e quer continuar
            viewModel.onEvent(ScoreEvent.AcknowledgeSetEnd)
        }
    }

    /** Mostra a animação final de vitória da Partida e permite guardar na DB */
    private fun showMatchWinAnimation(winnerName: String) {
        binding.lottieMatchWin.visibility = View.VISIBLE
        binding.lottieMatchWin.playAnimation()
        binding.tvMatchWinnerOverlay.text = getString(R.string.match_winner, winnerName)
        binding.cardMatchWinOverlay.visibility = View.VISIBLE
        binding.btnSaveMatch.setOnClickListener {
            viewModel.saveCompletedMatch() // Grava os dados no Room via UseCase
            binding.lottieMatchWin.cancelAnimation()
            binding.lottieMatchWin.visibility = View.GONE
            binding.cardMatchWinOverlay.visibility = View.GONE
            
            // Diálogo de confirmação após guardar
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.match_saved))
                .setMessage(getString(R.string.match_saved_message))
                .setPositiveButton(getString(R.string.new_match)) { _, _ ->
                    findNavController().navigate(R.id.action_scoreboard_to_setup)
                }
                .setNegativeButton(getString(R.string.view_history)) { _, _ ->
                    findNavController().navigate(R.id.historyFragment)
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

