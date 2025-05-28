package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SettingsDialogTest {

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Tests constructing a CategoryEditorJFrame
     */
    @Test
    void testConstructor() {
        //assumeFalse(GraphicsEnvironment.isHeadless());
        final var jDialog = GuiActionRunner.execute(() -> new SettingsDialog(false));
        assertNotNull(jDialog);
        GuiActionRunner.execute(jDialog::getRid);
        /*try {
            SwingUtilities.invokeAndWait(() -> {
                final SettingsDialog jDialog = new SettingsDialog(false);
                assertNotNull(jDialog);
                jDialog.getRid();
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }*/
    }

}