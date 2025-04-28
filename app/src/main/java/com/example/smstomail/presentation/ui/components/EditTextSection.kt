package com.example.smstomail.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.example.smstomail.R
import com.example.smstomail.presentation.ui.theme.AppTheme

@Composable
fun EditTextSection(
    @StringRes labelText: Int,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val labelText = stringResource(labelText)
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = labelText,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(
                    all = dimensionResource(R.dimen.padding_small)
                )
                .weight(1f)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .weight(2f)
                .testTag(labelText)
        )
    }
}

@PreviewLightDark()
@Composable
fun EditTextSectionPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            EditTextSection(
                labelText = R.string.login_label,
                value = "login",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}