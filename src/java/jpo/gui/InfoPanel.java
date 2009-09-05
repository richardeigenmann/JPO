package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import javax.swing.*;
import java.util.logging.Logger;
import jpo.dataModel.Tools;

/*
InfoPanel.java: Widget that shows stuff about what is selected

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 *  The InfoPanel shows interesting stuff about what has been selected. Such as either the
 *  picture details or the information about the group. It works as a JScrollPane that swaps different
 *  JPanels into it's viewport.
 */
public class InfoPanel extends JTabbedPane {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( InfoPanel.class.getName() );

    /**
     * ThumbnailComponent to show the picture that has been selected
     */
    public Thumbnail thumbnail;

    /**
     *  A link to the statistics panel class.
     */
    public CollectionPropertiesJPanel statsJPanel = new CollectionPropertiesJPanel();

    /**
     *  Unknown Type of Node
     */
    public final JLabel unknownJPanel = new JLabel( "Unknown Node" );

 
    public JScrollPane statsScroller = new JScrollPane( statsJPanel );


    /**
     *   Constructor for the InfoPanel.
     *   methods that allow thumbnails to be displayed. <p>
     *
     */
    public InfoPanel() {
        Tools.checkEDT();
        statsJPanel.setBackground( Settings.JPO_BACKGROUND_COLOR );
        setMinimumSize( Settings.infoPanelMinimumSize );
        setPreferredSize( Settings.infoPanelPreferredSize );


        statsScroller.setWheelScrollingEnabled( true );
        //  set the amount by which the panel scrolls down when the user clicks the
        //  little down or up arrow in the scrollbar
        statsScroller.getVerticalScrollBar().setUnitIncrement( 20 );
        this.addTab( "Stats", statsScroller );
     
        thumbnail = new Thumbnail( Settings.thumbnailSize );
        this.addTab( "Thumbnail", thumbnail );
        this.addTab( "Word Cloud", new JPanel() );
    }
  
}
