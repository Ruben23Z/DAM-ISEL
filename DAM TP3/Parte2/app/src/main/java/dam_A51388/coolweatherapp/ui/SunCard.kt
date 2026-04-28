package dam_A51388.coolweatherapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_A51388.coolweatherapp.R
import dam_A51388.coolweatherapp.data.WeatherData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SunCard(data: WeatherData) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height)
                        quadraticTo(size.width / 2, -size.height / 2, size.width, size.height)
                    }
                    drawPath(
                        path,
                        color = Color.Gray.copy(alpha = 0.3f),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                    )
                    
                    // Sun position
                    val sunrise = LocalDateTime.parse(data.daily.sunrise[0])
                    val sunset = LocalDateTime.parse(data.daily.sunset[0])
                    val now = LocalDateTime.now()
                    
                    val totalMinutes = java.time.Duration.between(sunrise, sunset).toMinutes().toFloat()
                    val currentMinutes = java.time.Duration.between(sunrise, now).toMinutes().toFloat()
                    val progress = (currentMinutes / totalMinutes).coerceIn(0f, 1f)
                    
                    // Simple quadratic interpolation for the sun circle
                    val x = size.width * progress
                    val t = progress
                    val y = (1 - t) * (1 - t) * size.height + 2 * (1 - t) * t * (-size.height / 2) + t * t * size.height
                    
                    drawCircle(color = Color(0xFFEF9F27), radius = 8.dp.toPx(), center = Offset(x, y))
                    drawLine(color = Color.Gray.copy(alpha = 0.5f), start = Offset(0f, size.height), end = Offset(size.width, size.height), strokeWidth = 1.dp.toPx())
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SunStatCol(stringResource(R.string.nascer), LocalDateTime.parse(data.daily.sunrise[0]).format(DateTimeFormatter.ofPattern("HH:mm")))
                SunStatCol(stringResource(R.string.luz), "%.1f h".format(java.time.Duration.between(LocalDateTime.parse(data.daily.sunrise[0]), LocalDateTime.parse(data.daily.sunset[0])).toMinutes() / 60f))
                SunStatCol(stringResource(R.string.p_r), LocalDateTime.parse(data.daily.sunset[0]).format(DateTimeFormatter.ofPattern("HH:mm")))
            }
        }
    }
}

@Composable
fun SunStatCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.W500)
    }
}
