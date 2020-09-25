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
import org.jpo.datamodel.Settings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThumbnailDescriptionPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(ThumbnailDescriptionPanel.class.getName());

    /**
     * This object holds the description
     */
    private final JTextArea pictureDescriptionJTA = new JTextArea();

    public JScrollPane getPictureDescriptionJSP() {
        return pictureDescriptionJSP;
    }

    /**
     * This JScrollPane holds the JTextArea pictureDescriptionJTA so that it can
     * have multiple lines of text if this is required.
     */
    private final JScrollPane pictureDescriptionJSP = new JScrollPane(pictureDescriptionJTA,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    public JTextField getHighresLocationJTextField() {
        return highresLocationJTextField;
    }

    /**
     * create a dumbCaret object which prevents undesirable scrolling behaviour
     *
     * @see NonFocussedCaret
     */
    private final NonFocussedCaret dumbCaret = new NonFocussedCaret();

    /**
     * The location of the image file
     */
    private final JTextField highresLocationJTextField = new JTextField() {
        @Override
        public boolean isVisible() {
            LOGGER.log(Level.INFO, "highreslocation text field is querying isVisible");
            return super.isVisible();
        }
    };

    private final JTextArea categoriesJTA = new JTextArea();

    private JPanel categoriesJPanel = new JPanel();

    private final JScrollPane categoriesJSP = new JScrollPane(categoriesJPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    public JButton getCategoryMenuPopupButton() {
        return categoryMenuPopupButton;
    }

    final JButton categoryMenuPopupButton = new JButton("\uf0fe");

    public static Font getLargeFont() {
        return LARGE_FONT;
    }

    /**
     * Font to be used for Large Texts:
     */
    private static final Font LARGE_FONT = Font.decode(Settings.getJpoResources().getString("ThumbnailDescriptionJPanelLargeFont"));

    public static Font getSmallFont() {
        return SMALL_FONT;
    }

    /**
     * Font to be used for small texts:
     */
    private static final Font SMALL_FONT = Font.decode(Settings.getJpoResources().getString("ThumbnailDescriptionJPanelSmallFont"));

    public ThumbnailDescriptionPanel() {
        initComponents();
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

        categoriesJTA.setWrapStyleWord(true);
        categoriesJTA.setLineWrap(true);
        categoriesJTA.setEditable(false);
        categoriesJTA.setCaret(dumbCaret);

        this.add(categoriesJSP, "hidemode 2, wrap");
        categoriesJSP.setMinimumSize(new Dimension(Settings.getThumbnailSize(), 50));
        categoriesJSP.setMaximumSize(new Dimension(Settings.getThumbnailSize(), 250));

        categoriesJPanel.setLayout(new WrapLayout());
        categoryMenuPopupButton.setFont(FontAwesomeFont.getFontAwesomeRegular18());

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
     * Sets the description of the ThumbnailDescriptionPanel.
     * It calls setTextAreaSize to resize the box according to the amount of text.
     * This method is EDT safe
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

    /**
     * Gets the description of the ThumbnailDescriptionPanel
     *
     * @return the description
     */
    public String getDescription() {
        return getPictureDescriptionJTA().getText();
    }

    @Deprecated
    public void setCategories(final String newCategories) {
        final Runnable runnable = () -> {
            categoriesJTA.setText(newCategories);
            categoriesJSP.setVisible(true);
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
    }

    public AbstractButton addCategory(String categoryDescription) {
        final JPanel categoryLabel = new JPanel();
        categoryLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        categoryLabel.add(new JLabel(categoryDescription));
        final JButton removeButton = new JButton("\uf057");
        removeButton.setFont(FontAwesomeFont.getFontAwesomeRegular18());
        categoryLabel.add(removeButton);
        categoriesJPanel.add(categoryLabel);
        return removeButton;
    }

    public void addCategoryMenu() {
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
    }

    /**
     * The factor which is multiplied with the ThumbnailDescription to determine
     * how large it is shown.
     */
    private float thumbnailSizeFactor = 1;


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

            int categoriesPanelTargetHeight = getTargetHeight(categoriesJSP);
            final Dimension categoriesSize = categoriesJSP.getPreferredSize();
            if ((categoriesPanelTargetHeight != categoriesSize.height) || (targetWidth != categoriesSize.width)) {
                categoriesJSP.setPreferredSize(new Dimension(targetWidth, descriptionTargetHeight));
                categoriesJSP.setMaximumSize(new Dimension(targetWidth, 250));
                this.revalidate();
            }

            //categoriesJpanel.setSize(new Dimension(300, 1));
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
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