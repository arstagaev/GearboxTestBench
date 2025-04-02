package serial_port

import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.*
import ui.main_screen.center.support_elements.*
import utils.*
import java.math.BigInteger

private var serialPort: SerialPort = SerialPort.getCommPort(COM_PORT)
private var serialPort_SECOND: SerialPort = SerialPort.getCommPort(COM_PORT_2)
private val crtx2 = CoroutineName("main")

suspend fun initSerialCommunication() {
    arrayOfComPorts = getComPorts_Array() as Array<SerialPort>

//   if ( isWindows == false ) {
//       println("IS MAC OS => without USB CONNECTION !!!")
//       return
//   }

    println(">>>serial communication has been started, COM_PORT:$COM_PORT ${BAUD_RATE}")
    serialPort = SerialPort.getCommPort(COM_PORT)
    serialPort.setComPortParameters(BAUD_RATE,8,1, SerialPort.NO_PARITY)
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
    serialPort.openPort()
    //serialPort.clearBreak()


    delay(2000)
    println("Run Callbacks::")
    val listener = PacketListener()
    serialPort.addDataListener(listener)
    //showMeSnackBar("baudRate of Port:${speedOfPort.value.text.toInt()} ", Color.White)
//////////////////////////////////////////////////////////////////////////////////////////////// SECOND PORT
    serialPort_SECOND = SerialPort.getCommPort(COM_PORT_2)
    serialPort_SECOND.setComPortParameters(BAUD_RATE,8,1, SerialPort.NO_PARITY)
    serialPort_SECOND.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
    serialPort_SECOND.openPort()

    if (serialPort_SECOND.isOpen) {
        println(">>>serial communication has been started, COM_PORT:$COM_PORT_2 ${BAUD_RATE}")

        //serialPort.clearBreak()
    } else {
        println("serialPort_SECOND is closed")
//        pressures.removeAt(8)
//        pressures.removeAt(9)
//        pressures.removeAt(10)
//        pressures.removeAt(11)
//
//        pressures.removeAt(12)
//        pressures.removeAt(13)
//        pressures.removeAt(14)
//        pressures.removeAt(15)
    }


    delay(2000)
    println("Run Callbacks::")
    val listener2 = PacketListener2()
    serialPort_SECOND.addDataListener(listener2)
}

suspend fun startReceiveFullData() {
//    repeat(
//        getComPorts_Array().size
//    ) {
//        println(">>>Available Com ports:${getComPorts_Array().get(it).systemPortName} is Open: ${getComPorts_Array().get(it).isOpen}||${getComPorts_Array().get(it).descriptivePortName}")
//    }
    if ( isWindows == false ) {
        println("IS MAC OS => without USB CONNECTION !!!")
        return
    }

    if (!serialPort.isOpen) {
        initSerialCommunication()
    }
    writeToSerialPort(byteArrayOf(0x74.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00))

}

fun stopSerialCommunication() {
    serialPort.removeDataListener()
    serialPort.closePort()

    serialPort_SECOND.removeDataListener()
    serialPort_SECOND.closePort()

    println(">< STOP SERIAL PORT // is Open:${serialPort.isOpen} ${BAUD_RATE}")
}

suspend fun pauseSerialComm() {
    //writeToSerialPort(byteArrayOf(0x71,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 0L)
    //writeToSerialPort(byteArrayOf(0x51,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00),false, delay = 0L)
    sendZerosToSolenoid()

    //writeToSerialPort(byteArrayOf(0x78, 0x8A.toByte(), 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00),withFlush = false)
    //delay(500)
    //serialPort.flushIOBuffers()
    writeToSerialPort(byteArrayOf(0x54, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,0x00, 0x00,0x00, 0x00,0x00),withFlush = false)

}

enum class MultiPortMode { First, Second, Dual }

suspend fun writeToSerialPort(sendBytes: ByteArray, withFlush: Boolean = false, delay: Long = 0L, mode: MultiPortMode = MultiPortMode.Dual) {
//    if (sendBytes[0] == 0x74.toByte()) {
//        //startTimer()
//    }
    repeat(1) {

        if (mode == MultiPortMode.First || mode == MultiPortMode.Dual) {
            logAct(">>>first: ${COM_PORT} ${sendBytes.toHexString()}   size of bytes: ${sendBytes.size}")
            serialPort.writeBytes(sendBytes, sendBytes.size.toLong())


        }

        if (mode == MultiPortMode.Second || mode == MultiPortMode.Dual) {
            try {
                if (serialPort_SECOND.isOpen ) {
                    logAct(">>>second: ${COM_PORT_2} ${sendBytes.toHexString()}   size of bytes: ${sendBytes.size}")
                    serialPort_SECOND.writeBytes(sendBytes, sendBytes.size.toLong())
                }
            }catch (e: Exception) { println("ERROR io Commands!!! ${e.message}") }
        }



        if (withFlush) {
            serialPort.flushIOBuffers()
            try {
                if (serialPort_SECOND.isOpen) {
                    serialPort_SECOND.flushIOBuffers()
                }
            }catch (e: Exception) {

                println("ERROR io Commands!!! ${e.message}")
            }
        }
        delay(delay)
        //println("goo " + sendBytes.size)
    }

}

// increment and decrement steps of scenario
suspend fun comparatorToSolenoid(newIndex: Int) {

    val idx = checkIntervalScenarios(newIndex)

    logGarbage("comparatorToSolenoid ${idx} ~~~ ]${scenario.size}[")


    pwm1SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[0].takeIf { it <= solenoids[0].maxPWM } }) ?: solenoids[0].maxPWM
    pwm2SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[1].takeIf { it <= solenoids[1].maxPWM } }) ?: solenoids[1].maxPWM
    pwm3SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[2].takeIf { it <= solenoids[2].maxPWM } }) ?: solenoids[2].maxPWM
    pwm4SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[3].takeIf { it <= solenoids[3].maxPWM } }) ?: solenoids[3].maxPWM
    pwm5SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[4].takeIf { it <= solenoids[4].maxPWM } }) ?: solenoids[4].maxPWM
    pwm6SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[5].takeIf { it <= solenoids[5].maxPWM } }) ?: solenoids[5].maxPWM
    pwm7SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[6].takeIf { it <= solenoids[6].maxPWM } }) ?: solenoids[6].maxPWM
    pwm8SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[7].takeIf { it <= solenoids[7].maxPWM } }) ?: solenoids[7].maxPWM

    pwm9SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs [8].takeIf { it <= solenoids[8].maxPWM } }) ?: solenoids[8].maxPWM
    pwm10SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[9].takeIf { it <= solenoids[9].maxPWM } }) ?: solenoids[9].maxPWM
    pwm11SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[10].takeIf { it <= solenoids[10].maxPWM } }) ?: solenoids[10].maxPWM
    pwm12SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[11].takeIf { it <= solenoids[11].maxPWM } }) ?: solenoids[11].maxPWM
    pwm13SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[12].takeIf { it <= solenoids[12].maxPWM } }) ?: solenoids[12].maxPWM
    pwm14SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[13].takeIf { it <= solenoids[13].maxPWM } }) ?: solenoids[13].maxPWM
    pwm15SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[14].takeIf { it <= solenoids[14].maxPWM } }) ?: solenoids[14].maxPWM
    pwm16SeekBar.value = (scenario.getOrNull(idx)?.let { it.chs[15].takeIf { it <= solenoids[15].maxPWM } }) ?: solenoids[15].maxPWM

//    pwm1SeekBar.value = (scenario.getOrNull(idx){})              .values[0].takeIf { it <= solenoids[0].maxPWM } ?: solenoids[0].maxPWM // [from 0 to 255]
//    pwm2SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[1].takeIf { it <= solenoids[1].maxPWM } ?: solenoids[1].maxPWM
//    pwm3SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[2].takeIf { it <= solenoids[2].maxPWM } ?: solenoids[2].maxPWM
//    pwm4SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[3].takeIf { it <= solenoids[3].maxPWM } ?: solenoids[3].maxPWM
//    pwm5SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[4].takeIf { it <= solenoids[4].maxPWM } ?: solenoids[4].maxPWM
//    pwm6SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[5].takeIf { it <= solenoids[5].maxPWM } ?: solenoids[5].maxPWM
//    pwm7SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[6].takeIf { it <= solenoids[6].maxPWM } ?: solenoids[6].maxPWM
//    pwm8SeekBar.value = (scenario.getOrElse(idx){ scenario[0] }) .values[7].takeIf { it <= solenoids[7].maxPWM } ?: solenoids[7].maxPWM

    //logGarbage("pwm1SeekBar -> ${pwm1SeekBar.value}  ${pwm2SeekBar.value}  ${pwm3SeekBar.value}")

    ch1 = pwm1SeekBar.value.toByte() //(rawPreByte0).toByte() // from 0 to 0xFF
    ch2 = pwm2SeekBar.value.toByte() //(rawPreByte1).toByte()
    ch3 = pwm3SeekBar.value.toByte() //(rawPreByte2).toByte()
    ch4 = pwm4SeekBar.value.toByte() //(rawPreByte3).toByte()
    ch5 = pwm5SeekBar.value.toByte() //(rawPreByte4).toByte()
    ch6 = pwm6SeekBar.value.toByte() //(rawPreByte5).toByte()
    ch7 = pwm7SeekBar.value.toByte() //(rawPreByte6).toByte()
    ch8 = pwm8SeekBar.value.toByte() //(rawPreByte7).toByte()


    ch9 =  pwm9SeekBar.value.toByte()
    ch10 = pwm10SeekBar.value.toByte()
    ch11 = pwm11SeekBar.value.toByte()
    ch12 = pwm12SeekBar.value.toByte()
    ch13 = pwm13SeekBar.value.toByte()
    ch14 = pwm14SeekBar.value.toByte()
    ch15 = pwm15SeekBar.value.toByte()
    ch16 = pwm16SeekBar.value.toByte()


    writeToSerialPort(byteArrayOf(0x71, ch1, 0x00, ch2, 0x00, ch3, 0x00, ch4, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 100L, mode = MultiPortMode.First)

    writeToSerialPort(byteArrayOf(0x51, ch5, 0x00, ch6, 0x00, ch7, 0x00, ch8, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 0L, mode = MultiPortMode.First)

    // Second:

    writeToSerialPort(byteArrayOf(0x71, ch9, 0x00, ch10, 0x00, ch11, 0x00, ch12, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 100L, mode = MultiPortMode.Second)

    writeToSerialPort(byteArrayOf(0x51, ch13, 0x00, ch14, 0x00, ch15, 0x00, ch16, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 0L, mode = MultiPortMode.Second)

//    pwm1SeekBar.value = map( rawPreByte0,0,255,0,100 )
//    pwm2SeekBar.value = map( rawPreByte1,0,255,0,100 )
//    pwm3SeekBar.value = map( rawPreByte2,0,255,0,100 )
//    pwm4SeekBar.value = map( rawPreByte3,0,255,0,100 )
//    pwm5SeekBar.value = map( rawPreByte4,0,255,0,100 )
//    pwm6SeekBar.value = map( rawPreByte5,0,255,0,100 )
//    pwm7SeekBar.value = map( rawPreByte6,0,255,0,100 )
//    pwm8SeekBar.value = map( rawPreByte7,0,255,0,100 )


//    repeat(size-1) {
//        btry += byteArrayOf(scenario[newIndex].values[it].toByte())
//    }

    //writeToSerialPort(btry)
    //txtOfScenario.value = scenario[idx].text
    txtOfScenario.value = scenario.getOrElse(idx){
        indexOfScenario.value = 0
        scenario[0]
    }.text

    //commentOfScenario.value = scenario[idx].comment
}

suspend fun sendZerosToSolenoid() {
    ch1 = 0x00.toByte()
    ch2 = 0x00.toByte()
    ch3 = 0x00.toByte()
    ch4 = 0x00.toByte()
    ch5 = 0x00.toByte()
    ch6 = 0x00.toByte()
    ch7 = 0x00.toByte()
    ch8 = 0x00.toByte()

    ch9 = 0x00.toByte()
    ch10 = 0x00.toByte()
    ch11 = 0x00.toByte()
    ch12 = 0x00.toByte()
    ch13 = 0x00.toByte()
    ch14 = 0x00.toByte()
    ch15 = 0x00.toByte()
    ch16 = 0x00.toByte()

    writeToSerialPort(byteArrayOf(
        0x71, ch1,
        0x00, ch2,
        0x00, ch3,
        0x00, ch4,
        0x00,0x00, 0x00,0x00, 0x00,0x00), delay = 100L, mode = MultiPortMode.First)

    writeToSerialPort(byteArrayOf(
        0x51, ch5,
        0x00, ch6,
        0x00, ch7,
        0x00, ch8, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 0L, mode = MultiPortMode.First)

    // For Second port:

    writeToSerialPort(byteArrayOf(
        0x71, ch9,
        0x00, ch10,
        0x00, ch11,
        0x00, ch12,
        0x00,0x00, 0x00,0x00, 0x00,0x00), delay = 100L, mode = MultiPortMode.Second)

    writeToSerialPort(byteArrayOf(
        0x51, ch13,
        0x00, ch14,
        0x00, ch15,
        0x00, ch16, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 0L, mode = MultiPortMode.Second)

}

suspend fun sendScenarioToController() {
    scenario.forEachIndexed { index, s ->
        val time = BigInteger.valueOf(s.time.toLong()).toByteArray()

        val send = byteArrayOf(
            0x73,index.toByte(),0x00,

            s.chs[0].toByte(),
            s.chs[1].toByte(),
            s.chs[2].toByte(),
            s.chs[3].toByte(),

            s.chs[4].toByte(),
            s.chs[5].toByte(),
            s.chs[6].toByte(),
            s.chs[7].toByte(),

            //time.getOrNull(1).takeIf { time.size == 2 } ?: 0x00,
            time.getOrNull(1) ?: 0x00,
            time[0],

            0x00
        )

        val send2 = byteArrayOf(
            0x73,index.toByte(),0x00,

            s.chs[8].toByte(),
            s.chs[9].toByte(),
            s.chs[10].toByte(),
            s.chs[11].toByte(),

            s.chs[12].toByte(),
            s.chs[13].toByte(),
            s.chs[14].toByte(),
            s.chs[15].toByte(),

            //time.getOrNull(1).takeIf { time.size == 2 } ?: 0x00,
            time.getOrNull(1) ?: 0x00,
            time[0],

            0x00
        )

        writeToSerialPort(send,delay = 10)

        writeToSerialPort(send2,delay = 10)
    }
}

fun sendFrequency() {
    var arrSend = arrayListOf<Frequence>()
    solenoids.forEachIndexed { index, solenoidHolder ->
        val mkrs = 1_000_000 / solenoidHolder.frequency

        val time = BigInteger.valueOf(mkrs.toLong()).toByteArray()
        arrSend.add(Frequence(units = time[0], dozens = time[1]))
    }


    CoroutineScope(Dispatchers.IO).launch {
        writeToSerialPort(byteArrayOf(
            0x68,
            arrSend[0].units,arrSend[0].dozens,
            arrSend[1].units,arrSend[1].dozens,
            arrSend[2].units,arrSend[2].dozens,
            arrSend[3].units,arrSend[3].dozens,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
        ))
        writeToSerialPort(byteArrayOf(
            0x48,
            arrSend[4].units,arrSend[4].dozens,
            arrSend[5].units,arrSend[5].dozens,
            arrSend[6].units,arrSend[6].dozens,
            arrSend[7].units,arrSend[7].dozens,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
        ))
    }

}

data class Frequence(val units: Byte, val dozens: Byte)