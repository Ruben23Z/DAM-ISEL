package dam_A51388.coolweatherapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import kotlin.random.Random

@Composable
fun WeatherCard(
    data: WeatherData,
    onUpdateLocation: (Float, Float) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
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
                .height(IntrinsicSize.Min), // Allow content to expand
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 240.dp)) {
                Canvas(modifier = Modifier.matchParentSize()) {
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
                        .fillMaxWidth()
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

                    // Extracted CoordinatesCard
                    CoordinatesCard(
                        isVisible = isEditing,
                        initialLat = data.latitude,
                        initialLon = data.longitude,
                        onUpdateLocation = onUpdateLocation,
                        onClose = { isEditing = false }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

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
