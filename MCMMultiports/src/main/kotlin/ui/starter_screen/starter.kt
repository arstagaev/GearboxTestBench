package ui.starter_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import openChartViewer
import openLastScenario
import openNewScenario
import parsing_excel.targetParseScenario
import screenNav
import showMeSnackBar
import storage.PickTarget
import storage.refreshParameters
import storage.openPicker
import ui.navigation.Screens
import ui.styles.fontDigital
import ui.styles.fontRoboGirls
import ui.styles.fontUbuntu
import utils.*


@OptIn(ExperimentalTextApi::class, ExperimentalComposeUiApi::class)
@Composable
fun StarterScreen() {
    //var remarrayports = remember { arrayOfComPorts }

    var choosenCOM = remember { mutableStateOf(0) }
    var choosenCOM2 = remember { mutableStateOf(0) }

    //TODO remove loop after Decompose
    LaunchedEffect(true) {
        var isFirstPortFounded = false

        while (isFirstPortFounded == false) {
            arrayOfComPorts = getComPorts_Array() as Array<SerialPort>
            println(arrayOfComPorts.joinToString { it.systemPortName })


            arrayOfComPorts.forEachIndexed { index, serialPort ->
                //println(">>> ${serialPort.descriptivePortName.contains("Silicon")}")
                if (serialPort.descriptivePortName.contains("Silicon")) { // Silicon Labs
                    if (!isFirstPortFounded) {
                        choosenCOM.value = index
                        COM_PORT = serialPort.systemPortName
                        isFirstPortFounded = true
                    } else {
                        choosenCOM2.value = index
                        COM_PORT_2 = serialPort.systemPortName
                    }
                }
            }
            delay(1000)
        }

    }


    var expandedOperator by remember { mutableStateOf(false) }

    var expandedCom by remember { mutableStateOf(false) }
    var expandedCom2 by remember { mutableStateOf(false) }

    var expandedBaud by remember { mutableStateOf(false) }
    var expandedSound by remember { mutableStateOf(false) }
    var visibilitySettings = remember { mutableStateOf(false)}


    var choosenBaud = remember { mutableStateOf(BAUD_RATE) }
    val textState = remember { mutableStateOf(OPERATOR_ID) }
    var listOfOperators = mutableListOf<String>()//loadOperators()

    var crtxscp = rememberCoroutineScope().coroutineContext



    Column(Modifier.fillMaxSize().background(Color.Black)) {
        Row(modifier = Modifier.fillMaxSize().weight(2f), horizontalArrangement = Arrangement.Center) {

            //Image("",painter = painterResource("drawable/trs.jpg"))
            Text("MCM - Modulation Control Module",
                modifier = Modifier//.fillMaxSize()
                    .padding(top = 20.dp).clickable {
                    //screenNav.value = Screens.MAIN
                }, fontSize = 50.sp, fontFamily = fontRoboGirls, color = Color.White, textAlign = TextAlign.Justify)
        }
        Row(modifier = Modifier.fillMaxSize().weight(3f).background(Color.Black), horizontalArrangement = Arrangement.Center) {
//            Image(
//                painter = painterResource("drawable/trs.jpg"),
//                contentDescription = null
//            )
            Column(Modifier.padding(16.dp)) {

                TextField(colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor =  Color.Transparent, //hide the indicator
                    unfocusedIndicatorColor = Color.Green),
                    modifier = Modifier.fillMaxWidth(),
                    value = textState.value,
                    onValueChange = {
                        textState.value = it
                        OPERATOR_ID = it
                        refreshParameters()
                    },
                    textStyle = TextStyle.Default.copy(fontSize = 35.sp)
                )

                Box {
                    Text("OPERATOR ID ⬆️",
                        modifier = Modifier.fillMaxSize().clickable {
                            expandedOperator = true
                        }, fontSize = 20.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)

                    DropdownMenu(
                        modifier = Modifier.background(Color.White),
                        expanded = expandedOperator,
                        onDismissRequest = { expandedOperator = false },
                    ) {
                        repeat(listOfOperators.size) {
                            Text("${listOfOperators[it]}", fontSize=18.sp, modifier = Modifier.fillMaxSize().padding(10.dp).clickable(onClick={}))
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxSize().weight(3f).padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            var activeOS by remember { mutableStateOf(false) }
            var activeOLS by remember { mutableStateOf(false) }
            var activeOC by remember { mutableStateOf(false) }
            var activeS by remember { mutableStateOf(false) }

            Box(Modifier.width(200.dp).background(if (activeOS)Color.Blue else Color.Transparent).border(BorderStroke(2.dp, Color.Blue))
                .onPointerEvent(PointerEventType.Enter) { activeOS = true }
                .onPointerEvent(PointerEventType.Exit) { activeOS = false }
                .clickable {
                    openNewScenario()

                }) {
                Text("Open Scenario",
                    modifier = Modifier.padding(4.dp), fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)
            }

            Box(Modifier.width(200.dp).background(if (activeOLS)Color.Blue else Color.Transparent).border(BorderStroke(2.dp, Color.Blue))
                .onPointerEvent(PointerEventType.Enter) { activeOLS = true }
                .onPointerEvent(PointerEventType.Exit) { activeOLS = false }
                .clickable {
                    openLastScenario()
                }) {
                Text("Open last scenario",
                    modifier = Modifier.padding(4.dp), fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)
            }

            Box(Modifier.width(200.dp).background(if (activeOC)Color.Blue else Color.Transparent).border(BorderStroke(2.dp, Color.Blue))
                .onPointerEvent(PointerEventType.Enter) { activeOC = true }
                .onPointerEvent(PointerEventType.Exit) { activeOC = false }
                .clickable {
                   openChartViewer()

                }) {
                Text("Open Chart",
                    modifier = Modifier.padding(4.dp), fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)
            }

            Box(Modifier.width(200.dp).background(if (activeS)Color.Blue else Color.Transparent)
                .border(BorderStroke(2.dp, Color.Blue))
                .onPointerEvent(PointerEventType.Enter) { activeS = true }
                .onPointerEvent(PointerEventType.Exit) { activeS = false }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {},
                        onDoubleTap = {
                            screenNav.value = Screens.EASTER_EGG
                        },
                        onLongPress = {},
                        onTap = {
                            visibilitySettings.value = !visibilitySettings.value
                        }
                    )
                }
                ) {
                Text("Settings",
                    modifier = Modifier.padding(4.dp)
                    , fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)
            }


//            Box(Modifier.width(200.dp).border(BorderStroke(2.dp, Color.Blue))
//                .clickable { visibilitySettings.value = !visibilitySettings.value }) {
//                Text("Settings",
//                    modifier = Modifier.padding(4.dp)
//                    , fontSize = 24.sp, fontFamily = fontUbuntu,
//                    color = Color.White, textAlign = TextAlign.Center
//                )
//
//            }
//            Box(Modifier.width(200.dp).border(BorderStroke(2.dp, Color.Blue))) {
//                Text("Quick",
//                    modifier = Modifier.padding(4.dp).clickable {
//                        screenNav.value = Screens.MAIN
//                    }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)
//
//            }
        }


        if(visibilitySettings.value) {
            Row(modifier = Modifier.fillMaxSize().weight(4f).background(Color.DarkGray),
                horizontalArrangement = Arrangement.SpaceBetween) {
                LazyColumn(Modifier.width(300.dp)) {
                    item {
                        Row {
                            Text("COM Port:",
                                modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                }, fontSize = 24.sp, fontFamily = FontFamily.Cursive, color = Color.White, textAlign = TextAlign.Center)

                            Box {
                                Text(
                                    if (arrayOfComPorts.isEmpty()) "‼️NO COM PORTS‼️" else COM_PORT,// arrayOfComPorts[choosenCOM.value].systemPortName,
                                    modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                        expandedCom = !expandedCom
                                    }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.Blue, textAlign = TextAlign.Center
                                )

                                DropdownMenu(
                                    modifier = Modifier.background(Color.White),
                                    expanded = expandedCom,
                                    onDismissRequest = { expandedCom = false },
                                ) {
                                    repeat(arrayOfComPorts.size-1) {
                                        Text("${arrayOfComPorts[it].descriptivePortName}", fontSize=18.sp, modifier = Modifier.fillMaxSize().padding(10.dp)
                                            .clickable(onClick= {
                                                choosenCOM.value = it
                                                COM_PORT = arrayOfComPorts[it].systemPortName
                                                logAct("DropdownMenu click ${COM_PORT}")
                                                refreshParameters()
                                            }))

                                    }
                                }
                            }

                        }
                    }

                    item {
                        Row {
                            Text("COM Port 2:",
                                modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                }, fontSize = 24.sp, fontFamily = FontFamily.Cursive, color = Color.White, textAlign = TextAlign.Center)

                            Box {
                                Text(
                                    if (arrayOfComPorts.isEmpty()) "‼️NO COM PORTS‼️" else COM_PORT_2,//arrayOfComPorts[choosenCOM2.value].systemPortName,
                                    modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                        expandedCom2 = !expandedCom2
                                    }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.Blue, textAlign = TextAlign.Center
                                )

                                DropdownMenu(
                                    modifier = Modifier.background(Color.White),
                                    expanded = expandedCom2,
                                    onDismissRequest = { expandedCom2 = false },
                                ) {
                                    repeat(arrayOfComPorts.size-1) {
                                        Text("${arrayOfComPorts[it].descriptivePortName}", fontSize=18.sp, modifier = Modifier.fillMaxSize().padding(10.dp)
                                            .clickable(onClick= {
                                                choosenCOM.value = it
                                                COM_PORT_2 = arrayOfComPorts[it].systemPortName
                                                logAct("DropdownMenu click ${COM_PORT_2}")
                                                refreshParameters()
                                            }))

                                    }
                                }
                            }

                        }
                    }

                    item {
                        Row {
                            Text("Baud-rate:",
                                modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)

                            Box {
                                Text(choosenBaud.value.toString(),
                                    modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                        expandedBaud = !expandedBaud
                                    }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.Blue, textAlign = TextAlign.Center)

                                DropdownMenu(
                                    modifier = Modifier.background(Color.White),
                                    expanded = expandedBaud,
                                    onDismissRequest = { expandedBaud = false },
                                ) {
                                    Text("38400",   fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 38400
                                    BAUD_RATE = choosenBaud.value
                                    })  .fillMaxSize().padding(10.dp))
                                    Text("57600",   fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 57600
                                    BAUD_RATE = choosenBaud.value
                                    })  .fillMaxSize().padding(10.dp))
                                    Text("115200",  fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 115200
                                    BAUD_RATE = choosenBaud.value
                                    }) .fillMaxSize().padding(10.dp))
                                    Text("128000",  fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 128000
                                    BAUD_RATE = choosenBaud.value
                                    }) .fillMaxSize().padding(10.dp))
                                    Text("256000",  fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 256000
                                    BAUD_RATE = choosenBaud.value
                                    }) .fillMaxSize().padding(10.dp))
                                    Text("500000",  fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 500000
                                    BAUD_RATE = choosenBaud.value
                                    }) .fillMaxSize().padding(10.dp))
                                    Text("1000000", fontSize=18.sp, modifier = Modifier.clickable(onClick= { choosenBaud.value = 1000000
                                    BAUD_RATE = choosenBaud.value
                                    }).fillMaxSize().padding(10.dp))
                                    Divider()
                                }
                            }
                        }
                    }

                    item {
                        Row {
                            Text("Sound type",
                                modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)

                            Box {
                                Text("${SOUND_ENABLED}",
                                    modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                        expandedSound = !expandedSound
                                    }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.Blue, textAlign = TextAlign.Center)

                                DropdownMenu(
                                    modifier = Modifier.background(Color.White),
                                    expanded = expandedSound,
                                    onDismissRequest = { expandedSound = false },
                                ) {
                                    Text("0",   fontSize=18.sp, modifier = Modifier.clickable(onClick= {
                                        SOUND_ENABLED = 0
                                    })  .fillMaxSize().padding(10.dp))
                                    Text("1",   fontSize=18.sp, modifier = Modifier.clickable(onClick= {
                                        SOUND_ENABLED = 1
                                    })  .fillMaxSize().padding(10.dp))
                                    Text("2",   fontSize=18.sp, modifier = Modifier.clickable(onClick= {
                                        SOUND_ENABLED = 2
                                    })  .fillMaxSize().padding(10.dp))

                                }
                            }
                        }
                    }

                    item {
                        Row {
                            Text("Enable Logger",
                                modifier = Modifier.width(200.dp).padding(4.dp).clickable {
                                }, fontSize = 24.sp, fontFamily = fontUbuntu, color = Color.White, textAlign = TextAlign.Center)


                            val checkedState = remember { mutableStateOf(SAVELOG) }
                            Checkbox(
                                checked = checkedState.value,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Blue,
                                    uncheckedColor = Color.Gray
                                ),
                                onCheckedChange = {
                                    checkedState.value = it
                                    SAVELOG = it
                                    refreshParameters()
                                }
                            )
                        }

                    }
                }

                Column(Modifier.width(600.dp).verticalScroll(rememberScrollState())) {
                    Row(Modifier.width(600.dp)) {
                        Text("⌨️\uD83D\uDD25Hot Keys:" +
                                "\n [ctrl + N] - new experiment" +
                                "\n [ctrl + L] - later experiment" +
                                "\n [ctrl + V] - open viewer" +
                                "\n [ctrl + Space] - start experiment" +
                                "\n [<-] - previous scenario" +
                                "\n [->] - next scenario" +
                                "",
                            modifier = Modifier.fillMaxSize().clickable {
                            },
                            fontSize = 24.sp,
                            fontFamily = fontUbuntu,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
