import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.AreaRendererEndType
import org.jfree.chart.renderer.category.AreaRenderer
import org.jfree.chart.title.TextTitle
import org.jfree.data.category.CategoryDataset
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.general.DatasetUtils
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import parsing_excel.targetParseScenario
import storage.openPicker
import ui.navigation.Screens
import ui.main_screen.center.CenterPiece
import ui.starter_screen.StarterScreen
import utils.Dir3Scenarios
import utils.Represent
import java.awt.Font
import java.io.File


//import VariablesUSB.*

//val serialPort: SerialPort = SerialPort.getCommPort("COM3")
var visiMainScr = mutableStateOf(true)
var showmeSnackBar = mutableStateOf(false)
var textForSnackBar = mutableStateOf("Alert")
var textForSnackBarColor = mutableStateOf(Color.Red)
var screenNav = mutableStateOf<Screens>(Screens.STARTER)

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun App() {
    val screenNavi = remember { screenNav }

    // for test:
//    LaunchedEffect(true) {
//        CoroutineScope(Dispatchers.IO).launch {
//            if (targetParseScenario(File(Dir3Scenarios,"scenario_demo.xls"))) {
//                screenNav.value = Screens.MAIN
//            }
//
//
//        }
//    }


    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Row{
                //leftPiece(visibilityOfMainScreen)

                when(screenNavi.value) {
                    Screens.STARTER -> {

                        //Box(Modifier.fillMaxSize().background(Color.Red))
                        StarterScreen()
                    }
                    Screens.MAIN -> {
                        CenterPiece()
                    }
                    Screens.EASTER_EGG -> {
                        Represent()
                    }
                    else -> {
                        CenterPiece()
                    }
                }
            }
            snackBarShow()
        }
    }
    showMeSnackBar("Приветствую!",Color.White)
}

fun showMeSnackBar(msg : String,color: Color = Color.White) {
    textForSnackBar.value = msg
    showmeSnackBar.value = true
    textForSnackBarColor.value = color
}

@Composable
fun snackBarShow() {
    var visibilityOfToast by remember { showmeSnackBar }

    AnimatedVisibility(
        visible = visibilityOfToast,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.Black.copy(alpha = 0.6f))
        ) {
            Text("${textForSnackBar.value}", fontSize = 40.sp,modifier = Modifier.fillMaxSize().padding(30.dp), color =textForSnackBarColor.value)
        }
        GlobalScope.launch {
            delay(3000)
            visibilityOfToast = false
        }
    }
}

@Composable
fun chartView() {
    Column(
      modifier = Modifier.width(200.dp).fillMaxHeight().background(Color.Yellow)
    ) {
        Row(
            modifier = Modifier
                //.width(IntrinsicSize.Max)
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 5.dp)
            //.background(Color.Red)
            , horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearChart(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Black),LinearChartStyle.Smooth,
                listOf(1f,2f,3f,4f,5f,6f), Color(255, 0, 0)
            )
        }
    }
}


///////
fun createDataset(): CategoryDataset? {
    var data = arrayOf(
        doubleArrayOf(
            1.0,
            2.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0,
            1.0
        )
    )

    var pzc = arrayListOf<Double>()
    var time = arrayListOf<Double>()
    var step = 0.0
    for (i in 0..100) {

        pzc.add(1.0/(0..10).random().toDouble())
        time.add(step)
        step += 0.5
    }
    var asd = pzc.toDoubleArray()
    var dfs = arrayOf(time)
    val dataset = DefaultCategoryDataset()
    dataset.addValue(10.0,"","")
    return DatasetUtils.createCategoryDataset(
        arrayOf("Oil"), arrayOf(), arrayOf(
        asd
        ))
}

private fun createDataset2(): XYSeriesCollection {
    val iexplorer = XYSeries("InternetExplorer")
    iexplorer.add(3.0, 4.0)
    iexplorer.add(4.0, 5.0)
    iexplorer.add(5.0, 4.0)


    val dataset = XYSeriesCollection()
//    dataset.addSeries(firefox)
//    dataset.addSeries(chrome)
    dataset.addSeries(iexplorer)
    return dataset
}

private fun createChart(dataset: CategoryDataset): JFreeChart? {
    val chart: JFreeChart = ChartFactory.createAreaChart(
        "Oil consumption",
        "Time",
        "Thousands bbl/day",
        dataset,
        PlotOrientation.VERTICAL,
        false,
        true,
        true
    )
    val plot: CategoryPlot = chart.getPlot() as CategoryPlot
    plot.setForegroundAlpha(0.3f)
    val renderer: AreaRenderer = plot.getRenderer() as AreaRenderer
    renderer.setEndType(AreaRendererEndType.LEVEL)
    chart.setTitle(
        TextTitle(
            "Oil consumption",
            Font("Serif", Font.BOLD, 18)
        )
    )

    return chart
}
