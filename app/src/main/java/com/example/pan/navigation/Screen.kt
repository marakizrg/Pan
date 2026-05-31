package com.example.pan.navigation

sealed class Screen(val route: String) {
    data object Login        : Screen("login")
    data object Register     : Screen("register")
    data object Dashboard    : Screen("dashboard")
    data object Notifications: Screen("notifications")
    data object DiplomaHelper: Screen("diploma_helper")
    data object StudyGuide   : Screen("study_guide")
    data object ClassLocator         : Screen("class_locator")
    data object ClassLocatorTour     : Screen("class_locator_tour")
    data object ClassLocatorPatision : Screen("class_locator_patision")
    data object ClassroomScanner  : Screen("classroom_scanner")
    data object Profile           : Screen("profile")
}