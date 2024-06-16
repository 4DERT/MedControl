package com.example.medcontrol.graphscreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry

@Composable
fun GraphCard(
    title: String,
    chartData: List<Entry>
) {

    val dataSetsColor = MaterialTheme.colorScheme.primaryContainer
    val fillColor = MaterialTheme.colorScheme.secondaryContainer
    val circleColor = MaterialTheme.colorScheme.primary

    OutlinedCard(
        onClick = { },
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(12.dp)
        )

        AndroidView(
            factory = { context ->
                Chart(
                    context = context,
                )
                    .getChart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(top = 0.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
            update = { chart ->

                showSet(
                    chart = chart,
                    dataSet = chartData,
                    dataSetsColor = dataSetsColor,
                    circleColor = circleColor,
                    drawCircles = true,
                    highLightColor = Color.Transparent,
                    limitLineColor = Color.Transparent,
                    textSize = 13.dp,
                    lineWidth = 4f,
                    circleRadius = 3.8f,
                    limitLineValue = null,
                    fillColor = fillColor,
                    isFillColor = true,
                    isDrawValues = true,
                    valueTextColor = Color.Black
                )

            }
        )
    }



}