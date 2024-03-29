package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R
import rwiftkey.themes.model.SimpleApplication

@Composable
fun ContinueWithXposedContainer(
    modifier: Modifier = Modifier,
    targetKeyboard: String,
    availableKeyboards: List<SimpleApplication>,
    onClickContinue: () -> Unit,
    onClickChangeTargetKeyboard: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState())
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
            Spacer(modifier = Modifier.padding(4.dp))
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
            Spacer(modifier = Modifier.padding(2.dp))
            if (availableKeyboards.size > 1) {
                TextButton(onClick = { onClickChangeTargetKeyboard() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = targetKeyboard)
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.refresh),
                            contentDescription = "Refresh icon",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            TextButton(onClick = { onClickContinue() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.mcontinue))
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_forward),
                        contentDescription = "Forward arrow",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = stringResource(id = R.string.continue_desc),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Medium,
                text = stringResource(id = R.string.not_working),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.not_working_desc),
                textAlign = TextAlign.Center
            )
        }
    }
}