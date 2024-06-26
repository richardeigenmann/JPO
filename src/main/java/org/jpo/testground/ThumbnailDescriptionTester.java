package org.jpo.testground;


/*
 Copyright (C) 2020-2024 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.gui.swing.CategoryButton;
import org.jpo.gui.swing.ResizeSlider;
import org.jpo.gui.swing.ThumbnailDescriptionPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A little GUI to experiment with the ThumbnailDescriptions
 */
public class ThumbnailDescriptionTester {

    /**
     * An entry point for standalone screen size testing.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Settings.loadSettings();
        try {
            SwingUtilities.invokeAndWait(ThumbnailDescriptionTester::new
            );
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(ThumbnailDescriptionTester.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Constructor for the test window
     */
    public ThumbnailDescriptionTester() {
        final var jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setPreferredSize(new Dimension(600, 500));

        final var frame = new JFrame("ThumbnailDescriptionTester");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final var panel = new ThumbnailDescriptionPanel();

        final var buttonPanel = new JPanel();
        buttonPanel.setLayout(new MigLayout());
        final var showAsSelectedButton = new JButton("call showAsSelected");
        showAsSelectedButton.addActionListener((ActionEvent e) -> panel.showAsSelected());
        buttonPanel.add(showAsSelectedButton);

        final var showAsUnselectedButton = new JButton("call showAsUnselected");
        showAsUnselectedButton.addActionListener((ActionEvent e) -> panel.showAsUnselected());
        buttonPanel.add(showAsUnselectedButton, "wrap");

        final var showFilenameButton = new JButton("show filename");
        showFilenameButton.addActionListener((ActionEvent e) -> panel.showFilename(true));
        buttonPanel.add(showFilenameButton);

        final var setFilenameButton = new JButton("set filename");
        setFilenameButton.addActionListener((ActionEvent e) -> panel.getHighresLocationJTextField().setText("/dir/dir/image.jpg"));
        buttonPanel.add(setFilenameButton);

        final var hideFilenameButton = new JButton("hide filename");
        hideFilenameButton.addActionListener((ActionEvent e) -> panel.showFilename(false));
        buttonPanel.add(hideFilenameButton, "wrap");

        final var addCategoryButton = new JButton("add Category");
        addCategoryButton.addActionListener((ActionEvent e) -> {
            final var categoryButton = new CategoryButton("Category");
            panel.addToCategoriesJPPanel(categoryButton);
            categoryButton.addRemovalListener(e1 -> panel.removeFirstCategory());
            categoryButton.addClickListener(e1 -> panel.setDescription("Category was clicked"));
        });
        buttonPanel.add(addCategoryButton);

        final var addCategoryMenuButton = new JButton("add Category Menu");
        addCategoryMenuButton.addActionListener((ActionEvent e) -> panel.addCategoryMenu());
        buttonPanel.add(addCategoryMenuButton);

        final var setDescription1Button = new JButton("Set description 1");
        setDescription1Button.addActionListener(e -> panel.setDescription("This is a description text"));
        buttonPanel.add(setDescription1Button);

        final var setDescription2Button = new JButton("Set description 2");
        setDescription2Button.addActionListener((ActionEvent e) -> panel.setDescription("This is a different description"));
        buttonPanel.add(setDescription2Button);

        final var setDescription3Button = new JButton("Set long description");
        setDescription3Button.addActionListener((ActionEvent e) -> panel.setDescription("A very long description\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce et libero accumsan, molestie velit sed, tincidunt erat. Vivamus a justo finibus, suscipit tellus sed, imperdiet quam. Integer consequat porttitor scelerisque. Aenean pulvinar tortor vitae est euismod dapibus. Fusce efficitur tortor ac tempor ultricies. Phasellus rhoncus placerat dui et convallis. Vivamus sit amet neque sit amet tortor ornare ultricies nec suscipit magna. Fusce est urna, fermentum ac metus vitae, maximus tempor ex. Duis scelerisque blandit tempor. Phasellus sagittis volutpat dolor, in mollis eros finibus in. Donec efficitur pellentesque mauris sit amet elementum. Duis mollis ex ut iaculis pretium. In hac habitasse platea dictumst."));
        buttonPanel.add(setDescription3Button, "wrap");

        final var callSetTextAreaSizeButton = new JButton("call setTextAreaSize");
        callSetTextAreaSizeButton.addActionListener((ActionEvent e) ->  panel.setTextAreaSize());
        buttonPanel.add(callSetTextAreaSizeButton);

        /*
           The largest size for the thumbnail slider
         */
        final var THUMBNAILSIZE_SLIDER_MIN = 5;

        final var THUMBNAILSIZE_SLIDER_MAX = 20;

        final var THUMBNAILSIZE_SLIDER_INIT = 20;

        final var resizeJSlider = new JSlider(SwingConstants.HORIZONTAL,
                THUMBNAILSIZE_SLIDER_MIN, THUMBNAILSIZE_SLIDER_MAX, THUMBNAILSIZE_SLIDER_INIT);
        resizeJSlider.setSnapToTicks(false);
        resizeJSlider.setMaximumSize(new Dimension(150, 40));
        resizeJSlider.setMajorTickSpacing(4);
        resizeJSlider.setMinorTickSpacing(2);
        resizeJSlider.setPaintTicks(true);
        resizeJSlider.setPaintLabels(false);
        resizeJSlider.addChangeListener((ChangeEvent e) -> {
            JSlider source = (JSlider) e.getSource();
            float thumbnailSizeFactor = (float) source.getValue() / ResizeSlider.THUMBNAILSIZE_SLIDER_MAX;
            panel.setThumbnailSizeFactor(thumbnailSizeFactor);
            panel.setTextAreaSize();
        });
        buttonPanel.add(resizeJSlider);

        jPanel.add(buttonPanel, BorderLayout.NORTH);

        panel.setTextAreaSize();
        jPanel.add(panel, BorderLayout.CENTER);

        frame.getContentPane().add(jPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }
}
