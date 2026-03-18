package pt.ipl.dam.tabletennisscore.ui.reminders

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.databinding.FragmentRemindersBinding
import pt.ipl.dam.tabletennisscore.ui.common.UiState

// [Fragmento] Gere a lista de lembretes de treino e pede permissões de notificação
@AndroidEntryPoint
class RemindersFragment : Fragment() {

    private var _binding: FragmentRemindersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RemindersViewModel by viewModels()
    private lateinit var adapter: ReminderAdapter

    /** [Permissões] Lida com a resposta do utilizador ao pedido de notificações (Android 13+) */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Se recusar, avisamos que os lembretes podem não aparecer visualmente
            Snackbar.make(binding.root, getString(R.string.notification_permission_denied), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRemindersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNotificationPermission() // Verifica se temos permissão para enviar alertas

        // Configura o adaptador com callbacks para ligar/desligar, editar e apagar
        adapter = ReminderAdapter(
            onToggle = { viewModel.toggleEnabled(it) },
            onEdit = { reminder ->
                val bundle = Bundle().apply { putLong("reminderId", reminder.id) }
                findNavController().navigate(R.id.action_reminders_to_add_edit, bundle)
            },
            onDelete = { viewModel.deleteReminder(it) }
        )
        binding.rvReminders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReminders.adapter = adapter

        // Botão Flutuante (FAB) para criar um novo lembrete
        binding.fabAddReminder.setOnClickListener {
            findNavController().navigate(R.id.action_reminders_to_add_edit)
        }

        // Observa a lista de lembretes da base de dados via LiveData/Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reminders.collect { state ->
                when (state) {
                    is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvEmpty.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        adapter.submitList(state.data)
                    }
                    is UiState.Error -> Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    /** [Segurança] No Android 13 (Tiramisu) ou superior, é obrigatório pedir permissão de POST_NOTIFICATIONS */
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

