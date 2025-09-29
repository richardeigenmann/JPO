package org.jpo.gui;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationJMenuBarTest {

    @Test
    void constructMenu() {
        try {
            var menuBarRef = new Object[]{null};
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    menuBarRef[0] = new ApplicationJMenuBar();
                }
            });
            assertNotNull(menuBarRef[0]);

        } catch (InterruptedException | InvocationTargetException e) {
            fail(e);
        }
    }
}