package com.nascriptone.siddharoopa.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.R
import com.nascriptone.siddharoopa.data.model.entity.Favorite
import com.nascriptone.siddharoopa.data.model.entity.RestProp
import com.nascriptone.siddharoopa.data.model.uiobj.Declension
import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.data.model.uiobj.IsFavorite
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenState
import com.nascriptone.siddharoopa.ui.screen.category.FilterState
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreenState
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreenState
import com.nascriptone.siddharoopa.ui.screen.home.ObserveSabda
import com.nascriptone.siddharoopa.ui.screen.quiz.CreationState
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionType
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizSectionState
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreenState
import com.nascriptone.siddharoopa.ui.screen.settings.Theme
import com.nascriptone.siddharoopa.ui.screen.table.StringParse
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import kotlinx.serialization.json.Json
import javax.inject.Inject


@HiltViewModel
class SiddharoopaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _entireSabdaList = MutableStateFlow(listOf<EntireSabda>())
    val entireSabdaList: StateFlow<List<EntireSabda>> = _entireSabdaList.asStateFlow()

    private val _homeUIState = MutableStateFlow(HomeScreenState())
    val homeUIState: StateFlow<HomeScreenState> = _homeUIState.asStateFlow()

    private val _categoryUIState = MutableStateFlow(CategoryScreenState())
    val categoryUIState: StateFlow<CategoryScreenState> = _categoryUIState.asStateFlow()

    private val _tableUIState = MutableStateFlow(TableScreenState())
    val tableUIState: StateFlow<TableScreenState> = _tableUIState.asStateFlow()

    private val _favoritesUIState = MutableStateFlow(FavoritesScreenState())
    val favoritesUIState: StateFlow<FavoritesScreenState> = _favoritesUIState.asStateFlow()

    private val _quizUIState = MutableStateFlow(QuizSectionState())
    val quizUIState: StateFlow<QuizSectionState> = _quizUIState.asStateFlow()

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
        observeSabda()
    }


    private fun getStringFromResources(resId: Int): String {
        return context.getString(resId)
    }


    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.changeTheme(theme)
        }
    }


    private fun observeSabda() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllRestProp().collect { props ->

                _homeUIState.update { it.copy(result = ObserveSabda.Loading) }

                val result = runCatching {
                    val favorites = props.mapNotNull { it.favorite }

                    val sabdaList = Table.entries.map { table ->
                        async {
                            val sabda = when (table) {
                                Table.GENERAL -> repository.getAllGeneralSabda()
                                Table.SPECIFIC -> repository.getAllSpecificSabda()
                            }

                            sabda.map { sabda ->
                                val favorite =
                                    favorites.find { it.id == sabda.id && it.table == table }

                                EntireSabda(
                                    sabda = sabda, table = table, isFavorite = IsFavorite(
                                        status = favorite != null,
                                        timestamp = favorite?.timestamp
                                            ?: System.currentTimeMillis()
                                    )
                                )
                            }
                        }
                    }.awaitAll().flatten()
                    _entireSabdaList.value = sabdaList
                    ObserveSabda.Success
                }.getOrElse { e ->
                    Log.e("observeSabda", "Error occurred", e)
                    ObserveSabda.Error(msg = e.message.orEmpty())
                }

                _homeUIState.update { it.copy(result = result) }
            }
        }
    }


    fun createQuizQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _quizUIState.update { it.copy(result = CreationState.Loading) }
            val entireSabdaList = entireSabdaList.value
            val userSelectedTable = quizUIState.value.questionFrom
            val userSelectedQuestionType = quizUIState.value.questionType
            val userSelectedQuestionRange = quizUIState.value.questionRange
            val result = runCatching {

                val chosenData = entireSabdaList.filter { sabda ->
                    listOfNotNull(
                        userSelectedTable?.let { it == sabda.table }
                    ).all { it }
                }
                val randomPickedSabda =
                    chosenData.shuffled().take(userSelectedQuestionRange.toInt())
                CreationState.Success(data = randomPickedSabda)
            }.getOrElse { e ->
                Log.d("error", "Question Creation error", e)
                CreationState.Error(e.message.orEmpty())
            }
            _quizUIState.update { it.copy(result = result) }
        }
    }


    fun updateQuizQuestionTable(table: Table?) {
        _quizUIState.update {
            it.copy(
                questionFrom = table,
            )
        }
    }

    fun updateQuizQuestionType(type: QuestionType) {
        _quizUIState.update {
            it.copy(
                questionType = type,
            )
        }
    }

    fun updateQuizQuestionRange(range: Float) {
        _quizUIState.update {
            it.copy(
                questionRange = range,
            )
        }
    }


    fun toggleFavoriteSabda(currentSabda: EntireSabda) {
        val isItFavorite = currentSabda.isFavorite.status
        if (isItFavorite) removeFavoriteFromRestProp(currentSabda)
        else addFavoriteToRestProp(currentSabda)
    }

    private fun removeFavoriteFromRestProp(currentSabda: EntireSabda) {
        viewModelScope.launch {
            runCatching {
                val favorite = Favorite(
                    id = currentSabda.sabda.id,
                    table = currentSabda.table,
                    timestamp = currentSabda.isFavorite.timestamp
                )
                repository.removeRestProp(favorite = favorite)
            }.getOrElse { e ->
                Log.d("error", "Remove Sabda Error Occur!", e)
            }
        }
    }

    private fun addFavoriteToRestProp(currentSabda: EntireSabda) {
        viewModelScope.launch {
            runCatching {
                val restProp = RestProp(
                    favorite = Favorite(
                        id = currentSabda.sabda.id,
                        table = currentSabda.table
                    )
                )
                repository.addRestProp(restProp)
            }.getOrElse { e ->
                Log.d("error", "Add Sabda Error Occur!", e)
            }
        }
    }

    fun updateSabdaToRemove(currentSabda: EntireSabda?) {
        _favoritesUIState.update {
            it.copy(sabdaToRemove = currentSabda)
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
        applyFilter(entireSabdaList.value)
    }

    fun updateGenderFilter(gender: Gender?) {
        _categoryUIState.update {
            it.copy(
                selectedGender = gender
            )
        }
        applyFilter(entireSabdaList.value)
    }

    fun updateTable(selectedTable: Table, selectedSound: Sound) {
        _categoryUIState.update {
            it.copy(
                selectedTable = selectedTable,
                selectedSound = selectedSound,
                selectedGender = null
            )
        }
    }

    fun parseStringToDeclension(currentSabda: EntireSabda) {
        viewModelScope.launch(Dispatchers.IO) {
            _tableUIState.update {
                it.copy(
                    result = StringParse.Loading
                )
            }
            val result = runCatching {
                val declensionOBJ = currentSabda.sabda.declension
                val declension = Json.decodeFromString<Declension>(declensionOBJ)
                val declensionTable = createDeclensionTable(declension)
                StringParse.Success(declensionTable = declensionTable)
            }.getOrElse { e ->
                StringParse.Error(msg = e.message ?: "Can't find declension table.")
            }
            _tableUIState.update { it.copy(result = result) }
        }
    }

    fun updateSelectedSabda(sabda: EntireSabda) {
        _tableUIState.update {
            it.copy(
                selectedSabda = sabda
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

    private fun applyFilter(data: List<EntireSabda>) {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryUIState.update { it.copy(result = FilterState.Loading) }
            val result = runCatching {
                val filteredData = data.filter { entireSabda ->

                    listOfNotNull(
                        categoryUIState.value.selectedTable?.let { it == entireSabda.table },
                        categoryUIState.value.selectedSound?.let { it.name.lowercase() == entireSabda.sabda.sound },
                        categoryUIState.value.selectedGender?.let { it.name.lowercase() == entireSabda.sabda.gender }
                    ).all { it }
                }
                FilterState.Success(filteredData = filteredData)
            }.getOrElse { e ->
                Log.d("error", "Error filtering data", e)
                FilterState.Error(msg = e.message.orEmpty())
            }
            _categoryUIState.update { it.copy(result = result) }
        }
    }

    fun filterSabda(entireSabdaList: List<EntireSabda>) = applyFilter(entireSabdaList)

}