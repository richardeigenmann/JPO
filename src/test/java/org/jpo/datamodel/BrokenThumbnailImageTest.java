package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class BrokenThumbnailImageTest {

    private SourcePicture sourcePicture;

    @Test
    void testGetImage() throws IOException {
        final var scalablePicture = new ScalablePicture();
        BrokenThumbnailImage.getImage(scalablePicture);

        sourcePicture = scalablePicture.getSourcePicture();
        assertEquals("cb2a91f3116eee469fa3d75b1a8017e49212c96d050ca003e62af0616dcdbdc7", sourcePicture.getSha256());
        assertEquals(151, sourcePicture.getWidth());
        assertEquals(136, sourcePicture.getHeight());
    }

    @Test
    void testThatYouCantInstantiateTheObject() {
        // A utility class's constructor is private, so we must use reflection to test it.
        final var exception = assertThrows(InvocationTargetException.class, () -> {
            final Constructor<BrokenThumbnailImage> constructor = BrokenThumbnailImage.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }, "Should not be able to instantiate the BrokenThumbnailImage class");

        // The reflection call wraps the original exception in an InvocationTargetException.
        // We need to check the cause to verify the correct exception was thrown.
        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertEquals("Utility class", exception.getCause().getMessage());
    }

}
