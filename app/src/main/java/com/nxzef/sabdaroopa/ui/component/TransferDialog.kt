package com.nxzef.sabdaroopa.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nxzef.sabdaroopa.ui.state.TransferState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDialog(
    transferState: TransferState,
    onDismissRequest: () -> Unit,
    onRetryClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (transferState is TransferState.Success) LaunchedEffect(Unit) { onSuccess() }
    else BasicAlertDialog(
        onDismissRequest = onDismissRequest, modifier = modifier
    ) {
        DialogLayout {
            when (transferState) {
                is TransferState.Loading -> Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(16.dp))
                    Text("Updating...")
                }

                is TransferState.Error -> Column(modifier = Modifier.padding(16.dp)) {
                    CustomDialogHead("Error")
                    Spacer(Modifier.height(4.dp))
                    CustomDialogDescription(transferState.message)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onRetryClick) {
                            Text("Retry")
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}