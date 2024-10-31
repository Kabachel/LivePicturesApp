package com.example.livepicturesapp.editor.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.livepicturesapp.R
import com.example.livepicturesapp.editor.model.DrawingPath
import com.example.livepicturesapp.editor.model.Frame
import com.example.livepicturesapp.editor.model.InteractType
import com.example.livepicturesapp.editor.model.MotionType
import com.example.livepicturesapp.editor.model.PathProperties
import com.example.livepicturesapp.editor.repository.FrameRepository
import com.example.livepicturesapp.editor.ui.dialogs.ColorPickerDialog
import com.example.livepicturesapp.editor.ui.dialogs.PathPropertiesDialog
import com.example.livepicturesapp.editor.ui.dialogs.ShowFramesDialog
import com.example.livepicturesapp.editor.utils.dragMotionEvent
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

@Composable
private fun ColumnScope.DrawingArea(
    paths: SnapshotStateList<DrawingPath>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    motionType: MutableState<MotionType>,
    currentPosition: MutableState<Offset>,
    previousPosition: MutableState<Offset>,
    interactMode: MutableState<InteractType>,
    currentPath: MutableState<Path>,
    currentPathProperty: MutableState<PathProperties>
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.img_drawing_area_background),
            contentDescription = "Drawing area background",
            contentScale = ContentScale.FillBounds,
        )
        DrawingAreaContent(paths, pathsUndone, motionType, currentPosition, previousPosition, interactMode, currentPath, currentPathProperty)
    }
}

@Composable
private fun DrawingAreaContent(
    paths: SnapshotStateList<DrawingPath>,
    pathsUndone: SnapshotStateList<DrawingPath>,
    _motionType: MutableState<MotionType>,
    _currentPosition: MutableState<Offset>,
    _previousPosition: MutableState<Offset>,
    _interactMode: MutableState<InteractType>,
    _currentPath: MutableState<Path>,
    _currentPathProperty: MutableState<PathProperties>
) {
    val motionType by _motionType
    val currentPosition by _currentPosition
    val previousPosition by _previousPosition
    val interactMode by _interactMode
    val currentPath by _currentPath
    val currentPathProperty by _currentPathProperty

    val drawModifier = Modifier
        .fillMaxSize()
        .dragMotionEvent(
            onDragStart = { pointerInputChange ->
                _motionType.value = MotionType.Down
                _currentPosition.value = pointerInputChange.position
            },
            onDrag = { pointerInputChange ->
                _motionType.value = MotionType.Move
                _currentPosition.value = pointerInputChange.position

                if (interactMode == InteractType.Move) {
                    val positionChange = pointerInputChange.positionChange()
                    paths.forEach { drawingPath ->
                        val path: Path = drawingPath.path
                        path.translate(positionChange)
                    }
                    currentPath.translate(positionChange)
                }
            },
            onDragEnd = { pointerInputChange ->
                _motionType.value = MotionType.Up
                _currentPosition.value = pointerInputChange.position
            }
        )

    Canvas(modifier = drawModifier) {
        when (motionType) {
            MotionType.Down -> {
                currentPath.moveTo(currentPosition.x, currentPosition.y)
                _previousPosition.value = currentPosition
            }

            MotionType.Move -> {
                if (interactMode != InteractType.Move) {
                    currentPath.quadraticBezierTo(
                        x1 = previousPosition.x,
                        y1 = previousPosition.y,
                        x2 = (previousPosition.x + currentPosition.x) / 2,
                        y2 = (previousPosition.y + currentPosition.y) / 2,
                    )
                }
                _previousPosition.value = currentPosition
            }

            MotionType.Up -> {
                if (interactMode != InteractType.Move) {
                    currentPath.lineTo(currentPosition.x, currentPosition.y)

                    paths += DrawingPath(currentPath, currentPathProperty.copy())
                    _currentPath.value = Path()
                }

                _currentPosition.value = Offset.Unspecified
                _previousPosition.value = Offset.Unspecified
                _motionType.value = MotionType.Idle
                _currentPath.value = Path()
                _currentPathProperty.value = _currentPathProperty.value.copy()
                if (interactMode != InteractType.Move) {
                    pathsUndone.clear()
                }
            }

            MotionType.Idle -> Unit
        }

        val checkPoint = drawContext.canvas.nativeCanvas.saveLayer(null, null)

        paths.forEach {
            val path = it.path
            val property = it.properties

            if (!property.isErase) {
                drawPath(
                    color = property.color,
                    path = path,
                    style = Stroke(
                        width = property.strokeWidth,
                        cap = property.strokeCap,
                        join = property.strokeJoin,
                    )
                )
            } else {
                drawPath(
                    color = Color.Transparent,
                    path = path,
                    style = Stroke(
                        width = property.strokeWidth,
                        cap = property.strokeCap,
                        join = property.strokeJoin
                    ),
                    blendMode = BlendMode.Clear
                )
            }
        }

        if (motionType != MotionType.Idle) {
            if (!currentPathProperty.isErase) {
                drawPath(
                    color = currentPathProperty.color,
                    path = currentPath,
                    style = Stroke(
                        width = currentPathProperty.strokeWidth,
                        cap = currentPathProperty.strokeCap,
                        join = currentPathProperty.strokeJoin
                    )
                )
            } else {
                drawPath(
                    color = Color.Transparent,
                    path = currentPath,
                    style = Stroke(
                        width = currentPathProperty.strokeWidth,
                        cap = currentPathProperty.strokeCap,
                        join = currentPathProperty.strokeJoin
                    ),
                    blendMode = BlendMode.Clear
                )
            }
        }
        drawContext.canvas.nativeCanvas.restoreToCount(checkPoint)
    }
}

@Preview
@Composable
private fun EditorScreenContentPreview() {
    LivePicturesTheme {
        EditorScreenContent()
    }
}
