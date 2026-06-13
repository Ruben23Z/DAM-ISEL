package A51388.spinnet.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.TrainingSession
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.planner.RoutineViewModel
import A51388.spinnet.ui.profile.PerformanceViewModel
import A51388.spinnet.ui.profile.PlayerProfileViewModel
import A51388.spinnet.ui.theme.*

data class DrillItem(
    val name: String, val duration: String, val accuracy: Int, val tag: String
)

@Composable
fun DashboardScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    routineViewModel: RoutineViewModel = viewModel()
) {
    val performanceViewModel: PerformanceViewModel = viewModel()
    val playerProfileViewModel: PlayerProfileViewModel = viewModel()

    val profileState by playerProfileViewModel.profile.collectAsStateWithLifecycle()
    val statsState by performanceViewModel.stats.collectAsStateWithLifecycle()
    val routinesState by routineViewModel.routines.collectAsStateWithLifecycle()

    val username = profileState?.username ?: "Alex"
    val streakDays = statsState.dayStreak
    val growth = statsState.volumeGrowthPercentage

    val activeRoutine = routinesState.firstOrNull()

    val peakSpeed = if (statsState.recentSessions.isEmpty()) 118 else 95 + (statsState.totalDrills * 3) % 25
    val spinConsistency = if (statsState.recentSessions.isEmpty()) 2450 else 2100 + (statsState.recentSessions.map { it.accuracy }.average().toInt() * 5)

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Surface, bottomBar = {
            SpinNetBottomBar(
                currentDestination = currentDestination, onNavigate = onNavigate
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A3E), Surface), radius = 800f
                    )
                )
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SYSTEM STATUS: OPTIMAL",
                        color = Tertiary,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Welcome back, $username",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (growth >= 0) "Your performance is up $growth% recently." else "Your performance is down ${-growth}% recently.",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                GlassCard(
                    modifier = Modifier.wrapContentSize(), innerPadding = 10.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "DAILY STREAK",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$streakDays DAYS",
                                color = Tertiary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            val trainingPlans by routineViewModel.trainingPlans.collectAsStateWithLifecycle()
            val activePlan = trainingPlans.firstOrNull()

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE TRAINING PLAN",
                                color = Tertiary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = activePlan?.title ?: "No Training Plans Available",
                                color = OnSurface,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicatorCard(
                                progress = if (activePlan != null) 1.0f else 0.0f,
                                label = if (activePlan != null) "Ready to run: ${activePlan.routines.size} exercises" else "Go to My Routines to create a plan"
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    Spacer(Modifier.height(10.dp))

                    var showPlanSelector by remember { mutableStateOf(false) }
                    var selectedPlanForTraining by remember { mutableStateOf<A51388.spinnet.data.model.TrainingPlan?>(null) }

                    if (showPlanSelector) {
                        AlertDialog(
                            onDismissRequest = { showPlanSelector = false },
                            containerColor = Color(0xFF10172A),
                            shape = RoundedCornerShape(20.dp),
                            title = {
                                Text(
                                    "Selecionar Plano de Treino",
                                    color = OnSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (trainingPlans.isEmpty()) {
                                        Text("Nenhum plano disponível. Cria um plano na aba de Planos.", color = OnSurfaceVariant)
                                    }
                                    trainingPlans.forEach { plan ->
                                        val isSel = selectedPlanForTraining?.id == plan.id
                                        GlassCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedPlanForTraining = plan }
                                                .border(
                                                    1.dp,
                                                    if (isSel) Secondary else Color.Transparent,
                                                    RoundedCornerShape(12.dp)
                                                ),
                                            innerPadding = 12.dp
                                        ) {
                                            Column {
                                                Text(plan.title, color = OnSurface, fontWeight = FontWeight.Bold)
                                                Text("${plan.routines.size} exercícios", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedPlanForTraining?.let { plan ->
                                            routineViewModel.activeTrainingPlan.value = plan
                                            routineViewModel.activeTrainingRoutine.value = null
                                            showPlanSelector = false
                                            onNavigate(SpinNetDestination.TrainingSession)
                                        }
                                    },
                                    enabled = selectedPlanForTraining != null,
                                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                                ) {
                                    Text("CONFIRMAR E INICIAR", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                OutlinedButton(onClick = { showPlanSelector = false }) {
                                    Text("CANCELAR")
                                }
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                showPlanSelector = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Secondary, contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "START TRAINING",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "RECENT DRILLS",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (statsState.recentSessions.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Sem treinos recentes. Começa a treinar para ver estatísticas!", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                statsState.recentSessions.forEach { session ->
                    DrillCard(
                        drill = DrillItem(
                            name = session.routineTitle,
                            duration = "${session.durationMinutes} min",
                            accuracy = session.accuracy,
                            tag = session.racketSide.uppercase()
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LinearProgressIndicatorCard(progress: Float, label: String) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(1000), label = "progress"
    )
    Column {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = NeonGreen,
            trackColor = SurfaceContainerHighest
        )
        Spacer(Modifier.height(4.dp))
        Text(text = label, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ArcProgressRing(progress: Float, size: androidx.compose.ui.unit.Dp) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress, animationSpec = tween(1200), label = "arc"
    )
    Box(
        modifier = Modifier
            .size(size)
            .drawBehind {
                val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                val sweep = animatedProgress * 270f
                drawArc(
                    color = SurfaceContainerHighest,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = stroke
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(NeonGreen, VibrantPurple)
                    ), startAngle = 135f, sweepAngle = sweep, useCenter = false, style = stroke
                )
            }, contentAlignment = Alignment.Center
        ) {
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            color = NeonGreen,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SpinVectorBars() {
    val vectors = listOf(
        "TOP SPIN" to 0.85f,
        "BACK SPIN" to 0.60f,
        "SIDE SPIN" to 0.72f,
        "FLAT DRIVE" to 0.45f,
    )
    vectors.forEach { (label, value) ->
        val animated by animateFloatAsState(
            targetValue = value, animationSpec = tween(900), label = label
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(80.dp)
            )
            Spacer(Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { animated },
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = VibrantPurple,
                trackColor = SurfaceContainerHighest
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${(value * 100).toInt()}%",
                color = OnSurface,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(32.dp)
            )
        }
    }
}

@Composable
private fun DrillCard(drill: DrillItem) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(NeonGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = drill.tag,
                            color = NeonGreen,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = drill.duration,
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = drill.name,
                    color = OnSurface,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (drill.accuracy >= 90) NeonGreen.copy(alpha = 0.15f)
                        else VibrantPurple.copy(alpha = 0.15f)
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${drill.accuracy}%",
                    color = if (drill.accuracy >= 90) NeonGreen else Tertiary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF051424)
@Composable
fun DashboardPreview() {
    SpinNetTheme {
        DashboardScreen(
            currentDestination = SpinNetDestination.Dashboard, onNavigate = {})
    }
}


