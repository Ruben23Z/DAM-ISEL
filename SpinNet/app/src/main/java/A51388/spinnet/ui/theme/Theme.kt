package A51388.spinnet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SpinNetDarkColorScheme = darkColorScheme(
    primary             = Primary,
    onPrimary           = OnPrimary,
    primaryContainer    = PrimaryContainer,
    onPrimaryContainer  = OnPrimaryContainer,
    inversePrimary      = InversePrimary,
    secondary           = Secondary,
    onSecondary         = OnSecondary,
    secondaryContainer  = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary            = Tertiary,
    onTertiary          = OnTertiary,
    tertiaryContainer   = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error               = Error,
    onError             = OnError,
    errorContainer      = ErrorContainer,
    onErrorContainer    = OnErrorContainer,
    background          = Surface,
    onBackground        = OnSurface,
    surface             = Surface,
    onSurface           = OnSurface,
    onSurfaceVariant    = OnSurfaceVariant,
    surfaceVariant      = SurfaceVariant,
    inverseSurface      = InverseSurface,
    inverseOnSurface    = InverseOnSurface,
    outline             = Outline,
    outlineVariant      = OutlineVariant,
    surfaceTint         = SurfaceTint,
)

@Composable
fun SpinNetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpinNetDarkColorScheme,
        typography  = SpinNetTypography,
        content     = content
    )
}
