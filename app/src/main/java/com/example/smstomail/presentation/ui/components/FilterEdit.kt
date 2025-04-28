package com.example.smstomail.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.R
import com.example.smstomail.presentation.ui.theme.AppTheme

fun Filter.Type.toStringRes(): Int {
    return when(this) {
        Filter.Type.SenderInclude -> R.string.sender_include_filter_type_text
        Filter.Type.SenderExclude -> R.string.sender_exclude_filter_type_text
        Filter.Type.MessageInclude -> R.string.message_include_filter_type_text
        Filter.Type.MessageExclude -> R.string.message_exclude_filter_type_text
    }
}

@Composable
fun FilterEdit(
    item: Filter,
    modifier: Modifier = Modifier,
    onUpdateItem: (Int, Filter.Type, String) -> Unit  = {_, _, _ -> },
    onRemoveButtonClicked: (Int) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val typeTextResId = item.type.toStringRes()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Text(
            text = stringResource(typeTextResId),
            modifier = Modifier
                .weight(0.5f)
                .padding(
                    start = 8.dp
                )
        )
        IconButton(
            onClick = {
                expanded = !expanded
            }
        ) {
            Icon(
                imageVector = if (expanded) {
                    Icons.Filled.KeyboardArrowUp
                } else {
                    Icons.Filled.KeyboardArrowDown
                },
                contentDescription = null
            )
        }
        TextField(
            value = item.value,
            onValueChange = { newValue ->
                onUpdateItem(item.id, item.type, newValue)
            },
            isError = item.value.isEmpty(),
            modifier = Modifier
                .weight(1f)
                .testTag("filterTextField")
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            Filter.Type.entries.forEach { filterType ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(filterType.toStringRes())
                        )
                    },
                    onClick = {
                        onUpdateItem(item.id, filterType, item.value)
                        expanded = false
                    },
                    enabled = filterType != item.type
                )
            }
        }
        IconButton(
            onClick = {
                onRemoveButtonClicked(item.id)
            },
            modifier = Modifier
                .size(
                    dimensionResource(R.dimen.icon_button_size_small)
                )
                .testTag("removeFilterButton")
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

@PreviewLightDark
@Composable
fun PreviewFilterEdit() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(verticalArrangement = Arrangement.Top) {
                FilterEdit(
                    item = Filter(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            top = 4.dp,
                            end = 8.dp,
                            bottom = 4.dp
                        )
                )
                FilterEdit(
                    item = Filter(
                        type = Filter.Type.MessageExclude,
                        value = "someText"
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 8.dp,
                            top = 4.dp,
                            end = 8.dp,
                            bottom = 4.dp
                        )
                )
            }
        }
    }
}