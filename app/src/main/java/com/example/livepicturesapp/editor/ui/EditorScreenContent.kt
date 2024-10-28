package com.example.livepicturesapp.editor.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.livepicturesapp.R
import com.example.livepicturesapp.editor.Event
import com.example.livepicturesapp.editor.State
import com.example.livepicturesapp.ui.components.EmptySpacer
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
fun EditorScreenContent(
    state: State.Content,
    onEvent: (Event) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LivePicturesTheme.colors.black)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EmptySpacer(16.dp)
        Header(onEvent)
        EmptySpacer(32.dp)
        DrawingArea()
        EmptySpacer(22.dp)
        Footer(onEvent)
        EmptySpacer(16.dp)
    }
}

@Composable
private fun Header(onEvent: (Event) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowsButtons(onEvent)
        FrameButtons(onEvent)
        PlayPauseButtons(onEvent)
    }
}

@Composable
private fun ArrowsButtons(onEvent: (Event) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Undo last action",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(24.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.Undo) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "Return the last canceled action",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(24.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.CancelUndo) },
        )
    }
}

@Composable
private fun FrameButtons(onEvent: (Event) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_bin),
            contentDescription = "Delete frame",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.DeleteFrame) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_file_plus),
            contentDescription = "Create frame",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.CreateFrame) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_layers),
            contentDescription = "Show all frames",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.ShowFrames) },
        )
    }
}

@Composable
private fun PlayPauseButtons(onEvent: (Event) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_pause),
            contentDescription = "Pause animation",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.PauseAnimation) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = "Play animation",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.PlayAnimation) },
        )
    }
}

@Composable
private fun ColumnScope.DrawingArea() {
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
    }
}

@Composable
private fun Footer(onEvent: (Event) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = "Choose pencil",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.ChoosePencil) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_brush),
            contentDescription = "Choose brush",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.ChooseBrush) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_erase),
            contentDescription = "Choose erase",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.ChooseErase) },
        )
        Icon(
            painter = painterResource(R.drawable.ic_figures),
            contentDescription = "Choose figure",
            tint = LivePicturesTheme.colors.white,
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable { onEvent(Event.ChooseFigure) },
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .weight(1f, fill = false)
                .clip(CircleShape)
                .background(LivePicturesTheme.colors.blue)
                .clickable(onClickLabel = "Choose color") { onEvent(Event.ChooseColor) },
        )
    }
}

@Preview
@Composable
private fun EditorScreenContent() {
    LivePicturesTheme {
        EditorScreenContent(State.Content) { }
    }
}
