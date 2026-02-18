package com.rafalskrzypczyk.issue_reports.presentation.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.databinding.FragmentListBinding
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.issue_reports.presentation.details.IssueReportDetailsFragment
import com.rafalskrzypczyk.issue_reports.presentation.list.ui_models.IssueReportUIModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IssueReportsFragment :
    BaseFragment<FragmentListBinding, IssueReportsContract.View, IssueReportsContract.Presenter>(
        FragmentListBinding::inflate
    ), IssueReportsContract.View {

    private lateinit var adapter: IssueReportsAdapter
    private var noElementsView: View? = null

    override fun onViewBound() {
        super.onViewBound()

        adapter = IssueReportsAdapter(
            onItemClicked = { presenter.onReportClicked(it) }
        )

        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            
            searchBar.makeGone()
            popoverScrollUp.root.makeGone()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getIssueReports()
    }

    override fun displayIssueReports(reports: List<IssueReportUIModel>) {
        adapter.submitList(reports)

        if (binding.loading.root.isVisible) {
            QuizEditorAnimations.animateReplaceScaleOutExpandFromTop(binding.loading.root, binding.recyclerView)
        }
        if (noElementsView?.isVisible == true) {
            QuizEditorAnimations.animateReplaceScaleOutIn(noElementsView!!, binding.recyclerView)
        }
        binding.recyclerView.makeVisible()
    }

    override fun displayLoading() {
        binding.recyclerView.makeGone()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    override fun displayNoElementsView() {
        if (noElementsView == null) {
            noElementsView = binding.stubEmptyList.inflate().apply { makeInvisible() }
            noElementsView?.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)?.makeGone()
        }

        when {
            binding.loading.root.isVisible -> {
                QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, noElementsView!!)
            }
            binding.recyclerView.isVisible -> {
                QuizEditorAnimations.animateReplaceScaleOutIn(binding.recyclerView, noElementsView!!)
            }
            else -> {
                noElementsView?.makeVisible()
            }
        }
    }

    override fun displayElementsCount(count: Int) {
        // Option to display in search bar if needed
    }

    override fun navigateToDetails(reportId: String) {
        val bundle = Bundle().apply {
            putString("reportId", reportId)
        }
        val detailsFragment = IssueReportDetailsFragment().apply { arguments = bundle }
        detailsFragment.show(parentFragmentManager, "IssueReportDetailsBS")
    }
}
