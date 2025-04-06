package utils

import storage.writeToFile

fun logAct(msg: String)     = logAgregator("a~>"+msg)
fun logInfo(msg: String)    = println("i~>"+msg)
fun logError(msg: String)  {
    repeat(5) {
        println("e~>"+msg)
    }
}

fun logGarbage(msg: String) = println("g~>"+msg)


fun logAgregator(msg: String) {
    println(msg)
    if (SAVELOG) {
        writeToFile(msg, Dir4MainConfig_Log)
    }
}
