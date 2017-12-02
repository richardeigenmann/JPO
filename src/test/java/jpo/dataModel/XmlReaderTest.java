package jpo.dataModel;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for the XmlReader class
 *
 * @author Richard Eigenmann
 */
public class XmlReaderTest {

    /**
     * Test the correction of a Jar reference where it is not needed
     */
    @Test
    public void testCorrectJarReferences() {
        final SortableDefaultMutableTreeNode rootNode = new SortableDefaultMutableTreeNode();

        URL image = Settings.CLASS_LOADER.getResource( "exif-test-canon-eos-350d.jpg" );
        File imageFile = null;
        try {
            imageFile = new File (image.toURI());
        } catch ( URISyntaxException ex ) {
            Logger.getLogger( XmlReaderTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail("Could not create imageFile");
        }
                
        final PictureInfo pi = new PictureInfo( imageFile.toString(), "First Picture", "Reference1" );
        final SortableDefaultMutableTreeNode picture1 = new SortableDefaultMutableTreeNode(pi);
        
        rootNode.add( picture1 );
        
        XmlReader.correctJarReferences( rootNode );
        
        assertEquals(((PictureInfo) picture1.getUserObject()).getImageFilename(), pi.getImageFilename());
    }

}
