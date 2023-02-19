package net.adamjak.tomas.files_organizer

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.logging.Logger
import kotlin.io.path.Path
import kotlin.io.path.notExists
import kotlin.io.path.pathString


class FilesOrganizer private constructor(
    private val inputFolder: File,
    private val outputFolder: File,
    private val folderStruct: FolderStruct,
    private val move: Boolean,
    private val replace: Boolean,
    private val granularity: Granularity,
) {
    companion object {
        val log: Logger = Logger.getGlobal()
    }

    fun organize() {
        log.info("Start organize")
        val filesToOrganiser: List<File> = this.inputFolder.listFiles().filter { f -> f.isFile }
        log.info("Organize ${filesToOrganiser.size} files")
        filesToOrganiser.forEach {
            this.copyFileToDateFolder(it)
        }
    }

    private fun copyFileToDateFolder(file: File) {
        val folderDesc = this.getFileCreateTime(file)

        val finalDestination = if (this.folderStruct == FolderStruct.TREE) {
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
            log.severe("File ${file.name} can not organize because in destination $finalDestination exist file with same name. Use -r modifier to replace it.")
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
                val localDate = LocalDate.ofInstant(fileTime.toInstant(), ZoneId.systemDefault())
                return YearMonthDay(
                    year = localDate.year,
                    month = localDate.monthValue,
                    day = localDate.dayOfMonth
                )
            }
        }
    }

    // region builder
    class Builder {

        private lateinit var inputFolderPath: String
        private lateinit var outputFolderPath: String
        private var tree: Boolean = false
        private var move: Boolean = false
        private var replace: Boolean = false
        private var granularity: Granularity = Granularity.YEAR_MONTH_DAY

        fun inputFolderPath(inputFolderPath: String) = apply { this.inputFolderPath = inputFolderPath }
        fun outputFolderPath(outputFolderPath: String) = apply { this.outputFolderPath = outputFolderPath }
        fun tree() = apply { this.tree = true}
        fun tree(tree: Boolean) = apply { this.tree = tree }
        fun move() = apply { this.move = true }
        fun move(move: Boolean) = apply { this.move = move }
        fun replace() = apply { this.replace = true }
        fun replace(replace: Boolean) = apply { this.replace = replace }
        fun granularity(granularity: Granularity) = apply { this.granularity = granularity }
        fun granularity(granularity: String?) = apply {
            if (granularity != null) this.granularity = Granularity.valueOf(granularity)
        }
        fun build(): FilesOrganizer {
            if (this.inputFolderPath == null || this.outputFolderPath == null) throw Exception("Input and output folder path are required")
            if (!Files.isDirectory(Path(inputFolderPath))) throw Exception("Input folder path is not folder.")
            if (!Files.isDirectory(Path(outputFolderPath))) {
                if (Files.exists(Path(outputFolderPath))) {
                    throw Exception("Output folder path is not folder.")
                }
                Files.createDirectories(Path(outputFolderPath))
            }

            val inputFolder = File(inputFolderPath)
            val outputFolder = File(outputFolderPath)

            if (!inputFolder.canRead()) throw Exception("Input folder is not readable.")
            if (!outputFolder.canWrite()) throw Exception("Output folder is not writable.")

            return FilesOrganizer(
                inputFolder = inputFolder,
                outputFolder = outputFolder,
                folderStruct = if (tree) FolderStruct.TREE else FolderStruct.SINGLE,
                move = move,
                replace = replace,
                granularity = granularity)
        }
    }
    // endregion
}