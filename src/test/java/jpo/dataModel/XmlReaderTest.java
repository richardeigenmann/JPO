package jpo.dataModel;

import org.junit.Test;

/**
 * Tests for the XmlReader class
 *
 * @author Richard Eigenmann
 */
public class XmlReaderTest {

    /**
     * Jpo uses the dtd file in the classpath. As this can go missing if the
     * build is poor this unit test checks whether it is there
     */
    @Test
    public void testCorrectJarReferences() {
        SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode();
        XmlReader.correctJarReferences(n);
        //TODO: figure out how to test this thing!
    }

}
