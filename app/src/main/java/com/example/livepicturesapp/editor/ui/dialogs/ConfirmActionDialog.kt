package com.example.livepicturesapp.editor.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.livepicturesapp.ui.theme.LivePicturesTheme

@Composable
internal fun ConfirmActionDialog(params: ConfirmActionDialogParams, showConfirmActionDialog: MutableState<Boolean>) {
    if (showConfirmActionDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmActionDialog.value = false },
            confirmButton = { ConfirmButtonText(params.confirmButtonParams, color = LivePicturesTheme.colors.red) },
            dismissButton = { ConfirmButtonText(params.dismissButtonParams) },
            title = { Text(text = params.title) }
        )
    }
}

@Composable
private fun ConfirmButtonText(params: ButtonParams, color: Color = Color.Unspecified) {
    Text(
        text = params.text,
        fontSize = 16.sp,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { params.onClick() }
            .padding(8.dp),
    )
}

internal data class ConfirmActionDialogParams(
    val title: String,
    val confirmButtonParams: ButtonParams,
    val dismissButtonParams: ButtonParams,
)

internal data class ButtonParams(
    val text: String,
    val onClick: () -> Unit,
)