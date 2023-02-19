package net.adamjak.tomas.files_organizer

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.LocalDateTime
import kotlin.test.assertFailsWith
import net.adamjak.tomas.files_organizer.FilesOrganizer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FilesOrganizerTest {

    private lateinit var tempDir1 : Path
    private lateinit var tempDir2 : Path

    @BeforeEach
    fun beforeEach () {
        this.tempDir1 = Files.createTempDirectory("dir1")
        this.tempDir2 = Files.createTempDirectory("dir2")
    }

    @AfterEach
    fun afterEach () {

        deleteDir(this.tempDir1.toFile())
        deleteDir(this.tempDir2.toFile())
    }

    private fun deleteDir(file: File) {
        val contents = file.listFiles()
        if (contents != null) {
            for (f in contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f)
                }
            }
        }
        file.delete()
    }


    @Test
    fun `Test organize folder copy` () {
        val tempFile1 = File.createTempFile("temp", "", this.tempDir1.toFile())
        FilesOrganizer.Builder()
            .inputFolderPath(this.tempDir1.toString())
            .outputFolderPath(this.tempDir2.toString())
            .build()
            .organize()

        Assertions.assertEquals(1,this.tempDir2.toFile().listFiles().size)

        val now: LocalDateTime = LocalDateTime.now()
        val strDate : String = now.year.toString() + "-" + String.format("%02d", now.month.value) + "-" + String.format("%02d", now.dayOfMonth)

        val createdDir = this.tempDir2.toFile().listFiles().first()
        Assertions.assertTrue(createdDir != null)
        Assertions.assertTrue(createdDir.isDirectory)
        Assertions.assertEquals(strDate, createdDir.name)
        Assertions.assertTrue(createdDir.listFiles().any { f -> f.name.equals(tempFile1.name) })
    }

    @Test
    fun `Test organize folder move` () {
        File.createTempFile("temp", "", this.tempDir1.toFile())
        FilesOrganizer.Builder()
            .inputFolderPath(this.tempDir1.toString())
            .outputFolderPath(this.tempDir2.toString())
            .move()
            .build()
            .organize()

        Assertions.assertEquals(0,this.tempDir1.toFile().listFiles().size)
        Assertions.assertEquals(1,this.tempDir2.toFile().listFiles().size)
    }

    @Test
    fun `Test organize folder tree struct` () {
        val tempFile1 = File.createTempFile("temp", "", this.tempDir1.toFile())
        FilesOrganizer.Builder()
            .inputFolderPath(this.tempDir1.toString())
            .outputFolderPath(this.tempDir2.toString())
            .tree()
            .build()
            .organize()

        Assertions.assertEquals(1,this.tempDir2.toFile().listFiles().size)

        val now: LocalDateTime = LocalDateTime.now()

        val yearDir = this.tempDir2.toFile().listFiles().first()
        Assertions.assertTrue(yearDir != null)
        Assertions.assertTrue(yearDir.isDirectory)
        Assertions.assertEquals(now.year.toString(), yearDir.name)

        val monthDir = yearDir.listFiles().first()
        Assertions.assertTrue(monthDir != null)
        Assertions.assertTrue(monthDir.isDirectory)
        Assertions.assertEquals(String.format("%02d",now.month.value), monthDir.name)

        val dayDir = monthDir.listFiles().first()
        Assertions.assertTrue(dayDir != null)
        Assertions.assertTrue(dayDir.isDirectory)
        Assertions.assertEquals(String.format("%02d",now.dayOfMonth), dayDir.name)

        Assertions.assertTrue(dayDir.listFiles().any { f -> f.name.equals(tempFile1.name) })
    }

    // region Builder
    @Test
    fun `Test builder build checks if folders arent null` () {
        assertFailsWith<Exception>(
            message = "Input and output folder path are required",
            block = {
                FilesOrganizer.Builder()
                    .outputFolderPath(this.tempDir2.toString())
                    .build()
            }
        )

        assertFailsWith<Exception>(
            message = "Input and output folder path are required",
            block = {
                FilesOrganizer.Builder()
                    .inputFolderPath(this.tempDir1.toString())
                    .build()
            }
        )
    }

    @Test
    fun `Test builder build checks if folders are dirs` () {
        val tempFile1 = File.createTempFile("temp", "", this.tempDir1.toFile())

        assertFailsWith<Exception>(
            message = "Input folder path is not folder.",
            block = {
                FilesOrganizer.Builder()
                    .inputFolderPath(tempFile1.toString())
                    .outputFolderPath(this.tempDir2.toString())
                    .build()
            }
        )

        assertFailsWith<Exception>(
            message = "Output folder path is not folder.",
            block = {
                FilesOrganizer.Builder()
                    .inputFolderPath(this.tempDir1.toString())
                    .outputFolderPath(tempFile1.toString())
                    .build()
            }
        )
    }

    // endregion

    // region YearMonthDay class
    @Test
    fun `Test YearMonthDay base functions` () {
        val yearMonthDay = FilesOrganizer.YearMonthDay(year = 1992, month = 5, day = 4)
        Assertions.assertEquals("1992-05-04", yearMonthDay.toString())
        Assertions.assertEquals("04", yearMonthDay.getDayString())
        Assertions.assertEquals("05", yearMonthDay.getMonthString())
        Assertions.assertEquals("1992", yearMonthDay.getYearString())

        Assertions.assertEquals(4, yearMonthDay.getDay())
        Assertions.assertEquals(5, yearMonthDay.getMonth())
        Assertions.assertEquals(1992, yearMonthDay.getYear())
    }

    @Test
    fun `Test YearMonthDay creating from fileTime` () {
        val ft : FileTime = FileTime.from(Instant.parse("1992-05-04T10:35:00.00Z"))
        val fromFileTime = FilesOrganizer.YearMonthDay.fromFileTime(ft)
        Assertions.assertEquals("1992-05-04", fromFileTime.toString())
    }
    // endregion
}