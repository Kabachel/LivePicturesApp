package com.example.livepicturesapp.editor

import androidx.compose.runtime.Composable
import com.example.livepicturesapp.editor.ui.EditorScreenContent

@Composable
fun EditorScreenUI(
    state: State,
) {
    when (state) {
        is State.Content -> EditorScreenContent()
    }
}

