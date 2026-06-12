package A51388.spinnet.ui.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.TrainingPlan
import A51388.spinnet.data.model.PlanRoutineItem
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*

data class PlanItemSelector(
    val routineId: String,
    val routineTitle: String,
    val shotsCount: Int,
    val isSelected: Boolean,
    val durationSeconds: Int,
    val restSeconds: Int
)

@Composable
fun MyRoutinesScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    viewModel: RoutineViewModel = viewModel()
) {
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val plans by viewModel.trainingPlans.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    var routineToDelete by remember { mutableStateOf<Routine?>(null) }
    var planToDelete by remember { mutableStateOf<TrainingPlan?>(null) }
    var showCreatePlanDialog by remember { mutableStateOf(false) }
    var planToShare by remember { mutableStateOf<TrainingPlan?>(null) }

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

    if (planToDelete != null) {
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            containerColor = SurfaceContainerHigh,
            title = { Text("Apagar plano?", color = OnSurface, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "\"${planToDelete!!.title}\" será eliminado permanentemente.",
                    color = OnSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTrainingPlan(planToDelete!!.id)
                        planToDelete = null
                    }, colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) { Text("APAGAR") }
            },
            dismissButton = {
                OutlinedButton(onClick = { planToDelete = null }) { Text("CANCELAR") }
            })
    }

    if (planToShare != null) {
        var customTitle by remember { mutableStateOf(planToShare!!.title) }
        var description by remember { mutableStateOf(planToShare!!.description) }
        AlertDialog(
            onDismissRequest = { planToShare = null },
            containerColor = SurfaceContainerHigh,
            title = { Text("Partilhar Plano", color = OnSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Título do Plano", color = OnSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = OutlineVariant, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição", color = OnSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = OutlineVariant, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.shareTrainingPlan(planToShare!!, customTitle, description)
                        planToShare = null
                    }, enabled = customTitle.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) { Text("PARTILHAR") }
            },
            dismissButton = {
                OutlinedButton(onClick = { planToShare = null }) { Text("CANCELAR") }
            })
    }

    var planBeingEdited by remember { mutableStateOf<TrainingPlan?>(null) }

    if (showCreatePlanDialog) {
        val isEditing = planBeingEdited != null
        var planName by remember { mutableStateOf(planBeingEdited?.title ?: "") }
        var planDesc by remember { mutableStateOf(planBeingEdited?.description ?: "") }
        // Keep a list of pair: Routine ID and a mutable state of (enabled, durationSecs, restSecs)
        // Order is determined by list index.
        val planItemsList = remember(routines, planBeingEdited) {
            mutableStateListOf<PlanItemSelector>().apply {
                if (isEditing) {
                    // Start with the exact ordered routines from the edited plan
                    planBeingEdited!!.routines.forEach { rItem ->
                        val routineFull = routines.find { it.id == rItem.routineId }
                        add(
                            PlanItemSelector(
                                routineId = rItem.routineId,
                                routineTitle = rItem.routineTitle,
                                shotsCount = routineFull?.shots?.size ?: rItem.shotsCount,
                                isSelected = true,
                                durationSeconds = rItem.durationSeconds,
                                restSeconds = rItem.restSeconds
                            )
                        )
                    }
                    // Add other available routines that are not currently in the plan
                    routines.forEach { r ->
                        if (none { it.routineId == r.id }) {
                            add(
                                PlanItemSelector(
                                    routineId = r.id,
                                    routineTitle = r.title,
                                    shotsCount = r.shots.size,
                                    isSelected = false,
                                    durationSeconds = 60,
                                    restSeconds = 15
                                )
                            )
                        }
                    }
                } else {
                    addAll(routines.map { r ->
                        PlanItemSelector(
                            routineId = r.id,
                            routineTitle = r.title,
                            shotsCount = r.shots.size,
                            isSelected = false,
                            durationSeconds = 60,
                            restSeconds = 15
                        )
                    })
                }
            }
        }

        AlertDialog(
            onDismissRequest = {
                showCreatePlanDialog = false
                planBeingEdited = null
            },
            containerColor = SurfaceContainerHigh,
            title = { Text(if (isEditing) "Editar Plano de Treino" else "Criar Plano de Treino", color = OnSurface, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = planName,
                        onValueChange = { planName = it },
                        label = { Text("Nome do Plano", color = OnSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = OutlineVariant, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = planDesc,
                        onValueChange = { planDesc = it },
                        label = { Text("Descrição", color = OnSurfaceVariant) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = OutlineVariant, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Escolhe e Ordena as Rotinas:", color = OnSurface, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (planItemsList.isEmpty()) {
                        Text("Não tens rotinas criadas para adicionar ao plano.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }

                    planItemsList.forEachIndexed { index, item ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Checkbox(
                                            checked = item.isSelected,
                                            onCheckedChange = { isChecked ->
                                                planItemsList[index] = item.copy(isSelected = isChecked)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = Secondary)
                                        )
                                        Column {
                                            Text(item.routineTitle, color = OnSurface, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text("${item.shotsCount} disparos", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Row {
                                        IconButton(
                                            enabled = index > 0,
                                            onClick = {
                                                val temp = planItemsList[index]
                                                planItemsList.removeAt(index)
                                                planItemsList.add(index - 1, temp)
                                            }
                                        ) {
                                            Icon(Icons.Outlined.ArrowUpward, "Subir", tint = if (index > 0) OnSurface else OnSurfaceVariant.copy(alpha = 0.3f))
                                        }
                                        IconButton(
                                            enabled = index < planItemsList.size - 1,
                                            onClick = {
                                                val temp = planItemsList[index]
                                                planItemsList.removeAt(index)
                                                planItemsList.add(index + 1, temp)
                                            }
                                        ) {
                                            Icon(Icons.Outlined.ArrowDownward, "Descer", tint = if (index < planItemsList.size - 1) OnSurface else OnSurfaceVariant.copy(alpha = 0.3f))
                                        }
                                    }
                                }

                                if (item.isSelected) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        var durText by remember(item.durationSeconds) { mutableStateOf(item.durationSeconds.toString()) }
                                        var restText by remember(item.restSeconds) { mutableStateOf(item.restSeconds.toString()) }

                                        OutlinedTextField(
                                            value = durText,
                                            onValueChange = { newValue ->
                                                durText = newValue.filter { it.isDigit() }
                                                planItemsList[index] = item.copy(durationSeconds = durText.toIntOrNull() ?: 0)
                                            },
                                            label = { Text("Treino (seg)", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = OutlineVariant, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                                            modifier = Modifier.weight(1f)
                                        )

                                        OutlinedTextField(
                                            value = restText,
                                            onValueChange = { newValue ->
                                                restText = newValue.filter { it.isDigit() }
                                                planItemsList[index] = item.copy(restSeconds = restText.toIntOrNull() ?: 0)
                                            },
                                            label = { Text("Descanso (seg)", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Secondary, unfocusedBorderColor = OutlineVariant, focusedTextColor = OnSurface, unfocusedTextColor = OnSurface),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val items = planItemsList.filter { it.isSelected }.map { selector ->
                            PlanRoutineItem(
                                routineId = selector.routineId,
                                routineTitle = selector.routineTitle,
                                durationMinutes = selector.durationSeconds / 60, // compatibility
                                durationSeconds = selector.durationSeconds,
                                restSeconds = selector.restSeconds,
                                shotsCount = selector.shotsCount
                            )
                        }
                        viewModel.saveTrainingPlan(
                            id = planBeingEdited?.id,
                            title = planName,
                            description = planDesc,
                            items = items,
                            workTimeSeconds = 60,
                            restTimeSeconds = 15
                        )
                        showCreatePlanDialog = false
                        planBeingEdited = null
                    },
                    enabled = planName.isNotBlank() && planItemsList.any { it.isSelected },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                ) { Text(if (isEditing) "GRAVAR" else "CRIAR") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showCreatePlanDialog = false
                    planBeingEdited = null
                }) { Text("CANCELAR") }
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
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "AS MINHAS",
                        color = Secondary,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (selectedTab == 0) "Exercícios" else "Planos",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedTab == 1) {
                    Button(
                        onClick = { showCreatePlanDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Criar Plano")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Secondary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Secondary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("EXERCÍCIOS (${routines.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("PLANOS DE TREINO (${plans.size})", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(Modifier.height(16.dp))

            if (selectedTab == 0) {
                if (routines.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.FitnessCenter, null, tint = OnSurfaceVariant, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Ainda não tens exercícios guardados.", color = OnSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(routines, key = { it.id }) { routine ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(routine.title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.height(4.dp))
                                        Text("${routine.shots.size} disparos", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(onClick = {
                                            viewModel.activeTrainingRoutine.value = routine
                                            viewModel.activeTrainingPlan.value = null
                                            onNavigate(SpinNetDestination.TrainingSession)
                                        }) {
                                            Icon(Icons.Outlined.PlayArrow, null, tint = NeonGreen)
                                        }
                                        IconButton(onClick = { routineToDelete = routine }) {
                                            Icon(Icons.Outlined.Delete, null, tint = Secondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (plans.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.ListAlt, null, tint = OnSurfaceVariant, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Ainda não tens planos de treino.", color = OnSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(plans, key = { it.id }) { plan ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(plan.title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            if (plan.description.isNotBlank()) {
                                                Text(plan.description, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            IconButton(onClick = {
                                                planBeingEdited = plan
                                                showCreatePlanDialog = true
                                            }) {
                                                Icon(Icons.Outlined.Edit, null, tint = OnSurface)
                                            }
                                            IconButton(onClick = { planToShare = plan }) {
                                                Icon(Icons.Outlined.Share, null, tint = Tertiary)
                                            }
                                            IconButton(onClick = { planToDelete = plan }) {
                                                Icon(Icons.Outlined.Delete, null, tint = Secondary)
                                            }
                                        }
                                    }
                                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                                    plan.routines.forEach { rItem ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(rItem.routineTitle, color = OnSurface, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Text("${rItem.durationSeconds}s / ${rItem.restSeconds}s", color = Tertiary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                                Icon(
                                                    Icons.Outlined.PlayArrow,
                                                    null,
                                                    tint = NeonGreen,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clickable {
                                                            viewModel.activeTrainingPlan.value = plan
                                                            viewModel.activeTrainingRoutine.value = null
                                                            onNavigate(SpinNetDestination.TrainingSession)
                                                        }
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
        }
    }
}