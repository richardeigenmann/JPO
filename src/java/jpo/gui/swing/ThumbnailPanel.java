package jpo.gui.swing;

import jpo.dataModel.Settings;
import jpo.*;
import javax.swing.*;
import jpo.dataModel.Tools;
import jpo.gui.ThumbnailLayoutManager;

/*
ThumbnailPanel.java:  This class shows thumbnails and their legends

Copyright (C) 2009  Richard Eigenmann, Zurich, Switzerland
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
 *  This class shows thumbnails and their legends
 */
public class ThumbnailPanel
        extends JScrollPane {

    /**
     *  Constructor. Make sure you are on the EDT before calling.
     */
    public ThumbnailPanel() {
        Tools.checkEDT();
        initComponents();
    }

    /**
     * the <code>JPanel</code> that is placed inside the <code>JScrollPane</code>
     * which holds the title and the ThumbnailComponents
     */
    private JPanel ThumbnailPane;

    /**
     *  Layout Manager for the Thumbnails
     */
    public final ThumbnailLayoutManager thumbnailLayout = new ThumbnailLayoutManager();


    private void initComponents() {
        ThumbnailPane = new JPanel();
        ThumbnailPane.setLayout( thumbnailLayout );


        ThumbnailPane.setBackground( Settings.JPO_BACKGROUND_COLOR );

        setMinimumSize( Settings.thumbnailJScrollPaneMinimumSize );
        setPreferredSize( Settings.thumbnailJScrollPanePreferredSize );

        setViewportView( ThumbnailPane );
        setWheelScrollingEnabled( true );
        setFocusable( true );

        //  set the amount by which the panel scrolls down when the user clicks the
        //  little down or up arrow in the scrollbar
        getVerticalScrollBar().setUnitIncrement( 80 );

    }

    /**
     * Returns the JPanel that holds the thumbnails.
     * @return The JPanel that holds the thumbnails
     */
    public JPanel getThumbnailPane() {
        return ThumbnailPane;
    }
}
