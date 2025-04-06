package ui.charts.new

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine

// Data class to hold summary statistics for one array.
private data class SummaryStats3(val min: Float, val max: Float, val avg: Float, val median: Float)

// Helper function to compute summary stats for a list of floats.
private fun computeSummaryStats(data: List<Float>): SummaryStats3 {
    if (data.isEmpty()) return SummaryStats3(0f, 0f, 0f, 0f)
    val sorted = data.sorted()
    val minVal = sorted.first()
    val maxVal = sorted.last()
    val avgVal = data.average().toFloat()
    val medianVal = if (sorted.size % 2 == 1) {
        sorted[sorted.size / 2]
    } else {
        val mid = sorted.size / 2
        (sorted[mid - 1] + sorted[mid]) / 2f
    }
    return SummaryStats3(minVal, maxVal, avgVal, medianVal)
}

/**
 * SummaryLineChart draws a line chart where, for each array,
 * the minimum, maximum, average, and median values are computed and then connected
 * by lines (without filling).
 *
 * The X axis shows the array index ("Arr 1", "Arr 2", etc.) and the Y axis is scaled
 * according to the overall range of the computed statistics.
 *
 * @param dataArrays List of arrays (each a list of Floats). The chart can display 1 to 10 arrays.
 * @param modifier Modifier for sizing the chart.
 */
@Composable
private fun SummaryLineChart(
    dataArrays: List<List<Float>>,
    modifier: Modifier = Modifier
) {
    // Compute summary stats for each array.
    val summaries = dataArrays.map { computeSummaryStats(it) }

    // Determine overall Y range from all statistics.
    val allValues = summaries.flatMap { listOf(it.min, it.max, it.avg, it.median) }
    val overallMin = allValues.minOrNull() ?: 0f
    val overallMax = allValues.maxOrNull() ?: 1f
    val range = if (overallMax - overallMin < 1e-3f) 1f else (overallMax - overallMin)

    // Define colors for each statistic line.
    val minColor = Color.Red
    val maxColor = Color.Green
    val avgColor = Color.Blue
    val medianColor = Color(0xFFFFA500) // Orange

    Canvas(modifier = modifier.fillMaxWidth().height(400.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Margins for the chart area.
        val leftMargin = 50f
        val bottomMargin = 30f
        val topMargin = 10f

        // Draw Y axis (vertical) along the left and X axis (horizontal) along the bottom.
        drawLine(color = Color.Black, start = Offset(leftMargin, topMargin), end = Offset(leftMargin, canvasHeight - bottomMargin), strokeWidth = 2f)
        drawLine(color = Color.Black, start = Offset(leftMargin, canvasHeight - bottomMargin), end = Offset(canvasWidth, canvasHeight - bottomMargin), strokeWidth = 2f)

        // Draw horizontal grid lines and Y axis labels.
        val tickCountY = 5
        drawIntoCanvas { canvas ->
            val textPaint = Paint().apply { color = org.jetbrains.skia.Color.BLACK }
            for (i in 0..tickCountY) {
                val y = (canvasHeight - bottomMargin) - i * ((canvasHeight - bottomMargin - topMargin) / tickCountY)
                val value = overallMin + i * (range / tickCountY)
                drawLine(Color.LightGray, Offset(leftMargin, y), Offset(canvasWidth, y), strokeWidth = 1f)
                val textLine = TextLine.make(String.format("%.2f", value), Font().apply { size = 24f })
                canvas.nativeCanvas.drawTextLine(textLine, 5f, y + 10f, textPaint)
            }
        }

        // X axis: one point per data array.
        val groupCount = summaries.size
        // Spacing between groups.
        val xSpacing = (canvasWidth - leftMargin) / (groupCount + 1)

        // Function to map a value to a Y coordinate.
        fun valueToY(v: Float): Float {
            val normalized = (v - overallMin) / range
            return (canvasHeight - bottomMargin) - normalized * (canvasHeight - bottomMargin - topMargin)
        }

        // Create paths for each statistic series.
        val minPath = Path()
        val maxPath = Path()
        val avgPath = Path()
        val medianPath = Path()

        summaries.forEachIndexed { index, stats ->
            val x = leftMargin + (index + 1) * xSpacing
            val yMin = valueToY(stats.min)
            val yMax = valueToY(stats.max)
            val yAvg = valueToY(stats.avg)
            val yMedian = valueToY(stats.median)

            if (index == 0) {
                minPath.moveTo(x, yMin)
                maxPath.moveTo(x, yMax)
                avgPath.moveTo(x, yAvg)
                medianPath.moveTo(x, yMedian)
            } else {
                minPath.lineTo(x, yMin)
                maxPath.lineTo(x, yMax)
                avgPath.lineTo(x, yAvg)
                medianPath.lineTo(x, yMedian)
            }
        }

        // Draw the statistic lines as strokes (lines only, no fill).
        drawPath(path = minPath, color = minColor, style = Stroke(width = 3f))
        drawPath(path = maxPath, color = maxColor, style = Stroke(width = 3f))
        drawPath(path = avgPath, color = avgColor, style = Stroke(width = 3f))
        drawPath(path = medianPath, color = medianColor, style = Stroke(width = 3f))

        // Draw X axis ticks and labels.
        drawIntoCanvas { canvas ->
            val textPaint = Paint().apply { color = org.jetbrains.skia.Color.BLACK }
            summaries.forEachIndexed { index, _ ->
                val x = leftMargin + (index + 1) * xSpacing
                // Draw a small vertical tick.
                drawLine(Color.Black, Offset(x, canvasHeight - bottomMargin), Offset(x, canvasHeight - bottomMargin + 5f), strokeWidth = 2f)
                // Draw the label ("Arr 1", "Arr 2", etc.).
                val textLine = TextLine.make("Arr ${index + 1}", Font().apply { size = 24f })
                canvas.nativeCanvas.drawTextLine(textLine, x - 20f, canvasHeight - 5f, textPaint)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSummaryLineChart() {
    // Generate sample data: 5 arrays, each with 50 random float numbers between 1 and 10.
    val sampleData = (1..5).map {
        List(50) { (1 + Math.random() * 9).toFloat() }
    }
    MaterialTheme {
        Surface {
            SummaryLineChart(dataArrays = sampleData)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Summary Line Chart") {
        MaterialTheme {
            // For example, create 5 arrays of random float values.
            val sampleData = (1..5).map {
                List(50) { (1 + Math.random() * 9).toFloat() }
            }
            SummaryLineChart(dataArrays = sampleData)
        }
    }
}
