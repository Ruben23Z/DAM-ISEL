package dam_A51388.coolweatherapp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_A51388.coolweatherapp.R
import dam_A51388.coolweatherapp.data.WeatherData
import dam_A51388.coolweatherapp.data.getWeatherIconName
import java.time.LocalDateTime

@Composable
fun HourlyForecastCard(data: WeatherData) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Column {
            // Horizontal Chips
            LazyRow(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(data.hourly.time.take(12)) { index, time ->
                    val hour = LocalDateTime.parse(time).hour
                    val isCurrent = index == 0
                    Surface(
                        color = if (isCurrent) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        border = if (isCurrent) BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)) else null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "%02d".format(hour), fontSize = 11.sp)
                            val iconName = getWeatherIconName(data.hourly.weatherCodes[index], 1)
                            val iconRes = context.resources.getIdentifier(iconName, "drawable", context.packageName)
                            if (iconRes != 0) {
                                Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                            Text(text = "${data.hourly.temperatures[index].toInt()}°", fontSize = 13.sp, fontWeight = FontWeight.W500)
                            val pop = data.hourly.precipitationProbability[index]
                            if (pop > 0) {
                                Text(text = "$pop%", fontSize = 11.sp, color = Color(0xFF378ADD))
                            }
                        }
                    }
                }
            }

            // Temperature Graph
            Canvas(modifier = Modifier.fillMaxWidth().height(100.dp).padding(horizontal = 20.dp)) {
                val temps = data.hourly.temperatures.take(12)
                val minTemp = temps.minOrNull() ?: 0f
                val maxTemp = temps.maxOrNull() ?: 10f
                val range = (maxTemp - minTemp).coerceAtLeast(1f)
                
                val points = temps.mapIndexed { i, t ->
                    Offset(
                        x = i * (size.width / (temps.size - 1)),
                        y = size.height - (t - minTemp) / range * size.height
                    )
                }

                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        cubicTo(
                            (prev.x + curr.x) / 2, prev.y,
                            (prev.x + curr.x) / 2, curr.y,
                            curr.x, curr.y
                        )
                    }
                }
                
                drawPath(path, color = Color(0xFF378ADD), style = Stroke(width = 2.dp.toPx()))
                points.forEach { drawCircle(color = Color(0xFF378ADD), radius = 3.dp.toPx(), center = it) }
            }

            // Detail Table
            Column(modifier = Modifier.padding(14.dp)) {
                data.hourly.time.take(6).forEachIndexed { index, time ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hour = LocalDateTime.parse(time).hour
                        Text(text = "%02d:00".format(hour), modifier = Modifier.width(50.dp), fontSize = 12.sp, color = Color.Gray)
                        
                        val iconName = getWeatherIconName(data.hourly.weatherCodes[index], 1)
                        val iconRes = context.resources.getIdentifier(iconName, "drawable", context.packageName)
                        if (iconRes != 0) {
                            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                        
                        val weatherDescResId = context.resources.getIdentifier("wmo_${data.hourly.weatherCodes[index]}", "string", context.packageName)
                        Text(
                            text = if (weatherDescResId != 0) stringResource(weatherDescResId) else stringResource(R.string.sky_clear),
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(text = "${data.hourly.temperatures[index].toInt()}°", fontSize = 13.sp, fontWeight = FontWeight.W500)
                        
                        val pop = data.hourly.precipitationProbability[index]
                        Text(
                            text = if (pop > 0) "$pop%" else "",
                            modifier = Modifier.width(40.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End,
                            fontSize = 11.sp,
                            color = Color(0xFF378ADD)
                        )
                    }
                    if (index < 5) HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
                }
            }
        }
    }
}
