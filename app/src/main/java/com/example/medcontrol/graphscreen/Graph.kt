package com.example.medcontrol.graphscreen

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Chart(
    context: Context,
    labels: List<String>,
    textColor: Color,
    gridColor: Color,
) {
    private val chart = LineChart(context)

    private val isGridLines = false
    private val gridWidth = .8f

    init {
        // chart settings
        chart.setDrawGridBackground(false)
        chart.isDoubleTapToZoomEnabled = false
        chart.setNoDataText("");

        // description settings
        val description = Description()
        description.isEnabled = false
        this.chart.description = description

        // X axis settings
        val xAxis: XAxis = this.chart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(isGridLines)
        xAxis.gridLineWidth = gridWidth
        xAxis.position = XAxis.XAxisPosition.BOTTOM;
        xAxis.isEnabled = true
        xAxis.textColor = textColor.toArgb()
        xAxis.gridColor = gridColor.toArgb()

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < labels.size) {
                    labels[index]
                } else {
                    ""
                }
            }
        }

        chart.xAxis.labelRotationAngle = -45f
        xAxis.granularity = .1f
        xAxis.isGranularityEnabled = true

        // Y right axis settings
        val rightAxis = chart.axisRight
        rightAxis.setDrawAxisLine(true)
        rightAxis.setDrawGridLines(true)
        rightAxis.isEnabled = false
        rightAxis.textColor = textColor.toArgb()
        rightAxis.gridColor = gridColor.toArgb()

        // Y left axis settings
        val leftAxis: YAxis = chart.axisLeft
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(isGridLines)
        leftAxis.setDrawLabels(true)
        leftAxis.gridLineWidth = gridWidth
        leftAxis.isEnabled = true
        leftAxis.textColor = textColor.toArgb()
        leftAxis.gridColor = gridColor.toArgb()

        // animations
//        chart.animateX(400)

        // legend settings
        val legend = chart.legend
        legend.isEnabled = false

    }

    fun getChart(): LineChart {
        return chart
    }

}

fun reRenderChart(
    chart: LineChart,
    labels: List<String>,
    textColor: Color,
    gridColor: Color
) {
    chart.xAxis.textColor = textColor.toArgb()
    chart.axisRight.textColor = textColor.toArgb()
    chart.axisLeft.textColor = textColor.toArgb()

    chart.xAxis.gridColor = gridColor.toArgb()
    chart.axisRight.gridColor = gridColor.toArgb()
    chart.axisLeft.gridColor = gridColor.toArgb()

    chart.xAxis.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index >= 0 && index < labels.size) {
                labels[index]
            } else {
                ""
            }
        }
    }
}

fun showSet(
    chart: LineChart,
    dataSet: List<Entry>,
    limitLineValue: Float? = null,
    drawCircles: Boolean,
    dataSetsColor: Color,
    highLightColor: Color,
    limitLineColor: Color,
    circleColor: Color,
    textSize: Dp,
    lineWidth: Float,
    circleRadius: Float,
    isFillColor: Boolean,
    fillColor: Color,
    isDrawValues: Boolean,
    valueTextColor: Color
): LineChart {

    val data = LineData()

    val set = LineDataSet(dataSet, "")
    set.color = dataSetsColor.toArgb()
    set.setDrawCircleHole(false)
    set.setDrawIcons(false)
    set.setDrawValues(isDrawValues)
    set.valueTextColor = valueTextColor.toArgb()
    set.valueTextSize = textSize.value
    set.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return " ".repeat(4) + value.toString()
        }
    }
    set.lineWidth = lineWidth
    set.circleRadius = circleRadius
    set.setDrawFilled(isFillColor)
    set.fillColor = fillColor.toArgb()
    set.setDrawCircles(drawCircles)
    set.setCircleColor(circleColor.toArgb())
    set.mode = LineDataSet.Mode.CUBIC_BEZIER
    set.highLightColor = highLightColor.toArgb()
    set.highlightLineWidth = 2f
    set.setDrawHorizontalHighlightIndicator(false)
    set.setDrawHighlightIndicators(false)

    data.addDataSet(set)

    chart.data = data

    chart.axisLeft.removeAllLimitLines()

    // Add horizontal limit line if limitLineValue is provided
    if(limitLineValue != null) {
        val line = LimitLine(limitLineValue)
        line.lineWidth = 2f
        line.lineColor = limitLineColor.toArgb()
        chart.axisLeft.addLimitLine(line)
    }

    chart.invalidate()

    return chart
}

fun showTwoSets(
    chart: LineChart,
    dataSet1: List<Entry>,
    dataSet2: List<Entry>,
    limitLineValue: Float? = null,
    drawCircles: Boolean,
    dataSet1Color: Color,
    dataSet2Color: Color,
    highLightColor: Color,
    limitLineColor: Color,
    circleColor: Color,
    textSize: Dp,
    lineWidth: Float,
    circleRadius: Float,
    isFillColor: Boolean,
    fillColor: Color,
    isDrawValues: Boolean,
    valueTextColor: Color
): LineChart {

    val data = LineData()

    // Set up the first data set
    val set1 = LineDataSet(dataSet1, "")
    set1.color = dataSet1Color.toArgb()
    set1.setDrawCircleHole(false)
    set1.setDrawIcons(false)
    set1.setDrawValues(isDrawValues)
    set1.valueTextColor = valueTextColor.toArgb()
    set1.valueTextSize = textSize.value
    set1.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return " ".repeat(4) + value.toString()
        }
    }
    set1.lineWidth = lineWidth
    set1.circleRadius = circleRadius
    set1.setDrawFilled(isFillColor)
    set1.fillColor = fillColor.toArgb()
    set1.setDrawCircles(drawCircles)
    set1.setCircleColor(circleColor.toArgb())
    set1.mode = LineDataSet.Mode.CUBIC_BEZIER
    set1.highLightColor = highLightColor.toArgb()
    set1.highlightLineWidth = 2f
    set1.setDrawHorizontalHighlightIndicator(false)
    set1.setDrawHighlightIndicators(false)

    data.addDataSet(set1)

    // Set up the second data set
    val set2 = LineDataSet(dataSet2, "")
    set2.color = dataSet2Color.toArgb()
    set2.setDrawCircleHole(false)
    set2.setDrawIcons(false)
    set2.setDrawValues(isDrawValues)
    set2.valueTextColor = valueTextColor.toArgb()
    set2.valueTextSize = textSize.value
    set2.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return " ".repeat(4) + value.toString()
        }
    }
    set2.lineWidth = lineWidth
    set2.circleRadius = circleRadius
    set2.setDrawFilled(isFillColor)
    set2.fillColor = fillColor.toArgb()
    set2.setDrawCircles(drawCircles)
    set2.setCircleColor(circleColor.toArgb())
    set2.mode = LineDataSet.Mode.CUBIC_BEZIER
    set2.highLightColor = highLightColor.toArgb()
    set2.highlightLineWidth = 2f
    set2.setDrawHorizontalHighlightIndicator(false)
    set2.setDrawHighlightIndicators(false)

    data.addDataSet(set2)

    chart.data = data

    chart.axisLeft.removeAllLimitLines()

    // Add horizontal limit line if limitLineValue is provided
    if (limitLineValue != null) {
        val line = LimitLine(limitLineValue)
        line.lineWidth = 2f
        line.lineColor = limitLineColor.toArgb()
        chart.axisLeft.addLimitLine(line)
    }

    chart.invalidate()

    return chart
}


fun showMultipleSets(
    chart: LineChart,
    dataSets: List<List<Entry>>,
    limitLineValue: Float? = null,
    drawCircles: Boolean,
    dataSetsColor: Color,
    highLightColor: Color,
    limitLineColor: Color,
    circleColor: Color
): LineChart {

    val data = LineData()

    dataSets.forEach { entrySet ->
        val set = LineDataSet(entrySet, "")
        set.color = dataSetsColor.toArgb()
        set.setDrawCircleHole(false)
        set.setDrawIcons(false)
        set.setDrawValues(false)
        set.lineWidth = 3f
        set.circleRadius = 3f
        set.setDrawFilled(false)
        set.setDrawCircles(drawCircles)
        set.setCircleColor(circleColor.toArgb())
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        // set.enableDashedHighlightLine(10f, 5f, 0f)
        set.highLightColor = highLightColor.toArgb()
        set.highlightLineWidth = 2f
        set.setDrawHorizontalHighlightIndicator(false)

        data.addDataSet(set)
    }

    chart.data = data

    chart.axisLeft.removeAllLimitLines()

    // add line horizontal line
    if(limitLineValue != null) {
        val line = LimitLine(limitLineValue)
        line.lineWidth = 2f;
        line.lineColor = limitLineColor.toArgb()
        chart.axisLeft.addLimitLine(line)
    }

    chart.invalidate()

    return chart
}

fun showMultipleSetsWithAvgMinMax(
    chart: LineChart,
    dataSetAvg: List<List<Entry>>?,
    dataSetMin: List<List<Entry>>?,
    dataSetMax: List<List<Entry>>?,
    limitLineValue: Float? = null,
    drawCircles: Boolean,
    dataSetAvgColor: Color,
    dataSetMinColor: Color,
    dataSetMaxColor: Color,
    highLightColor: Color,
    limitLineColor: Color,
    circleColor: Color,
    invalidateChart: Boolean
): LineChart {

    val data = LineData()

    val dataSets = mapOf(
        dataSetAvg to dataSetAvgColor,
        dataSetMin to dataSetMinColor,
        dataSetMax to dataSetMaxColor
    )

    dataSets.forEach { (set, color) ->

        if(set == null){
            return@forEach
        }

        set.forEach { entrySet ->
            val set = LineDataSet(entrySet, "")
            set.color = color.toArgb()
            set.setDrawCircleHole(false)
            set.setDrawIcons(false)
            set.setDrawValues(false)
            set.lineWidth = 3f
            set.circleRadius = 3f
            set.setDrawFilled(false)
            set.setDrawCircles(drawCircles)
            set.setCircleColor(circleColor.toArgb())
            set.mode = LineDataSet.Mode.CUBIC_BEZIER
            set.highLightColor = highLightColor.toArgb()
            set.highlightLineWidth = 2f
            set.setDrawHorizontalHighlightIndicator(false)

            data.addDataSet(set)
        }
    }


    chart.data = data

    chart.axisLeft.removeAllLimitLines()

    // add line horizontal line
    if(limitLineValue != null) {
        val line = LimitLine(limitLineValue)
        line.lineWidth = 2f;
        line.lineColor = limitLineColor.toArgb()
        chart.axisLeft.addLimitLine(line)
    }

    if(invalidateChart)
        chart.invalidate()

    return chart
}