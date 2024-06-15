package com.example.medcontrol.graphscreen.modal

import android.app.Application
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.medcontrol.R


enum class Option {
    HEART_RATE, BLOOD_SUGAR, BLOOD_PRESSURE
}

data class GraphModalState(
    val selectedOption: Option?,
    val options: List<Option>,
    val timestamp: Long?,
    val pulse: Int?,
    val bloodSugar: Int?,
    val systolic: Int?,
    val diastolic: Int?
)

@Composable
fun GraphModal(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onHideDialog: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel = remember { GraphModalViewModel(application) }
    val state = viewModel.state.collectAsState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(context.getString(R.string.add_entry))
        },
        text = {
            Column {

                ExposedDropdownMenu(
                    state.value,
                    onOptionSelected = {
                        viewModel.modalEvent(GraphModalEvent.Choose(it))
                    },
                    optionToStrings = {
                        viewModel.optionToStrings(it)
                    }
                )

                when (state.value.selectedOption) {
                    Option.HEART_RATE -> {

                        HeartRateInputs(
                            state.value.pulse,
                            onHeartRateChange = {
                                viewModel.modalEvent(GraphModalEvent.HeartRate(it))
                            }
                        )

                    }

                    Option.BLOOD_SUGAR -> {
                        BloodSugarInputs(
                            state.value.bloodSugar,
                            onBloodSugarChange = {
                                viewModel.modalEvent(GraphModalEvent.BloodSugar(it))
                            }
                        )
                    }

                    Option.BLOOD_PRESSURE -> {
                        BloodPressureInputs(
                            state.value.systolic,
                            state.value.diastolic,
                            onSystolicChange = {
                                viewModel.modalEvent(GraphModalEvent.Systolic(it))
                            },
                            onDiastolicChange = {
                                viewModel.modalEvent(GraphModalEvent.Diastolic(it))
                            }
                        )
                    }

                    null -> {}
                }

            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onHideDialog()
            }) {
                Text(context.getString(R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(context.getString(R.string.dismiss))
            }
        }
    )

}

@Composable
fun BloodPressureInputs(
    systolic: Int?,
    diastolic: Int?,
    onSystolicChange: (Int?) -> Unit,
    onDiastolicChange: (Int?) -> Unit
) {
    val interactionSourceSystolic = remember { MutableInteractionSource() }
    val interactionSourceDiastolic = remember { MutableInteractionSource() }
    val iconSelectColor = OutlinedTextFieldDefaults.colors().focusedIndicatorColor
    val iconUnselectColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor

    Column{
        OutlinedTextField(
            value = systolic?.toString() ?: "",
            onValueChange = { input ->
                // Update the value only if the input is a number
                if (input.all { it.isDigit() }) {
                    if (input.isEmpty())
                        onSystolicChange(null)
                    else
                        onSystolicChange(input.toInt())
                }
            },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = if (interactionSourceSystolic.collectIsFocusedAsState().value)
                        iconSelectColor
                    else
                        iconUnselectColor
                )
            },
            label = { Text("systolic") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            interactionSource = interactionSourceSystolic
        )


        OutlinedTextField(
            value = diastolic?.toString() ?: "",
            onValueChange = { input ->
                // Update the value only if the input is a number
                if (input.all { it.isDigit() }) {
                    if (input.isEmpty())
                        onDiastolicChange(null)
                    else
                        onDiastolicChange(input.toInt())
                }
            },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (interactionSourceDiastolic.collectIsFocusedAsState().value)
                        iconSelectColor
                    else
                        iconUnselectColor
                )
            },
            label = { Text("diastolic") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth(),
            interactionSource = interactionSourceDiastolic
        )
    }



}

@Composable
fun BloodSugarInputs(
    bloodSugar: Int?,
    onBloodSugarChange: (Int?) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = bloodSugar?.toString() ?: "",
        onValueChange = { input ->
            // Update the value only if the input is a number
            if (input.all { it.isDigit() }) {
                if (input.isEmpty())
                    onBloodSugarChange(null)
                else
                    onBloodSugarChange(input.toInt())
            }
        },
        label = { Text("Enter Blood Sugar (mg/dl)") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        interactionSource = interactionSource
    )

}

@Composable
fun HeartRateInputs(
    heartRate: Int?,
    onHeartRateChange: (Int?) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val iconSelectColor = OutlinedTextFieldDefaults.colors().focusedIndicatorColor
    val iconUnselectColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor

    OutlinedTextField(
        value = heartRate?.toString() ?: "",
        onValueChange = { input ->
            // Update the value only if the input is a number
            if (input.all { it.isDigit() }) {
                if (input.isEmpty())
                    onHeartRateChange(null)
                else
                    onHeartRateChange(input.toInt())
            }
        },
        trailingIcon = {
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (interactionSource.collectIsFocusedAsState().value)
                    iconSelectColor
                else
                    iconUnselectColor
            )
        },
        label = { Text("Enter Heart Rate (bpm)") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        interactionSource = interactionSource
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenu(
    state: GraphModalState,
    onOptionSelected: (Option) -> Unit,
    optionToStrings: (Option) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    val options = state.options

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (state.selectedOption != null) optionToStrings(state.selectedOption) else "",
            onValueChange = { }, // No-op since the field is read-only
            label = { Text(stringResource(R.string.select_option)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            readOnly = true, // Make the TextField read-only
            modifier = Modifier
                .menuAnchor() // Anchor the menu to the text field
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(optionToStrings(selectionOption))
                    },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}