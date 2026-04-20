package org.jpo.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.jpo.datamodel.Tools.copyResourceToTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
Copyright (C) 2023-2026 Richard Eigenmann.
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
            pi1.setImageLocation(copyResourceToTempFile("/exif-test-canon-eos-350d.jpg"));
            pi2.setImageLocation(copyResourceToTempFile("/exif-test-canon-eos-60d.jpg"));
        } catch (IOException e) {
            fail("Failed to load setup resources: " + e.getMessage());
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
        final var nodeStatistics = new NodeStatistics(rootNode);
        assertEquals( rootNode, nodeStatistics.getNode());
    }

    /**
     * Test of getNumberOfNodes method, of class NodeStatistics.
     */
    @Test
    void testGetNumberOfNodes() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        var nodeStatistics = new NodeStatistics(rootNode);
        assertEquals( 9, nodeStatistics.getNumberOfNodes());
    }

    /**
     * Test of getNumberOfGroups method, of class NodeStatistics.
     */
    @Test
    void testGetNumberOfGroups() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        var nodeStatistics = new NodeStatistics(rootNode);
        assertEquals( 3, nodeStatistics.getNumberOfGroups());
    }

    /**
     * Test of getNumberOfPictures method, of class NodeStatistics.
     */
    @Test
    void testGetNumberOfPictures() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        var nodeStatistics = new NodeStatistics(rootNode);
        assertEquals( 5, nodeStatistics.getNumberOfPictures());
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
        var nodeStatistics = new NodeStatistics(picture1);
        assertEquals( 3148102, nodeStatistics.getSizeOfPictures() );
    }


    @Test
    void getSizeOfPicturesSinglePictureInfo() throws IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var NIKON_D100_IMAGE = "/exif-test-nikon-d100-1.jpg";
        final var imageFile = copyResourceToTempFile(NIKON_D100_IMAGE);
        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(imageFile);
        final var node = new SortableDefaultMutableTreeNode(pictureInfo);
        final var nodeStatistics = new NodeStatistics(node);
        assertEquals(21599, nodeStatistics.getSizeOfPictures());
    }

    @Test
    void getSizeOfPicturesSingleGroupInfo() throws IOException {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var NIKON_D100_IMAGE = "/exif-test-nikon-d100-1.jpg";
        final var SAMSUNG_S4_IMAGE = "/exif-test-samsung-s4.jpg";

        final var root = new SortableDefaultMutableTreeNode(new GroupInfo("Root Node"));
        final var groupNode1 = new SortableDefaultMutableTreeNode(new GroupInfo("Group 1"));
        root.add(groupNode1);
        final var imageFile1 = copyResourceToTempFile(NIKON_D100_IMAGE);
        final var pictureInfo1 = new PictureInfo();
        pictureInfo1.setImageLocation(imageFile1);
        final var piNode1 = new SortableDefaultMutableTreeNode(pictureInfo1);
        groupNode1.add(piNode1);

        final var groupNode2 = new SortableDefaultMutableTreeNode(new GroupInfo("Group 2"));
        root.add(groupNode2);
        final var imageFile2 = copyResourceToTempFile(SAMSUNG_S4_IMAGE);
        final var pictureInfo2 = new PictureInfo();
        pictureInfo2.setImageLocation(imageFile2);
        final var piNode2 = new SortableDefaultMutableTreeNode(pictureInfo2);
        groupNode2.add(piNode2);

        final var nodeStatistics = new NodeStatistics(root);
        assertEquals(21599 + 2354328, nodeStatistics.getSizeOfPictures());
    }


}
