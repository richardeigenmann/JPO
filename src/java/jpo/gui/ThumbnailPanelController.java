package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfo;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import java.util.*;
import java.util.logging.Logger;
import jpo.dataModel.Tools;
import jpo.gui.swing.ThumbnailPanel;
import jpo.gui.swing.ThumbnailPanelTitle;

/*
ThumbnailPanelController.java:  a JScrollPane that shows thumbnails

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
 *  The ThumbnailPanelController manages a JPanel in a JScrollPane that displays a group of pictures
 *  in a grid of thumbnails or ad hoc search results. Real pictures are shown as a thumbnail
 *  of the image whilst sub-groups are shown as a folder icon. Each thumbnail has it's caption
 *  under the image. <p>
 *
 *  If the size of the component is changed the images are re-laid out and can take advantage of the
 *  extra space if there is some.
 *
 */
public class ThumbnailPanelController implements RelayoutListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger(ThumbnailPanelController.class.getName());
    /**
     * The panel that holds the Thumbnails
     */
    private ThumbnailPanel thumbnailPanel;
    /**
     * The title above the ThumbnailPanel
     */
    private ThumbnailPanelTitle titleJPanel;

    /**
     * Returns a handle to the JScrollPane widget
     * @return The JScollPane widget
     */
    public JScrollPane getJScrollPane() {
        return thumbnailPanel;
    }

    /**
     *   Instructs the ThumbnailPanelController to display the specified set of nodes to be displayed
     *   @param mySetOfNodes 	The Interface with the collection of nodes
     */
    public void show(ThumbnailBrowserInterface mySetOfNodes) {
        if (this.mySetOfNodes != null) {
            this.mySetOfNodes.removeRelayoutListener(this);
            this.mySetOfNodes.cleanup();
        }
        this.mySetOfNodes = mySetOfNodes;
        mySetOfNodes.addRelayoutListener(this);  //Todo, investigate how we unattach these...

        Runnable r = new Runnable() {

            public void run() {
                Settings.pictureCollection.clearSelection();
                thumbnailPanel.getVerticalScrollBar().setValue(0);
                startIndex = 0;
                curPage = 1;
                assignThumbnails();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    /**
     *  color for title
     */
    private static final Color GRAY_BLUE_LIGHT = new Color(204, 204, 255);
    /**
     *  color for title
     */
    private static final Color GRAY_BLUE_DARK = new Color(51, 51, 102);
    /**
     * currently displayed page
     * */
    private int curPage = 1;
    /**
     *   A Thread that will load the images.
     *
     * private ThumbnailLoaderThread tl = null; */
    /**
     *  variable that will signal to the thread to stop loading images.
     */
    public boolean stopThread;
    /**
     *  This object refers to the set of Nodes that is being browsed in the ThumbnailPanelController
     */
    public ThumbnailBrowserInterface mySetOfNodes;
    /**
     *  a variable to hold the current starting position of thumbnails being
     *  displayed out of a group or search. Range 0..count()-1<p>
     *
     *  This was invented to allow the number of thumbnails to be restricted
     *  so that <<Out of memory>> errors may be averted on long lists of
     *  pictures.
     *
     **/
    private int startIndex;
    /**
     *   An array that holds the 50 or so ThumbnailComponents that are being displayed
     */
    private Thumbnail[] thumbnails;
    /**
     *   An array that holds the 50 or so ThumbnailDescriptionJPanels that are being displayed
     */
    private ThumbnailDescriptionJPanel[] thumbnailDescriptionJPanels;
    /**
     *   The amount of horizontal padding between the thumbnails and descriptions.
     */
    private static final int horizontalPadding = 10;
    /**
     *   The amount of vertical padding between the thumbnails and descriptions.
     */
    private static final int verticalPadding = 10;
    /**
     *  This variable keeps track of how many thumbnails per page the component was initialised
     *  with. If the number changes because the user changed it in the settings then the difference
     *  is recognised and the arrays are recreated.
     */
    private int initialisedMaxThumbnails = Integer.MIN_VALUE;
    /**
     *  Factor for the Thumbnails
     */
    private float thumbnailSizeFactor = 1;
    /**
     *  Point where the mouse was pressed
     */
    private Point mousePressedPoint;

    /**
     *   creates a new JScrollPane with an embedded JPanel and provides a set of
     *   methods that allow thumbnails to be displayed. <p>
     *
     *   The passing in of the caller is obsolete and should be removed when
     *   a better interface type solution has been built.
     *
     */
    public ThumbnailPanelController() {
        Runnable r = new Runnable() {

            public void run() {
                initComponents();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Request that the ThumbnailPanel show the first page of Thumbnails
     */
    private void goToFirstPage() {
        if (startIndex == 0) {
            return;
        }

        startIndex = 0;
        thumbnailPanel.getVerticalScrollBar().setValue(0);
        curPage = 1;
        assignThumbnails();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the previous page of Thumbnails
     */
    private void goToPreviousPage() {
        startIndex = startIndex - Settings.maxThumbnails;
        if (startIndex < 0) {
            startIndex = 0;
        }
        thumbnailPanel.getVerticalScrollBar().setValue(0);
        curPage--;
        assignThumbnails();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the next page of Thumbnails
     */
    private void goToNextPage() {
        startIndex = startIndex + Settings.maxThumbnails;
        thumbnailPanel.getVerticalScrollBar().setValue(0);
        curPage++;
        assignThumbnails();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the last page of Thumbnails
     */
    private void goToLastPage() {
        int last = mySetOfNodes.getNumberOfNodes();
        int tgtPage = last / Settings.maxThumbnails;
        curPage = tgtPage + 1;
        startIndex = tgtPage * Settings.maxThumbnails;
        thumbnailPanel.getVerticalScrollBar().setValue(0);
        assignThumbnails();
        setButtonStatus();
    }

    private void initComponents() {
        thumbnailPanel = new ThumbnailPanel();

        initThumbnailsArray();


        titleJPanel = new ThumbnailPanelTitle();

        // Wire up the events
        titleJPanel.firstThumbnailsPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                goToFirstPage();
            }
        });
        titleJPanel.previousThumbnailsPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                goToPreviousPage();
            }
        });
        titleJPanel.nextThumbnailsPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                goToNextPage();
            }
        });
        titleJPanel.lastThumbnailsPageButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                goToLastPage();
            }
        });

        titleJPanel.resizeJSlider.addChangeListener(new SlideChangeListener());



        thumbnailPanel.setColumnHeaderView(titleJPanel);

        JPanel whiteArea = new JPanel();
        thumbnailPanel.setCorner(JScrollPane.UPPER_RIGHT_CORNER, whiteArea);

        thumbnailPanel.ThumbnailPane.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                thumbnailPanel.requestFocusInWindow();

                Graphics g = thumbnailPanel.ThumbnailPane.getGraphics();
                thumbnailPanel.ThumbnailPane.paint(g); //cheap way of undoing old rectancle... TODO: use the glass pane

                Point mouseMovedToPoint = e.getPoint();
                Rectangle r = new Rectangle(mousePressedPoint,
                        new Dimension(mouseMovedToPoint.x - mousePressedPoint.x,
                        mouseMovedToPoint.y - mousePressedPoint.y));
                if (mouseMovedToPoint.x < mousePressedPoint.x) {
                    r.x = mouseMovedToPoint.x;
                    r.width = mousePressedPoint.x - mouseMovedToPoint.x;
                }
                if (mouseMovedToPoint.y < mousePressedPoint.y) {
                    r.y = mouseMovedToPoint.y;
                    r.height = mousePressedPoint.y - mouseMovedToPoint.y;
                }

                // I wonder why they don't put the following two lines into the SWING library but
                // let you work out this binary math on your own from the unhelpful description?
                boolean ctrlpressed = (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK;
                boolean shiftpressed = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;

                if (!(ctrlpressed | shiftpressed)) {
                    Settings.pictureCollection.clearSelection();
                }
                Rectangle thumbnailRectangle = new Rectangle();
                SortableDefaultMutableTreeNode n;
                for (int i = 0; i < thumbnails.length; i++) {
                    thumbnails[i].getBounds(thumbnailRectangle);
                    if (r.intersects(thumbnailRectangle)) {
                        n = thumbnails[i].referringNode;
                        if (n != null) {
                            Settings.pictureCollection.addToSelectedNodes(n);
                        }
                    }
                }


            }
        });

        thumbnailPanel.ThumbnailPane.addMouseMotionListener(new MouseInputAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                Point mouseMovedToPoint = e.getPoint();
                Rectangle r = new Rectangle(mousePressedPoint,
                        new Dimension(mouseMovedToPoint.x - mousePressedPoint.x,
                        mouseMovedToPoint.y - mousePressedPoint.y));
                if (mouseMovedToPoint.x < mousePressedPoint.x) {
                    r.x = mouseMovedToPoint.x;
                    r.width = mousePressedPoint.x - mouseMovedToPoint.x;
                }
                if (mouseMovedToPoint.y < mousePressedPoint.y) {
                    r.y = mouseMovedToPoint.y;
                    r.height = mousePressedPoint.y - mouseMovedToPoint.y;
                }
                Graphics g = thumbnailPanel.ThumbnailPane.getGraphics();
                thumbnailPanel.ThumbnailPane.paint(g); //cheap way of undoing old rectancle...
                g.drawRect(r.x, r.y, r.width, r.height);

                // find out if we need to scroll the window
                Rectangle viewRect = thumbnailPanel.getViewport().getViewRect();
                JScrollBar verticalScrollBar = thumbnailPanel.getVerticalScrollBar();
                final int scrolltrigger = 40;
                if (mouseMovedToPoint.y - viewRect.y - viewRect.height > -scrolltrigger) {
                    // logger.info("must scroll down");
                    int increment = verticalScrollBar.getUnitIncrement(1);
                    int position = verticalScrollBar.getValue();
                    if (position < verticalScrollBar.getMaximum()) {
                        verticalScrollBar.setValue(position + increment);
                    }
                } else if (mouseMovedToPoint.y - viewRect.y < scrolltrigger) {
                    //logger.info("must scroll up");
                    int increment = verticalScrollBar.getUnitIncrement(1);
                    int position = verticalScrollBar.getValue();
                    if (position > verticalScrollBar.getMinimum()) {
                        verticalScrollBar.setValue(position - increment);
                    }

                }

            }
        });


        thumbnailPanel.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        //logger.info("thumbnailJScrollPane: Trapped a KeyTyped event for key code: " + Integer.toString( e.getKeyCode() ) );
                        if (e.getKeyCode() == KeyEvent.VK_A && e.isControlDown()) {
                            //logger.info("thumbnailJScrollPane: Got a CTRL-A");
                            selectAll();
                        }
                    }
                });
    }

    private class SlideChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            thumbnailSizeFactor = (float) source.getValue() / titleJPanel.THUMBNAILSIZE_MAX;
            thumbnailPanel.thumbnailLayout.setThumbnailWidth((int) (350 * thumbnailSizeFactor));
            for (int i = 0; i < Settings.maxThumbnails; i++) {
                thumbnails[i].setFactor(thumbnailSizeFactor);
                thumbnailDescriptionJPanels[i].setFactor(thumbnailSizeFactor);
            }
            thumbnailPanel.thumbnailLayout.layoutContainer(thumbnailPanel.ThumbnailPane);
        }
    }

    /**
     *  creates the arrays for the thumbnails and the descriptions and adds them to the ThubnailPane.
     */
    public void initThumbnailsArray() {
        thumbnails = new Thumbnail[Settings.maxThumbnails];
        thumbnailDescriptionJPanels = new ThumbnailDescriptionJPanel[Settings.maxThumbnails];
        thumbnailPanel.ThumbnailPane.removeAll();
        initialisedMaxThumbnails = Settings.maxThumbnails;
        for (int i = 0; i < Settings.maxThumbnails; i++) {
            thumbnails[i] = new Thumbnail();
            thumbnailDescriptionJPanels[i] = new ThumbnailDescriptionJPanel();
            thumbnailPanel.ThumbnailPane.add(thumbnails[i]);
            thumbnailPanel.ThumbnailPane.add(thumbnailDescriptionJPanels[i]);
        }
    }

    /**
     *  this method runs through all the thumbnails on the panel and makes sure they are set to the
     *  correct node. It also sets the tile of the JScrollPane.
     */
    public void assignThumbnails() {
        if (mySetOfNodes == null) {
            return;
        }

        titleJPanel.setTitle(mySetOfNodes.getTitle());

        if (initialisedMaxThumbnails != Settings.maxThumbnails) {
            initThumbnailsArray();
        }

        int groupCount = mySetOfNodes.getNumberOfNodes();

        setPageStats();

        setButtonStatus();
        // take the thumbnails off the creation queue if they were on it.
        // as setNode is now internally synchronised this can slow down removal
        // from the queue
        for (int i = 0; i < Settings.maxThumbnails; i++) {
            thumbnails[i].unqueue();
        }
        for (int i = 0; i < Settings.maxThumbnails; i++) {
            thumbnails[i].setNode(mySetOfNodes, i + startIndex);
            thumbnailDescriptionJPanels[i].setNode(mySetOfNodes.getNode(i + startIndex));
        }
    }
  
    /**
     * Sets the text in the title for displaying page count information
     */
    private void setPageStats() {
        final int total = mySetOfNodes.getNumberOfNodes();
        final int lastOnPage = Math.min(startIndex + Settings.maxThumbnails, total);
        Runnable r = new Runnable() {

            public void run() {
                titleJPanel.lblPage.setText(String.format("Thumbnails %d to %d of %d", startIndex + 1, lastOnPage, total));
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     *  This method sets whether the first, previous, next and last buttons are visible or not
     */
    public void setButtonStatus() {
        Runnable r = new Runnable() {

            public void run() {
                if (startIndex == 0) {
                    titleJPanel.firstThumbnailsPageButton.setEnabled(false);
                    titleJPanel.previousThumbnailsPageButton.setEnabled(false);
                } else {
                    titleJPanel.firstThumbnailsPageButton.setEnabled(true);
                    titleJPanel.previousThumbnailsPageButton.setEnabled(true);
                }

                int count = mySetOfNodes.getNumberOfNodes();
                if ((startIndex + Settings.maxThumbnails) < count) {
                    titleJPanel.lastThumbnailsPageButton.setEnabled(true);
                    titleJPanel.nextThumbnailsPageButton.setEnabled(true);
                } else {
                    titleJPanel.lastThumbnailsPageButton.setEnabled(false);
                    titleJPanel.nextThumbnailsPageButton.setEnabled(false);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

 
  
    /**
     *  This method select all Thumbnails which are not null
     */
    public void selectAll() {
        SortableDefaultMutableTreeNode n;
        for (int i = 0; i < thumbnails.length; i++) {
            n = thumbnails[i].referringNode;
            if (n != null) {
                Settings.pictureCollection.addToSelectedNodes(n);
            }
        }
    }

   
 

 

 
}








