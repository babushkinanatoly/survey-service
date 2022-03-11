package ru.babushkinanatoly.surveyservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme

class MainActivity : ComponentActivity() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope.launch {
            val loggedIn = (applicationContext as App).loggedIn.first()
            setContent {
                SurveyServiceTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        SurveyServiceApp(loggedIn)
                    }
                }
            }
        }
    }
}
