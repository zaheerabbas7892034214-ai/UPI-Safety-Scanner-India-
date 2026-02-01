package com.upisafety.scanner

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object History : Screen("history")
    object Paywall : Screen("paywall")
    object Settings : Screen("settings")
}
