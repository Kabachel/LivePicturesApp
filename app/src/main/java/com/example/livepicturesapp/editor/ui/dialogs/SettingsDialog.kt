package com.example.livepicturesapp.editor.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.livepicturesapp.editor.ui.ANIMATION_DELAY_BETWEEN_FRAMES
import com.example.livepicturesapp.editor.ui.PREVIOUS_FRAMES_VISIBLE_COUNT
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
internal fun SettingsDialog(showSettingsDialog: MutableState<Boolean>) {
    var animationDelay by remember { mutableLongStateOf(ANIMATION_DELAY_BETWEEN_FRAMES) }
    var previousFramesVisibleCount by remember { mutableIntStateOf(PREVIOUS_FRAMES_VISIBLE_COUNT) }

    if (showSettingsDialog.value) {
        Dialog(onDismissRequest = { showSettingsDialog.value = false }) {
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
                    "Settings",
                    color = LivePicturesTheme.colors.black,
                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                )
                EmptySpacer(16.dp)
                Text(
                    text = "Animation delay between frames (ms)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                TextField(
                    value = animationDelay.toString(),
                    onValueChange = {
                        var newValue = it.toLongOrNull() ?: 50L
                        if (newValue > 10000L) newValue = 10000L
                        animationDelay = newValue
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Slider(
                    value = animationDelay.toFloat(),
                    onValueChange = { animationDelay = it.toLong() },
                    valueRange = 50f..10000f,
                )

                Text(
                    text = "Previous frames visible count",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                TextField(
                    value = previousFramesVisibleCount.toString(),
                    onValueChange = {
                        var newValue = it.toIntOrNull() ?: 0
                        if (newValue > 10) newValue = 10
                        previousFramesVisibleCount = newValue
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Slider(
                    value = previousFramesVisibleCount.toFloat(),
                    onValueChange = { previousFramesVisibleCount = it.toInt() },
                    valueRange = 0f..5f,
                )
                EmptySpacer(16.dp)

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showSettingsDialog.value = false
                        ANIMATION_DELAY_BETWEEN_FRAMES = animationDelay
                        PREVIOUS_FRAMES_VISIBLE_COUNT = previousFramesVisibleCount
                    },
                ) { Text("Apply") }
            }
        }
    }
}