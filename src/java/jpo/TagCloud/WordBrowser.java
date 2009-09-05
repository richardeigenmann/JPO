package jpo.TagCloud;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpo.dataModel.Tools;

/*
WordBrowser.java:  A Widget with a slider and a TagCloud

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
 * A controller that constructs the TagCloud GUI, wires up the slider against the
 * list of words and nodes and fires TagClicked notifications when a word is clicked.
 *
 * @author Richard Eigenmann
 */
public class WordBrowser extends JPanel {

    /**
     * This special panel holds the words (TagCloudJLabel).
     */
    private VerticalGrowJPanel labelPanel = new VerticalGrowJPanel();

    /**
     * The scroll pane that facilitates the scrolling of the tags
     */
    private JScrollPane scrollPane = new JScrollPane( labelPanel );

    /**
     * The listener to send the word click events off to.
     */
    private TagClickListener tagClickListener;

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( WordBrowser.class.getName() );

    /**
     * The minimum number of words to show.
     */
    private int minimumWords;


    /**
     * The DescriptionordMap  for the TagCloud
     */
    //private DescriptionWordMap dwm;
    /**
     * Constructor to call to create a new WordBrowser. It used BorderLayout and
     * puts the Slider in the top part and the scroll pane in the center part.
     * 
     * @param wordMap
     * @param tagClickListener The listener to notify word clicks to
     * @param minimumWords The minimum number of words to show (at least 1; enforced)
     */
    public WordBrowser( WordMap wordMap, final TagClickListener tagClickListener, final int minimumWords ) {
        logger.fine( String.format( "WordBrowser instantiated with WordMap, TagClickListener and minimumWords (%d)", minimumWords ) );
        logger.finest( "WordMap contents: " + wordMap.toString() );
        Tools.checkEDT();

        this.tagClickListener = tagClickListener;
        this.minimumWords = minimumWords;
        // never trust inputs
        if ( this.minimumWords < 1 ) {
            logger.finest( String.format( "minimumWords was %d which is less than 1; setting to 1", this.minimumWords ) );
            this.minimumWords = 1;
        }

        setLayout( new BorderLayout() );
        JPanel controlsPanel = new JPanel();
        JSlider slider = new JSlider( 0, 10 );
        slider.setPreferredSize( new Dimension( 150, 20 ) );
        slider.addChangeListener( new MySliderChangeListener() );
        controlsPanel.add( slider );
        add( controlsPanel, BorderLayout.PAGE_START );

        scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        add( scrollPane, BorderLayout.CENTER );

        setWordMap( wordMap );
    }

    /**
     * The wordMap that will be used for the TagCloud
     */
    private WordMap wordMap;


    public void setWordMap( WordMap wm ) {
        wordMap = wm;
        showWords( this.minimumWords );
    }


    /**
     * Runs off an creates the labels for the limit number of words. Removes
     * all previous lables and adds the labels for the supplied map.
     * Adds the MouseListener to the labels.
     * @param limit the amount of words to be show maximum
     */
    public void showWords( final int limit ) {
        Tools.checkEDT();
        TreeSet<String> topWords = wordMap.getTopWords( limit );
        logger.fine( String.format( "maxNodes determined to be %d", wordMap.getMaximumCount() ) );

        labelPanel.removeAll();

        Iterator<String> it = topWords.iterator();
        while ( it.hasNext() ) {
            String s = it.next();
            float percent = (float) wordMap.getWordCountMap().get( s ) / (float) wordMap.getMaximumCount();
            TagCloudJLabel tagCloudEntry = new TagCloudJLabel( s, percent );
            tagCloudEntry.addMouseListener( wordClickListener );
            labelPanel.add( tagCloudEntry );
        }
        // these two validate steps caused me hours of grief: If the slider is moved
        // rapidly it seems that the JScrollPane validate does not always trigger
        // the validate of the Viewport and the layout is corrupted. RE 30 Jul 2009
        labelPanel.validate();
        scrollPane.validate();
        scrollPane.repaint();

    }

    /**
     * Listener for the slider which limits the number of words to be shown
     */
    private class MySliderChangeListener implements ChangeListener {

        /**
         * Receive slider moves and using an exponential formula adjusts the
         * number of words being shown.
         * @param ce The event
         */
        public void stateChanged( ChangeEvent ce ) {
            Tools.checkEDT();
            final JSlider source = (JSlider) ce.getSource();
            final int value = source.getValue();
            double pct = Math.pow( 2f, value ) / Math.pow( 2f, source.getMaximum() );
            if ( value == 0 ) {
                // correct for the value 0 which I want to have 0 for.
                pct = 0f;
            }
            int availableWords = wordMap.getWordCountMap().size();
            int limit = (int) ( pct * ( availableWords - minimumWords ) ) + minimumWords;
            if ( limit > availableWords ) {
                logger.severe( String.format( "Limit (%d) is greater than available words (%d) setting to available words", limit, availableWords ) );
                limit = availableWords;
            }
            showWords( limit );
        }
    }

    /**
     * A click listener that fires off the tagClicked event to the tagClickListener when
     * a click is registered on a word label.
     */
    private MouseAdapter wordClickListener = new MouseAdapter() {

        @Override
        public void mouseClicked( MouseEvent e ) {
            TagCloudJLabel wl = (TagCloudJLabel) e.getComponent();
            tagClickListener.tagClicked( wl.getText() );
        }
    };
}
