package parsing_excel

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import parsing_excel.models.PressuresHolder
import parsing_excel.models.ScenarioStep
import parsing_excel.models.SolenoidHolder
import utils.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

var wholeSheet = mutableListOf<MutableList<String>>()
suspend fun targetParseScenario(inputScenario: File?) : Boolean {

    if (inputScenario == null)
        return false
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
        // to bottom V
        val row: Row = rowIterator.next()

        var rowComplete = mutableListOf<String>()
        val cellIterator: Iterator<Cell> = row.cellIterator()

        while (cellIterator.hasNext()) {
            // to right ->
            val cell = cellIterator.next()
            if (cell.toString().isNotBlank() && cell.toString().isNotEmpty() && cell.toString() != "") {
                rowComplete.add(cell.toString())
            }

//            when (cell.cellType) {
//                CellType.NUMERIC -> print(cell.numericCellValue.toString() + "t")
//                CellType.STRING -> print(cell.stringCellValue + "t")
//            }
        }
        println("${incr}Row: ${rowComplete.joinToString()} ${rowComplete.size}")
        incr++
        if (rowComplete.isNotEmpty()) {
            wholeSheet.add(rowComplete)
        }

    }
    println("Size sheet rows:${wholeSheet[2].size} in rows column:${wholeSheet[2][2].length}")

    // check valid of xls:
    if ( wholeSheet.size < 22 || wholeSheet[2].size < 2)
        return false

    // clear all in iteration:
    solenoids.clear()
    pressures.clear()
    scenario.clear()

    // Fill file address to Standard
    val standard = File(Dir7ReportsStandard, wholeSheet[0][0])
    if ( !standard.name.endsWith("txt") ) {
        needReWriteStandard = true
    } else {
        chartFileStandard.value = File(Dir7ReportsStandard, standard.name)
    }


    repeat(8) {
        var asd = arrayListOf<String>(
            wholeSheet[2][it+1],
            wholeSheet[3][it+1],
            wholeSheet[4][it+1],
            wholeSheet[5][it+1],
            wholeSheet[6][it+1],
            wholeSheet[7][it+1],
            wholeSheet[8][it+1],
            wholeSheet[9][it+1]
        )
        println("cooopppppyyy ${it} ${asd.joinToString()} ${pressures.size}")
        //incr++
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
                isVisible =   wholeSheet[10].getOrNull(it+1) == "true"
            )
        )
        logInfo("pressures: ${pressures.joinToString()} ]")

    }

    var maxPWMs = arrayListOf<Int>()

    repeat(8) {
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



    limitTime = 0

    for ( i in (27) until wholeSheet.size) {
        var valueSteps = arrayListOf<Int>()

        repeat(8) {
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
                text = wholeSheet[i][9],
                comment = if (wholeSheet[i].size != 11) "" else wholeSheet[i][10]
            )
        )
    }



    println("scenario steps: ${scenario.joinToString()}")

    file.close()
    wholeSheet.clear()
    if (needReWriteStandard) {
        writeToExcel(0,0, chartFileStandard.value.name)
    }
    return true
}


fun createDemoScenarioXLS(filePath: String) {
    val workbook = HSSFWorkbook()
    val sheet = workbook.createSheet("ScenarioDemo")

    // Header
    sheet.createRow(0).createCell(0).setCellValue("StandardFile.txt")

    // Pressures (Rows 2-10)
    val pressureHeaders = listOf("Name", "Index", "MinValue", "MaxValue", "Tolerance", "Unit", "Comment", "Color", "Visibility")
    val pressureData = listOf(
        listOf("Pressure1", 1, 10, 100, 5, "bar", "Pressure 1", "#FF0000", true),
        listOf("Pressure2", 2, 20, 200, 10, "psi", "Pressure 2", "#00FF00", true),
        listOf("Pressure3", 3, 30, 300, 15, "kPa", "Pressure 3", "#0000FF", false),
    )

    for ((index, header) in pressureHeaders.withIndex()) {
        val row = sheet.createRow(index + 2)
        row.createCell(0).setCellValue(header)
        pressureData.forEachIndexed { colIndex, data ->
            row.createCell(colIndex + 1).apply {
                when (val value = data[index]) {
                    is Int -> setCellValue(value.toDouble())
                    is Boolean -> setCellValue(value.toString())
                    else -> setCellValue(value as String)
                }
            }
        }
    }

    // Solenoids (Rows 14-22)
    val solenoidHeaders = listOf("Name", "Index", "MaxPWM", "Step", "Color", "Frequency", "ExpectedTestValue", "CurrentMaxValue", "Visibility")
    val solenoidData = listOf(
        listOf("Solenoid1", 1, 255, 5, "#AAAAAA", 50, 128, 200, true),
        listOf("Solenoid2", 2, 200, 10, "#BBBBBB", 60, 100, 150, false),
        listOf("Solenoid3", 3, 150, 15, "#CCCCCC", 70, 75, 100, true),
    )

    for ((index, header) in solenoidHeaders.withIndex()) {
        val row = sheet.createRow(index + 14)
        row.createCell(0).setCellValue(header)
        solenoidData.forEachIndexed { colIndex, data ->
            row.createCell(colIndex + 1).apply {
                when (val value = data[index]) {
                    is Int -> setCellValue(value.toDouble())
                    is Boolean -> setCellValue(value.toString())
                    else -> setCellValue(value as String)
                }
            }
        }
    }

    // Scenario steps (from row 27 onwards)
    val scenarioSteps = listOf(
        listOf(1000, 10, 20, 30, 40, 50, 60, 70, 80, "Step 1", "Initial setup"),
        listOf(2000, 15, 25, 35, 45, 55, 65, 75, 85, "Step 2", "Middle step"),
        listOf(1500, 20, 30, 40, 50, 60, 70, 80, 90, "Step 3", "Final step"),
    )

    scenarioSteps.forEachIndexed { index, stepData ->
        val row = sheet.createRow(index + 27)
        stepData.forEachIndexed { colIndex, data ->
            row.createCell(colIndex).apply {
                when (data) {
                    is Int -> setCellValue(data.toDouble())
                    else -> setCellValue(data as String)
                }
            }
        }
    }

    // Write to file
    FileOutputStream(File(filePath)).use { outputStream ->
        workbook.write(outputStream)
        workbook.close()
    }
}
