package org.jpo.cache;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.ThumbnailController;
import org.jpo.gui.swing.Thumbnail;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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
                assertEquals( QUEUE_PRIORITY.LOWEST_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.LOW_PRIORITY);
                assertEquals(QUEUE_PRIORITY.LOW_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.MEDIUM_PRIORITY);
                assertEquals( QUEUE_PRIORITY.MEDIUM_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.HIGH_PRIORITY);
                assertEquals( QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority);
                tqr.increasePriorityTo(QUEUE_PRIORITY.MEDIUM_PRIORITY);
                assertEquals( QUEUE_PRIORITY.HIGH_PRIORITY, tqr.priority);
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
                assertTrue(highPriorityRequest.compareTo(mediumPriorityRequest) < 0);
                assertTrue(highPriorityRequest.compareTo(lowestPriorityRequest) < 0);
                assertTrue(mediumPriorityRequest.compareTo(lowPriorityRequest) < 0);
                assertTrue(lowPriorityRequest.compareTo(lowestPriorityRequest) < 0);
                assertEquals(0, lowestPriorityRequest.compareTo(lowestPriorityRequest));
                assertEquals(0, lowPriorityRequest.compareTo(lowPriorityRequest));
                assertEquals(0, mediumPriorityRequest.compareTo(mediumPriorityRequest));
                assertEquals(0, highPriorityRequest.compareTo(highPriorityRequest));
                assertTrue(lowestPriorityRequest.compareTo(lowPriorityRequest) > 0);
                assertTrue(lowPriorityRequest.compareTo(mediumPriorityRequest) > 0);
                assertTrue(mediumPriorityRequest.compareTo(highPriorityRequest) > 0);
                assertTrue(lowestPriorityRequest.compareTo(highPriorityRequest) > 0);
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
                assertFalse(highPriorityRequest.equals(mediumPriorityRequest));
                assertFalse(mediumPriorityRequest.equals(lowPriorityRequest));
                assertFalse(lowPriorityRequest.equals(lowestPriorityRequest));
                assertTrue(lowestPriorityRequest.equals(lowestPriorityRequest));
                assertTrue(lowPriorityRequest.equals(lowPriorityRequest));
                assertTrue(mediumPriorityRequest.equals(mediumPriorityRequest));
                assertTrue(highPriorityRequest.equals(highPriorityRequest));
                assertFalse(lowestPriorityRequest.equals(lowPriorityRequest));
                assertFalse(lowPriorityRequest.equals(mediumPriorityRequest));
                assertFalse(mediumPriorityRequest.equals(highPriorityRequest));
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


}
