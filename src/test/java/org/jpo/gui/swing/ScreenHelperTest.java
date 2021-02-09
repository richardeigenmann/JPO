package org.jpo.gui.swing;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScreenHelperTest {


    @Test
    public void testGetNumberOfScreenDevices() {
        final int screens = ScreenHelper.getNumberOfScreenDevices();
        if (GraphicsEnvironment.isHeadless()) {
            assertEquals(screens, 0);
        } else {
            assertTrue(screens >= 1);
        }
    }

    @Test
    public void testIsXinerama() {
        final boolean isXinerama = ScreenHelper.isXinerama();
        // not much we can test here as each machine will be different
        // but at least the code executes without a crash
    }

    @Test
    public void testGetXineramaScreenBounds() {
        final Rectangle bounds = ScreenHelper.getXineramaScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(bounds.width > 200);
            assertTrue(bounds.height > 200);
        }
    }

    @Test
    public void testGetPrimaryScreen() {
        final Rectangle primaryScreenBounds = ScreenHelper.getPrimaryScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(primaryScreenBounds.width > 200);
            assertTrue(primaryScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(primaryScreenBounds.width <= overallBounds.width);
            assertTrue(primaryScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetSecondaryScreen() {
        final Rectangle secondaryScreenBounds = ScreenHelper.getSecondaryScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(secondaryScreenBounds.width > 200);
            assertTrue(secondaryScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(secondaryScreenBounds.width <= overallBounds.width);
            assertTrue(secondaryScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetLeftScreenBounds() {
        final Rectangle leftScreenBounds = ScreenHelper.getLeftScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(leftScreenBounds.width > 200);
            assertTrue(leftScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(leftScreenBounds.width <= overallBounds.width);
            assertTrue(leftScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetRightScreenBounds() {
        final Rectangle rightScreenBounds = ScreenHelper.getRightScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(rightScreenBounds.width > 200);
            assertTrue(rightScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(rightScreenBounds.width <= overallBounds.width);
            assertTrue(rightScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetTopLeftScreenBounds() {
        final Rectangle topLeftScreenBounds = ScreenHelper.getTopLeftScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(topLeftScreenBounds.width > 200);
            assertTrue(topLeftScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(topLeftScreenBounds.width <= overallBounds.width);
            assertTrue(topLeftScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetTopRightScreenBounds() {
        final Rectangle topRightScreenBounds = ScreenHelper.getTopRightScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(topRightScreenBounds.width > 200);
            assertTrue(topRightScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(topRightScreenBounds.width <= overallBounds.width);
            assertTrue(topRightScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetBottomLeftScreenBounds() {
        final Rectangle bottomLeftScreenBounds = ScreenHelper.getBottomLeftScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(bottomLeftScreenBounds.width > 200);
            assertTrue(bottomLeftScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(bottomLeftScreenBounds.width <= overallBounds.width);
            assertTrue(bottomLeftScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testGetBottomRightScreenBounds() {
        final Rectangle bottomRightScreenBounds = ScreenHelper.getBottomRightScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(bottomRightScreenBounds.width > 200);
            assertTrue(bottomRightScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds();
            assertTrue(bottomRightScreenBounds.width <= overallBounds.width);
            assertTrue(bottomRightScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    public void testExplainGraphicsEnvironment() {
        final StringBuilder stringBuilder = ScreenHelper.explainGraphicsEnvironment();
        assertTrue(stringBuilder.length() > 200);
    }

}