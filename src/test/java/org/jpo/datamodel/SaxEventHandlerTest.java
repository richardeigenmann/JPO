package org.jpo.datamodel;

import org.junit.Test;
import org.xml.sax.InputSource;

import static org.junit.Assert.assertNotNull;

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
        // not sure how I want to create the object as it needs input streams and stuff so I'll just do the code that
        // the method resolveEntity does.
        //InputSource s = new InputSource( Settings.CLASS_LOADER.getResourceAsStream( "org.jpo/collection.dtd" ) );
        InputSource s = SaxEventHandler.getCollectionDtdInputSource();
        assertNotNull( "No org.jpo/collection.dtd found", s );
    }

}
