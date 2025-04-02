package ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import enums.StateExperiments
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.IntervalMarker
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYItemRenderer
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.ui.Layer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import parsing_excel.targetParseScenario
import parsing_excel.writeToExcel
import showMeSnackBar
import storage.PickTarget
import storage.openPicker
import storage.openPickerLambda
import ui.charts.models.SeriesChart
import utils.*
import java.awt.BasicStroke
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.swing.BoxLayout
import javax.swing.JPanel

class ChartWindowV2(val isViewerOnly: Boolean = false) {
    var withStandard = true
    val dataset = XYSeriesCollection()

    private var seriesListExperiment = mutableListOf<SeriesChart>()
    private var seriesListStandard = mutableListOf<SeriesChart>()

    var chartPanel: JPanel? = null
    var isLoading = mutableStateOf(false)
    val crtx = CoroutineScope(Dispatchers.Default)

    private var renderer: XYItemRenderer = XYLineAndShapeRenderer(true, false)
    private val xAxis = NumberAxis("time (ms)")
    private val yAxis = NumberAxis("Bar")
    private var plot = XYPlot(dataset, xAxis, yAxis, renderer)

    var chart = JFreeChart(
        "Эталон и текущий эксперимент", JFreeChart.DEFAULT_TITLE_FONT,
        plot,true
    )

    private var sizeStandard   = 0
    private var sizeExperiment = 0

    var stateOfVisibleSeries = arrayListOf<Boolean>()

    val arrClrExperiment = arrayOf(
        java.awt.Color.RED,
        java.awt.Color.ORANGE,
        java.awt.Color.YELLOW,
        java.awt.Color.GREEN,
        java.awt.Color.BLUE,
        java.awt.Color.CYAN,
        java.awt.Color.MAGENTA,
        java.awt.Color.BLACK
    )

    val arrClrStandard = arrayOf(
        java.awt.Color(255, 145, 145),
        java.awt.Color(255, 200, 50),
        java.awt.Color(255, 255, 150),
        java.awt.Color(147, 255, 100),
        java.awt.Color(147, 147, 255),
        java.awt.Color(147, 255, 255),
        java.awt.Color(255, 147, 255),
        java.awt.Color(128, 128, 128)
    )

    var halfNumberOfCharts = 0


    init {

        crtx.launch {

            delay(1000)
            fillUp()
            delay(1000)
            STATE_EXPERIMENT.value = StateExperiments.NONE
        }.invokeOnCompletion {
            crtx.launch {
                addMarkersScenarios()

            }
        }


        chartPanel = ChartPanel(chart)
    }
    @Composable
    fun chartWindow() {

        Window(
            title = if (isViewerOnly) "Viewer" else "Compare with Standard",
            state = WindowState(size = DpSize(1000.dp, 800.dp)),
            onCloseRequest = {

                CoroutineScope(Dispatchers.Default).launch {
                    if (isViewerOnly) {
                        doOpen_Second_ChartWindow.value = false
                    } else {
                        doOpen_First_ChartWindow.value = false
                    }
                    delay(100)

                    writeToExcel(0,0, chartFileStandard.value.name)

                }
            },
        ) {
            val chrt = remember { STATE_EXPERIMENT }

            if (chrt.value == StateExperiments.NONE) {
                ChartView()
            } else {
                Box(Modifier.fillMaxSize().background(Color.Black)) {
                    Text("Loading...",
                        modifier = Modifier.padding(top = (10).dp,start = 20.dp),
                        fontFamily = FontFamily.Default, fontSize = 40.sp,
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    fun ChartView() {
        val standardFile = remember { chartFileStandard }

        Box {
            Column(
                modifier = Modifier.fillMaxSize()//fillMaxWidth().height(800.dp)
            ) {
                Row(Modifier.fillMaxWidth().height(20.dp).background(Color.White), horizontalArrangement = Arrangement.Start) {
                    Box(Modifier.height(20.dp).weight(0.5f).clickable {
                        openPicker(Dir7ReportsStandard, PickTarget.PICK_STANDARD_CHART).let {
                            if(it != null) {
                                chartFileStandard.value = it
                                CoroutineScope(Dispatchers.Default).launch {
                                    writeToExcel(0,0, chartFileStandard.value.name)
                                }

                            }
                        }

                        CoroutineScope(Dispatchers.Default).launch {
                            fillStandard()
                        }

                    }){
                        Text("Эталон(${sizeStandard}): ${standardFile.value.name}", modifier = Modifier.padding(0.dp).align(
                            Alignment.Center),
                            fontFamily = FontFamily.Default, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black
                        )
                    }
                    repeat((dataset.seriesCount/2)) {
                        val rmClr = remember { mutableStateOf(stateOfVisibleSeries[it+dataset.seriesCount/2]) }
                        Box(
                            Modifier.width(30.dp).fillMaxHeight().padding(horizontal = 3.dp).background(if (rmClr.value) Color.Blue else Color.LightGray).clickable {
                                stateOfVisibleSeries[it+dataset.seriesCount/2] = !stateOfVisibleSeries[it+dataset.seriesCount/2]
                                rmClr.value = stateOfVisibleSeries[it+dataset.seriesCount/2]
                                renderer.setSeriesVisible(it+dataset.seriesCount/2, stateOfVisibleSeries[it+dataset.seriesCount/2], true)

                                //renderer.setSeriesVisible(0,false,true)
                            }
                        ) {
                            Text("${it+1+dataset.seriesCount/2}")
                        }
                    }

                }
                Row(Modifier.fillMaxWidth().height(20.dp).background(Color.White), horizontalArrangement = Arrangement.Start) {
                    Box(Modifier.height(20.dp).weight(0.5f).clickable {
                        openPicker(Dir2Reports, PickTarget.PICK_CHART_VIEWER).let { if(it != null) chartFileAfterExperiment.value = it }

                        CoroutineScope(Dispatchers.Default).launch {
                            fillExperiment()
                        }

                    }){
                        Text("Эксперимент(${sizeExperiment}): ${chartFileAfterExperiment.value.name}", modifier = Modifier.padding(0.dp).align(
                            Alignment.Center),
                            fontFamily = FontFamily.Default, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Black
                        )
                    }
                    repeat((dataset.seriesCount/2)) {
                        val rmClr = remember { mutableStateOf(stateOfVisibleSeries[it]) }
                        Box(
                            Modifier.width(30.dp).fillMaxHeight().padding(horizontal = 3.dp).background(if (rmClr.value) Color.Blue else Color.LightGray).clickable {
                                stateOfVisibleSeries[it] = !stateOfVisibleSeries[it]
                                rmClr.value = stateOfVisibleSeries[it]
                                renderer.setSeriesVisible(it, stateOfVisibleSeries[it], true)

                                //renderer.setSeriesVisible(0,false,true)
                            }
                        ) {
                            Text("${it+1}")
                        }
                    }
                }
                SwingPanel(
                    background = Color.DarkGray,
                    modifier = Modifier.fillMaxSize(),//.size(width = 1000.dp, height =  600.dp),
                    factory = {
                        JPanel().apply {
                            setLayout(BoxLayout(this, BoxLayout.Y_AXIS))
                            add(chartPanel)

                        }
                    }
                )

            }
        }
    }


    private suspend fun fillUp() {
        STATE_EXPERIMENT.value = StateExperiments.PREPARE_CHART
        logAct("fillUp chart ${chartFileAfterExperiment.value.name}  ${chartFileStandard.value.name}")

        logGarbage(">>>1")
        seriesListExperiment.clear()
        seriesListStandard.clear()

        seriesListExperiment.add(SeriesChart(XYSeries("Давление 1"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 2"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 3"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 4"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 5"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 6"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 7"),))
        seriesListExperiment.add(SeriesChart(XYSeries("Давление 8"),))

        seriesListStandard.add(SeriesChart(XYSeries("Давление 1 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 2 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 3 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 4 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 5 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 6 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 7 [Эталон]"),))
        seriesListStandard.add(SeriesChart(XYSeries("Давление 8 [Эталон]"),))

        /**
         * FILL from EXPERIMENT:
         */
        fillExperiment()


        /**
         * FILL from STANDARD:
         */
        fillStandard()
        logGarbage(">>>2")


        // // //

        logGarbage(">>>3")
        sizeExperiment =  seriesListExperiment[0].xySeries.items.size
        dataset.addSeries(seriesListExperiment[0].xySeries)
        dataset.addSeries(seriesListExperiment[1].xySeries)
        dataset.addSeries(seriesListExperiment[2].xySeries)
        dataset.addSeries(seriesListExperiment[3].xySeries)
        dataset.addSeries(seriesListExperiment[4].xySeries)
        dataset.addSeries(seriesListExperiment[5].xySeries)
        dataset.addSeries(seriesListExperiment[6].xySeries)
        dataset.addSeries(seriesListExperiment[7].xySeries)

        if (withStandard) {
            sizeStandard = seriesListStandard[0].xySeries.items.size
            //logInfo("chart series16 ${series16.maximumItemCount} ${series16}")
            dataset.addSeries(seriesListStandard[0].xySeries)
            dataset.addSeries(seriesListStandard[1].xySeries)
            dataset.addSeries(seriesListStandard[2].xySeries)
            dataset.addSeries(seriesListStandard[3].xySeries)
            dataset.addSeries(seriesListStandard[4].xySeries)
            dataset.addSeries(seriesListStandard[5].xySeries)
            dataset.addSeries(seriesListStandard[6].xySeries)
            dataset.addSeries(seriesListStandard[7].xySeries)
        }


        xAxis.autoRangeIncludesZero = false

        logGarbage(">>>4")

        repeat(dataset.seriesCount) {
            stateOfVisibleSeries.add(true)
        }

        logGarbage(">>>5")
        repeat(8) {
            //renderer
            renderer.setSeriesPaint(it, arrClrExperiment[it])
            renderer.setSeriesStroke(it, BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND))
            //renderer.setSeriesShape(it+8,ShapeUtilities.createDiamond(0f))
        }

        if (withStandard) {
            repeat(8) {

                //renderer.setSeriesShape(it+8,ShapeUtilities.createDiamond(6f))
                renderer.setSeriesPaint(it + 8, arrClrStandard[it])
                renderer.setSeriesStroke(
                    it + 8,
                    //ShapeUtilities.createDiamond(6f)
                    BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1f, floatArrayOf(10f), 0.5f)
                )
                //renderer.sha
            }
        }


        logGarbage(">>>6")
        plot = XYPlot(dataset, xAxis, yAxis, renderer)
        //plot
        plot.setOrientation(PlotOrientation.VERTICAL)
        logGarbage(">>>7")
        plot.setRenderer(renderer)




    }

    private suspend fun fillExperiment() {
        isLoading.value = true
        logAct("fillExperiment !!!")
        try {
            repeat(seriesListExperiment.size) {
                seriesListExperiment[it].xySeries.clear()
            }


            val br = BufferedReader(FileReader(chartFileAfterExperiment.value))
            var line: String?
            //var countOfLine = 0
            while (br.readLine().also { line = it } != null) {
                if(line != ""|| line != " ") {
                    if (line?.first() == '#') {
                        val items = line?.split("#")?.toTypedArray()
                        if (items != null ) {
                            println("# ${items.joinToString()}")
                            when {
                                items[0] == "standard" -> chartFileStandard.value = File(Dir7ReportsStandard, items[1])
                                items[0] == "visibility" -> {

                                }
                            }


                        }

                    } else {
                        val items = line?.split(";","|")?.toTypedArray()
                        if (items != null ) {
                            seriesListExperiment[0].xySeries.add(items[0].toDouble(),items[1].toDouble())
                            seriesListExperiment[1].xySeries.add(items[0].toDouble(),items[2].toDouble())
                            seriesListExperiment[2].xySeries.add(items[0].toDouble(),items[3].toDouble())
                            seriesListExperiment[3].xySeries.add(items[0].toDouble(),items[4].toDouble())

                            seriesListExperiment[4].xySeries.add(items[0].toDouble(),items[5].toDouble())
                            seriesListExperiment[5].xySeries.add(items[0].toDouble(),items[6].toDouble())
                            seriesListExperiment[6].xySeries.add(items[0].toDouble(),items[7].toDouble())
                            seriesListExperiment[7].xySeries.add(items[0].toDouble(),items[8].toDouble())

                        }
                    }

                }
                //countOfLine++
            }
            br.close()
        } catch (e: Exception) {
            logError("error +${e.message}")
            showMeSnackBar("Error Chart:  ${e.message}",Color.Red)
        }

        seriesListExperiment[0].xySeries.notify = true
        seriesListExperiment[1].xySeries.notify = true
        seriesListExperiment[2].xySeries.notify = true
        seriesListExperiment[3].xySeries.notify = true
        seriesListExperiment[4].xySeries.notify = true
        seriesListExperiment[5].xySeries.notify = true
        seriesListExperiment[6].xySeries.notify = true
        seriesListExperiment[7].xySeries.notify = true

        halfNumberOfCharts = dataset.seriesCount / 2
        isLoading.value = false
    }


    private suspend fun fillStandard() {
        isLoading.value = true
        logAct("fillStandard !!!")
        repeat(seriesListStandard.size) {
            seriesListStandard[it].xySeries.clear()
        }
        try {
            seriesListStandard[0].xySeries.clear()
            seriesListStandard[1].xySeries.clear()
            seriesListStandard[2].xySeries.clear()
            seriesListStandard[3].xySeries.clear()
            seriesListStandard[4].xySeries.clear()
            seriesListStandard[5].xySeries.clear()
            seriesListStandard[6].xySeries.clear()
            seriesListStandard[7].xySeries.clear()


            val br = BufferedReader(FileReader(chartFileStandard.value))
            var line: String?
            //var countOfLine = 0
            while (br.readLine().also { line = it } != null) {
                if(line != ""|| line != " ") {
                    if (line?.first() == '&') {
                        val items = line?.split("&")?.toTypedArray()
                        Dir_10_ScenarioForChart = File(Dir3Scenarios,items?.get(0) ?: "")
                    } else {
                        val items = line?.split(";","|")?.toTypedArray()
                        if (items != null ) {
                            seriesListStandard[0].xySeries.add(items[0].toDouble(),items[1].toDouble())
                            seriesListStandard[1].xySeries.add(items[0].toDouble(),items[2].toDouble())
                            seriesListStandard[2].xySeries.add(items[0].toDouble(),items[3].toDouble())
                            seriesListStandard[3].xySeries.add(items[0].toDouble(),items[4].toDouble())

                            seriesListStandard[4].xySeries.add(items[0].toDouble(),items[5].toDouble())
                            seriesListStandard[5].xySeries.add(items[0].toDouble(),items[6].toDouble())
                            seriesListStandard[6].xySeries.add(items[0].toDouble(),items[7].toDouble())
                            seriesListStandard[7].xySeries.add(items[0].toDouble(),items[8].toDouble())

                        }
                    }

                }
                //countOfLine++
            }
            br.close()
        } catch (e: Exception) {
            logError("error +${e.message}")
            showMeSnackBar("Error Chart:  ${e.message}",Color.Red)
        }

        //dataset.notify = true
        seriesListStandard[0].xySeries.notify = true
        seriesListStandard[1].xySeries.notify = true
        seriesListStandard[2].xySeries.notify = true
        seriesListStandard[3].xySeries.notify = true
        seriesListStandard[4].xySeries.notify = true
        seriesListStandard[5].xySeries.notify = true
        seriesListStandard[6].xySeries.notify = true
        seriesListStandard[7].xySeries.notify = true

        isLoading.value = false
    }

    suspend fun addMarkersScenarios() {
        if (Dir_10_ScenarioForChart.exists()) {
            targetParseScenario(Dir_10_ScenarioForChart)
        }


        if (scenario.isEmpty()) {
            openPickerLambda(Dir3Scenarios) {
                crtx.launch {
                    targetParseScenario(it)

                    var lastNum = 0.0
                    logGarbage("SCENNNAAX ${scenario.size} ")
                    repeat(scenario.size) {
                        logGarbage("SCENNNAA")
                        val target = IntervalMarker(lastNum, lastNum+scenario[it].time.toDouble(),
                            if (it % 2 == 0)java.awt.Color.LIGHT_GRAY else java.awt.Color.GRAY)
                        lastNum += scenario[it].time.toDouble()
                        target.label = "[${scenario[it].comment}]"
                        //target.paint = java.awt.Color.GREEN
                        //target.labelAnchor = RectangleAnchor.LEFT
                        //target.labelTextAnchor = TextAnchor.CENTER_LEFT
                        //target.setPaint(java.awt.Color.GREEN)
                        //target.setPaint(new Color(10, 222, 2, 128));
                        //target.setPaint(new Color(10, 222, 2, 128));
                        //plot.addRangeMarker(1,target,Layer.FOREGROUND,true)
                        val plot: XYPlot = chart.getXYPlot()
                        plot.addDomainMarker(target, Layer.BACKGROUND)
                        chart.isNotify = true
                    }
                    //showMeChart.value = true


                    chart = JFreeChart(
                        "Эталон и текущий эксперимент", JFreeChart.DEFAULT_TITLE_FONT,
                        plot,true
                    )
                    logGarbage(">>>8 $}")
                    soundUniversal(Dir0Configs_End)
                    halfNumberOfCharts = dataset.seriesCount
                }

            }
            //openNewScenario(isRefreshForChart = true)
        } else {

        }


    }

}