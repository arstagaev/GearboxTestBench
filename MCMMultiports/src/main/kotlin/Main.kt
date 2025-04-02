// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import enums.ExplorerMode
import kotlinx.coroutines.*
import parsing_excel.writeToExcel
import serial_port.comparatorToSolenoid
import serial_port.pauseSerialComm
import storage.createDemoConfigExcelFile
import storage.createNeededFolders
import storage.readParameters
import ui.charts.ChartWindowNew
import utils.*
import kotlin.concurrent.fixedRateTimer


@OptIn(ExperimentalComposeUiApi::class)
fun main() = application (
//    title = "Агрегатка Tech v.1.1.4",
//    state = WindowState(size = DpSize(1000.dp, 800.dp)),
//    visible = true
) {
    Window(
        title = "MCM-16 Ports version: 1.19",
        state = WindowState(size = DpSize(1000.dp, 800.dp)),
        icon = painterResource("drawable/ava.png"),
        onKeyEvent = {
            println("Pressed: ${it.key} Type: ${it.type}")
            if(it.key == Key.Spacebar && it.type == KeyEventType.KeyUp){
                isHidedCurrents.value = !isHidedCurrents.value
                println("isHidedCurrents pressed")
                true
            } else if ( it.key == Key.DirectionRight && it.type == KeyEventType.KeyUp) {
                //shiftIsPressed = true
                 CoroutineScope(Dispatchers.IO).launch {
                     indexOfScenario.value++

                     comparatorToSolenoid(indexOfScenario.value)

                     //txtOfScenario.value = scenario.getOrElse(indexOfScenario.value) { 0 }
                     scenario.getOrNull(indexOfScenario.value)?.let { txtOfScenario.value = it.text }
                 }
                 true
            } else if (it.key == Key.DirectionLeft &&  it.type == KeyEventType.KeyUp) {
                //shiftIsPressed = false
                 indexOfScenario.value--
                 CoroutineScope(Dispatchers.IO).launch {

                     comparatorToSolenoid(indexOfScenario.value)
                 }
                 scenario.getOrNull(indexOfScenario.value)?.let { txtOfScenario.value = it.text }
                true
            }else if (it.key == Key.CtrlLeft && it.key == Key.Spacebar &&  it.type == KeyEventType.KeyDown) {
                 //shiftIsPressed = false
                 launchPlay()
                 true
            }else if (it.key == Key.N &&  it.type == KeyEventType.KeyDown && it.key == Key.CtrlLeft) {
                 //shiftIsPressed = false
                 openNewScenario()
                 true
            }else if (it.key == Key.L &&  it.type == KeyEventType.KeyDown && it.key == Key.CtrlLeft) {
                 //shiftIsPressed = false
                 openLastScenario()
                 true
            }else if (it.key == Key.V &&  it.type == KeyEventType.KeyDown && it.key == Key.CtrlLeft) {
                 //shiftIsPressed = false
                 openChartViewer()
                 true
            }

             else {
                // let other handlers receive this event
                false
            }
        },
        onCloseRequest = {
            CoroutineScope(Dispatchers.IO + CoroutineName("onCloseRequest")).launch {
                writeToExcel(row = 0, column = 3, (scaleGauges.value.toInt() * 100).toString())
                if (isWindows) {
                    pauseSerialComm()
                    delay(500)
                } else {
                    showMeSnackBar("NO Connect to ${COM_PORT} !!", Color.Red)
                    delay(2000)
                }

                exitApplication()
            }

        },
    ) {
        val doOpenNewWindowInternal = remember { doOpen_First_ChartWindow }
        val doOpenNewWindowInternal2 = remember { doOpen_Second_ChartWindow }
        //COM_PORT = "COM10"//getComPorts_Array().get(0).systemPortName
        //readExcelFile()

        //var initParameters = readParameters(Dir4MainConfig)

        createNeededFolders()
        createDemoConfigExcelFile()
        initialize(readParameters(Dir4MainConfig_Txt))

        var isHaveConn = false
        getComPorts_Array()?.forEach {
            if (it.systemPortName == COM_PORT) {
                isHaveConn = true
            }
        }
        if (!isHaveConn) {
            showMeSnackBar("NO Connect to ${COM_PORT} !!", Color.Red)
        }

        App()

        if (EXPLORER_MODE.value == ExplorerMode.AUTO) {
            if (doOpenNewWindowInternal.value && isAlreadyReceivedBytesForChart.value) {
                ChartWindowNew(withStandard = true).chartWindow()
                //chartWindow()
            }
        }

        if (doOpenNewWindowInternal2.value) {
            ChartWindowNew(withStandard = true,isViewerOnly = true).chartWindow()
            //chartWindow()
        }
    }




//    val properties: Properties = Properties()
//    properties.load(App::class.java.getResourceAsStream("/version.properties"))
//    System.out.println(properties.getProperty("version"))
//    initSerialCommunication("COM3")
}

fun startTimer() {
    fixedRateTimer("timer_2", daemon = true, 0L,1000L) {

        //timeOfMeasure.value += 1
    }
}