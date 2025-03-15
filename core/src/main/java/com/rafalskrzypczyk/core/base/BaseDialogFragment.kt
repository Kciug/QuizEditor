package com.rafalskrzypczyk.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB
) : DialogFragment() {
    private var _binding: VB? = null

    /**
     * Gets the view binding for this dialog fragment.
     *
     * @throws IllegalStateException if accessed before `onCreateView()` or after `onDestroyView()`.
     */
    protected val binding: VB get() = _binding ?: throw
    IllegalStateException("Binding is accessed before onCreateView() or after onDestroyView()")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        onViewBound()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Called after the view binding is initialized in `onCreateView`.
     *
     * Subclasses can override this method to initialize view components.
     */
    protected open fun onViewBound() {
        // Optional: Subclasses can override this to perform actions after binding is set.
    }
}