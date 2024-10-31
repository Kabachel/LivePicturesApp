package com.example.livepicturesapp.editor.ui.dialogs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.livepicturesapp.editor.model.PathProperties
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PathPropertiesDialog(pathProperties: PathProperties, showPropertiesDialog: MutableState<Boolean>) {
    var strokeWidth by remember { mutableStateOf(pathProperties.strokeWidth) }
    var strokeCap by remember { mutableStateOf(pathProperties.strokeCap) }
    var strokeJoin by remember { mutableStateOf(pathProperties.strokeJoin) }

    if (showPropertiesDialog.value) {
        Dialog(onDismissRequest = { showPropertiesDialog.value = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LivePicturesTheme.colors.white)
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EmptySpacer(8.dp)
                Text(
                    "Path Properties",
                    color = LivePicturesTheme.colors.black,
                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                )
                EmptySpacer(16.dp)
                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    val path = Path().apply {
                        moveTo(0f, size.height / 2)
                        lineTo(size.width, size.height / 2)
                    }
                    drawPath(
                        color = pathProperties.color,
                        path = path,
                        style = Stroke(
                            width = strokeWidth,
                            cap = strokeCap,
                            join = strokeJoin
                        )
                    )
                }

                EmptySpacer(32.dp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stroke Width: ",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    TextField(
                        value = strokeWidth.toInt().toString(),
                        onValueChange = {
                            var newValue = it.toFloatOrNull() ?: 1f
                            if (newValue > 100f) newValue = 100f
                            strokeWidth = newValue
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }

                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    valueRange = 1f..100f,
                )

                Text(
                    text = "Stroke Cap: $strokeCap",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                var strokeCapDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = strokeCapDropdownExpanded,
                    onExpandedChange = { strokeCapDropdownExpanded = !strokeCapDropdownExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = strokeCap.toString(),
                        onValueChange = { },
                        label = { Text("Stroke Cap") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = strokeCapDropdownExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = strokeCapDropdownExpanded,
                        onDismissRequest = { strokeCapDropdownExpanded = false }
                    ) {
                        listOf("Butt", "Round", "Square").forEach { selectionOption: String ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    strokeCap = when (selectionOption) {
                                        "Butt" -> StrokeCap.Butt
                                        "Round" -> StrokeCap.Round
                                        else -> StrokeCap.Square
                                    }
                                    strokeCapDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                EmptySpacer(16.dp)

                var strokeJoinDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = strokeJoinDropdownExpanded,
                    onExpandedChange = { strokeJoinDropdownExpanded = !strokeJoinDropdownExpanded }
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = strokeJoin.toString(),
                        onValueChange = { },
                        label = { Text("Stroke Join") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = strokeCapDropdownExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = strokeJoinDropdownExpanded,
                        onDismissRequest = { strokeJoinDropdownExpanded = false }
                    ) {
                        listOf("Miter", "Round", "Bevel").forEach { selectionOption: String ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    strokeJoin = when (selectionOption) {
                                        "Miter" -> StrokeJoin.Miter
                                        "Round" -> StrokeJoin.Round
                                        else -> StrokeJoin.Bevel
                                    }
                                    strokeJoinDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                EmptySpacer(32.dp)

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pathProperties.strokeWidth = strokeWidth
                        pathProperties.strokeCap = strokeCap
                        pathProperties.strokeJoin = strokeJoin
                        showPropertiesDialog.value = false
                    },
                ) { Text("Apply") }
            }
        }
    }
}