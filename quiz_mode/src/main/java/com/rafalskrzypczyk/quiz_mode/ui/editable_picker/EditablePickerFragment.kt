package com.rafalskrzypczyk.quiz_mode.ui.editable_picker

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.quiz_mode.databinding.FragmentEditablePickerBinding

class EditablePickerFragment(
    private val presenter: EditablePickerContract.Presenter,
    onDismiss: () -> Unit
) : BaseBottomSheetFragment<FragmentEditablePickerBinding>(
    FragmentEditablePickerBinding::inflate,
    onDismiss
), EditablePickerContract.View {
    private lateinit var adapter: EditablePickerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.setPickerView(this)
        presenter.getItemList()
    }

    override fun onViewBound() {
        super.onViewBound()

        adapter = EditablePickerAdapter(
            onItemSelected = { presenter.onItemSelected(it) },
            onItemDeselected = { presenter.onItemDeselected(it) }
        )
        binding.recyclerView.adapter = adapter

        binding.searchBar.addTextChangedListener(
            afterTextChanged = {
                presenter.onSearchQueryChanged(it.toString())
            }
        )
    }

    override fun displayData(items: List<Checkable>) {
        adapter.updateData(items)
    }
}