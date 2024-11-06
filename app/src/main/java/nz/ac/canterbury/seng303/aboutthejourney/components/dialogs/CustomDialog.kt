package nz.ac.canterbury.seng303.aboutthejourney.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A custom dialog that can be used to display any given content to the user with
 * a title and two action buttons, one for confirming and one for cancelling.
 * @param title                     The title of the dialog.
 * @param content                   The content of the dialog.
 * @param onConfirm                 The action to take when the user confirms.
 * @param onCancel                  The action to take when the user cancels.
 * @param confirmButtonText         The text to display on the confirm button.
 */
@Composable
fun CustomDialog(
    title: String,
    content: @Composable () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmButtonText: String
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onCancel) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                content()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onCancel) {
                        Text(context.getString(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = onConfirm) {
                        Text(confirmButtonText)
                    }
                }
            }
        }
    }
}