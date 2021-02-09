package org.jpo.testground;

import org.jpo.datamodel.Settings;
import org.jpo.gui.swing.NonFocussedCaret;
import org.jpo.gui.swing.ResizableJFrame;
import org.jpo.gui.swing.ScreenHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.swing.ResizableJFrame.WindowSize.*;


/*
 Copyright (C) 2002-2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * A class to test the ResizableJFrame. This can be problematic because
 * different users are likely to have different screen configurations.
 */
public class ResizableJFrameTest {

    /**
     * Constructor for the test window
     */
    public ResizableJFrameTest() {

        final JPanel bluePanel = new JPanel();
        bluePanel.setBackground(Color.CYAN);
        bluePanel.setMaximumSize(new Dimension(200, 200));
        bluePanel.setPreferredSize(new Dimension(250, 250));

        final ResizableJFrame resizableJFrame = new ResizableJFrame("Blue Window", bluePanel, WINDOW_BOTTOM_RIGHT);
        resizableJFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JPanel buttonPanel = new JPanel();
        final JButton fullScreen = new JButton("FullScreen");
        fullScreen.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_FULLSCREEN));
        buttonPanel.add(fullScreen);

        final JButton custom1Button = new JButton("Custom 1");
        custom1Button.addActionListener((ActionEvent e) -> {
            resizableJFrame.switchWindowMode(WINDOW_CUSTOM_SIZE);
            resizableJFrame.setBounds(new Rectangle(400, 200, 500, 400));
        });
        buttonPanel.add(custom1Button);

        final JButton custom2Button = new JButton("Custom 2");
        custom2Button.addActionListener((ActionEvent e) -> {
            resizableJFrame.switchWindowMode(WINDOW_CUSTOM_SIZE);
            resizableJFrame.setBounds(new Rectangle(480, 270, 400, 300));
        });
        buttonPanel.add(custom2Button);

        final JButton custom3Button = new JButton("Custom 3");
        custom3Button.addActionListener((ActionEvent e) -> {
            resizableJFrame.switchWindowMode(WINDOW_CUSTOM_SIZE);
            resizableJFrame.setBounds(new Rectangle(670, 600, 470, 450));
        });
        buttonPanel.add(custom3Button);


        final JButton leftSize = new JButton("Left");
        leftSize.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_LEFT));
        buttonPanel.add(leftSize);

        final JButton topLeftSize = new JButton("Top Left");
        topLeftSize.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_TOP_LEFT));
        buttonPanel.add(topLeftSize);

        final JButton bottomLeftSize = new JButton("Bottom Left");
        bottomLeftSize.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_BOTTOM_LEFT));
        buttonPanel.add(bottomLeftSize);

        final JButton rightSize = new JButton("Right");
        rightSize.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_RIGHT));
        buttonPanel.add(rightSize);

        final JButton topRightSize = new JButton("Top Right");
        topRightSize.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_TOP_RIGHT));
        buttonPanel.add(topRightSize);

        final JButton bottomRightSize = new JButton("Bottom Right");
        bottomRightSize.addActionListener((ActionEvent e) -> resizableJFrame.switchWindowMode(WINDOW_BOTTOM_RIGHT));
        buttonPanel.add(bottomRightSize);

        final JButton decorateButton = new JButton("Decorate");
        decorateButton.addActionListener((ActionEvent e) -> resizableJFrame.showWindowDecorations(true));
        buttonPanel.add(decorateButton);

        final JButton undecorateButton = new JButton("Undecorate");
        undecorateButton.addActionListener((ActionEvent e) -> resizableJFrame.showWindowDecorations(false));
        buttonPanel.add(undecorateButton);

        final JTextArea jta = new JTextArea(20, 80);
        jta.setCaret(new NonFocussedCaret());
        jta.setText(ScreenHelper.explainGraphicsEnvironment().toString());

        final JButton refresh = new JButton("Refresh");
        refresh.addActionListener((ActionEvent e) -> jta.setText(ScreenHelper.explainGraphicsEnvironment().toString()));
        buttonPanel.add(refresh);


        final JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(buttonPanel, BorderLayout.NORTH);

        final JScrollPane jsp = new JScrollPane(jta);
        p.add(jsp, BorderLayout.CENTER);


        final JFrame controlFrame = new JFrame("ResizableJFrameTest");
        controlFrame.setAlwaysOnTop(true);
        controlFrame.setLayout(new BorderLayout());
        controlFrame.add("Center", p);
        controlFrame.pack();
        controlFrame.setVisible(true);
        controlFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        controlFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                resizableJFrame.dispose();
            }
        });
    }

    /**
     * An entry point for standalone screen size testing.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Settings.loadSettings();
        try {
            SwingUtilities.invokeAndWait(ResizableJFrameTest::new
            );
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(ResizableJFrameTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }
}
