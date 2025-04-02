import androidx.compose.ui.graphics.Color
import enums.StateExperiments
import enums.StateParseBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import parsing_excel.targetParseScenario
import serial_port.writeToSerialPort
import storage.PickTarget
import storage.openPicker
import storage.refreshParameters
import ui.navigation.Screens
import utils.*


fun launchPlay() {
    if (STATE_EXPERIMENT.value != StateExperiments.START) {
        CoroutineScope(Dispatchers.IO).launch {
            writeToSerialPort(
                byteArrayOf(
                    0x78,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00
                ), withFlush = false
            )
        }

//                            if (GLOBAL_STATE.value != StateParseBytes.PLAY) {
//                                ctxScope.launch {
//
//                                    initSerialCommunication()
//                                }
//                            }

        GLOBAL_STATE.value = StateParseBytes.PLAY
        sound_On()
        logGarbage("ONON $test_time V")
        test_time = 0

        indexOfScenario.value = 0
        indexScenario = 0
        num = scenario[indexScenario].time
        isAlreadyReceivedBytesForChart.value = false
        logGarbage("ONON $test_time A")

    } else {
        sound_Error()
    }
}


fun openNewScenario(isRefreshForChart: Boolean = false) {

    CoroutineScope(Dispatchers.Default).launch {
        //openPicker(Dir3Scenarios)
        //targetParseScenario(createDemoConfigFile())
        if (isRefreshForChart) {
            //open just viewer

            showMeSnackBar("Нужно выбрать сценарий для отметки степов")
            if (!targetParseScenario(openPicker(Dir3Scenarios))) {
                showMeSnackBar("Ошибка при парсинге xls", Color.Red)
            }else {

                //doOpen_First_ChartWindow.value = true
                //isAlreadyReceivedBytesForChart.value = true
                //screenNav.value = Screens.MAIN
            }
        } else {
            // open new scenario
            isAlreadyReceivedBytesForChart.value = false
            refreshParameters()
            if (!targetParseScenario(openPicker(Dir3Scenarios))) {
                showMeSnackBar("Ошибка при парсинге xls", Color.Red)
            }else {

                //doOpen_First_ChartWindow.value = true
                //isAlreadyReceivedBytesForChart.value = true
                screenNav.value = Screens.MAIN
            }
        }


    }
}

fun openLastScenario() {
    CoroutineScope(Dispatchers.Default).launch {
        if (targetParseScenario(LAST_SCENARIO)) {

            screenNav.value = Screens.MAIN
        }


    }
}

fun openChartViewer() {
    CoroutineScope(Dispatchers.Default).launch {
        openPicker(Dir2Reports, PickTarget.PICK_CHART_VIEWER,isOnlyViewer = true)?.let { chartFileAfterExperiment.value = it }




    }
}