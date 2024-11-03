package com.example.livepicturesapp.editor.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.livepicturesapp.R
import com.example.livepicturesapp.editor.model.InteractType
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
internal fun Footer(
    interactType: InteractType,
    selectedColor: Color,
    showPathProperties: Boolean,
    showColorPicker: Boolean,
    isAnimationShowing: Boolean,
    showSettings: Boolean,
    onPencilClick: () -> Unit,
    onBrushClick: () -> Unit,
    onEraseClick: () -> Unit,
    onFiguresClick: () -> Unit,
    onColorClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val alpha by animateFloatAsState(if (isAnimationShowing) 0f else 1f, label = "alpha")
    val offset by animateFloatAsState(if (isAnimationShowing) 20f else 0f, label = "offset")
    Row(
        modifier = Modifier.graphicsLayer(alpha = alpha, translationY = offset),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = "Choose pencil",
            tint = if (interactType == InteractType.Draw) {
                LivePicturesTheme.colors.green
            } else {
                LivePicturesTheme.colors.white
            },
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isAnimationShowing) { onPencilClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_brush),
            contentDescription = "Choose brush",
            tint = if (showPathProperties) LivePicturesTheme.colors.green else LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isAnimationShowing) { onBrushClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_erase),
            contentDescription = "Choose erase",
            tint = if (interactType == InteractType.Erase) {
                LivePicturesTheme.colors.green
            } else {
                LivePicturesTheme.colors.white
            },
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isAnimationShowing) { onEraseClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_figures),
            contentDescription = "Choose figure",
            tint = LivePicturesTheme.colors.gray,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isAnimationShowing) { onFiguresClick() },
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .then(
                    if (showColorPicker) {
                        Modifier.border(2.dp, LivePicturesTheme.colors.green, CircleShape)
                    } else {
                        Modifier.border(2.dp, LivePicturesTheme.colors.white, CircleShape)
                    }
                )
                .padding(1.dp)
                .clip(CircleShape)
                .background(selectedColor)
                .clickable(enabled = !isAnimationShowing, onClickLabel = "Choose color") { onColorClick() },
        )
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Choose figure",
            tint = if (showSettings) LivePicturesTheme.colors.green else LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isAnimationShowing) { onSettingsClick() },
        )
    }
}

@Preview
@Composable
private fun FooterPreview() {
    LivePicturesTheme {
        Footer(
            interactType = InteractType.Move,
            selectedColor = Color.Blue,
            showPathProperties = false,
            showColorPicker = false,
            isAnimationShowing = false,
            showSettings = false,
            onPencilClick = {},
            onBrushClick = {},
            onEraseClick = {},
            onFiguresClick = {},
            onColorClick = {},
            onSettingsClick = {}
        )
    }
}