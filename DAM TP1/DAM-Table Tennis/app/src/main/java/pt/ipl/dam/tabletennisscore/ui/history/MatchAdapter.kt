package pt.ipl.dam.tabletennisscore.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.ipl.dam.tabletennisscore.databinding.ItemMatchBinding
import pt.ipl.dam.tabletennisscore.domain.model.MatchSummary
import java.text.SimpleDateFormat
import java.util.*

/**
 * [Adaptador] Liga a lista de MatchSummary à visualização (RecyclerView).
 * Usa ListAdapter para animações de lista automáticas e eficientes.
 */
class MatchAdapter(
    private val onItemClick: (MatchSummary) -> Unit
) : ListAdapter<MatchSummary, MatchAdapter.MatchViewHolder>(DiffCallback) {

    /** ViewHolder: Recipiente que segura as referências visuais de cada linha da lista */
    inner class MatchViewHolder(private val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: MatchSummary) {
            // Preenche os dados nos campos de texto
            binding.tvMatchPlayers.text = "${match.player1Name} vs ${match.player2Name}"
            binding.tvMatchResult.text = match.setsScore
            binding.tvMatchWinner.text = "🏆 ${match.winnerName}"
            
            // Formata a data de Long (milissegundos) para texto legível em PT-PT
            binding.tvMatchDate.text = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault())
                .format(Date(match.startedAt))
            
            binding.tvMatchDuration.text = "${match.durationMinutes}min"
            
            // Define o clique na linha inteira
            binding.root.setOnClickListener { onItemClick(match) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        // Infla o layout XML de cada item
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        // "Cola" os dados na posição X ao ViewHolder
        holder.bind(getItem(position))
    }

    /** [Lógica Eficiente] Compara duas listas para saber o que mudou (evita redesenhar tudo) */
    companion object DiffCallback : DiffUtil.ItemCallback<MatchSummary>() {
        override fun areItemsTheSame(oldItem: MatchSummary, newItem: MatchSummary) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MatchSummary, newItem: MatchSummary) = oldItem == newItem
    }
}

