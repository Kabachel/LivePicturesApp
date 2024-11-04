package com.example.livepicturesapp.editor.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.livepicturesapp.editor.model.Frame
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
internal fun ShowFramesDialog(
    frames: List<Frame>,
    showFramesDialog: MutableState<Boolean>,
    onFrameSelected: (Frame) -> Unit,
    onDeleteAllFramesSelected: () -> Unit
) {
    if (showFramesDialog.value) {
        Dialog(onDismissRequest = { showFramesDialog.value = !showFramesDialog.value }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LivePicturesTheme.colors.white)
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("All Frames", color = LivePicturesTheme.colors.black)
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(frames) { index: Int, frame: Frame ->
                        FrameCard(
                            frame,
                            index,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onFrameSelected(frame)
                                    showFramesDialog.value = !showFramesDialog.value
                                }
                        )
                    }
                }
                FilledTonalButton(
                    onClick = { onDeleteAllFramesSelected() },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Delete all frames", color = LivePicturesTheme.colors.red) }
            }
        }
    }
}

@Composable
private fun FrameCard(
    frame: Frame,
    index: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$index Layer", color = LivePicturesTheme.colors.black)
        EmptySpacer(8.dp)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, LivePicturesTheme.colors.black, RoundedCornerShape(16.dp))
                .background(color = Color.White),
        ) {
            frame.drawingPaths.forEach {
                val transformedPath = Path()
                transformedPath.asAndroidPath().set(it.path.asAndroidPath())
                val pathSize = it.path.getBounds().size
                val matrix = Matrix()
                // TODO not working correct
                val x = size.width / pathSize.width
                val y = size.height / pathSize.height
                matrix.scale(0.1f, 0.1f)
                transformedPath.transform(matrix)
                val property = it.properties

                if (!property.isErase) {
                    drawPath(
                        color = property.color,
                        path = transformedPath,
                        style = Stroke(
                            width = (property.strokeWidth / 10) + 1,
                            cap = property.strokeCap,
                            join = property.strokeJoin,
                        )
                    )
                } else {
                    drawPath(
                        // TODO hardcode color for erase, BlendMode.Clear is not working
                        color = Color.White,
                        path = transformedPath,
                        style = Stroke(
                            width = (property.strokeWidth / 10) + 1,
                            cap = property.strokeCap,
                            join = property.strokeJoin
                        ),
//                        blendMode = BlendMode.Clear
                    )
                }
            }
        }
    }
}
