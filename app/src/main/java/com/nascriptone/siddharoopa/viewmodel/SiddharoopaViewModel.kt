package com.nascriptone.siddharoopa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.local.QuizQuestion
import com.nascriptone.siddharoopa.data.local.QuizResultMessage
import com.nascriptone.siddharoopa.data.model.CaseName
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.EntireSabda
import com.nascriptone.siddharoopa.data.model.FormName
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.IsFavorite
import com.nascriptone.siddharoopa.data.model.MCQ
import com.nascriptone.siddharoopa.data.model.MTF
import com.nascriptone.siddharoopa.data.model.Phrase
import com.nascriptone.siddharoopa.data.model.QTemplate
import com.nascriptone.siddharoopa.data.model.Sound
import com.nascriptone.siddharoopa.data.model.Table
import com.nascriptone.siddharoopa.data.model.entity.Favorite
import com.nascriptone.siddharoopa.data.model.entity.RestProp
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
import com.nascriptone.siddharoopa.ui.screen.category.CategoryScreenState
import com.nascriptone.siddharoopa.ui.screen.category.FilterState
import com.nascriptone.siddharoopa.ui.screen.favorites.FavoritesScreenState
import com.nascriptone.siddharoopa.ui.screen.home.HomeScreenState
import com.nascriptone.siddharoopa.ui.screen.home.ObserveSabda
import com.nascriptone.siddharoopa.ui.screen.quiz.Action
import com.nascriptone.siddharoopa.ui.screen.quiz.Answer
import com.nascriptone.siddharoopa.ui.screen.quiz.CreationState
import com.nascriptone.siddharoopa.ui.screen.quiz.Dashboard
import com.nascriptone.siddharoopa.ui.screen.quiz.McqGeneratedData
import com.nascriptone.siddharoopa.ui.screen.quiz.McqStats
import com.nascriptone.siddharoopa.ui.screen.quiz.MtfGeneratedData
import com.nascriptone.siddharoopa.ui.screen.quiz.MtfStats
import com.nascriptone.siddharoopa.ui.screen.quiz.Option
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionWithNumber
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionOption
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizMode
import com.nascriptone.siddharoopa.ui.screen.quiz.QuizSectionState
import com.nascriptone.siddharoopa.ui.screen.quiz.Result
import com.nascriptone.siddharoopa.ui.screen.quiz.ValuationState
import com.nascriptone.siddharoopa.ui.screen.settings.SettingsScreenState
import com.nascriptone.siddharoopa.ui.screen.settings.Theme
import com.nascriptone.siddharoopa.ui.screen.table.StringParse
import com.nascriptone.siddharoopa.ui.screen.table.TableScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
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
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


@HiltViewModel
class SiddharoopaViewModel @Inject constructor(
    private val repository: AppRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val resourceProvider: ResourceProvider
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

    fun quizValuation() {
        val data = quizUIState.value.questionList.requireSuccess {
            it.isNotEmpty()
        } ?: run {
            Log.d("quizValuation", "Skipped: question list empty or not in success state")
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            _quizUIState.update { it.copy(result = ValuationState.Calculate) }
            val result = runCatching {
                delay(1000)

                val mcqStates =
                    data.mapNotNull { it.option as? Option.McqOption }.takeIf { it.isNotEmpty() }
                val mtfStates =
                    data.mapNotNull { it.option as? Option.MtfOption }.takeIf { it.isNotEmpty() }
                val mcqStats = mcqStates?.calculateMcqStats()
                val mtfStats = mtfStates?.calculateMtfStats()

                val table = quizUIState.value.table
                val dashboard = getDashboardData(mcqStats, mtfStats, table)

                Result(
                    mcqStats = mcqStats,
                    mtfStats = mtfStats,
                    finalData = data.take(2),
                    dashboard = dashboard
                )
            }.map {
                ValuationState.Success(result = it)
            }.getOrElse { e ->
                Log.d("quizValuation", "Valuation failed", e)
                ValuationState.Error(message = e.message.orEmpty())
            }
            _quizUIState.update { it.copy(result = result) }
        }
    }

    fun updateAnswer(id: Int, action: Action) {
        val currentState = quizUIState.value
        val questionOptions = currentState.questionList.requireSuccess { it.isNotEmpty() } ?: return

        val currentAnswer = currentState.currentAnswer
        val updatedList = questionOptions.mapIndexed { index, questionOption ->
            if (index != id) return@mapIndexed questionOption

            val newState = when (action) {
                Action.SKIP -> questionOption.option
                Action.SUBMIT -> when (val state = questionOption.option) {
                    is Option.McqOption -> {
                        val answer = (currentAnswer as? Answer.Mcq)?.answer
                        state.copy(mcqGeneratedData = state.mcqGeneratedData.copy(answer = answer))
                    }

                    is Option.MtfOption -> {
                        val answer = (currentAnswer as? Answer.Mtf)?.answer
                        state.copy(mtfGeneratedData = state.mtfGeneratedData.copy(answer = answer))
                    }
                }
            }

            questionOption.copy(option = newState)
        }

        _quizUIState.update {
            it.copy(
                questionList = CreationState.Success(updatedList),
                currentAnswer = Answer.Unspecified
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
        viewModelScope.launch(Dispatchers.Default) {
            _quizUIState.update { it.copy(questionList = CreationState.Loading) }
            val result = runCatching {
                val entireSabdaList = entireSabdaList.value
                val userSelectedTable = quizUIState.value.table
                val userSelectedQuestionType = quizUIState.value.quizMode
                val userSelectedQuestionRange = quizUIState.value.questionRange
                val maxMCQ = (userSelectedQuestionRange * 70) / 100
                val chosenTable = entireSabdaList.filter { sabda ->
                    listOfNotNull(
                        userSelectedTable?.let { it == sabda.table }).all { it }
                }
                val randomPickedSabda = chosenTable.shuffled().take(userSelectedQuestionRange)

                val data = randomPickedSabda.mapIndexed { index, entireSabda ->
                    val questionCollection = when (userSelectedQuestionType) {
                        QuizMode.All -> if (index < maxMCQ) QuizQuestion.mcqQuestions
                        else QuizQuestion.mtfQuestions

                        QuizMode.MCQ -> QuizQuestion.mcqQuestions
                        QuizMode.MTF -> QuizQuestion.mtfQuestions
                    }
                    val sabda = entireSabda.sabda
                    val qTemplate = questionCollection.random()
                    getQuestionOption(index, qTemplate, sabda, entireSabdaList)
                }

                CreationState.Success(data = data)
            }.getOrElse { e ->
                Log.d("error", "Question Creation error", e)
                CreationState.Error(e.message.orEmpty())
            }
            _quizUIState.update { it.copy(questionList = result) }
        }
    }

    private fun getQuestionOption(
        index: Int,
        qTemplate: QTemplate,
        sabda: Sabda,
        entireSabdaList: List<EntireSabda>
    ): QuestionOption {

        val template = qTemplate.questionResId
        val allGenders = Gender.entries.toSet()
        val allForms = FormName.entries.toSet()
        val allSabda = entireSabdaList.map { it.sabda }.toSet()
        val allAntas = entireSabdaList.map { it.sabda.anta }.toSet()
        val declension = Json.decodeFromString<Declension>(sabda.declension)

        lateinit var templateKey: Map<String, String>

        val option: Option = when (val result = qTemplate.phrase) {
            is Phrase.McqKey -> {
                val mcqDeepInfo = generateMcqDeepInfo(
                    type = result.type,
                    sabda = sabda,
                    declension = declension,
                    genders = allGenders,
                    anta = allAntas,
                    allForms = allForms
                )
                templateKey = mcqDeepInfo.templateKey
                mcqDeepInfo.option
            }

            is Phrase.MtfKey -> {
                val mtfData = generateMtfDeepInfo(
                    type = result.type,
                    sabda = sabda,
                    declension = declension,
                    genders = allGenders,
                    allForms = allForms,
                    allSabda = allSabda,
                )
                templateKey = mtfData.templateKey
                mtfData.option
            }
        }

        val getTemplateString = resourceProvider.getString(template)
        val regexQuestion: String = replacePlaceholders(getTemplateString, templateKey)
        val questionWithNumber = QuestionWithNumber(
            number = index,
            question = regexQuestion
        )
        return QuestionOption(questionWithNumber = questionWithNumber, option = option)
    }

    private fun generateMcqDeepInfo(
        type: MCQ,
        sabda: Sabda,
        declension: Declension,
        genders: Set<Gender>,
        anta: Set<String>,
        allForms: Set<FormName>
    ): DeepInfo {

        lateinit var templateKey: Map<String, String>
        lateinit var options: Set<String>
        lateinit var trueOption: String

        val allWords = declension.values.flatMap { it.values }

        when (type) {
            MCQ.ONE, MCQ.TWO, MCQ.THREE, MCQ.EIGHT -> {

                var temp: String?
                var randomCase: CaseName
                var randomForm: FormName
                do {
                    randomCase = declension.keys.random()
                    randomForm = declension.getValue(randomCase).keys.random()
                    temp = declension.getValue(randomCase).getValue(randomForm)
                } while (temp == null)


                trueOption = requireNotNull(temp)
                options = getUniqueShuffledSet(allWords, trueOption)
                templateKey = mapOf(
                    "vibhakti" to resourceProvider.getString(randomCase.sktName),
                    "vachana" to resourceProvider.getString(randomForm.sktName),
                    "sabda" to sabda.word
                )

            }

            MCQ.FOUR -> {
                val listOfGenders = genders.map { resourceProvider.getString(it.sktName) }.toList()
                val fEnum = Gender.valueOf(sabda.gender.uppercase())
                trueOption = resourceProvider.getString(fEnum.sktName)
                options = getUniqueShuffledSet(listOfGenders, trueOption)
                templateKey = mapOf(
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

                trueOption = resourceProvider.getString(selectedForm.sktName)
                options = allForms.map { resourceProvider.getString(it.sktName) }.shuffled().toSet()
                templateKey = mapOf(
                    "form" to chosenFormValue, "sabda" to sabda.word
                )

            }

            MCQ.SIX -> {

                trueOption = ""
                options = emptySet()
                templateKey = emptyMap()
            } // Sixth Question business logic will set after the additional sabda insertion.

            MCQ.SEVEN -> {

                val listOfAntas = anta.toList()
                trueOption = sabda.anta
                options = getUniqueShuffledSet(listOfAntas, trueOption)
                templateKey = mapOf(
                    "sabda" to sabda.word
                )
            }
        }

        val data = McqGeneratedData(
            options = options,
            trueOption = trueOption,
        )

        return DeepInfo(
            templateKey = templateKey,
            option = Option.McqOption(data)
        )
    }

    private fun generateMtfDeepInfo(
        type: MTF,
        sabda: Sabda,
        declension: Declension,
        genders: Set<Gender>,
        allForms: Set<FormName>,
        allSabda: Set<Sabda>,
    ): DeepInfo {

        var templateKey = emptyMap<String, String>()
        lateinit var options: Map<String?, String>
        lateinit var trueOption: List<String>


        val shuffledDeclension = declension.toList().shuffled().toMap()

        when (type) {
            MTF.ONE, MTF.TWO -> {

                lateinit var temp: Map<String, String>
                var extraOption: String? = null
                val forms = allForms.shuffled()

                for (form in forms) {
                    val formSet = declension.mapNotNull { it.value[form] }.toMutableSet()
                    val validEntries = shuffledDeclension.filterValues {
                        val currentForm = it[form]
                        currentForm != null && formSet.remove(currentForm)
                    }.mapKeys { resourceProvider.getString(it.key.sktName) }
                    if (validEntries.size >= 4) {
                        val correctOptions = validEntries.entries.take(3)
                        val distractorCandidates = validEntries.entries.drop(3)
                        temp = correctOptions.associate { (k, v) ->
                            when (type) {
                                MTF.ONE -> k to v[form]!!
                                else -> v[form]!! to k
                            }
                        }
                        val candidate = distractorCandidates.randomOrNull()
                        extraOption = when (type) {
                            MTF.ONE -> candidate?.value?.get(form)
                            else -> candidate?.key
                        }
                        break
                    }
                }


                trueOption = temp.values.toList()
                val shuffled = (if (extraOption != null) (trueOption + extraOption)
                else trueOption).shuffled()
                val keys = if (extraOption != null) temp.keys + null else temp.keys
                options = keys.zip(shuffled).toMap()
                templateKey = mapOf(
                    "sabda" to sabda.word
                )

            }

            MTF.THREE -> {

                trueOption = emptyList()
                options = emptyMap()
            } // Third Question business logic will set after the additional data insertion

            MTF.FOUR -> {

                lateinit var temp: Map<String, String>
                val sabdaByGender = allSabda.groupBy { it.gender }
                val shuffledGenders = genders.shuffled()
                temp = shuffledGenders.mapNotNull { gender ->
                    sabdaByGender[gender.name.lowercase()]?.random()
                }.associate { sabda ->
                    val resId = Gender.valueOf(sabda.gender.uppercase()).sktName
                    resourceProvider.getString(resId) to sabda.word
                }


                trueOption = temp.values.toList()
                val shuffledValues = trueOption.shuffled()
                options = temp.keys.zip(shuffledValues).toMap()

            }

            MTF.FIVE -> {

                lateinit var temp: Map<String, String>
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
                        temp = keySet.shuffled().associate { key ->
                            val currentSet = currentDeclension.values.mapNotNull { it[key] }.toSet()
                            val available = currentSet.minus(previousValues)
                            require(available.isNotEmpty()) {
                                "No unique value available for key: ${key.name}"
                            }
                            val chosen = available.random()
                            previousValues.add(chosen)
                            resourceProvider.getString(key.sktName) to chosen
                        }
                        break
                    }
                }

                trueOption = temp.values.toList()
                val shufflesValues = trueOption.shuffled()
                options = temp.keys.zip(shufflesValues).toMap()
                templateKey = mapOf(
                    "sabda" to sabda.word
                )
            }
        }

        val data = MtfGeneratedData(
            options = options,
            trueOption = trueOption
        )

        return DeepInfo(
            templateKey = templateKey,
            option = Option.MtfOption(data)
        )
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
                table = table,
            )
        }
    }

    fun updateQuizQuestionType(type: QuizMode) {
        _quizUIState.update {
            it.copy(
                quizMode = type,
            )
        }
    }

    fun updateQuizQuestionRange(range: Int) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.Default) {
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
        viewModelScope.launch(Dispatchers.Default) {
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

private fun List<Option.McqOption>.calculateMcqStats(): McqStats {
    val total = size
    val attended = count { it.mcqGeneratedData.answer != null }
    val correct = count { it.mcqGeneratedData.answer == it.mcqGeneratedData.trueOption }
    val wrong = attended - correct
    val skipped = total - attended
    val score = correct * 2

    return McqStats(
        totalQuestions = total,
        attended = attended,
        skipped = skipped,
        correct = correct,
        wrong = wrong,
        score = score
    )
}

private fun List<Option.MtfOption>.calculateMtfStats(): MtfStats {
    val totalSet = size
    val totalPairs = totalSet * 3

    var correct = 0
    var attended = 0

    forEach { state ->
        val userAnswers = state.mtfGeneratedData.answer
        val correctAnswers = state.mtfGeneratedData.trueOption

        if (userAnswers != null) {
            attended++
            correct += userAnswers.zip(correctAnswers).count { (a, b) -> a == b }
        }
    }

    val wrong = (attended * 3) - correct
    val skipped = totalSet - attended

    return MtfStats(
        totalSet = totalSet,
        totalPairs = totalPairs,
        attended = attended,
        skipped = skipped,
        correct = correct,
        wrong = wrong,
        score = correct
    )
}

private fun getDashboardData(mcqStats: McqStats?, mtfStats: MtfStats?, table: Table?): Dashboard {

    val score = (mcqStats?.score ?: 0) + (mtfStats?.score ?: 0)
    val totalPossibleScore = (((mcqStats?.totalQuestions ?: 0) * 2) + (mtfStats?.totalPairs ?: 0))
    val accuracy = getQuizAccuracy(score, totalPossibleScore)
    val message = getMessage(accuracy)

    return Dashboard(
        accuracy = accuracy,
        message = message,
        table = table,
        score = score,
        totalPossibleScore = totalPossibleScore
    )
}

private data class DeepInfo(
    val templateKey: Map<String, String>,
    val option: Option
)

private const val PATTERN = "\\{(\\w+)\\}"
private val placeholderRegex = Regex(PATTERN)
private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return placeholderRegex.replace(template) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}

private fun getQuizAccuracy(es: Int, tps: Int): Float {
    if (tps == 0) return 0f
    return BigDecimal(es * 100).divide(BigDecimal(tps), 1, RoundingMode.HALF_UP).toFloat()
}

private fun getMessage(accuracy: Float): Int {
    val p = 100 / 4
    val i = (accuracy / p).toInt().coerceAtMost(3)
    return QuizResultMessage.messageList[i].random()
}

inline fun <T> CreationState<T>.requireSuccess(predicate: (T) -> Boolean): T? {
    val success = this as? CreationState.Success<T> ?: return null
    return if (predicate(success.data)) success.data else null
}