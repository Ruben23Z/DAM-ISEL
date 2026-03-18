package pt.ipl.dam.tabletennisscore.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.databinding.FragmentHistoryBinding
import pt.ipl.dam.tabletennisscore.ui.common.UiState

// [Fragmento] Exibe a lista cronológica de partidas guardadas
@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView() // Configura a lista e o comportamento de deslizar (swipe)
        observeMatches() // Observa os dados vindos da DB
    }

    private fun setupRecyclerView() {
        // Inicializa o adaptador com um clique para ver detalhes
        adapter = MatchAdapter { match ->
            val bundle = Bundle().apply { putLong("matchId", match.id) }
            findNavController().navigate(R.id.action_history_to_detail, bundle)
        }
        binding.rvMatches.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMatches.adapter = adapter

        // [UX] Implementação de "Deslizar para Apagar" (Swipe-to-delete)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                // Obtém a partida na posição deslizada e apaga-a via ViewModel
                val match = adapter.currentList[vh.adapterPosition]
                viewModel.deleteMatch(match.id)
                // Mostra uma confirmação temporária (Snackbar)
                Snackbar.make(binding.root, getString(R.string.match_deleted), Snackbar.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(binding.rvMatches)
    }

    private fun observeMatches() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Coleta o estado da UI (Loading, Success ou Error)
            viewModel.matches.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.rvMatches.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvMatches.visibility = View.VISIBLE
                        // Se não houver dados, mostra o texto de "Lista Vazia"
                        binding.tvEmptyState.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        // Envia a nova lista para o adaptador (que usa DiffUtil para atualizar apenas o necessário)
                        adapter.submitList(state.data)
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

