package A51388.spinnet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import A51388.spinnet.ui.theme.*

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    it, contentDescription = null, tint = OnSurfaceVariant
                )
            }
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        isError = isError,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .border(1.dp, if (isError) Error else GlassBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonGreen,
            unfocusedBorderColor = Color.Transparent,
            focusedLabelColor = NeonGreen,
            unfocusedLabelColor = OnSurfaceVariant,
            cursorColor = NeonGreen,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            errorBorderColor = Error,
            errorLabelColor = Error,
        )
    )
}