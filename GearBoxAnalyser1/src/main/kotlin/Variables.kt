import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object Variables {


    val MEASURMENT_ARRAY_SIZE = 1000
    val CHART_ARRAY_SIZE = 500
    val MINIMAL_VALUE_SENSOR = 0f//-20_000f


    var countPacketsPerSecond = mutableStateOf(0)
    var allPackets = 0

    // current value:
    var measureValueX = mutableStateOf(0.0)
    var measureValueY = mutableStateOf(0.0)
    var measureValueZ = mutableStateOf(0.0)

    var lastXAccelerations = mutableListOf<Double>()
    var lastYAccelerations = mutableListOf<Double>()
    var lastZAccelerations = mutableStateListOf<Double>()

    // max:
    var measureMaxX = mutableStateOf(0.0)
    var measureMaxY = mutableStateOf(0.0)
    var measureMaxZ = mutableStateOf(0.0)

    // min:
    var measureMinX = mutableStateOf(0.0)
    var measureMinY = mutableStateOf(0.0)
    var measureMinZ = mutableStateOf(0.0)

    // average:
    var measureAverageX = mutableStateOf(0.0)
    var measureAverageY = mutableStateOf(0.0)
    var measureAverageZ = mutableStateOf(0.0)

    var accChangesArray = mutableStateListOf<Double>(0.0,1.0,2.0,3.0,4.0,5.0)
    var logCat = mutableStateListOf<String>("")
    var chatCanva = mutableStateListOf<String>()


    // Analysis
    var analysisParameters = mutableStateListOf<AnalysisBlock>()
    data class AnalysisBlock(val name: String, var value: Double)
}