package dam_A51388.coolweatherapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_A51388.coolweatherapp.data.WeatherData

@Composable
fun QuickStatsGrid(data: WeatherData) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                StatCard(
                    label = "Vento",
                    value = "${data.current.windSpeed} km/h",
                    subValue = getWindDirectionText(data.current.windDirection)
                ) {
                    Canvas(modifier = Modifier.size(40.dp)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        drawCircle(
                            color = Color.Gray.copy(alpha = 0.2f),
                            radius = size.width / 2,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        rotate(data.current.windDirection.toFloat(), center) {
                            val path = Path().apply {
                                moveTo(center.x, center.y - 15.dp.toPx())
                                lineTo(center.x - 5.dp.toPx(), center.y + 10.dp.toPx())
                                lineTo(center.x + 5.dp.toPx(), center.y + 10.dp.toPx())
                                close()
                            }
                            drawPath(path, color = Color(0xFF378ADD))
                        }
                    }
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                StatCard(
                    label = "Pressão",
                    value = "${data.current.surfacePressure.toInt()} hPa"
                ) {
                    val progress = ((data.current.surfacePressure - 980) / (1040 - 980)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = Color(0xFF378ADD),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                val uv = data.daily.uvIndexMax.firstOrNull() ?: 0f
                StatCard(
                    label = "Índice UV",
                    value = "%.1f".format(uv),
                    subValue = getUvClassification(uv)
                ) {
                    LinearProgressIndicator(
                        progress = { (uv / 12f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = Color(0xFFEF9F27),
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                StatCard(
                    label = "Condição",
                    value = getSkyCondition(data.current.weatherCode),
                    subValue = getVisibilityText(data.current.weatherCode)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    subValue: String = "",
    content: @Composable () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subValue.isNotEmpty()) {
                Text(
                    text = subValue,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Box(modifier = Modifier.padding(top = 8.dp)) {
                content()
            }
        }
    }
}

// Helpers
private fun getWindDirectionText(dir: Int): String {
    val directions = listOf("N", "NE", "L", "SE", "S", "SO", "O", "NO")
    val index = ((dir + 22.5) / 45).toInt() % 8
    return "${directions[index]} · $dir°"
}

private fun getUvClassification(uv: Float): String {
    return when {
        uv < 3 -> "Baixo"
        uv < 6 -> "Moderado"
        uv < 8 -> "Alto"
        uv < 11 -> "Muito alto"
        else -> "Extremo"
    }
}

private fun getSkyCondition(code: Int): String {
    return when (code) {
        0 -> "Limpo"
        1, 2 -> "Parcial"
        3 -> "Nublado"
        else -> "Encoberto"
    }
}

private fun getVisibilityText(code: Int): String {
    return when (code) {
        0, 1 -> "Visibilidade boa"
        2, 3 -> "Visibilidade média"
        else -> "Visibilidade reduzida"
    }
}
