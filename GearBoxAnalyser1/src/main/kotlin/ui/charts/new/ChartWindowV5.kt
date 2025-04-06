package ui.charts.new

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
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
private data class SummaryStats2(val min: Float, val max: Float, val avg: Float, val median: Float)

// Helper function to compute summary stats for a list of floats.
private fun computeSummaryStats(data: List<Float>): SummaryStats2 {
    if (data.isEmpty()) return SummaryStats2(0f, 0f, 0f, 0f)
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
    return SummaryStats2(minVal, maxVal, avgVal, medianVal)
}

/**
 * RawDataChartWithSummaries draws a line chart for a single array of float numbers.
 *
 * It plots the raw data as a line (using the array index for X values)
 * and then draws horizontal lines for the summary statistics (min, max, avg, median)
 * in separate colors.
 *
 * @param data A list of Float values (e.g. [1f,2f,23f,3f,4f,4f,5f,44f,56f]).
 * @param modifier Modifier for sizing the chart.
 * @param title Optional title to be drawn at the top.
 */
@Composable
private fun RawDataChartWithSummaries(
    data: List<Float>,
    modifier: Modifier = Modifier,
    title: String = ""
) {
    // Compute summary statistics.
    val stats = computeSummaryStats(data)
    // For Y axis scaling, use the raw data range.
    val overallMin = data.minOrNull() ?: 0f
    val overallMax = data.maxOrNull() ?: 1f
    val range = if (overallMax - overallMin < 1e-3f) 1f else (overallMax - overallMin)

    // Define colors for the summary lines.
    val minColor = Color.Red
    val maxColor = Color.Green
    val avgColor = Color.Blue
    val medianColor = Color(0xFFFFA500) // Orange

    Canvas(modifier = modifier.fillMaxWidth().height(300.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Margins for drawing.
        val leftMargin = 50f
        val rightMargin = 10f
        val bottomMargin = 30f
        val topMargin = 10f

        // Draw Y axis (vertical) and X axis (horizontal).
        drawLine(
            color = Color.Black,
            start = Offset(leftMargin, topMargin),
            end = Offset(leftMargin, canvasHeight - bottomMargin),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Black,
            start = Offset(leftMargin, canvasHeight - bottomMargin),
            end = Offset(canvasWidth - rightMargin, canvasHeight - bottomMargin),
            strokeWidth = 2f
        )

        // Draw horizontal grid lines and Y axis labels.
        val tickCountY = 5
        drawIntoCanvas { canvas ->
            val textPaint = Paint().apply { color = org.jetbrains.skia.Color.BLACK }
            for (i in 0..tickCountY) {
                val y = (canvasHeight - bottomMargin) - i * ((canvasHeight - bottomMargin - topMargin) / tickCountY)
                val value = overallMin + i * (range / tickCountY)
                drawLine(Color.LightGray, Offset(leftMargin, y), Offset(canvasWidth - rightMargin, y), strokeWidth = 1f)
                val textLine = TextLine.make(String.format("%.2f", value), Font().apply { size = 24f })
                canvas.nativeCanvas.drawTextLine(textLine, 5f, y + 10f, textPaint)
            }
        }

        // Draw the raw data line.
        val n = data.size
        val xSpacing = if(n > 1) (canvasWidth - leftMargin - rightMargin) / (n - 1) else 0f
        val rawPath = Path().apply {
            data.forEachIndexed { index, value ->
                val x = leftMargin + index * xSpacing
                val normalized = (value - overallMin) / range
                val y = (canvasHeight - bottomMargin) - normalized * (canvasHeight - bottomMargin - topMargin)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        // Draw raw data in magenta.
        drawPath(path = rawPath, color = Color.Magenta, style = Stroke(width = 3f))

        // Function to map a value to a Y coordinate.
        fun valueToY(v: Float): Float {
            val normalized = (v - overallMin) / range
            return (canvasHeight - bottomMargin) - normalized * (canvasHeight - bottomMargin - topMargin)
        }

        // Draw horizontal summary lines.
        fun drawSummaryLine(value: Float, color: Color) {
            val y = valueToY(value)
            drawLine(color, Offset(leftMargin, y), Offset(canvasWidth - rightMargin, y), strokeWidth = 2f)
        }
        drawSummaryLine(stats.min, minColor)
        drawSummaryLine(stats.max, maxColor)
        drawSummaryLine(stats.avg, avgColor)
        drawSummaryLine(stats.median, medianColor)

        // Optionally, draw the title.
        if (title.isNotEmpty()) {
            drawIntoCanvas { canvas ->
                val textPaint = Paint().apply { color = org.jetbrains.skia.Color.BLACK }
                val textLine = TextLine.make(title, Font().apply { size = 28f })
                canvas.nativeCanvas.drawTextLine(textLine, leftMargin, topMargin + 28f, textPaint)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewRawDataChartWithSummaries() {
    // Example raw data: the inserted array "1f,2f,23f,3f,4f,4f,5f,44f,56f"
    val sampleData = listOf(1f, 2f, 23f, 3f, 4f, 4f, 5f, 44f, 56f)
    MaterialTheme {
        Surface {
            RawDataChartWithSummaries(data = sampleData, title = "Raw Data & Summaries")
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Raw Data Chart with Summaries") {
        MaterialTheme {
            // For demonstration, show one chart for the sample array.
            val sampleData = listOf(1f, 2f, 23f, 3f, 4f, 4f, 5f, 44f, 56f)
            Column {
                RawDataChartWithSummaries(data = sampleData, title = "Chart for Array 1")
                // If you had multiple arrays, you could call RawDataChartWithSummaries() multiple times.
            }
        }
    }
}
