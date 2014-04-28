package jpo.TagCloud;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import jpo.dataModel.Tools;

/*
 TagCloud.java:  A Widget that shows a TagCloud

 Copyright (C) 2009-2014  Richard Eigenmann.
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
 * A controller that constructs the TagCloud of Labels and handles mouse clicks,
 * sending them on to TagClickListeners.
 *
 * @author Richard Eigenmann
 */
public class TagCloud extends JScrollPane {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( TagCloud.class.getName() );

    /**
     * Constructor to call to create a new TagCloud. It used BorderLayout and
     * puts the Slider in the top part and the scroll pane in the centre part.
     */
    public TagCloud() {
        Tools.checkEDT();
        setViewportView( verticalGrowJPanel );
        setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
    }

    /**
     * The number of words to show. The default is 10 words.
     */
    private int wordsToShow = 10;

    /**
     * Sets the maximum number of words to show. The number is validated and set
     * to be 1 or higher. Call showWords afterwards to update the tags being
     * shown.
     *
     * @param wordsToShow the number of words to show in the range
     * 1..Integer.MAX_VALUE.
     */
    public void setMaxWordsToShow( int wordsToShow ) {
        // never trust inputs
        if ( wordsToShow < 1 ) {
            LOGGER.finest( String.format( "wordsToShow was %d which is less than 1; setting to 1.", wordsToShow ) );
            wordsToShow = 1;
        }
        this.wordsToShow = wordsToShow;
    }

    /**
     * This special panel holds the words (TagCloudJLabel).
     */
    private final VerticalGrowJPanel verticalGrowJPanel = new VerticalGrowJPanel();

    /**
     * The wordMap that will be used for the TagCloud
     */
    private WordMap wordMap;

    /**
     * This method receives the WordMap of the words to be shown in the
     * TagCloud. Call showWords afterwards to update the tags being shown.
     *
     * @param wordMap The WordMap of the words to be shown.
     */
    public void setWordMap( WordMap wordMap ) {
        this.wordMap = wordMap;
    }

    /**
     * Returns the currently used WordMap. Careful, could be null!
     *
     * @return the currently used WordMap
     */
    public WordMap getWordMap() {
        return wordMap;
    }

    /**
     * Runs off an creates the labels for the wordsToShow number of words.
     * Removes all previous labels and adds the labels for the supplied map.
     * Adds the MouseListener to the labels.
     */
    public void showWords() {
        Tools.checkEDT();

        verticalGrowJPanel.removeAll();
        if ( wordMap != null ) { // if no wordMap, leave panel empty
            TreeSet<String> topWords = wordMap.getTopWords( wordsToShow );

            Iterator<String> it = topWords.iterator();
            while ( it.hasNext() ) {
                String s = it.next();
                float percent = (float) wordMap.getWordValueMap().get( s ) / (float) wordMap.getMaximumWordValue();
                TagCloudJLabel tagCloudEntry = new TagCloudJLabel( s, percent );
                tagCloudEntry.addMouseListener( wordClickListener );
                verticalGrowJPanel.add( tagCloudEntry );
            }
        } else {
            verticalGrowJPanel.add( new JLabel( "hitting else" ) );
        }
        // After hours of frustration it turns out we need to validate the panel and the scrollpane.
        verticalGrowJPanel.validate();
        validate();
        repaint();
    }

    /**
     * A click listener that fires off the tagClicked event to the
     * tagClickListener when a click is registered on a word label.
     */
    private final transient MouseAdapter wordClickListener = new MouseAdapter() {

        @Override
        public void mouseClicked( MouseEvent e ) {
            TagCloudJLabel wl = (TagCloudJLabel) e.getComponent();
            String tag = wl.getText();
            notifyTagClickListeners( tag );
        }
    };

    /**
     * Notifies the listeners that a tag was clicked
     *
     * @param tag the tag that was clicked
     */
    private void notifyTagClickListeners( String tag ) {
        synchronized ( tagClickListeners ) {
            for ( TagClickListener tagClickListener : tagClickListeners ) {
                tagClickListener.tagClicked( tag );
            }
        }
    }

    /**
     * The TagClickListeners that want to be notified when the user clicks on a Tag.
     */
    private final Set<TagClickListener> tagClickListeners = Collections.synchronizedSet( new HashSet<TagClickListener>() );

    /**
     * Register a TagClickListener to receive the word a user clicked on.
     *
     * @param listener The listener that will be registered
     */
    public void addTagClickListener( TagClickListener listener ) {
        tagClickListeners.add( listener );
    }

    /**
     * Remove the specified TagClickListener.
     *
     * @param listener	The listener to deregister
     */
    public void removeTagClickListener( TagClickListener listener ) {
        tagClickListeners.remove( listener );
    }
}
