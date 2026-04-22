package A51388.spinnet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
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
import A51388.spinnet.ui.components.NeonButton
import A51388.spinnet.ui.components.SpinNetBottomBar
import A51388.spinnet.ui.navigation.SpinNetDestination
import A51388.spinnet.ui.theme.*

private val GRID_COLS = 5
private val GRID_ROWS = 4
private val TOTAL_ZONES = GRID_COLS * GRID_ROWS

@Composable
fun RoutinePlannerScreen(
    currentDestination: SpinNetDestination,
    onNavigate: (SpinNetDestination) -> Unit,
) {
    var selectedZones by remember { mutableStateOf(setOf<Int>()) }
    var sequence by remember { mutableStateOf(listOf<Int>()) }
    var topSpinSlider by remember { mutableFloatStateOf(0.85f) }
    var backSpinSlider by remember { mutableFloatStateOf(0.40f) }
    var sideSpinSlider by remember { mutableFloatStateOf(0.65f) }

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
                        colors = listOf(Color(0xFF1A103E), Surface),
                        radius = 900f
                    )
                )
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header ───────────────────────────────────────────
            Text(
                text = "SPINNET",
                color = NeonGreen,
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Routine Planner",
                color = OnSurface,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Map zones and spin vectors for your session",
                color = OnSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(24.dp))

            // ── Target Grid ──────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "TARGET GRID",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    // Table tennis table net divider label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "← OPPONENT SIDE →",
                            color = OnSurfaceVariant.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.height(6.dp))

                    for (row in 0 until GRID_ROWS) {
                        if (row == GRID_ROWS / 2) {
                            // Net divider
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = NeonGreen.copy(alpha = 0.4f),
                                thickness = 1.5.dp
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (col in 0 until GRID_COLS) {
                                val zoneIdx = row * GRID_COLS + col
                                val zoneNum = zoneIdx + 1
                                val isSelected = zoneIdx in selectedZones
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSelected) NeonGreen.copy(alpha = 0.25f)
                                            else SurfaceContainerHighest.copy(alpha = 0.5f)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) NeonGreen else GlassBorder,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            selectedZones = if (isSelected)
                                                selectedZones - zoneIdx
                                            else
                                                selectedZones + zoneIdx
                                            if (!isSelected) {
                                                sequence = sequence + zoneNum
                                            } else {
                                                sequence = sequence.filter { it != zoneNum }
                                            }
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$zoneNum",
                                        color = if (isSelected) NeonGreen else OnSurfaceVariant,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "← YOUR SIDE →",
                            color = NeonGreen.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Spin Dynamics ────────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "SPIN DYNAMICS",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    SpinSlider("TOP SPIN",  topSpinSlider,  NeonGreen) { topSpinSlider = it }
                    SpinSlider("BACK SPIN", backSpinSlider, VibrantPurple) { backSpinSlider = it }
                    SpinSlider("SIDE SPIN", sideSpinSlider, Tertiary) { sideSpinSlider = it }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Current Sequence ─────────────────────────────────
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CURRENT SEQUENCE",
                            color = OnSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (sequence.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    sequence = emptyList()
                                    selectedZones = emptySet()
                                }
                            ) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("CLEAR", color = OnSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    if (sequence.isEmpty()) {
                        Text(
                            text = "Tap zones on the grid to build your sequence",
                            color = OnSurfaceVariant.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sequence.forEach { zone ->
                                ZoneChip(zone)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    NeonButton(
                        onClick = { /* start session */ },
                        enabled = sequence.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("START SESSION", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SpinSlider(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
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
            modifier = Modifier.width(72.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = SurfaceContainerHighest
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${(value * 100).toInt()}%",
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(34.dp)
        )
    }
}

@Composable
private fun ZoneChip(zone: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NeonGreen.copy(alpha = 0.15f))
            .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "Z$zone",
            color = NeonGreen,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF051424)
@Composable
fun RoutinePlannerPreview() {
    SpinNetTheme {
        RoutinePlannerScreen(
            currentDestination = SpinNetDestination.RoutinePlanner,
            onNavigate = {}
        )
    }
}
