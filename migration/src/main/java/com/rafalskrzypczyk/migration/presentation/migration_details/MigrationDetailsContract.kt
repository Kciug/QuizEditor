package com.rafalskrzypczyk.migration.presentation.migration_details

import android.os.Bundle
import com.rafalskrzypczyk.core.base.BaseContract
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.migration.domain.models.MigrationRecord

interface MigrationDetailsContract {
    interface View : BaseContract.View {
        fun displaySourceEnvironment(env: String)
        fun displayTargetEnvironment(env: String)
        fun displayItemCount(count: Int)
        fun displayMigrationHistory(history: List<MigrationRecord>)
        fun displayModePreview(mode: String, title: String, details: String)
        fun displayProductionSyncInfo(lastTransferDate: String, needsUpdate: Boolean)
        fun displayMigrationSuccess()
        override fun displayLoading()
        fun hideLoading()
        override fun displayError(message: String)
        fun dismiss()
        fun displayTargetPicker(options: List<Database>)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun getData(arguments: Bundle?)
        fun onTargetEnvClicked()
        fun onTargetEnvSelected(env: Database)
        fun onMigrateClicked()
        fun onCancelClicked()
    }
}
