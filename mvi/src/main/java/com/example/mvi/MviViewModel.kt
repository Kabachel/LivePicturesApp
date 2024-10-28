package com.example.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * [MviViewModel] базовый класс ViewModel, позволяющий типобезопасно настроить работу по контракту MVI.
 *
 * @author Данила Шабанов on 12.08.2024
 */
abstract class MviViewModel<
    Event : ViewEvent,
    State : ViewState,
    Effect : ViewEffect,
    > : ViewModel() {

    private val initialState: State by lazy { setInitialState() }

    private val mutableStateFlow = MutableStateFlow(initialState)
    val stateFlow: StateFlow<State> = mutableStateFlow.asStateFlow()

    val state: State
        get() = stateFlow.value

    private val mutableEffectFlow = SingleSharedFlow<Effect>()
    val effectFlow: SharedFlow<Effect> = mutableEffectFlow.asSharedFlow()

    /**
     * Установка начального состояния экрана.
     */
    abstract fun setInitialState(): State

    /**
     * Отправить новый [ViewEvent] экрана в презентационный слой.
     */
    abstract fun handleEvent(event: Event)

    /**
     * Отправить новый [ViewState] экрана в UI слой: Composable-функцию.
     *
     * Доступно взаимодействие с текущим стейтом в стиле "reduce": State -> New State,
     * например: setState { copy(isLoading = true) }, если [ViewState] определён как data-класс.
     */
    protected fun setState(reducer: State.() -> State) {
        mutableStateFlow.update(reducer)
    }

    /**
     * Отправить [ViewEffect] в UI слой: Composable-функцию.
     */
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        mutableEffectFlow.tryEmit(effectValue)
    }
}