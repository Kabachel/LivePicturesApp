package com.example.livepicturesapp.editor

import com.example.mvi.ViewEvent
import com.example.mvi.ViewState

sealed interface State : ViewState {
    data object Content : State
}

sealed interface Event : ViewEvent {
    /* Header */
    data object Undo : Event
    data object Redo : Event
    data object DeleteFrame : Event
    data object CreateFrame : Event
    data object ShowFrames : Event
    data object PauseAnimation : Event
    data object PlayAnimation : Event

    /* Footer */
    data object ChoosePencil : Event
    data object ChooseBrush : Event
    data object ChooseErase : Event
    data object ChooseFigure : Event
    data object ChooseColor : Event
}