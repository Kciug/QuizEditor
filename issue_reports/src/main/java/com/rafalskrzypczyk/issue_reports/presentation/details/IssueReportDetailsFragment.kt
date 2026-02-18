package com.rafalskrzypczyk.issue_reports.presentation.details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.domain.models.GameMode
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.nav_handling.DrawerNavigationHandler
import com.rafalskrzypczyk.issue_reports.databinding.FragmentIssueReportDetailsBinding
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssueReportDetailsFragment :
    BaseBottomSheetFragment<FragmentIssueReportDetailsBinding, IssueReportDetailsContract.View, IssueReportDetailsContract.Presenter>(
        FragmentIssueReportDetailsBinding::inflate
    ), IssueReportDetailsContract.View {

    override fun onViewBound() {
        super.onViewBound()

        val reportId = arguments?.getString("reportId") ?: return
        presenter.getReportDetails(reportId)

        binding.buttonOpenQuestion.setOnClickListener {
            presenter.onOpenQuestionClicked()
        }
        
        binding.buttonCloseReport.setOnClickListener {
            showCloseReportDialog()
        }
    }

    override fun displayReportDetails(report: IssueReportUIModel) {
        with(binding) {
            textDescription.text = report.description
            textQuestionContent.text = report.questionContent
            textDate.text = report.dateString
            
            val result: Pair<Int, String> = when (report.gameMode) {
                GameMode.QUIZ -> com.rafalskrzypczyk.core.R.color.green to "QUIZ"
                GameMode.SWIPE -> com.rafalskrzypczyk.core.R.color.primary to "SWIPE"
                GameMode.TRANSLATION -> com.rafalskrzypczyk.core.R.color.orange_primary to "TRANSLATIONS"
                GameMode.CEM -> com.rafalskrzypczyk.core.R.color.purple_500 to "CEM"
            }
            labelGameMode.setColorAndText(requireContext().getColor(result.first), result.second)
        }
    }

    override fun navigateToQuestionEditor(gameMode: String, questionId: Long) {
        val navHandler = activity as? DrawerNavigationHandler
        val bundle = Bundle().apply {
            putLong("questionId", questionId)
        }
        
        val tag = when(gameMode) {
            "QUIZ_MODE" -> "nav_quiz_mode" // These tags should match what's in nav_graph or expected by navigateToDestinationByTag
            "SWIPE_MODE" -> "nav_swipe_quiz_mode"
            "TRANSLATIONS_MODE" -> "nav_translations_mode"
            "CEM_MODE" -> "nav_cem_mode"
            else -> null
        }
        
        tag?.let {
            navHandler?.navigateToDestinationByTag(it, bundle)
            // After returning from editor (handled by fragment lifecycle or simple check on resume)
            // But here we'll just show the dialog if the user returns to THIS fragment.
            // Since it's a BottomSheet, it might still be there.
        }
    }

    override fun onResume() {
        super.onResume()
        // Here we could check if we just returned from an editor.
        // For simplicity, let's assume if the user opens it and comes back, we ask.
    }

    override fun showCloseReportDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Zamknij zgłoszenie")
            .setMessage("Czy problem został rozwiązany i zgłoszenie można usunąć?")
            .setPositiveButton("Tak") { _, _ -> presenter.onCloseReportConfirmed() }
            .setNegativeButton("Nie", null)
            .show()
    }

    override fun displayLoading() {
        // Implement loading if needed
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    override fun closeDetails() {
        dismiss()
    }
}
