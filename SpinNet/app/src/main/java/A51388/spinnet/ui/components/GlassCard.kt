package A51388.spinnet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import A51388.spinnet.ui.theme.GlassBorder
import A51388.spinnet.ui.theme.GlassFill
import A51388.spinnet.ui.theme.SurfaceContainerHigh

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    innerPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SurfaceContainerHigh.copy(alpha = 0.85f),
                        Color(0xFF0D1C2D).copy(alpha = 0.90f)
                    )
                )
            )
            .border(
                width = 1.dp, color = GlassBorder, shape = shape
            )
            .padding(innerPadding), content = content
    )
}
