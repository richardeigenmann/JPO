package org.jpo.gui;

import org.assertj.swing.edt.GuiActionRunner;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThumbnailLayoutManagerTest {

    @Test
    void testCalcCols() {
        // Create the JPanel on the EDT
        final var panel = GuiActionRunner.execute(() -> new JPanel());

        final var layoutManaer = new ThumbnailLayoutManager(panel);
        assertEquals( 1, layoutManaer.calculateCols(550));
        assertEquals( 2, layoutManaer.calculateCols(800));
    }

    private class FixedWidthJComponent extends JComponent {
        final int width;

        public FixedWidthJComponent(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

    }

    @Test
    void testCalcColsFromPanel() {
        final var fixedWidth400 = GuiActionRunner.execute(() -> new FixedWidthJComponent(400));
        final var layoutManager = new ThumbnailLayoutManager(fixedWidth400);
        assertEquals( 1, layoutManager.calculateCols());

        final var fixedWidth800 = GuiActionRunner.execute(() -> new FixedWidthJComponent(800));
        final var layoutManager2 = new ThumbnailLayoutManager(fixedWidth800);
        assertEquals( 2, layoutManager2.calculateCols());

    }


}