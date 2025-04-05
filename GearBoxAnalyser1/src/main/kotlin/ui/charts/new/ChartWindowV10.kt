package ui.charts.new

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.ValueMarker
import org.jfree.chart.plot.XYPlot
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utils.logInfo
import java.awt.BasicStroke
import java.awt.Color as AwtColor
import java.io.File
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

// ---- Data Loading and Chart Creation ----

// Creates a dataset from a log file (ignores header lines starting with '#').
// The optional prefix is added to each series key.
private fun createDatasetFromLogFile(file: File, prefix: String = ""): XYSeriesCollection {
    val seriesList = List(16) { index -> XYSeries("$prefix" + "Series ${index + 1}") }
    file.forEachLine { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
            val items = trimmed.split(";")
            if (items.size >= 16) {
                val xValue = seriesList[0].itemCount.toDouble()
                for (i in 0 until 16) {
                    val yValue = items[i].toIntOrNull() ?: 0
                    seriesList[i].add(xValue, yValue.toDouble())
                }
            }
        }
    }
    return XYSeriesCollection().apply { seriesList.forEach { addSeries(it) } }
}

// Creates a JFreeChart line chart from the given dataset.
private fun createLineChart(dataset: XYSeriesCollection): JFreeChart {
    return ChartFactory.createXYLineChart(
        "Log File Line Chart",   // chart title
        "Sample Index",          // x-axis label
        "Value",                 // y-axis label
        dataset,                 // data
        PlotOrientation.VERTICAL,
        true,                    // include legend
        true,                    // tooltips
        false                    // urls
    )
}

// ---- Spectral Analysis Helper ----

// Computes a naive DFT on a series' Y values, returning a new series of (frequency, magnitude)
private fun computeSpectralData(series: XYSeries): XYSeries {
    val N = series.itemCount
    val yValues = (0 until N).map { series.getY(it).toDouble() }
    val spectralSeries = XYSeries("${series.key} FFT")
    val halfN = N / 2
    for (k in 0..halfN) {
        var re = 0.0
        var im = 0.0
        for (n in 0 until N) {
            val angle = 2 * Math.PI * k * n / N
            re += yValues[n] * cos(angle)
            im -= yValues[n] * sin(angle)
        }
        val magnitude = sqrt(re * re + im * im) / N
        spectralSeries.add(k.toDouble(), magnitude)
    }
    return spectralSeries
}

// ---- Statistics & Correlation Helpers ----

// Data class holding statistics for a series.
private data class SeriesStats5(val min: Double, val max: Double, val avg: Double, val median: Double)

// Rounds a value to a specified number of decimals.
private fun roundValue(value: Double, decimals: Int): Double {
    val factor = 10.0.pow(decimals.toDouble())
    return round(value * factor) / factor
}

// Computes min, max, average, and median for a series, with rounding.
private fun computeStatData(series: XYSeries): SeriesStats5 {
    val values = (0 until series.itemCount).map { series.getY(it).toDouble() }
    if (values.isEmpty()) return SeriesStats5(0.0, 0.0, 0.0, 0.0)
    val minRaw = values.minOrNull() ?: 0.0
    val maxRaw = values.maxOrNull() ?: 0.0
    val avgRaw = values.average()
    val sorted = values.sorted()
    val medianRaw = if (sorted.size % 2 == 1) sorted[sorted.size / 2]
    else (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
    val min = roundValue(minRaw, 2)
    val max = roundValue(maxRaw, 2)
    val avg = roundValue(avgRaw, 3)
    val median = roundValue(medianRaw, 3)
    return SeriesStats5(min, max, avg, median)
}

// Computes Pearson correlation coefficient between two series.
private fun pearsonCorrelation(series1: XYSeries, series2: XYSeries): Double {
    val n = minOf(series1.itemCount, series2.itemCount)
    if (n <= 1) return 0.0
    val values1 = (0 until n).map { series1.getY(it).toDouble() }
    val values2 = (0 until n).map { series2.getY(it).toDouble() }
    val mean1 = values1.average()
    val mean2 = values2.average()
    val numerator = values1.zip(values2).sumByDouble { (x, y) -> (x - mean1) * (y - mean2) }
    val denominator = sqrt(values1.sumByDouble { (it - mean1).pow(2.0) } * values2.sumByDouble { (it - mean2).pow(2.0) })
    return if (denominator == 0.0) 0.0 else numerator / denominator
}

// Returns a color interpolated between red and green based on the correlation value.
// Closer to 1.0 gives green; closer to 0.0 gives red.
private fun correlationColor(corr: Double): Color {
    val clamped = corr.coerceIn(0.0, 1.0)
    val red = (1.0 - clamped).toFloat()
    val green = clamped.toFloat()
    return Color(red, green, 0f)
}

// ---- Threshold Parsing ----

@Serializable
data class ChartThresholds(val top: Double, val bottom: Double)

// Parses a JSON file and returns the thresholds.
private fun loadThresholdsFromJson(file: File): ChartThresholds {
    val jsonString = file.readText()
    // Optionally, configure the JSON parser (e.g., ignore unknown keys)
    return Json { ignoreUnknownKeys = true }.decodeFromString(jsonString)
}

// ---- Main Composable ----

@Composable
private fun JFreeChartDemoWithMultipleFiles() {
    // Files for two datasets.
    val file1 = File("standard_file.txt")
    val file2 = File("visibility_file.txt")
    // File for thresholds.
    val thresholdsFile = File("thresholds.json")
    val thresholds = remember { loadThresholdsFromJson(thresholdsFile) }

    // Create datasets.
    val fullDataset1 = createDatasetFromLogFile(file1)  // keys: "Series 1", ..., "Series 16"
    val fullDataset2 = createDatasetFromLogFile(file2, "F2 ") // keys: "F2 Series 1", ..., "F2 Series 16"

    // Extract series.
    val allSeries1 = remember {
        mutableStateListOf<XYSeries>().apply {
            for (i in 0 until fullDataset1.seriesCount) {
                add(fullDataset1.getSeries(i))
            }
        }
    }
    val allSeries2 = remember {
        mutableStateListOf<XYSeries>().apply {
            for (i in 0 until fullDataset2.seriesCount) {
                add(fullDataset2.getSeries(i))
            }
        }
    }
    val seriesCount1 = allSeries1.size
    val seriesCount2 = allSeries2.size

    // Visibility states.
    val visibleStates1 = remember { mutableStateListOf<Boolean>().apply { repeat(seriesCount1) { add(true) } } }
    val visibleStates2 = remember { mutableStateListOf<Boolean>().apply { repeat(seriesCount2) { add(true) } } }

    // Toggle for spectral analysis.
    var showSpectral by remember { mutableStateOf(false) }

    // Derived state: combine visible series from both files.
    val currentDataset by derivedStateOf {
        XYSeriesCollection().apply {
            for (i in allSeries1.indices) {
                if (visibleStates1[i]) {
                    if (showSpectral)
                        addSeries(computeSpectralData(allSeries1[i]))
                    else
                        addSeries(allSeries1[i])
                }
            }
            for (i in allSeries2.indices) {
                if (visibleStates2[i]) {
                    if (showSpectral)
                        addSeries(computeSpectralData(allSeries2[i]))
                    else
                        addSeries(allSeries2[i])
                }
            }
        }
    }

    // Derived state for Pearson correlations (for corresponding series).
    val correlationList by derivedStateOf {
        val n = minOf(seriesCount1, seriesCount2)
        List(n) { i -> pearsonCorrelation(allSeries1[i], allSeries2[i]) }
    }

    // LazyRow states for toggle rows.
    val listState1 = rememberLazyListState()
    val listState2 = rememberLazyListState()
    val listStateCorr = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var scrollPositionSeries1 by remember { mutableStateOf(0) }
    var scrollPositionSeries2 by remember { mutableStateOf(0) }
    var scrollPositionCorr by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Switcher row for Raw vs Spectral view.
        Row(
            modifier = Modifier.fillMaxWidth().padding(2.dp).weight(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Display: ", fontSize = TextUnit(12f, TextUnitType.Sp))
            Text(
                text = if (showSpectral) "Spectral Analysis" else "Raw Data",
                modifier = Modifier.clickable { showSpectral = !showSpectral },
                color = Color.Blue,
                fontSize = TextUnit(12f, TextUnitType.Sp)
            )
        }
        // Row for toggles for File 1 series.
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text("File 1 Series", fontSize = TextUnit(12f, TextUnitType.Sp))
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(1.dp),
                state = listState1
            ) {
                for (i in 0 until seriesCount1) {
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .background(if (visibleStates1[i]) Color.Green else Color.LightGray)
                                .clickable { visibleStates1[i] = !visibleStates1[i] },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val statData = computeStatData(allSeries1[i])
                            Text(
                                fontSize = TextUnit(10f, TextUnitType.Sp),
                                text = buildAnnotatedString {
                                    withStyle(style = ParagraphStyle(lineHeight = 10.sp)) {
                                        withStyle(
                                            style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)
                                        ) {
                                            append("F1 s. ${i + 1}")
                                        }
                                        append("\nmin = ${statData.min}")
                                        append("\nmax = ${statData.max}")
                                        append("\navg = ${statData.avg}")
                                        append("\nmed = ${statData.median}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        // Row for toggles for File 2 series.
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text("File 2 Series", fontSize = TextUnit(12f, TextUnitType.Sp))
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(1.dp),
                state = listState2
            ) {
                for (i in 0 until seriesCount2) {
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .background(if (visibleStates2[i]) Color.Green else Color.LightGray)
                                .clickable { visibleStates2[i] = !visibleStates2[i] },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val statData = computeStatData(allSeries2[i])
                            Text(
                                fontSize = TextUnit(10f, TextUnitType.Sp),
                                text = buildAnnotatedString {
                                    withStyle(style = ParagraphStyle(lineHeight = 10.sp)) {
                                        withStyle(
                                            style = SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)
                                        ) {
                                            append("F2 s. ${i + 1}")
                                        }
                                        append("\nmin = ${statData.min}")
                                        append("\nmax = ${statData.max}")
                                        append("\navg = ${statData.avg}")
                                        append("\nmed = ${statData.median}")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        // Row for correlation between corresponding File 1 and File 2 series.
        Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Text(
                "Correlation (Pearson) for corresponding series",
                fontSize = TextUnit(12f, TextUnitType.Sp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(1.dp),
                state = listStateCorr
            ) {
                val n = minOf(seriesCount1, seriesCount2)
                for (i in 0 until n) {
                    item {
                        val corr = correlationList[i]
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .background(correlationColor(corr))
                                .clickable { },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Corr s.${i + 1}",
                                fontSize = TextUnit(10f, TextUnitType.Sp)
                            )
                            Text(
                                text = "${roundValue(corr, 2)}",
                                fontSize = TextUnit(10f, TextUnitType.Sp)
                            )
                        }
                    }
                }
            }
        }
        // Row for threshold markers (if desired, you could display current thresholds here).
        // For this example, we simply display the loaded thresholds.
        Column(modifier = Modifier.fillMaxWidth().weight(0.5f), horizontalAlignment = Alignment.CenterHorizontally) {
            val thresholds = remember { loadThresholdsFromJson(File("thresholds.json")) }
            Text("Thresholds: top = ${thresholds.top}, bottom = ${thresholds.bottom}", fontSize = TextUnit(12f, TextUnitType.Sp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Re-create ChartPanel when currentDataset changes.
        key(currentDataset) {
            SwingPanel(
                modifier = Modifier.fillMaxSize().weight(10f),
                factory = {
                    // Create chart, then add threshold markers.
                    val chart = createLineChart(currentDataset)
                    val plot = chart.plot as? XYPlot
                    if (plot != null) {
                        val topMarker = ValueMarker(thresholds.top)
                        topMarker.paint = AwtColor.BLUE
                        topMarker.stroke = BasicStroke(2.0f)
                        plot.addRangeMarker(topMarker)
                        val bottomMarker = ValueMarker(thresholds.bottom)
                        bottomMarker.paint = AwtColor.RED
                        bottomMarker.stroke = BasicStroke(2.0f)
                        plot.addRangeMarker(bottomMarker)
                    }
                    ChartPanel(chart)
                }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewJFreeChartDemoWithMultipleFiles() {
    MaterialTheme {
        JFreeChartDemoWithMultipleFiles()
    }
}

private fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Analysis") {
        MaterialTheme {
            JFreeChartDemoWithMultipleFiles()
        }
    }
}
