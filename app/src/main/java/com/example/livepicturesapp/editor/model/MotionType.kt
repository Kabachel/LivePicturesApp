package com.example.livepicturesapp.editor.model

/**
 * For tracking motion events
 *
 * @property Idle There is not touch
 * @property Down Touch is started
 * @property Move Touch is moved
 * @property Up Touch is ended
 */
enum class MotionType {
    Idle,
    Down,
    Move,
    Up,
}