package org.jpo.gui.swing;

import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Objects;

/*
Copyright (C) 2009-2021  Richard Eigenmann, Zürich, Switzerland
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

/**
 * This class does the widgets at the top of the ThumbnailPanel
 *
 * <br><img src="doc-files/ThumbnailPanelTitle.png" alt="ThumbnailPanelTitle visualisation">
 */
public class ThumbnailPanelTitle
        extends JPanel {

    /**
     * Constructor for the class which does the widgets at the top of the ThumbnailPanel
     */
    public ThumbnailPanelTitle() {
        super();
        initComponents();
    }


    /**
     * Allows the caller to get a handle on the showFilenamesButton so that a controller
     * can bind to the click event and act on it.
     *
     * @return The Button that should toggle whether to show filenames
     */
    public JButton getShowFilenamesButton() {
        return showFilenamesButton;
    }

    private final JButton showFilenamesButton = new JButton("\uf1c9");

    public JButton getShowTimestampButton() {
        return showTimestampButton;
    }

    private final JButton showTimestampButton = new JButton("\uf073");

    public JTextField getSearchField() {
        return searchField;
    }

    private final JTextField searchField = new JTextField("", 20);

    private final JButton searchButton = new JButton("\uf002");


    /**
     * JLabel for holding the thumbnail counts
     * */
    public final JLabel lblPage = new JLabel();

    /**
     *  the JLabel that holds the description of what is being shown in the TubnmailPanel
     */
    private final JLabel title = new JLabel();

    @TestOnly
    public String getTitle() {
        return title.getText();
    }



    /**
     * The panel with the navigation buttons
     */
    private final NavigationButtonPanel navigationButtonPanel = new NavigationButtonPanel();

    public NavigationButtonPanel getNavigationButtonPanel() {
        return navigationButtonPanel;
    }

    /**
     * Slider to control the size of the thumbnails
     */
    private final ResizeSlider resizeJSlider = new ResizeSlider();

    public void addResizeChangeListener(final ChangeListener cl) {
        resizeJSlider.addChangeListener(cl);
    }

    /**
     * Sets up the components. Must be on the EDT.
     */
    private void initComponents() {
        showFilenamesButton.setFont(FontAwesomeFont.getFontAwesomeRegular24());
        showFilenamesButton.setToolTipText("Show or Hide the filenames");
        showTimestampButton.setFont(FontAwesomeFont.getFontAwesomeRegular24());
        showTimestampButton.setToolTipText("Show or Hide the timestamps");

        searchField.setMinimumSize(new Dimension(100, 25));
        searchField.setMaximumSize(new Dimension(250, 25));
        hideSearchField();

        searchButton.setFont(FontAwesomeFont.getFontAwesomeSolid24());
        searchButton.setToolTipText("Click to enter search text.");
        searchButton.addActionListener(e -> {
            searchField.setVisible(!searchField.isVisible());
            searchField.getParent().validate();
            searchField.requestFocus();
            searchField.selectAll();
        });

        final var boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        setLayout(boxLayout);
        setBackground(Color.LIGHT_GRAY);
        add(Box.createRigidArea(new Dimension(5, 0)));

        add(navigationButtonPanel);

        lblPage.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(lblPage);
        add(title);
        add(resizeJSlider);
        add(Box.createHorizontalGlue());
        add(searchField);
        add(searchButton);
        add(showTimestampButton);
        add(showFilenamesButton);

        title.setFont(Settings.getTitleFont());
    }

    public class NavigationButtonPanel extends JPanel {

        /**
         * a button to navigate back to the first page
         **/
        private final JButton firstThumbnailsPageButton =
                new JButton(new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("icon_first.gif"))));
        /**
         * a button to navigate to the next page
         **/
        private final JButton nextThumbnailsPageButton =
                new JButton(new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("Forward24.gif"))));

        /**
         * a button to navigate to the last page
         **/
        private final JButton lastThumbnailsPageButton =
                new JButton(new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("icon_last.gif"))));

        /**
         * a button to navigate to the first page
         **/
        private final JButton previousThumbnailsPageButton =
                new JButton(new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("Back24.gif"))));

        public NavigationButtonPanel() {
            super();
            final var boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
            setLayout(boxLayout);
            setBackground(Color.LIGHT_GRAY);

            firstThumbnailsPageButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            firstThumbnailsPageButton.setPreferredSize(new Dimension(25, 25));
            firstThumbnailsPageButton.setVerticalAlignment(SwingConstants.CENTER);
            firstThumbnailsPageButton.setOpaque(false);
            firstThumbnailsPageButton.setEnabled(false);
            firstThumbnailsPageButton.setFocusPainted(false);
            firstThumbnailsPageButton.setToolTipText(Settings.getJpoResources().getString("ThumbnailToolTipPrevious"));

            previousThumbnailsPageButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            previousThumbnailsPageButton.setPreferredSize(new Dimension(25, 25));
            previousThumbnailsPageButton.setVerticalAlignment(SwingConstants.CENTER);
            previousThumbnailsPageButton.setOpaque(false);
            previousThumbnailsPageButton.setEnabled(false);
            previousThumbnailsPageButton.setFocusPainted(false);
            previousThumbnailsPageButton.setToolTipText(Settings.getJpoResources().getString("ThumbnailToolTipPrevious"));

            nextThumbnailsPageButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            nextThumbnailsPageButton.setPreferredSize(new Dimension(25, 25));
            nextThumbnailsPageButton.setVerticalAlignment(SwingConstants.CENTER);
            nextThumbnailsPageButton.setOpaque(false);
            nextThumbnailsPageButton.setEnabled(false);
            nextThumbnailsPageButton.setFocusPainted(false);
            nextThumbnailsPageButton.setToolTipText(Settings.getJpoResources().getString("ThumbnailToolTipNext"));

            lastThumbnailsPageButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
            lastThumbnailsPageButton.setPreferredSize(new Dimension(25, 25));
            lastThumbnailsPageButton.setVerticalAlignment(SwingConstants.CENTER);
            lastThumbnailsPageButton.setOpaque(false);
            lastThumbnailsPageButton.setEnabled(false);
            lastThumbnailsPageButton.setFocusPainted(false);
            lastThumbnailsPageButton.setToolTipText(Settings.getJpoResources().getString("ThumbnailToolTipNext"));

            add(firstThumbnailsPageButton);
            add(previousThumbnailsPageButton);
            add(nextThumbnailsPageButton);
            add(lastThumbnailsPageButton);
        }

        public JButton getFirstThumbnailsPageButton() {
            return firstThumbnailsPageButton;
        }

        public JButton getNextThumbnailsPageButton() {
            return nextThumbnailsPageButton;
        }

        public JButton getLastThumbnailsPageButton() {
            return lastThumbnailsPageButton;
        }

        public JButton getPreviousThumbnailsPageButton() {
            return previousThumbnailsPageButton;
        }

    }

    public void hideSearchField() {
        searchField.setVisible(false);
        searchField.revalidate();
    }


    /**
     *   Changes the title at the top of the page.<p>
     *   This method is EDT safe; it can be called from outside the EDT
     *   and it will detect this and submit itself on the EDT.
     *
     * @param    titleString    The text to be printed across the top
     *				of all columns. Usually this will be
     *				the name of the group
     */
    public void setTitle( final String titleString ) {
        final Runnable runnable = () -> title.setText(titleString);
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater(runnable );
        } else {
            runnable.run();
        }
    }
}
