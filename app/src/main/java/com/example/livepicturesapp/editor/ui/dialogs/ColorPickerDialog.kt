package com.example.livepicturesapp.editor.ui.dialogs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toRect
import com.example.livepicturesapp.editor.model.PathProperties
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun ColorPickerDialog(
    pathProperties: PathProperties,
    showColorPicker: MutableState<Boolean>,
) {
    if (showColorPicker.value) {
        Dialog(onDismissRequest = { showColorPicker.value = !showColorPicker.value }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LivePicturesTheme.colors.white)
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var hsv by remember {
                    val hsv = floatArrayOf(0f, 0f, 0f)
                    android.graphics.Color.colorToHSV(Color.Blue.toArgb(), hsv)

                    mutableStateOf(Triple(hsv[0], hsv[1], hsv[2]))
                }
                val backgroundColor by remember(hsv) { mutableStateOf(Color.hsv(hsv.first, hsv.second, hsv.third)) }
                SatValPanel(hue = hsv.first) { sat, value -> hsv = Triple(hsv.first, sat, value) }
                EmptySpacer(32.dp)
                HueBar { hue -> hsv = Triple(hue, hsv.second, hsv.third) }
                EmptySpacer(32.dp)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(backgroundColor)
                )
                EmptySpacer(32.dp)
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pathProperties.color = Color.hsv(hsv.first, hsv.second, hsv.third)
                        showColorPicker.value = false
                    },
                ) { Text("Apply") }
            }
        }
    }
}

@Composable
private fun HueBar(setColor: (Float) -> Unit) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .emitDragGesture(interactionSource)
    ) {
        val drawScopeSize = size
        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        // Prepare hue colors to draw
        val hueColors = IntArray((huePanel.width()).toInt())
        var hue = 0f
        for (i in hueColors.indices) {
            hueColors[i] = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
            hue += 360f / hueColors.size
        }

        // Prepare bitmap to draw
        val linePaint = Paint()
        linePaint.strokeWidth = 0F
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            Canvas(bitmap).drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
        }

        // Draw hue palette
        drawBitmap(
            bitmap = bitmap,
            panel = huePanel
        )

        // Helper function to convert point coordinates to hue color
        fun convertPointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x = when {
                pointX < huePanel.left -> 0F
                pointX > huePanel.right -> width
                else -> pointX - huePanel.left
            }
            return x * 360f / width
        }

        // Update the hue point
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            pressOffset = Offset(pressPos, 0f)
            val selectedHue = convertPointToHue(pressPos)
            setColor(selectedHue)
        }

        // Draw hue selector
        drawCircle(
            Color.White,
            radius = size.height / 2,
            center = Offset(pressOffset.x, size.height / 2),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun SatValPanel(
    hue: Float,
    setSatVal: (Float, Float) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    var pressOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .size(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .emitDragGesture(interactionSource)
    ) {
        val cornerRadius = 16.dp.toPx()
        val satValSize = size

        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        // Create shaders for saturation and value
        val rgb = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
            -0x1, rgb, Shader.TileMode.CLAMP
        )
        val valShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
            -0x1, -0x1000000, Shader.TileMode.CLAMP
        )

        // Draw the combined saturation-value gradient onto the bitmap
        Canvas(bitmap).drawRoundRect(
            satValPanel,
            cornerRadius,
            cornerRadius,
            Paint().apply {
                shader = ComposeShader(
                    valShader,
                    satShader,
                    PorterDuff.Mode.MULTIPLY
                )
            }
        )

        // Draw the bitmap onto the canvas
        drawBitmap(
            bitmap = bitmap,
            panel = satValPanel
        )

        // Helper function to convert point coordinates to saturation and value
        fun pointToSatVal(pointX: Float, pointY: Float): Pair<Float, Float> {
            val width = satValPanel.width()
            val height = satValPanel.height()

            val x = when {
                pointX < satValPanel.left -> 0f
                pointX > satValPanel.right -> width
                else -> pointX - satValPanel.left
            }

            val y = when {
                pointY < satValPanel.top -> 0f
                pointY > satValPanel.bottom -> height
                else -> pointY - satValPanel.top
            }

            val satPoint = 1f / width * x
            val valuePoint = 1f - 1f / height * y

            return satPoint to valuePoint
        }

        // Collect and handle press events to update the saturation and value
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPositionOffset = Offset(
                pressPosition.x.coerceIn(0f..satValSize.width),
                pressPosition.y.coerceIn(0f..satValSize.height)
            )

            pressOffset = pressPositionOffset
            val (satPoint, valuePoint) = pointToSatVal(pressPositionOffset.x, pressPositionOffset.y)
            setSatVal(satPoint, valuePoint)
        }

        // Draw the input selector at the current press offset
        colorInputSelector(pressOffset)
    }
}

private fun DrawScope.colorInputSelector(pressOffset: Offset) {
    drawCircle(
        color = Color.White,
        radius = 8.dp.toPx(),
        center = pressOffset,
        style = Stroke(width = 2.dp.toPx())
    )

    drawCircle(
        color = Color.White,
        radius = 2.dp.toPx(),
        center = pressOffset,
    )
}

private fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource,
    setOffset: (Offset) -> Unit
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)
                ?.pressPosition
                ?.let(setOffset)
        }
    }
}

private fun Modifier.emitDragGesture(interactionSource: MutableInteractionSource): Modifier {
    return then(
        Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    interactionSource.tryEmit(PressInteraction.Press(change.position))
                }
            }
            .clickable(interactionSource, indication = null) {}
    )
}

private fun DrawScope.drawBitmap(
    bitmap: Bitmap,
    panel: RectF
) {
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap,
            null,
            panel.toRect(),
            null
        )
    }
}