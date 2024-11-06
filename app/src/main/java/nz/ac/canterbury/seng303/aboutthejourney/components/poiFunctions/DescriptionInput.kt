package nz.ac.canterbury.seng303.aboutthejourney.components.poiFunctions

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nz.ac.canterbury.seng303.aboutthejourney.R

/**
 * A composable function to display a text input field for entering a description.
 *
 * @param description The current description text.
 * @param onDescriptionChange Callback when the description text changes.
 */
@Composable
fun DescriptionSection(description: String, onDescriptionChange: (String) -> Unit) {
    val context = LocalContext.current

    Text(
        text = context.getString(R.string.add_description_header),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentWidth(Alignment.Start)
            .fillMaxWidth()
    )
    TextField(
        value = description,
        onValueChange = { onDescriptionChange(it) },
        label = { Text(context.getString(R.string.enter_description)) },
        modifier = Modifier
            .fillMaxWidth()

    )
}