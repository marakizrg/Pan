package com.example.pan.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pan.ui.screens.auth.LoginScreen
import com.example.pan.ui.screens.auth.RegisterScreen
import com.example.pan.ui.screens.classlocator.ClassLocatorScreen
import com.example.pan.ui.screens.dashboard.DashboardScreen
import com.example.pan.ui.screens.diplomapal.DiplomaPalScreen
import com.example.pan.ui.screens.notifications.NotificationsScreen
import com.example.pan.ui.screens.studyguide.StudyGuideScreen
import com.example.pan.viewmodel.AuthViewModel

@Composable
fun PanNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel            = authViewModel,
                onLoginSuccess       = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel         = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack    = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(onNavigateTo = { route -> navController.navigate(route) })
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.DiplomaHelper.route) {
            DiplomaPalScreen(
                onBack                 = { navController.popBackStack() },
                onNavigateToStudyGuide = { navController.navigate(Screen.StudyGuide.route) }
            )
        }

        composable(Screen.StudyGuide.route) {
            StudyGuideScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.ClassLocator.route) {
            ClassLocatorScreen(onBack = { navController.popBackStack() })
        }
    }
}
