package org.jpo.gui.swing;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Mockito.when;

class ScreenHelperTest {

    @Test
    void testGetNumberOfScreenDevices_0_Devices() {
        try (var mockedGraphicsEnvironment = Mockito.mockStatic(GraphicsEnvironment.class)) {
            var mockGraphicsEnvironment = Mockito.mock(GraphicsEnvironment.class);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::getLocalGraphicsEnvironment).thenReturn(mockGraphicsEnvironment);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::isHeadless).thenReturn(true);

            when(mockGraphicsEnvironment.getScreenDevices()).thenReturn(new GraphicsDevice[]{});

            assertEquals(0, ScreenHelper.getNumberOfScreenDevices(mockGraphicsEnvironment));
        } catch (Exception e) {
            fail ("Test Failed: " + e.getMessage());
        }
    }

    @Test
    void testGetNumberOfScreenDevices_1_Device() {
        var mockGraphicsDevice1 = Mockito.mock(GraphicsDevice.class);
        when(mockGraphicsDevice1.getIDstring()).thenReturn("mockDisplay1");

        try (var mockedGraphicsEnvironment = Mockito.mockStatic(GraphicsEnvironment.class)) {
            var mockGraphicsEnvironment = Mockito.mock(GraphicsEnvironment.class);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::getLocalGraphicsEnvironment).thenReturn(mockGraphicsEnvironment);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::isHeadless).thenReturn(false);

            when(mockGraphicsEnvironment.getScreenDevices()).thenReturn(new GraphicsDevice[]{mockGraphicsDevice1});

            assertEquals(1, ScreenHelper.getNumberOfScreenDevices(mockGraphicsEnvironment));
        } catch (Exception e) {
            fail ("Test Failed: " + e.getMessage());
        }
    }

    @Test
    void testGetNumberOfScreenDevices_2_Devices() {
        // Mock GraphicsDevice instances
        var mockGraphicsDevice1 = Mockito.mock(GraphicsDevice.class);
        when(mockGraphicsDevice1.getIDstring()).thenReturn("mockDisplay1");

        var mockGraphicsDevice2 = Mockito.mock(GraphicsDevice.class);
        when(mockGraphicsDevice2.getIDstring()).thenReturn("mockDisplay2");

        try (var mockedGraphicsEnvironment = Mockito.mockStatic(GraphicsEnvironment.class)) {
            var mockGraphicsEnvironment = Mockito.mock(GraphicsEnvironment.class);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::getLocalGraphicsEnvironment).thenReturn(mockGraphicsEnvironment);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::isHeadless).thenReturn(false); // Control headless state

            when(mockGraphicsEnvironment.getScreenDevices()).thenReturn(new GraphicsDevice[]{mockGraphicsDevice1, mockGraphicsDevice2});

            assertEquals(2, ScreenHelper.getNumberOfScreenDevices(mockGraphicsEnvironment));
        } catch (Exception e) {
            fail ("Test Failed: " + e.getMessage());
        }
    }

    @Test
    void testIsXinerama() {
        var mockGraphicsDevice1 = Mockito.mock(GraphicsDevice.class);
        var mockGraphicsConfiguration1 = Mockito.mock(GraphicsConfiguration.class);
        when(mockGraphicsConfiguration1.getBounds()).thenReturn(new Rectangle(0, 0, 1920, 1080));
        when(mockGraphicsDevice1.getDefaultConfiguration()).thenReturn(mockGraphicsConfiguration1);
        when(mockGraphicsDevice1.getConfigurations()).thenReturn(new GraphicsConfiguration[]{mockGraphicsConfiguration1});

        var mockGraphicsDevice2 = Mockito.mock(GraphicsDevice.class);
        var mockGraphicsConfiguration2 = Mockito.mock(GraphicsConfiguration.class);
        when(mockGraphicsConfiguration2.getBounds()).thenReturn(new Rectangle(1920, 0, 1280, 720));
        when(mockGraphicsDevice2.getDefaultConfiguration()).thenReturn(mockGraphicsConfiguration2);
        when(mockGraphicsDevice2.getConfigurations()).thenReturn(new GraphicsConfiguration[]{mockGraphicsConfiguration2});

        try (var mockedGraphicsEnvironment = Mockito.mockStatic(GraphicsEnvironment.class)) {
            var mockGraphicsEnvironment = Mockito.mock(GraphicsEnvironment.class);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::getLocalGraphicsEnvironment).thenReturn(mockGraphicsEnvironment);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::isHeadless).thenReturn(false);
            when(mockGraphicsEnvironment.getScreenDevices()).thenReturn(new GraphicsDevice[]{mockGraphicsDevice1, mockGraphicsDevice2});

            final var bounds = ScreenHelper.getXineramaScreenBounds(mockGraphicsEnvironment);

            assertTrue(ScreenHelper.isXinerama(mockGraphicsEnvironment));
            assertEquals(0, bounds.x);
            assertEquals(0, bounds.y);
            assertEquals(1920 + 1280, bounds.width); // Example: 3200
            assertEquals(1080, bounds.height);      // Example: Max height
        }
    }

    @Test
    void testIsNotXinerama() {
        var mockGraphicsDevice = Mockito.mock(GraphicsDevice.class);
        var mockGraphicsConfiguration1 = Mockito.mock(GraphicsConfiguration.class);
        when(mockGraphicsConfiguration1.getBounds()).thenReturn(new Rectangle(0, 0, 1920, 1080));
        when(mockGraphicsDevice.getDefaultConfiguration()).thenReturn(mockGraphicsConfiguration1);
        when(mockGraphicsDevice.getConfigurations()).thenReturn(new GraphicsConfiguration[]{mockGraphicsConfiguration1});

        try (var mockedGraphicsEnvironment = Mockito.mockStatic(GraphicsEnvironment.class)) {
            var mockGraphicsEnvironment = Mockito.mock(GraphicsEnvironment.class);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::getLocalGraphicsEnvironment).thenReturn(mockGraphicsEnvironment);
            mockedGraphicsEnvironment.when(GraphicsEnvironment::isHeadless).thenReturn(false);
            when(mockGraphicsEnvironment.getScreenDevices()).thenReturn(new GraphicsDevice[]{mockGraphicsDevice});

            final var bounds = ScreenHelper.getXineramaScreenBounds(mockGraphicsEnvironment);

            assertFalse(ScreenHelper.isXinerama(mockGraphicsEnvironment));
            assertEquals(0, bounds.x);
            assertEquals(0, bounds.y);
            assertEquals(1920 , bounds.width); // Example: 3200
            assertEquals(1080, bounds.height);      // Example: Max height
        }
    }


    @Test
    void testGetXineramaScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle bounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(bounds.width > 200);
            assertTrue(bounds.height > 200);
        }
    }

    @Test
    void testGetPrimaryScreen() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle primaryScreenBounds = ScreenHelper.getPrimaryScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(primaryScreenBounds.width > 200);
            assertTrue(primaryScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(primaryScreenBounds.width <= overallBounds.width);
            assertTrue(primaryScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetSecondaryScreen() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle secondaryScreenBounds = ScreenHelper.getSecondaryScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(secondaryScreenBounds.width > 200);
            assertTrue(secondaryScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(secondaryScreenBounds.width <= overallBounds.width);
            assertTrue(secondaryScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetLeftScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle leftScreenBounds = ScreenHelper.getLeftScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(leftScreenBounds.width > 200);
            assertTrue(leftScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(leftScreenBounds.width <= overallBounds.width);
            assertTrue(leftScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetRightScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle rightScreenBounds = ScreenHelper.getRightScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(rightScreenBounds.width > 200);
            assertTrue(rightScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(rightScreenBounds.width <= overallBounds.width);
            assertTrue(rightScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetTopLeftScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle topLeftScreenBounds = ScreenHelper.getTopLeftScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(topLeftScreenBounds.width > 200);
            assertTrue(topLeftScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(topLeftScreenBounds.width <= overallBounds.width);
            assertTrue(topLeftScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetTopRightScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle topRightScreenBounds = ScreenHelper.getTopRightScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(topRightScreenBounds.width > 200);
            assertTrue(topRightScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(topRightScreenBounds.width <= overallBounds.width);
            assertTrue(topRightScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetBottomLeftScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle bottomLeftScreenBounds = ScreenHelper.getBottomLeftScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(bottomLeftScreenBounds.width > 200);
            assertTrue(bottomLeftScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(bottomLeftScreenBounds.width <= overallBounds.width);
            assertTrue(bottomLeftScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testGetBottomRightScreenBounds() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final Rectangle bottomRightScreenBounds = ScreenHelper.getBottomRightScreenBounds();
        if (!GraphicsEnvironment.isHeadless()) {
            // arbitrary but if you have a screen, would it not be wider than 200 pixels?
            assertTrue(bottomRightScreenBounds.width > 200);
            assertTrue(bottomRightScreenBounds.height > 200);

            final Rectangle overallBounds = ScreenHelper.getXineramaScreenBounds(GraphicsEnvironment.getLocalGraphicsEnvironment());
            assertTrue(bottomRightScreenBounds.width <= overallBounds.width);
            assertTrue(bottomRightScreenBounds.height <= overallBounds.height);
        }
    }

    @Test
    void testExplainGraphicsEnvironment() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final StringBuilder stringBuilder = ScreenHelper.explainGraphicsEnvironment();
        assertTrue(stringBuilder.length() > 200);
    }

}