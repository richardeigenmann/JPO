package org.jpo.gui.swing;

import org.jpo.dataModel.Settings;
import org.jpo.dataModel.Tools;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.Objects;

/*
Copyright (C) 2009-2019  Richard Eigenmann, Zürich, Switzerland
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
 *  This class does the widgets at the top of the ThumbnailPanel
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
     *  a button to navigate back to the first page
     **/
    public final JButton firstThumbnailsPageButton =
            //new JButton( new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("org/jpo/images/icon_first.gif"))) );
            new JButton( new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("icon_first.gif"))) );
    /**
     *  a button to navigate to the next page
     **/
    public final JButton nextThumbnailsPageButton =
            new JButton( new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("Forward24.gif"))) );

    /**
     *  a button to navigate to the last page
     **/
    public final JButton lastThumbnailsPageButton =
            new JButton( new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("icon_last.gif"))) );

    /**
     *  a button to navigate to the first page
     **/
    public final JButton previousThumbnailsPageButton =
            new JButton( new ImageIcon(Objects.requireNonNull(ThumbnailPanelTitle.class.getClassLoader().getResource("Back24.gif"))) );

    /**
     * JLabel for holding the thumbnail counts
     * */
    public final JLabel lblPage = new JLabel();

    /**
     *  the JLabel that holds the description of what is being shown in the TubnmailPanel
     */
    private final JLabel title = new JLabel();

    /**
     *  The largest size for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_MIN = 5;

    /**
     *  The smallest size for the thumbnail slider
     */
    public static final int THUMBNAILSIZE_MAX = 20;

    /**
     *  The starting position for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_INIT = 20;

    /**
     *   Slider to control the size of the thumbnails
     */
    public final JSlider resizeJSlider = new JSlider( JSlider.HORIZONTAL,
            THUMBNAILSIZE_MIN, THUMBNAILSIZE_MAX, THUMBNAILSIZE_INIT );


    /**
     * Sets up the components. Must be on the EDT.
     */
    private void initComponents() {
        Tools.checkEDT();
        firstThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        firstThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        firstThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        firstThumbnailsPageButton.setOpaque( false );
        firstThumbnailsPageButton.setEnabled( false );
        firstThumbnailsPageButton.setFocusPainted( false );
        firstThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipPrevious" ) );

        previousThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        previousThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        previousThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        previousThumbnailsPageButton.setOpaque( false );
        previousThumbnailsPageButton.setEnabled( false );
        previousThumbnailsPageButton.setFocusPainted( false );
        previousThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipPrevious" ) );

        nextThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        nextThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        nextThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        nextThumbnailsPageButton.setOpaque( false );
        nextThumbnailsPageButton.setEnabled( false );
        nextThumbnailsPageButton.setFocusPainted( false );
        nextThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipNext" ) );

        lastThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        lastThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        lastThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        lastThumbnailsPageButton.setOpaque( false );
        lastThumbnailsPageButton.setEnabled( false );
        lastThumbnailsPageButton.setFocusPainted( false );
        lastThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipNext" ) );

        BoxLayout bl = new BoxLayout( this, BoxLayout.X_AXIS );
        setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );	// JA
        setLayout( bl );
        setBackground( Color.LIGHT_GRAY );
        add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        add( firstThumbnailsPageButton );
        add( previousThumbnailsPageButton );
        add( nextThumbnailsPageButton );
        add( lastThumbnailsPageButton );
        lblPage.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );					// JA
        add( lblPage );
        add( title );

        resizeJSlider.setSnapToTicks( false );
        resizeJSlider.setMaximumSize( new Dimension( 150, 40 ) );
        resizeJSlider.setMajorTickSpacing( 4 );
        resizeJSlider.setMinorTickSpacing( 2 );
        resizeJSlider.setPaintTicks( true );
        resizeJSlider.setPaintLabels( false );
        add( resizeJSlider );

        title.setFont( Settings.titleFont );
    }


    /**
     *   Changes the title at the top of the page.<p>
     *   This method is EDT safe; it can be called from outside the EDT
     *   and it will detect this and submit itself on the EDT.
     *
     * @param	titleString	The text to be printed across the top
     *				of all columns. Usually this will be
     *				the name of the group
     */
    public void setTitle( final String titleString ) {
        Runnable runnable = () -> title.setText( titleString );
        if ( ! SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater(runnable );
        } else {
            runnable.run();
        }

    }
}
