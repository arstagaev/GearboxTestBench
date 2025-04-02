package ui.main_screen.center.support_elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import serial_port.MultiPortMode
import serial_port.writeToSerialPort
import ui.styles.fontDigital
import utils.*

@Composable
fun solenoidsPanel2(
    sizeRow: Size,
    duration: MutableStateFlow<Long>
) {
    val crctx = rememberCoroutineScope().coroutineContext

    var current9 by remember { mutableStateOf(-1) }
    var current10 by remember { mutableStateOf(-1) }
    var current11 by remember { mutableStateOf(-1) }
    var current12 by remember { mutableStateOf(-1) }
    var current13 by remember { mutableStateOf(-1) }
    var current14 by remember { mutableStateOf(-1) }
    var current15 by remember { mutableStateOf(-1) }
    var current16 by remember { mutableStateOf(-1) }



    //var internalIndexOfScenario = remember { indexOfScenario }
//    var pwm1 by remember { pwm1SeekBar }
//    var pwm2 by remember { pwm2SeekBar }
//    var pwm3 by remember { pwm3SeekBar }
//    var pwm4 by remember { pwm4SeekBar }
//
//    var pwm5 by remember { mutableStateOf(-1) }
//    var pwm6 by remember { mutableStateOf(-1) }
//    var pwm7 by remember { mutableStateOf(-1) }
//    var pwm8 by remember { mutableStateOf(-1) }

//    if (solenoids.size<5){
//        showMeSnackBar("Excel error",Color.Red)
//    }else {
//        showMeSnackBar("Excel config parse success",Color.White)
//    }
    LaunchedEffect(true) {
        CoroutineScope(Dispatchers.IO+crctx).launch {
            dataChunkCurrents2.collect {
                delay(DELAY_FOR_GET_DATA)

                current9 = it.firstCurrentData
                current10 = it.secondCurrentData
                current11 = it.thirdCurrentData
                current12 = it.fourthCurrentData

                current13 = it.fifthCurrentData
                current14 = it.sixthCurrentData
                current15 = it.seventhCurrentData
                current16 = it.eighthCurrentData
            }
        }

//        CoroutineScope(Dispatchers.IO +crctx).launch {
//            dataChunkCurrents2.collect {
//                delay(DELAY_FOR_GET_DATA)
//                current9  = it.firstCurrentData
//                current10 = it.secondCurrentData
//                current11 = it.thirdCurrentData
//                current12 = it.fourthCurrentData
//                current13 = it.fifthCurrentData
//                current14 = it.sixthCurrentData
//                current15 = it.seventhCurrentData
//                current16 = it.eighthCurrentData
//            }
//        }
    }
    val aspc = 4f
    Column(modifier = Modifier.fillMaxSize().background(Color.Magenta)) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),//.fillMaxSize(),
            //columns = GridCells.Adaptive(150.dp),
            columns = GridCells.Fixed(4),
            verticalArrangement =   Arrangement.spacedBy(0.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            // content padding
            contentPadding = PaddingValues(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 0.dp
            ),
            content = {
                if (solenoids[8].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(index = 9,solenoids[8].displayName, current = map(x=current9,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[8].currentMaxValue), maxPWM = solenoids[8].maxPWM, step = solenoids[8].step, duration = duration)

                        }
                    }
                }
                if (solenoids[9].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 10,
                                solenoids[9].displayName,
                                current = map(
                                    x = current10,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[9].currentMaxValue
                                ),
                                maxPWM = solenoids[9].maxPWM,
                                step = solenoids[9].step,
                                duration = duration
                            )

                        }
                    }
                }
                if (solenoids[10].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 11,
                                solenoids[10].displayName,
                                current = map(
                                    x = current11,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[10].currentMaxValue
                                ),
                                maxPWM = solenoids[10].maxPWM,
                                step = solenoids[10].step,
                                duration = duration
                            )

                        }
                    }
                }
                if (solenoids[11].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 12,
                                solenoids[11].displayName,
                                current = map(
                                    x = current12,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[11].currentMaxValue
                                ),
                                maxPWM = solenoids[11].maxPWM,
                                step = solenoids[11].step,
                                duration = duration
                            )

                        }
                    }
                }
                /////
                if(solenoids[12].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 13,
                                solenoids[12].displayName,
                                current = map(
                                    x = current13,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[12].currentMaxValue
                                ),
                                maxPWM = solenoids[12].maxPWM,
                                step = solenoids[12].step,
                                duration = duration
                            )
                        }
                    }
                }
                if (solenoids[13].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 14,
                                solenoids[13].displayName,
                                current = map(
                                    x = current14,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[13].currentMaxValue
                                ),
                                maxPWM = solenoids[13].maxPWM,
                                step = solenoids[13].step,
                                duration = duration
                            )
                        }
                    }
                }
                if (solenoids[14].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 15,
                                solenoids[14].displayName,
                                current = map(
                                    x = current15,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[14].currentMaxValue
                                ),
                                maxPWM = solenoids[14].maxPWM,
                                step = solenoids[14].step,
                                duration = duration
                            )
                        }
                    }
                }
                if (solenoids[15].isVisible) {
                    item {
                        Box(Modifier.aspectRatio(aspc)) {
                            justBar(
                                index = 16,
                                solenoids[15].displayName,
                                current = map(
                                    x = current16,
                                    in_min = 0,
                                    in_max = 4095,
                                    out_min = 0,
                                    out_max = solenoids[15].currentMaxValue
                                ),
                                maxPWM = solenoids[15].maxPWM,
                                step = solenoids[15].step,
                                duration = duration
                            )
                        }
                    }
                }

//                if (solenoids[8].isVisible) {
//                    item {
//                        justBar(
//                            index = 9,
//                            solenoids[8].displayName,
//                            current = map(
//                                x = current16,
//                                in_min = 0,
//                                in_max = 4095,
//                                out_min = 0,
//                                out_max = solenoids[0].currentMaxValue
//                            ),
//                            maxPWM = solenoids[8].maxPWM,
//                            step = solenoids[8].step,
//                            duration = duration
//                        )
//                    }
//                }
            }
        )
//        Row(
//            modifier = Modifier.fillMaxSize().weight(1f) //.padding(10.dp)
////            .width(sizeRow.width.dp)
////            .height(IntrinsicSize.Min)
//                .background(Color.Black)
//        ) {
//
//            justBar(index = 1,solenoids[0].displayName, current = map(x=current1,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[0].currentMaxValue), maxPWM = solenoids[0].maxPWM, step = solenoids[0].step, duration = duration)
//            justBar(index = 2,solenoids[1].displayName, current = map(x=current2,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[1].currentMaxValue), maxPWM = solenoids[1].maxPWM, step = solenoids[1].step, duration = duration)
//            justBar(index = 3,solenoids[2].displayName, current = map(x=current3,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[2].currentMaxValue), maxPWM = solenoids[2].maxPWM, step = solenoids[2].step, duration = duration)
//            justBar(index = 4,solenoids[3].displayName, current = map(x=current4,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[3].currentMaxValue), maxPWM = solenoids[3].maxPWM, step = solenoids[3].step, duration = duration)
//        }
//        Row(
//            modifier = androidx.compose.ui.Modifier.weight(1f) //.padding(10.dp)
////                .width(sizeRow.width.dp)
////                .height(IntrinsicSize.Min)
//                .background(Color.Black)
//        ) {
//            justBar(index = 5, solenoids[4].displayName, current = map(x=current5,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[0].currentMaxValue), maxPWM = solenoids[4].maxPWM, step = solenoids[4].step, duration = duration)
//            justBar(index = 6, solenoids[5].displayName, current = map(x=current6,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[0].currentMaxValue), maxPWM = solenoids[5].maxPWM, step = solenoids[5].step, duration = duration)
//            justBar(index = 7, solenoids[6].displayName, current = map(x=current7,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[0].currentMaxValue), maxPWM = solenoids[6].maxPWM, step = solenoids[6].step, duration = duration)
//            justBar(index = 8, solenoids[7].displayName, current = map(x=current8,in_min=0, in_max = 4095, out_min=0, out_max = solenoids[0].currentMaxValue), maxPWM = solenoids[7].maxPWM, step = solenoids[7].step, duration = duration)
//        }
    }

}

var ch9 = 0x00.toByte()
var ch10 = 0x00.toByte()
var ch11 = 0x00.toByte()
var ch12 = 0x00.toByte()

var ch13 = 0x00.toByte()
var ch14 = 0x00.toByte()
var ch15 = 0x00.toByte()
var ch16 = 0x00.toByte()

@Composable
private fun justBar(
    index: Int,
    channelName: String,
    current: Int,
    maxPWM: Int,
    step: Int,
    duration: MutableStateFlow<Long>,
    //PWM: Int
    ) {

    // PWM [from 0 to 255]
    val PWMremember = remember {
        when(index) {
            9 -> pwm9SeekBar
            10 -> pwm10SeekBar
            11 -> pwm11SeekBar
            12 -> pwm12SeekBar

            13 -> pwm13SeekBar
            14 -> pwm14SeekBar
            15 -> pwm15SeekBar
            16 -> pwm16SeekBar
            else -> pwm9SeekBar
        }
    }


        Column(
            modifier = Modifier.padding(0.dp, 1.dp).fillMaxSize()
                //.width(200.dp)
                //.height(90.dp)
                .background(Color.Black)
                .padding(5.dp)
            //.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxSize().weight(2f)//.height(60.dp)
                    .background(Color.Black),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.DarkGray).weight(1f).clickable {
                        PWMremember.value = 0
                        //pos.value = 1.0f
                        CoroutineScope(Dispatchers.IO).launch {
                            selectorForChannels(index, PWMremember.value.toByte())
                            //selectorForChannels(index, PWMremember.value.to2ByteArray()[0])
                            if (isChangedFirstFourth) {
                                writeToSerialPort(byteArrayOf(0x71,ch9, 0x00,ch10, 0x00,ch11, 0x00,ch12, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 100L, mode = MultiPortMode.Second)

                            }else {
                                writeToSerialPort(byteArrayOf(0x51,ch13, 0x00,ch14, 0x00,ch15, 0x00,ch16, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 0L, mode = MultiPortMode.Second)

                            }
                            delay(100)
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp).align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = "<<",
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Gray).weight(2f).clickable {
                        PWMremember.value = PWMremember.value - step
                        if (PWMremember.value < 0) {
                            PWMremember.value = 0
                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            selectorForChannels(index, PWMremember.value.toByte())
                            //selectorForChannels(index, PWMremember.value.to2ByteArray()[0])
                            if (isChangedFirstFourth) {
                                writeToSerialPort(byteArrayOf(0x71,ch9, 0x00,ch10, 0x00,ch11, 0x00,ch12, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 100L, mode = MultiPortMode.Second)
                            }else {
                                writeToSerialPort(byteArrayOf(0x51,ch13, 0x00,ch14, 0x00,ch15, 0x00,ch16, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 0L, mode = MultiPortMode.Second)
                            }
                        }
                        //pos.value-= 0.1f
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp).align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = "-",
                        color = Color.White
                    )
                }


                Spacer(modifier = Modifier.width(10.dp).fillMaxHeight())
                Column(Modifier.fillMaxSize().weight(5f)) {
                    Text(
                        "${channelName}",
                        modifier = Modifier.fillMaxSize().weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 8.sp,
                        color = Color.White
                    )
                    Text(
                        "${current}",
                        modifier = Modifier.fillMaxSize().weight(1f),
                        fontFamily = fontDigital,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                Column(Modifier.fillMaxSize().weight(5f)) {
                    Text(
                        "PWM (%)",
                        modifier = Modifier.fillMaxSize().weight(1f),
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Text(
                        "${map(PWMremember.value,0,255,0,100)}",
                        modifier = Modifier.fillMaxSize().weight(1f),
                        fontFamily = fontDigital,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp).fillMaxHeight())


                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Gray).weight(2f).clickable {
                        PWMremember.value = PWMremember.value + step
                        if (PWMremember.value > maxPWM) {
                            PWMremember.value = maxPWM
                        }
                        if (PWMremember.value > 255) {
                            PWMremember.value = 255
                        }
                        println("WELL ${(PWMremember.value.toFloat())}")

                        CoroutineScope(Dispatchers.IO).launch {
                            selectorForChannels(index, PWMremember.value.toByte())
                            if (isChangedFirstFourth) {
                                writeToSerialPort(byteArrayOf(0x71,ch9, 0x00,ch10, 0x00,ch11, 0x00,ch12, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 100L, mode = MultiPortMode.Second)

                            }else {
                                writeToSerialPort(byteArrayOf(0x51,ch13, 0x00,ch14, 0x00,ch15, 0x00,ch16, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 0L, mode = MultiPortMode.Second)

                            }
                            delay(100)
                        }
                        //pos.value += 0.1f
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp).align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = "+",
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.DarkGray).weight(1f).clickable {
                        PWMremember.value = 255
                        if (PWMremember.value > maxPWM) {
                            PWMremember.value = maxPWM
                        }
                        //pos.value = 1.0f
                        CoroutineScope(Dispatchers.IO).launch {
                            selectorForChannels(index, PWMremember.value.toByte())
                            if (isChangedFirstFourth) {
                                writeToSerialPort(byteArrayOf(0x71,ch9, 0x00,ch10, 0x00,ch11, 0x00,ch12, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 100L, mode = MultiPortMode.Second)

                            }else {
                                writeToSerialPort(byteArrayOf(0x51,ch13, 0x00,ch14, 0x00,ch15, 0x00,ch16, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 0L, mode = MultiPortMode.Second)

                            }
                            delay(100)
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp).align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = ">>",
                        color = Color.White
                    )
                }

            }
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f)//.height(20.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    progress = (map(PWMremember.value,0,255,0,100)/100f).toFloat() //((PWMremember.value.toFloat() * 100f) / 255f)/100f //rndTo2deci(pos.value)
                )
            }
        }
}
private var isChangedFirstFourth = true

private fun selectorForChannels(chIndex: Int, byte: Byte) {
    isChangedFirstFourth = chIndex in 9..12

    when(chIndex) {
        9 -> ch9 = byte
        10 -> ch10 = byte
        11 -> ch11 = byte
        12 -> ch12 = byte

        13 -> ch13 = byte
        14 -> ch14 = byte
        15 -> ch15 = byte
        16 -> ch16 = byte
    }
}

