package pt.ipl.dam.tabletennisscore.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pt.ipl.dam.tabletennisscore.databinding.FragmentMatchDetailBinding

/**
 * [Fragmento] Exibe os detalhes pormenorizados de uma partida específica (ex: pontuação de cada set).
 * Utiliza o ID da partida passado por argumento para carregar os dados.
 */
@AndroidEntryPoint
class MatchDetailFragment : Fragment() {

    // View Binding para aceder aos elementos do layout XML de forma segura
    private var _binding: FragmentMatchDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Infla o layout do detalhe da partida
        _binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Configura o botão de "Retroceder" para voltar ao ecrã anterior na pilha de navegação
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        
        // NOTA: Os detalhes de cada set são normalmente passados via navArgs ou bundle 
        // e seriam renderizados aqui através de um ViewModel ou carregamento direto da DB.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpa a referência do binding para evitar fugas de memória (Memory Leaks)
        _binding = null
    }
}