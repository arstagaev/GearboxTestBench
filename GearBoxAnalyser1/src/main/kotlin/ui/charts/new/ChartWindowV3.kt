package ui.charts.new//package ui.charts
//
//import Variables.allPackets
//import Variables.chatCanva
//import Variables.countPacketsPerSecond
//import Variables.measureAverageX
//import Variables.measureAverageY
//import Variables.measureAverageZ
//import Variables.measureMaxX
//import Variables.measureMaxY
//import Variables.measureMaxZ
//import Variables.measureMinX
//import Variables.measureMinY
//import Variables.measureMinZ
//import androidx.compose.ui.window.Window
//import androidx.compose.ui.window.application
//
//
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.runtime.Composable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonColors
//import androidx.compose.material.Text
//import androidx.compose.material.TextField
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.unit.TextUnit
//import androidx.compose.ui.unit.TextUnitType
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun ChartScreen(
//    items: List<Double>,
//    logCat: List<String>,
//    onMarkClicked: () -> Unit,
//    onStartClicked: () -> Unit,
//    onStopClicked: () -> Unit
//) {
//    var showRealtimeDialogFFT by remember { mutableStateOf(false) }
//    Column(modifier = Modifier.fillMaxSize()) {
//        LazyRow(
//            modifier = Modifier.fillMaxWidth().background(Color.DarkGray),
//            horizontalArrangement = Arrangement.spacedBy(8.dp) // adds 8.dp space between items
//        ) {
//            item {
//                Box(
//                    Modifier.width(150.dp).height(60.dp)
//                        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
//                        .padding(horizontal = 2.dp)
//                        .align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Min: \n${measureMinX.value.toInt()};${measureMinY.value.toInt()};${measureMinZ.value.toInt()}",
//                        fontSize = TextUnit(12f, TextUnitType.Sp),
//                        color = Color.White
//                    )
//                }
//            }
//
//            item {
//                Box(
//                    Modifier.width(150.dp).height(60.dp)
//                        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
//                        .padding(horizontal = 2.dp)
//                        .align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Avg: \n${measureAverageX.value.toInt()};${measureAverageY.value.toInt()};${measureAverageZ.value.toInt()}",
//                        fontSize = TextUnit(12f, TextUnitType.Sp),
//                        color = Color.White
//                    )
//                }
//            }
//
//            item {
//                Box(
//                    Modifier.width(150.dp).height(60.dp)
//                        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
//                        .padding(horizontal = 2.dp)
//                        .align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Max: \n${measureMaxX.value.toInt()};${measureMaxY.value.toInt()};${measureMaxZ.value.toInt()}",
//                        fontSize = TextUnit(12f, TextUnitType.Sp),
//                        color = Color.White
//                    )
//                }
//            }
//
//            item {
//                Box(
//                    Modifier.width(150.dp)
//                        .background(Color.Blue, shape = RoundedCornerShape(20.dp))
//                        .padding(horizontal = 2.dp)
//                        .align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Packets: \n${countPacketsPerSecond.value}/$allPackets",
//                        fontSize = TextUnit(12f, TextUnitType.Sp),
//                        color = Color.White
//                    )
//                }
//            }
//        }
//        // Top half: Chart placeholder
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth(),
//            contentAlignment = Alignment.Center
//        ) {
////            DynamicAccelerationChart()
//            DrawChart(
//                accelerationData = items,
//                modifier = Modifier.fillMaxSize().background(Color.LightGray)
//            )
//        }
//
//        HorizontalDivider()
//
//        // Bottom half: LazyColumn + Buttons
//        var textCommand by remember { mutableStateOf(TextFieldValue("")) }
//
//        Row(modifier = Modifier
//            .weight(1f)
//            .fillMaxWidth()
//        ) {
//            Column(
//                modifier = Modifier
//                    .weight(2f)
//                    .fillMaxHeight()
//                    .padding(1.dp)
//                    .background(Color.Gray)
//            ) {
//                LazyColumn(
//                    modifier = Modifier
//                        .weight(10f)
//                        .fillMaxSize()
//                        .padding(1.dp)
//                        .background(Color.DarkGray)
//                ) {
//                    items(logCat) { item ->
//                        Text(text = item.toString(), modifier = Modifier.padding(vertical = 8.dp), color = Color.White)
//                    }
//                }
//                Row(
//                    modifier = Modifier
//                        .weight(2f)
//                        .fillMaxSize(),
//                ) {
//                    TextField(
//                        modifier = Modifier.fillMaxSize().weight(5f),
//                        value = textCommand,
//                        onValueChange = {
//                            textCommand = it
//                        },
////                        label = { Text(text = "Your textCommand") },
//                        placeholder = { Text(text = "Text command") },
//                    )
//                    Box(modifier = Modifier.fillMaxSize().weight(1f).background(ColorButton1).align(Alignment.CenterVertically).clickable {
//
//                        chatCanva.add(runCommand(textCommand.text))
//
//                    }) {
//                        Text(modifier = Modifier.align(Alignment.Center), text = "OK", color = Color.White,
//                            fontSize = TextUnit(12f, TextUnitType.Sp))
//                    }
//                }
//            }
//
//
//            LazyColumn(
//                modifier = Modifier
//                    .wrapContentWidth()
//                    .fillMaxHeight()
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                item {
//                    Button(onClick = onMarkClicked, colors = ButtonColors(
//                        containerColor = Color.Red, contentColor = Color.Black,
//                        disabledContentColor = Color.DarkGray, disabledContainerColor = Color.LightGray)
//                    ) { Text("ALERT") }
//                }
//                item { Button(onClick = {}) { Text("ON/OFF \nConnect") } }
//                item { Button(onClick = onStartClicked) { Text("Start rec") } }
//                item { Button(onClick = onStopClicked) { Text("Stop rec") } }
//                item { Button(onClick = {
//                    showRealtimeDialogFFT = true
//                }) { Text("Real-time FFT Analysis") } }
//            }
//        }
//    }
//    if (showRealtimeDialogFFT) {
//        FFTAlertDialog(items.toList().map { it.toFloat() },
//            onDismiss = { showRealtimeDialogFFT = false }
//        )
//    }
//}
//
//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//
//    }
//}
//
