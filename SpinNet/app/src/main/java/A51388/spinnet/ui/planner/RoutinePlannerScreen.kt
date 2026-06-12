package A51388.spinnet.ui.planner

import A51388.spinnet.data.model.Routine
import A51388.spinnet.data.model.Shot
import A51388.spinnet.notification.NotificationScheduler
import A51388.spinnet.ui.community.CommunityViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.GlassTextField
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.components.NeonButton
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.outlined.Delete

private enum class SpinDir(
    val label: String, val spinName: String, val icon: ImageVector
) {
    NW("NW", "Left Topspin", Icons.Outlined.NorthWest), N(
        "N", "Top Spin", Icons.Outlined.North
    ),
    NE("NE", "Right Topspin", Icons.Outlined.NorthEast), W(
        "W", "Left Side-Spin", Icons.Outlined.West
    ),
    E("E", "Right Side-Spin", Icons.Outlined.East), SW(
        "SW", "Left Backspin", Icons.Outlined.SouthWest
    ),
    S("S", "Back Spin", Icons.Outlined.South), SE("SE", "Right Backspin", Icons.Outlined.SouthEast),
}

private data class SeqShot(
    val index: Int, val zone: Int, val spin: SpinDir, val velocity: Int, val freq: String
)

@Composable
fun RoutinePlannerScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
    routineViewModel: RoutineViewModel = viewModel()
) {
    val communityViewModel: CommunityViewModel = viewModel()

    val cloneMessage by routineViewModel.cloneSuccess.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cloneMessage) {
        cloneMessage?.let {
            snackbarHostState.showSnackbar(it)
            routineViewModel.clearCloneMessage()
        }
    }

    // Rotina selecionada para partilh
    var routineToShare by remember { mutableStateOf<Routine?>(null) }

    if (routineToShare != null) {
        ShareRoutineDialog(
            routine = routineToShare!!,
            onDismiss = { routineToShare = null },
            onConfirm = { customTitle, description ->
                routineViewModel.shareRoutine(routineToShare!!, customTitle, description)
                routineToShare = null
            })
    }

    val savedRoutines by routineViewModel.routines.collectAsStateWithLifecycle()
    var routineTitle by remember { mutableStateOf("") }

    var selectedZone by remember { mutableStateOf<Int?>(3) }
    var selectedSpin by remember { mutableStateOf(SpinDir.E) }
    var intensity by remember { mutableFloatStateOf(0.85f) }
    var sequence by remember {
        mutableStateOf(
            listOf(
                SeqShot(1, 3, SpinDir.N, 45, "High"),
                SeqShot(2, 11, SpinDir.S, 30, "Low"),
                SeqShot(3, 6, SpinDir.W, 38, "Med"),
            )
        )
    }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { SpinNetBottomBar(currentDestination, onNavigate) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1A0A2E), Surface), radius = 900f))
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
                        "TRAINING MODULE",
                        color = Secondary,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Routine Planner",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Map zones and spin vectors for your session",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { onNavigate(SpinNetDestination.MyRoutines) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Outlined.History, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "MY ROUTINES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))


            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Target Grid",
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    //mesa (4×4) — Zonas 1-8 = meu lado (esq) | Zonas 9-16 = lado adversário (dir)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 2f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(3.dp, Color.White, RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp)
                        ) {
                            // Net separator vertical (linha de rede)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxHeight()
                                    .width(6.dp)
                                    .background(Color.White)
                            )
                            Column(modifier = Modifier.fillMaxSize()) {
                                for (row in 0 until 4) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        for (col in 0 until 4) {
                                            // Zones 1-8 = player side (left cols 0-1), 9-16 = opponent side (right cols 2-3)
                                            val zone = if (col < 2) row * 2 + col + 1 else row * 2 + (col - 2) + 9
                                            val isPlayerSide = col < 2
                                            val sel = zone == selectedZone
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .border(
                                                        0.5.dp,
                                                        if (isPlayerSide) Secondary.copy(alpha = 0.12f)
                                                        else Tertiary.copy(alpha = 0.12f)
                                                    )
                                                    .background(
                                                        if (sel) (if (isPlayerSide) Secondary else Tertiary).copy(alpha = 0.55f)
                                                        else if (isPlayerSide) Secondary.copy(alpha = 0.04f)
                                                        else Tertiary.copy(alpha = 0.04f)
                                                    )
                                                    .then(
                                                        if (sel) Modifier.border(
                                                            2.dp,
                                                            if (isPlayerSide) Secondary else Tertiary
                                                        ) else Modifier
                                                    )
                                                    .clickable(
                                                        indication = null,
                                                        interactionSource = remember { MutableInteractionSource() }
                                                    ) { selectedZone = zone },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "$zone",
                                                    color = if (sel) Color.White
                                                    else if (isPlayerSide) Secondary.copy(alpha = 0.75f)
                                                    else Tertiary.copy(alpha = 0.75f),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Secondary))
                            Text("MEU LADO (1-8)", color = Secondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(Tertiary))
                            Text("ADVERSÁRIO (9-16)", color = Tertiary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatusChip(
                            text = "SELECTING TARGET ZONE", dotColor = Secondary, showDot = true
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ecra de Spin
            AccentCard(accentColor = Secondary, side = AccentSide.LEFT) {
                Column {
                    Text(
                        "Spin Dynamics",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpinWheel(
                            selected = selectedSpin,
                            onSelect = { selectedSpin = it },
                            modifier = Modifier.size(160.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "SELECTED SPIN",
                                color = Secondary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                selectedSpin.spinName,
                                color = OnSurface,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "INTENSITY",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            "${(intensity * 100).toInt()}%",
                            color = Tertiary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(intensity)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(Brush.horizontalGradient(listOf(Secondary, Tertiary)))
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedZone?.let { z ->
                                    sequence = sequence + SeqShot(
                                        index = sequence.size + 1,
                                        zone = z,
                                        spin = selectedSpin,
                                        velocity = listOf(30, 38, 45, 52).random(),
                                        freq = listOf("Low", "Med", "High").random()
                                    )
                                }
                            },
                            enabled = selectedZone != null,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Secondary, contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                "ADD TO SEQUENCE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val playerZones = (1..8).toList()
                                val opponentZones = (9..16).toList()

                                // KEY FIX: read selectedZone first (updated by previous Randomize click),
                                // then fall back to last sequence item. This gives true alternation on
                                // repeated Randomize clicks without needing to add to sequence each time.
                                val currentZone = selectedZone
                                    ?: sequence.lastOrNull()?.zone
                                    ?: playerZones.random()
                                val currentIsPlayer = currentZone in playerZones
                                selectedZone = if (currentIsPlayer) opponentZones.random() else playerZones.random()
                                selectedSpin = SpinDir.entries.random()
                                intensity = 0.3f + (Math.random() * 0.7f).toFloat()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                "RANDOMIZE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sequencia
            AccentCard(accentColor = Tertiary, side = AccentSide.TOP) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Current Sequence",
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceContainer)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "${sequence.size} SHOTS",
                                color = Tertiary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    sequence.forEach { shot ->
                        ShotCard(shot = shot, onRemove = {
                            sequence = sequence.filter { it != shot }
                                .mapIndexed { i, s -> s.copy(index = i + 1) }
                        })
                        Spacer(Modifier.height(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                2.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp), contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.AddCircle,
                                null,
                                tint = OnSurfaceVariant.copy(alpha = 0.38f),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "ADD NEXT SHOT",
                                color = OnSurfaceVariant.copy(alpha = 0.38f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(Modifier.height(12.dp))

                    var showReviewDialog by remember { mutableStateOf(false) }
                    if (showReviewDialog) {
                        AlertDialog(
                            onDismissRequest = { showReviewDialog = false },
                            containerColor = Color(0xFF10172A),
                            shape = RoundedCornerShape(20.dp),
                            title = {
                                Text(
                                    "Review Sequence",
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
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    sequence.forEach { s ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(SurfaceContainerLow)
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Shot ${s.index}: Zone ${s.zone}",
                                                color = OnSurface,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                s.spin.spinName,
                                                color = Tertiary,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showReviewDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Secondary)
                                ) {
                                    Text("Fechar", color = Color.White)
                                }
                            }
                        )
                    }

                    Button(
                        onClick = {
                            showReviewDialog = true
                        },
                        enabled = sequence.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary, contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Text(
                            "REVIEW",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    GlassTextField(
                        value = routineTitle,
                        onValueChange = { routineTitle = it },
                        label = "Nome do exercício",
                        leadingIcon = Icons.Outlined.Edit
                    )

                    Spacer(Modifier.height(8.dp))
                    NeonButton(
                        onClick = {
                            if (routineTitle.isNotBlank() && sequence.isNotEmpty()) {
                                routineViewModel.saveRoutine(
                                    title = routineTitle, shots = sequence.map { s ->
                                        Shot(
                                            index = s.index,
                                            zone = s.zone,
                                            spinName = s.spin.spinName,
                                            velocity = s.velocity,
                                            freq = s.freq
                                        )
                                    })
                                sequence = emptyList()
                                routineTitle = ""
                            }
                        }, enabled = routineTitle.isNotBlank() && sequence.isNotEmpty()
                    ) { Text("SAVE EXERCISE") }

                    if (savedRoutines.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))

                        Text(
                            "SAVED ROUTINES",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        savedRoutines.forEach { routine ->
                            var showTimePicker by remember { mutableStateOf(false) }
                            val context = LocalContext.current

                            if (showTimePicker) {
                                val now = java.util.Calendar.getInstance()
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val cal = java.util.Calendar.getInstance().apply {
                                            set(java.util.Calendar.HOUR_OF_DAY, hour)
                                            set(java.util.Calendar.MINUTE, minute)
                                            set(java.util.Calendar.SECOND, 0)
                                            if (timeInMillis < System.currentTimeMillis()) add(
                                                java.util.Calendar.DAY_OF_YEAR, 1
                                            )
                                        }
                                        NotificationScheduler.schedule(
                                            context = context,
                                            routineId = routine.id,
                                            title = routine.title,
                                            shots = routine.shots.size,
                                            scheduledAt = cal.timeInMillis
                                        )
                                        showTimePicker = false
                                    },
                                    now.get(java.util.Calendar.HOUR_OF_DAY),
                                    now.get(java.util.Calendar.MINUTE),
                                    true
                                ).apply {
                                    setOnDismissListener {
                                        showTimePicker = false
                                    }
                                }.show()
                            }

                            GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
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
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            "${routine.shots.size} shots",
                                            color = OnSurfaceVariant,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = { showTimePicker = true },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Notifications,
                                                contentDescription = "Schedule",
                                                tint = Secondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                sequence = routine.shots.mapIndexed { i, s ->
                                                    SeqShot(
                                                        index = i + 1,
                                                        zone = s.zone,
                                                        spin = SpinDir.values()
                                                            .find { it.spinName == s.spinName }
                                                            ?: SpinDir.N,
                                                        velocity = s.velocity,
                                                        freq = s.freq)
                                                }
                                                routineTitle = routine.title
                                            }, modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.FileDownload,
                                                contentDescription = "Load",
                                                tint = Tertiary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { routineToShare = routine },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Share,
                                                contentDescription = "Share",
                                                tint = OnSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                NotificationScheduler.cancel(context, routine.id)
                                                routineViewModel.deleteRoutine(routine.id)
                                            }, modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = "Delete",
                                                tint = Error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private enum class AccentSide { LEFT, TOP }

@Composable
private fun AccentCard(accentColor: Color, side: AccentSide, content: @Composable () -> Unit) {
    when (side) {
        AccentSide.LEFT -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF282A2E).copy(alpha = 0.85f),
                                Color(0xFF0D1C2D).copy(alpha = 0.90f)
                            )
                        )
                    )
                    .border(1.dp, GlassBorder)
                    .padding(16.dp)
            ) { content() }
        }

        AccentSide.TOP -> Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentColor)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF282A2E).copy(alpha = 0.85f),
                                Color(0xFF0D1C2D).copy(alpha = 0.90f)
                            )
                        )
                    )
                    .border(1.dp, GlassBorder)
                    .padding(16.dp)
            ) { content() }
        }
    }
}

@Composable
private fun StatusChip(text: String, dotColor: Color, showDot: Boolean, alpha: Float = 1f) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLow)
            .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showDot) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
            Text(
                text,
                color = OnSurface.copy(alpha = alpha),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SpinWheel(
    selected: SpinDir, onSelect: (SpinDir) -> Unit, modifier: Modifier = Modifier
) {
    val cells: List<SpinDir?> = listOf(
        SpinDir.NW, SpinDir.N, SpinDir.NE,
        SpinDir.W, null, SpinDir.E,
        SpinDir.SW, SpinDir.S, SpinDir.SE,
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (col in 0 until 3) {
                    val dir = cells[row * 3 + col]
                    if (dir == null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "●",
                                    color = Color(0xFF2A2A2A),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                    } else {
                        val isSel = dir == selected
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Secondary else SurfaceContainerLow)
                                .border(
                                    1.dp,
                                    if (isSel) Secondary else Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    onSelect(dir)
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                dir.icon,
                                dir.label,
                                tint = if (isSel) Color.White else OnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShotCard(shot: SeqShot, onRemove: () -> Unit) {
    val isFirst = shot.index == 1
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .border(
                1.dp,
                if (isFirst) Secondary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isFirst) Secondary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    "${shot.index}",
                    color = if (isFirst) Secondary else Color(0xFF64748B),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Zone ${shot.zone}",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        shot.spin.spinName,
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "Velocity: ${shot.velocity}m/s • Freq: ${shot.freq}",
                    color = Tertiary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Outlined.Close,
                    "Remove",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

//partilha
@Composable
private fun ShareRoutineDialog(
    routine: Routine,
    onDismiss: () -> Unit,
    onConfirm: (customTitle: String, description: String) -> Unit
) {
    // Título, que já é pré-preenchido
    var title by remember { mutableStateOf(routine.title) }
    // Descrição opcional
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1F2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Partilhar Rotina",
                color = OnSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Define como a rotina vai aparecer no feed da comunidade.",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                // Campo de título
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
                // Campo de descrição
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Introdução (opcional)", color = OnSurfaceVariant) },
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
            // Btn inativo se o titulo estiver vazio
            Button(
                onClick = { onConfirm(title.trim(), description.trim()) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Partilhar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = OnSurfaceVariant)
            }
        })
}

@Preview(showBackground = true, backgroundColor = 0xFF0A192F)
@Composable
fun RoutinePlannerPreview() {
    SpinNetTheme {
        RoutinePlannerScreen(
            currentDestination = SpinNetDestination.RoutinePlanner, onNavigate = {})
    }
}