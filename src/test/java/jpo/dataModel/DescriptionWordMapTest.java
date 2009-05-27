package jpo.dataModel;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.TreeMap;
import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class DescriptionWordMapTest extends TestCase {

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
     * Test of getMap method, of class DescriptionWordMap.
     */
    public void testGetMap() {
        DescriptionWordMap dwm = new DescriptionWordMap( getSomeNodes() );
        AbstractMap<String, HashSet<SortableDefaultMutableTreeNode>> result = dwm.getMap();
        assertTrue( String.format("Verifying that a map of words (which has %d words) was built and that it has more than 4 words", result.size()), result.size() > 4 );
    }


    /**
     * Test of getCountOfNodes method, of class DescriptionWordMap.
     */
    public void testGetCountOfNodes() {
        DescriptionWordMap dwm = new DescriptionWordMap( getSomeNodes() );
        int countWelcome = dwm.getCountOfNodes( "Welcome" );
        assertEquals( "Expecting 3 hits for the word Welcome", countWelcome, 3 );

        int countNewYork = dwm.getCountOfNodes( "New York" );
        assertEquals( "Expecting 2 hits for the 2 word city New York", countNewYork, 2 );
    }


    /**
     * Test of getMaximumNodes method, of class DescriptionWordMap.
     */
    public void testGetMaximumNodes() {
        DescriptionWordMap dwm = new DescriptionWordMap( getSomeNodes() );
        int max = DescriptionWordMap.getMaximumNodes( dwm.getMap() );
        assertEquals( "Expecting Welcome to be the top counter with 3 entries", max, 3 );
    }


    /**
     * Test of getTruncatedMap method, of class DescriptionWordMap.
     */
    public void testTruncatedMap() {
        DescriptionWordMap dwm = new DescriptionWordMap( getSomeNodes() );
        dwm.truncateToTop( 2);
        TreeMap<String,HashSet<SortableDefaultMutableTreeNode>> truncatedMap = dwm.getTruncatedMap();
        assertEquals( "Expecting the truncated map to have only 2 words", truncatedMap.size(), 2 );

        AbstractMap<String,HashSet<SortableDefaultMutableTreeNode>> fullMap = dwm.getMap();
        assertTrue( "Expecting the maximum nodes for the top word to be the same",DescriptionWordMap.getMaximumNodes( fullMap ) == DescriptionWordMap.getMaximumNodes( truncatedMap ));


    }



}
