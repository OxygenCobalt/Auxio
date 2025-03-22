/*
 * Copyright (c) 2024 Auxio Project
 * CoverStorageTest.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.musikr.covers.stored

import android.os.ParcelFileDescriptor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class CoverStorageTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var storageDir: File
    private lateinit var coverStorage: CoverStorage

    @Before
    fun setup() = runTest {
        storageDir = tempFolder.newFolder("covers")
        coverStorage = CoverStorage.at(storageDir)
    }

    @Test(expected = IllegalStateException::class)
    fun at_nonExistentDirectory_throwsException() = runTest {
        // Arrange
        val nonExistentDir = File(tempFolder.root, "nonexistent")

        // Act - should throw
        CoverStorage.at(nonExistentDir)
    }

    @Test(expected = IllegalStateException::class)
    fun at_fileNotDirectory_throwsException() = runTest {
        // Arrange
        val file = tempFolder.newFile("not-a-directory")

        // Act - should throw
        CoverStorage.at(file)
    }

    @Test
    fun find_existingCover_returnsCorrectCover() = runTest {
        // Arrange
        val coverName = "test-cover.jpg"
        val coverFile = File(storageDir, coverName)
        val testData = byteArrayOf(1, 2, 3, 4, 5)
        
        coverFile.writeBytes(testData)

        // Act
        val cover = coverStorage.find(coverName)

        // Assert
        assertNotNull(cover)
        assertEquals(coverName, cover?.id)
        
        // Verify the cover can be opened
        val inputStream = cover?.open()
        assertNotNull(inputStream)
        
        val bytes = inputStream?.readBytes()
        assertEquals(testData.size, bytes?.size)
        assertTrue(testData.contentEquals(bytes!!))
    }

    @Test
    fun find_nonExistentCover_returnsNull() = runTest {
        // Act
        val cover = coverStorage.find("nonexistent.jpg")

        // Assert
        assertNull(cover)
    }

    @Test
    fun find_ioExceptionDuringAccess_returnsNull() = runTest {
        // Arrange
        val coverName = "error-cover.jpg"
        val mockFile = mockk<File>()
        val storageDir = spyk(tempFolder.newFolder("error-covers"))
        
        // Setup a mock File that throws IOException when accessed
        every { storageDir.listFiles() } returns arrayOf(mockFile)
        every { mockFile.name } returns coverName
        every { mockFile.exists() } returns true
        every { mockFile.inputStream() } throws IOException("Test exception")
        
        val errorCoverStorage = CoverStorage.at(storageDir)

        // Act
        val cover = errorCoverStorage.find(coverName)

        // Assert
        assertNull(cover)
    }

    @Test
    fun write_newCover_createsFileAndReturnsCover() = runTest {
        // Arrange
        val coverName = "new-cover.jpg"
        val testData = byteArrayOf(10, 20, 30, 40, 50)

        // Act
        val cover = coverStorage.write(coverName) { outputStream ->
            outputStream.write(testData)
        }

        // Assert
        assertNotNull(cover)
        assertEquals(coverName, cover.id)
        
        // Verify the file was created correctly
        val file = File(storageDir, coverName)
        assertTrue(file.exists())
        
        val fileData = file.readBytes()
        assertEquals(testData.size, fileData.size)
        assertTrue(testData.contentEquals(fileData))
    }

    @Test
    fun write_existingCover_returnsExistingCover() = runTest {
        // Arrange
        val coverName = "existing-cover.jpg"
        val existingData = byteArrayOf(1, 2, 3, 4, 5)
        
        // Create existing file
        val existingFile = File(storageDir, coverName)
        existingFile.writeBytes(existingData)

        // Act - trying to write new data should not overwrite
        val cover = coverStorage.write(coverName) { outputStream ->
            outputStream.write(byteArrayOf(99, 98, 97))
        }

        // Assert
        assertNotNull(cover)
        assertEquals(coverName, cover.id)
        
        // Verify the file still has the original data
        val fileData = existingFile.readBytes()
        assertEquals(existingData.size, fileData.size)
        assertTrue(existingData.contentEquals(fileData))
    }

    @Test(expected = IOException::class)
    fun write_ioExceptionDuringWrite_propagatesException() = runTest {
        // Arrange
        val coverName = "error-write.jpg"
        
        // Act - should throw
        coverStorage.write(coverName) { outputStream ->
            throw IOException("Test write exception")
        }
    }

    @Test
    fun write_ioExceptionDuringWrite_cleanupsTempFile() = runTest {
        // Arrange
        val coverName = "error-cleanup.jpg"
        val tmpFileName = "$coverName.tmp"
        
        try {
            // Act - will throw
            coverStorage.write(coverName) { outputStream ->
                // Write some data first
                outputStream.write(byteArrayOf(1, 2, 3))
                throw IOException("Test exception")
            }
        } catch (e: IOException) {
            // Expected
        }
        
        // Assert - temp file should be deleted
        val tmpFile = File(storageDir, tmpFileName)
        assertTrue(!tmpFile.exists())
    }

    @Test
    fun ls_noExcludeFilter_returnsAllFiles() = runTest {
        // Arrange
        val files = listOf("cover1.jpg", "cover2.png", "cover3.jpeg")
        
        files.forEach { 
            File(storageDir, it).createNewFile()
        }

        // Act
        val result = coverStorage.ls(emptySet())

        // Assert
        assertEquals(files.size, result.size)
        files.forEach {
            assertTrue(result.contains(it))
        }
    }

    @Test
    fun ls_withExcludeFilter_returnsFilteredFiles() = runTest {
        // Arrange
        val files = listOf("cover1.jpg", "cover2.png", "cover3.jpeg")
        val excluded = setOf("cover1.jpg", "cover3.jpeg")
        
        files.forEach { 
            File(storageDir, it).createNewFile()
        }

        // Act
        val result = coverStorage.ls(excluded)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.contains("cover2.png"))
        assertTrue(!result.contains("cover1.jpg"))
        assertTrue(!result.contains("cover3.jpeg"))
    }

    @Test
    fun ls_emptyDirectory_returnsEmptyList() = runTest {
        // Act
        val result = coverStorage.ls(emptySet())

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun rm_existingFile_deletesFile() = runTest {
        // Arrange
        val coverName = "to-delete.jpg"
        val file = File(storageDir, coverName)
        file.createNewFile()
        
        assertTrue(file.exists())

        // Act
        coverStorage.rm(coverName)

        // Assert
        assertTrue(!file.exists())
    }

    @Test
    fun rm_nonExistentFile_doesNothing() = runTest {
        // Act - should not throw
        coverStorage.rm("nonexistent.jpg")
    }
    
    @Test
    fun fdCover_fd_returnsValidFileDescriptor() = runTest {
        // Arrange
        val coverName = "fd-test.jpg"
        val testData = byteArrayOf(1, 2, 3, 4, 5)
        
        val coverFile = File(storageDir, coverName)
        coverFile.writeBytes(testData)
        
        // Act
        val cover = coverStorage.find(coverName)
        val fd = cover?.fd()
        
        // Assert
        assertNotNull(fd)
        
        // Clean up
        fd?.close()
    }
    
    @Test
    fun fdCover_fd_returnsNullOnIOException() = runTest {
        // Arrange
        val coverName = "fd-error.jpg"
        
        // Create a mock file that throws on ParcelFileDescriptor.open
        val mockFile = mockk<File>()
        every { mockFile.exists() } returns true
        every { mockFile.name } returns coverName
        
        mockkStatic(ParcelFileDescriptor::class)
        coEvery { 
            ParcelFileDescriptor.open(any(), any()) 
        } throws IOException("Test fd exception")
        
        // Create mock cover
        val cover = FSStoredCover(mockFile)
        
        // Act
        val fd = cover.fd()
        
        // Assert
        assertNull(fd)
    }
}

// Exposing the private FSStoredCover class for testing
private class FSStoredCover(private val file: File) : org.oxycblt.musikr.covers.FDCover {
    override val id: String = file.name

    override suspend fun fd() =
        try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        } catch (e: IOException) {
            null
        }

    override suspend fun open() = file.inputStream()

    override fun equals(other: Any?) = other is FSStoredCover && file == other.file

    override fun hashCode() = file.hashCode()
}