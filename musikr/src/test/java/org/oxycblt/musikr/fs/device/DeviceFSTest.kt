/*
 * Copyright (c) 2024 Auxio Project
 * DeviceFSTest.kt is part of Auxio.
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

package org.oxycblt.musikr.fs.device

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.DocumentsContract
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.fs.MusicLocation
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.Volume
import org.oxycblt.musikr.fs.path.DocumentPathFactory
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class DeviceFSTest {

    private lateinit var contentResolver: ContentResolver
    private lateinit var context: Context
    private lateinit var mockRootUri: Uri
    private lateinit var mockChildUri: Uri
    private lateinit var documentPathFactory: DocumentPathFactory
    private lateinit var deviceFS: DeviceFS
    private lateinit var testPath: Path

    @Before
    fun setup() {
        // Set up mocks
        contentResolver = mockk(relaxed = true)
        context = mockk(relaxed = true)
        mockRootUri = mockk()
        mockChildUri = mockk()
        documentPathFactory = mockk()
        
        // Create test path
        val testVolume = TestVolume("primary")
        val components = Components.parseUnix("")
        testPath = Path(testVolume, components)
        
        // Mock context to return our contentResolver
        every { context.contentResolverSafe } returns contentResolver
        
        // Mock DocumentsContract static methods
        mockkStatic(DocumentsContract::class)
        every { DocumentsContract.buildChildDocumentsUriUsingTree(any(), any()) } returns mockRootUri
        every { DocumentsContract.buildDocumentUriUsingTree(any(), any()) } returns mockChildUri
        every { DocumentsContract.getTreeDocumentId(any()) } returns "tree-doc-id"
        every { DocumentsContract.isTreeUri(any()) } returns true
        
        // Mock DocumentPathFactory
        mockkStatic(DocumentPathFactory::class)
        every { DocumentPathFactory.from(any()) } returns documentPathFactory
        every { documentPathFactory.unpackDocumentTreeUri(any()) } returns testPath
        
        // Mock MusicLocation resolving
        every { 
            contentResolver.persistedUriPermissions 
        } returns emptyList()
        
        every { 
            contentResolver.takePersistableUriPermission(any(), any()) 
        } just Runs
        
        // Create DeviceFS instance via the companion function
        deviceFS = DeviceFS.from(context, withHidden = false)
    }

    @Test
    fun explore_emptyDirectory_returnsNoFiles() = runTest {
        // Arrange
        val location = createTestMusicLocation("primary", emptyList())
        val emptyCursor = createMockCursor(emptyList())
        
        // Set up the query to return our empty cursor
        mockContentResolverQuery(emptyCursor)

        // Act
        val result = deviceFS.explore(flow { emit(location) }).toList()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun explore_filtersHiddenFiles() = runTest {
        // Arrange
        val location = createTestMusicLocation("primary", emptyList())
        
        val entries = listOf(
            MockFileEntry("doc1", "visible.mp3", "audio/mpeg", 1024, 1000),
            MockFileEntry("doc2", ".hidden.mp3", "audio/mpeg", 2048, 2000)
        )
        
        val cursor = createMockCursor(entries)
        mockContentResolverQuery(cursor)

        // Act
        val result = deviceFS.explore(flow { emit(location) }).toList()

        // Assert
        assertEquals(1, result.size)
        assertEquals("visible.mp3", result[0].path.name)
    }

    @Test
    fun explore_includesHiddenFiles_whenConfigured() = runTest {
        // Arrange
        val deviceFSWithHidden = DeviceFS.from(context, withHidden = true)
        val location = createTestMusicLocation("primary", emptyList())
        
        val entries = listOf(
            MockFileEntry("doc1", "visible.mp3", "audio/mpeg", 1024, 1000),
            MockFileEntry("doc2", ".hidden.mp3", "audio/mpeg", 2048, 2000)
        )
        
        val cursor = createMockCursor(entries)
        mockContentResolverQuery(cursor)

        // Act
        val result = deviceFSWithHidden.explore(flow { emit(location) }).toList()

        // Assert
        assertEquals(2, result.size)
        val fileNames = result.map { it.path.name }
        assertTrue(fileNames.contains("visible.mp3"))
        assertTrue(fileNames.contains(".hidden.mp3"))
    }

    @Test
    fun explore_traversesDirectories() = runTest {
        // Arrange
        val location = createTestMusicLocation("primary", emptyList())
        
        // Root directory contains 1 file and 1 subdirectory
        val rootEntries = listOf(
            MockFileEntry("doc1", "root.mp3", "audio/mpeg", 1024, 1000),
            MockDirEntry("dir1", "subdir")
        )
        
        // Subdirectory contains 2 files
        val subdirEntries = listOf(
            MockFileEntry("doc2", "sub1.mp3", "audio/mpeg", 2048, 2000),
            MockFileEntry("doc3", "sub2.mp3", "audio/mpeg", 3072, 3000)
        )
        
        // Set up cursor behavior to return different cursors for different queries
        val rootCursor = createMockCursor(rootEntries)
        val subdirCursor = createMockCursor(subdirEntries)
        
        // First call will get root entries, second call will get subdir entries
        mockContentResolverQuery(rootCursor, subdirCursor)

        // Act
        val result = deviceFS.explore(flow { emit(location) }).toList()

        // Assert
        assertEquals(3, result.size)
        
        val fileNames = result.map { it.path.name }
        assertTrue(fileNames.contains("root.mp3"))
        assertTrue(fileNames.contains("sub1.mp3"))
        assertTrue(fileNames.contains("sub2.mp3"))
    }

    @Test
    fun explore_multipleLocations_returnsAllFiles() = runTest {
        // Arrange
        val location1 = createTestMusicLocation("primary", listOf("Music"))
        val location2 = createTestMusicLocation("secondary", listOf("Downloads"))
        
        val location1Entries = listOf(
            MockFileEntry("doc1", "music1.mp3", "audio/mpeg", 1024, 1000),
            MockFileEntry("doc2", "music2.mp3", "audio/mpeg", 2048, 2000)
        )
        
        val location2Entries = listOf(
            MockFileEntry("doc3", "download1.mp3", "audio/mpeg", 3072, 3000)
        )
        
        val location1Cursor = createMockCursor(location1Entries)
        val location2Cursor = createMockCursor(location2Entries)
        
        // First call will process location1, second call will process location2
        mockContentResolverQuery(location1Cursor, location2Cursor)

        // Act
        val result = deviceFS.explore(flow { 
            emit(location1)
            emit(location2)
        }).toList()

        // Assert
        assertEquals(3, result.size)
        
        val fileNames = result.map { it.path.name }
        assertTrue(fileNames.contains("music1.mp3"))
        assertTrue(fileNames.contains("music2.mp3"))
        assertTrue(fileNames.contains("download1.mp3"))
    }

    @Test
    fun explore_fileMetadata_isCorrect() = runTest {
        // Arrange
        val location = createTestMusicLocation("primary", emptyList())
        
        val entries = listOf(
            MockFileEntry("doc1", "test.mp3", "audio/mpeg", 1024, 1000)
        )
        
        val cursor = createMockCursor(entries)
        mockContentResolverQuery(cursor)

        // Act
        val result = deviceFS.explore(flow { emit(location) }).toList()

        // Assert
        assertEquals(1, result.size)
        with(result[0]) {
            assertEquals("test.mp3", path.name)
            assertEquals("audio/mpeg", mimeType)
            assertEquals(1024, size)
            assertEquals(1000, modifiedMs)
        }
    }

    /**
     * Mock the content resolver's query method to handle cursor interactions
     */
    private fun mockContentResolverQuery(vararg cursors: Cursor) {
        val cursorList = cursors.toList()
        var callCount = 0
        
        // Mock the useQuery extension function
        every { 
            contentResolver.useQuery<Any>(any(), any(), any(), any(), any()) 
        } answers { call ->
            val cursor = cursorList[callCount % cursorList.size]
            callCount++
            val block = call.invocation.args[4] as (Cursor) -> Any
            block(cursor)
        }
    }

    /**
     * Create a mock cursor with the given entries
     */
    private fun createMockCursor(entries: List<MockFSEntry>): Cursor {
        val cursor = MatrixCursor(
            arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            )
        )
        
        entries.forEach { entry ->
            when (entry) {
                is MockFileEntry -> {
                    cursor.addRow(arrayOf(
                        entry.id,
                        entry.name,
                        entry.mimeType,
                        entry.size,
                        entry.lastModified
                    ))
                }
                is MockDirEntry -> {
                    cursor.addRow(arrayOf(
                        entry.id,
                        entry.name,
                        DocumentsContract.Document.MIME_TYPE_DIR,
                        0,
                        0
                    ))
                }
            }
        }
        
        return cursor
    }

    /**
     * Create a test MusicLocation using the factory method
     */
    private fun createTestMusicLocation(volumeId: String, pathComponents: List<String>): MusicLocation {
        // For each different location, we need a slightly different path to be returned
        val volume = TestVolume(volumeId)
        val components = Components.parseUnix(pathComponents.joinToString("/"))
        val path = Path(volume, components)
        
        // Override the path that will be returned for this specific call
        every { documentPathFactory.unpackDocumentTreeUri(mockRootUri) } returns path
        
        // Use MusicLocation.new which is a public factory method
        return MusicLocation.new(context, mockRootUri)!!
    }

    // Mock classes for test data
    sealed class MockFSEntry
    
    data class MockFileEntry(
        val id: String,
        val name: String,
        val mimeType: String,
        val size: Long,
        val lastModified: Long
    ) : MockFSEntry()
    
    data class MockDirEntry(
        val id: String,
        val name: String
    ) : MockFSEntry()
    
    class TestVolume(override val id: String?) : Volume.External {
        override val mediaStoreName: String? = id
        override val components: Components? = null
        
        override fun resolveName(context: Context): String = id ?: "unknown"
    }
}