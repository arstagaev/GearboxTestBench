package storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import showMeSnackBar
import storage.models.ParameterCommon
import utils.*
import java.io.*

fun checkNeededFolders(): Boolean {
        return false
}

fun createNeededFolders() {


  var dirs = arrayOf<File>()
  dirs+= Dir1Configs
  dirs+= Dir2Reports
  dirs+= Dir3Scenarios

  dirs.forEach {
          if (!it.exists()) {
                  it.mkdirs()
                  logAct("Folder created: ${it.absoluteFile}")
          }
  }
}

fun createDemoConfigExcelFile() {

   logAct("createDemoConfigFile")
   val theFileXls = File(Dir3Scenarios,"scenario_demo_test.xls")


   if (!theFileXls.exists()) {

       File("scenario_demo_test.xls").copyTo(theFileXls)

       logAct("Excel file-config created: ${theFileXls.absoluteFile}")
   }
}

fun readParameters(file: File) : List<ParameterCommon> {
    logAct("readParameters")
    if (!file.exists()) {
        refreshParameters()
    }

    //val PCListSerializer: KSerializer<List<ParameterCommonJson>> = ListSerializer(ParameterCommonJson.serializer())
//
    //val obj = Json.decodeFromString(PCListSerializer, file.readText(Charsets.UTF_8))

    //Json.decodeFromString<ArrayList<ParameterCommon>>()
    var listParams = mutableListOf<ParameterCommon>()
    try {
        val br = BufferedReader(FileReader(file))
        var line: String?
        var countOfLine = 0
        while (br.readLine().also { line = it } != null) {
            if(line != ""|| line != " "){
                val items = line?.split("=")?.toTypedArray()
                if (items != null ) {
                    when(items[0]) {
                        "comport" -> {
                            listParams.add(ParameterCommon(name = "comport", value = items[1]))
                        }
                        "baudrate" -> {
                            listParams.add(ParameterCommon(name = "baudrate", value = items[1]))
                        }
                        "last_operator_id" -> {
                            listParams.add(ParameterCommon(name = "last_operator_id", value = items[1]))
                        }
                        "sound_enabled" -> {
                            listParams.add(ParameterCommon(name = "sound_enabled", value = items[1]))
                        }
                        "last_scenario" -> {
                            listParams.add(ParameterCommon(name = "last_scenario", value = items[1]))
                        }
                        "delay_before_chart" -> {
                            listParams.add(ParameterCommon(name = "delay_before_chart", value = items[1]))
                        }
                        "save_log" -> {
                            listParams.add(ParameterCommon(name = "save_log", value = items[1]))
                        }
                    }
                }
            }
            countOfLine++
        }
        br.close()
    } catch (e: Exception) {
        logError("error +${e.message}")
    }


    return listParams
}

fun refreshParameters() {
    logAct("refresh Parameters")
    //"comport" -> COM_P
    //"baudrate" -> BAUD
    ////"is_demo" ->
    //"last_operator_id"
//    var newParameters = arrayListOf<ParameterCommonJson>(
//        ParameterCommonJson("comport","COM10"),
//        ParameterCommonJson("baudrate","COM10"),
//        ParameterCommonJson("last_operator_id","Жималбек Аббас Гамлядиндов Оглы"),
//    )

    var newParameters = arrayListOf(
        ParameterCommon("comport","${COM_PORT}"),
        ParameterCommon("baudrate","${BAUD_RATE}"),
        ParameterCommon("last_operator_id","${OPERATOR_ID}"),
        ParameterCommon("sound_enabled","${SOUND_ENABLED}"),
        ParameterCommon("last_scenario","${LAST_SCENARIO}"),
        ParameterCommon("delay_before_chart","${DELAY_BEFORE_CHART}"),
        ParameterCommon("save_log","${SAVELOG}"),
    )

    //IF first launch
    val fl = Dir4MainConfig_Txt
    if (!fl.exists()) {
        println("Try to create new file: ${fl.absolutePath}")
        fl.createNewFile()
//        newParameters = arrayListOf(
//            ParameterCommon("comport","COM10"),
//            ParameterCommon("baudrate","500000"),
//            ParameterCommon("last_operator_id","Гаджилы Жималбек Али оглы"),
//            ParameterCommon("sound_enabled","1"),
//            ParameterCommon("last_scenario","${Dir9Scenario.absolutePath}"),
//        )
    }

    val bw = fl.bufferedWriter()

    try {
        // read lines in txt by Bufferreader
        repeat(newParameters.size) {
            bw.write("${newParameters[it].name}=${newParameters[it].value}\n")
        }
        bw.close()
    }catch (e: Exception){
        showMeSnackBar("Error! ${e.message}")
    }

//    val PCListSerializer: KSerializer<List<ParameterCommonJson>> = ListSerializer(ParameterCommonJson.serializer())
//
//    // json:
//    val json = Json.encodeToString(PCListSerializer, newParameters)
//
//    var newFileJson = Dir4MainConfig_Json
//    newFileJson.writeText(json)
//
//    if (!newFileJson.exists()) {
//        newFileJson.createNewFile()
//    }

}

fun loadOperators() : MutableList<String> {
    return Dir5Operators.readLines().toMutableList().asReversed()
}

fun createMeasureExperiment() {
    logGarbage("createMeasureExperiment() ${arr1Measure.size}")
    if (arr1Measure.isEmpty())
        return

    val fl = File(Dir2Reports, generateTimestampLastUpdate()+"_${OPERATOR_ID}"+"_chart.txt")
    CoroutineScope(Dispatchers.Default).launch {
        //logInfo("createMeasureExperiment ${arr8Measure.joinToString()}")


        fl.createNewFile()
        val bw = fl.bufferedWriter()
        try {
            // read lines in txt by Bufferreader
            bw.write("#standard#${chartFileStandard.value.name}\n")
            bw.write(
                "#visibility#${pressures[0].isVisible.toBin()}#${pressures[1].isVisible.toBin()}#${pressures[2].isVisible.toBin()}#${pressures[3].isVisible.toBin()}"+
                    "#${pressures[4].isVisible.toBin()}#${pressures[5].isVisible.toBin()}#${pressures[6].isVisible.toBin()}#${pressures[7].isVisible.toBin()}\n"
            )
            bw.write("#name#${pressures[0].displayName}#\n")
            repeat(arr1Measure.size-1) {
                val newStroke =
                    "${arr0Time[it]};${arr1Measure[it]}|"+
                    "${arr2Measure[it]}|"+
                    "${arr3Measure[it]}|"+
                    "${arr4Measure[it]}|"+
                    "${arr5Measure[it]}|"+
                    "${arr6Measure[it]}|"+
                    "${arr7Measure[it]}|"+
                    "${arr8Measure[it]}"
                logInfo("newStroke= ${newStroke}")
                bw.write("${newStroke}\n")
            }
            bw.close()

            arr0Time.clear()
            arr1Measure.clear()
            arr2Measure.clear()
            arr3Measure.clear()
            arr4Measure.clear()
            arr5Measure.clear()
            arr6Measure.clear()
            arr7Measure.clear()
            arr8Measure.clear()

        }catch (e: Exception){
            showMeSnackBar("Error! ${e.message}")
        }
    }
    chartFileAfterExperiment.value = fl
    doOpen_First_ChartWindow.value = true
}

fun readMeasuredExperiment(file: File) {
    try {
        val br = BufferedReader(FileReader(file))
        var line: String?
        var countOfLine = 0
        while (br.readLine().also { line = it } != null) {
            if(line != ""|| line != " "){
                val items = line?.split(";","|")?.toTypedArray()
                if (items != null ) {

                }
            }
            countOfLine++
        }
        br.close()
    } catch (e: Exception) {
        logError("error +${e.message}")
    }
}


fun writeToFile(msg: String, fl: File) {

    //IF first launch
    //val fl = Dir4MainConfig_Log
    if (!fl.exists()) {
        println("Try to create new file: ${fl.absolutePath}")
        fl.createNewFile()
    }
    val fileOutputStream = FileOutputStream(fl,true)
    val outputStreamWriter = OutputStreamWriter(fileOutputStream)

    try {



        outputStreamWriter.append(msg+"\n")


    }catch (e:Exception) {
        logError("ERROR ${e.message}")
    } finally {
        outputStreamWriter.close()
        fileOutputStream.close()

    }

//    val bw = fl.bufferedWriter()
//
//    try {
//        bw.append(msg)
//        bw.close()
//    }catch (e: Exception){
//        showMeSnackBar("Error! ${e.message}")
//    }

}
