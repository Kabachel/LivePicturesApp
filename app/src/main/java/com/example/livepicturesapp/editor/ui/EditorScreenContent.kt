package com.example.livepicturesapp.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.livepicturesapp.editor.model.DrawingPath
import com.example.livepicturesapp.editor.model.Frame
import com.example.livepicturesapp.editor.model.InteractType
import com.example.livepicturesapp.editor.model.MotionType
import com.example.livepicturesapp.editor.model.PathProperties
import com.example.livepicturesapp.editor.repository.FrameRepository
import com.example.livepicturesapp.editor.ui.dialogs.ColorPickerDialog
import com.example.livepicturesapp.editor.ui.dialogs.PathPropertiesDialog
import com.example.livepicturesapp.editor.ui.dialogs.ShowFramesDialog
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme
import java.util.UUID

val frameRepository = FrameRepository()

@Composable
fun EditorScreenContent() {
    val frameUuid = remember { mutableStateOf<UUID>(UUID.randomUUID()) }
    val paths = remember { mutableStateListOf<DrawingPath>() }
    val pathsUndone = remember { mutableStateListOf<DrawingPath>() }
    val motionType = remember { mutableStateOf(MotionType.Idle) }
    val currentPosition = remember { mutableStateOf(Offset.Unspecified) }
    val previousPosition = remember { mutableStateOf(Offset.Unspecified) }
    val interactMode = remember { mutableStateOf(InteractType.Move) }
    val currentPath = remember { mutableStateOf(Path()) }
    val currentPathProperty = remember { mutableStateOf(PathProperties()) }
    val showPropertiesDialog = remember { mutableStateOf(false) }
    val showColorPickerDialog = remember { mutableStateOf(false) }
    val showFramesDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val frame = Frame(emptyList())
        frameUuid.value = frame.uuid
        frameRepository.addFrame(frame.uuid, frame)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LivePicturesTheme.colors.black)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EmptySpacer(16.dp)
        Header(
            isPathsEmpty = paths.isEmpty(),
            isPathsUndoneEmpty = pathsUndone.isEmpty(),
            isDeleteFrameEnabled = frameRepository.getFrames().size > 1,
            onUndoClick = {
                if (paths.isNotEmpty()) {
                    val lastDrawingPath = paths.last()
                    paths.remove(lastDrawingPath)
                    pathsUndone += DrawingPath(lastDrawingPath.path, lastDrawingPath.properties.copy())
                }
            },
            onRedoClick = {
                if (pathsUndone.isNotEmpty()) {
                    val lastDrawingPath = pathsUndone.last()
                    pathsUndone.removeLast()
                    paths += DrawingPath(lastDrawingPath.path, lastDrawingPath.properties.copy())
                }
            },
            onDeleteFrameClick = {
                if (frameRepository.getFrames().size > 1) {
                    val newFrame = frameRepository.deleteFrameByUuid(frameUuid.value)
                    if (newFrame != null) {
                        paths.clear()
                        pathsUndone.clear()
                        currentPath.value = Path()
                        currentPosition.value = Offset.Unspecified
                        previousPosition.value = Offset.Unspecified
                        currentPathProperty.value = currentPathProperty.value.copy()

                        frameUuid.value = newFrame.uuid
                        paths += newFrame.drawingPaths
                    } else {
                        println("deleteFrameByUuid(${frameUuid.value}) returns null!")
                    }
                }
            },
            onCreateFrameClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                val newFrame = Frame(emptyList())
                frameRepository.addFrame(frameUuid.value, newFrame)
                frameUuid.value = newFrame.uuid

                paths.clear()
                pathsUndone.clear()
                currentPath.value = Path()
                currentPosition.value = Offset.Unspecified
                previousPosition.value = Offset.Unspecified
                currentPathProperty.value = currentPathProperty.value.copy()
            },
            onShowFramesClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                showFramesDialog.value = !showFramesDialog.value
            },
            onPauseClick = {},
            onPlayClick = {},
        )
        EmptySpacer(32.dp)
        DrawingArea(paths, pathsUndone, motionType, currentPosition, previousPosition, interactMode, currentPath, currentPathProperty)
        EmptySpacer(22.dp)

        Footer(
            interactType = interactMode.value,
            selectedColor = currentPathProperty.value.color,
            showPathProperties = showPropertiesDialog.value,
            showColorPicker = showColorPickerDialog.value,
            onPencilClick = {
                if (interactMode.value == InteractType.Draw) {
                    interactMode.value = InteractType.Move
                } else {
                    interactMode.value = InteractType.Draw
                }
                currentPathProperty.value = currentPathProperty.value.copy(isErase = false)
            },
            onBrushClick = { showPropertiesDialog.value = !showPropertiesDialog.value },
            onEraseClick = {
                if (interactMode.value == InteractType.Erase) {
                    interactMode.value = InteractType.Move
                    currentPathProperty.value = currentPathProperty.value.copy(isErase = false)
                } else {
                    interactMode.value = InteractType.Erase
                    currentPathProperty.value = currentPathProperty.value.copy(isErase = true)
                }
            },
            onFiguresClick = {},
            onColorClick = { showColorPickerDialog.value = !showColorPickerDialog.value },
        )
        EmptySpacer(16.dp)
    }

    PathPropertiesDialog(currentPathProperty.value, showPropertiesDialog)
    ColorPickerDialog(currentPathProperty.value, showColorPickerDialog)
    ShowFramesDialog(
        frames = frameRepository.getFrames(),
        showFramesDialog = showFramesDialog,
        onFrameSelected = { selectedFrame ->
            frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())

            paths.clear()
            pathsUndone.clear()
            currentPath.value = Path()
            currentPosition.value = Offset.Unspecified
            previousPosition.value = Offset.Unspecified
            currentPathProperty.value = currentPathProperty.value.copy()

            frameUuid.value = selectedFrame.uuid
            paths += selectedFrame.drawingPaths
        }
    )
}

@Preview
@Composable
private fun EditorScreenContentPreview() {
    LivePicturesTheme {
        EditorScreenContent()
    }
}
