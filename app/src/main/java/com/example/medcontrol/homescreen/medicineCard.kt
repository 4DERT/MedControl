package com.example.medcontrol.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//data class TakeDateViewItem(
//    val hour: Int,
//    val minute: Int,
//    val dayList: List<Pair<String, Boolean>>,
//)
//
//data class MedicineViewItem(
//    val name: String,
//    val nextTake: String,
//    val dates: List<TakeDateViewItem>
//)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineCard(
    data: MedicineViewItem
) {
    Card(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .fillMaxWidth()

    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = data.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = "NEXT TAKE TODO")
            }

        }


    }
}