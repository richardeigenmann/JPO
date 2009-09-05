package jpo.dataModel;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class DescriptionWordMapTest extends TestCase {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( DescriptionWordMapTest.class.getName() );


    public DescriptionWordMapTest( String testName ) {
        super( testName );
    }


    private SortableDefaultMutableTreeNode getSomeNodes() {
        SortableDefaultMutableTreeNode rootNode = new SortableDefaultMutableTreeNode( new GroupInfo( "Root Node" ) );

        PictureInfo pi1 = new PictureInfo();
        pi1.setDescription( "Welcome to Anchorage" );
        SortableDefaultMutableTreeNode pictureNode1 = new SortableDefaultMutableTreeNode( pi1 );

        PictureInfo pi2 = new PictureInfo();
        pi2.setDescription( "Welcome to Zürich" );
        SortableDefaultMutableTreeNode pictureNode2 = new SortableDefaultMutableTreeNode( pi2 );

        PictureInfo pi3 = new PictureInfo();
        pi3.setDescription( "Welcome to New York" );
        SortableDefaultMutableTreeNode pictureNode3 = new SortableDefaultMutableTreeNode( pi3 );

        PictureInfo pi4 = new PictureInfo();
        pi4.setDescription( "New York by Night" );
        SortableDefaultMutableTreeNode pictureNode4 = new SortableDefaultMutableTreeNode( pi4 );

        rootNode.add( pictureNode1 );
        rootNode.add( pictureNode2 );
        rootNode.add( pictureNode3 );
        rootNode.add( pictureNode4 );

        return rootNode;
    }


    /**
     * Test of getWordNodeMap method, of class DescriptionWordMap.
     */
    public void testGetMap() {
        DescriptionWordMap dwm = new DescriptionWordMap( getSomeNodes() );
        TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> result = dwm.getWordNodeMap();
        assertTrue( String.format( "Verifying that a map of words (which has %d words) was built and that it has more than 4 words", result.size() ), result.size() > 4 );
    }


    /**
     * Test of getCountOfNodes method, of class DescriptionWordMap.
     */
    public void testGetCountOfNodes() {
        DescriptionWordMap dwm = new DescriptionWordMap( getSomeNodes() );
        TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> result = dwm.getWordNodeMap();
        HashSet<SortableDefaultMutableTreeNode> welcomeNodes = result.get( "Welcome" );
        int countWelcome = welcomeNodes.size();
        assertEquals( "Expecting 3 hits for the word Welcome", countWelcome, 3 );

        HashSet<SortableDefaultMutableTreeNode> newYorkNodes = result.get( "New York" );
        int countNewYork = newYorkNodes.size();
        assertEquals( "Expecting 2 hits for the 2 word city New York", countNewYork, 2 );
    }


    /**
     * Test that the right things happen when we give a null node
     */
    public void testNullNode() {
        DescriptionWordMap dwm = new DescriptionWordMap( null );
        int count = dwm.getWordNodeMap().size();
        assertEquals( "Expecting 0 words on a null node", count, 0 );
    }
}
