package com.example.medcontrol.homescreen

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcontrol.AlarmReceiver
import com.example.medcontrol.R
import com.example.medcontrol.database.MedicineDao
import com.example.medcontrol.database.MedicineEntity
import com.example.medcontrol.database.NotificationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


class HomeScreenViewModel(
    private val dao: MedicineDao,
    application: Application
) : AndroidViewModel(application) {

    private val appContext: Context = getApplication<Application>().applicationContext

    val state = MutableStateFlow<ItemsListState>(ItemsListState.Loading)
    val fabState = MutableStateFlow(HomeScreenViewItem(isAddMedicineModalVisible = false))

    private val medicineList: StateFlow<List<MedicineEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    init {
        viewModelScope.launch {
            medicineList.collect { items ->
                if (items.isEmpty()) {
                    state.value = ItemsListState.Empty("test")
                } else {
                    state.value = ItemsListState.Success(makeList(items))
                }
            }
        }
    }

    private fun makeList(dbItems: List<MedicineEntity>): List<MedicineViewItem> {
        return dbItems.map { medicineEntity ->
            MedicineViewItem(
                id = medicineEntity.id,
                name = medicineEntity.name,
                notifications = makeNotificationViewItems(medicineEntity),
                nextTake = makeNextTakeString(makeNotificationViewItems(medicineEntity)),
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun makeNotificationViewItems(medicineEntity: MedicineEntity) =
        medicineEntity.notifications.map { notificationEntity ->
            NotificationViewItem(
                selectedDays = notificationEntity.selectedDays,
                isExpended = false,
                uuid = notificationEntity.uuid,
                timeState = TimePickerState(
                    notificationEntity.hour,
                    notificationEntity.minute,
                    true
                ),
                scrollState = ScrollState(0)
            )
        }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun makeNextTakeString(notifications: List<NotificationViewItem>): String? {
        val now = LocalDateTime.now()
        val today = now.dayOfWeek
        val currentTime = now.toLocalTime()

        val daysOfWeek = listOf(
            appContext.getString(R.string.monday),
            appContext.getString(R.string.tuesday),
            appContext.getString(R.string.wednesday),
            appContext.getString(R.string.thursday),
            appContext.getString(R.string.friday),
            appContext.getString(R.string.saturday),
            appContext.getString(R.string.sunday)
        )

        val nextNotification = notifications
            .flatMap { notification ->
                notification.selectedDays
                    .filter { it.value }
                    .map { day ->
                        val notificationTime =
                            LocalTime.of(notification.timeState.hour, notification.timeState.minute)
                        val dayDifference = (day.key.value - today.value + 7) % 7
                        val notificationDateTime =
                            if (dayDifference == 0 && notificationTime.isAfter(currentTime)) {
                                now.withHour(notification.timeState.hour)
                                    .withMinute(notification.timeState.minute)
                            } else {
                                now.plusDays((if (dayDifference == 0) 7 else dayDifference).toLong())
                                    .withHour(notification.timeState.hour)
                                    .withMinute(notification.timeState.minute)
                            }
                        notificationDateTime to dayDifference
                    }
            }
            .minByOrNull { it.first }

        return nextNotification?.let {
            val (dateTime, dayDifference) = it
            val timeString = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            when (dayDifference) {
                0 -> appContext.getString(R.string.today, timeString)
                1 -> appContext.getString(R.string.tomorrow, timeString)
                else -> {
                    val dayName = daysOfWeek[(today.value + dayDifference - 1) % 7]
                    if (dayDifference <= 7) {
                        appContext.getString(R.string.on_day_at_time, dayName, timeString)
                    } else {
                        appContext.getString(R.string.next_week_on_day_at_time, dayName, timeString)
                    }
                }
            }
        }
    }


    fun showAddMedicineModal() {
        fabState.update { it.copy(isAddMedicineModalVisible = true, medicineToEdit = null) }
    }

    fun dismissAddMedicineModal() {
        hideAddMedicineModal()
    }

    fun hideAddMedicineModal() {
        fabState.update { it.copy(isAddMedicineModalVisible = false) }
    }

    fun addMedicine(medicine: MedicineViewItem) {
        viewModelScope.launch {
            val medicineEntity = toMedicineEntity(medicine)
            dao.insertMedicine(medicineEntity)
        }

        scheduleNotification(appContext, medicine)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun toMedicineEntity(viewItem: MedicineViewItem): MedicineEntity {
        val entity = MedicineEntity(
            id = viewItem.id,
            name = viewItem.name,
            notifications = viewItem.notifications.map { notificationViewItem ->
                NotificationEntity(
                    selectedDays = notificationViewItem.selectedDays,
                    hour = notificationViewItem.timeState.hour,
                    minute = notificationViewItem.timeState.minute,
                    uuid = notificationViewItem.uuid
                )
            }
        )

        return entity
    }

    fun showMedicineDetails(item: MedicineViewItem) {
        fabState.update { it.copy(isAddMedicineModalVisible = true, medicineToEdit = item) }
    }

    fun updateMedicine(item: MedicineViewItem) {
        viewModelScope.launch {
            dao.updateMedicine(toMedicineEntity(item))
        }
    }

    fun deleteMedicine(item: MedicineViewItem) {
        viewModelScope.launch {
            dao.deleteMedicine(item.id)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    @OptIn(ExperimentalMaterial3Api::class)
    fun scheduleNotification(context: Context, medicine: MedicineViewItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        medicine.notifications.forEach { notification ->
            notification.selectedDays.forEach { (dayOfWeek, isSelected) ->
                if (isSelected) {

                    val uuid = notification.uuid.hashCode() + dayOfWeek.value

                    val intent = Intent(context, AlarmReceiver::class.java).apply {
                        putExtra("notification_id", uuid)
                        putExtra("medicine_name", medicine.name)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        uuid,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        set(Calendar.HOUR_OF_DAY, notification.timeState.hour)
                        set(Calendar.MINUTE, notification.timeState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        set(
                            Calendar.DAY_OF_WEEK,
                            (dayOfWeek.value % 7) + 1
                        ) // Adjusting the day of the week

                        // If the set time is before the current time, schedule it for the next week
                        if (before(Calendar.getInstance())) {
                            add(Calendar.WEEK_OF_YEAR, 1)
                        }
                    }

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )

                }
            }
        }
    }

}