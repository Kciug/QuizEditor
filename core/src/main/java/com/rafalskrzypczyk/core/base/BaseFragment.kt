package com.rafalskrzypczyk.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.rafalskrzypczyk.core.app_bar_handler.ActionBarBuilder
import javax.inject.Inject


abstract class BaseFragment<VB: ViewBinding, V : BaseContract.View, P : BaseContract.Presenter<V>> (
    private val bindingInflater: (LayoutInflater) -> VB
) : Fragment() {

    @Inject
    lateinit var presenter: P

    private var _binding: VB? = null

    /**
     * Gets the view binding for this fragment.
     *
     * @throws IllegalStateException if accessed before `onCreateView()` or after `onDestroyView()`.
     */
    protected val binding: VB get() = _binding ?: throw
    IllegalStateException("Binding is accessed before onCreateView() or after onDestroyView()")

    protected var activityActionBarBuilder: ActionBarBuilder? = null
    protected var actionMenuRes: Int? = null
    protected var actionMenuCallback: ((MenuItem) -> Boolean)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        activityActionBarBuilder = requireActivity() as? ActionBarBuilder
        onViewBound()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        presenter.onAttachView(this as V)
        presenter.onViewCreated()

        activityActionBarBuilder?.setupActionBarMenu(actionMenuRes, actionMenuCallback)
    }

    override fun onDestroyView() {
        presenter.onDestroy()
        _binding = null
        super.onDestroyView()
    }

    /**
     * Called after the view binding is initialized in `onCreateView`.
     *
     * Subclasses can override this method to initialize view components.
     * Use this function to set actionMenuRes and actionMenuCallback.
     */
    protected open fun onViewBound() {}
}