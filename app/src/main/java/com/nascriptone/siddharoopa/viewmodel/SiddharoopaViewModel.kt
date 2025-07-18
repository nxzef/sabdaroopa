package com.nascriptone.siddharoopa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.local.QuizQuestion
import com.nascriptone.siddharoopa.data.model.entity.Favorite
import com.nascriptone.siddharoopa.data.model.entity.RestProp
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.model.uiobj.CaseName
import com.nascriptone.siddharoopa.data.model.uiobj.Declension
import com.nascriptone.siddharoopa.data.model.uiobj.EntireSabda
import com.nascriptone.siddharoopa.data.model.uiobj.FormName
import com.nascriptone.siddharoopa.data.model.uiobj.Gender
import com.nascriptone.siddharoopa.data.model.uiobj.IsFavorite
import com.nascriptone.siddharoopa.data.model.uiobj.MCQ
import com.nascriptone.siddharoopa.data.model.uiobj.MTF
import com.nascriptone.siddharoopa.data.model.uiobj.Phrase
import com.nascriptone.siddharoopa.data.model.uiobj.Sound
import com.nascriptone.siddharoopa.data.model.uiobj.Table
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenState
import com.nascriptone.siddharoopa.ui.screen.category.FilterState
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreenState
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreenState
import com.nascriptone.siddharoopa.ui.screen.home.ObserveSabda
import com.nascriptone.siddharoopa.ui.screen.quiz.Answer
import com.nascriptone.siddharoopa.ui.screen.quiz.CreationState
import com.nascriptone.siddharoopa.ui.screen.quiz.McqGeneratedData
import com.nascriptone.siddharoopa.ui.screen.quiz.MtfGeneratedData
import com.nascriptone.siddharoopa.ui.screen.quiz.Option
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionOption
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionType
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizSectionState
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreenState
import com.nascriptone.siddharoopa.ui.screen.settings.Theme
import com.nascriptone.siddharoopa.ui.screen.table.StringParse
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun submitAnswer(qID: Int) {
        val result = quizUIState.value.result
        if (result !is CreationState.Success) return

        val questionOptions = result.data
        val questionOptionsToMutable = questionOptions.toMutableList()
        val answer = quizUIState.value.currentAnswer
        val targetObject = questionOptionsToMutable[qID]
        val updatedObject = targetObject.copy(answer = answer)
        questionOptionsToMutable[qID] = updatedObject
        val updatedList = questionOptionsToMutable.toList()

        _quizUIState.update {
            it.copy(
                result = CreationState.Success(data = updatedList)
            )
        }
    }


    fun updateCurrentAnswer(answer: Answer) {
        _quizUIState.update {
            it.copy(
                currentAnswer = answer
            )
        }
    }

    fun createQuizQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _quizUIState.update { it.copy(result = CreationState.Loading) }
            val result = runCatching {
                val entireSabdaList = entireSabdaList.value
                val userSelectedTable = quizUIState.value.questionFrom
                val userSelectedQuestionType = quizUIState.value.questionType
                val userSelectedQuestionRange = quizUIState.value.questionRange.toInt()
                val maxMCQ = (userSelectedQuestionRange * 70) / 100
                val allGenders = entireSabdaList.map { it.sabda.gender }.toSet()
                val allSabda = entireSabdaList.map { it.sabda }.toSet()
                val allAntas = entireSabdaList.map { it.sabda.anta }.toSet()
                val chosenData = entireSabdaList.filter { sabda ->
                    listOfNotNull(
                        userSelectedTable?.let { it == sabda.table }).all { it }
                }
                val randomPickedSabda = chosenData.shuffled().take(userSelectedQuestionRange)

                val data = randomPickedSabda.mapIndexed { index, entireSabda ->
                    val questionCollection = when (userSelectedQuestionType) {
                        QuestionType.All -> if (index < maxMCQ) QuizQuestion.mcqQuestions else QuizQuestion.mtfQuestions
                        QuestionType.MCQ -> QuizQuestion.mcqQuestions
                        QuestionType.MTF -> QuizQuestion.mtfQuestions
                    }
                    val sabda = entireSabda.sabda
                    val declension = Json.decodeFromString<Declension>(sabda.declension)
                    val randomTemplate = questionCollection.random()
                    val question = randomTemplate.questionResId

                    val option = when (val result = randomTemplate.phrase) {
                        is Phrase.McqKey -> {
                            val mcqOption = generateMcqOption(
                                result.mcqData, sabda, declension, allGenders, allAntas
                            )
                            Option.McqOption(mcqOption)
                        }

                        is Phrase.MtfKey -> {
                            val mtfOption = generateMtfOption(
                                result.mtfData, sabda, declension, allGenders, allSabda
                            )
                            Option.MtfOption(mtfOption)
                        }
                    }
                    QuestionOption(
                        question = question, option = option
                    )
                }

                CreationState.Success(data = data)
            }.getOrElse { e ->
                Log.d("error", "Question Creation error", e)
                CreationState.Error(e.message.orEmpty())
            }
            _quizUIState.update { it.copy(result = result) }
        }
    }

    private fun generateMcqOption(
        type: MCQ,
        sabda: Sabda,
        declension: Declension,
        genders: Set<String>,
        anta: Set<String>
    ): McqGeneratedData {
        var options: Set<String> = emptySet()
        var trueOption: String? = null
        var questionKey: Map<String, String> = emptyMap()
        val allForm = declension.values.flatMap { it.values }
        val allVachana = declension.values.flatMap { it.keys }.toSet().map { it.name }
        when (type) {
            MCQ.ONE, MCQ.TWO, MCQ.THREE, MCQ.EIGHT -> {

                var randomCase: CaseName
                var randomForm: FormName
                do {
                    randomCase = declension.keys.random()
                    randomForm = declension.getValue(randomCase).keys.random()
                    trueOption = declension.getValue(randomCase).getValue(randomForm)
                } while (trueOption == null)


                options = getUniqueShuffledSet(allForm, trueOption)
                questionKey = mapOf(
                    "vibhakti" to randomCase.name,
                    "vachana" to randomForm.name,
                    "sabda" to sabda.word
                )

            }

            MCQ.FOUR -> {
                val listOfGenders = genders.toList()
                trueOption = sabda.gender
                options = getUniqueShuffledSet(listOfGenders, trueOption)
                questionKey = mapOf(
                    "sabda" to sabda.word
                )
            }

            MCQ.FIVE -> {

                var selectedForm: FormName
                var chosenFormValue: String?
                do {
                    val randomCase = declension.keys.random()
                    selectedForm = declension.getValue(randomCase).keys.random()
                    chosenFormValue = declension.getValue(randomCase).getValue(selectedForm)
                } while (chosenFormValue == null)

                trueOption = selectedForm.name
                options = allVachana.shuffled().toSet()
                questionKey = mapOf(
                    "form" to chosenFormValue, "sabda" to sabda.word
                )

            }

            MCQ.SIX -> {} // Sixth Question business logic will set after the additional sabda insertion.

            MCQ.SEVEN -> {

                val listOfAntas = anta.toList()
                trueOption = sabda.anta
                options = getUniqueShuffledSet(listOfAntas, trueOption)
                questionKey = mapOf(
                    "sabda" to sabda.word
                )
            }
        }
        return McqGeneratedData(options, trueOption.orEmpty(), questionKey)
    }

    private fun generateMtfOption(
        type: MTF,
        sabda: Sabda,
        declension: Declension,
        genders: Set<String>,
        allSabda: Set<Sabda>
    ): MtfGeneratedData {

        var options = mapOf<String, String>()
        var correctOptionMap = mapOf<String, String>()
        var questionKey = mapOf<String, String>()
        val shuffledDeclension = declension.toList().shuffled().toMap()

        when (type) {


            MTF.ONE, MTF.TWO -> {

                val forms = declension.values.flatMap { it.keys }.toSet().shuffled()
                var extraOption: String? = null

                for (form in forms) {
                    val formSet = declension.mapNotNull { it.value[form] }.toMutableSet()
                    val validEntries = shuffledDeclension.filterValues {
                        val currentForm = it[form]
                        currentForm != null && formSet.remove(currentForm)
                    }
                    if (validEntries.size >= 4) {
                        val correctOptions = validEntries.entries.take(3)
                        val distractorCandidates = validEntries.entries.drop(3)
                        correctOptionMap = correctOptions.associate { (k, v) ->
                            when (type) {
                                MTF.ONE -> k.name to v[form]!!
                                else -> v[form]!! to k.name
                            }
                        }
                        val candidate = distractorCandidates.randomOrNull()
                        extraOption = when (type) {
                            MTF.ONE -> candidate?.value?.get(form)
                            else -> candidate?.key?.name
                        }
                        break
                    }
                }

                val fullOptionMap =
                    if (extraOption != null) correctOptionMap + ("" to extraOption) else correctOptionMap
                val shuffledValues = fullOptionMap.values.shuffled()
                options = fullOptionMap.keys.zip(shuffledValues).toMap()
                questionKey = mapOf(
                    "sabda" to sabda.word
                )

            }

            MTF.THREE -> {} // Third Question business logic will set after the additional data insertion


            MTF.FOUR -> {

                val sabdaByGender = allSabda.groupBy { it.gender }
                val shuffledGenders = genders.shuffled()
                correctOptionMap = shuffledGenders.mapNotNull { gender ->
                    sabdaByGender[gender]?.random()
                }.associate { sabda -> sabda.gender to sabda.word }


                val shuffledValues = correctOptionMap.values.shuffled()
                options = correctOptionMap.keys.zip(shuffledValues).toMap()

            }


            MTF.FIVE -> {

                var currentDeclension = declension
                val invalidDeclensionSet = mutableSetOf<Sabda>()
                while (true) {
                    val keySet = currentDeclension.values.flatMap { it.keys }.toSet()
                    val hasEmptyKey = keySet.any { key ->
                        currentDeclension.values.mapNotNull { it[key] }.toSet().isEmpty()
                    }
                    if (hasEmptyKey) {
                        val currentSabda = allSabda.find {
                            val decoded = Json.decodeFromString<Declension>(it.declension)
                            decoded == currentDeclension
                        } ?: error("Current declension not found in allSabda")
                        invalidDeclensionSet.add(currentSabda)
                        val remaining = allSabda.subtract(invalidDeclensionSet)
                        if (remaining.isEmpty()) break
                        val randomSabda = remaining.random()
                        val newDeclension =
                            Json.decodeFromString<Declension>(randomSabda.declension)
                        currentDeclension = newDeclension
                    } else {
                        val previousValues = mutableSetOf<String>()
                        correctOptionMap = keySet.shuffled().associate { key ->
                            val currentSet = currentDeclension.values.mapNotNull { it[key] }.toSet()
                            val available = currentSet.minus(previousValues)
                            require(available.isNotEmpty()) {
                                "No unique value available for key: ${key.name}"
                            }
                            val chosen = available.random()
                            previousValues.add(chosen)
                            key.name to chosen
                        }
                        val shufflesValues = correctOptionMap.values.shuffled()
                        options = correctOptionMap.keys.zip(shufflesValues).toMap()
                        break
                    }
                }
                questionKey = mapOf(
                    "sabda" to sabda.word
                )
            }
        }
        return MtfGeneratedData(options, correctOptionMap, questionKey)
    }

    private fun getUniqueShuffledSet(
        originalList: List<String?>, newItem: String
    ): Set<String> {
        val cleanedList = originalList.filterNotNull().toSet()
        val candidates = cleanedList - newItem
        if (candidates.size < 3) {
            val fallback = candidates.toMutableList().apply { add(newItem) }.shuffled().toSet()
            return fallback
        }
        var randomThree: List<String>
        do {
            randomThree = candidates.shuffled().take(3)
        } while (newItem in randomThree)
        return (randomThree + newItem).shuffled().toSet()
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
                        id = currentSabda.sabda.id, table = currentSabda.table
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
                selectedTable = selectedTable, selectedSound = selectedSound, selectedGender = null
            )
        }
    }

    fun parseStringToDeclension(currentSabda: EntireSabda) {
        viewModelScope.launch(Dispatchers.IO) {
            _tableUIState.update { it.copy(result = StringParse.Loading) }
            val result = runCatching {
                val declensionString = currentSabda.sabda.declension
                val declension = Json.decodeFromString<Declension>(declensionString)
                StringParse.Success(declension = declension)
            }.getOrElse { e ->
                Log.d("parseError", e.message.orEmpty(), e)
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

    private fun applyFilter(data: List<EntireSabda>) {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryUIState.update { it.copy(result = FilterState.Loading) }
            val result = runCatching {
                val filteredData = data.filter { entireSabda ->

                    listOfNotNull(
                        categoryUIState.value.selectedTable?.let { it == entireSabda.table },
                        categoryUIState.value.selectedSound?.let { it.name.lowercase() == entireSabda.sabda.sound },
                        categoryUIState.value.selectedGender?.let { it.name.lowercase() == entireSabda.sabda.gender }).all { it }
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