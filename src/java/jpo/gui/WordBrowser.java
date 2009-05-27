package jpo.gui;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpo.dataModel.Tools;
import jpo.dataModel.DescriptionWordMap;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
WordBrowser.java:  A JFrame with the TagCloud

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
 * The controller that makes the GUI work
 *
 * @author Richard Eigenmann
 */
public class WordBrowser extends JPanel {

    private TagCloudJPanel panel = new TagCloudJPanel(new WordLabelListener());

    /**
     * Constructor to call to create a new YearlyAnalysisGui
     * @param startNode
     */
    public WordBrowser(final SortableDefaultMutableTreeNode startNode) {
        super();
        if (startNode == null) {
            Tools.log("WordBrowser created with a null node. Not acceptable.");
            return;
        }

        Runnable r = new Runnable() {

            public void run() {
                JPanel controlsPanel = new JPanel();
                JSlider slider = new JSlider(0, 10);
                slider.setPreferredSize(new Dimension(70, 20));
                slider.addChangeListener(new MySliderChangeListener());
                controlsPanel.add(slider);
                add(controlsPanel);

                if (getParent() == null) {
                    panel.setPreferredSize(new Dimension(200, 200));
                } else {
                    panel.setPreferredSize(getParent().getPreferredSize());
                }
                add(panel);

                dwm = new DescriptionWordMap(startNode);
                availableWords = dwm.getMap().size();
                showWords(30);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }

    }

    private class ShowWordsThread extends Thread {

        public ShowWordsThread(int limit) {
            this.limit = limit;
        }
        final int limit;

        @Override
        public void run() {
            showWords(limit);
        }
    }

    /**
     * Runs off an creates the labels for the limit number of words
     * @param limit the amount of words to be show maximum
     */
    public void showWords(final int limit) {
        dwm.truncateToTop(limit);
        Runnable r = new Runnable() {

            public void run() {
                panel.clearPanel();
                panel.createLabels(dwm.getTruncatedMap());
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    private DescriptionWordMap dwm;
    private int availableWords;

    /**
     * Listener for the slider which limits the number of words to be shown
     */
    private class MySliderChangeListener implements ChangeListener {

        /**
         * Receive slider moves and using an exponential formula adjusts the
         * number of words being shown.
         * @param ce The event
         */
        public void stateChanged(ChangeEvent ce) {
            final JSlider source = (JSlider) ce.getSource();
            final int value = source.getValue();
            double pct = Math.pow(2f, value) / Math.pow(2f, source.getMaximum());
            if (value == 0) {
                // correct for the value 0 which I want to have 0 for.
                pct = 0f;
            }
            final int limit = (int) (pct * (availableWords - 30)) + 30;
            new ShowWordsThread(limit).start();
        }
    }

    /**
     * Listens to the mouse clicks and shows the set of images in the Thumbnail pane  in clicked.
     */
    private class WordLabelListener extends MouseAdapter {

        /**
         * Listens to the mouse clicks and shows the set of images in the Thumbnail pane  in clicked.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            TagCloudJLabel wl = (TagCloudJLabel) e.getComponent();
            String key = wl.getText();
            HashSet<SortableDefaultMutableTreeNode> hs = dwm.getMap().get(key);
            ArrayList<SortableDefaultMutableTreeNode> set = new ArrayList<SortableDefaultMutableTreeNode>(hs);
            ArrayListBrowser alb = new ArrayListBrowser(key, set);
            Jpo.thumbnailJScrollPane.show(alb);
        }
    }
}
