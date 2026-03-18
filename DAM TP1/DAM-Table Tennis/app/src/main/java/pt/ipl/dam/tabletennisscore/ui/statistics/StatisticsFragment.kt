package pt.ipl.dam.tabletennisscore.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pt.ipl.dam.tabletennisscore.R
import pt.ipl.dam.tabletennisscore.databinding.FragmentStatisticsBinding
import pt.ipl.dam.tabletennisscore.domain.model.OverallStats
import pt.ipl.dam.tabletennisscore.ui.common.UiState

// [Fragmento] Exibe gráficos e dados comparativos de desempenho
@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStats() // Começa a ouvir o processamento das estatísticas
    }

    private fun observeStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stats.collect { state ->
                when (state) {
                    is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        renderStats(state.data) // Desenha os gráficos quando os dados chegam
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    /** 
     * [Visualização] Utiliza a biblioteca MPAndroidChart para desenhar dados.
     * Transforma os modelos de domínio em objetos de gráfico (PieEntry, BarEntry).
     */
    private fun renderStats(stats: OverallStats) {
        binding.tvTotalMatches.text = getString(R.string.total_matches, stats.totalMatches)
        binding.tvTotalSets.text = getString(R.string.total_sets, stats.totalSets)
        binding.tvTotalPoints.text = getString(R.string.total_points, stats.totalPoints)

        // Se não houver jogos no histórico, avisa o utilizador
        if (stats.playerStats.isEmpty()) {
            binding.tvEmptyStats.visibility = View.VISIBLE
            return
        }
        binding.tvEmptyStats.visibility = View.GONE

        // --- Gráfico de Torta (PieChart): Rácio Vitórias/Derrotas do Melhor Jogador ---
        val topPlayer = stats.playerStats.first()
        val pieEntries = listOf(
            PieEntry(topPlayer.wins.toFloat(), getString(R.string.wins)),
            PieEntry(topPlayer.losses.toFloat(), getString(R.string.losses))
        )
        val pieDataSet = PieDataSet(pieEntries, topPlayer.playerName).apply {
            colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336")) // Verde/Vermelho
            valueTextSize = 14f
            valueTextColor = Color.WHITE
        }
        binding.pieChart.data = PieData(pieDataSet)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.animateY(800) // Animação de entrada
        binding.pieChart.invalidate() // Força o redesenho

        // --- Gráfico de Barras (BarChart): Média de Pontos por Jogador ---
        val barEntries = stats.playerStats.mapIndexed { i, p ->
            BarEntry(i.toFloat(), p.avgPointsPerSet)
        }
        val barDataSet = BarDataSet(barEntries, getString(R.string.avg_points_per_set)).apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
        }
        binding.barChart.data = BarData(barDataSet)
        binding.barChart.description.isEnabled = false
        binding.barChart.animateY(800)
        binding.barChart.invalidate()

        // --- Lista de Texto: Resumo rápido de Streaks e Win Rates ---
        val sb = StringBuilder()
        stats.playerStats.forEach { p ->
            sb.append("${p.playerName}: ${p.wins}V ${p.losses}D  " +
                "WR: ${"%.0f".format(p.winRate * 100)}%  " +
                "Streak: ${if (p.currentStreak >= 0) "+${p.currentStreak}" else "${p.currentStreak}"}\n")
        }
        binding.tvPlayerStats.text = sb.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

