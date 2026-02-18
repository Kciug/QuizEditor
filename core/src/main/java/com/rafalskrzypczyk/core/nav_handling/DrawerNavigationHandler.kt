package com.rafalskrzypczyk.core.nav_handling

import android.os.Bundle

interface DrawerNavigationHandler {
    fun navigateToTopLevelDestination(destination: Int)
    fun navigateToChat()
    fun navigateToDestinationByTag(tag: String, args: Bundle? = null)
    fun openQuestionDetails(gameMode: String, questionId: Long)
}