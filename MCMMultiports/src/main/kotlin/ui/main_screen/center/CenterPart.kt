package ui.main_screen.center

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import enums.ExplorerMode
import enums.StateExperiments
import enums.StateParseBytes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import launchPlay
import screenNav
import serial_port.*
import storage.createMeasureExperiment
import ui.charts.Pointer
import ui.custom.GaugeX
import ui.main_screen.center.support_elements.solenoidsPanel
import ui.main_screen.center.support_elements.solenoidsPanel2
import ui.navigation.Screens
import utils.*


@Composable
fun CenterPiece(
) {
    var sizeRow    by remember {mutableStateOf(Size.Zero)}
    var pressure1X by remember { mutableStateOf(0) }
    var pressure2X by remember { mutableStateOf(0) }
    var pressure3X by remember { mutableStateOf(0) }
    var pressure4X by remember { mutableStateOf(0) }
    var pressure5X by remember { mutableStateOf(0) }
    var pressure6X by remember { mutableStateOf(0) }
    var pressure7X by remember { mutableStateOf(0) }
    var pressure8X by remember { mutableStateOf(0) }

    var pressure9X  by remember { mutableStateOf(0) }
    var pressure10X by remember { mutableStateOf(0) }
    var pressure11X by remember { mutableStateOf(0) }
    var pressure12X by remember { mutableStateOf(0) }
    var pressure13X by remember { mutableStateOf(0) }
    var pressure14X by remember { mutableStateOf(0) }
    var pressure15X by remember { mutableStateOf(0) }
    var pressure16X by remember { mutableStateOf(0) }
    val duration = MutableStateFlow(100L)

    val stateChart = remember { STATE_EXPERIMENT }
    val explMode = remember { EXPLORER_MODE }
    val expandedCom = remember { mutableStateOf(false) }
    //val isHideCurrents = remember { isHidedCurrents }
    val gscl = remember { scaleGauges }
    val txt = remember { txtOfScenario }

    val ctxScope =
        CoroutineScope(Dispatchers.IO) + rememberCoroutineScope().coroutineContext + CoroutineName("MainScreen-CenterPart")

    // Get local density from composable
    val localDensity = LocalDensity.current

    // Create element height in dp state
    var columnHeightDp by remember {
        mutableStateOf(0.dp)
    }
    var isShowPlay = remember { mutableStateOf(false) }

    LaunchedEffect(isHidedCurrents.value) {
        println("isHidedCurrents triggered")
    }
    LaunchedEffect(true) {
        ctxScope.launch {
            //EXPLORER_MODE.value = ExplorerMode.MANUAL
            //reInitSolenoids()
            indexOfScenario.value = 0

            if ( isWindows == false ) {
                println("xxxxxxxxxxxx IS MAC OS => without USB CONNECTION !!!  xxxxxxxxxxxxxxxxxx")

                return@launch
            }
            //sound_On()
            startReceiveFullData()
            comparatorToSolenoid(indexOfScenario.value)
            sendScenarioToController()
            var count = 0
            dataChunkGauges.collect {
                isShowPlay.value = true
                //delay(DELAY_FOR_GET_DATA)
                //logGarbage("Exp>  ${STATE_CHART.value.name}||${arr1Measure.size} ${dataChunkGauges.replayCache.size} ${solenoids.size} ${pressures.size} ${scenario.size}")

                //println("|<<<<<<<<<<<<<<<<<<<${it.isExperiment} [${it.firstGaugeData}]")
                //longForChart.add(if (pressure1X > 1000) { 1000 } else pressure1X)
                //longForChart.add(pressure1X)

                pressure1X = map(it.firstGaugeData, 0, 4095, (pressures[0].minValue), (pressures[0].maxValue),)
                pressure2X = map(it.secondGaugeData, 0, 4095, (pressures[1].minValue), (pressures[1].maxValue),)
                pressure3X = map(it.thirdGaugeData, 0, 4095, (pressures[2].minValue), (pressures[2].maxValue),)
                pressure4X = map(it.fourthGaugeData, 0, 4095, (pressures[3].minValue), (pressures[3].maxValue),)
                pressure5X = map(it.fifthGaugeData, 0, 4095, (pressures[4].minValue), (pressures[4].maxValue),)
                pressure6X = map(it.sixthGaugeData, 0, 4095, (pressures[5].minValue), (pressures[5].maxValue),)
                pressure7X = map(it.seventhGaugeData, 0, 4095, (pressures[6].minValue), (pressures[6].maxValue),)
                pressure8X = map(it.eighthGaugeData, 0, 4095, (pressures[7].minValue), (pressures[7].maxValue),)

                when (EXPLORER_MODE.value) {
                    ExplorerMode.AUTO -> {
                        //logGarbage("konec ${}")
                        if (
                            //limitTime >= incrementTime &&
                            it.isExperiment
                        ) {
                            count++
                            arr0Time   .add(incrementTime)
                            arr1Measure.add(pressure1X) //it.firstGaugeData, ))
                            arr2Measure.add(pressure2X) //it.secondGaugeData,))
                            arr3Measure.add(pressure3X) //it.thirdGaugeData, ))
                            arr4Measure.add(pressure4X) //it.fourthGaugeData,))
                            arr5Measure.add(pressure5X) //it.fifthGaugeData, ))
                            arr6Measure.add(pressure6X) //it.sixthGaugeData, ))
                            arr7Measure.add(pressure7X) //it.seventhGaugeData))
                            arr8Measure.add(pressure8X) //it.eighthGaugeData, ))

//                            num = scenario[indexScenario].time
//
//                            if (num > 0) {
//                                // 2 - is recieve data every 2ms
//                                num -= 2
//                            } else {
//                                indexScenario++
//                                num = scenario[indexScenario].time
//                                txt.value = scenario[indexScenario].text
//                            }
                            incrementTime += 2
                            //test_time += 2

                        } else if (STATE_EXPERIMENT.value == StateExperiments.PREP_DATA) {
                            /**
                             * Already filled experiment
                             */
                            logGarbage("Output: |${incrX}|=>|${count}|  | ${arr1Measure.size} ${arr1Measure[arr1Measure.lastIndex]}")

                            STATE_EXPERIMENT.value = StateExperiments.PREPARE_CHART
                            incrementTime = 0
                            if (!isAlreadyReceivedBytesForChart.value) {
                                isAlreadyReceivedBytesForChart.value = true
                                createMeasureExperiment()
                            }


                        }
                    }

                    ExplorerMode.MANUAL -> {
                        // without recording
                    }
                }
            }
            /////
        }

        ctxScope.launch {
            dataChunkGauges2.collect {
                isShowPlay.value = true
                //delay(DELAY_FOR_GET_DATA)
                //logGarbage("Exp>  ${STATE_CHART.value.name}||${arr1Measure.size} ${dataChunkGauges.replayCache.size} ${solenoids.size} ${pressures.size} ${scenario.size}")

                //println("|<<<<<<<<<<<<<<<<<<<${it.isExperiment} [${it.firstGaugeData}]")
                //longForChart.add(if (pressure1X > 1000) { 1000 } else pressure1X)
                //longForChart.add(pressure1X)
                if(pressures.size == 17) {
                    pressure9X  = map(it.firstGaugeData, 0, 4095, ( pressures[9 ].minValue), (pressures[9 ].maxValue),)
                    pressure10X = map(it.secondGaugeData, 0, 4095, (pressures[10].minValue), (pressures[10].maxValue),)
                    pressure11X = map(it.thirdGaugeData, 0, 4095, ( pressures[11].minValue), (pressures[11].maxValue),)
                    pressure12X = map(it.fourthGaugeData, 0, 4095, (pressures[12].minValue), (pressures[12].maxValue),)
                    pressure13X = map(it.fifthGaugeData, 0, 4095, ( pressures[13].minValue), (pressures[13].maxValue),)
                    pressure14X = map(it.sixthGaugeData, 0, 4095, ( pressures[14].minValue), (pressures[14].maxValue),)
                    pressure15X = map(it.seventhGaugeData, 0, 4095,(pressures[15].minValue), (pressures[15].maxValue),)
                    pressure16X = map(it.eighthGaugeData, 0, 4095, (pressures[16].minValue), (pressures[16].maxValue),)

                }

                when (EXPLORER_MODE.value) {
                    ExplorerMode.AUTO -> {
                        //logGarbage("konec ${}")
                        if (
                        //limitTime >= incrementTime &&
                            (it.isExperiment)
                        ) {
                            arr0Time   .add(incrementTime)
                            arr9Measure .add( pressure9X  ) //it.firstGaugeData, ))
                            arr10Measure.add( pressure10X ) //it.secondGaugeData,))
                            arr11Measure.add( pressure11X ) //it.thirdGaugeData, ))
                            arr12Measure.add( pressure12X ) //it.fourthGaugeData,))
                            arr13Measure.add( pressure13X ) //it.fifthGaugeData, ))
                            arr14Measure.add( pressure14X ) //it.sixthGaugeData, ))
                            arr15Measure.add( pressure15X ) //it.seventhGaugeData))
                            arr16Measure.add( pressure16X ) //it.eighthGaugeData, ))

//                            num = scenario[indexScenario].time
//
//                            if (num > 0) {
//                                // 2 - is recieve data every 2ms
//                                num -= 2
//                            } else {
//                                indexScenario++
//                                num = scenario[indexScenario].time
//                                txt.value = scenario[indexScenario].text
//                            }
                            incrementTime += 2
                            //test_time += 2

                        } else if (STATE_EXPERIMENT.value == StateExperiments.PREP_DATA) {
                            /**
                             * Already filled experiment
                             */
                            //logGarbage("Output: |${incrX}|=>|${count}|  | ${arr1Measure.size} ${arr1Measure[arr1Measure.lastIndex]}")

                            STATE_EXPERIMENT.value = StateExperiments.PREPARE_CHART
                            incrementTime = 0
                            if (!isAlreadyReceivedBytesForChart.value) {
                                isAlreadyReceivedBytesForChart.value = true
                                createMeasureExperiment()
                            }


                        }
                    }

                    ExplorerMode.MANUAL -> {
                        // without recording
                    }
                }
            }
        }
    }
    /**
     * Composer:
     */
    Column(
        modifier = Modifier //.padding(10.dp)
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                sizeRow = coordinates.size.toSize()
            }
    ) {
        Row(Modifier.fillMaxWidth().weight(0.5f), horizontalArrangement = Arrangement.SpaceBetween) {
//                Box(Modifier.size(40.dp)) {
//                    Image(painterResource("/trs.jpg"),"")
//                }
            Row(Modifier) {
                if (isExperimentStarts.value) {
                    Text(
                        "Rec...",
                        modifier = Modifier.padding(top = (10).dp, start = 20.dp).clickable {
                        },
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
                Text(
                    "${txt.value}",
                    modifier = Modifier.width(90.dp).padding(top = (10).dp, start = 20.dp).clickable {
                        //screenNav.value = Screens.STARTER
                    },
                    fontFamily = FontFamily.Default,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )

                // TOP BAR:
                Box(Modifier.clickable {
                    expandedCom.value = !expandedCom.value
                }) {
                    Text(
                        "Mode: ${explMode.value.name}",
                        modifier = Modifier.padding(top = (10).dp, start = 20.dp),
                        fontFamily = FontFamily.Default, fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, color = Color.White
                    )

                    DropdownMenu(
                        modifier = Modifier.background(Color.White),
                        expanded = expandedCom.value,
                        onDismissRequest = { expandedCom.value = false },
                    ) {
                        Text(
                            "AUTO", fontSize = 18.sp, modifier = Modifier.fillMaxSize().padding(10.dp)
                                .clickable(onClick = {
                                    EXPLORER_MODE.value = ExplorerMode.AUTO
                                }), color = Color.Black
                        )
                        Text(
                            "MANUAL", fontSize = 18.sp, modifier = Modifier.fillMaxSize().padding(10.dp)
                                .clickable(onClick = {
                                    EXPLORER_MODE.value = ExplorerMode.MANUAL
                                }), color = Color.Black
                        )
                    }
                }
                AnimatedVisibility(isShowPlay.value) {
                    Box(Modifier.clickable {
                        test_time = 0
                        // launch
                        if (explMode.value == ExplorerMode.AUTO) {
                            launchPlay()
                        } else if (explMode.value == ExplorerMode.MANUAL) {
                            indexOfScenario.value--
                            ctxScope.launch {

                                comparatorToSolenoid(indexOfScenario.value)
                            }
                            scenario.getOrNull(indexOfScenario.value)?.let { txtOfScenario.value = it.text }
                            //txtOfScenario.value = scenario.getOrNull(indexOfScenario.value)?.text
                            //txtOfScenario.value = scenario[indexOfScenario.value].text
                        }


                    }) {
                        Text(
                            if (explMode.value == ExplorerMode.AUTO) "▶" else "⏪",
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = (10).dp, start = 20.dp),
                            fontFamily = FontFamily.Default,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Box(Modifier.clickable {
                    //stop scenario

                    CoroutineScope(Dispatchers.IO).launch {
                        if (explMode.value == ExplorerMode.AUTO) {
                            reInitSolenoids()
                            GLOBAL_STATE.value = StateParseBytes.WAIT
//                            initSerialCommunication()
//                            startReceiveFullData()
                        } else if (explMode.value == ExplorerMode.MANUAL) {
                            indexOfScenario.value++
                            comparatorToSolenoid(indexOfScenario.value)

                            //txtOfScenario.value = scenario.getOrElse(indexOfScenario.value) { 0 }
                            scenario.getOrNull(indexOfScenario.value)?.let { txtOfScenario.value = it.text }
                            //txtOfScenario.value = scenario.getOrElse(indexOfScenario.value) { scenario[0] }.text
                        }
                    }
                }) {
                    Text(
                        if (explMode.value == ExplorerMode.AUTO) "⏸" else "⏩",
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = (10).dp, start = 20.dp),
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text("1st:${COM_PORT},${BAUD_RATE},${limitTime}ms \n2nd:${COM_PORT_2},${BAUD_RATE},${limitTime}ms ", modifier = Modifier.padding(top = (10).dp,start = 20.dp)
                    , fontFamily = FontFamily.Default, fontSize = 10.sp, fontWeight = FontWeight.Light, color = Color.DarkGray
                )
            }

            Row(Modifier) {
                Column(Modifier.width(100.dp).fillMaxHeight()) {
                    Box(Modifier.fillMaxSize().weight(1f).clickable {
                        if (scaleGauges.value<= 6.1f){
                            scaleGauges.value+= 0.25f
                        }

                    }) {
                        Text("🔍")
                    }
                    Box(Modifier.fillMaxSize().weight(1f).clickable {
                        if (scaleGauges.value> 1.1f){
                            scaleGauges.value-= 0.25f
                        }

                    }) {
                        Text("➖")
                    }
                }
                Box(Modifier.clickable {
                    isHidedCurrents.value = !isHidedCurrents.value
                }) {
                    Text(
                        "Hide Currents⚡️",
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = (10).dp, start = 20.dp),
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(Modifier.clickable {
                    CoroutineScope(Dispatchers.IO+CoroutineName("onCloseRequest")).launch {
                        delay(10)
                        pauseSerialComm()
                        scenario.clear()
                    }
                    screenNav.value = Screens.STARTER
                }) {
                    Text(
                        "Home↩️",
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = (10).dp, start = 20.dp),
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

        }


        //Spacer(Modifier.fillMaxWidth().height(10.dp))

        Row(Modifier.weight(5f)) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                //columns = GridCells.Adaptive(150.dp),
                columns = GridCells.Adaptive(200.dp*gscl.value),
                verticalArrangement =   Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
                // content padding
                contentPadding = PaddingValues(
                    start = 0.dp,
                    top = 0.dp,
                    end = 0.dp,
                    bottom = 0.dp
                )
            ) {
                itemsIndexed(pressures) { index , press ->
                    if (press.isVisible) {
                        Box(Modifier
                            .aspectRatio(1f).background(if (isDebugMode)Color.Red else Color.Transparent)
                            .onGloballyPositioned { coordinates ->
                                // Set column height using the LayoutCoordinates
                                if (coordinates.size.width != 0) {
                                    columnHeightDp = with(localDensity) { coordinates.size.width.toDp() }
                                }
                            }
                        ) {
                            GaugeX(
                                DpSize(columnHeightDp, columnHeightDp),
                                when(index) {
                                    0 -> pressure1X
                                    1 -> pressure2X
                                    2 -> pressure3X
                                    3 -> pressure4X
                                    4 -> pressure5X
                                    5 -> pressure6X
                                    6 -> pressure7X
                                    7 -> pressure8X

                                    8 ->  pressure9X
                                    9 ->  pressure10X
                                    10 -> pressure11X
                                    11 -> pressure12X
                                    12 -> pressure13X
                                    13 -> pressure14X
                                    14 -> pressure15X
                                    15 -> pressure16X
                                    else -> 0
                                },
                                (pressures[index].minValue),
                                (pressures[index].maxValue.toInt()),
                                type = "Бар",
                                displayName = pressures[index].displayName,
                                comment = pressures[index].commentString
                            )
                        }
                    }

                }
            }
        }
        if(!isHidedCurrents.value) {
            Row(Modifier.fillMaxSize().weight(2f)) {
                solenoidsPanel(sizeRow, duration)
            }
            Row(Modifier.fillMaxSize().weight(2f)) {
                solenoidsPanel2(sizeRow, duration)
            }
        }

    }
}
