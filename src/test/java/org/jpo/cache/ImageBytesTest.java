package org.jpo.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.attribute.FileTime;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ImageBytes} class.
 */
class ImageBytesTest {

    private static final byte[] TEST_BYTES = {1, 2, 3, 4, 5};
    private ImageBytes imageBytes;

    @BeforeEach
    void setUp() {
        imageBytes = new ImageBytes(TEST_BYTES);
    }

    @Test
    void testConstructorAndGetBytes() {
        // The constructor should store the byte array, and getBytes() should return it.
        assertArrayEquals(TEST_BYTES, imageBytes.getBytes(), "getBytes() should return the original byte array.");
    }

    @Test
    void testGetByteArrayInputStream() {
        // The input stream should contain the same bytes as the original array.
        final var inputStream = imageBytes.getByteArrayInputStream();
        final byte[] readBytes = inputStream.readAllBytes();
        assertArrayEquals(TEST_BYTES, readBytes, "The ByteArrayInputStream should contain the correct bytes.");
    }

    @Test
    void testRetrievedFromCacheFlag() {
        // The flag should default to false.
        assertFalse(imageBytes.isRetrievedFromCache(), "isRetrievedFromCache should default to false.");

        // Test setting the flag to true.
        imageBytes.setRetrievedFromCache(true);
        assertTrue(imageBytes.isRetrievedFromCache(), "isRetrievedFromCache should be true after being set.");

        // Test setting the flag back to false.
        imageBytes.setRetrievedFromCache(false);
        assertFalse(imageBytes.isRetrievedFromCache(), "isRetrievedFromCache should be false after being reset.");
    }

    @Test
    void testLastModificationTime() {
        // Create a specific point in time for the test.
        final Instant now = Instant.now();
        final FileTime fileTime = FileTime.from(now);

        // Set the modification time.
        imageBytes.setLastModification(fileTime);

        // Get the modification time and assert it's the same.
        final FileTime retrievedFileTime = imageBytes.getLastModification();
        assertEquals(fileTime, retrievedFileTime, "getLastModification() should return the same FileTime that was set.");
    }

    @Test
    void testGetLastModificationBeforeSetting() {
        // Before setLastModification is called, the internal Instant is null.
        // FileTime.from(null) throws a NullPointerException. This test verifies that behavior.
        assertThrows(NullPointerException.class,
                () -> imageBytes.getLastModification(),
                "Should throw NullPointerException if getLastModification() is called before setting a time.");
    }
}