package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.quiz_mode.R
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentCheckablePickerBinding
import com.rafalskrzypczyk.quiz_mode.domain.CheckablePickerInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckablePickerFragment(
    private val parentInteractor: CheckablePickerInteractor,
) : BaseBottomSheetFragment<FragmentCheckablePickerBinding, CheckablePickerContract.View, CheckablePickerContract.Presenter>(
    FragmentCheckablePickerBinding::inflate
), CheckablePickerContract.View {
    private lateinit var adapter: CheckablePickerAdapter

    private var noElementsView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.attachInteractor(parentInteractor)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onViewBound() {
        super.onViewBound()

        adapter = CheckablePickerAdapter(
            onItemSelected = { presenter.onItemSelected(it) },
            onItemDeselected = { presenter.onItemDeselected(it) }
        )
        binding.recyclerView.adapter = adapter

        binding.searchBar.setOnTextChanged { presenter.onSearchQueryChanged(it) }
        binding.searchBar.setOnClearClick { presenter.onSearchQueryChanged("") }

        binding.buttonSubmit.setOnClickListener {
            dismiss()
        }
    }

    override fun displayTitle(title: String) {
        binding.labelTitle.text = title
    }

    override fun displayData(items: List<Checkable>) {
        adapter.updateData(items)
    }

    override fun displayNoItems(message: String) {
        if(noElementsView == null) {
            val stub = binding.stubNoItems
            noElementsView = stub.inflate()
        }

        val messageTextView = noElementsView?.findViewById<TextView>(R.id.message_no_items_to_display)
        messageTextView?.text = message
    }

    override fun displayLoading() {
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}