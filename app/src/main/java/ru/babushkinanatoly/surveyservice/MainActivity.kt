package ru.babushkinanatoly.surveyservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by (applicationContext as App).appTheme.collectAsState()
            val loggedIn = (applicationContext as App).loggedIn
            SurveyServiceTheme(darkTheme = theme ?: isSystemInDarkTheme()) {
                Surface(color = MaterialTheme.colors.background) {
                    SurveyServiceApp(loggedIn)
                }
            }
        }
    }
}
