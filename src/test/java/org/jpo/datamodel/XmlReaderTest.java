package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Tests for the XmlReader class
 *
 * @author Richard Eigenmann
 */
public class XmlReaderTest {

    @Test
    public void testReader() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SortableDefaultMutableTreeNode rootNode = new SortableDefaultMutableTreeNode();

        URL image = XmlReaderTest.class.getClassLoader().getResource( "exif-test-canon-eos-350d.jpg" );
        File imageFile = null;
        try {
            imageFile = new File( Objects.requireNonNull(image).toURI() );
        } catch ( URISyntaxException | NullPointerException ex ) {
            Logger.getLogger( XmlReaderTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail( "Could not create imageFile" );
        }

        final PictureInfo pi = new PictureInfo( imageFile, "First Picture" );
        final SortableDefaultMutableTreeNode picture1 = new SortableDefaultMutableTreeNode( pi );

        rootNode.add( picture1 );
        assertEquals( ( (PictureInfo) picture1.getUserObject() ).getImageFile().getName(), pi.getImageFile().getName() );
    }

}
