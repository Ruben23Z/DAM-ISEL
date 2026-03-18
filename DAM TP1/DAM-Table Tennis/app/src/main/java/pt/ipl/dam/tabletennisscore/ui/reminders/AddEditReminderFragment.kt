package pt.ipl.dam.tabletennisscore.ui.reminders

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder
import pt.ipl.dam.tabletennisscore.databinding.FragmentAddEditReminderBinding

// [Fragmento] Permite criar ou editar as definições de um lembrete
@AndroidEntryPoint
class AddEditReminderFragment : Fragment() {

    private var _binding: FragmentAddEditReminderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RemindersViewModel by viewModels()

    private var selectedHour = 9 // Hora por defeito
    private var selectedMinute = 0
    private var editingReminderId: Long = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verifica se recebemos um ID por argumento (se sim, estamos em modo Edição)
        editingReminderId = arguments?.getLong("reminderId", 0L) ?: 0L

        if (editingReminderId != 0L) {
            binding.tvAddTitle.text = getString(R.string.edit_reminder)
            loadReminderData() // Preenche os campos com os dados atuais
        } else {
            binding.tvAddTitle.text = getString(R.string.add_reminder)
            updateTimeDisplay()
        }

        // Abre o seletor de horas do Android (TimePickerDialog)
        binding.btnPickTime.setOnClickListener {
            TimePickerDialog(requireContext(), { _, h, m ->
                selectedHour = h
                selectedMinute = m
                updateTimeDisplay()
            }, selectedHour, selectedMinute, true).show()
        }

        binding.btnSave.setOnClickListener { saveReminder() } // Guarda as alterações
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() } // Sai sem guardar
    }

    /** Carrega os dados da DB para os campos de texto e Chips (dias) */
    private fun loadReminderData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getReminder(editingReminderId).collect { reminder ->
                reminder?.let {
                    binding.etTitle.setText(it.title)
                    binding.etNotes.setText(it.notes)
                    selectedHour = it.scheduledHour
                    selectedMinute = it.scheduledMinute
                    updateTimeDisplay()
                    setDayMask(it.repeatDayMask)
                }
            }
        }
    }

    /** Marca os Chips (dias da semana) com base no número guardado na DB */
    private fun setDayMask(mask: Int) {
        val chipGroup = binding.chipGroupDays
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            // Se o bit na posição I for 1, o Chip fica selecionado
            chip?.isChecked = (mask and (1 shl i)) != 0
        }
    }

    private fun updateTimeDisplay() {
        binding.tvSelectedTime.text = "%02d:%02d".format(selectedHour, selectedMinute)
    }

    /** [Lógica Bitwise] Converte a seleção dos 7 Chips num único número inteiro (máscara) */
    private fun buildDayMask(): Int {
        var mask = 0
        val chipGroup = binding.chipGroupDays
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            // Se o Chip do dia estiver marcado, levantamos o bit correspondente
            if (chip?.isChecked == true) mask = mask or (1 shl i)
        }
        // Se nenhum dia for selecionado, por segurança assumimos todos (0b1111111)
        return if (mask == 0) 127 else mask 
    }

    /** Valida os campos e envia o objeto final para o ViewModel salvar */
    private fun saveReminder() {
        val title = binding.etTitle.text?.toString()?.trim()
        if (title.isNullOrBlank()) {
            binding.tilTitle.error = getString(R.string.error_title_required)
            return
        }
        
        val reminder = Reminder(
            id = editingReminderId,
            title = title,
            notes = binding.etNotes.text?.toString()?.trim() ?: "",
            scheduledHour = selectedHour,
            scheduledMinute = selectedMinute,
            repeatDayMask = buildDayMask(),
            isEnabled = true
        )
        
        if (editingReminderId == 0L) viewModel.saveReminder(reminder)
        else viewModel.updateReminder(reminder)
        
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

