package ru.babushkinanatoly.base_feature.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.babushkinanatoly.base_feature.util.isDarkTheme
import ru.babushkinanatoly.core_api.Settings.AppTheme
import ru.babushkinanatoly.core_api.Settings.AppTheme.FOLLOW_SYSTEM

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun SurveyServiceTheme(
    appTheme: AppTheme = FOLLOW_SYSTEM,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = appTheme.isDarkTheme()
    val systemUiController = rememberSystemUiController()

    systemUiController.apply {
        setStatusBarColor(
            color = if (isDarkTheme) StatusBarDark else Color.Transparent,
            darkIcons = !isDarkTheme,
        )
        setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkTheme,
        )
    }
    MaterialTheme(
        colors = if (isDarkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
