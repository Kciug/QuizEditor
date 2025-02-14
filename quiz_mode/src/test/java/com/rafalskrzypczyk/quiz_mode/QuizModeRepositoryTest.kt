package com.rafalskrzypczyk.quiz_mode

import com.rafalskrzypczyk.core.api_result.Response
import com.rafalskrzypczyk.quiz_mode.data.CategoryDTO
import com.rafalskrzypczyk.quiz_mode.data.FirestoreApi
import com.rafalskrzypczyk.quiz_mode.data.QuizModeRepositoryImpl
import com.rafalskrzypczyk.quiz_mode.domain.models.Category
import com.rafalskrzypczyk.quiz_mode.domain.models.Question
import com.rafalskrzypczyk.quiz_mode.domain.models.toDTO
import com.rafalskrzypczyk.quiz_mode.domain.models.toDomain
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class QuizModeRepositoryTest {
    private lateinit var repository: QuizModeRepositoryImpl
    private val firestoreApi: FirestoreApi = mockk(relaxed = true)

    private val categories = mutableListOf(Category.new("Category1"), Category.new("Category2"))
    private val questions = mutableListOf(Question.new("Question1"), Question.new("Question2"))

    @Before
    fun setup() {
        repository = QuizModeRepositoryImpl(firestoreApi)

        coEvery { firestoreApi.getAllCategories() } returns flowOf(Response.Success(categories.map { it.toDTO() }))
        coEvery { firestoreApi.addCategory(any())} answers {
            categories.add(firstArg<CategoryDTO>().toDomain())
            Response.Success(Unit)
        }
        coEvery { firestoreApi.deleteCategory(any()) } answers {
            categories.removeIf { it.id == firstArg<Int>() }
            Response.Success(Unit)
        }

        coEvery { firestoreApi.getAllQuestions() } returns flowOf(Response.Success(questions.map { it.toDTO() }))
    }

    @Test
    fun `getAllCategories fetches from Firestore if cache is empty`() = runTest{
        repository.getAllCategories().collect {response ->
            assertTrue(response is Response.Success)
            assertEquals(categories, (response as Response.Success).data)
        }
    }

    @Test
    fun `getAllCategories returns cached data if available`() = runTest {
        val dynamicCategoriesData = mutableListOf<CategoryDTO>()
        dynamicCategoriesData.addAll(categories.map { it.toDTO() })
        coEvery { firestoreApi.getAllCategories() } returns flowOf(Response.Success(dynamicCategoriesData))
        repository.getAllCategories().collect{}
        dynamicCategoriesData.clear()

        repository.getAllCategories().collect { response ->
            assertTrue(response is Response.Success)
            assertEquals(categories, (response as Response.Success).data)
        }
    }

    @Test
    fun `addCategory adds category and updates cache`() = runTest {
        repository.getAllCategories().collect{}
        val newCategoryTitle = "NewCat"

        val result = repository.addCategory(newCategoryTitle)
        assertTrue(result is Response.Success)

        assertTrue(categories.any { it.title == newCategoryTitle })
        assertEquals(categories.find { it.title == newCategoryTitle }!!.id, (result as Response.Success).data)
        repository.getAllCategories().collect { response ->
            assertTrue((response as Response.Success).data.any { it.title == newCategoryTitle })
        }
    }

    @Test
    fun `addCategory doesn't add category to cache if api returns error`() = runTest {
        coEvery { firestoreApi.addCategory(any()) } returns Response.Error("Error")
        repository.getAllCategories()
        val newCategoryTitle = "NewCat"

        val result = repository.addCategory(newCategoryTitle)

        assertTrue(result is Response.Error)
        assertFalse(categories.any { it.title == newCategoryTitle })
        repository.getAllCategories().collect { response ->
            assertFalse((response as Response.Success).data.any { it.title == newCategoryTitle })
        }
    }

    @Test
    fun `deleteCategory removes category and updates cache`() = runTest {
        repository.getAllCategories().collect{}
        val idToRemove = categories.first().id

        val result = repository.deleteCategory(idToRemove)

        assertTrue(result is Response.Success)
        assertFalse(categories.any { it.id == idToRemove })
        repository.getAllCategories().collect { response ->
            assertTrue(response is Response.Success)
            assertFalse((response as Response.Success).data.any { it.id == idToRemove })
        }
    }

    @Test
    fun `deleteCategory doesn't remove category from cache if api returns error`() = runTest {
        coEvery { firestoreApi.deleteCategory(any()) } returns Response.Error("Error")
        repository.getAllCategories()
        val idToRemove = categories.first().id

        val result = repository.deleteCategory(idToRemove)

        assertTrue(result is Response.Error)
        assertTrue(categories.any { it.id == idToRemove })
        repository.getAllCategories().collect { response ->
            assertTrue((response as Response.Success).data.any { it.id == idToRemove })
        }
    }

    @Test
    fun `getCategoryById returns category from cache`() = runTest {
        repository.getAllCategories().collect{}
        val categoryIdToFetch = categories.first().id

        repository.getCategoryById(categoryIdToFetch).collect { response ->
            assertTrue(response is Response.Success)
            assertEquals(categoryIdToFetch, (response as Response.Success).data.id)
        }
    }

    @Test
    fun `getCategoryById returns error when category not found`() = runTest {
        repository.getCategoryById(99).collect { response ->
            assertTrue(response is Response.Error)
        }
    }

    @Test
    fun `getQuestionById returns question from cache`() = runTest {
        val questionIdToFetch = questions.first().id

        repository.getAllQuestions().collect{}

        repository.getQuestionById(questionIdToFetch).collect { response ->
            assertTrue(response is Response.Success)
            assertEquals(questionIdToFetch, (response as Response.Success).data.id)
        }
    }

    @Test
    fun `getQuestionById returns error when question not found`() = runTest {
        repository.getQuestionById(99).collect { response ->
            assertTrue(response is Response.Error)
        }
    }

    @Test
    fun `updateCategory updates cache correctly`() = runTest {
        coEvery { firestoreApi.updateCategory(any()) } returns Response.Success(Unit)
        repository.getAllCategories().collect{}
        val categoryToUpdate = categories.first().copy()
        categoryToUpdate.title = "Updated Category"

        val result = repository.updateCategory(categoryToUpdate)

        assertTrue(result is Response.Success)
        repository.getAllCategories().collect{
            assertTrue((it as Response.Success).data.any { it.title == categoryToUpdate.title })
        }
    }

    @Test
    fun `updateQuestion updates cache correctly`() = runTest {
        coEvery { firestoreApi.updateQuestion(any()) } returns Response.Success(Unit)
        repository.getAllQuestions().collect{}
        val questionToUpdate = questions.first().copy()
        questionToUpdate.text = "Updated Question"

        val result = repository.updateQuestion(questionToUpdate)

        assertTrue(result is Response.Success)
        repository.getAllQuestions().collect{
            assertTrue((it as Response.Success).data.any { it.text == questionToUpdate.text })
        }
    }
}