package org.jpo.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
Copyright (C) 2023-2024 Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed
in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
*/


/**
 *
 * @author Richard Eigenmann
 */
class NodeStatisticsTest{


    private SortableDefaultMutableTreeNode rootNode;

    private final GroupInfo gr1 = new GroupInfo("Group1");

    private SortableDefaultMutableTreeNode group1;

    private final GroupInfo gr2 = new GroupInfo("Group2");

    private final GroupInfo gr3 = new GroupInfo("Group3");

    private final PictureInfo pi1 = new PictureInfo();

    private SortableDefaultMutableTreeNode picture1;

    private final PictureInfo pi2 = new PictureInfo();

    private final PictureInfo pi3 = new PictureInfo(new File("images/image3.jpg"), "Third Picture");

    private final PictureInfo pi4 = new PictureInfo(new File("images/image4.jpg"), "Fourth Picture");

    private final PictureInfo pi5 = new PictureInfo(new File("images/image5.jpg"), "Fifth Picture");

    @BeforeEach
    void setUp()  {
        rootNode = new SortableDefaultMutableTreeNode();
        group1 = new SortableDefaultMutableTreeNode(gr1);
        SortableDefaultMutableTreeNode group2 = new SortableDefaultMutableTreeNode(gr2);
        try {
            pi1.setImageLocation(new File(ClassLoader.getSystemResources("exif-test-canon-eos-350d.jpg").nextElement().toURI()));
            pi2.setImageLocation(new File(ClassLoader.getSystemResources("exif-test-canon-eos-60d.jpg").nextElement().toURI()));
        } catch (URISyntaxException | IOException e) {
            fail(e.getMessage());
        }
        pi1.setDescription( "First Picture");
        pi2.setDescription("Second Picture");
        picture1 = new SortableDefaultMutableTreeNode(pi1);
        SortableDefaultMutableTreeNode picture2 = new SortableDefaultMutableTreeNode(pi2);
        SortableDefaultMutableTreeNode group3 = new SortableDefaultMutableTreeNode(gr3);
        SortableDefaultMutableTreeNode picture3 = new SortableDefaultMutableTreeNode(pi3);
        SortableDefaultMutableTreeNode picture4 = new SortableDefaultMutableTreeNode(pi4);
        SortableDefaultMutableTreeNode picture5 = new SortableDefaultMutableTreeNode(pi5);
        rootNode.add(group1);
        rootNode.add(group2);
        group1.add(picture1);
        group1.add(picture2);
        group1.add(group3);
        group3.add(picture3);
        group2.add(picture4);
        group2.add(picture5);
    }

    /**
     * Test of getNode method, of class NodeStatistics.
     */
    @Test
    void testSetGetNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        NodeStatistics ns1 = new NodeStatistics(rootNode);
        assertEquals( rootNode, ns1.getNode());
    }

    /**
     * Test of getNumberOfNodes method, of class NodeStatistics.
     */
    @Test
    void testGetNumberOfNodes() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        NodeStatistics ns1 = new NodeStatistics(rootNode);
        assertEquals( 9, ns1.getNumberOfNodes());
    }

    /**
     * Test of getNumberOfGroups method, of class NodeStatistics.
     */
    @Test
    void testGetNumberOfGroups() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        NodeStatistics ns = new NodeStatistics(rootNode);
        assertEquals( 3, ns.getNumberOfGroups());
    }

    /**
     * Test of getNumberOfPictures method, of class NodeStatistics.
     */
    @Test
    void testGetNumberOfPictures() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        NodeStatistics ns = new NodeStatistics(rootNode);
        assertEquals( 5, ns.getNumberOfPictures());
    }

    /**
     * Test of countPictures method, of class NodeStatistics.
     */
    @Test
    void testCountPictures() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // Recursive picture count
        assertEquals(3, NodeStatistics.countPictures(group1, true));
        // Non-recursive picture count
        assertEquals(2, NodeStatistics.countPictures(group1, false));
    }

    
    /**
     * Test of countPictures with null parameter
     */
    @Test
    void testCountPicturesNull() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        assertEquals(0, NodeStatistics.countPictures(null, true));
        assertEquals(0, NodeStatistics.countPictures(null, false));
    }


    /**
     * Test sizeOfPictures on a single PictureInfo Node
     */
    @Test
    void testSizeOfPicturesPictureInfo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        NodeStatistics ns = new NodeStatistics(picture1);
        assertEquals( 3148102, ns.getSizeOfPictures() );
    }


    @Test
    void getSizeOfPicturesSinglePictureInfo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        try {
            final File imageFile = new File(NodeStatisticsTest.class.getClassLoader().getResource(NIKON_D100_IMAGE).toURI());
            final PictureInfo pi = new PictureInfo();
            pi.setImageLocation(imageFile);
            final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode(pi);
            final NodeStatistics ns = new NodeStatistics(node);
            assertEquals(21599, ns.getSizeOfPictures());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getSizeOfPicturesSingleGroupInfo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final String NIKON_D100_IMAGE = "exif-test-nikon-d100-1.jpg";
        final String SAMSUNG_S4_IMAGE = "exif-test-samsung-s4.jpg";
        try {
            final SortableDefaultMutableTreeNode root = new SortableDefaultMutableTreeNode(new GroupInfo("Root Node"));
            final SortableDefaultMutableTreeNode g1 = new SortableDefaultMutableTreeNode(new GroupInfo("Group 1"));
            root.add(g1);
            final File imageFile1 = new File(NodeStatisticsTest.class.getClassLoader().getResource(NIKON_D100_IMAGE).toURI());
            final PictureInfo pictureInfo1 = new PictureInfo();
            pictureInfo1.setImageLocation(imageFile1);
            final SortableDefaultMutableTreeNode piNode1 = new SortableDefaultMutableTreeNode(pictureInfo1);
            g1.add(piNode1);

            final SortableDefaultMutableTreeNode g2 = new SortableDefaultMutableTreeNode(new GroupInfo("Group 2"));
            root.add(g2);
            final File imageFile2 = new File(NodeStatisticsTest.class.getClassLoader().getResource(SAMSUNG_S4_IMAGE).toURI());
            final PictureInfo pictureInfo2 = new PictureInfo();
            pictureInfo2.setImageLocation(imageFile2);
            final SortableDefaultMutableTreeNode piNode2 = new SortableDefaultMutableTreeNode(pictureInfo2);
            g2.add(piNode2);

            final NodeStatistics ns = new NodeStatistics(root);
            assertEquals(21599 + 2354328, ns.getSizeOfPictures());
        } catch (URISyntaxException e) {
            fail(e.getMessage());
        }
    }

}
