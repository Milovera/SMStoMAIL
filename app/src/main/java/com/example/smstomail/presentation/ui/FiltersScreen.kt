package com.example.smstomail.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.smstomail.R
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.presentation.ui.components.FilterEdit
import com.example.smstomail.presentation.ui.state.FiltersUiState
import com.example.smstomail.presentation.ui.theme.AppTheme

@Composable
fun FiltersScreen(
    filtersUiState: FiltersUiState,
    modifier: Modifier = Modifier,
    onAddButtonClicked: () -> Unit = {},
    onUpdateItem: (Int, Filter.Type, String) -> (Unit) = {_, _, _ -> },
    onRemoveButtonClicked: (Int) -> Unit = {},
    onSaveButtonClicked: () -> (Unit) = {},
    onResetButtonClicked: () -> (Unit) = {}
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .weight(1f)
        ) {
            items(filtersUiState.item) {filter ->
                FilterEdit(
                    item = filter,
                    onUpdateItem = onUpdateItem,
                    onRemoveButtonClicked = onRemoveButtonClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = dimensionResource(R.dimen.padding_short),
                            bottom = dimensionResource(R.dimen.padding_short)
                        )
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_large)
                )
        ) {
            Button(
                onClick = onResetButtonClicked,
                enabled = filtersUiState.isModified
            ) {
                Text(
                    text = stringResource(R.string.reset_settings_button_text)
                )
            }
            Button(
                onClick = onAddButtonClicked
            ) {
                Text(
                    text = stringResource(R.string.add_button_text)
                )
            }
            Button(
                onClick = onSaveButtonClicked,
                enabled = filtersUiState.isValid && filtersUiState.isModified
            ) {
                Text(
                    text = stringResource(R.string.save_button_text)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewFilterScreen() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var filtersList by remember {
                mutableStateOf(
                    listOf(
                        Filter(),
                        Filter(id = 1, value = "1")
                    )
                )
            }

            val state = FiltersUiState(
                item = filtersList,
                isValid = true,
                isModified = true
            )

            FiltersScreen(
                filtersUiState = state,
                onAddButtonClicked = {
                    filtersList = filtersList.plus(
                        Filter(
                            id = if(filtersList.isEmpty()) { 0 } else { filtersList.maxOf { it.id }.plus(1)}
                        )
                    )
                },
                onUpdateItem = { id, type, value ->
                    filtersList = filtersList
                        .associateBy { it.id }
                        .toMutableMap()
                        .apply {
                            this[id] = Filter(id, type, value)
                        }
                        .values
                        .toList()
                },
                onRemoveButtonClicked = { id ->
                    filtersList = filtersList
                        .associateBy { it.id }
                        .toMutableMap()
                        .apply {
                            remove(id)
                        }
                        .values
                        .toList()
                },
                onResetButtonClicked = {
                    filtersList = listOf(
                        Filter(),
                        Filter(id = 1, value = "1")
                    )
                }
            )
        }
    }
}