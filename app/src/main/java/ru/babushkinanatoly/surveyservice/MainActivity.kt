package ru.babushkinanatoly.surveyservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val appTheme by (applicationContext as App).appTheme.collectAsState()
            SurveyServiceTheme(appTheme) {
                Surface {
                    SurveyServiceApp()
                }
            }
        }
    }
}
