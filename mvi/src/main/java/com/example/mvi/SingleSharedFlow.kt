package com.example.mvi

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Не хранит старые значения для новых подписчиков.
 * Используется для потока одноразовых событий - тосты, уведомления и тп.
 *
 * @author Данила Шабанов on 12.08.2024
 */
@Suppress("FunctionName")
internal fun <T> SingleSharedFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
)