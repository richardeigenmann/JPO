package org.jpo.testground;


/*
 Copyright (C) 2020-2022 Richard Eigenmann.
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
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;
import org.jpo.gui.ThumbnailController;
import org.jpo.gui.swing.ResizeSlider;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A little GUI to experiment with the ThumbnailDescriptions
 */
public class ThumbnailTester {

    /**
     * An entry point for standalone screen size testing.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EventBusInitializer.registerEventHandlers();
        Settings.loadSettings();
        Settings.setPictureCollection(new PictureCollection());
        JpoEventBus.getInstance().post(new StartNewCollectionRequest());
        JpoEventBus.getInstance().post(new StartThumbnailCreationDaemonRequest());
        try {
            SwingUtilities.invokeAndWait(ThumbnailTester::new);
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(ThumbnailTester.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Constructor for the test window
     */
    public ThumbnailTester() {
        final var thumbnailController = new ThumbnailController(350);

        final var SAMSUNG_S4_IMAGE = "testimage.jpg";
        var imageFile = new File("nofile");
        try {
            imageFile = new File(ThumbnailTester.class.getClassLoader().getResource(SAMSUNG_S4_IMAGE).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        var rootNode = Settings.getPictureCollection().getRootNode();

        final File[] files = {imageFile};
        JpoEventBus.getInstance().post(
                new PictureAdderRequest(rootNode,
                files, false, false, false,  new ArrayList<Integer>()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var pictureNode = rootNode.getChildPictureNodes(true).get(0);
        final var singleNodeNavigator = new SingleNodeNavigator(pictureNode);
        thumbnailController.setNode(singleNodeNavigator, 0);


        final var jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setPreferredSize(new Dimension(600, 500));

        final var frame = new JFrame("ThumbnailTester");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


        final var buttonPanel = new JPanel();
        buttonPanel.setLayout(new MigLayout());
        final var showDecoratedButton = new JButton("Decorate");
        showDecoratedButton.addActionListener((ActionEvent e) -> thumbnailController.setDecorateThumbnails(true));
        buttonPanel.add(showDecoratedButton);

        final var showUndecoratedButton = new JButton("Undecorate");
        showUndecoratedButton.addActionListener((ActionEvent e) -> thumbnailController.setDecorateThumbnails(false));
        buttonPanel.add(showUndecoratedButton, "wrap");

        final var showSelectedButton = new JButton("Selected");
        showSelectedButton.addActionListener((ActionEvent e) -> Settings.getPictureCollection().addToSelectedNodes(pictureNode));
        buttonPanel.add(showSelectedButton);

        final var showUnselectedButton = new JButton("Unselected");
        showUnselectedButton.addActionListener((ActionEvent e) -> Settings.getPictureCollection().clearSelection());
        buttonPanel.add(showUnselectedButton, "wrap");

        final var showAsOfflineButton = new JButton("Offline");
        showAsOfflineButton.addActionListener((ActionEvent e) -> thumbnailController.getThumbnail().drawOfflineIcon(true));
        buttonPanel.add(showAsOfflineButton);

        final var showAsOnlineButton = new JButton("Online");
        showAsOnlineButton.addActionListener((ActionEvent e) -> thumbnailController.getThumbnail().drawOfflineIcon(false));
        buttonPanel.add(showAsOnlineButton, "wrap");

        final var showMailSelectedButton = new JButton("Mail Selected");
        showMailSelectedButton.addActionListener((ActionEvent e) -> {
            Settings.getPictureCollection().addToMailSelection(pictureNode);
            thumbnailController.determineMailSelectionStatus();
        });
        buttonPanel.add(showMailSelectedButton);

        final var showMailUnselectedButton = new JButton("Mail Unselected");
        showMailUnselectedButton.addActionListener((ActionEvent e) -> {
            Settings.getPictureCollection().clearMailSelection();
            thumbnailController.determineMailSelectionStatus();
        });
        buttonPanel.add(showMailUnselectedButton, "wrap");

        final var showTimestamp = new JButton("Show Timestamp");
        showTimestamp.addActionListener((ActionEvent e) -> {
            var pictureInfo = (PictureInfo) pictureNode.getUserObject();
            thumbnailController.getThumbnail().setTimestamp(pictureInfo.getFormattedCreationTimeForTimestamp());
            thumbnailController.getThumbnail().repaint();
        });
        buttonPanel.add(showTimestamp);

        final var hideTimestamp = new JButton("Hide Timestamp");
        hideTimestamp.addActionListener((ActionEvent e) -> {
            thumbnailController.getThumbnail().setTimestamp("");
            thumbnailController.getThumbnail().repaint();
        });
        buttonPanel.add(hideTimestamp, "wrap");

        final var resizeJSlider = new ResizeSlider();
        resizeJSlider.addChangeListener((ChangeEvent e) -> {
            JSlider source = (JSlider) e.getSource();
            float thumbnailSizeFactor = (float) source.getValue() / ResizeSlider.THUMBNAILSIZE_SLIDER_MAX;
            thumbnailController.setFactor(thumbnailSizeFactor);
            thumbnailController.getThumbnail().revalidate();
        });
        buttonPanel.add(resizeJSlider, "spanx 2");


        jPanel.add(buttonPanel, BorderLayout.NORTH);

        final var centerPanel = new JPanel();
        centerPanel.add(thumbnailController.getThumbnail());
        jPanel.add(centerPanel, BorderLayout.CENTER);

        frame.getContentPane().add(jPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }
}
