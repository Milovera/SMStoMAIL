package com.example.smstomail.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.smstomail.R
import com.example.smstomail.presentation.ui.theme.AppTheme

@Composable
fun RecipientEdit(
    value: String,
    onValueChange: (String) -> Unit,
    isValueCorrect: (String) -> Boolean,
    onAddButtonClicked: () -> Unit,
    onRemoveButtonClicked: () -> Unit,
    isLast: Boolean,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = value,
            singleLine = true,
            onValueChange = onValueChange,
            isError = !isValueCorrect(value),
            modifier = Modifier
                .weight(1f)
                .testTag("recipientEditTag")
        )
        IconButton(
            onClick = onAddButtonClicked,
            modifier = Modifier
                .size(dimensionResource(R.dimen.icon_button_size))
                .padding(dimensionResource(R.dimen.padding_small))
                .testTag("addRecipientButton")
        ) {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                tint = Color.Green,
                contentDescription = null,
                modifier = Modifier
                    .size(
                        dimensionResource(R.dimen.icon_button_size)
                    )
            )
        }
        if (!isLast) {
            IconButton(
                onClick = onRemoveButtonClicked,
                modifier = Modifier
                    .size(
                        dimensionResource(R.dimen.icon_button_size_small)
                    )
                    .testTag("removeRecipientButton")
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    tint = Color.Red,
                    contentDescription = null,
                    modifier = Modifier
                        .size(
                            dimensionResource(R.dimen.icon_button_size_small)
                        )
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun RecipientEditPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            var value by remember{
                mutableStateOf("")
            }
            var isLast by remember {
                mutableStateOf(false)
            }

            RecipientEdit(
                value = value,
                onValueChange = {
                    value = it
                },
                isValueCorrect = {
                    it.isNotEmpty()
                },
                onAddButtonClicked = {
                    isLast = false
                },
                onRemoveButtonClicked = {
                    isLast = true
                },
                isLast = isLast,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}