package com.alexpumpet.steptimer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class ScreenViewModel : ViewModel() {
    private val _state: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Start())
    val state = _state.asStateFlow()

    private var currStep = 0
    private val steps = mutableMapOf<Int, Int>()

    private val counter = Counter {
        if (state == CounterState.PROCESSED)
            processCommand(Command.SetCounter((currTime / 1000L).toInt()))
    }

    fun processCommand(command: Command) {
        when (command) {
            Command.NextStep -> {
                counter.stop()
                currStep++
                steps[currStep] = 0
                _state.value = ScreenState.Process(
                    time = "00:00",
                    mainCaption = currStep.toString()
                )
                counter.start()
            }

            is Command.SetCounter -> {
                steps[currStep] = command.currTime
                _state.value = ScreenState.Process(
                    time = String.format(Locale.getDefault(), "%02d:%02d", command.currTime / 60, command.currTime % 60),
                    mainCaption = currStep.toString()
                )
            }

            Command.Finish -> {
                counter.stop()
                _state.value = ScreenState.Finish(
                    info = info()
                )
                currStep = 0
                steps.clear()
            }
        }
    }

    private fun info(): String {
        val locale = Locale.getDefault()
        val totalTime = steps.values.sum()
        val avgTime = steps.values.average().toInt()
        val minTime = steps.values.min()
        val maxTime = steps.values.max()

        return """
               |LAST RESULTS
               | 
               |Total steps: ${steps.count()}
               |Total time: ${String.format(locale, "%02d:%02d", totalTime / 60, totalTime % 60)}
               |Avg time: ${String.format(locale, "%02d:%02d", avgTime / 60, avgTime % 60)}
               |Min time: ${String.format(locale, "%02d:%02d", minTime / 60, minTime % 60)}
               |Max time: ${String.format(locale, "%02d:%02d", maxTime / 60, maxTime % 60)}
               |
            """
            .trimMargin()
            .let { str ->
                val sb = StringBuilder(str)
                steps.keys.sorted().forEach { step ->
                    steps[step]?.let { stepTime ->
                        sb.append("\nStep $step: ${String.format(locale, "%02d:%02d", stepTime / 60, stepTime % 60)}")
                    }
                }
                sb.toString()
            }
    }

}

sealed interface Command {
    data object NextStep : Command
    data object Finish : Command
    data class SetCounter(val currTime: Int) : Command
}

sealed interface ScreenState {
    val mainCaption: String

    data class Start(override val mainCaption: String = "Start") : ScreenState
    data class Process(val time: String, override val mainCaption: String) : ScreenState
    data class Finish(val info: String, override val mainCaption: String = "Start Next") : ScreenState
}
