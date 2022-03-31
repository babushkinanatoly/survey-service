package ru.babushkinanatoly.feature_auth

import android.content.res.Configuration
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.babushkinanatoly.base_feature.AppNavigation.Screen.AuthWorkflow
import ru.babushkinanatoly.base_feature.theme.SurveyServiceTheme
import ru.babushkinanatoly.feature_auth.login.LogInScreen
import ru.babushkinanatoly.feature_auth.login.di.LogInViewModel
import ru.babushkinanatoly.feature_auth.signup.SignUpScreen
import ru.babushkinanatoly.feature_auth.signup.di.SignUpViewModel

@Composable
fun AuthWorkflow(
    onAuthSuccess: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = AuthWorkflow.LogIn.route
    ) {
        composable(AuthWorkflow.LogIn.route) {
            LogInScreen(
                logInModel = viewModel<LogInViewModel>().logInComponent.provideModel(),
                onSignUp = {
                    navController.navigate(AuthWorkflow.SignUp.route) {
                        popUpTo(AuthWorkflow.LogIn.route) {
                            inclusive = true
                        }
                    }
                },
                onLogInSuccess = onAuthSuccess
            )
        }
        composable(AuthWorkflow.SignUp.route) {
            SignUpScreen(
                signUpModel = viewModel<SignUpViewModel>().signUpComponent.provideModel(),
                onLogIn = {
                    navController.navigate(AuthWorkflow.LogIn.route) {
                        popUpTo(AuthWorkflow.SignUp.route) {
                            inclusive = true
                        }
                    }
                },
                onSignUpSuccess = onAuthSuccess
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "AuthWorkflowPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun AuthWorkflowPreview() {
    SurveyServiceTheme {
        Scaffold {
            AuthWorkflow {}
        }
    }
}
