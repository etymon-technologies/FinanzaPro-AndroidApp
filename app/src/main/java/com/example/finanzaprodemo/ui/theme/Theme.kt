package com.example.finanzaProDemo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import com.google.accompanist.systemuicontroller.rememberSystemUiController


private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background =  Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background =  Color.White


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

// Define multiple custom colors for Light and Dark Themes
private val LightCustomColors = CustomColors(
    headerSectionColor = StatusBarColor,
    headerControlBgColor = Color.White,
    rowSeparatorColor = Color.LightGray,
    rowBackgroundColor = Color.White,
    rowPrimaryTextColor = Color.Black,
    rowSecondaryTextColor= Color.Gray,
    iconTintColor= Color.Gray,
)

private val DarkCustomColors = CustomColors(
    headerSectionColor = StatusBarColor,
    headerControlBgColor = Color.White,
    rowSeparatorColor = Color.DarkGray,
    rowBackgroundColor = Color(0xFF242526),
    rowPrimaryTextColor = Color.LightGray,
    rowSecondaryTextColor= Color.Gray,
    iconTintColor= Color.LightGray
)

val LocalCustomColors = staticCompositionLocalOf { LightCustomColors }


@Composable
fun JetpackLearningTheme(
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
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    // Remember System UI Controller
    val systemUiController = rememberSystemUiController()

    // Set status bar color to match primary custom color
    SideEffect {
        systemUiController.setStatusBarColor(
            color = customColors.headerSectionColor
        )
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}