package A51388.spinnet.ui.ai

import A51388.spinnet.data.model.GeneratedShot
import A51388.spinnet.data.model.Shot
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.NeonButton
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.planner.RoutineViewModel
import A51388.spinnet.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiRoutineScreen(
    onNavigate: (SpinNetDestination) -> Unit,
    routineViewModel: RoutineViewModel,
    aiViewModel: AiRoutineViewModel = viewModel()
) {
    val state by aiViewModel.state.collectAsStateWithLifecycle()
    var promptText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Surface, topBar = {
            TopAppBar(
                title = { }, navigationIcon = {
                IconButton(onClick = { onNavigate(SpinNetDestination.RoutinePlanner) }) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Voltar",
                        tint = OnSurface
                    )
                }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1E0B36), Surface), radius = 900f))
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "AI COACH",
                color = Secondary,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Gerador de Rotinas",
                color = OnSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            if (state is AiState.Success) {
                val routine = (state as AiState.Success).routine
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            routine.title,
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${routine.shots.size} DISPAROS GERADOS",
                            color = Tertiary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainerLow)
                                .border(
                                    1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            routine.shots.forEach { shot: GeneratedShot ->
                                val sideAbbrev = when (shot.racketSide) {
                                    "Forehand" -> "FH"
                                    "Backhand" -> "BH"
                                    "Both" -> "FH+BH"
                                    else -> shot.racketSide
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainerLowest)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            "Shot ${shot.index}: Zona ${shot.zone}",
                                            color = OnSurface,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Vel: ${shot.velocity}m/s • Freq: ${shot.freq}",
                                            color = OnSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    Text(
                                        "${shot.spinName} • $sideAbbrev",
                                        color = Tertiary,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val modelShots = routine.shots.map { s: GeneratedShot ->
                                    Shot(
                                        index = s.index,
                                        zone = s.zone,
                                        spinName = s.spinName,
                                        velocity = s.velocity,
                                        freq = s.freq,
                                        racketSide = s.racketSide
                                    )
                                }
                                routineViewModel.loadGeneratedSequence(modelShots, routine.title)
                                aiViewModel.reset()
                                onNavigate(SpinNetDestination.RoutinePlanner)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "USAR ESTA ROTINA",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { aiViewModel.reset() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
                        ) {
                            Text("GERAR OUTRA", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = promptText,
                    onValueChange = { promptText = it },
                    label = {
                        Text(
                            "Descreve o teu treino", style = MaterialTheme.typography.bodySmall
                        )
                    },
                    placeholder = {
                        Text(
                            "Ex: treino de 30 minutos focado no backhand, nível intermédio, com bastante topspin",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SurfaceContainerHigh.copy(alpha = 0.6f), RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, GlassBorder, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = NeonGreen,
                        unfocusedLabelColor = OnSurfaceVariant,
                        cursorColor = NeonGreen,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    )
                )

                Spacer(Modifier.height(12.dp))

                val chips = listOf(
                    "Backhand 20min Iniciante",
                    "Topspin Avançado",
                    "Serviço e Receção",
                    "Defesa 45min",
                    "Competição FH+BH"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chips.forEach { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceContainerLow)
                                .border(1.dp, OutlineVariant, RoundedCornerShape(20.dp))
                                .clickable { promptText = chip }
                                .padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text(
                                chip,
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                NeonButton(
                    onClick = {
                        if (promptText.isNotBlank()) {
                            aiViewModel.generateRoutine(promptText)
                        }
                    }, enabled = promptText.isNotBlank() && state !is AiState.Loading
                ) {
                    Text("GERAR COM IA", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                if (state is AiState.Loading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Secondary)
                        Text(
                            "A IA está a criar o teu plano...",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (state is AiState.Error) {
                    val errorMsg = (state as AiState.Error).message
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}