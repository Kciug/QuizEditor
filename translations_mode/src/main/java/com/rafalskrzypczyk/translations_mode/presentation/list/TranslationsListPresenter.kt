package com.rafalskrzypczyk.translations_mode.presentation.list

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.core.base.BasePresenter
import com.rafalskrzypczyk.core.database_management.DatabaseEventBus
import com.rafalskrzypczyk.core.sort_filter.SelectableMenuItem
import com.rafalskrzypczyk.core.utils.Constants
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.translations_mode.domain.TranslationQuestion
import com.rafalskrzypczyk.translations_mode.domain.TranslationsRepository
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsFilters
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsFilters.Companion.toFilterOption
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsFilters.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsSort
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsSort.Companion.toSelectableMenuItem
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsSort.Companion.toSortOption
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.TranslationQuestionsSort.Companion.toSortType
import com.rafalskrzypczyk.translations_mode.presentation.list.ui_models.toUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class TranslationsListPresenter @Inject constructor(
    private val repository: TranslationsRepository,
    private val resourceProvider: ResourceProvider
) : BasePresenter<TranslationsListContract.View>(), TranslationsListContract.Presenter {
    private val questionsData = MutableStateFlow<List<TranslationQuestion>>(emptyList())
    private val searchQuery = MutableStateFlow("")
    private val sortOption = MutableStateFlow<TranslationQuestionsSort.SortOptions>(TranslationQuestionsSort.defaultSortOption)
    private val sortType = MutableStateFlow<TranslationQuestionsSort.SortTypes>(TranslationQuestionsSort.defaultSortType)
    private val filterType = MutableStateFlow<TranslationQuestionsFilters>(TranslationQuestionsFilters.defaultFilter)

    override fun onViewCreated() {
        super.onViewCreated()
        getData()
        observeDatabaseEvent()
    }

    private fun getData() {
        presenterScope?.launch {
            delay(Constants.PRESENTER_INITIAL_DELAY)
            repository.getAllQuestions().collectLatest {
                when (it) {
                    is Response.Success -> {
                        questionsData.value = it.data
                        observeDataChanges()
                        displayData()
                    }
                    is Response.Error -> view.displayError(it.error)
                    is Response.Loading -> view.displayLoading()
                }
            }
        }
    }

    private fun displayData() {
        presenterScope?.launch {
            combine(
                questionsData,
                searchQuery,
                sortOption,
                sortType,
                filterType
            ) { questions, query, sortOption, sortType, filter ->
                var filtered = questions.filter { it.phrase.contains(query, ignoreCase = true) }
                filtered = filterData(filtered, filter)
                filtered = sortData(filtered, sortOption, sortType)
                filtered
            }.collectLatest {
                if (questionsData.value.isEmpty()) view.displayNoElementsView()
                else view.displayQuestions(it.map { it.toUIModel(resourceProvider) })
                view.displayElementsCount(it.size)
            }
        }
    }

    private fun sortData(
        data: List<TranslationQuestion>,
        sortOption: TranslationQuestionsSort.SortOptions,
        sortType: TranslationQuestionsSort.SortTypes
    ): List<TranslationQuestion> {
        val sorted = when (sortOption) {
            TranslationQuestionsSort.SortOptions.ByDate -> data.sortedByDescending { it.dateCreated }
            TranslationQuestionsSort.SortOptions.ByPhrase -> data.sortedBy { it.phrase.lowercase() }
            TranslationQuestionsSort.SortOptions.ByTranslationCount -> data.sortedBy { it.translations.size }
        }
        return if (sortType == TranslationQuestionsSort.SortTypes.Descending) sorted.reversed() else sorted
    }

    private fun filterData(data: List<TranslationQuestion>, filter: TranslationQuestionsFilters): List<TranslationQuestion> {
        return when (filter) {
            TranslationQuestionsFilters.None -> data
            TranslationQuestionsFilters.WithTranslations -> data.filter { it.translations.isNotEmpty() }
            TranslationQuestionsFilters.WithoutTranslations -> data.filter { it.translations.isEmpty() }
        }
    }

    private fun observeDataChanges() {
        presenterScope?.launch {
            repository.getUpdatedQuestions().collectLatest { questionsData.value = it }
        }
    }

    private fun observeDatabaseEvent() {
        presenterScope?.launch {
            DatabaseEventBus.eventReloadData.collectLatest {
                getData()
            }
        }
    }

    override fun removeQuestion(questionId: Long) {
        presenterScope?.launch {
            val response = repository.deleteQuestion(questionId)
            if (response is Response.Error) view.displayError(response.error)
        }
    }

    override fun searchBy(query: String) {
        searchQuery.value = query
    }

    override fun onSortMenuOpened() {
        view.displaySortMenu(
            sortOptions = TranslationQuestionsSort.getSortOptions().map { it.toSelectableMenuItem(sortOption.value == it) },
            sortTypes = TranslationQuestionsSort.getSortTypes().map { it.toSelectableMenuItem(sortType.value == it) }
        )
    }

    override fun onFilterMenuOpened() {
        view.displayFilterMenu(
            filterOptions = TranslationQuestionsFilters.getFilters().map { it.toSelectableMenuItem(filterType.value == it) }
        )
    }

    override fun sortByOption(sort: SelectableMenuItem) {
        sortOption.value = sort.toSortOption() ?: TranslationQuestionsSort.defaultSortOption
    }

    override fun sortByType(sort: SelectableMenuItem) {
        sortType.value = sort.toSortType() ?: TranslationQuestionsSort.defaultSortType
    }

    override fun filterBy(filter: SelectableMenuItem) {
        filterType.value = filter.toFilterOption() ?: TranslationQuestionsFilters.defaultFilter
    }
}
