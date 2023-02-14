import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.util.Calendar
import java.util.Date
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.io.path.Path
import kotlin.io.path.notExists
import kotlin.io.path.pathString


class FileOrganiser private constructor(
    private val inputFolder: File,
    private val outputFolder: File,
    private val tree: Boolean,
    private val move: Boolean,
    private val replace: Boolean,
) {
    companion object {
        val log: Logger = Logger.getGlobal()
    }

    fun organise() {
        log.info("Start organise")
        val filesToOrganiser: List<File> = this.inputFolder.listFiles().filter { f -> f.isFile }
        log.info("Organise ${filesToOrganiser.size} files")
        filesToOrganiser.forEach {
            this.copyFileToDateFolder(it)
        }
    }

    private fun copyFileToDateFolder(file: File) {
        val folderDesc = this.getFileCreateTime(file)

        val finalDestination = if (this.tree) {
            Paths.get(
                this.outputFolder.path,
                folderDesc.getYearString(),
                folderDesc.getMonthString(),
                folderDesc.getDayString()
            )
        } else {
            Paths.get(this.outputFolder.path, folderDesc.toString())
        }

        if (finalDestination.notExists()) {
            Files.createDirectories(finalDestination)
        }

        try {
            if (this.move) {
                log.info("Move ${file.path} to ${finalDestination.pathString}")
                if (this.replace) {
                    Files.move(file.toPath(), Paths.get(finalDestination.toString(), file.name), StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Files.move(file.toPath(), Paths.get(finalDestination.toString(), file.name))
                }
            } else {
                log.info("Copy ${file.path} to ${finalDestination.pathString}")
                if (this.replace) {
                    Files.copy(file.toPath(), Paths.get(finalDestination.toString(), file.name), StandardCopyOption.REPLACE_EXISTING)
                } else {
                    Files.copy(file.toPath(), Paths.get(finalDestination.toString(), file.name))
                }
            }
        } catch (e : java.nio.file.FileAlreadyExistsException) {
            log.severe("File ${file.name} can not organise because in destination $finalDestination exist file with same name. Use -r modifier to replace it.")
        }
    }

    private fun getFileCreateTime(file: File): YearMonthDay {
        return YearMonthDay.fromFileTime(
            Files.readAttributes(file.toPath(), BasicFileAttributes::class.java).creationTime()
        )
    }

    data class YearMonthDay(private val year: Int, private val month: Int, private val day: Int) {
        fun getYear(): Int {
            return this.year
        }

        fun getYearString(): String {
            return this.year.toString()
        }

        fun getMonth(): Int {
            return this.month
        }

        fun getMonthString(): String {
            return String.format("%02d", this.month)
        }

        fun getDay(): Int {
            return this.day
        }

        fun getDayString(): String {
            return String.format("%02d", this.day)
        }

        override fun toString(): String {
            return this.year.toString() + "-" + String.format("%02d", this.month) + "-" + String.format(
                "%02d",
                this.day
            )
        }

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
    data class Builder(
        var inputFolderPath: String?,
        var outputFolderPath: String?,
        var tree: Boolean = false,
        var move: Boolean = false,
        var replace: Boolean = false
    ) {

        fun inputFolderPath(inputFolderPath: String) = apply { this.inputFolderPath = inputFolderPath }
        fun outputFolderPath(outputFolderPath: String) = apply { this.outputFolderPath = outputFolderPath }
        fun tree(tree: Boolean) = apply { this.tree = tree }
        fun move(move: Boolean) = apply { this.move = move }
        fun replace(replace: Boolean) = apply { this.replace = replace }
        fun build(): FileOrganiser {
            if (this.inputFolderPath == null || this.outputFolderPath == null) throw Exception("Input and output folder path are required")
            if (!Files.isDirectory(Path(inputFolderPath!!))) throw Exception("Input folder path is not folder.")
            if (!Files.isDirectory(Path(outputFolderPath!!))) {
                if (Files.exists(Path(outputFolderPath!!))) {
                    throw Exception("Output folder path is not folder.")
                }
                Files.createDirectories(Path(outputFolderPath!!))
            }

            val inputFolder = File(inputFolderPath)
            val outputFolder = File(outputFolderPath)

            if (!inputFolder.canRead()) throw Exception("Input folder is not readable.")
            if (!inputFolder.canWrite()) throw Exception("Output folder is not writable.")

            return FileOrganiser(inputFolder, outputFolder, tree, move, replace)
        }
    }
    // endregion
}