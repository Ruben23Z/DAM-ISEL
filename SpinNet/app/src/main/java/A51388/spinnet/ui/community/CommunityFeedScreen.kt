package A51388.spinnet.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.SharedRoutine
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.planner.RoutineViewModel
import A51388.spinnet.ui.theme.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@Composable
fun CommunityFeedScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    routineViewModel: RoutineViewModel = viewModel()
) {
    val communityViewModel: CommunityViewModel = viewModel()
    val feed by communityViewModel.feed.collectAsStateWithLifecycle()
    val sharedPlans by communityViewModel.sharedPlans.collectAsStateWithLifecycle()
    val cloneMessage by routineViewModel.cloneSuccess.collectAsStateWithLifecycle()
    val actionMessage by communityViewModel.actionMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentUid = communityViewModel.currentUid

    var selectedTab by remember { mutableIntStateOf(0) }
    var feedSubTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    val tabs = listOf("Feed", "Network")

    LaunchedEffect(cloneMessage) {
        cloneMessage?.let {
            snackbarHostState.showSnackbar(it)
            routineViewModel.clearCloneMessage()
        }
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            communityViewModel.clearActionMessage()
        }
    }

    Scaffold(
        containerColor = Surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            SpinNetBottomBar(
                currentDestination = currentDestination, onNavigate = onNavigate
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF0A1A3E), Surface), radius = 900f
                    )
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SPINNET",
                            color = NeonGreen,
                            style = MaterialTheme.typography.labelLarge,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = "Explore",
                            color = OnSurface,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                            .border(1.dp, GlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Descobre rotinas e planos partilhados pela comunidade SpinNet.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(16.dp))
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceContainer,
                contentColor = NeonGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NeonGreen
                    )
                }) {
                tabs.forEachIndexed { idx, title ->
                    Tab(selected = selectedTab == idx, onClick = { selectedTab = idx }, text = {
                        Text(
                            text = title.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedTab == idx) NeonGreen else OnSurfaceVariant
                        )
                    })
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        TabRow(
                            selectedTabIndex = feedSubTab,
                            containerColor = Color.Transparent,
                            contentColor = Secondary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[feedSubTab]),
                                    color = Secondary
                                )
                            }
                        ) {
                            Tab(
                                selected = feedSubTab == 0,
                                onClick = { feedSubTab = 0 },
                                text = { Text("ROTINAS", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = feedSubTab == 1,
                                onClick = { feedSubTab = 1 },
                                text = { Text("PLANOS DE TREINO", fontWeight = FontWeight.Bold) }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        if (feedSubTab == 0) {
                            if (feed.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Outlined.PeopleOutline,
                                            contentDescription = null,
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        Text(
                                            "Ainda não há rotinas partilhadas.",
                                            color = OnSurfaceVariant,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            } else {
                                feed.forEach { routine ->
                                    CommunityRoutineCard(
                                        routine = routine,
                                        routineViewModel = routineViewModel,
                                        communityViewModel = communityViewModel,
                                        isOwner = routine.sharedBy == currentUid
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        } else {
                            if (sharedPlans.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Outlined.ListAlt,
                                            contentDescription = null,
                                            tint = OnSurfaceVariant,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        Text(
                                            "Ainda não há planos partilhados.",
                                            color = OnSurfaceVariant,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            } else {
                                sharedPlans.forEach { plan ->
                                    CommunityPlanCard(
                                        plan = plan,
                                        routineViewModel = routineViewModel,
                                        communityViewModel = communityViewModel,
                                        isOwner = (plan["sharedBy"] as? String) == currentUid
                                    )
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    if (selectedTab == 1) Icons.Outlined.People
                                    else Icons.Outlined.Notifications,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = if (selectedTab == 1) "Connect with other players"
                                    else "You're all caught up!",
                                    color = OnSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CommunityRoutineCard(
    routine: SharedRoutine,
    routineViewModel: RoutineViewModel,
    communityViewModel: CommunityViewModel,
    isOwner: Boolean
) {
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditSharedRoutineDialog(
            routine = routine,
            onDismiss = { showEditDialog = false },
            onSave = { newTitle, newDesc ->
                communityViewModel.updateSharedRoutine(routine.id, newTitle, newDesc)
                showEditDialog = false
            })
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFF1A1F2E),
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Apagar publicação?",
                    color = OnSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Esta ação é irreversível. A rotina é removida do feed para todos.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        communityViewModel.unshareRoutine(routine.id)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apagar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = OnSurfaceVariant)
                }
            })
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Secondary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            routine.sharedBy.take(1).uppercase(),
                            color = Secondary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            routine.title,
                            color = OnSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${routine.shots.size} shots",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Secondary.copy(alpha = 0.15f))
                            .border(1.dp, Secondary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "ROUTINE",
                            color = Secondary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    if (isOwner) {
                        Box {
                            IconButton(
                                onClick = { showMenu = true }, modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.MoreVert,
                                    contentDescription = "Opções",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                containerColor = Color(0xFF1E2433)
                            ) {
                                DropdownMenuItem(leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = null,
                                        tint = OnSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }, text = {
                                    Text(
                                        "Editar",
                                        color = OnSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }, onClick = {
                                    showMenu = false
                                    showEditDialog = true
                                })
                                DropdownMenuItem(leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }, text = {
                                    Text(
                                        "Tornar privado",
                                        color = OnSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }, onClick = {
                                    showMenu = false
                                    communityViewModel.setRoutinePrivate(routine.id)
                                })
                                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                                DropdownMenuItem(leadingIcon = {
                                    Icon(
                                        Icons.Outlined.DeleteOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFCF6679),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }, text = {
                                    Text(
                                        "Apagar",
                                        color = Color(0xFFCF6679),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }, onClick = {
                                    showMenu = false
                                    showDeleteConfirm = true
                                })
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(10.dp))

            if (!routine.description.isNullOrBlank()) {
                Text(
                    text = routine.description,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "SHOTS",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                    Text(
                        "${routine.shots.size}",
                        color = OnSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "ZONES",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                    Text(
                        routine.shots.map { it.zone }.distinct().size.toString(),
                        color = OnSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "SPINS",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp
                    )
                    Text(
                        routine.shots.map { it.spinName }.distinct().size.toString(),
                        color = OnSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!isOwner) {
                Button(
                    onClick = {
                        routineViewModel.addFromCommunity(
                            Routine(
                                id = routine.id,
                                title = routine.title,
                                shots = routine.shots,
                                createdAt = routine.createdAt
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary, contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "ADICIONAR AO MEU PLANO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityPlanCard(
    plan: Map<String, Any>,
    routineViewModel: RoutineViewModel,
    communityViewModel: CommunityViewModel,
    isOwner: Boolean
) {
    val title = plan["title"] as? String ?: "Plan"
    val desc = plan["description"] as? String ?: ""
    val rawRoutines = plan["routines"] as? List<Map<String, Any>> ?: emptyList()
    val planId = plan["id"] as? String ?: ""
    val sharedBy = plan["sharedBy"] as? String ?: ""

    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = Color(0xFF1A1F2E),
            shape = RoundedCornerShape(20.dp),
            title = { Text("Apagar publicação?", color = OnSurface, fontWeight = FontWeight.Bold) },
            text = { Text("Esta ação é irreversível. O plano de treino é removido do feed para todos.", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall) },
            confirmButton = {
                Button(
                    onClick = {
                        communityViewModel.unsharePlan(planId)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679)),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Apagar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar", color = OnSurfaceVariant) }
            })
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Tertiary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            sharedBy.take(1).uppercase(),
                            color = Tertiary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(title, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${rawRoutines.size} sequências", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Tertiary.copy(alpha = 0.15f))
                            .border(1.dp, Tertiary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text("PLAN", color = Tertiary, style = MaterialTheme.typography.labelSmall)
                    }
                    if (isOwner) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Outlined.Delete, null, tint = Error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            if (desc.isNotBlank()) {
                Text(desc, color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
            rawRoutines.forEach { r ->
                val rTitle = r["routineTitle"] as? String ?: ""
                val rDur = r["durationMinutes"] ?: 10
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(rTitle, color = OnSurface, style = MaterialTheme.typography.bodySmall)
                    Text("$rDur min", color = Tertiary, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            if (!isOwner) {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        communityViewModel.cloneSharedPlan(plan, routineViewModel)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.FileDownload, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("ADICIONAR PLANO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EditSharedRoutineDialog(
    routine: SharedRoutine,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf(routine.title) }
    var description by remember { mutableStateOf(routine.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1F2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Editar publicação",
                color = OnSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título", color = OnSurfaceVariant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Secondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Introdução", color = OnSurfaceVariant) },
                    maxLines = 4,
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Secondary,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface,
                        cursorColor = Secondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title.trim(), description.trim()) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = OnSurfaceVariant)
            }
        })
}