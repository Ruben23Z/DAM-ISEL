package A51388.spinnet.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import A51388.spinnet.data.model.TrainingSession
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.border
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PerformanceProfileScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    viewModel: PerformanceViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Surface,
        bottomBar = { SpinNetBottomBar(currentDestination, onNavigate) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1A0A1A), Surface), radius = 900f))
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                "PERFORMANCE CORE",
                color = Secondary,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Estatísticas",
                color = OnSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // evolução semanal
                val growthSign = if (stats.volumeGrowthPercentage >= 0) "+" else ""
                KpiCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.TrendingUp,
                    iconTint = Secondary,
                    topLabel = "EVOLUÇÃO",

                    topLabelColor = OnSurfaceVariant,
                    value = "$growthSign${stats.volumeGrowthPercentage}%",
                    valueColor = if (stats.volumeGrowthPercentage >= 0) Secondary else Color.Red,
                    bottomLabel = "VOL. SEMANAL"
                )

                // total de treinos
                KpiCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.FitnessCenter,
                    iconTint = Tertiary,
                    topLabel = "LIFETIME",
                    topLabelColor = OnSurfaceVariant,
                    value = "${stats.totalDrills}",
                    valueColor = OnSurface,
                    bottomLabel = "TOTAL DRILLS"
                )

                //streakatual
                KpiCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.LocalFireDepartment,
                    iconTint = Secondary,
                    topLabel = if (stats.dayStreak > 0) "ON FIRE" else "INACTIVE",
                    topLabelColor = if (stats.dayStreak > 0) Secondary else OnSurfaceVariant,
                    value = "${stats.dayStreak}",
                    valueColor = if (stats.dayStreak > 0) Secondary else OnSurface,
                    bottomLabel = "DAY STREAK"
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Insights de Rotina",
                color = OnSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val totalHours = stats.timeTrained / 3_600_000L
                    val totalMinutes = (stats.timeTrained % 3_600_000L) / 60_000L
                    val weekHours = stats.timeTrainedThisWeek / 3_600_000L
                    val weekMinutes = (stats.timeTrainedThisWeek % 3_600_000L) / 60_000L

                    StatRow(
                        label = "Tempo Total Treinado", value = "${totalHours}h ${totalMinutes}m"
                    )
                    StatRow(
                        label = "Tempo Treinado (Esta Semana)", value = "${weekHours}h ${weekMinutes}m"
                    )
                    StatRow(
                        label = "Média por Sessão",
                        value = "${stats.averageSessionDurationMinutes} min"
                    )
                    StatRow(
                        label = "Treino mais Longo", value = "${stats.longestSessionMinutes} min"
                    )
                    StatRow(
                        label = "Dias Ativos (Este Mês)",
                        value = "${stats.activeDaysThisMonth} dias"
                    )
                    StatRow(
                        label = "Melhor Ofensiva Histórica", value = "${stats.bestDayStreak} dias"
                    )
                    StatRow(label = "Lado Preferido da Raquete", value = stats.racketPreferredSide)

                    val hiatoText = when (stats.daysSinceLastSession) {
                        0 -> "Treinou hoje!"
                        1 -> "Ontem"
                        else -> "Há ${stats.daysSinceLastSession} dias"
                    }
                    StatRow(label = "Último Treino", value = hiatoText)
                }
            }

            Spacer(Modifier.height(16.dp))


            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Atividade Semanal",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Minutos de treino nos últimos 7 dias",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(14.dp))
                    WeeklyActivityChart(sessions = stats.recentSessions)
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Evolução da Precisão",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Precisão média por semana (últimas 8 semanas)",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(14.dp))
                    AccuracyLineChart(data = stats.weeklyAccuracy)
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Calendário de Treinos",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Visualização das sessões efetuadas este mês",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(14.dp))
                    TrainingCalendarGrid(sessions = stats.recentSessions)
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Distribuição por Rotina",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Percentagem de treinos por tipo de rotina",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(14.dp))
                    RoutineDistributionChart(sessions = stats.recentSessions)
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Equilíbrio de Raquete",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Distribuição de jogadas entre Forehand e Backhand",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(16.dp))
                    RacketSideBalanceChart(sessions = stats.recentSessions)
                }
            }

            Spacer(Modifier.height(16.dp))

            // historico de sessoes recentes
            Text(
                "Routine History",
                color = OnSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            if (stats.recentSessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.FitnessCenter,
                            null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Sem sessões registadas.",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                stats.recentSessions.forEach { session ->
                    SessionHistoryCard(session)
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            color = OnSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    topLabel: String,
    topLabelColor: Color,
    value: String,
    valueColor: Color,
    bottomLabel: String
) {
    GlassCard(modifier = modifier, innerPadding = 12.dp) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
                Text(
                    topLabel,
                    color = topLabelColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                value,
                color = valueColor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                maxLines = 1
            )
            Text(
                bottomLabel,
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WeeklyActivityChart(sessions: List<TrainingSession>) {
    val dailyVolumes = (0..6).map { i ->
        val dayCal = java.util.Calendar.getInstance()
        dayCal.add(java.util.Calendar.DAY_OF_YEAR, -i)
        
        // Start of that day
        dayCal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        dayCal.set(java.util.Calendar.MINUTE, 0)
        dayCal.set(java.util.Calendar.SECOND, 0)
        dayCal.set(java.util.Calendar.MILLISECOND, 0)
        val start = dayCal.timeInMillis
        val end = start + 86_400_000L
        
        val volume = sessions.filter { it.completedAt in start..<end }.sumOf { it.durationMinutes }
        val dayLabel = when (dayCal.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.SUNDAY -> "D"
            java.util.Calendar.MONDAY -> "S"
            java.util.Calendar.TUESDAY -> "T"
            java.util.Calendar.WEDNESDAY -> "Q"
            java.util.Calendar.THURSDAY -> "Q"
            java.util.Calendar.FRIDAY -> "S"
            java.util.Calendar.SATURDAY -> "S"
            else -> ""
        }
        dayLabel to volume
    }.reversed()

    val maxVolume = dailyVolumes.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        dailyVolumes.forEach { (day, volume) ->
            val heightPercentage = volume.toFloat() / maxVolume.toFloat()
            val animatedHeightPercentage by animateFloatAsState(
                targetValue = heightPercentage,
                animationSpec = tween(900),
                label = "barHeight"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                if (volume > 0) {
                    Text(
                        "${volume}m",
                        color = Secondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(0.4f)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(animatedHeightPercentage)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = if (volume > 0) {
                                        listOf(Secondary, Secondary.copy(alpha = 0.4f))
                                    } else {
                                        listOf(Color.Transparent, Color.Transparent)
                                    }
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    day,
                    color = if (volume > 0) OnSurface else OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SessionHistoryCard(session: TrainingSession) {
    val dateStr = remember(session.completedAt) {
        if (session.completedAt == 0L) "—"
        else SimpleDateFormat("dd MMM yyyy  HH:mm", Locale.getDefault()).format(Date(session.completedAt))
    }
    val accentColor = when {
        session.accuracy >= 85 -> NeonGreen
        session.accuracy >= 60 -> Tertiary
        else -> Secondary
    }
    val qualityLabel = when {
        session.accuracy >= 85 -> "Excelente"
        session.accuracy >= 60 -> "Bom"
        else -> "A melhorar"
    }

    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(200),
        label = "chevron"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { expanded = !expanded },
        innerPadding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ── Header row ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Bolt, null, tint = accentColor, modifier = Modifier.size(20.dp))
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        session.routineTitle.ifBlank { "Sessão de treino" },
                        color = OnSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${session.durationMinutes} min • ${session.reps} reps • $dateStr",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Accuracy badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${session.accuracy}%",
                        color = accentColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(6.dp))

                Icon(
                    Icons.Outlined.ChevronRight,
                    null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = chevronRotation }
                )
            }

            // ── Expanded feedback detail ──
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                Spacer(Modifier.height(12.dp))

                Text(
                    "FEEDBACK DA SESSÃO",
                    color = Secondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Accuracy detail
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${session.accuracy}%",
                            color = accentColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "PRECISÃO",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            qualityLabel,
                            color = accentColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Racket side
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.SportsTennis,
                            null,
                            tint = Tertiary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            session.racketSide.ifBlank { "—" },
                            color = OnSurface,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "RAQUETE",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    // Duration
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${session.durationMinutes}m",
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "DURAÇÃO",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            "${session.reps} reps",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Notes (only if present)
                if (session.notes.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Outlined.EditNote,
                                null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Notas",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            session.notes,
                            color = OnSurface,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingCalendarGrid(sessions: List<TrainingSession>) {
    val calendar = java.util.Calendar.getInstance()
    val currentMonth = calendar.get(java.util.Calendar.MONTH)
    val currentYear = calendar.get(java.util.Calendar.YEAR)

    val trainedDays = sessions.mapNotNull { session ->
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = session.completedAt }
        if (cal.get(java.util.Calendar.MONTH) == currentMonth && cal.get(java.util.Calendar.YEAR) == currentYear) {
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        } else null
    }.toSet()

    val tempCal = java.util.Calendar.getInstance()
    tempCal.set(java.util.Calendar.YEAR, currentYear)
    tempCal.set(java.util.Calendar.MONTH, currentMonth)
    tempCal.set(java.util.Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = tempCal.get(java.util.Calendar.DAY_OF_WEEK)
    val maxDays = tempCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

    val monthName = when (currentMonth) {
        java.util.Calendar.JANUARY -> "Janeiro"
        java.util.Calendar.FEBRUARY -> "Fevereiro"
        java.util.Calendar.MARCH -> "Março"
        java.util.Calendar.APRIL -> "Abril"
        java.util.Calendar.MAY -> "Maio"
        java.util.Calendar.JUNE -> "Junho"
        java.util.Calendar.JULY -> "Julho"
        java.util.Calendar.AUGUST -> "Agosto"
        java.util.Calendar.SEPTEMBER -> "Setembro"
        java.util.Calendar.OCTOBER -> "Outubro"
        java.util.Calendar.NOVEMBER -> "Novembro"
        java.util.Calendar.DECEMBER -> "Dezembro"
        else -> ""
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            "$monthName $currentYear",
            color = OnSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val weekdays = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekdays.forEach { day ->
                Text(
                    day,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    modifier = Modifier.width(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        val startOffset = if (firstDayOfWeek == java.util.Calendar.SUNDAY) 6 else firstDayOfWeek - 2
        val totalCells = maxDays + startOffset
        val rowsCount = (totalCells + 6) / 7

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (row in 0 until rowsCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - startOffset + 1
                        
                        if (dayNumber in 1..maxDays) {
                            val didTrain = trainedDays.contains(dayNumber)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (didTrain) Secondary else SurfaceContainerHighest
                                    )
                                    .border(
                                        1.dp,
                                        if (didTrain) Secondary else Color.White.copy(alpha = 0.05f),
                                        RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$dayNumber",
                                    color = if (didTrain) Color.Black else OnSurface,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Box(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineDistributionChart(sessions: List<TrainingSession>) {
    val total = sessions.size.coerceAtLeast(1)
    val grouped = sessions.groupBy { it.routineTitle.ifBlank { "Sem Título" } }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

    if (grouped.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sem rotinas registadas.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        grouped.forEachIndexed { index, (title, count) ->
            val percentage = (count.toFloat() / total.toFloat() * 100).toInt()
            val animatedPercentage by animateFloatAsState(
                targetValue = count.toFloat() / total.toFloat(),
                animationSpec = tween(900),
                label = "routineBarHeight"
            )
            val barColor = if (index == 0) Secondary else if (index == 1) Tertiary else OnSurfaceVariant

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(title, color = OnSurface, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                    Text("$count treinos ($percentage%)", color = barColor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerHighest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPercentage)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun RacketSideBalanceChart(sessions: List<TrainingSession>) {
    val total = sessions.size.coerceAtLeast(1)
    val forehandCount = sessions.count { it.racketSide.equals("Forehand", ignoreCase = true) }
    val backhandCount = sessions.count { it.racketSide.equals("Backhand", ignoreCase = true) }
    val otherCount = total - forehandCount - backhandCount
    
    val fhPercentage = (forehandCount.toFloat() / total.toFloat() * 100).toInt()
    val bhPercentage = (backhandCount.toFloat() / total.toFloat() * 100).toInt()
    val otherPercentage = 100 - fhPercentage - bhPercentage
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Forehand ($fhPercentage%)", color = Secondary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text("Backhand ($bhPercentage%)", color = Tertiary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceContainerHighest)
        ) {
            if (forehandCount > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(forehandCount.toFloat().coerceAtLeast(0.1f))
                        .background(Secondary)
                )
            }
            if (backhandCount > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(backhandCount.toFloat().coerceAtLeast(0.1f))
                        .background(Tertiary)
                )
            }
            if (otherCount > 0 && otherCount != total) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(otherCount.toFloat().coerceAtLeast(0.1f))
                        .background(OnSurfaceVariant.copy(alpha = 0.4f))
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Baseado no lado dominante por sessão",
            color = OnSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun AccuracyLineChart(data: List<Pair<String, Float>>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Sem dados suficientes",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        return
    }

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000),
        label = "accuracyLineProgress"
    )

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height
            val paddingLeft = 40.dp.toPx()
            val paddingRight = 20.dp.toPx()
            val paddingTop = 10.dp.toPx()
            val paddingBottom = 20.dp.toPx()

            val chartWidth = width - paddingLeft - paddingRight
            val chartHeight = height - paddingTop - paddingBottom

            // Y grid lines at 0%, 25%, 50%, 75%, 100%
            val gridLines = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
            gridLines.forEach { ratio ->
                val y = paddingTop + chartHeight * (1f - ratio)
                drawLine(
                    color = OnSurfaceVariant.copy(alpha = 0.2f),
                    start = androidx.compose.ui.geometry.Offset(paddingLeft, y),
                    end = androidx.compose.ui.geometry.Offset(width - paddingRight, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val points = data.mapIndexed { index, pair ->
                val x = paddingLeft + if (data.size > 1) {
                    index.toFloat() / (data.size - 1) * chartWidth
                } else {
                    chartWidth / 2f
                }
                val normValue = (pair.second / 100f).coerceIn(0f, 1f)
                val y = paddingTop + chartHeight * (1f - normValue)
                x to y
            }

            // Draw line segment by segment, scaling by progress
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                
                val startRatio = i.toFloat() / (points.size - 1)
                val endRatio = (i + 1).toFloat() / (points.size - 1)
                
                if (animationProgress > startRatio) {
                    val segmentProgress = ((animationProgress - startRatio) / (endRatio - startRatio)).coerceIn(0f, 1f)
                    val currentX = p1.first + (p2.first - p1.first) * segmentProgress
                    val currentY = p1.second + (p2.second - p1.second) * segmentProgress
                    
                    drawLine(
                        color = Tertiary,
                        start = androidx.compose.ui.geometry.Offset(p1.first, p1.second),
                        end = androidx.compose.ui.geometry.Offset(currentX, currentY),
                        strokeWidth = 3.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
            }

            // Draw dots at data points
            points.forEachIndexed { index, (x, y) ->
                val dotRatio = index.toFloat() / (points.size - 1)
                if (animationProgress >= dotRatio) {
                    drawCircle(
                        color = Secondary,
                        center = androidx.compose.ui.geometry.Offset(x, y),
                        radius = 5.dp.toPx()
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Week labels aligned under points
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { (week, _) ->
                Text(
                    text = week,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    modifier = Modifier.width(32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}