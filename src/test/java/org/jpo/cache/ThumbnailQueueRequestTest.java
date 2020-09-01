package org.jpo.cache;

import junit.framework.TestCase;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.ThumbnailController;
import org.jpo.gui.swing.Thumbnail;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.fail;
import static org.junit.Assume.assumeFalse;

/**
 * @author Richard Eigenmann
 */
public class ThumbnailQueueRequestTest {

    @Test
    public void increasePriorityToTest() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                ThumbnailController controller = new ThumbnailController(new Thumbnail(), 350);
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                ThumbnailQueueRequest tqr = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.LOWEST_PRIORITY, new Dimension(350, 350));
                Assert.assertEquals("Priority should be LOWEST_PRIORITY", QUEUE_PRIORITY.LOWEST_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.LOW_PRIORITY);
                Assert.assertEquals("Priority should have been increased to LOW_PRIORITY", QUEUE_PRIORITY.LOW_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.MEDIUM_PRIORITY);
                Assert.assertEquals("Priority should have been increased to MEDIUM_PRIORITY", QUEUE_PRIORITY.MEDIUM_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.HIGH_PRIORITY);
                Assert.assertEquals("Priority should have been increased to HIGH_PRIORITY", QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.MEDIUM_PRIORITY);
                Assert.assertEquals("Priority should stay at HIGH_PRIORITY when increasing to MEDIUM_PRIORITY", QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void compareToTest() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                ThumbnailController controller = new ThumbnailController(new Thumbnail(), 350);
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                ThumbnailQueueRequest lowestPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.LOWEST_PRIORITY, new Dimension(350, 350));
                ThumbnailQueueRequest lowPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350));
                ThumbnailQueueRequest mediumPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(350, 350));
                ThumbnailQueueRequest highPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.HIGH_PRIORITY, new Dimension(350, 350));
                TestCase.assertTrue(highPriorityRequest.compareTo(mediumPriorityRequest) < 0);
                TestCase.assertTrue(highPriorityRequest.compareTo(lowestPriorityRequest) < 0);
                TestCase.assertTrue(mediumPriorityRequest.compareTo(lowPriorityRequest) < 0);
                TestCase.assertTrue(lowPriorityRequest.compareTo(lowestPriorityRequest) < 0);
                TestCase.assertEquals(0, lowestPriorityRequest.compareTo(lowestPriorityRequest));
                TestCase.assertEquals(0, lowPriorityRequest.compareTo(lowPriorityRequest));
                TestCase.assertEquals(0, mediumPriorityRequest.compareTo(mediumPriorityRequest));
                TestCase.assertEquals(0, highPriorityRequest.compareTo(highPriorityRequest));
                TestCase.assertTrue(lowestPriorityRequest.compareTo(lowPriorityRequest) > 0);
                TestCase.assertTrue(lowPriorityRequest.compareTo(mediumPriorityRequest) > 0);
                TestCase.assertTrue(mediumPriorityRequest.compareTo(highPriorityRequest) > 0);
                TestCase.assertTrue(lowestPriorityRequest.compareTo(highPriorityRequest) > 0);
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void equalsTest() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                ThumbnailController controller = new ThumbnailController(new Thumbnail(), 350);
                SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                ThumbnailQueueRequest lowestPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.LOWEST_PRIORITY, new Dimension(350, 350));
                ThumbnailQueueRequest lowPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.LOW_PRIORITY, new Dimension(350, 350));
                ThumbnailQueueRequest mediumPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.MEDIUM_PRIORITY, new Dimension(350, 350));
                ThumbnailQueueRequest highPriorityRequest = new ThumbnailQueueRequest(controller, node, QUEUE_PRIORITY.HIGH_PRIORITY, new Dimension(350, 350));
                TestCase.assertFalse(highPriorityRequest.equals(mediumPriorityRequest));
                TestCase.assertFalse(mediumPriorityRequest.equals(lowPriorityRequest));
                TestCase.assertFalse(lowPriorityRequest.equals(lowestPriorityRequest));
                TestCase.assertTrue(lowestPriorityRequest.equals(lowestPriorityRequest));
                TestCase.assertTrue(lowPriorityRequest.equals(lowPriorityRequest));
                TestCase.assertTrue(mediumPriorityRequest.equals(mediumPriorityRequest));
                TestCase.assertTrue(highPriorityRequest.equals(highPriorityRequest));
                TestCase.assertFalse(lowestPriorityRequest.equals(lowPriorityRequest));
                TestCase.assertFalse(lowPriorityRequest.equals(mediumPriorityRequest));
                TestCase.assertFalse(mediumPriorityRequest.equals(highPriorityRequest));
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


}
