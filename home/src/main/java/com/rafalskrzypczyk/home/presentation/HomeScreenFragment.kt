package com.rafalskrzypczyk.home.presentation

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreenFragment :
    BaseFragment<FragmentHomeScreenBinding, HomeScreenContract.View, HomeScreenContract.Presenter>(
        FragmentHomeScreenBinding::inflate
    ),
    HomeScreenContract.View {

    private var startWorkGuideView: View? = null
    private var activityDrawerNavigationHandler: DrawerNavigationHandler? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityDrawerNavigationHandler = activity as? DrawerNavigationHandler
    }

    override fun onViewBound() {
        super.onViewBound()

        binding.btnContinueWork.setOnClickListener { presenter.onContinueWork() }
        binding.notificationNewMessages.setOnClickListener { navigateToChat() }
    }

    override fun displayUserName(name: String) {
        binding.tvUserName.text = name
    }

    override fun setStartWorkGuide() {
        binding.btnContinueWork.makeGone()
        binding.tvContinueWorkMessage.makeGone()

        if(startWorkGuideView == null) {
            val startWorkGuideStub = binding.stubStartWorkGuide
            startWorkGuideView = startWorkGuideStub.inflate()
        }

        val welcomeMessageTV = startWorkGuideView?.findViewById<TextView>(R.id.tv_welcome_message)
        val startGuideTV = startWorkGuideView?.findViewById<TextView>(R.id.tv_guide_start)
        if(welcomeMessageTV != null && startGuideTV != null) buildWelcomeMessage(welcomeMessageTV, startGuideTV)

    }

    override fun navigateToDestination(destination: Int) {
        activityDrawerNavigationHandler?.navigateToTopLevelDestination(destination)
    }

    override fun displayNewMessagesNotification() {
        QuizEditorAnimations.animateScaleIn(binding.notificationNewMessages)
    }

    override fun hideNewMessagesNotification() {
        QuizEditorAnimations.animateScaleOut(binding.notificationNewMessages)
    }

    override fun displayLoading() {
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    private fun navigateToChat() {
        activityDrawerNavigationHandler?.navigateToChat()
    }

    private fun buildWelcomeMessage(welcomeMessageTV: TextView, startGuideTV: TextView) {
        val messageWelcome = getString(R.string.home_message_guide_part1)
        val messageRelatedApp = getString(R.string.home_message_related_app)
        val messageStartGuide = getString(R.string.home_message_guide_part2)
        val messageStartGuideContinuation = getString(R.string.home_message_guide_part3)
        val drawerIcon = ContextCompat.getDrawable(requireContext(), com.rafalskrzypczyk.core.R.drawable.ic_menu_24)
        val colorPrimary = ContextCompat.getColor(requireContext(), com.rafalskrzypczyk.core.R.color.primary)

        val spannableMessageWelcome = SpannableString("$messageWelcome $messageRelatedApp")
        spannableMessageWelcome.setSpan(
            StyleSpan(Typeface.BOLD),
            messageWelcome.length,
            spannableMessageWelcome.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableMessageWelcome.setSpan(
            ForegroundColorSpan(colorPrimary),
            messageWelcome.length,
            spannableMessageWelcome.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        drawerIcon?.let {
            it.setBounds(0, 0, drawerIcon.intrinsicWidth, drawerIcon.intrinsicHeight)
            it.setTint(colorPrimary)
        }
        val spanDrawerIcon = drawerIcon?.let { ImageSpan(it) }

        val textBuilder = SpannableStringBuilder()
        textBuilder.append("$messageStartGuide ")
        val imageStart = textBuilder.length
        textBuilder.append(" ")
        spanDrawerIcon?.let { textBuilder.setSpan(spanDrawerIcon, imageStart, textBuilder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
        textBuilder.append(" $messageStartGuideContinuation")

        welcomeMessageTV.text = spannableMessageWelcome
        startGuideTV.text = textBuilder
    }
}