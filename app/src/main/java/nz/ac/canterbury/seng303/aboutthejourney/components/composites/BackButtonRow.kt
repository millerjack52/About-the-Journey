package nz.ac.canterbury.seng303.aboutthejourney.components.composites

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import nz.ac.canterbury.seng303.aboutthejourney.R
import nz.ac.canterbury.seng303.aboutthejourney.components.TripleButtonRow
import nz.ac.canterbury.seng303.aboutthejourney.components.buttons.BackButton

/**
 * The button for when a user is currently undergoing an action and has the option to go back.
 * The back button is centred in the middle of the screen.
 * @param goBack    the action to take when the user wants to go back
 */
@Composable
fun BackButtonRow(
    goBack: () -> Unit
) {
    val context = LocalContext.current

    TripleButtonRow(
        buttonOne = {},
        buttonTwo = {
            BackButton(
                onClick = goBack,
                contentDescription = context.getString(R.string.back_button_description)
            )
        },
        buttonThree = {}
    )
}
