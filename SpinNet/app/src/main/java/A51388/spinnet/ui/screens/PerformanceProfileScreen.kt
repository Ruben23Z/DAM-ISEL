package A51388.spinnet.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*


private data class RoutineEntry(
    val name: String,
    val duration: String,
    val reps: Int,
    val whenAgo: String,
    val accuracy: Int,
    val badge: String,
    val badgeColor: Color,
    val iconTint: Color
)

private val routineHistory = listOf(
    RoutineEntry(
        name = "High Intensity Forehand Drill",
        duration = "45 mins",
        reps = 280,
        whenAgo = "Yesterday",
        accuracy = 98,
        badge = "Personal Best",
        badgeColor = Secondary,       // red
        iconTint = Secondary
    ),
    RoutineEntry(
        name = "Backspin Defense Block",
        duration = "30 mins",
        reps = 150,
        whenAgo = "2 days ago",
        accuracy = 82,
        badge = "Steady Progress",
        badgeColor = Color(0xFF64748B),
        iconTint = Tertiary
    ),
    RoutineEntry(
        name = "Precision Service Routine",
        duration = "20 mins",
        reps = 80,
        whenAgo = "4 days ago",
        accuracy = 94,
        badge = "Gold Standard",
        badgeColor = Tertiary,        // blue accent
        iconTint = Tertiary
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
//  Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PerformanceProfileScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
) {
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

            // ── Header ──────────────────────────────────────────────────────
            Text(
                "PERFORMANCE CORE",
                color = Secondary,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Alex \"The Spinner\" Chen",
                color = OnSurface,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // ── KPI Bento Grid ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Win Rate
                KpiCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.EmojiEvents,
                    iconTint = Secondary,
                    topLabel = "+2.4% vs LW",
                    topLabelColor = OnSurfaceVariant,
                    value = "78%",
                    valueColor = OnSurface,
                    bottomLabel = "WIN RATE"
                )
                // Total Drills
                KpiCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.FitnessCenter,
                    iconTint = Tertiary,
                    topLabel = "LIFETIME REPS",
                    topLabelColor = OnSurfaceVariant,
                    value = "1,204",
                    valueColor = OnSurface,
                    bottomLabel = "TOTAL DRILLS"
                )
                // Day Streak
                KpiCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.LocalFireDepartment,
                    iconTint = Secondary,
                    topLabel = "ON FIRE",
                    topLabelColor = Secondary,
                    value = "12",
                    valueColor = Secondary,
                    bottomLabel = "DAY STREAK"
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Training Intensity Chart ─────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Training Intensity",
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Secondary)
                            )
                            Box(
                                Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Tertiary)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    IntensityBars()
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Spin Mastery ─────────────────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "Spin Mastery",
                        color = OnSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(14.dp))
                    SpinMasteryBars()
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Secondary, contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text(
                            "UPGRADE MASTERY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Routine History ──────────────────────────────────────────────
            Text(
                "Routine History",
                color = OnSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))
            routineHistory.forEach { entry ->
                RoutineHistoryCard(entry)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

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
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                value,
                color = valueColor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
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
private fun IntensityBars() {
    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val values = listOf(0.40f, 0.65f, 0.85f, 0.50f, 0.95f, 0.30f, 0.45f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.zip(values).forEachIndexed { idx, (day, value) ->
            val animated by animateFloatAsState(
                targetValue = value, animationSpec = tween(800 + idx * 80), label = day
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight(animated)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            if (value >= 0.9f) Brush.verticalGradient(
                                listOf(
                                    Secondary, Secondary.copy(alpha = 0.5f)
                                )
                            )
                            else Brush.verticalGradient(
                                listOf(
                                    Secondary.copy(alpha = 0.4f), Secondary.copy(alpha = 0.15f)
                                )
                            )
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    day,
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SpinMasteryBars() {
    val spins = listOf(
        Triple("Topspin Attack", 0.92f, Tertiary),
        Triple("Defensive Backspin", 0.68f, Tertiary),
        Triple("Lateral Side Spin", 0.45f, Tertiary),
    )
    spins.forEach { (label, value, accent) ->
        val animated by animateFloatAsState(
            targetValue = value, animationSpec = tween(900), label = label
        )
        Column(modifier = Modifier.padding(vertical = 5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = OnSurface, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${(value * 100).toInt()}%",
                    color = accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animated)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Tertiary.copy(alpha = 0.7f), Tertiary))
                        )
                )
            }
        }
    }
}

@Composable
private fun RoutineHistoryCard(entry: RoutineEntry) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon square
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(entry.iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Bolt,
                    null,
                    tint = entry.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.name,
                    color = OnSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${entry.duration} • ${entry.reps} reps • ${entry.whenAgo}",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${entry.accuracy}% Accuracy",
                    color = OnSurface,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    entry.badge,
                    color = entry.badgeColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111317)
@Composable
fun PerformanceProfilePreview() {
    SpinNetTheme {
        PerformanceProfileScreen(
            currentDestination = SpinNetDestination.Performance, onNavigate = {})
    }
}
