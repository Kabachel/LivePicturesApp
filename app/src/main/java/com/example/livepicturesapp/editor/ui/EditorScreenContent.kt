package com.example.livepicturesapp.editor.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.livepicturesapp.editor.ui.components.DrawingArea
import com.example.livepicturesapp.editor.ui.components.Footer
import com.example.livepicturesapp.editor.ui.components.Header
import com.example.livepicturesapp.editor.ui.dialogs.ButtonParams
import com.example.livepicturesapp.editor.ui.dialogs.ColorPickerDialog
import com.example.livepicturesapp.editor.ui.dialogs.ConfirmActionDialog
import com.example.livepicturesapp.editor.ui.dialogs.ConfirmActionDialogParams
import com.example.livepicturesapp.editor.ui.dialogs.PathPropertiesDialog
import com.example.livepicturesapp.editor.ui.dialogs.SettingsDialog
import com.example.livepicturesapp.editor.ui.dialogs.ShowFramesDialog
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import java.util.UUID

val frameRepository = FrameRepository()

internal var PREVIOUS_FRAMES_VISIBLE_COUNT = 2
internal var ANIMATION_DELAY_BETWEEN_FRAMES = 500L

private const val TAG = "EditorScreenContent"

// TODO do less recompositions, do split on components and less stateful
@Composable
fun EditorScreenContent() {
    val frameUuid = remember { mutableStateOf<UUID>(UUID.randomUUID()) }
    val previousFrames = remember { mutableStateListOf<Frame>() }
    val paths = remember { mutableStateListOf<DrawingPath>() }
    val pathsUndone = remember { mutableStateListOf<DrawingPath>() }
    val motionType = remember { mutableStateOf(MotionType.Idle) }
    val currentPosition = remember { mutableStateOf(Offset.Unspecified) }
    val previousPosition = remember { mutableStateOf(Offset.Unspecified) }
    val interactMode = remember { mutableStateOf(InteractType.Draw) }
    val currentPath = remember { mutableStateOf(Path()) }
    val currentPathProperty = remember { mutableStateOf(PathProperties()) }
    val showPropertiesDialog = remember { mutableStateOf(false) }
    val showColorPickerDialog = remember { mutableStateOf(false) }
    val showFramesDialog = remember { mutableStateOf(false) }
    val showConfirmDeleteFrameDialog = remember { mutableStateOf(false) }
    val showConfirmDeleteAllFramesDialog = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }
    val isAnimationShowing = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val frame = Frame(emptyList())
        frameUuid.value = frame.uuid
        frameRepository.addFrame(frame.uuid, frame)
    }

    LaunchedEffect(key1 = isAnimationShowing.value) {
        if (isAnimationShowing.value) {
            previousFrames.clear()
            // TODO maybe do not from start? but with current open frame?
            var i = 0
            val allFrames = frameRepository.getFrames()
            while (true) {
                val currentFrame = allFrames[i]
                paths.clear()
                paths += currentFrame.drawingPaths
                frameUuid.value = currentFrame.uuid
                delay(ANIMATION_DELAY_BETWEEN_FRAMES)
                if (i == allFrames.lastIndex) i = 0 else i++
            }
        } else {
            cancel()
            val currentFrame = frameRepository.getFrames().find { it.uuid == frameUuid.value }
            currentFrame?.let { showPreviousFrames(it, previousFrames) }
        }
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
            isAnimationShowing = isAnimationShowing.value,
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
            onDeleteFrameClick = { showConfirmDeleteFrameDialog.value = true },
            onCreateFrameClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                val newFrame = Frame(emptyList())
                frameRepository.addFrame(frameUuid.value, newFrame)
                frameUuid.value = newFrame.uuid

                paths.clear()
                previousFrames.clear()
                pathsUndone.clear()
                currentPath.value = Path()
                currentPosition.value = Offset.Unspecified
                previousPosition.value = Offset.Unspecified
                currentPathProperty.value = currentPathProperty.value.copy()

                showPreviousFrames(newFrame, previousFrames)
            },
            onDuplicateFrameClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                val newFrame = Frame(paths.toList())
                frameRepository.addFrame(frameUuid.value, newFrame)
                frameUuid.value = newFrame.uuid

                paths.clear()
                previousFrames.clear()
                pathsUndone.clear()
                currentPath.value = Path()
                currentPosition.value = Offset.Unspecified
                previousPosition.value = Offset.Unspecified
                currentPathProperty.value = currentPathProperty.value.copy()

                paths += newFrame.drawingPaths
                showPreviousFrames(newFrame, previousFrames)
            },
            onShowFramesClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                showFramesDialog.value = !showFramesDialog.value
            },
            onPauseClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                isAnimationShowing.value = false
            },
            onPlayClick = {
                frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())
                isAnimationShowing.value = true
            },
        )
        EmptySpacer(32.dp)
        DrawingArea(
            paths,
            previousFrames,
            pathsUndone,
            motionType,
            currentPosition,
            previousPosition,
            interactMode,
            currentPath,
            currentPathProperty,
            isAnimationShowing.value
        )
        EmptySpacer(22.dp)
        Footer(
            interactType = interactMode.value,
            selectedColor = currentPathProperty.value.color,
            showPathProperties = showPropertiesDialog.value,
            showColorPicker = showColorPickerDialog.value,
            isAnimationShowing = isAnimationShowing.value,
            showSettings = showSettingsDialog.value,
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
            onSettingsClick = { showSettingsDialog.value = !showSettingsDialog.value }
        )
        EmptySpacer(16.dp)
    }

    SettingsDialog(showSettingsDialog)
    ConfirmDeleteFrameDialog(
        showConfirmDeleteFrameDialog,
        frameUuid,
        paths,
        previousFrames,
        pathsUndone,
        currentPath,
        currentPosition,
        previousPosition,
        currentPathProperty
    )
    ConfirmDeleteAllFramesDialog(
        showConfirmDeleteAllFramesDialog,
        frameUuid,
        paths,
        previousFrames,
        pathsUndone,
        currentPath,
        currentPosition,
        previousPosition,
        currentPathProperty
    )
    PathPropertiesDialog(currentPathProperty.value, showPropertiesDialog)
    ColorPickerDialog(currentPathProperty.value, showColorPickerDialog)
    ShowFramesDialog(
        frames = frameRepository.getFrames(),
        showFramesDialog = showFramesDialog,
        onFrameSelected = { selectedFrame ->
            frameRepository.updateDrawingPathsByUuid(frameUuid.value, paths.toList())

            paths.clear()
            previousFrames.clear()
            pathsUndone.clear()
            currentPath.value = Path()
            currentPosition.value = Offset.Unspecified
            previousPosition.value = Offset.Unspecified
            currentPathProperty.value = currentPathProperty.value.copy()

            frameUuid.value = selectedFrame.uuid
            paths += selectedFrame.drawingPaths
            showPreviousFrames(selectedFrame, previousFrames)
        },
        onDeleteAllFramesSelected = {
            showFramesDialog.value = false
            showConfirmDeleteAllFramesDialog.value = true
        }
    )
}

@Composable
private fun ConfirmDeleteFrameDialog(
    showConfirmDeleteFrameDialog: MutableState<Boolean>,
    frameUuid: MutableState<UUID>,
    paths: SnapshotStateList<DrawingPath>,
    previousFrames: SnapshotStateList<Frame>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    currentPath: MutableState<Path>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    currentPathProperty: MutableState<PathProperties>
) {
    ConfirmActionDialog(
        params = ConfirmActionDialogParams(
            title = "Delete current frame?",
            confirmButtonParams = ButtonParams(text = "Delete", onClick = {
                showConfirmDeleteFrameDialog.value = false
                deleteCurrentFrame(frameUuid, paths, previousFrames, pathsUndone, currentPath, currentPosition, previousPosition, currentPathProperty)
            }),
            dismissButtonParams = ButtonParams(text = "Cancel", onClick = {
                showConfirmDeleteFrameDialog.value = false
            }),
        ),
        showConfirmActionDialog = showConfirmDeleteFrameDialog,
    )
}

@Composable
private fun ConfirmDeleteAllFramesDialog(
    showConfirmDeleteAllFramesDialog: MutableState<Boolean>,
    frameUuid: MutableState<UUID>,
    paths: SnapshotStateList<DrawingPath>,
    previousFrames: SnapshotStateList<Frame>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    currentPath: MutableState<Path>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    currentPathProperty: MutableState<PathProperties>
) {
    ConfirmActionDialog(
        params = ConfirmActionDialogParams(
            title = "Delete all frames?",
            confirmButtonParams = ButtonParams(text = "Delete", onClick = {
                showConfirmDeleteAllFramesDialog.value = false
                deleteAllFrames(frameUuid, paths, previousFrames, pathsUndone, currentPath, currentPosition, previousPosition, currentPathProperty)
            }),
            dismissButtonParams = ButtonParams(text = "Cancel", onClick = {
                showConfirmDeleteAllFramesDialog.value = false
            }),
        ),
        showConfirmActionDialog = showConfirmDeleteAllFramesDialog,
    )
}

private fun deleteCurrentFrame(
    frameUuid: MutableState<UUID>,
    paths: SnapshotStateList<DrawingPath>,
    previousFrames: SnapshotStateList<Frame>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    currentPath: MutableState<Path>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    currentPathProperty: MutableState<PathProperties>
) {
    if (frameRepository.getFrames().size > 1) {
        val newFrame = frameRepository.deleteFrameByUuid(frameUuid.value)
        if (newFrame != null) {
            paths.clear()
            previousFrames.clear()
            pathsUndone.clear()
            currentPath.value = Path()
            currentPosition.value = Offset.Unspecified
            previousPosition.value = Offset.Unspecified
            currentPathProperty.value = currentPathProperty.value.copy()

            frameUuid.value = newFrame.uuid
            paths += newFrame.drawingPaths
            showPreviousFrames(newFrame, previousFrames)
        } else {
            Log.e(TAG, "deleteFrameByUuid(${frameUuid.value}) returns null!")
        }
    }
}

private fun deleteAllFrames(
    frameUuid: MutableState<UUID>,
    paths: SnapshotStateList<DrawingPath>,
    previousFrames: SnapshotStateList<Frame>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    currentPath: MutableState<Path>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    currentPathProperty: MutableState<PathProperties>
) {
    frameRepository.deleteAllFrames()
    paths.clear()
    previousFrames.clear()
    pathsUndone.clear()
    currentPath.value = Path()
    currentPosition.value = Offset.Unspecified
    previousPosition.value = Offset.Unspecified
    currentPathProperty.value = currentPathProperty.value.copy()

    val newFrame = Frame(emptyList())
    frameRepository.addFrame(newFrame.uuid, newFrame)
    frameUuid.value = newFrame.uuid
}

private fun showPreviousFrames(
    currentFrame: Frame,
    previousFramePaths: SnapshotStateList<Frame>
) {
    val frames = frameRepository.getFrames()
    for (i in frames.indices) {
        if (frames[i].uuid == currentFrame.uuid) {
            for (j in i - 1 downTo i - PREVIOUS_FRAMES_VISIBLE_COUNT) {
                if (j < 0) break
                previousFramePaths += frames[j]
            }
        }
    }
}

@Preview
@Composable
private fun EditorScreenContentPreview() {
    LivePicturesTheme {
        EditorScreenContent()
    }
}
