package parsing_excel

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import parsing_excel.models.GaugeParameter
import parsing_excel.models.PressuresHolder
import parsing_excel.models.ScenarioStep
import parsing_excel.models.SolenoidHolder
import storage.refreshParameters
import utils.*
import java.io.File
import java.io.FileInputStream

var wholeSheet = mutableListOf<MutableList<String>>()
suspend fun targetParseScenario(inputScenario: File?) : Boolean {
//https://devmark.ru/article/excel-reading
    val NUMBER_OF_PORTS = 16
    if (inputScenario == null) {
        logAct("Error: inputScenario xls == null")
        return false
    }
    logAct("Start parsing ${inputScenario.absolutePath}")

    var needReWriteStandard = false

    val file = FileInputStream(inputScenario)
    //creating workbook instance that refers to .xls file
    val wb = HSSFWorkbook(file)
    //creating a Sheet object to retrieve the object
    val sheet = wb.getSheetAt(0)
    //evaluating cell type
    val formulaEvaluator: FormulaEvaluator = wb.creationHelper.createFormulaEvaluator()

    //Iterate through each row's one by one
    val rowIterator: Iterator<Row> = sheet.iterator()
    var incr = 0

    while (rowIterator.hasNext()) {
        var rowComplete = mutableListOf<String>()

        // to bottom V
        val row: Row = rowIterator.next()
        val cellIterator: Iterator<Cell> = row.cellIterator()

        while (cellIterator.hasNext()) {
            // to right ->
            val cell = cellIterator.next()
            if (cell.toString().isNotBlank() && cell.toString().isNotEmpty() && cell.toString() != "") {
                //print("[${cell.toString()}]")
                rowComplete.add(cell.toString())
            }

//            when (cell.cellType) {
//                CellType.NUMERIC -> print(cell.numericCellValue.toString() + "t")
//                CellType.STRING -> print(cell.stringCellValue + "t")
//            }
        }
        println("${incr}Row: ${rowComplete.joinToString()} | size: ${rowComplete.size}")
        incr++
        if (rowComplete.isNotEmpty()) {
            wholeSheet.add(rowComplete)
        }
    }
    println("Size sheet rows:${wholeSheet[2].size} last cell:  ${wholeSheet[wholeSheet.lastIndex][wholeSheet[wholeSheet.lastIndex].lastIndex].toString()}")
    println("last row: ${wholeSheet[wholeSheet.lastIndex]}  number of rows:${wholeSheet.size}")
    println("last column: ${wholeSheet[32]}")
    // row ; column
    // check valid of xls:
    if ( wholeSheet.size < 22 || wholeSheet[2].size < 2)
        return false

    // clear all in iteration:
    solenoids.clear()
    pressures.clear()
    scenario.clear()

    // Fill file address to Standard chart, Эталон . txt
    val standard = File(Dir7ReportsStandard, wholeSheet[0][0])
    if ( !standard.name.endsWith("txt") ) {
        needReWriteStandard = true
    } else {
        chartFileStandard.value = File(Dir7ReportsStandard, standard.name)
    }


    repeat(NUMBER_OF_PORTS) {
//        var asd = arrayListOf<String>(
//            wholeSheet[2][it+1],
//            wholeSheet[3][it+1],
//            wholeSheet[4][it+1],
//            wholeSheet[5][it+1],
//            wholeSheet[6][it+1],
//            wholeSheet[7][it+1],
//            wholeSheet[8][it+1],
//            wholeSheet[9][it+1]
//        )
//        println("cooopppppyyy ${it} ${asd.joinToString()} ${pressures.size}")
        //incr++
        //println("pizdec:  ${wholeSheet[11].getOrNull(it+1)?.toFloat()?.toInt() ?: 100}")
        pressures.add(
            PressuresHolder(
                displayName =  wholeSheet[2][it+1],
                index =        wholeSheet[3][it+1].toFloat().toInt(),
                minValue =     wholeSheet[4][it+1].toFloat().toInt(),
                maxValue =     wholeSheet[5][it+1].toFloat().toInt(),
                tolerance =    wholeSheet[6][it+1].toFloat().toInt(),
                unit =         wholeSheet[7][it+1],
                commentString =wholeSheet[8][it+1],
                prefferedColor=wholeSheet[9][it+1],
                isVisible =    wholeSheet[10].getOrNull(it+1) == "true",
                //parameters = GaugeParameter(size = wholeSheet[11].getOrNull(it+1)?.toFloat()?.toInt() ?: 100)
            )
        )
    }
    logInfo("pressures: ${pressures.joinToString()} ]")
    scaleGauges.value = wholeSheet[0][2].toString().toIntOrNull()?.let { it / 100f } ?: 1f
    var maxPWMs = arrayListOf<Int>()

    repeat(NUMBER_OF_PORTS) {
//        var asd = arrayListOf<String>(
//            wholeSheet[14][it+1],
//            wholeSheet[15][it+1],
//            wholeSheet[16][it+1],
//            wholeSheet[17][it+1],
//            wholeSheet[18][it+1],
//            wholeSheet[19][it+1],
//            wholeSheet[20][it+1],
//            wholeSheet[21][it+1]
//        )
//        println( pizdec [${it}] ${asd.joinToString()} ${pressures.size}")

        solenoids.add(
            SolenoidHolder(
                displayName =       wholeSheet[14][it+1],
                index =             wholeSheet[15][it+1].toDouble().toInt(),
                maxPWM =            wholeSheet[16][it+1].toDouble().toInt(),
                step =              wholeSheet[17][it+1].toDouble().toInt(),
                preferredColor =    wholeSheet[18][it+1],
                frequency =         wholeSheet[19][it+1].toDouble().toInt(),
                expectedTestValue = wholeSheet[20][it+1].toDouble().toInt(),
                currentMaxValue =   wholeSheet[21][it+1].toDouble().toInt(),
                isVisible =         wholeSheet[22].getOrNull(it+1) == "true"
            )
        )
        maxPWMs.add(wholeSheet[16][it+1].toDouble().toInt())


    }
    logInfo("solenoids: ${solenoids.joinToString()} ]")
    limitTime = 0

    // 27 by vertical:
    for ( i in 27 until wholeSheet.size) {
        var valueSteps = arrayListOf<Int>()

        repeat(NUMBER_OF_PORTS) {
            var newPWM = wholeSheet[i][it+1].toDouble().toInt()

            // check limits maxPWM, for not burn out
            if (newPWM > maxPWMs[it]) {
                newPWM = maxPWMs[it]
            }else if (newPWM < 0) {
                newPWM = 0
            }

            valueSteps.add(newPWM)

//            selectorForChannels(it+1,wholeSheet[i][it+1].toDouble().toInt().toByte())
//            when(it) {
//                0 -> pwm1SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                1 -> pwm2SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                2 -> pwm3SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                3 -> pwm4SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                4 -> pwm5SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                5 -> pwm6SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                6 -> pwm7SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//                7 -> pwm8SeekBar.value = wholeSheet[i][it+1].toDouble().toInt()
//
//                else -> pwm1SeekBar
//            }

        }

        println("<>>> ${valueSteps.joinToString()}")

        val newTime = wholeSheet[i][0].toDouble().toInt()

        limitTime += newTime

        scenario.add(
            ScenarioStep(
                time = newTime,
                chs = valueSteps,
                text = wholeSheet[i][17],
                comment = if (wholeSheet[i].size != 19) "" else wholeSheet[i][18]
            )
        )
    }



    println("scenario steps: ${scenario.joinToString()}")

    file.close()
    wholeSheet.clear()
    refreshParameters()
    if (needReWriteStandard) {
        writeToExcel(0,0, chartFileStandard.value.name)
    }
    return true
}