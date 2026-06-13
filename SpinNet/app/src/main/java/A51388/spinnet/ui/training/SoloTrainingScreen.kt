package A51388.spinnet.ui.training

import A51388.spinnet.data.remote.GroqApiService
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.NeonButton
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.planner.RoutineViewModel
import A51388.spinnet.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoloTrainingScreen(
    onNavigate: (SpinNetDestination) -> Unit,
    routineViewModel: RoutineViewModel
) {
    var isFinished by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning, isFinished) {
        while (isRunning && !isFinished) {
            delay(1000L)
            elapsedSeconds += 1
        }
    }

    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    if (isFinished) {
        // Fase CONCLUSÃO
        var accuracy by remember { mutableFloatStateOf(80f) }
        var selectedSide by remember { mutableStateOf("Forehand") }
        var notes by remember { mutableStateOf("") }
        var aiAnalysis by remember { mutableStateOf<String?>(null) }
        var aiLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val finalDurationMin = maxOf(1, elapsedSeconds / 60)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    listOf(Color(0xFF0A192F), Color(0xFF051424))
                ))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                "Treino Livre Concluído! 🏓",
                                color = OnSurface,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$finalDurationMin min", color = NeonGreen, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    Text("DURAÇÃO", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                }
                                Box(Modifier.width(1.dp).height(40.dp).background(OutlineVariant.copy(alpha = 0.3f)))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("N/A", color = Secondary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    Text("EXERCÍCIOS", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                "FEEDBACK DA SESSÃO",
                                color = Secondary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Precisão sentida", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${accuracy.toInt()}%",
                                        color = if (accuracy >= 80) NeonGreen else if (accuracy >= 50) Tertiary else Secondary,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Slider(
                                    value = accuracy,
                                    onValueChange = { accuracy = it },
                                    valueRange = 0f..100f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Secondary,
                                        activeTrackColor = Secondary,
                                        inactiveTrackColor = SurfaceContainerHighest
                                    )
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Lado da Raquete", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf("Forehand", "Backhand", "Ambos").forEach { side ->
                                        val active = selectedSide == side
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (active) Secondary else SurfaceContainerLow)
                                                .border(1.dp, if (active) Secondary else OutlineVariant, RoundedCornerShape(8.dp))
                                                .clickable { selectedSide = side }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(side, color = if (active) Color.White else OnSurfaceVariant, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Notas (opcional)", color = OnSurfaceVariant) },
                                placeholder = { Text("Ex: bom ritmo hoje, transições rápidas...", color = OnSurfaceVariant.copy(0.5f)) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Secondary,
                                    unfocusedBorderColor = OutlineVariant,
                                    focusedTextColor = OnSurface,
                                    unfocusedTextColor = OnSurface
                                )
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                routineViewModel.completeSession(
                                    routineId = "solo",
                                    routineTitle = "Treino Livre",
                                    durationMinutes = finalDurationMin,
                                    reps = 0,
                                    accuracy = accuracy.toInt(),
                                    racketSide = selectedSide,
                                    notes = notes
                                )
                                onNavigate(SpinNetDestination.Dashboard)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "GUARDAR",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (aiLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Tertiary)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    aiLoading = true
                                    coroutineScope.launch {
                                        val res = GroqApiService.analyzeSession(
                                            routineTitle = "Treino Livre",
                                            durationMinutes = finalDurationMin,
                                            reps = 0,
                                            accuracy = accuracy.toInt(),
                                            racketSide = selectedSide,
                                            notes = notes
                                        )
                                        aiLoading = false
                                        res.fold<Unit, String>(
                                            onSuccess = { aiAnalysis = it },
                                            onFailure = { aiAnalysis = "Erro ao obter análise: ${it.localizedMessage}" }
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Tertiary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Tertiary)
                            ) {
                                Icon(Icons.Outlined.Psychology, null, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "ANÁLISE IA",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (aiAnalysis != null) {
                AlertDialog(
                    onDismissRequest = { aiAnalysis = null },
                    containerColor = Color(0xFF10172A),
                    shape = RoundedCornerShape(20.dp),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Outlined.AutoAwesome, null, tint = Tertiary)
                            Text(
                                "Análise do Teu Treino",
                                color = OnSurface,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = aiAnalysis!!,
                                color = OnSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { aiAnalysis = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                        ) {
                            Text("Fechar", color = Color.White)
                        }
                    }
                )
            }
        }
    } else {
        // Fase ATIVO
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    listOf(Color(0xFF0A192F), Color(0xFF051424))
                ))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Custom Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { onNavigate(SpinNetDestination.Dashboard) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Voltar", tint = OnSurface)
                    }
                    Text(
                        "TREINO LIVRE",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(48.dp))

                val sweepAngle = ((elapsedSeconds % 300) / 300f) * 360f

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    innerPadding = 24.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .drawBehind {
                                    val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    drawArc(
                                        color = Color.White.copy(alpha = 0.05f),
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = stroke
                                    )
                                    drawArc(
                                        color = Secondary,
                                        startAngle = -90f,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        style = stroke
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formattedTime,
                                color = OnSurface,
                                style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(48.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Secondary)
                                    .clickable { isRunning = !isRunning },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                    contentDescription = if (isRunning) "Pausar" else "Retomar",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        OutlinedButton(
                            onClick = {
                                isFinished = true
                                isRunning = false
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Error),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text("TERMINAR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
