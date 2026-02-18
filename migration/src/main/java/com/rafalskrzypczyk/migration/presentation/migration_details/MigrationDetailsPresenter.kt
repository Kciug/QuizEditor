package com.rafalskrzypczyk.migration.presentation.migration_details

import android.os.Bundle
import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.database_management.Database
import com.rafalskrzypczyk.core.database_management.DatabaseManager
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.migration.domain.MigrationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MigrationDetailsPresenter @Inject constructor(
    private val migrationRepository: MigrationRepository,
    private val databaseManager: DatabaseManager,
    private val userManager: UserManager,
    private val resourceProvider: ResourceProvider
) : BasePresenter<MigrationDetailsContract.View>(), MigrationDetailsContract.Presenter {

    private var mode: String = ""
    private var categoryId: Long = -1L
    private var sourceEnv: Database = Database.DEVELOPMENT
    private var targetEnv: Database = Database.PRODUCTION

    override fun getData(arguments: Bundle?) {
        mode = arguments?.getString("mode") ?: ""
        categoryId = arguments?.getLong("categoryId", -1L) ?: -1L
        sourceEnv = databaseManager.getDatabase()

        targetEnv = Database.entries.find { it != sourceEnv } ?: sourceEnv

        view.displaySourceEnvironment(sourceEnv.name)
        view.displayTargetEnvironment(targetEnv.name)

        loadPreview()
        loadHistory()
    }

    private fun loadPreview() {
        presenterScope?.launch {
            when (mode) {
                "main" -> {
                    view.displayModePreview("main", "Category Migration", "Transferring selected category and its questions")
                    view.displayItemCount(0)
                }
                "swipe" -> {
                    view.displayModePreview("swipe", "Swipe Mode Bulk Migration", "Transferring ALL swipe questions")
                }
                "translations" -> {
                    view.displayModePreview("translations", "Translations Bulk Migration", "Transferring ALL translations")
                }
                "cem" -> {
                    val preview = migrationRepository.getCemCategoryMigrationPreview(categoryId, sourceEnv)
                    if (preview is Response.Success) {
                        val (subs, ques) = preview.data
                        view.displayModePreview("cem", "CEM Category Tree Migration", "")
                        view.displayItemCount(1 + subs + ques)
                        
                        val details = "${resourceProvider.getString(com.rafalskrzypczyk.core.R.string.text_subcategories_count)} $subs, " +
                                     "${resourceProvider.getString(com.rafalskrzypczyk.core.R.string.text_questions_count)} $ques"
                        view.displayModePreview("cem", "CEM Category Tree", details)
                    }
                }
            }
        }
    }

    private fun loadHistory() {
        presenterScope?.launch {
            migrationRepository.getMigrationHistory(mode).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        view.displayMigrationHistory(response.data)
                    }
                    is Response.Error -> view.displayError(response.error)
                    is Response.Loading -> {}
                }
            }
        }
    }

    override fun onTargetEnvClicked() {
        val options = Database.entries.filter { it != sourceEnv }
        view.displayTargetPicker(options)
    }

    override fun onTargetEnvSelected(env: Database) {
        targetEnv = env
        view.displayTargetEnvironment(targetEnv.name)
    }

    override fun onMigrateClicked() {
        val user = userManager.getCurrentLoggedUser()
        if (user == null) {
            view.displayError("User not logged in")
            return
        }

        view.displayLoading()
        presenterScope?.launch {
            val result = when (mode) {
                "main" -> migrationRepository.migrateMainModeCategory(categoryId, sourceEnv, targetEnv, user.name)
                "swipe" -> migrationRepository.migrateSwipeMode(sourceEnv, targetEnv, user.name)
                "translations" -> migrationRepository.migrateTranslationsMode(sourceEnv, targetEnv, user.name)
                "cem" -> migrationRepository.migrateCemModeCategory(categoryId, sourceEnv, targetEnv, user.name)
                else -> Response.Error("Unknown mode")
            }

            view.hideLoading()
            when (result) {
                is Response.Success -> {
                    view.displayMigrationSuccess()
                    view.dismiss()
                }
                is Response.Error -> view.displayError(result.error)
                is Response.Loading -> {}
            }
        }
    }

    override fun onCancelClicked() {
        view.dismiss()
    }
}
