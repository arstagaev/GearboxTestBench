import GlobalVariables.Dir1Configs
import GlobalVariables.Dir2Reports
import GlobalVariables.Dir3Scenarios
import GlobalVariables.FileSampleA
import GlobalVariables.FileSampleB
import java.io.File
import kotlin.math.pow
import kotlin.math.round

/**
 * Ensures the file exists.
 *
 * If the file does not exist, the parent directories are created (if necessary)
 * and the file is created with content provided by [defaultContent]. If no content
 * is provided, an empty file is created.
 *
 * @param defaultContent A lambda that returns the default text to write into the file.
 * @return This file.
 */
fun File.ensureExists(defaultContent: () -> String = { "" }): File {
    if (!exists()) {
        parentFile?.mkdirs()
        createNewFile()
        val content = defaultContent()
        if (content.isNotEmpty()) {
            writeText(content)
        }
    }
    return this
}

/**
 * Ensures that this file exists.
 *
 * If the file does not exist, this function will ensure that the parent folder exists
 * (by calling [ensureDirectoryExists]), and then it will copy the content from [template]
 * into this file.
 *
 * @param template The file that contains the default content that should be copied.
 * @return This file (now guaranteed to exist).
 */
fun File.ensureExistsOrCopy(template: File): File {
    if (!exists()) {
        // Ensure the parent directory exists.
        parentFile?.ensureDirectoryExists()
        // Copy the content from template into this file.
        template.copyTo(this, overwrite = true)
    }
    return this
}


/**
 * Ensures that this directory exists.
 *
 * If this file does not exist or is not a directory, it will be created as a directory
 * (including any necessary parent directories).
 *
 * @return This file (now guaranteed to be a directory).
 */
fun File.ensureDirectoryExists(): File {
    if (!exists() || !isDirectory) {
        mkdirs()
    }
    return this
}

fun checkDirs() {
    Dir1Configs.ensureDirectoryExists()
    Dir2Reports.ensureDirectoryExists()
    Dir3Scenarios.ensureDirectoryExists()
}

fun checkSamples() {
    FileSampleA.ensureExistsOrCopy(File("file_samples","analyse_sample1.txt"))
    FileSampleB.ensureExistsOrCopy(File("file_samples","analyse_sample2.txt"))
}
