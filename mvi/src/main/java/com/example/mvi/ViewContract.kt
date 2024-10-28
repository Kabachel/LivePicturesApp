package com.example.mvi

/**
 * Событие пользовательского взаимодействия: нажатие кнопки, ввод текста, отправка формы, свайп, и т.п.
 * Отправляется из слоя UI (Composable-функция) в презентационный слой (ViewModel).
 */
interface ViewEvent

/**
 * Состояние экрана в виде data-класса или sealed-класса: Initial, Loading, Success, Error и т.п.
 * Отправляется из презентационного слоя (ViewModel) в UI слой (Composable-функция).
 */
interface ViewState

/**
 * "Одноразовые", "One-shot" или "One-Time" события или состояния экрана, которые не могут быть выражены
 * через [ViewState]: показ тоста 1 раз, показ тоста N раз, показ снэка, поток real-time значений,
 * скрытие клваиатуры, показ анимации, навигационное событие, и т.п.
 * Отправляется из презентационного слоя (ViewModel) в UI слой (Composable-функция).
 */
interface ViewEffect

/**
 * Реализация-заглушка, позволяет определить вашу [MviViewModel] без использования [ViewEvent].
 */
object NoEvent : ViewEvent

/**
 * Реализация-заглушка, позволяет определить вашу [MviViewModel] без использования [ViewState].
 */
object NoState : ViewState

/**
 * Реализация-заглушка, позволяет определить вашу [MviViewModel] без использования [ViewEffect].
 */
object NoEffects : ViewEffect