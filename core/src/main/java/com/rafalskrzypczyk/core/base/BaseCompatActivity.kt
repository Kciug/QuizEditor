package com.rafalskrzypczyk.core.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseCompatActivity<VB: ViewBinding> (
    private val bindingInflater: (LayoutInflater) -> VB
) : AppCompatActivity() {
    private var _binding: VB? = null

    /**
     * Gets the view binding for this activity.
     *
     * @throws IllegalStateException if accessed before `onCreate` or after `onDestroy`.
     */
    protected val binding: VB get() = _binding ?: throw
    IllegalStateException("Binding is accessed before onCreate() or after onDestroy()")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)

//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
//            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            view.setPadding(0, systemInsets.top, 0, systemInsets.bottom)
//            insets
//        }

        setContentView(binding.root)
        onViewBound()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Called after the view binding is initialized in `onCreate`.
     *
     * Subclasses can override this method to perform actions that require the view binding.
     */
    protected open fun onViewBound() {
        // Optional: Subclasses can override this to perform actions after binding is set.
    }
}