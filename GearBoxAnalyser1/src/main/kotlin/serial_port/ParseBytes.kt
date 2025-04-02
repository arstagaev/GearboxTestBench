package serial_port

import com.fazecast.jSerialComm.*
import enums.StateExperiments
import kotlinx.coroutines.*
import showMeSnackBar
import storage.createMeasureExperiment
import ui.charts.Pointer
import utils.*


private val DEBUG_PARSING = false



class PacketListener : SerialPortPacketListener {
    override fun getListeningEvents(): Int {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED
    }

    override fun getPacketSize(): Int {
        return 16
    }

    override fun serialEvent(event: SerialPortEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            val newData = event.receivedData
            //println("${newData.toHexString()}")
            coreParse(newData)
        }

    }
}

private var arrCurrRaw  = arrayListOf<ByteArray>()
private var arrPressRaw = arrayListOf<ByteArray>()

private var arrCurr =  arrayListOf<ArrayList<Int>>()
private var arrPress = arrayListOf<ArrayList<Int>>()
private var start_time = 0L
private var incr = 0
var incrX = 0
private var lastGauge : DataChunkG? = null


suspend fun coreParse(updData: ByteArray) = withContext(Dispatchers.IO) {
    var dch: DataChunkG? = null
    var dchCurr: DataChunkCurrent? = null

    if (incr == 0) {
        start_time = System.currentTimeMillis()
    }
    incr++
    val delta = System.currentTimeMillis() - start_time

    if (delta >= 1000) {
        // measure number of packets:
        println("${System.currentTimeMillis()/1000L}> ${updData[0]} ${updData[15]} [size:${updData.size}] ${incr} ]-[ ${delta} ms ** $limitTime >= ${incrementTime} .state${STATE_EXPERIMENT.value.name}")
        incr = 0
    }


    if (incrementTime >= 100_000 && !isExperimentStarts.value) {
        incrementTime = 0
    }

    when {
        updData[0] == 0xFE.toByte() && updData[1] == 0xFF.toByte() &&
        updData[2] == 0xFE.toByte() && updData[3] == 0xFF.toByte() &&
        updData[4] == 0xFE.toByte() && updData[5] == 0xFF.toByte() &&

        updData[6] == 0xFE.toByte() && updData[7] == 0xFF.toByte() &&
        updData[8] == 0xFE.toByte() && updData[9] == 0xFF.toByte() &&
        updData[10] == 0xFE.toByte() && updData[11] == 0xFF.toByte() &&
        updData[12] == 0xFE.toByte() && updData[13] == 0xFF.toByte()  -> {
            STATE_EXPERIMENT.value = StateExperiments.START

            isExperimentStarts.value = true
            logGarbage("Start Experiment! ${isExperimentStarts.value}")

        }
        updData.all { it == 0xFF.toByte() } -> {
            isExperimentStarts.value = false
            STATE_EXPERIMENT.value = StateExperiments.PREP_DATA
            logGarbage("End Experiment! all it == 0xFF ${isExperimentStarts.value} contOfPacks: ${incrX}")
            //incrX = 0
        }

        //pressure
        //STATE_EXPERIMENT.value != StateExperiments.PREP_DATA &&
        updData[1] < 16 && updData[3] < 16 && updData[5] < 16 && updData[7] < 16 -> {
            //println("> ${updData.toHexString()} [size:${updData.size}]")
            if (isExperimentStarts.value) {
                incrX++
            }
            dch = DataChunkG(
                isExperiment = (isExperimentStarts.value),
                onesAndTens(byteToInt(updData[0]).toUInt() , byteToInt(updData[1]).toUInt()),
                onesAndTens(byteToInt(updData[2]).toUInt() , byteToInt(updData[3]).toUInt()),
                onesAndTens(byteToInt(updData[4]).toUInt() , byteToInt(updData[5]).toUInt()),
                onesAndTens(byteToInt(updData[6]).toUInt() , byteToInt(updData[7]).toUInt()),

                onesAndTens(byteToInt( updData[8]).toUInt() , byteToInt(updData[9] ).toUInt()),
                onesAndTens(byteToInt(updData[10]).toUInt(),  byteToInt(updData[11]).toUInt()),
                onesAndTens(byteToInt(updData[12]).toUInt(),  byteToInt(updData[13]).toUInt()),
                onesAndTens(byteToInt(updData[14]).toUInt(),  byteToInt(updData[15]).toUInt())
            )




            //logGarbage(">>> ${dch.toString()}")
            //println("PRES ${dch.toString()}")

            dataChunkGauges.emit(dch)
            lastGauge = dch

            if (DEBUG_PARSING) {
                arrPressRaw.add(updData)

                arrPress.add(arrayListOf(
                    dch.firstGaugeData,
                    dch.secondGaugeData,
                    dch.thirdGaugeData,
                    dch.fourthGaugeData,
                    dch.fifthGaugeData,
                    dch.sixthGaugeData,
                    dch.seventhGaugeData,
                    dch.eighthGaugeData
                ))
            }
        }

        //currency
        //STATE_EXPERIMENT.value != StateExperiments.PREP_DATA &&
        //!isExperimentStarts.value &&
        updData[1] in 16..31 &&
        updData[3] in 16..31 &&
        updData[5] in 16..31 &&
        updData[7] in 16..31 -> {
            if (isExperimentStarts.value) {
                incrX++
            }
            dchCurr = DataChunkCurrent(
                onesAndTens(byteToInt(updData[0]).toUInt() , byteToInt(updData[1]).toUInt()-16u),
                onesAndTens(byteToInt(updData[2]).toUInt() , byteToInt(updData[3]).toUInt()-16u),
                onesAndTens(byteToInt(updData[4]).toUInt() , byteToInt(updData[5]).toUInt()-16u),
                onesAndTens(byteToInt(updData[6]).toUInt() , byteToInt(updData[7]).toUInt()-16u),

                onesAndTens(byteToInt( updData[8]).toUInt() , byteToInt(updData[9] ).toUInt()-16u),
                onesAndTens(byteToInt(updData[10]).toUInt(),  byteToInt(updData[11]).toUInt()-16u),
                onesAndTens(byteToInt(updData[12]).toUInt(),  byteToInt(updData[13]).toUInt()-16u),
                onesAndTens(byteToInt(updData[14]).toUInt(),  byteToInt(updData[15]).toUInt()-16u)
            )
            //println("CURR  ${updData.joinToString()}||${dchCurr.toString()}")
            dataChunkCurrents.emit(dchCurr)
            dataChunkGauges.emit(lastGauge!!)

            if (DEBUG_PARSING) {
                arrCurrRaw.add(updData)

                arrCurr.add(arrayListOf(
                    dchCurr.firstCurrentData,
                    dchCurr.secondCurrentData,
                    dchCurr.thirdCurrentData,
                    dchCurr.fourthCurrentData,
                    dchCurr.fifthCurrentData,
                    dchCurr.sixthCurrentData,
                    dchCurr.seventhCurrentData,
                    dchCurr.eighthCurrentData,
                ))
            }


        }


//        (updData[0] == 0xFF.toByte() &&
//         updData[1] == 0xFF.toByte() &&
//         updData[2] == 0xFF.toByte() &&
//         updData[3] == 0xFF.toByte()
//         ) -> {
//            isExperimentStarts.value = false
//        }
        else -> {
            // if not valid numbers - refresh connection
            if (STATE_EXPERIMENT.value == StateExperiments.NONE) {

                logError("not valid numbers - refresh connection !!!")
                showMeSnackBar("not valid numbers - refresh connection")
                //delay(1000)
                //startReceiveFullData()
            }

        }

    }

    if (DEBUG_PARSING) {
        // print clear results:
        if (arrPressRaw.size > 9) {
            stopSerialCommunication()
            println("_______current:")
            repeat(arrCurrRaw.size) {
                println(arrCurrRaw[it].toHexString())
            }
            println("_______pressure:")
            repeat(arrPressRaw.size) {
                println(arrPressRaw[it].toHexString())
            }
            ///
            println("********************************")

            repeat(arrCurr.size) {
                println(arrCurr[it])
            }
            repeat(arrPress.size) {
                println(arrPress[it])
            }
        }
        arrCurr.clear()
        arrPress.clear()
        arrCurrRaw.clear()
        arrPressRaw.clear()
    }
}
