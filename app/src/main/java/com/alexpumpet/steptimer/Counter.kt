package com.alexpumpet.steptimer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CounterState { NEW, PAUSED, STOPPED, FINISHED, PROCESSED }

open class Counter(val step: Long = 1000L, val onEvent: Counter.() -> Unit = {}) {
    private var job: Job? = null

    var currTime: Long = 0L
        private set

    var maxTime: Long = 0L
        private set

    var state = CounterState.NEW
        private set

    var isPaused: Boolean = false

    init {
        event()
    }

    fun start(maxTime: Long = 0L) {
        if (job?.isActive == true) return
        this.currTime = 0L
        this.maxTime = maxTime
        isPaused = false
        runTimer()
    }

    fun pause() {
        if (state == CounterState.NEW) return
        if (job?.isActive == true) {
            job?.cancel()
            isPaused = true
            state = CounterState.PAUSED
        }
        event()
    }

    fun resume() {
        if (state == CounterState.NEW) return
        if (isPaused && (maxTime == 0L || currTime < maxTime)) {
            isPaused = false
            runTimer()
        }
    }

    fun stop() {
        if (state == CounterState.NEW) return
        job?.cancel()
        isPaused = false
        state = CounterState.STOPPED
        event()
    }

    private fun event() {
        onEvent(this)
    }

    private fun runTimer() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while ((maxTime == 0L || currTime < maxTime) && !isPaused) {
                delay(step)
                currTime += step
                state = CounterState.PROCESSED
                event()
            }
            if (maxTime in 1L..currTime && !isPaused) {
                state = CounterState.FINISHED
                event()
            }
        }
    }
}