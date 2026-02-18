package com.rafalskrzypczyk.migration.presentation.migration_details

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rafalskrzypczyk.core.base.BaseBottomSheetFragment
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.error_handling.ErrorDialog
import com.rafalskrzypczyk.core.extensions.makeGone
import com.rafalskrzypczyk.core.extensions.makeVisible
import com.rafalskrzypczyk.migration.R
import com.rafalskrzypczyk.migration.databinding.FragmentMigrationDetailsBinding
import com.rafalskrzypczyk.migration.domain.models.MigrationRecord
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MigrationDetailsBottomSheetFragment : BaseBottomSheetFragment<FragmentMigrationDetailsBinding, MigrationDetailsContract.View, MigrationDetailsContract.Presenter>(
    FragmentMigrationDetailsBinding::inflate
), MigrationDetailsContract.View {

    private lateinit var historyAdapter: MigrationHistoryAdapter

    override fun setupBottomSheetDialog() {
        super.setupBottomSheetDialog()
        bottomSheet.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getData(arguments)
    }

    override fun onViewBound() {
        super.onViewBound()
        historyAdapter = MigrationHistoryAdapter()
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())

        with(binding) {
            sectionNavbar.title.text = getString(com.rafalskrzypczyk.core.R.string.title_migration)
            sectionNavbar.buttonClose.setOnClickListener { presenter.onCancelClicked() }
            sectionNavbar.buttonSave.makeGone()

            btnTargetEnv.setOnClickListener { presenter.onTargetEnvClicked() }
            btnConfirmMigration.setOnClickListener { presenter.onMigrateClicked() }
        }
    }

    override fun displaySourceEnvironment(env: String) {
        binding.tvSourceEnv.text = env
    }

    override fun displayTargetEnvironment(env: String) {
        binding.tvTargetEnv.text = env
    }

    override fun displayItemCount(count: Int) {
        binding.tvItemsToMigrate.text = count.toString()
    }

    override fun displayMigrationHistory(history: List<MigrationRecord>) {
        historyAdapter.submitList(history)
        binding.tvEmptyHistory.isVisible = history.isEmpty()
    }

    override fun displayModePreview(mode: String, title: String, details: String) {
        binding.tvPreviewTitle.text = title
        binding.tvPreviewDetails.text = details
    }

    override fun displayMigrationSuccess() {
        Toast.makeText(requireContext(), "Migration successful", Toast.LENGTH_SHORT).show()
    }

    override fun displayLoading() {
        binding.loading.root.makeVisible()
        binding.btnConfirmMigration.isEnabled = false
    }

    override fun hideLoading() {
        binding.loading.root.makeGone()
        binding.btnConfirmMigration.isEnabled = true
    }

    override fun displayError(message: String) {
        ErrorDialog(requireContext(), message).show()
    }

    override fun dismiss() {
        super.dismiss()
    }

    override fun displayTargetPicker(options: List<Database>) {
        val popup = PopupMenu(requireContext(), binding.tvTargetEnv)
        options.forEachIndexed { index, database ->
            popup.menu.add(Menu.NONE, index, Menu.NONE, database.name)
        }
        popup.setOnMenuItemClickListener { item ->
            presenter.onTargetEnvSelected(options[item.itemId])
            true
        }
        popup.show()
    }
}
