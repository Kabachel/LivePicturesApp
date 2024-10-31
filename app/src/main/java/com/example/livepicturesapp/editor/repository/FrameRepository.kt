package com.example.livepicturesapp.editor.repository

import com.example.livepicturesapp.editor.model.DrawingPath
import com.example.livepicturesapp.editor.model.Frame
import java.util.UUID

/**
 * Storage for list of [Frame]
 */
class FrameRepository {
    private val frames: MutableList<Frame> = mutableListOf()

    // TODO Add insert after current frame
    /**
     * Add element after given uuid, if uuid is not found -> add to end of the list
     */
    fun addFrame(uuid: UUID, frame: Frame) {
        val index = frames.withIndex().find { it.value.uuid == uuid }?.index
        if (index != null) {
            frames.add(index + 1, frame)
        } else {
            frames += frame
        }
    }

    /**
     * @return new frame to show
     */
    fun deleteFrameByUuid(uuid: UUID): Frame? {
        val index = frames.withIndex().find { it.value.uuid == uuid }?.index
        if (index != null) {
            frames.removeAt(index)
        } else return null

        return if (index > 0) frames[index - 1] else frames[index]
    }

    fun updateDrawingPathsByUuid(uuid: UUID, drawingPaths: List<DrawingPath>) {
        frames.replaceAll {
            if (it.uuid == uuid) {
                it.copy(drawingPaths = drawingPaths)
            } else it
        }
    }

    fun getFrames(): List<Frame> = frames
}