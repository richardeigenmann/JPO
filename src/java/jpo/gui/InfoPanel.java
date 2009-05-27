package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfo;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import javax.swing.tree.*;

/*
InfoPanel.java:  a JScrollPane that shows information after selection events.

Copyright (C) 2002-2009  Richard Eigenmann.
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
     * ThumbnailComponent to show the picture that has been selected
     */
    private Thumbnail thumbnail;
    /**
     *  A link to the statistics panel class.
     */
    private final CollectionPropertiesJPanel statsJPanel = new CollectionPropertiesJPanel();
    /**
     *  Unknown Type of Node
     */
    private final JLabel unknownJPanel = new JLabel("Unknown Node");
    /**
     *  A millisecond delay for the polling of the thumbnail queue and memory status
     */
    private static final int delay = 800; //milliseconds
    /**
     *  A timer to fire off the refresh of the Thumbnail Queue display.
     *  Is only alive if the InfoPanel is showing the statistics panel.
     */
    private Timer t;
    private JScrollPane statsScroller = new JScrollPane();

    /**
     *   Constructor for the InfoPanel.
     *   methods that allow thumbnails to be displayed. <p>
     *
     */
    public InfoPanel() {
        statsJPanel.setBackground(Settings.JPO_BACKGROUND_COLOR);
        setMinimumSize(Settings.infoPanelMinimumSize);
        setPreferredSize(Settings.infoPanelPreferredSize);


        statsScroller.setWheelScrollingEnabled(true);
        //  set the amount by which the panel scrolls down when the user clicks the
        //  little down or up arrow in the scrollbar
        statsScroller.getVerticalScrollBar().setUnitIncrement(20);

        final ActionListener taskPerformer = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                statsJPanel.updateQueueCount();
            }
        };
        t = new Timer(delay, taskPerformer);
        this.addTab("Info", statsScroller);


        thumbnail = new Thumbnail(Settings.thumbnailSize);
        this.addTab("Thumbnail", thumbnail);
        this.addTab("Word Cloud", new JPanel());


    }

    /**
     *   Invoked to tell that we should display something
     *   @param nde 	The Group or Picture node to be displayed.
     */
    public void showInfo(DefaultMutableTreeNode nde) {
        if (!(nde instanceof SortableDefaultMutableTreeNode)) {
            return; //ToDo do something smart when a query is shown.
        }
        final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) nde;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (node == null) {
                    statsScroller.setViewportView(unknownJPanel);
                    t.stop();
                    t.start();  // updates the queue-count
                } else if (node.getUserObject() instanceof PictureInfo) {
                    thumbnail.setNode(new SingleNodeBrowser(node), 0);
                    //statsScroller.setViewportView( thumbnail );
                    t.stop();
                } else {
                    // ToDo get this stuff off the event handler thread
                    statsJPanel.updateStats(node);
                    statsScroller.setViewportView(statsJPanel);
                    setComponentAt(2, new WordBrowser(node));
                    t.start();  // updates the queue-count
                }
            }
        });
    }
}
