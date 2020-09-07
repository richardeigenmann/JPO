package org.jpo.gui.swing;

import org.jpo.datamodel.Settings;
import org.jpo.gui.ThumbnailControllerTest;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


public class ThumbnailDescriptionPanelTest {

    @Test
    public void testConstructor() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();
                assertNotNull( panel );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSelected() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();
                assertNotNull( panel );

                panel.showAsSelected();
                assertEquals(Settings.SELECTED_COLOR_TEXT,panel.getPictureDescriptionJTA().getBackground());

                panel.showAsUnselected();
                assertEquals(Settings.UNSELECTED_COLOR,panel.getPictureDescriptionJTA().getBackground());

                panel.showAsSelected(true);
                assertEquals(Settings.SELECTED_COLOR_TEXT,panel.getPictureDescriptionJTA().getBackground());

                panel.showAsSelected(false);
                assertEquals(Settings.UNSELECTED_COLOR,panel.getPictureDescriptionJTA().getBackground());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testSetDescription() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();
                assertNotNull( panel );

                final String TEST_TEXT_1 = "This is a test description";
                panel.setDescription(TEST_TEXT_1);
                assertEquals(TEST_TEXT_1,panel.getDescription());

                final String TEST_TEXT_2 = "This is a different description";
                panel.setDescription(TEST_TEXT_2);
                assertEquals(TEST_TEXT_2,panel.getDescription());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailControllerTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail();
            Thread.currentThread().interrupt();
        }
    }


}