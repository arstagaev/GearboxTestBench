package utils

import androidx.compose.runtime.mutableStateOf
import com.fazecast.jSerialComm.SerialPort
import enums.ExplorerMode
import enums.StateExperiments
import enums.StateParseBytes
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.skiko.OS
import parsing_excel.models.PressuresHolder
import parsing_excel.models.ScenarioStep
import parsing_excel.models.SolenoidHolder
import storage.models.ParameterCommon
import ui.charts.Pointer
import java.io.File
import javax.swing.JFileChooser


val isDebugMode = false
var DELAY_FOR_GET_DATA = 0L
var arrayOfComPorts = arrayOf<SerialPort>()
val isWindows by lazy { !System.getProperties().getProperty("os.name").contains("Mac", ignoreCase = true) }
val OS_SLASH by lazy {
    if (isWindows) "\\" else  "/"
}

val MAINFOLDER = "mcm"

val Dir0Configs_Analysis = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}config","inner_marker.wav")
val Dir0Configs_End = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}config","auto_click.wav")
val Dir0Configs_Run = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}config","run_vine.wav")
val Dir0Configs_Error = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}config","tesla_err.wav")


val Dir1Configs = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}config")
val Dir2Reports = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}reports")
val Dir3Scenarios = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}scenarios")
val Dir4MainConfig_Json = File(Dir1Configs,"${OS_SLASH}config.json")
val Dir4MainConfig_Txt = File(Dir1Configs,"${OS_SLASH}config.txt")
val Dir4MainConfig_Log by lazy { File(Dir1Configs,"${OS_SLASH}log${generateTimestampLastUpdate()}.txt") }
val Dir5Operators = File(Dir1Configs,"${OS_SLASH}operator_ids.txt")

val Dir6 = File(Dir2Reports,"${OS_SLASH}demo.txt")
val Dir7ReportsStandard = File("${JFileChooser().fileSystemView.defaultDirectory.toString()}${OS_SLASH}${MAINFOLDER}${OS_SLASH}reports${OS_SLASH}standard")
val Dir8 = File(Dir7ReportsStandard,"${OS_SLASH}stndrd.txt")
val Dir9Scenario = File(Dir3Scenarios,"scenario_demo_test.xls")

var Dir_10_ScenarioForChart = File(Dir3Scenarios,"scenario_demo_test.xls")

var COM_PORT = "COM0"
var COM_PORT_2 = "COM0"
var BAUD_RATE = 500000
var OPERATOR_ID = "no name"
var SOUND_ENABLED = 0
var LAST_SCENARIO = Dir9Scenario
var DELAY_BEFORE_CHART = 2000
var SAVELOG = true
var isHidedCurrents = mutableStateOf(false)
var scaleGauges = mutableStateOf(1.0f)

var solenoids = mutableListOf<SolenoidHolder>()
var pressures = mutableListOf<PressuresHolder>()
var scenario  = mutableListOf<ScenarioStep>()

var GLOBAL_STATE = mutableStateOf(StateParseBytes.INIT)
var STATE_EXPERIMENT = mutableStateOf(StateExperiments.NONE)
var EXPLORER_MODE = mutableStateOf(ExplorerMode.AUTO)


var dataChunkGauges   = MutableSharedFlow<DataChunkG>(replay = 0, extraBufferCapacity = 1000, onBufferOverflow = BufferOverflow.SUSPEND)
var dataChunkCurrents = MutableSharedFlow<DataChunkCurrent>(replay = 0, extraBufferCapacity = 1000, onBufferOverflow = BufferOverflow.SUSPEND)

var dataChunkGauges2   = MutableSharedFlow<DataChunkG>(replay = 0, extraBufferCapacity = 1000, onBufferOverflow = BufferOverflow.SUSPEND)
var dataChunkCurrents2 = MutableSharedFlow<DataChunkCurrent>(replay = 0, extraBufferCapacity = 1000, onBufferOverflow = BufferOverflow.SUSPEND)


val PRESSURE_MAX_RAW = 4095
val CURRENT_MAX_RAW = 255

var arr0Time = arrayListOf<Int>()
var arr1Measure = arrayListOf<Int>()
var arr2Measure = arrayListOf<Int>()
var arr3Measure = arrayListOf<Int>()
var arr4Measure = arrayListOf<Int>()
var arr5Measure = arrayListOf<Int>()
var arr6Measure = arrayListOf<Int>()
var arr7Measure = arrayListOf<Int>()
var arr8Measure = arrayListOf<Int>()

var arr9Measure  = arrayListOf<Int>()
var arr10Measure = arrayListOf<Int>()
var arr11Measure = arrayListOf<Int>()
var arr12Measure = arrayListOf<Int>()
var arr13Measure = arrayListOf<Int>()
var arr14Measure = arrayListOf<Int>()
var arr15Measure = arrayListOf<Int>()
var arr16Measure = arrayListOf<Int>()

// Currents:
var pwm1SeekBar = mutableStateOf<Int>(-1)
var pwm2SeekBar = mutableStateOf<Int>(-1)
var pwm3SeekBar = mutableStateOf<Int>(-1)
var pwm4SeekBar = mutableStateOf<Int>(-1)
var pwm5SeekBar = mutableStateOf<Int>(-1)
var pwm6SeekBar = mutableStateOf<Int>(-1)
var pwm7SeekBar = mutableStateOf<Int>(-1)
var pwm8SeekBar = mutableStateOf<Int>(-1)

var pwm9SeekBar = mutableStateOf<Int>(-1)
var pwm10SeekBar = mutableStateOf<Int>(-1)
var pwm11SeekBar = mutableStateOf<Int>(-1)
var pwm12SeekBar = mutableStateOf<Int>(-1)
var pwm13SeekBar = mutableStateOf<Int>(-1)
var pwm14SeekBar = mutableStateOf<Int>(-1)
var pwm15SeekBar = mutableStateOf<Int>(-1)
var pwm16SeekBar = mutableStateOf<Int>(-1)

var limitTime = -1
var indexOfScenario = mutableStateOf(0)
var txtOfScenario = mutableStateOf("")
var commentOfScenario = mutableStateOf("")

// recording:
var test_time = 0
var indexScenario = 0
var num : Int = 0

var isAlreadyReceivedBytesForChart = mutableStateOf(false)
var doOpen_First_ChartWindow = mutableStateOf(false)
var doOpen_Second_ChartWindow = mutableStateOf(false)

var chartFileAfterExperiment = mutableStateOf( File(Dir2Reports,"demo2.txt") )
var chartFileStandard = mutableStateOf( File(Dir7ReportsStandard,"17_02_2023X12_04_04_chart.txt") )

var isExperimentStarts = mutableStateOf(false)
var incrementTime = 0

data class DataChunkG(
    var isExperiment: Boolean = false,
    var firstGaugeData:   Int,
    var secondGaugeData:  Int,
    var thirdGaugeData:   Int,
    var fourthGaugeData:  Int,
    var fifthGaugeData:   Int,
    var sixthGaugeData:   Int,
    var seventhGaugeData: Int,
    var eighthGaugeData:  Int
    )

data class DataChunkCurrent(
    var firstCurrentData: Int,
    var secondCurrentData: Int,
    var thirdCurrentData: Int,
    var fourthCurrentData: Int,
    var fifthCurrentData: Int,
    var sixthCurrentData: Int,
    var seventhCurrentData: Int,
    var eighthCurrentData: Int
)