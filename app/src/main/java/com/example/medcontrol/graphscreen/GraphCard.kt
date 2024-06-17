package com.example.medcontrol.graphscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.Entry

data class GraphData(
    val entries: List<Entry>,
    val labels: List<String>
)

@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.clickableWithoutRipple(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) = composed(
    factory = {
        this.then(
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick() }
            )
        )
    }
)

@Composable
fun GraphCard(
    title: String,
    chartData: GraphData,
    secondChartData: GraphData? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val dataSetsColor = MaterialTheme.colorScheme.primaryContainer
    val fillColor = MaterialTheme.colorScheme.secondaryContainer
    val circleColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val gridColor = MaterialTheme.colorScheme.onSurface

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp)
            .clickableWithoutRipple(
                interactionSource = interactionSource,
                onClick = {}
            )
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
                    labels = chartData.labels,
                    textColor = textColor,
                    gridColor = gridColor
                )
                    .getChart()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(top = 0.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
            update = { chart ->

                if (secondChartData == null)
                    showSet(
                        chart = chart,
                        dataSet = chartData.entries,
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
                        valueTextColor = textColor
                    )
                else
                    showTwoSets(
                        chart = chart,
                        dataSet1 = chartData.entries,
                        dataSet2 = secondChartData.entries,
                        dataSet1Color = dataSetsColor,
                        dataSet2Color = dataSetsColor,
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
                        valueTextColor = textColor
                    )

                reRenderChart(
                    chart = chart,
                    labels = chartData.labels,
                    textColor = textColor,
                    gridColor = gridColor
                )

            }
        )

    }


}