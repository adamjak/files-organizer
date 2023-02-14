import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.time.Month
import java.time.Year
import java.util.Calendar
import java.util.Date
import kotlin.io.path.Path

class FileOrganiser private constructor(
    private val inputFolder: File,
    private val outputFolder: File,
    val tree: Boolean
) {

    fun organise () {
        val filesToOrganiser: List<File> = this.inputFolder.listFiles().filter { f -> f.isFile }

    }

    private fun getFileCreateTime (file: File) : YearMonthDay {
        return YearMonthDay.fromFileTime(Files.readAttributes(file.toPath(), BasicFileAttributes::class.java).creationTime())
    }

    data class YearMonthDay (val year: Int, val month: Int, val day: Int) {
        companion object {
            fun fromFileTime(fileTime: FileTime): YearMonthDay {
                val calendar = Calendar.getInstance()
                calendar.time = Date.from(fileTime.toInstant())
                return YearMonthDay(
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        }
    }

    // region builder
    data class Builder (
        var inputFolderPath: String?,
        var outputFolderPath: String?,
        var tree : Boolean = false
    ) {

        fun inputFolderPath(inputFolderPath: String) = apply { this.inputFolderPath = inputFolderPath }
        fun outputFolderPath(outputFolderPath: String) = apply { this.outputFolderPath = outputFolderPath }
        fun tree(tree: Boolean) = apply { this.tree = tree }
        fun build() : FileOrganiser {
            if (this.inputFolderPath == null || this.outputFolderPath == null) throw Exception("Input and output folder path are required")
            if (!Files.isDirectory(Path(inputFolderPath!!))) throw Exception("Input folder path is not folder.")
            if (!Files.isDirectory(Path(outputFolderPath!!))) throw Exception("Output folder path is not folder.")

            val inputFolder = File(inputFolderPath)
            val outputFolder = File(outputFolderPath)

            if (!inputFolder.canRead()) throw Exception("Input folder is not readable.")
            if (!inputFolder.canWrite()) throw Exception("Output folder is not writable.")

            return FileOrganiser(inputFolder, outputFolder, tree)
        }
    }
    // endregion
}