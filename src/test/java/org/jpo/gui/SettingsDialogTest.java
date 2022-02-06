package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class SettingsDialogTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Tests constructing a CategoryEditorJFrame
     */
    @Test
    public void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final SettingsDialog jDialog = new SettingsDialog(false);
                assertNotNull(jDialog);
                jDialog.getRid();
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}