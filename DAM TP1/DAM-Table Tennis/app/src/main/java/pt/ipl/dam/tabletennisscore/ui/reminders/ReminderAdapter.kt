package pt.ipl.dam.tabletennisscore.ui.reminders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipl.dam.tabletennisscore.data.local.entity.Reminder
import pt.ipl.dam.tabletennisscore.databinding.ItemReminderBinding

/**
 * [Adaptador] Liga a lista de objetos Reminder à UI.
 */
class ReminderAdapter(
    private val onToggle: (Reminder) -> Unit, // Clique no interruptor (Switch)
    private val onEdit: (Reminder) -> Unit,   // Clique no lápis (Editar)
    private val onDelete: (Reminder) -> Unit  // Clique no lixo (Apagar)
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(DiffCallback) {

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.tvReminderTitle.text = reminder.title
            binding.tvReminderTime.text = "%02d:%02d".format(reminder.scheduledHour, reminder.scheduledMinute)
            binding.tvReminderNotes.text = reminder.notes.ifEmpty { "Sem notas" }
            
            // Configura o interruptor
            binding.switchEnabled.setOnCheckedChangeListener(null) // Evita loops infinitos de disparo
            binding.switchEnabled.isChecked = reminder.isEnabled
            binding.switchEnabled.setOnCheckedChangeListener { _, _ -> onToggle(reminder) }
            
            binding.btnEdit.setOnClickListener { onEdit(reminder) }
            binding.btnDelete.setOnClickListener { onDelete(reminder) }

            // [Lógica] Descodifica a máscara de bits para mostrar os dias abreviados em PT
            val days = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
            val mask = reminder.repeatDayMask
            // Filtra o nome dos dias onde o bit correspondente é 1
            binding.tvDays.text = days.filterIndexed { i, _ -> (mask shr i) and 1 == 1 }.joinToString(" · ")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
    }
}

