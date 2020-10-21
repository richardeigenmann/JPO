package org.jpo.gui.swing;

/*
 ThumbnailDescriptionPanel.java:  a Swing view for the descriptions of a thumbnail

 Copyright (C) 2020  Richard Eigenmann, Zürich, Switzerland
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


import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ThumbnailDescriptionPanel extends JPanel {

    /**
     * Font to be used for Large Texts:
     */
    private static final Font LARGE_FONT = Font.decode(Settings.getJpoResources().getString("ThumbnailDescriptionJPanelLargeFont"));
    /**
     * Font to be used for small texts:
     */
    private static final Font SMALL_FONT = Font.decode(Settings.getJpoResources().getString("ThumbnailDescriptionJPanelSmallFont"));
    final JButton categoryMenuPopupButton = new JButton("\uf0fe");
    /**
     * This object holds the description
     */
    private final JTextArea pictureDescriptionJTA = new JTextArea();
    /**
     * This JScrollPane holds the JTextArea pictureDescriptionJTA so that it can
     * have multiple lines of text if this is required.
     */
    private final JScrollPane pictureDescriptionJSP = new JScrollPane(pictureDescriptionJTA,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    /**
     * create a dumbCaret object which prevents undesirable scrolling behaviour
     *
     * @see NonFocussedCaret
     */
    private final NonFocussedCaret dumbCaret = new NonFocussedCaret();
    /**
     * The location of the image file
     */
    private final JTextField highresLocationJTextField = new JTextField();
    private final JPanel categoriesJPanel = new JPanel();

    /**
     * The factor which is multiplied with the ThumbnailDescription to determine
     * how large it is shown.
     */
    private float thumbnailSizeFactor = 1;
    private final JScrollPane categoriesJSP = new JScrollPane(categoriesJPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER) {

        @Override
        public Dimension getMaximumSize() {
            // clamp the maximum width to the scaled tumbnail width
            final Dimension superMaximumSize = super.getMaximumSize();
            superMaximumSize.width = (int) (Settings.getThumbnailSize() * thumbnailSizeFactor);
            return superMaximumSize;
        }
    };

    public ThumbnailDescriptionPanel() {
        initComponents();
    }

    public static Font getLargeFont() {
        return LARGE_FONT;
    }

    public static Font getSmallFont() {
        return SMALL_FONT;
    }

    private static int getTargetHeight(final JScrollPane jScrollPane) {
        final Dimension textAreaSize = jScrollPane.getComponent(0).getPreferredSize();

        int targetHeight;
        if (textAreaSize.height < jScrollPane.getMinimumSize().height) {
            targetHeight = jScrollPane.getMinimumSize().height;
        } else if (textAreaSize.height > jScrollPane.getMaximumSize().height) {
            targetHeight = jScrollPane.getMaximumSize().height;
        } else {
            targetHeight = (((textAreaSize.height / 30) + 1) * 30);
        }
        return targetHeight;
    }

    public JScrollPane getPictureDescriptionJSP() {
        return pictureDescriptionJSP;
    }

    public JTextField getHighresLocationJTextField() {
        return highresLocationJTextField;
    }

    public JButton getCategoryMenuPopupButton() {
        return categoryMenuPopupButton;
    }

    private void initComponents() {
        this.setBackground(Color.WHITE);
        this.setLayout(new MigLayout());

        highresLocationJTextField.setEditable(false);
        highresLocationJTextField.setBorder(BorderFactory.createEmptyBorder());
        this.add(highresLocationJTextField, "hidemode 2, wrap");
        highresLocationJTextField.setVisible(false);

        pictureDescriptionJTA.setWrapStyleWord(true);
        pictureDescriptionJTA.setLineWrap(true);
        pictureDescriptionJTA.setEditable(true);
        pictureDescriptionJTA.setCaret(dumbCaret);

        this.add(categoriesJSP, "hidemode 2, wrap");
        categoriesJSP.setMinimumSize(new Dimension(Settings.getThumbnailSize(), 50));
        categoriesJSP.setMaximumSize(new Dimension(Settings.getThumbnailSize(), 250));

        categoriesJPanel.setLayout(new WrapLayout());
        categoryMenuPopupButton.setFont(FontAwesomeFont.getFontAwesomeRegular18());
        categoryMenuPopupButton.setBorder(new EmptyBorder(0, 5, 0, 0));
        categoryMenuPopupButton.setContentAreaFilled(false);
        categoryMenuPopupButton.setFocusPainted(false);
        categoryMenuPopupButton.setOpaque(false);

        // this is a bit of a cludge to get the JTextArea to grow in height as text is
        // being entered. Annoyingly the getPreferredSize of the JTextArea doesn't immediately
        // reflect the new size and the setTextArea doesn't immediately adjust. But subsequent
        // inserts retrigger this and things catch up.
        getPictureDescriptionJTA().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setTextAreaSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setTextAreaSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setTextAreaSize();
            }
        });

        // it is the Scrollpane you must constrain, not the TextArea
        pictureDescriptionJSP.setMinimumSize(new Dimension(Settings.getThumbnailSize(), 25));
        pictureDescriptionJSP.setMaximumSize(new Dimension(Settings.getThumbnailSize(), 250));

        this.add(pictureDescriptionJSP);
    }

    public JTextArea getPictureDescriptionJTA() {
        return pictureDescriptionJTA;
    }

    /**
     * Alters the display to show the node is "selected" or not.
     * This method is EDT safe.
     *
     * @param selected true is the node should be shown as "selected", false if not
     */
    public void showAsSelected(final boolean selected) {
        if (selected) {
            showAsSelected();
        } else {
            showAsUnselected();
        }
    }

    /**
     * changes the background color so that the user sees that the thumbnail is part of the
     * selection.<p>
     * This method is EDT safe.
     */
    public void showAsSelected() {
        final Runnable r = () -> getPictureDescriptionJTA().setBackground(Settings.getSelectedColorText());
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Changes the color so that the user sees that the thumbnail is not part of
     * the selection<p>
     * This method is EDT safe
     */
    public void showAsUnselected() {
        final Runnable runnable = () -> getPictureDescriptionJTA().setBackground(Settings.getUnselectedColor());
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Gets the description of the ThumbnailDescriptionPanel
     *
     * @return the description
     */
    public String getDescription() {
        return getPictureDescriptionJTA().getText();
    }

    /**
     * Sets the description of the ThumbnailDescriptionPanel.
     * It calls setTextAreaSize to resize the box according to the amount of text.
     * This method is EDT safe
     *
     * @param newDescription the new Descriptions
     */
    public void setDescription(final String newDescription) {
        final Runnable runnable = () -> {
            getPictureDescriptionJTA().setText(newDescription);
            setTextAreaSize();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    public void clearCategories() {
        categoriesJPanel.removeAll();
        categoriesJSP.revalidate();
        categoriesJSP.repaint();
    }

    /**
     * Adds a Category visual to the the categoriesJPanel and returns the removeButton JButton to the called who
     * should then wire up the click event with the removal action
     *
     * @param categoryDescription The category to show
     * @return the remove Button
     */
    public AbstractButton addCategory(final String categoryDescription) {
        Tools.checkEDT();
        final JPanel categoryPanel = new JPanel();
        final JLabel categoryLabel = new JLabel(categoryDescription) {

            @Override
            protected void paintComponent(Graphics g) {
                Dimension arcs = new Dimension(15, 15);
                int width = getWidth();
                int height = getHeight();
                Graphics2D graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                //Draws the rounded opaque panel with borders.
                graphics.setColor(getBackground());
                graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint background
                graphics.setColor(getForeground());
                graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint border
                super.paintComponent(g);
            }
        };
        final Border border = categoryLabel.getBorder();
        final Border margin = new EmptyBorder(6, 6, 6, 6);
        categoryLabel.setBorder(new CompoundBorder(border, margin));

        categoryPanel.add(categoryLabel);
        final JButton removeButton = new JButton("\uf057");
        removeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        removeButton.setContentAreaFilled(false);
        removeButton.setFocusPainted(false);
        removeButton.setOpaque(false);
        removeButton.setFont(FontAwesomeFont.getFontAwesomeRegular18());
        categoryPanel.add(removeButton);
        categoriesJPanel.add(categoryPanel);
        this.revalidate(); // makes the JScrollPanel reevaluate it's size so it grows
        return removeButton;
    }

    @TestOnly
    public void removeFirstCategory() {
        categoriesJPanel.remove(0);
        this.revalidate();
        categoriesJSP.repaint();
    }


    public void addCategoryMenu() {
        Tools.checkEDT();
        categoriesJPanel.add(categoryMenuPopupButton);
        categoriesJPanel.revalidate();
    }

    /**
     * Call this method with a factor between 0 and 1 to request the thumbnail description to shrink or expand along
     * with the size of the picture above it.
     * This method calls setTextAreaSize to do the resizing right away.
     * It is EDT safe.
     *
     * @param thumbnailSizeFactor the size factor with which to multiply
     */
    public void setThumbnailSizeFactor(final float thumbnailSizeFactor) {
        this.thumbnailSizeFactor = thumbnailSizeFactor;
        setTextAreaSize();
        categoriesJPanel.revalidate();
    }

    /**
     * sets the size of the TextArea.
     * This method is EDT safe.
     */
    public void setTextAreaSize() {
        final Runnable runnable = () -> {
            int targetWidth = (int) (Settings.getThumbnailSize() * thumbnailSizeFactor);
            int descriptionTargetHeight = getTargetHeight(getPictureDescriptionJSP());
            final Dimension scrollPaneSize = getPictureDescriptionJSP().getPreferredSize();
            if ((descriptionTargetHeight != scrollPaneSize.height) || (targetWidth != scrollPaneSize.width)) {
                getPictureDescriptionJSP().setPreferredSize(new Dimension(targetWidth, descriptionTargetHeight));
                getPictureDescriptionJSP().setMaximumSize(new Dimension(targetWidth, 250));
                this.revalidate();
            }

            if (highresLocationJTextField.getWidth() != targetWidth) {
                highresLocationJTextField.setPreferredSize(new Dimension(targetWidth, 30));
                highresLocationJTextField.setMaximumSize(new Dimension(targetWidth, 30));
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    /**
     * Turns on the filename display or turns it off
     *
     * @param showFilename true to turn on, false to turn off
     */
    public void showFilename(boolean showFilename) {
        final Runnable runnable = () -> {
            highresLocationJTextField.setVisible((showFilename));
            this.revalidate();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }

    }


}