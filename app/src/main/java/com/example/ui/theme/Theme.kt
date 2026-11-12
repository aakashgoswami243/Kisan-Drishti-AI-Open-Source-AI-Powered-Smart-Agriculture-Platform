package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = FreshLime,
    onPrimaryContainer = ForestGreenDark,
    secondary = ForestGreenDark,
    onSecondary = Color.White,
    secondaryContainer = FreshLimeLight,
    onSecondaryContainer = DarkText,
    tertiary = WarmAmber,
    onTertiary = Color.White,
    tertiaryContainer = AmberLight,
    onTertiaryContainer = DarkText,
    background = SoftCream,
    onBackground = DarkText,
    surface = SurfaceCard,
    onSurface = DarkText,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = LightText,
    outline = Color(0xFF869B8F),
    error = ErrorRed,
    errorContainer = ErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = FreshLime,
    onPrimary = ForestGreenDark,
    primaryContainer = ForestGreen,
    onPrimaryContainer = FreshLimeLight,
    secondary = FreshLimeLight,
    onSecondary = ForestGreenDark,
    secondaryContainer = ForestGreenDark,
    onSecondaryContainer = FreshLimeLight,
    tertiary = WarmAmber,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF6B4700),
    onTertiaryContainer = AmberLight,
    background = Color(0xFF131F19),
    onBackground = Color(0xFFE2EBE5),
    surface = Color(0xFF192A22),
    onSurface = Color(0xFFE2EBE5),
    surfaceVariant = Color(0xFF22352B),
    onSurfaceVariant = Color(0xFFB5C4BB),
    outline = Color(0xFF5E7569),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A)
)

@Composable
fun KisanDrishtiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for strong agritech identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
