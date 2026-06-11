package A51388.spinnet.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*

data class DrillItem(
    val name: String, val duration: String, val accuracy: Int, val tag: String
)




//DPS APAGAR
private val recentDrills = listOf(
    DrillItem("Heavy Topspin Loop", "45 min", 94, "FOREHAND"),
    DrillItem("Backspin Defense", "30 min", 81, "BACKHAND"),
    DrillItem("Pendulum Serve Drill", "20 min", 88, "SERVICE"),
)

@Composable
fun DashboardScreen(
    currentDestination: SpinNetDestination, onNavigate: (SpinNetDestination) -> Unit
) {
    val scrollState = rememberScrollState()
    var showRescheduleDialog by remember { mutableStateOf(false) }

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
                        text = "Welcome back, Alex",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your performance is up 12% this week.",
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
                                text = "14 DAYS",
                                color = Tertiary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACTIVE SESSION",
                                color = Tertiary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Heavy Topspin\nMastery",
                                color = OnSurface,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicatorCard(progress = 0.68f, label = "68% complete")
                        }
                        Spacer(Modifier.width(16.dp))
                        ArcProgressRing(progress = 0.68f, size = 90.dp)
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    Spacer(Modifier.height(10.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // RESUME button
                        Button(
                            onClick = { /* navigate to session */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Secondary, contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "RESUME",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { showRescheduleDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = OnSurface
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "RESCHEDULE",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (showRescheduleDialog) {
                RescheduleDialog(onDismiss = { showRescheduleDialog = false })
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ball Speed Card
                GlassCard(
                    modifier = Modifier.weight(1f), innerPadding = 16.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Speed,
                                contentDescription = "Speed",
                                tint = Secondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Secondary.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FASTEST",
                                    color = Secondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "PEAK BALL SPEED",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "118",
                                color = OnSurface,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "MPH",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f), innerPadding = 16.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Analytics,
                                contentDescription = "Analytics",
                                tint = NeonGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+4.2%",
                                    color = NeonGreen,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "SPIN CONSISTENCY",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "2,450",
                                color = OnSurface,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "RPM",
                                color = OnSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "SPIN FOCUS VECTORS",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    SpinVectorBars()
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "RECENT DRILLS",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            recentDrills.forEach { drill ->
                DrillCard(drill = drill)
                Spacer(Modifier.height(10.dp))
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
                // track
                drawArc(
                    color = SurfaceContainerHighest,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = stroke
                )
                // fill
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RescheduleDialog(onDismiss: () -> Unit) {
    val durationOptions = listOf("15 min", "30 min", "45 min", "60 min", "90 min")
    val intensityOptions = listOf("Light", "Moderate", "High", "Extreme")
    val dayOptions = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    var selectedDuration by remember { mutableStateOf("45 min") }
    var selectedIntensity by remember { mutableStateOf("High") }
    var selectedDays by remember { mutableStateOf(setOf("Mon", "Wed", "Fri")) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp), color = SurfaceContainerHigh, tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RESCHEDULE",
                            color = Tertiary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Heavy Topspin Mastery",
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Duration picker
                Text(
                    "SESSION DURATION",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    durationOptions.forEach { opt ->
                        val isSelected = opt == selectedDuration
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDuration = opt },
                            label = {
                                Text(opt, style = MaterialTheme.typography.labelSmall)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Secondary.copy(alpha = 0.2f),
                                selectedLabelColor = Secondary,
                                containerColor = SurfaceContainerHighest,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = Secondary.copy(alpha = 0.6f),
                                borderColor = OutlineVariant
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "INTENSITY",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    intensityOptions.forEach { opt ->
                        val isSelected = opt == selectedIntensity
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedIntensity = opt },
                            label = {
                                Text(opt, style = MaterialTheme.typography.labelSmall)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Tertiary.copy(alpha = 0.2f),
                                selectedLabelColor = Tertiary,
                                containerColor = SurfaceContainerHighest,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = Tertiary.copy(alpha = 0.6f),
                                borderColor = OutlineVariant
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "TRAINING DAYS",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dayOptions.forEach { day ->
                        val isSelected = day in selectedDays
                        val bgColor = if (isSelected) Secondary else SurfaceContainerHighest
                        val textColor = if (isSelected) Color.White else OnSurfaceVariant
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    selectedDays =
                                        if (isSelected) selectedDays - day else selectedDays + day
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.take(1),
                                color = textColor,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Text("CANCEL", style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary, contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "CONFIRM",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
