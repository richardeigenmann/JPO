package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.eventBus.JpoEventBus;
import org.jpo.eventBus.ShowGroupRequest;
import org.jpo.eventBus.ShowQueryRequest;
import org.jpo.dataModel.PictureInfo;
import org.jpo.dataModel.Settings;
import org.jpo.dataModel.SortableDefaultMutableTreeNode;
import org.jpo.dataModel.TextQuery;
import org.tagcloud.TagClickListener;
import org.tagcloud.TagCloud;
import org.tagcloud.WeightedWord;
import org.tagcloud.WeightedWordInterface;

import javax.swing.*;
import java.util.*;
import java.util.logging.Logger;

/*
 Copyright (C) 2009-2019  Richard Eigenmann.
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
 * Manages the Tag Cloud
 */
public class TagCloudController implements TagClickListener {
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(TagCloudController.class.getName());

    /**
     * Reference to the TagCloud widget
     */
    private final TagCloud tagCloud = new TagCloud();

    /**
     * Constructs the Controller
     */
    public TagCloudController() {
        JpoEventBus.getInstance().register( TagCloudController.this );
        tagCloud.addTagClickListener( TagCloudController.this );
        tagCloud.setMaxWordsToShow( Settings.tagCloudWords );
    }


    /**
     * Returns the tag cloud component
     *
     * @return the tag cloud component
     */
    public JComponent getTagCloud() {
        return tagCloud;
    }

    private NodeWordMapper nodeWordMapper;

    /**
     * Handles the ShowGroupRequest by updating the display...
     *
     * @param event Event
     */
    @Subscribe
    public void handleGroupSelectionEvent( final ShowGroupRequest event ) {
        SwingUtilities.invokeLater( () -> {
            nodeWordMapper = new NodeWordMapper(event.getNode());
            tagCloud.setWordsList( nodeWordMapper.getWeightedWords() );
        } );
    }

    @Override
    public void tagClicked( WeightedWordInterface weightedWord ) {
        if ( nodeWordMapper == null ) {
            return;
        }

        TextQuery textQuery = new TextQuery( weightedWord.getWord() );
        textQuery.setStartNode( nodeWordMapper.getRootNode() );
        JpoEventBus.getInstance().post( new ShowQueryRequest( textQuery ) );
    }

    private final static HashSet<String> strikeWordsSet = new HashSet<>( Arrays.asList(
            "als",
            "Am",
            "am",
            "an",
            "An",
            "and",
            "at",
            "auf",
            "Auf",
            "Aufstieg",
            "aus",
            "bei",
            "Bei",
            "Beim",
            "beim",
            "Blick",
            "by",
            "das",
            "Das",
            "de",
            "del",
            "dem",
            "den",
            "der",
            "Der",
            "des",
            "Die",
            "die",
            "DSC",
            "dsc",
            "DSCN",
            "durch",
            "Ein",
            "ein",
            "eine",
            "einem",
            "einen",
            "einer",
            "eines",
            "El",
            "en",
            "fuer",
            "für",
            "gesehen",
            "hat",
            "hinter",
            "ich",
            "II",
            "Il",
            "im",
            "Im",
            "Image",
            "img",
            "IMG",
            "in",
            "In",
            "in",
            "ist",
            "je",
            "La",
            "man",
            "meine",
            "MG",
            "mir",
            "Mit",
            "mit",
            "nach",
            "neben",
            "Neuer",
            "nicht",
            "noch",
            "Nähe",
            "nähert",
            "ob",
            "of",
            "of",
            "on",
            "Richtung",
            "san",
            "SDC",
            "sein",
            "sich",
            "The",
            "the",
            "to",
            "ueber",
            "um",
            "und",
            "uns",
            "unter",
            "Unterwegs",
            "unterwegs",
            "van",
            "vom",
            "von",
            "vor",
            "warum",
            "was",
            "wer",
            "wie",
            "wie",
            "wir",
            "wird",
            "with",
            "without",
            "wurde",
            "während",
            "zu",
            "zum",
            "zur",
            "zwischen",
            "über"
    ) );

    private static final String[] multiWordTerms = {
        "Aprés Ski",
        "Cape Town",
        "Crans Montana",
        "Den Haag",
        "Empire State",
        "Goldman Sachs",
        "Groot Marico",
        "Halfmoon Bay",
        "Hoch Ybrig",
        "Lions Head",
        "Marigot Bay",
        "Nags Head",
        "New York",
        "New Zealand",
        "Persischer Golf",
        "Petit Bateau",
        "Quadra Island",
        "Red Sea",
        "Rotes Meer",
        "Saudi Arabien",
        "Seleger Moor",
        "South Africa",
        "South Georgia",
        "St Gallen",
        "St. Petersinsel",
        "Tel Aviv",
        "Toten Meer",
        "Totes Meer",
        "Vic Falls",
        "Victoria Falls",
        "Washington State"
    };


    private static class NodeWordMapper {

        private final List<WeightedWordInterface> weightedWordList = new ArrayList<>();

        private final SortableDefaultMutableTreeNode rootNode;

        NodeWordMapper( SortableDefaultMutableTreeNode node ) {
            this.rootNode = node;
            buildList();
        }

        public List<WeightedWordInterface> getWeightedWords() {
            return weightedWordList;
        }

        public SortableDefaultMutableTreeNode getRootNode() {
            return rootNode;
        }

        /**
         * Zips through the nodes and builds the word to node set map.
         */
        private void buildList() {
            SortableDefaultMutableTreeNode node;
            Object userObject;
            Enumeration nodes = rootNode.breadthFirstEnumeration();
            while ( nodes.hasMoreElements() ) {
                node = (SortableDefaultMutableTreeNode) nodes.nextElement();
                userObject = node.getUserObject();
                if ( userObject instanceof PictureInfo ) {
                    String description = ( (PictureInfo) userObject ).getDescription();
                    splitAndAdd( description );
                }
            }
            wordCountMap.keySet().forEach( (key ) -> weightedWordList.add( new WeightedWord( key, wordCountMap.get( key ) ) ));
        }

        /**
         * split the string and add the node to the map
         *
         * @param description The description of split
         */
        private void splitAndAdd( String description ) {
            String fixAprostropheS = description.replaceAll( "\\'s", "" );
            String noPunctuation = fixAprostropheS.replaceAll( "[\\.:!,\\'\\\";\\?\\(\\)\\[\\]#\\$\\*\\+<>\\/&=]", "" );
            String noNumbers = noPunctuation.replaceAll( "\\d", "" );

            for ( String multiWordTerm : multiWordTerms ) {
                if ( noNumbers.contains( multiWordTerm ) ) {
                    noNumbers = noNumbers.replace( multiWordTerm, "" );
                    addWord( multiWordTerm );
                }
            }

            for ( String s : noNumbers.split( "[\\s_\\-]+" ) ) {
                if ( !strikeWordsSet.contains( s ) ) {
                    addWord( s );
                }
            }
        }

        private final Map<String, Integer> wordCountMap = new HashMap<>();

        private void addWord( String word ) {
            if ( wordCountMap.containsKey( word ) ) {
                wordCountMap.put( word, wordCountMap.get( word ) + 1 );
            } else {
                wordCountMap.put( word, 1 );
            }
        }

    }
}
