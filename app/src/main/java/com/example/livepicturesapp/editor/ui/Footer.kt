package com.example.livepicturesapp.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.livepicturesapp.R
import com.example.livepicturesapp.editor.model.InteractType
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
internal fun Footer(
    interactType: InteractType,
    onPencilClick: () -> Unit,
    onBrushClick: () -> Unit,
    onEraseClick: () -> Unit,
    onFiguresClick: () -> Unit,
    onColorClick: () -> Unit,
) {
    Row(
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
                .clickable { onPencilClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_brush),
            contentDescription = "Choose brush",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onBrushClick() },
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
                .clickable { onEraseClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_figures),
            contentDescription = "Choose figure",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onFiguresClick() },
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .background(LivePicturesTheme.colors.blue)
                .clickable(onClickLabel = "Choose color") { onColorClick() },
        )
    }
}

@Preview
@Composable
private fun FooterPreview() {
    LivePicturesTheme {
        Footer(
            interactType = InteractType.Move,
            onPencilClick = {},
            onBrushClick = {},
            onEraseClick = {},
            onFiguresClick = {},
            onColorClick = {},
        )
    }
}