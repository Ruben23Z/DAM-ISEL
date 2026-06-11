package A51388.spinnet.ui.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import A51388.spinnet.data.model.Routine
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.planner.RoutineViewModel
import A51388.spinnet.ui.theme.*

@Composable
fun MyRoutinesScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    viewModel: RoutineViewModel = viewModel()
) {
    // Lista as rotinas, permite apagar uma  rotina
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    var routineToDelete by remember { mutableStateOf<Routine?>(null) }

    //confirmacao para apagar
    if (routineToDelete != null) {
        AlertDialog(
            onDismissRequest = { routineToDelete = null },
            containerColor = SurfaceContainerHigh,
            title = { Text("Apagar rotina?", color = OnSurface, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "\"${routineToDelete!!.title}\" será eliminada permanentemente.",
                    color = OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteRoutine(routineToDelete!!.id)
                        routineToDelete = null
                    }, colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) { Text("APAGAR") }
            },
            dismissButton = {
                OutlinedButton(onClick = { routineToDelete = null }) { Text("CANCELAR") }
            })
    }

    Scaffold(
        containerColor = Surface,
        bottomBar = { SpinNetBottomBar(currentDestination, onNavigate) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1A0A2E), Surface), radius = 900f))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Títulos
            Spacer(Modifier.height(24.dp))
            Text(
                "AS MINHAS",
                color = Secondary,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Rotinas",
                color = OnSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${routines.size} rotina${if (routines.size != 1) "s" else ""} guardada${if (routines.size != 1) "s" else ""}",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(20.dp))

            //para ecrã vazio
            if (routines.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.FitnessCenter,
                            null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Ainda não tens rotinas guardadas.",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(routines, key = { it.id }) { routine ->
                        // Cartão da rotina com efeito de vidro
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        routine.title,
                                        color = OnSurface,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${routine.shots.size} shot${if (routine.shots.size != 1) "s" else ""}",
                                        color = OnSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                // Botao de eliminação
                                IconButton(onClick = { routineToDelete = routine }) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        null,
                                        tint = Secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}