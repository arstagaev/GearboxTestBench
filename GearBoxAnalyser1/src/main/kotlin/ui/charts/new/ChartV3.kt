package ui.charts.new

import java.io.File
import kotlin.random.Random

data class DataPoint(
    val x: Double,
    val yValues: List<Double>
)

data class ChartData(
    val metadata: Map<String, String>,
    val dataPoints: List<DataPoint>
)

/**
 * Generates a chart file from the given ChartData.
 *
 * This writes the metadata lines first (each starting with '#') and then each data point.
 */
fun generateChartFile(file: File, chartData: ChartData) {
    file.bufferedWriter().use { writer ->
        // Write metadata lines.
        chartData.metadata.forEach { (key, value) ->
            writer.write("#$key;$value")
            writer.newLine()
        }
        // Write data points.
        chartData.dataPoints.forEach { point ->
            // Create a line with X and eight Y values separated by ';'
            val line = buildString {
                append(point.x)
                point.yValues.forEach { append(";"); append(it) }
            }
            writer.write(line)
            writer.newLine()
        }
    }
}



suspend fun iniUsing() {
    // Define some metadata.
    val metadata = mapOf(
        "standard" to "standard_file.txt",
        "visibility" to "true"
    )

    // Generate some dummy data: for example, X increasing from 0 and eight random Y values.
    val dataPoints = List(500) { i ->
        val x = i * 0.01  // e.g. time stamp or x-value
        val yValues = List(8) { Random.nextDouble(0.0, 10.0) }
        DataPoint(x, yValues)
    }

    val chartData = ChartData(metadata, dataPoints)
    val file = File("chartData.txt")

    // Generate the file.
    generateChartFile(file, chartData)
    println("Chart file generated: ${file.absolutePath}")

    // Later, parse the file.
//    val parsedChartData = parseChartFile(file)
//    println("Parsed metadata: ${parsedChartData.metadata}")
//    println("Parsed data point count: ${parsedChartData.dataPoints.size}")
}
