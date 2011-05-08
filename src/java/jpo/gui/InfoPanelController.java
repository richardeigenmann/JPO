package jpo.gui;

import jpo.dataModel.ArrayListNavigator;
import jpo.TagCloud.TagClickListener;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;
import javax.swing.tree.*;
import jpo.dataModel.DescriptionWordMap;
import jpo.TagCloud.TagCloud;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;

/*
InfoPanelController.java:  The Controller for the Info Panel

Copyright (C) 2009-2011  Richard Eigenmann.
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
 *  Controller for the stuff the InfoPanel shows. Holds the reference to the InfoPanel widget
 */
public class InfoPanelController implements TagClickListener {

    /**
     *   Constructor for the InfoPanel.
     *   methods that allow thumbnails to be displayed. <p>
     *
     */
    public InfoPanelController() {
        Tools.checkEDT();
    }
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( InfoPanelController.class.getName() );
    private final NodeStatisticsPanel statsJPanel = new NodeStatisticsPanel();
    /**
     *  A millisecond delay for the polling of the thumbnailController queue and memory status
     */
    private static final int delay = 5000; //milliseconds
    /**
     *  A timer to fire off the refresh of the Thumbnail Queue display.
     *  Is only alive if the InfoPanel is showing the statistics panel.
     */
    private Timer statUpdateTimer = new Timer( delay, new ActionListener() {

        @Override
        public void actionPerformed( ActionEvent ae ) {
            statsJPanel.updateStats();
        }
    } );

    /**
     * Returns the InfoPanel Widget
     * @return The InfoPanel widget as a generic JComponent
     */
    public JComponent getInfoPanel() {
        return statsJPanel;
    }
    
    
    
    private final TagCloud tagCloud = new TagCloud();

    public JComponent getTagCloud() {
        return tagCloud;
    }
    
    
    /**
     *   Invoked to tell that we should display something
     *   @param nde 	The Group or Picture node to be displayed.
     */
    public void showInfo( DefaultMutableTreeNode nde ) {
        if ( !( nde instanceof SortableDefaultMutableTreeNode ) ) {
            LOGGER.fine( "The node is not a SortableDefaultMutableTreeNode. Don't know what to do. Skipping" );
            return; //ToDo do something smart when a query is shown.
        }
        final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) nde;
        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {
                if ( node == null ) {
                    //infoPanel.statsScroller.setViewportView( infoPanel.unknownJPanel );
                    statUpdateTimer.stop();
                    statUpdateTimer.start();  // updates the queue-count
                } else if ( node.getUserObject() instanceof PictureInfo ) {
                    //infoPanel.thumbnailController.setNode( new SingleNodeNavigator( node ), 0 );
                    statUpdateTimer.stop();
                } else {
                    // ToDo get this stuff off the event handler thread
                    LOGGER.fine( "Updating stats" );
                    statsJPanel.updateStats( node );
                    statUpdateTimer.start();  // updates the queue-count

                    tagCloud.setMaxWordsToShow( Settings.tagCloudWords );
                    dwm = new DescriptionWordMap( node );
                    tagCloud.setWordMap( dwm );
                    tagCloud.addTagClickListener( InfoPanelController.this );
                    tagCloud.showWords();
                }
            }
        } );
    }
    DescriptionWordMap dwm;

    @Override
    public void tagClicked( String key ) {
        HashSet<SortableDefaultMutableTreeNode> hs = dwm.getWordNodeMap().get( key );
        ArrayList<SortableDefaultMutableTreeNode> set = new ArrayList<SortableDefaultMutableTreeNode>( hs );
        ArrayListNavigator alb = new ArrayListNavigator( key, set );
        Jpo.showThumbnails( alb );
    }
}
