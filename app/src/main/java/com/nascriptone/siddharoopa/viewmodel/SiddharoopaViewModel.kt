package com.nascriptone.siddharoopa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nascriptone.siddharoopa.data.local.QuizResultMessage
import com.nascriptone.siddharoopa.data.model.CaseName
import com.nascriptone.siddharoopa.data.model.Category
import com.nascriptone.siddharoopa.data.model.Declension
import com.nascriptone.siddharoopa.data.model.FormName
import com.nascriptone.siddharoopa.data.model.Gender
import com.nascriptone.siddharoopa.data.model.MCQ
import com.nascriptone.siddharoopa.data.model.MTF
import com.nascriptone.siddharoopa.data.model.Phrase
import com.nascriptone.siddharoopa.data.model.QTemplate
import com.nascriptone.siddharoopa.data.model.entity.Sabda
import com.nascriptone.siddharoopa.data.repository.AppRepository
import com.nascriptone.siddharoopa.data.repository.UserPreferencesRepository
import com.nascriptone.siddharoopa.domain.utils.ResourceProvider
import com.nascriptone.siddharoopa.ui.screen.quiz.Answer
import com.nascriptone.siddharoopa.ui.screen.quiz.Dashboard
import com.nascriptone.siddharoopa.ui.screen.quiz.McqGeneratedData
import com.nascriptone.siddharoopa.ui.screen.quiz.McqStats
import com.nascriptone.siddharoopa.ui.screen.quiz.Mode
import com.nascriptone.siddharoopa.ui.screen.quiz.MtfGeneratedData
import com.nascriptone.siddharoopa.ui.screen.quiz.MtfStats
import com.nascriptone.siddharoopa.ui.screen.quiz.Option
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionOption
import com.nascriptone.siddharoopa.ui.screen.quiz.QuestionWithNumber
import com.nascriptone.siddharoopa.ui.screen.settings.AppPreferencesState
import com.nascriptone.siddharoopa.ui.screen.settings.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class SiddharoopaViewModel @Inject constructor(
    private val repository: AppRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

//    private val _quizUIState = MutableStateFlow(QuizSectionState())
//    val quizUIState: StateFlow<QuizSectionState> = _quizUIState.asStateFlow()


    val appPreferencesState: StateFlow<AppPreferencesState> =
        preferencesRepository.currentTheme.map {
            AppPreferencesState(currentTheme = it)
        }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AppPreferencesState()
        )

    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesRepository.changeTheme(theme)
        }
    }

//    fun quizValuation() {
//        val data = quizUIState.value.questionOptionList.requireSuccess {
//            it.isNotEmpty()
//        } ?: run {
//            Log.d("quizValuation", "Skipped: question list empty or not in success state")
//            return
//        }
//        viewModelScope.launch(Dispatchers.Default) {
//            _quizUIState.update { it.copy(result = ValuationState.Calculate) }
//            val result = runCatching {
//                val mcqStates =
//                    data.mapNotNull { it.option as? Option.McqOption }.takeIf { it.isNotEmpty() }
//                val mtfStates =
//                    data.mapNotNull { it.option as? Option.MtfOption }.takeIf { it.isNotEmpty() }
//                val mcqStats = mcqStates?.calculateMcqStats()
//                val mtfStats = mtfStates?.calculateMtfStats()
//                val category = filter.value.selectedCategory
//                val dashboard = getDashboardData(mcqStats, mtfStats, category)
//                Result(
//                    mcqStats = mcqStats,
//                    mtfStats = mtfStats,
//                    finalData = data.take(2),
//                    dashboard = dashboard
//                )
//            }.map {
//                ValuationState.Success(result = it)
//            }.getOrElse { e ->
//                Log.d("quizValuation", "Valuation failed", e)
//                ValuationState.Error(message = e.message.orEmpty())
//            }
//            _quizUIState.update { it.copy(result = result) }
//        }
//    }

//    fun updateAnswer(id: Int, action: Action) {
//        val currentState = quizUIState.value
//        val questionOptions =
//            currentState.questionOptionList.requireSuccess { it.isNotEmpty() } ?: return
//
//        val currentAnswer = currentState.currentAnswer
//        val updatedList = questionOptions.mapIndexed { index, questionOption ->
//            if (index != id) return@mapIndexed questionOption
//
//            val newState = when (action) {
//                Action.SKIP -> questionOption.option
//                Action.SUBMIT -> when (val state = questionOption.option) {
//                    is Option.McqOption -> {
//                        val answer = (currentAnswer as? Answer.Mcq)?.answer
//                        state.copy(mcqGeneratedData = state.mcqGeneratedData.copy(answer = answer))
//                    }
//
//                    is Option.MtfOption -> {
//                        val answer = (currentAnswer as? Answer.Mtf)?.answer
//                        state.copy(mtfGeneratedData = state.mtfGeneratedData.copy(answer = answer))
//                    }
//                }
//            }
//
//            questionOption.copy(option = newState)
//        }
//
//        _quizUIState.update {
//            it.copy(
//                questionOptionList = CreationState.Success(updatedList),
//                currentAnswer = Answer.Unspecified
//            )
//        }
//    }

//
//    fun toggleSelectedId(id: Int) {
//        if (!_uiState.value.isSelectMode) enterSelectionMode(SelectionTrigger.CARD)
//        _uiState.update { it.copy(selectedIds = it.selectedIds.toggleInSet(id)) }
//        if (_uiState.value.selectedIds.isEmpty() &&
//            _uiState.value.selectionTrigger == SelectionTrigger.CARD
//        ) exitSelectionMode()
//    }
//
//    fun toggleSelectionMode() {
//        if (_uiState.value.isSelectMode) exitSelectionMode()
//        else enterSelectionMode(SelectionTrigger.TOOLBAR)
//    }
//
//    private fun enterSelectionMode(trigger: SelectionTrigger) {
//        _uiState.update {
//            it.copy(
//                isSelectMode = true,
//                selectionTrigger = trigger
//            )
//        }
//    }
//
//    private fun exitSelectionMode() {
//        _uiState.update {
//            it.copy(
//                isSelectMode = false,
//                selectedIds = emptySet(),
//                selectionTrigger = SelectionTrigger.NONE
//            )
//        }
//    }

//    private fun <T> Set<T>.toggleInSet(item: T): Set<T> =
//        if (item in this) this - item else this + item

    fun updateCurrentAnswer(answer: Answer) {
//        _quizUIState.update {
//            it.copy(
//                currentAnswer = answer
//            )
//        }
    }

//    fun createQuizQuestions() {
//        viewModelScope.launch(Dispatchers.Default) {
//            _quizUIState.update { it.copy(questionOptionList = CreationState.Loading) }
//            val result = runCatching {
//                val list = sabdaList.value
//                val quizMode = quizUIState.value.quizMode
//                val questionRange = quizUIState.value.questionRange
//                val maxMCQ = (questionRange * 70) / 100
//                val filteredSabda = filterWith().toQuestionRange(questionRange)
//                val data: List<QuestionOption> = List(questionRange) { i ->
//                    val sabda = filteredSabda[i]
//                    val questionCollection = when (quizMode) {
//                        QuizMode.All -> if (i < maxMCQ) QuizQuestion.mcqQuestions
//                        else QuizQuestion.mtfQuestions
//
//                        QuizMode.MCQ -> QuizQuestion.mcqQuestions
//                        QuizMode.MTF -> QuizQuestion.mtfQuestions
//                    }
//                    val qTemplate = questionCollection.random()
//                    getQuestionOption(i, qTemplate, sabda, list)
//                }
//                CreationState.Success(data = data)
//            }.getOrElse { e ->
//                Log.d("error", "Question Creation error", e)
//                CreationState.Error(e.message.orEmpty())
//            }
//            _quizUIState.update { it.copy(questionOptionList = result) }
//        }
//    }

    private fun getQuestionOption(
        index: Int, qTemplate: QTemplate, sabda: Sabda, list: List<Sabda>
    ): QuestionOption {

        val template = qTemplate.questionResId
        val allGenders = Gender.entries.toSet()
        val allForms = FormName.entries.toSet()
        val allAntas = list.map { it.anta }.toSet()
        val declension = sabda.declension

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
                    list = list,
                )
                templateKey = mtfData.templateKey
                mtfData.option
            }
        }

        val getTemplateString = resourceProvider.getString(template)
        val regexQuestion: String = replacePlaceholders(getTemplateString, templateKey)
        val questionWithNumber = QuestionWithNumber(
            number = index, question = regexQuestion
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
                    "vibhakti" to resourceProvider.getString(randomCase.skt),
                    "vachana" to resourceProvider.getString(randomForm.skt),
                    "sabda" to sabda.word
                )

            }

            MCQ.FOUR -> {
                val listOfGenders = genders.map { resourceProvider.getString(it.skt) }.toList()
                trueOption = resourceProvider.getString(sabda.gender.skt)
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

                trueOption = resourceProvider.getString(selectedForm.skt)
                options = allForms.map { resourceProvider.getString(it.skt) }.shuffled().toSet()
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
            templateKey = templateKey, option = Option.McqOption(data)
        )
    }

    private fun generateMtfDeepInfo(
        type: MTF,
        sabda: Sabda,
        declension: Declension,
        genders: Set<Gender>,
        allForms: Set<FormName>,
        list: List<Sabda>,
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
                    }.mapKeys { resourceProvider.getString(it.key.skt) }
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
                val sabdaByGender = list.groupBy { it.gender }
                val shuffledGenders = genders.shuffled()
                temp = shuffledGenders.mapNotNull { gender ->
                    sabdaByGender[gender]?.random()
                }.associate { sabda ->
                    val resId = sabda.gender.skt
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
                        val currentSabda = list.find {
                            it.declension == currentDeclension
                        } ?: error("Current declension not found in allSabda")
                        invalidDeclensionSet.add(currentSabda)
                        val remaining = list.subtract(invalidDeclensionSet)
                        if (remaining.isEmpty()) break
                        val randomSabda = remaining.random()
                        val newDeclension = randomSabda.declension
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
                            resourceProvider.getString(key.skt) to chosen
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
            options = options, trueOption = trueOption
        )

        return DeepInfo(
            templateKey = templateKey, option = Option.MtfOption(data)
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

    fun updateQuizQuestionType(type: Mode) {
//        _quizUIState.update {
//            it.copy(
//                mode = type,
//            )
//        }
    }

    fun updateQuizQuestionRange(range: Int) {
//        _quizUIState.update {
//            it.copy(
//                range = range,
//            )
//        }
    }

//    fun deleteAllItemFromFavorite() {
//        removeItemsFromFavorite(_uiState.value.selectedIds)
//    }

    private fun removeItemsFromFavorite(ids: Set<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                repository.removeItemsFromFavorite(ids)
            }.getOrElse {
                Log.d("ERROR", "Remove Sabda Error", it)
            }
        }
    }

    fun toggleFavoriteSabda(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { repository.toggleFavorite(id, System.currentTimeMillis()) }.getOrElse {
                Log.d("ERROR", "Toggle Sabda Error", it)
            }
        }
    }
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

private fun getDashboardData(
    mcqStats: McqStats?, mtfStats: MtfStats?, category: Category?
): Dashboard {

    val score = (mcqStats?.score ?: 0) + (mtfStats?.score ?: 0)
    val totalPossibleScore = (((mcqStats?.totalQuestions ?: 0) * 2) + (mtfStats?.totalPairs ?: 0))
    val accuracy = getQuizAccuracy(score, totalPossibleScore)
    val message = getMessage(accuracy)

    return Dashboard(
        accuracy = accuracy,
        message = message,
        category = category,
        score = score,
        totalPossibleScore = totalPossibleScore
    )
}

private data class DeepInfo(
    val templateKey: Map<String, String>, val option: Option
)

private const val PATTERN = "\\{(\\w+)\\}"
private val placeholderRegex = Regex(PATTERN)
private fun replacePlaceholders(template: String, values: Map<String, String>): String {
    return placeholderRegex.replace(template) { match ->
        values[match.groupValues[1]] ?: match.value
    }
}

private fun <T> List<T>.toQuestionRange(range: Int): List<T> {
    if (isEmpty()) return emptyList()

    return if (size >= range) {
        this.shuffled().take(range)
    } else {
        val result = this.shuffled().toMutableList()
        repeat(range - size) {
            result += this.random()
        }
        result
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