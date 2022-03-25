package ru.babushkinanatoly.surveyservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import kotlinx.coroutines.runBlocking
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyServiceTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val loggedIn = runBlocking {
                        (applicationContext as App).loggedIn()
                    }
                    SurveyServiceApp(loggedIn)
                }
            }
        }
    }
}
