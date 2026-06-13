package A51388.spinnet.ui.planner

import A51388.spinnet.data.remote.GroqApiService
import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.Shot
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.theme.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TrainingSessionScreen(
    onNavigate: (A51388.spinnet.ui.navigation.SpinNetDestination) -> Unit,
    routineViewModel: RoutineViewModel
) {
    val routine by routineViewModel.activeTrainingRoutine.collectAsState()
    val plan by routineViewModel.activeTrainingPlan.collectAsState()
    val allRoutines by routineViewModel.routines.collectAsState()

    // Determine sequence of training segments (Routine + Work time + Rest time)
    val segments = remember(routine, plan, allRoutines) {
        if (plan != null) {
            plan!!.routines.mapNotNull { pItem ->
                val fullRoutine = allRoutines.find { it.id == pItem.routineId }
                if (fullRoutine != null) {
                    TrainingSegment(
                        routine = fullRoutine,
                        workSeconds = pItem.durationSeconds,
                        restSeconds = pItem.restSeconds
                    )
                } else null
            }
        } else if (routine != null) {
            listOf(
                TrainingSegment(
                    routine = routine!!,
                    workSeconds = routine!!.shots.size * 4, // Estimate 4s per shot default
                    restSeconds = 15
                )
            )
        } else {
            emptyList()
        }
    }

    if (segments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Secondary)
                Spacer(Modifier.height(8.dp))
                Text("A carregar rotinas...", color = OnSurfaceVariant)
            }
        }
        return
    }

    var currentSegmentIndex by remember { mutableIntStateOf(0) }
    val currentSegment = segments[currentSegmentIndex]

    val shots = currentSegment.routine.shots

    var isResting by remember { mutableStateOf(false) }
    // Timer is active for the whole routine segment or REST phase
    var timeLeftSec by remember(currentSegmentIndex, isResting) {
        mutableIntStateOf(if (isResting) currentSegment.restSeconds else currentSegment.workSeconds)
    }
    var isTimerRunning by remember { mutableStateOf(true) }
    var showCompletionScreen by remember { mutableStateOf(false) }
    val startTime = remember { System.currentTimeMillis() }

    LaunchedEffect(timeLeftSec, isResting, isTimerRunning) {
        if (isTimerRunning && timeLeftSec > 0) {
            delay(1000L)
            timeLeftSec -= 1
        } else if (isTimerRunning && timeLeftSec == 0) {
            if (!isResting) {
                isResting = true
            } else {
                if (currentSegmentIndex < segments.lastIndex) {
                    isResting = false
                    currentSegmentIndex += 1
                } else {
                    showCompletionScreen = true
                    isTimerRunning = false
                }
            }
        }
    }

    if (showCompletionScreen) {
        var accuracy by remember { mutableFloatStateOf(80f) }
        var selectedSide by remember { mutableStateOf("Forehand") }
        var notes by remember { mutableStateOf("") }
        var aiAnalysis by remember { mutableStateOf<String?>(null) }
        var aiLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val finalDurationMin = maxOf(1, ((System.currentTimeMillis() - startTime) / 60000).toInt())
        val totalShots = segments.sumOf { it.routine.shots.size }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Brush.verticalGradient(
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
                    // Header de conclusão
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
                                "Treino Concluído! 💪",
                                color = OnSurface,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                plan?.title ?: currentSegment.routine.title,
                                color = Tertiary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
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
                                    Text("${segments.size}", color = Secondary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    Text("EXERCÍCIOS", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                }
                                Box(Modifier.width(1.dp).height(40.dp).background(OutlineVariant.copy(alpha = 0.3f)))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$totalShots", color = Tertiary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    Text("SHOTS", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                item {
                    // Feedback form
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text(
                                "FEEDBACK DA SESSÃO",
                                color = Secondary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            // Precisão slider
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Precisão sentida", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${accuracy.toInt()}%",
                                        color = when {
                                            accuracy >= 80 -> NeonGreen
                                            accuracy >= 50 -> Tertiary
                                            else -> Secondary
                                        },
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

                            // Lado raquete
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

                            // Notas livres
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Notas (opcional)", color = OnSurfaceVariant) },
                                placeholder = { Text("Ex: bom ritmo no exercício 2...", color = OnSurfaceVariant.copy(0.5f)) },
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
                    // Botões de ação
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (plan != null) {
                                    routineViewModel.completeSession(
                                        routineId = plan!!.id,
                                        routineTitle = plan!!.title,
                                        durationMinutes = finalDurationMin,
                                        reps = totalShots,
                                        accuracy = accuracy.toInt(),
                                        racketSide = selectedSide,
                                        notes = notes
                                    )
                                    routineViewModel.activeTrainingPlan.value = null
                                } else {
                                    routineViewModel.completeSession(
                                        routineId = currentSegment.routine.id,
                                        routineTitle = currentSegment.routine.title,
                                        durationMinutes = finalDurationMin,
                                        reps = currentSegment.routine.shots.size,
                                        accuracy = accuracy.toInt(),
                                        racketSide = selectedSide,
                                        notes = notes
                                    )
                                    routineViewModel.activeTrainingRoutine.value = null
                                }
                                onNavigate(A51388.spinnet.ui.navigation.SpinNetDestination.Dashboard)
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
                                "GUARDAR E TERMINAR",
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
                                            routineTitle = plan?.title ?: currentSegment.routine.title,
                                            durationMinutes = finalDurationMin,
                                            reps = totalShots,
                                            accuracy = accuracy.toInt(),
                                            racketSide = selectedSide,
                                            notes = notes
                                        )
                                        aiLoading = false
                                        res.fold(
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

                        OutlinedButton(
                            onClick = {
                                routineViewModel.activeTrainingPlan.value = null
                                routineViewModel.activeTrainingRoutine.value = null
                                onNavigate(A51388.spinnet.ui.navigation.SpinNetDestination.Dashboard)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Text("PASSAR À FRENTE (sem guardar)", style = MaterialTheme.typography.labelMedium)
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
        }
        return
    

    var selectedShotForDetail by remember { mutableStateOf<Shot?>(null) }

    if (selectedShotForDetail != null) {
        val shot = selectedShotForDetail!!
        AlertDialog(
            onDismissRequest = { selectedShotForDetail = null },
            containerColor = Color(0xFF10172A),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Detalhes do Disparo (Shot ${shot.index})",
                    color = OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Zona", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            Text("${shot.zone}", color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Efeito", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            Text(shot.spinName, color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Velocidade", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            Text("${shot.velocity}m/s", color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RAQUETE", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            Text(shot.racketSide, color = OnSurface, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Mesa 4x4 — zonas 1-8 = meu lado (vermelho), 9-16 = adversário (azul)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(3f / 2f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF020C1B))
                            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            // Rede vertical
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxHeight()
                                    .width(4.dp)
                                    .background(Color.White)
                            )
                            Column(modifier = Modifier.fillMaxSize()) {
                                for (r in 0 until 4) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        for (c in 0 until 4) {
                                            // Novo esquema: 1-8 = meu lado (cols 0-1), 9-16 = adversário (cols 2-3)
                                            val z = if (c < 2) r * 2 + c + 1 else r * 2 + (c - 2) + 9
                                            val isPlayerSide = c < 2
                                            val isTarget = z == shot.zone
                                            val sideColor = if (isPlayerSide) Secondary else Tertiary
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .border(0.5.dp, sideColor.copy(alpha = 0.15f))
                                                    .background(
                                                        if (isTarget) sideColor.copy(alpha = 0.65f)
                                                        else sideColor.copy(alpha = 0.05f)
                                                    )
                                                    .then(if (isTarget) Modifier.border(1.5.dp, sideColor) else Modifier),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isTarget) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(10.dp)
                                                            .clip(CircleShape)
                                                            .background(Color.White)
                                                    )
                                                } else {
                                                    Text(
                                                        "$z",
                                                        color = sideColor.copy(alpha = 0.5f),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 9.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text("Frequência: ${shot.freq}", color = Tertiary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedShotForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    Scaffold(containerColor = Surface) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF1A1A3E), Surface)))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    routineViewModel.activeTrainingRoutine.value = null
                    routineViewModel.activeTrainingPlan.value = null
                    onNavigate(A51388.spinnet.ui.navigation.SpinNetDestination.Dashboard)
                }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = OnSurface)
                }
                Text(
                    if (isResting) "PAUSA" else "TREINO",
                    color = if (isResting) Tertiary else Secondary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Secondary.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Exercício ${currentSegmentIndex + 1}/${segments.size}",
                        color = Secondary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            if (!isResting) {
                // EXERCISE ACTIVE STATE
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            currentSegment.routine.title.uppercase(),
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Timer Circle
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Secondary.copy(alpha = 0.15f))
                                .border(3.dp, Secondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$timeLeftSec",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Toca num shot para ver detalhes:",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Button(
                                onClick = {
                                    isResting = true
                                    timeLeftSec = currentSegment.restSeconds
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Error.copy(alpha = 0.8f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("SKIP", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Shots clickable list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            shots.forEach { shot ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceContainerLow)
                                        .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                                        .clickable { selectedShotForDetail = shot }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "Shot ${shot.index}: Zona ${shot.zone}",
                                            color = OnSurface,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            shot.spinName,
                                            color = Tertiary,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // REST STATE
                val nextExerciseName = if (currentSegmentIndex < segments.lastIndex) {
                    segments[currentSegmentIndex + 1].routine.title
                } else "FIM DO TREINO"

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            "PAUSA / DESCANSO",
                            color = Tertiary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Timer Circle (Rest Color)
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(Tertiary.copy(alpha = 0.15f))
                                .border(3.dp, Tertiary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$timeLeftSec",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "PRÓXIMO EXERCÍCIO:",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                nextExerciseName.uppercase(),
                                color = OnSurface,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                if (currentSegmentIndex < segments.lastIndex) {
                                    isResting = false
                                    currentSegmentIndex += 1
                                } else {
                                    showCompletionScreen = true
                                    isTimerRunning = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SKIP PAUSE", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

data class TrainingSegment(
    val routine: Routine,
    val workSeconds: Int,
    val restSeconds: Int
)
