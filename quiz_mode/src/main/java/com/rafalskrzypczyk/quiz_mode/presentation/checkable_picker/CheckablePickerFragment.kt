package com.rafalskrzypczyk.quiz_mode.presentation.checkable_picker

import android.content.Context
import android.os.Bundle
import android.view.View
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentCheckablePickerBinding
import com.rafalskrzypczyk.quiz_mode.domain.CheckablePickerInteractor
import com.rafalskrzypczyk.quiz_mode.domain.models.Checkable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckablePickerFragment (
    private val parentInteractor: CheckablePickerInteractor
) : BaseBottomSheetFragment<FragmentCheckablePickerBinding>(
    FragmentCheckablePickerBinding::inflate
), CheckablePickerContract.View {
    @Inject
    lateinit var presenter: CheckablePickerContract.Presenter

    private lateinit var adapter: CheckablePickerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.onAttach(this)
        presenter.attachInteractor(parentInteractor)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
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

    override fun onDestroyView() {
        presenter.onDestroy()
        super.onDestroyView()
    }

    override fun displayData(items: List<Checkable>) {
        adapter.updateData(items)
    }

    override fun showLoading() {
    }

    override fun showError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }
}