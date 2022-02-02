package ru.babushkinanatoly.surveyservice.ui.nav

import androidx.annotation.StringRes
import ru.babushkinanatoly.surveyservice.R
import ru.babushkinanatoly.surveyservice.ui.nav.AppNavigation.Screen.NavWorkflow

open class AppNavigation(val route: String, @StringRes val resId: Int) {

    companion object {
        val bottomNavItems = listOf(
            NavWorkflow.SurveyFeedWorkflow,
            NavWorkflow.UserSurveysWorkflow,
            NavWorkflow.ProfileWorkflow
        )
    }

    private object ScreenRoutes {
        const val auth = "auth"
        const val newSurvey = "newsurvey"
        const val settings = "settings"
        const val navWorkflow = "navworkflow"
        const val surveyFeedWorkflow = "surveyfeedworkflow"
        const val surveyFeed = "surveyfeed"
        const val surveyDetails = "surveydetails"
        const val userSurveysWorkflow = "usersurveysworkflow"
        const val userSurveys = "usersurveys"
        const val userSurveyDetails = "usersurveydetails"
        const val profileWorkflow = "profileworkflow"
        const val profile = "profile"
        const val statistics = "statistics"
    }

    sealed class Screen(screenRoute: String, screenResId: Int) : AppNavigation(screenRoute, screenResId) {
        object Auth : Screen(ScreenRoutes.auth, R.string.auth)
        object NewSurvey : Screen(ScreenRoutes.newSurvey, R.string.new_survey)
        object Settings : Screen(ScreenRoutes.settings, R.string.settings)

        object NavWorkflow : Screen(ScreenRoutes.navWorkflow, R.string.navigation) {
            object SurveyFeedWorkflow : Screen(ScreenRoutes.surveyFeedWorkflow, R.string.survey_feed) {
                object SurveyFeed : Screen(ScreenRoutes.surveyFeed, R.string.survey_feed)
                object SurveyDetails : Screen(ScreenRoutes.surveyDetails, R.string.survey_details)
            }

            object UserSurveysWorkflow : Screen(ScreenRoutes.userSurveysWorkflow, R.string.user_surveys) {
                object UserSurveys : Screen(ScreenRoutes.userSurveys, R.string.user_surveys)
                object UserSurveyDetails : Screen(ScreenRoutes.userSurveyDetails, R.string.user_survey_details)
            }

            object ProfileWorkflow : Screen(ScreenRoutes.profileWorkflow, R.string.profile) {
                object Profile : Screen(ScreenRoutes.profile, R.string.profile)
                object Statistics : Screen(ScreenRoutes.statistics, R.string.statistics)
            }
        }
    }
}
