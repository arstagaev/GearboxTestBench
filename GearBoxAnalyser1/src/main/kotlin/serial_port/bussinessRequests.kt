package serial_port

import ui.main_screen.center.support_elements.*
import utils.indexOfScenario

suspend fun reInitSolenoids() {
    comparatorToSolenoid(indexOfScenario.value)

    writeToSerialPort(byteArrayOf(0x71,ch1, 0x00,ch2, 0x00,ch3, 0x00,ch4, 0x00,0x00, 0x00,0x00, 0x00,0x00), delay = 100L)

    writeToSerialPort(byteArrayOf(0x51,ch5, 0x00,ch6, 0x00,ch7, 0x00,ch8, 0x00,0x00, 0x00,0x00, 0x00,0x00),delay = 0L)

}

//suspend fun manageSolenoids(isNextStep: Boolean) {
//    if (isNextStep) {
//
//        indexOfScenario++
//        if (indexOfScenario)
//    } else {
//        indexOfScenario--
//    }
//}