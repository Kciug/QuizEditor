package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentCheckablePickerBinding
import com.rafalskrzypczyk.quiz_mode.domain.CheckablePickerInteractorContract
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable

class CheckablePickerFragment (
    private val parentInteractor: CheckablePickerInteractorContract
) : BaseBottomSheetFragment<FragmentCheckablePickerBinding>(
    FragmentCheckablePickerBinding::inflate
), CheckablePickerContract.View {
    lateinit var presenter: CheckablePickerContract.Presenter

    private lateinit var adapter: CheckablePickerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = CheckablePickerPresenter(this, parentInteractor)
        presenter.getItemList()
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

    override fun displayData(items: List<Checkable>) {
        adapter.updateData(items)
    }
}