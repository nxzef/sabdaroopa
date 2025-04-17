package com.nascriptone.siddharoopa.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.model.uiobj.CategoryViewType
import com.nascriptone.siddharoopa.data.model.uiobj.Declension
import com.nascriptone.siddharoopa.data.model.uiobj.FavoriteSabdaDetails
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
import com.nascriptone.siddharoopa.ui.screen.Gender
import com.nascriptone.siddharoopa.ui.screen.TableCategory
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenState
import com.nascriptone.siddharoopa.ui.screen.category.DataFetchState
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreenState
import com.nascriptone.siddharoopa.ui.screen.favorites.ScreenState
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreenState
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreenState
import com.nascriptone.siddharoopa.ui.screen.settings.Theme
import com.nascriptone.siddharoopa.ui.screen.table.StringParse
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class SiddharoopaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _homeUIState = MutableStateFlow(HomeScreenState())
    val homeUIState: StateFlow<HomeScreenState> = _homeUIState.asStateFlow()

    private val _categoryUIState = MutableStateFlow(CategoryScreenState())
    val categoryUIState: StateFlow<CategoryScreenState> = _categoryUIState.asStateFlow()


    private val _tableUIState = MutableStateFlow(TableScreenState())
    val tableUIState: StateFlow<TableScreenState> = _tableUIState.asStateFlow()

    private val _favoritesUIState = MutableStateFlow(FavoritesScreenState())
    val favoritesUIState: StateFlow<FavoritesScreenState> = _favoritesUIState.asStateFlow()


    val settingsUIState: StateFlow<SettingsScreenState> = preferencesRepository.currentTheme.map {
        SettingsScreenState(currentTheme = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = runBlocking {
            SettingsScreenState(
                currentTheme = preferencesRepository.currentTheme.first()
            )
        })


    private fun getStringFromResources(resId: Int): String {
        return context.getString(resId)
    }


    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.changeTheme(theme)
        }
    }

    fun fetchFavoriteSabda() {
        viewModelScope.launch(Dispatchers.IO) {
            _favoritesUIState.update { it.copy(result = ScreenState.Loading) }

            val result = runCatching {
                val favoriteSabda = repository.getAllFavoriteSabda()

                val favoriteSabdaList = TableCategory.entries
                    .map { table ->
                        val desiredIDs = favoriteSabda
                            .filter { it.favSabdaCategory == table.name }
                            .map { it.favSabdaId }

                        if (desiredIDs.isEmpty()) return@map emptyList()

                        val sabdaList = when (table) {
                            TableCategory.General -> repository.getGeneralFavoritesSabda(desiredIDs)
                            TableCategory.Specific -> repository.getSpecificFavoritesSabda(desiredIDs)
                        }

                        sabdaList.map { sabda -> FavoriteSabdaDetails(sabda, table) }
                    }
                    .flatten()

                ScreenState.Success(data = favoriteSabdaList)
            }.getOrElse { e ->
                Log.e("error", e.message.orEmpty(), e)
                ScreenState.Error(msg = e.message.orEmpty())
            }

            _favoritesUIState.update { it.copy(result = result) }
        }
    }



    private suspend fun updateCurrentSabda() {
        val userSelectedData = tableUIState.value.selectedSabda
        val favSabdaId = userSelectedData.sabda?.id ?: 0
        val favSabdaCategory = userSelectedData.tableCategory?.name.orEmpty()
        _tableUIState.update {
            it.copy(
                currentSabda = it.currentSabda.copy(
                    favSabdaId = favSabdaId, favSabdaCategory = favSabdaCategory
                )
            )
        }
        checkFavoriteSabdaExistence(tableUIState.value.currentSabda)
    }

    fun toggleFavoriteSabda() {
        viewModelScope.launch {
            val currentSabda = tableUIState.value.currentSabda
            val isItFavorite = tableUIState.value.isItFavorite
            try {
                if (!isItFavorite) {
                    repository.addFavoriteSabda(currentSabda)
                } else {
                    repository.removeFavoriteSabda(
                        currentSabda.favSabdaId,
                        currentSabda.favSabdaCategory
                    )
                }
                checkFavoriteSabdaExistence(currentSabda)
            } catch (e: Exception) {
                Log.e("FavoriteSabda", "Failed to insert/delete", e)
            }
        }
    }

    private suspend fun checkFavoriteSabdaExistence(favoriteSabda: FavoriteSabda) {
        val isExist =
            repository.isFavoriteExists(favoriteSabda.favSabdaId, favoriteSabda.favSabdaCategory)
        _tableUIState.update {
            it.copy(
                isItFavorite = isExist
            )
        }
    }


    fun resetTableState() {
        _tableUIState.value = TableScreenState()
    }

    fun updateSoundFilter(sound: Sound) {
        _categoryUIState.update {
            it.copy(
                selectedSound = sound, selectedGender = null
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
                selectedGender = null,
                isDataFetched = it.lastFetchedCategory == selectedCategory.title,
                lastFetchedCategory = selectedCategory.title
            )
        }
    }


    fun parseStringToDeclension() {
        viewModelScope.launch(Dispatchers.IO) {
            _tableUIState.update { it.copy(result = StringParse.Loading) }
            updateCurrentSabda()
            val result = runCatching {
                val declensionOBJ = tableUIState.value.selectedSabda.sabda?.declension
                val declension = Gson().fromJson(
                    declensionOBJ, Declension::class.java
                )
                val declensionTable = createDeclensionTable(declension)
                StringParse.Success(declensionTable = declensionTable)
            }.getOrElse { e ->
                StringParse.Error(msg = e.message ?: "Can't find declension table.")
            }

            _tableUIState.update { it.copy(result = result) }
        }
    }

    fun updateSelectedTable(sabda: Sabda, sabdaDetailText: String) {
        val selectedCategory = categoryUIState.value.selectedCategory
        val tableCategory = selectedCategory?.category ?: TODO()
        _tableUIState.update {
            it.copy(
                selectedSabda = it.selectedSabda.copy(
                    sabda = sabda, tableCategory = tableCategory, sabdaDetailText = sabdaDetailText
                )
            )
        }
    }

    private fun createDeclensionTable(declension: Declension): List<List<String?>> {
        val vibakti = getStringFromResources(R.string.vibakti)
        val single = getStringFromResources(R.string.single)
        val dual = getStringFromResources(R.string.dual)
        val plural = getStringFromResources(R.string.plural)
        return listOf(
            listOf(vibakti, single, dual, plural), listOf(
                getStringFromResources(R.string.nominative),
                declension.nominative?.single,
                declension.nominative?.dual,
                declension.nominative?.plural
            ), listOf(
                getStringFromResources(R.string.vocative),
                declension.vocative?.single,
                declension.vocative?.dual,
                declension.vocative?.plural
            ), listOf(
                getStringFromResources(R.string.accusative),
                declension.accusative?.single,
                declension.accusative?.dual,
                declension.accusative?.plural
            ), listOf(
                getStringFromResources(R.string.instrumental),
                declension.instrumental?.single,
                declension.instrumental?.dual,
                declension.instrumental?.plural
            ), listOf(
                getStringFromResources(R.string.dative),
                declension.dative?.single,
                declension.dative?.dual,
                declension.dative?.plural
            ), listOf(
                getStringFromResources(R.string.ablative),
                declension.ablative?.single,
                declension.ablative?.dual,
                declension.ablative?.plural
            ), listOf(
                getStringFromResources(R.string.genitive),
                declension.genitive?.single,
                declension.genitive?.dual,
                declension.genitive?.plural
            ), listOf(
                getStringFromResources(R.string.locative),
                declension.locative?.single,
                declension.locative?.dual,
                declension.locative?.plural
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
                    categoryUIState.value.selectedGender?.let { sabda.gender == it.name.lowercase() }).all { it }
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
                DataFetchState.Error(e.message ?: "Unknown error occurred")
            }

            _categoryUIState.update { it.copy(result = result, isDataFetched = true) }
            applyFilter()
        }
    }


}