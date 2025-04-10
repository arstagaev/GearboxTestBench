import androidx.compose.runtime.mutableStateOf
import java.io.File
import javax.swing.JFileChooser


object GlobalVariables {
    var file1 = mutableStateOf<File?>(null)
    var file2 = mutableStateOf<File?>(null)


    var DirMCM = File(JFileChooser().fileSystemView.defaultDirectory.toString(),"mcm")//File(System.getProperty("user.home"), "Documents/mcm")

    val Dir1Configs = File(DirMCM,"config")
    val Dir2Reports = File(DirMCM,"reports")
    val Dir3Scenarios = File(DirMCM,"scenarios")

    // samples:
    val FileSampleA= File(Dir2Reports,"analyse_sample1.txt")
    val FileSampleB= File(Dir2Reports,"analyse_sample2.txt")
}