package rwiftkey.themes.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R

@Composable
fun ContinueWithXposedContainer(
    onClickContinue: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Medium,
                text = stringResource(id = R.string.dialog_no_root_title),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.dialog_no_root_desc),
                textAlign = TextAlign.Center
            )
            Divider(modifier = Modifier.padding(12.dp), color = Color.Black, thickness = 1.dp)
            Text(
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Medium,
                text = stringResource(id = R.string.proceed_without_root_title),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.proceed_without_root_desc),
                textAlign = TextAlign.Center
            )
            TextButton(onClick = { onClickContinue() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.mcontinue))
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Forward arrow",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.continue_desc),
                textAlign = TextAlign.Center
            )
        }
    }
}