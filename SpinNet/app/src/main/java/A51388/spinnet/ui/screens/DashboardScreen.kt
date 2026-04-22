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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import A51388.spinnet.ui.components.GlassCard
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*

data class DrillItem(
    val name: String,
    val duration: String,
    val accuracy: Int,
    val tag: String
)

private val recentDrills = listOf(
    DrillItem("Heavy Topspin Loop", "45 min", 94, "FOREHAND"),
    DrillItem("Backspin Defense", "30 min", 81, "BACKHAND"),
    DrillItem("Pendulum Serve Drill", "20 min", 88, "SERVICE"),
)

@Composable
fun DashboardScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit
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
                        radius = 800f
                    )
                )
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header ─────────────────────────────────────────
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
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Welcome back, Alex",
                        color = OnSurface,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Performance ↑ 12% this week",
                        color = NeonGreen,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(VibrantPurple.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = Tertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Topspin Mastery Card ─────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT FOCUS",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Heavy Topspin\nMastery",
                            color = OnSurface,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicatorCard(progress = 0.73f, label = "73% complete")
                    }
                    Spacer(Modifier.width(16.dp))
                    ArcProgressRing(progress = 0.73f, size = 90.dp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Spin Focus Vectors Card ──────────────────────────
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

            // ── Recent Drills ────────────────────────────────────
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


// ── Sub-composables ──────────────────────────────────────────────────────────

@Composable
private fun LinearProgressIndicatorCard(progress: Float, label: String) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
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
        targetValue = progress,
        animationSpec = tween(1200),
        label = "arc"
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
                    ),
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = stroke
                )
            },
        contentAlignment = Alignment.Center
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
            targetValue = value,
            animationSpec = tween(900),
            label = label
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
        modifier = Modifier.fillMaxWidth(),
        innerPadding = 14.dp
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
                    ),
                contentAlignment = Alignment.Center
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
            currentDestination = SpinNetDestination.Dashboard,
            onNavigate = {}
        )
    }
}
