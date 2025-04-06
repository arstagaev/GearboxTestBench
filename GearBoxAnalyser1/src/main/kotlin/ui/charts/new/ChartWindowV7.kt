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
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import utils.logInfo
import java.io.File
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Reads a log file and creates an XYSeriesCollection.
 *
 * Expected file format:
 *
 * #standard;standard_file.txt
 * #visibility;true
 * 3846;3820;1472;3263;3095;3370;3502;3201;3106;2482;500;391;2051;2035;1362;1835
 * 2079;2874;2516;2733;3480;2249;3122;3193;2381;442;3818;3075;478;294;3233;1025
 * ...
 *
 * This function ignores header lines (starting with '#') and parses each data line,
 * adding the value from each column (1–16) into its corresponding XYSeries.
 * The X value is the line (sample) index.
 */
private fun createDatasetFromLogFile(file: File): XYSeriesCollection {
    val seriesList = List(16) { index -> XYSeries("Series ${index + 1}") }
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

/**
 * Creates a JFreeChart line chart from the given dataset.
 */
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

/**
 * Computes the FFT (spectral analysis) for a given XYSeries.
 *
 * This function performs a naive Discrete Fourier Transform on the series' Y values,
 * returning a new XYSeries whose X values are the frequency index (0..N/2)
 * and whose Y values are the magnitude of the Fourier transform (normalized).
 */
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

// Data class holding all statistics for a series.
private data class SeriesStats2(val min: Double, val max: Double, val avg: Double, val median: Double)

// Helper function to round a double to a specified number of decimals.
private fun roundValue(value: Double, decimals: Int): Double {
    val factor = 10.0.pow(decimals.toDouble())
    return round(value * factor) / factor
}

/**
 * Computes the min, max, average, and median values for the given XYSeries,
 * rounding min and max to 2 decimals and avg and median to 3 decimals.
 */
private fun computeStatData(series: XYSeries): SeriesStats2 {
    val values = (0 until series.itemCount).map { series.getY(it).toDouble() }
    if (values.isEmpty()) return SeriesStats2(0.0, 0.0, 0.0, 0.0)
    val minRaw = values.minOrNull() ?: 0.0
    val maxRaw = values.maxOrNull() ?: 0.0
    val avgRaw = values.average()
    val sorted = values.sorted()
    val medianRaw = if (sorted.size % 2 == 1) {
        sorted[sorted.size / 2]
    } else {
        (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
    }
    val min = roundValue(minRaw, 2)
    val max = roundValue(maxRaw, 2)
    val avg = roundValue(avgRaw, 3)
    val median = roundValue(medianRaw, 3)
    return SeriesStats2(min, max, avg, median)
}

@Composable
private fun JFreeChartDemoWithToggles() {
    // Path to your log file.
    val file = File("generated_chart.txt")
    // Create the full raw dataset.
    val fullDataset = createDatasetFromLogFile(file)
    // Extract the original series.
    val allSeries = remember {
        mutableStateListOf<XYSeries>().apply {
            for (i in 0 until fullDataset.seriesCount) {
                add(fullDataset.getSeries(i))
            }
        }
    }
    val seriesCount = allSeries.size

    // Visibility state for each series (all initially true).
    val visibleStates = remember { mutableStateListOf<Boolean>().apply { repeat(seriesCount) { add(true) } } }

    // Toggle for displaying spectral analysis.
    var showSpectral by remember { mutableStateOf(false) }

    // Derived state: rebuild the dataset from visible series.
    // If spectral analysis is on, compute FFT for each visible series.
    val currentDataset by derivedStateOf {
        XYSeriesCollection().apply {
            for (i in allSeries.indices) {
                if (visibleStates[i]) {
                    if (showSpectral)
                        addSeries(computeSpectralData(allSeries[i]))
                    else
                        addSeries(allSeries[i])
                }
            }
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var scrollPositionSeries by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Switcher row for selecting Raw vs Spectral view.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .weight(0.5f),
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
        // Row with series toggles and statistics.
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left/Right scroll buttons.
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(20.dp).clickable {
                    coroutineScope.launch {
                        scrollPositionSeries--
                        if (scrollPositionSeries in 0 until seriesCount) {
                            listState.animateScrollToItem(index = scrollPositionSeries)
                        } else {
                            scrollPositionSeries = 0
                        }
                        logInfo("new scroll: $scrollPositionSeries")
                    }
                }) {
                    Text("<")
                }
                Box(modifier = Modifier.size(20.dp).clickable {
                    coroutineScope.launch {
                        scrollPositionSeries++
                        if (scrollPositionSeries in 0 until seriesCount) {
                            listState.animateScrollToItem(index = scrollPositionSeries)
                        } else {
                            scrollPositionSeries = seriesCount - 1
                        }
                        logInfo("new scroll: $scrollPositionSeries")
                    }
                }) {
                    Text(">")
                }
            }
            // Toggle controls for each series.
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .weight(10f),
                state = listState
            ) {
                for (i in 0 until seriesCount) {
                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .background(if (visibleStates[i]) Color.Green else Color.LightGray)
                                .clickable { visibleStates[i] = !visibleStates[i] },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Build annotated string with series index and stats.
                            val statData = computeStatData(allSeries[i])
                            Text(
                                fontSize = TextUnit(10f, TextUnitType.Sp),
                                text = buildAnnotatedString {
                                    withStyle(style = ParagraphStyle(lineHeight = 10.sp)) {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Blue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("s. ${i + 1}")
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
        Spacer(modifier = Modifier.height(8.dp))
        // Re-create ChartPanel when currentDataset changes.
        key(currentDataset) {
            SwingPanel(
                modifier = Modifier.fillMaxSize().weight(10f),
                factory = { ChartPanel(createLineChart(currentDataset)) }
            )
        }
    }
}

@Composable
@Preview
private fun PreviewJFreeChartDemoWithToggles() {
    MaterialTheme {
        JFreeChartDemoWithToggles()
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Analysis") {
        MaterialTheme {
            JFreeChartDemoWithToggles()
        }
    }
}
