import GlobalVariables.file1
import GlobalVariables.file2
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    MaterialTheme {
        // File chooser UI for selecting files.
        var file1Internal by remember { file1 }
        var file2Internal by remember { file2 }
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (file1Internal == null || file2Internal == null) {
                Button(onClick = { file1.value = chooseFile() }) {
                    Text(if (file1Internal == null) "Choose File 1" else file1Internal?.name ?: "no name")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { file2.value = chooseFile() }) {
                    Text(if (file2Internal == null) "Choose File 2" else file2Internal?.name  ?: "no name")
                }
            } else {
                JFreeChartDemoWithMultipleFiles()
            }
        }
    }
}

fun main() = application {
    checkDirs()
    checkSamples()

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
