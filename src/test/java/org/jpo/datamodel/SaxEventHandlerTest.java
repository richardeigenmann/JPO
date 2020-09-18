package org.jpo.datamodel;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * @author Richard Eigenmann
 */
public class SaxEventHandlerTest {

    /**
     * Jpo uses the dtd file in the classpath. As this can go missing if the
     * build is poor this unit test checks whether it is there
     */
    @Test
    public void testGetCollectionDtdInputSource() {
        try {
            InputSource inputSource = SaxEventHandler.getCollectionDtdInputSource();
            assertNotNull(inputSource);

            try (final InputStream is = inputSource.getByteStream()) {
                final String dtdDocument = IOUtils.toString(is, "UTF-8");
                assert (dtdDocument.contains("collection"));
            }

        } catch (IOException e) {
            fail("Could not find collection.dtd file: " + e.getMessage());
        }

    }

}
