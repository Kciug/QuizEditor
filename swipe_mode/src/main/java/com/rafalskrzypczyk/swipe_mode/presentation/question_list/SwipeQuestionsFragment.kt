package com.rafalskrzypczyk.swipe_mode.presentation.question_list

import android.view.View
import androidx.core.view.isVisible
import com.rafalskrzypczyk.core.animations.QuizEditorAnimations
import com.rafalskrzypczyk.core.base.BaseFragment
import com.rafalskrzypczyk.core.databinding.FragmentListBinding
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeInvisible
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.sort_filter.SortAndFilterMenuBuilder
import com.rafalskrzypczyk.swipe_mode.presentation.question_list.ui_models.SwipeQuestionSimpleUIModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SwipeQuestionsFragment :
    BaseFragment<FragmentListBinding, SwipeQuestionsContract.View, SwipeQuestionsContract.Presenter>(
        FragmentListBinding::inflate
    ), SwipeQuestionsContract.View {

    private lateinit var adapter: SwipeQuestionsAdapter
    private lateinit var actionBarMenuBuilder: SortAndFilterMenuBuilder

    override fun onViewBound() {
        super.onViewBound()

        adapter = SwipeQuestionsAdapter(
            onCategoryClicked = { openQuestionDetailsSheet(it) },
            onCategoryRemoved = { presenter.removeCategory(it) }
        )
        binding.recyclerViewCategories.adapter = adapter
    }

    override fun displayQuestions(questions: List<SwipeQuestionSimpleUIModel>) {
        adapter.submitList(questions)
        if(binding.loading.root.isVisible)
            QuizEditorAnimations.animateReplaceScaleOutExpandFromTop(binding.loading.root, binding.recyclerViewCategories)
    }

    override fun displaySortMenu(
        sortOptions: List<SelectableMenuItem>,
        sortTypes: List<SelectableMenuItem>,
    ) {
        TODO("Not yet implemented")
    }

    override fun displayFilterMenu(filterOptions: List<SelectableMenuItem>) {
        TODO("Not yet implemented")
    }

    override fun displayNoElementsView() {
        val stub = binding.stubEmptyList
        val noElementsView = stub.inflate().apply { makeInvisible() }

        QuizEditorAnimations.animateReplaceScaleOutIn(binding.loading.root, noElementsView!!)

        val buttonAddNew = noElementsView.findViewById<View>(com.rafalskrzypczyk.core.R.id.button_add_new)
        buttonAddNew?.setOnClickListener { openNewQuestionSheet() }
    }

    private fun openQuestionDetailsSheet(categoryId: Long) {

    }

    private fun openNewQuestionSheet() {

    }

    override fun displayLoading() {
        binding.recyclerViewCategories.makeGone()
        binding.loading.root.makeVisible()
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}