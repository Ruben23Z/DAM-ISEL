package dam_A51388.coolweatherapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam_A51388.coolweatherapp.data.WeatherData
import dam_A51388.coolweatherapp.data.getWeatherIconName
import dam_A51388.coolweatherapp.data.wmoLabels
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun HeroCard(
    data: WeatherData,
    onUpdateLocation: (Float, Float) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var latText by remember { mutableStateOf(data.latitude.toString()) }
    var lonText by remember { mutableStateOf(data.longitude.toString()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val random = Random(42)
                    repeat(18) {
                        val x = random.nextFloat() * size.width
                        val y = random.nextFloat() * size.height
                        val radius = (2 + random.nextFloat() * 4).dp.toPx()
                        val alpha = 0.08f + random.nextFloat() * 0.3f
                        drawCircle(
                            color = Color.White.copy(alpha = alpha),
                            radius = radius,
                            center = Offset(x, y)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = data.timezone,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { isEditing = !isEditing }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    AnimatedVisibility(visible = isEditing) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = latText,
                                    onValueChange = { latText = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Lat") },
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = lonText,
                                    onValueChange = { lonText = it },
                                    modifier = Modifier.weight(1f),
                                    label = { Text("Lon") },
                                    singleLine = true
                                )
                            }
                            Button(
                                onClick = {
                                    val lat = latText.toFloatOrNull()
                                    val lon = lonText.toFloatOrNull()
                                    if (lat != null && lon != null) {
                                        onUpdateLocation(lat, lon)
                                        isEditing = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Atualizar localização")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    val iconName = getWeatherIconName(data.current.weatherCode, data.current.isDay)
                    val iconRes = context.resources.getIdentifier(iconName, "drawable", context.packageName)
                    if (iconRes != 0) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp).padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("pt", "PT"))
            val dateStr = LocalDateTime.parse(data.current.time).format(formatter)

            Text(text = dateStr, fontSize = 12.sp, color = Color.Gray)

            Text(
                text = "${data.current.temperature.toInt()}°",
                fontSize = 64.sp,
                fontWeight = FontWeight.W500,
                letterSpacing = (-2).sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = wmoLabels[data.current.weatherCode] ?: "Desconhecido",
                fontSize = 15.sp,
                color = Color.Gray
            )

            Text(
                text = "Sensação térmica ${data.current.apparentTemperature.toInt()}°",
                fontSize = 13.sp,
                color = Color.Gray
            )
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

                        Text(
                            text = wmoLabels[data.hourly.weatherCodes[index]] ?: "Céu Limpo",
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

@Composable
fun SunCard(data: WeatherData) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
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
                SunStatCol("Nascer", LocalDateTime.parse(data.daily.sunrise[0]).format(DateTimeFormatter.ofPattern("HH:mm")))
                SunStatCol("Luz", "%.1f h".format(java.time.Duration.between(LocalDateTime.parse(data.daily.sunrise[0]), LocalDateTime.parse(data.daily.sunset[0])).toMinutes() / 60f))
                SunStatCol("Pôr", LocalDateTime.parse(data.daily.sunset[0]).format(DateTimeFormatter.ofPattern("HH:mm")))
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
