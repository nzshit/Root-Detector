package com.rootdetector.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun RootDetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color = Accents[0],
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> darkColorScheme(
            primary = accent, background = BgDark, surface = SurfDark,
            onPrimary = TextPrimary, onBackground = TextPrimary, onSurface = TextPrimary,
            secondary = Green, tertiary = Red, error = Red
        )
        else -> lightColorScheme(
            primary = accent, background = BgLight, surface = SurfLight,
            onPrimary = TextPrimary, onBackground = TextDark, onSurface = TextDark,
            secondary = Green, tertiary = Red, error = Red
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val w = (view.context as Activity).window
            WindowCompat.getInsetsController(w, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = scheme,
        shapes = androidx.compose.material3.Shapes(
            small = SmallShape, medium = CardShape, large = GaugeShape
        ),
        content = content
    )
}
