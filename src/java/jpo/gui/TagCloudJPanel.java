package jpo.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import jpo.dataModel.DescriptionWordMap;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
TagCloudJPanel.java:  A JPanel that shows a TagCloud

Copyright (C) 2009  Richard Eigenmann.
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
 *  A Widget that shows a TagCloud
 *
 * @author Richard Eigenmann
 */
public class TagCloudJPanel extends JScrollPane {

    /**
     * Creates a JScrollPane with an embedded JPanel that holds the
     * WordCloudLabels
     * @param mouseController  The MouseListener to forward the clicks to
     */
    public TagCloudJPanel(MouseListener mouseController) {
        super();
        this.mouseController = mouseController;

        Runnable r = new Runnable() {

            public void run() {
                myPanel = new JPanel();
                setViewportView(myPanel);
                setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    /**
     * The panel that holds the labels
     */
    private JPanel myPanel;
    /**
     * The MouseListener that we will forward clicks to
     */
    private MouseListener mouseController;

    /**
     * Clear all the Labels off the panel and repaint it.
     */
    public void clearPanel() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                myPanel.removeAll();
                myPanel.repaint();
            }
        });
    }

    /**
     * Creates the labels for the supplied map. Adds the MouseListener to the
     * labels.
     * @param wordMap
     */
    public void createLabels(final AbstractMap<String, HashSet<SortableDefaultMutableTreeNode>> wordMap) {
        final int maxNodes = DescriptionWordMap.getMaximumNodes(wordMap);

        Runnable r = new Runnable() {

            public void run() {
                Iterator<Entry<String, HashSet<SortableDefaultMutableTreeNode>>> it = wordMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, HashSet<SortableDefaultMutableTreeNode>> pairs = it.next();
                    float percent = pairs.getValue().size() / (float) maxNodes;
                    TagCloudJLabel tagCloudEntry = new TagCloudJLabel(pairs.getKey(), percent);
                    tagCloudEntry.addMouseListener(mouseController);
                    myPanel.add(tagCloudEntry);
                }
                myPanel.validate();
                int countComponents = myPanel.getComponentCount();
                if (countComponents < 1) {
                    myPanel.setPreferredSize(new Dimension(200, 200));
                } else {
                    Component lastComponent = myPanel.getComponent(countComponents - 1);
                    Point location = lastComponent.getLocation();
                    Dimension size = lastComponent.getPreferredSize();
                    int panelHeight = location.y + size.height;
                    Dimension prefSize = myPanel.getPreferredSize();
                    prefSize.height = panelHeight;
                    myPanel.setPreferredSize(prefSize);
                }
                validate();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
}
