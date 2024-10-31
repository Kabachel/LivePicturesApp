package com.example.livepicturesapp.editor.repository

import com.example.livepicturesapp.editor.model.Frame

/**
 * Storage for list of [Frame]
 */
class FrameRepository {
    private val frames: MutableList<Frame> = mutableListOf()

    fun addFrame(frame: Frame) {
        frames += frame
    }

    fun deleteFrame(frame: Frame): Boolean {
        return frames.remove(frame)
    }

    fun getFrames(): List<Frame> = frames
}