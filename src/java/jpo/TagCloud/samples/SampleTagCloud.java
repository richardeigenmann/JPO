package jpo.TagCloud.samples;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import jpo.TagCloud.TagClickListener;
import jpo.TagCloud.TagCloud;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpo.TagCloud.WordMap;


/*
 Copyright (C) 2009,  Richard Eigenmann, Zürich, Switzerland

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
 * Shows how to generate a sample tag cloud
 *
 * @author Richard Eigenmann
 * @deprecated
 */
public class SampleTagCloud
        extends JFrame
        implements TagClickListener {

    /**
     * Let's have a logger
     */
    private static final Logger LOGGER = Logger.getLogger( SampleTagCloud.class.getName() );

    /**
     * Make this class directly executable.
     *
     * @param args Command line arguments
     */
    public static void main( String[] args ) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                new SampleTagCloud();
            }
        };
        SwingUtilities.invokeLater( r );
    }

    /**
     * One set of words for cities and their population
     */
    private final WordMap cities = new Cities();

    /**
     * One set of words for countries and their population
     */
    private final WordMap countries = new Countries();

    /**
     * The TagCloud widget that we would like to show.
     */
    private final TagCloud tagCloud = new TagCloud();

    /**
     * Creates the JFrame, control widgets and TagCloud
     */
    public SampleTagCloud() {
        setPreferredSize( new Dimension( 850, 400 ) );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.getContentPane().add( getControlsPanel(), BorderLayout.PAGE_START );

        tagCloud.addTagClickListener( this );
        this.getContentPane().add( tagCloud );

        pack();
        setVisible( true );
    }

    /**
     * Build the widgets to have some interaction on the screen
     *
     * @return Returns a panel with stuff
     */
    private JPanel getControlsPanel() {
        JPanel controlsPanel = new JPanel();
        JButton citiesButton = new JButton( "Cities" );
        citiesButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                commentLabel.setText( "Setting the TagCloud to show cities" );
                tagCloud.setWordMap( cities );
                tagCloud.showWords();
            }
        } );
        controlsPanel.add( citiesButton );

        JButton countriesButton = new JButton( "Countries" );
        countriesButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                commentLabel.setText( "Setting the TagCloud to show countries" );
                tagCloud.setWordMap( countries );
                tagCloud.showWords();
            }
        } );
        controlsPanel.add( countriesButton );

        JSlider slider = new JSlider( 0, 10 ); // we will only use the slider for the percentage of total
        slider.setPreferredSize( new Dimension( 150, 20 ) );
        slider.addChangeListener( new MySliderChangeListener() );
        controlsPanel.add( slider );

        controlsPanel.add( commentLabel );
        return controlsPanel;
    }

    /**
     * A label that we can use to show what is happening.
     */
    private final JLabel commentLabel = new JLabel( "what's going on" );

    /**
     * This method is fired whenever a label is clicked because we registerd
     * this object as a TagClickListener on the TagCloud.
     *
     * @param key The string that the user clicked on
     */
    @Override
    public void tagClicked( String key ) {
        WordMap wordMap = tagCloud.getWordMap();
        Map<String, Integer> wordValueMap = wordMap.getWordValueMap();
        int population = wordValueMap.get( key );
        commentLabel.setText( String.format( "Clicked: %s; Population: %,d", key, population ) );
    }

    /**
     * Listener for the slider which limits the number of words to be shown
     */
    private class MySliderChangeListener
            implements ChangeListener {

        int minimumWords = 10;

        /**
         * Receive slider moves and using an exponential formula to adjust the
         * number of words being shown.
         *
         * @param ce The event
         */
        @Override
        public void stateChanged( ChangeEvent ce ) {
            final JSlider slider = (JSlider) ce.getSource();
            final int value = slider.getValue();
            double pct = Math.pow( 2f, value ) / Math.pow( 2f, slider.getMaximum() );
            if ( value == 0 ) {
                // correct for the value 0 which I want to have 0 for.
                pct = 0f;
            }
            WordMap map = tagCloud.getWordMap();
            if ( map == null ) {
                return;
            }
            int availableWords = map.getWordValueMap().size();
            int numberOfWords = (int) ( pct * ( availableWords - minimumWords ) ) + minimumWords;
            if ( numberOfWords > availableWords ) {
                LOGGER.severe( String.format( "Limit (%d) is greater than available words (%d) setting to available words", numberOfWords, availableWords ) );
                numberOfWords = availableWords;
            }
            commentLabel.setText( String.format( "Slider set to %d%%; showing %d words", (int) ( pct * 100f ), numberOfWords ) );
            tagCloud.setMaxWordsToShow( numberOfWords );
            tagCloud.showWords();
        }
    }

}
