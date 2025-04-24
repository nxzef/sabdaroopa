package com.nascriptone.siddharoopa.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.FavoriteSabda
import com.nascriptone.siddharoopa.data.model.uiobj.Declension
import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
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
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
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


    init {
        observeFavorites()
    }


    private fun getStringFromResources(resId: Int): String {
        return context.getString(resId)
    }


    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.changeTheme(theme)
        }
    }


    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getAllFavoriteSabda().collect { favoriteSabdaList ->
                _favoritesUIState.update { it.copy(result = ScreenState.Loading) }

                val state = runCatching {
                    val reversed = favoriteSabdaList.asReversed()

                    val favoriteSabdaDetailsUnordered = Table.entries.map { table ->
                        val desiredIDs =
                            reversed.filter { it.favSabdaCategory == table.name }
                                .map { it.favSabdaId }

                        if (desiredIDs.isEmpty()) return@map emptyList()

                        val sabdaList = when (table) {
                            Table.GENERAL -> repository.getGeneralFavoritesSabda(desiredIDs)
                            Table.SPECIFIC -> repository.getSpecificFavoritesSabda(
                                desiredIDs
                            )
                        }

                        sabdaList.fastMap { sabda -> EntireSabda(sabda, table) }
                    }.flatten()

                    val unorderedMap = favoriteSabdaDetailsUnordered.associateBy { it.sabda.id }
                    val favoriteSabdaList = reversed.mapNotNull { sabda ->
                        unorderedMap[sabda.favSabdaId]
                    }

                    tableUIState.value.currentSabda?.let { currentSabda ->
                        _tableUIState.update { it.copy(isItFavorite = favoriteSabdaList.any { it == currentSabda }) }
                    }

                    if (favoriteSabdaList.isEmpty()) ScreenState.Empty
                    else ScreenState.Success(data = favoriteSabdaList)

                }.getOrElse { e ->
                    Log.e("error", e.message.orEmpty(), e)
                    ScreenState.Error(msg = e.message.orEmpty())
                }

                _favoritesUIState.update { it.copy(result = state) }
            }
        }
    }


    fun toggleFavoriteSabda(currentSabda: EntireSabda) {
        val isItFavorite = tableUIState.value.isItFavorite
        if (isItFavorite) removeSabdaFromFavorites(currentSabda)
        else addSabdaToFavorites(currentSabda)
    }

    fun removeSabdaFromFavorites(currentSabda: EntireSabda) {
        viewModelScope.launch {
            runCatching {
                repository.removeFavoriteSabda(
                    id = currentSabda.sabda.id,
                    table = currentSabda.table.name
                )
            }.getOrElse { e ->
                Log.d("error", "Remove Sabda Error Occur!", e)
            }
        }
    }

    fun addSabdaToFavorites(currentSabda: EntireSabda) {
        viewModelScope.launch {
            runCatching {
                val favoriteSabda = FavoriteSabda(
                    favSabdaId = currentSabda.sabda.id,
                    favSabdaCategory = currentSabda.table.name
                )
                repository.addFavoriteSabda(favoriteSabda)
            }.getOrElse { e ->
                Log.d("error", "Add Sabda Error Occur!", e)
            }
        }
    }

    fun updateSabdaToRemove(currentSabda: EntireSabda) {
        _favoritesUIState.update {
            it.copy(sabdaToRemove = currentSabda)
        }
    }

    private fun checkFavoriteSabdaExistence(currentSabda: EntireSabda): Boolean {
        val currentState = favoritesUIState.value.result
        if (currentState !is ScreenState.Success) return false
        val favoriteSabdaList = currentState.data
        return favoriteSabdaList.any { it == currentSabda }
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


    fun updateTable(
        selectedTable: Table,
        selectedSound: Sound,
    ) {
        _homeUIState.update {
            it.copy(
                selectedTable = selectedTable,
                selectedSound = selectedSound
            )
        }
    }

    fun parseStringToDeclension(currentSabda: EntireSabda) {
        viewModelScope.launch(Dispatchers.IO) {
            _tableUIState.update {
                it.copy(
                    currentSabda = currentSabda,
                    result = StringParse.Loading
                )
            }
            val result = runCatching {
                val declensionOBJ = currentSabda.sabda.declension
                val declension = Gson().fromJson(
                    declensionOBJ, Declension::class.java
                )
                val declensionTable = createDeclensionTable(declension)
                StringParse.Success(declensionTable = declensionTable)
            }.getOrElse { e ->
                StringParse.Error(msg = e.message ?: "Can't find declension table.")
            }
            val isExist = checkFavoriteSabdaExistence(currentSabda)
            _tableUIState.update { it.copy(result = result, isItFavorite = isExist) }
        }
    }

    fun updateSelectedSabda(sabda: EntireSabda) {
        _categoryUIState.update {
            it.copy(
                selectedSabda = sabda
            )
        }
        categoryUIState.value.selectedSabda?.let {
            selectSabdaToShowDeclension(it)
        }
    }

    fun selectSabdaToShowDeclension(sabda: EntireSabda) {
        _tableUIState.update {
            it.copy(
                currentSabda = sabda
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
            val filteredData = data.filter { entireSabda ->

                listOfNotNull(
                    categoryUIState.value.selectedSound?.let { it.name.lowercase() == entireSabda.sabda.sound },
                    categoryUIState.value.selectedGender?.let { it.name.lowercase() == entireSabda.sabda.gender }
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
        viewModelScope.launch(Dispatchers.IO) {
            val selectedTable = homeUIState.value.selectedTable
            val selectedSound = homeUIState.value.selectedSound

            val alreadyFetched = _categoryUIState.updateAndGet {
                it.copy(
                    selectedSound = selectedSound,
                    selectedGender = null,
                    isDataFetched = it.lastFetchedTable == selectedTable,
                    lastFetchedTable = selectedTable
                )
            }.isDataFetched

            if (alreadyFetched) {
                applyFilter()
                return@launch
            }

            _categoryUIState.update { it.copy(result = DataFetchState.Loading) }

            val result = runCatching {
                val retrievedData = when (selectedTable) {
                    Table.GENERAL -> repository.getAllGeneralSabda()
                    Table.SPECIFIC -> repository.getAllSpecificSabda()
                }

                val data = retrievedData.fastMap { sabda ->
                    EntireSabda(sabda = sabda, table = selectedTable)
                }

                DataFetchState.Success(data)
            }.getOrElse { e ->
                DataFetchState.Error(e.message ?: "Unknown error occurred")
            }

            _categoryUIState.update {
                it.copy(result = result, isDataFetched = true)
            }

            applyFilter()
        }
    }


}