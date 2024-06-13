package com.example.medcontrol.homescreen.modal

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.medcontrol.R
import com.example.medcontrol.homescreen.MedicineViewItem
import com.example.medcontrol.homescreen.NotificationViewItem
import java.time.DayOfWeek

@Composable
fun MedicineModal(
    data: MedicineViewItem? = null,
    onDismissRequest: () -> Unit,
    onConfirm: (MedicineViewItem) -> Unit,
    onUpdate: (MedicineViewItem) -> Unit,
    onHideDialog: () -> Unit
) {
    val context = LocalContext.current

    val viewModel = remember { MedicineModalViewModel(data) }
    val state = viewModel.state.collectAsState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            if (data == null)
                Text(context.getString(R.string.add_medicine))
            else
                Text(context.getString(R.string.update_medicine))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = state.value.name,
                    onValueChange = { viewModel.cardEvent(MedicineModalEvent.SetName(it)) },
                    label = { Text(context.getString(R.string.medicine_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(5.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(state.value.notifications) {
                        ExpendableNotificationCard(
                            it,
                            onToggle = {
                                viewModel.cardEvent(
                                    MedicineModalEvent.ToggleNotification(
                                        it.uuid
                                    )
                                )
                            },
                            onDayClicked = { day, isChecked ->
                                viewModel.cardEvent(
                                    MedicineModalEvent.SetDay(
                                        it.uuid,
                                        day,
                                        isChecked
                                    )
                                )
                            },
                            onDelete = {
                                viewModel.cardEvent(
                                    MedicineModalEvent.DeleteNotification(
                                        it.uuid
                                    )
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedIconButton(onClick = { viewModel.cardEvent(MedicineModalEvent.Notification) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = context.getString(R.string.add_notification)
                        )
                    }

                }


            }
        },
        confirmButton = {
            Button(onClick = {
                if (data == null)
                    onConfirm(state.value)
                else
                    onUpdate(state.value)
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

@SuppressLint("DefaultLocale")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExpendableNotificationCard(
    state: NotificationViewItem,
    onToggle: () -> Unit,
    onDayClicked: (DayOfWeek, Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    val rotationState by animateFloatAsState(
        targetValue = if (state.isExpended) 180f else 0f, label = ""
    )

    var notificationTitle by remember { mutableStateOf("") }

    Row {

        OutlinedCard(
            onClick = { onToggle() },
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 400,
                        easing = LinearOutSlowInEasing
                    ),
                ),
            shape = RoundedCornerShape(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                IconButton(
                    onClick = { onToggle() },
                    modifier = Modifier
                        .rotate(rotationState)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }

                Text(
                    text = String.format(
                        "%s: %02d:%02d",
                        context.getString(R.string.notification),
                        state.timeState.hour,
                        state.timeState.minute
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                // Delete notification button
                IconButton(
                    onClick = { onDelete() },

                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }

            }

            if (state.isExpended) {
                ShowTimeSetter(state, onDayClicked)
            }

        }

    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ShowTimeSetter(
    state: NotificationViewItem,
    onDayClicked: (DayOfWeek, Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(8.dp)
    ) {


        Text(context.getString(R.string.set_time))
        TimeInput(
            state = state.timeState,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(context.getString(R.string.select_days))
        Row(
            modifier = Modifier.horizontalScroll(state = state.scrollState)
        ) {
            DaysRow(
                dayName = context.getString(R.string.monday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.MONDAY)
            ) { onDayClicked(DayOfWeek.MONDAY, it) }
            DaysRow(
                dayName = context.getString(R.string.tuesday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.TUESDAY)
            ) { onDayClicked(DayOfWeek.TUESDAY, it) }
            DaysRow(
                dayName = context.getString(R.string.wednesday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.WEDNESDAY)
            ) { onDayClicked(DayOfWeek.WEDNESDAY, it) }
            DaysRow(
                dayName = context.getString(R.string.thursday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.THURSDAY)
            ) { onDayClicked(DayOfWeek.THURSDAY, it) }
            DaysRow(
                dayName = context.getString(R.string.friday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.FRIDAY)
            ) { onDayClicked(DayOfWeek.FRIDAY, it) }
            DaysRow(
                dayName = context.getString(R.string.saturday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.SATURDAY)
            ) { onDayClicked(DayOfWeek.SATURDAY, it) }
            DaysRow(
                dayName = context.getString(R.string.sunday_short),
                isChecked = state.selectedDays.getValue(DayOfWeek.SUNDAY)
            ) { onDayClicked(DayOfWeek.SUNDAY, it) }
        }
    }
}

@Composable
fun DaysRow(dayName: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    OutlinedIconToggleButton(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier
            .padding(0.dp)
            .scale(1f)
    ) {
        Text(dayName)
    }

}

