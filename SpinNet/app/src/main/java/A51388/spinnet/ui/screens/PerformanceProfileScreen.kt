package A51388.spinnet.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
    val when_: String,
    val accuracy: Int,
    val badge: String,
    val badgeColor: Color
)

private val routineHistory = listOf(
    RoutineEntry("High Intensity Forehand Drill", "45 mins", 280, "Yesterday",  98, "Personal Best",   Color(0xFFD9FF00)),
    RoutineEntry("Backspin Defense Block",         "30 mins", 150, "2 days ago", 82, "Steady Progress", Color(0xFF8B5CF6)),
    RoutineEntry("Precision Service Routine",      "20 mins",  80, "4 days ago", 94, "Gold Standard",   Color(0xFFD9FF00)),
)

@Composable
fun PerformanceProfileScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            SpinNetBottomBar(
                currentDestination = currentDestination,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A3E), Surface),
                        radius = 900f
                    )
                )
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header label ────────────────────────────────────
            Text(
                text = "SPINNET",
                color = NeonGreen,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 3.sp
            )
            Text(
                text = "Performance Core",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(20.dp))

            // ── Player profile strip ─────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(VibrantPurple.copy(alpha = 0.6f), PrimaryContainer)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AC", color = NeonGreen, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Alex \"The Spinner\" Chen",
                            color = OnSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Elite Rank • Season 4", color = OnSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Stat chips row ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(modifier = Modifier.weight(1f), value = "78%",   label = "WIN RATE",    accent = NeonGreen)
                StatChip(modifier = Modifier.weight(1f), value = "1,204", label = "TOTAL DRILLS", accent = VibrantPurple)
                StatChip(modifier = Modifier.weight(1f), value = "12🔥",   label = "DAY STREAK",  accent = Tertiary)
            }

            Spacer(Modifier.height(16.dp))

            // ── Training Intensity ───────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("TRAINING INTENSITY", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(14.dp))
                    IntensityBars()
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Spin Mastery ─────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("SPIN MASTERY", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(12.dp))
                    SpinMasteryBars()
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Routine History ──────────────────────────────────
            Text("ROUTINE HISTORY", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(10.dp))
            routineHistory.forEach { entry ->
                RoutineHistoryCard(entry)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    accent: Color
) {
    GlassCard(modifier = modifier, innerPadding = 12.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = accent,     style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(label, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun IntensityBars() {
    // 7 weekly bars (Mon–Sun)
    val days   = listOf("M", "T", "W", "T", "F", "S", "S")
    val values = listOf(0.6f, 0.9f, 0.4f, 1.0f, 0.75f, 0.5f, 0.3f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.zip(values).forEachIndexed { idx, (day, value) ->
            val animated by animateFloatAsState(
                targetValue = value,
                animationSpec = tween(800 + idx * 80),
                label = day
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height((animated * 80).dp)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(NeonGreen, VibrantPurple)
                            )
                        )
                )
                Spacer(Modifier.height(4.dp))
                Text(day, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun SpinMasteryBars() {
    val spins = listOf(
        "TOPSPIN"   to 0.92f,
        "BACKSPIN"  to 0.78f,
        "SIDESPIN"  to 0.65f,
        "FLAT"      to 0.50f,
    )
    spins.forEach { (label, value) ->
        val animated by animateFloatAsState(targetValue = value, animationSpec = tween(900), label = label)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(72.dp))
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SurfaceContainerHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animated)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(VibrantPurple, NeonGreen)))
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                "${(value * 100).toInt()}%",
                color = NeonGreen,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp)
            )
        }
    }
}

@Composable
private fun RoutineHistoryCard(entry: RoutineEntry) {
    GlassCard(modifier = Modifier.fillMaxWidth(), innerPadding = 14.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.name, color = OnSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${entry.duration} • ${entry.reps} reps • ${entry.when_}",
                    color = OnSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(entry.badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(entry.badge, color = entry.badgeColor, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${entry.accuracy}%",
                    color = if (entry.accuracy >= 90) NeonGreen else Tertiary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("accuracy", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF051424)
@Composable
fun PerformanceProfilePreview() {
    SpinNetTheme {
        PerformanceProfileScreen(
            currentDestination = SpinNetDestination.Performance,
            onNavigate = {}
        )
    }
}
