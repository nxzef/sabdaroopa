package com.nascriptone.siddharoopa.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DiscardDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomDialog(
        visible = visible, onDismissRequest = onDismissRequest, head = {
            CustomDialogHead("Discard Changes?")
        }, description = {
            CustomDialogDescription("You've made changes to your selection. Discard them and go back?")
        }, action = {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onConfirm) {
                    Text(
                        "Discard", color = Color.Red.copy(
                            green = 0.3f, blue = 0.3f
                        )
                    )
                }
            }
        }, modifier = modifier
    )
}