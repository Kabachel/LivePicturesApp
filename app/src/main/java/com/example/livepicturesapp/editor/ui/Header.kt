package com.example.livepicturesapp.editor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
internal fun Header(
    // TODO rename with "enabled"
    isPathsEmpty: Boolean,
    isPathsUndoneEmpty: Boolean,
    isDeleteFrameEnabled: Boolean,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onDeleteFrameClick: () -> Unit,
    onCreateFrameClick: () -> Unit,
    onShowFramesClick: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowsButtons(onUndoClick, onRedoClick, isPathsEmpty, isPathsUndoneEmpty)
        FrameButtons(isDeleteFrameEnabled, onDeleteFrameClick, onCreateFrameClick, onShowFramesClick)
        PlayPauseButtons(onPauseClick, onPlayClick)
    }
}

@Composable
private fun ArrowsButtons(
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    isPathsEmpty: Boolean,
    isPathsUndoneEmpty: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Undo last action",
            tint = if (isPathsEmpty) LivePicturesTheme.colors.gray else LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(24.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isPathsEmpty) { onUndoClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "Return the last canceled action",
            tint = if (isPathsUndoneEmpty) LivePicturesTheme.colors.gray else LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(24.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = !isPathsUndoneEmpty) { onRedoClick() },
        )
    }
}

@Composable
private fun FrameButtons(
    isDeleteFrameEnabled: Boolean,
    onDeleteFrameClick: () -> Unit,
    onCreateFrameClick: () -> Unit,
    onShowFramesClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_bin),
            contentDescription = "Delete frame",
            tint = if (isDeleteFrameEnabled) LivePicturesTheme.colors.white else LivePicturesTheme.colors.gray,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(enabled = isDeleteFrameEnabled) { onDeleteFrameClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_file_plus),
            contentDescription = "Create frame",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onCreateFrameClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_layers),
            contentDescription = "Show all frames",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onShowFramesClick() },
        )
    }
}

@Composable
private fun PlayPauseButtons(
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_pause),
            contentDescription = "Pause animation",
            tint = LivePicturesTheme.colors.gray,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onPauseClick() },
        )
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = "Play animation",
            tint = LivePicturesTheme.colors.gray,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onPlayClick() },
        )
    }
}

@Preview
@Composable
private fun HeaderPreview() {
    LivePicturesTheme {
        Header(
            isPathsEmpty = false,
            isPathsUndoneEmpty = false,
            isDeleteFrameEnabled = true,
            onUndoClick = {},
            onRedoClick = {},
            onDeleteFrameClick = {},
            onCreateFrameClick = {},
            onShowFramesClick = {},
            onPauseClick = {},
            onPlayClick = {},
        )
    }
}