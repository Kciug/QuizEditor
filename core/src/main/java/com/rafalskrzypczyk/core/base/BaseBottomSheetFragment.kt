package com.rafalskrzypczyk.core.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VB: ViewBinding> (
    private val bindingInflater: (inflater: LayoutInflater) -> VB,
    private val onDismiss: () -> Unit = {}
) : BottomSheetDialogFragment() {
    companion object{
        const val HEIGHT_MODIFIER = 0.97f
    }

    private var _binding: VB? = null
    val binding: VB get() = _binding ?: throw IllegalStateException("Binding is accessed before onCreateView() or after onDestroyView()")

    private var _bottomSheet: View? = null
    val bottomSheet: View get() = _bottomSheet ?: throw IllegalStateException("BottomSheet is accessed before onViewCreated() or after onDestroyView()")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        setupBottomSheetDialog()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bottomSheet = null
        _binding = null
    }

    fun setupBottomSheetDialog(){
        bottomSheet.let {
            it.layoutParams.height = (resources.displayMetrics.heightPixels * HEIGHT_MODIFIER).toInt()

            val behavior = BottomSheetBehavior.from(it)
            behavior.skipCollapsed = true

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}