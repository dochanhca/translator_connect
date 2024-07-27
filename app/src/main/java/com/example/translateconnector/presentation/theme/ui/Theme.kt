package com.example.translateconnector.presentation.theme.ui

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat

private val DarkColorScheme = darkColorScheme(
    primary = colorPrimary,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = colorPrimary,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TranslateConnectorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    val currentTypography = MaterialTheme.typography
    val customTypography = currentTypography.copy(
        labelSmall = currentTypography.labelSmall.copy(
            color = textGrey,
            fontSize = 11.sp
        ),
        labelMedium = currentTypography.labelMedium.copy(
            color = textGrey,
            fontSize = 12.sp
        ),
        labelLarge = currentTypography.labelLarge.copy(
            color = textGrey,
            fontSize = 14.sp
        ),
        titleMedium = currentTypography.titleMedium.copy(
            color = textGrey,
            fontSize = 16.sp
        ),
        titleLarge = currentTypography.titleLarge.copy(
            color = textGrey,
            fontSize = 18.sp
        ),
        headlineSmall = currentTypography.headlineSmall.copy(
            color = textGrey,
            fontSize = 24.sp
        ),

    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}