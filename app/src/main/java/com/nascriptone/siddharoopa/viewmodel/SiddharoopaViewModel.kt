package com.nascriptone.siddharoopa.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.model.uiobj.CategoryViewType
import com.nascriptone.siddharoopa.data.model.uiobj.Declension
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.ui.screen.Gender
import com.nascriptone.siddharoopa.ui.screen.TableCategory
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenState
import com.nascriptone.siddharoopa.ui.screen.category.DataFetchState
import com.nascriptone.siddharoopa.ui.screen.table.StringParse
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SiddharoopaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository
) :
    ViewModel() {

    private val _categoryUIState =
        MutableStateFlow(CategoryScreenState())
    val categoryUIState: StateFlow<CategoryScreenState> = _categoryUIState.asStateFlow()


    private val _tableUIState = MutableStateFlow(TableScreenState())
    val tableUIState: StateFlow<TableScreenState> = _tableUIState.asStateFlow()

    private fun getStringFromResources(resId: Int): String {
        return context.getString(resId)
    }

    fun resetTableState() {
        _tableUIState.value = TableScreenState()
    }

    fun updateSoundFilter(sound: Sound) {
        _categoryUIState.update {
            it.copy(
                selectedSound = sound
            )
        }

        applyFilter()
    }

    fun updateGenderFilter(gender: Gender?) {
        _categoryUIState.update {
            it.copy(
                selectedGender = gender
            )
        }

        applyFilter()
    }


    fun updateSelectedCategory(
        selectedCategory: CategoryViewType,
        selectedSound: Sound,
    ) {
        _categoryUIState.update {
            it.copy(
                selectedCategory = selectedCategory,
                selectedSound = selectedSound,
                isDataFetched = it.lastFetchedCategory == selectedCategory.title,
                lastFetchedCategory = selectedCategory.title
            )
        }
    }


    fun parseStringToDeclension() {
        viewModelScope.launch(Dispatchers.IO) {
            _tableUIState.update { it.copy(result = StringParse.Loading) }

            val result = runCatching {
                val declension = Gson().fromJson(
                    tableUIState.value.selectedSabda?.declension,
                    Declension::class.java
                )
                val declensionTable = createDeclensionTable(declension)
                StringParse.Success(declensionTable = declensionTable)
            }.getOrElse { e ->
                StringParse.Error(msg = e.message ?: "Can't find declension table.")
            }

            _tableUIState.update { it.copy(result = result) }
        }
    }

    fun updateSelectedTable(selectedSabda: Sabda) {
        _tableUIState.update {
            it.copy(
                selectedSabda = selectedSabda
            )
        }
    }

    private fun createDeclensionTable(declension: Declension): List<List<String>> {
        val vibakti = getStringFromResources(R.string.vibakti)
        val single = getStringFromResources(R.string.single)
        val dual = getStringFromResources(R.string.dual)
        val plural = getStringFromResources(R.string.plural)
        return listOf(
            listOf(vibakti, single, dual, plural),
            listOf(
                getStringFromResources(R.string.nominative),
                declension.nominative.single,
                declension.nominative.dual,
                declension.nominative.plural
            ),
            listOf(
                getStringFromResources(R.string.vocative),
                declension.vocative.single,
                declension.vocative.dual,
                declension.vocative.plural
            ),
            listOf(
                getStringFromResources(R.string.accusative),
                declension.accusative.single,
                declension.accusative.dual,
                declension.accusative.plural
            ),
            listOf(
                getStringFromResources(R.string.instrumental),
                declension.instrumental.single,
                declension.instrumental.dual,
                declension.instrumental.plural
            ),
            listOf(
                getStringFromResources(R.string.dative),
                declension.dative.single,
                declension.dative.dual,
                declension.dative.plural
            ),
            listOf(
                getStringFromResources(R.string.ablative),
                declension.ablative.single,
                declension.ablative.dual,
                declension.ablative.plural
            ),
            listOf(
                getStringFromResources(R.string.genitive),
                declension.genitive.single,
                declension.genitive.dual,
                declension.genitive.plural
            ),
            listOf(
                getStringFromResources(R.string.locative),
                declension.locative.single,
                declension.locative.dual,
                declension.locative.plural
            )
        )
    }

    private fun applyFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            val data =
                (categoryUIState.value.result as? DataFetchState.Success)?.data ?: emptyList()


            val filteredData = data.filter { sabda ->
                listOfNotNull(
                    categoryUIState.value.selectedSound?.let { sabda.sound == it.eng.lowercase() },
                    categoryUIState.value.selectedGender?.let { sabda.gender == it.name.lowercase() }
                ).all { it }
            }

            _categoryUIState.update {
                it.copy(
                    filteredData = filteredData
                )
            }
        }
    }


    fun fetchSabda() {
        if (categoryUIState.value.isDataFetched) {
            applyFilter()
            return
        }

        val category = categoryUIState.value.selectedCategory?.category

        viewModelScope.launch(Dispatchers.IO) {
            _categoryUIState.update {
                it.copy(
                    result = DataFetchState.Loading
                )
            }

            val result = try {
                val data = category?.let {
                    when (it) {
                        TableCategory.General -> repository.getAllGeneralSabda()
                        TableCategory.Specific -> repository.getAllSpecificSabda()
                    }
                } ?: throw IllegalArgumentException("Invalid category")

                DataFetchState.Success(data)
            } catch (e: Exception) {
                Log.e("fetchSabda", "Error fetching data", e)
                DataFetchState.Error(e.message ?: "Unknown error occurred")
            }

            _categoryUIState.update { it.copy(result = result, isDataFetched = true) }
            applyFilter()
        }
    }


}