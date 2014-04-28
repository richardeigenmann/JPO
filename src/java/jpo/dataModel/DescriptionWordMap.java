package jpo.dataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import jpo.TagCloud.WordMap;

/*
 DescrpitionWordMap.java:  Builds a map of Words from the Nodes linked to the nodes where the word was

 Copyright (C) 2009.2014  Richard Eigenmann.
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
 * Builds a list of words from the nodes and links to a set of nodes where they
 * were found
 *
 * @author Richard Eigenmann
 */
public class DescriptionWordMap extends WordMap {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( DescriptionWordMap.class.getName() );

    /**
     * Map that holds the words and a set of the nodes where the words were
     * found.
     */
    private final TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> wordNodeMap = new TreeMap<>();

    /**
     * Builds a TreeMap of words that links to a HashSet of Nodes
     *
     * @param startNode The node from which to start the analysis
     */
    public DescriptionWordMap( SortableDefaultMutableTreeNode startNode ) {
        buildList( startNode );
    }

    /**
     * Builds a TreeMap of words that links to a HashSet of Nodes
     *
     * @param navigator The NodeNavigator with the nodes from which to take the
     * words.
     */
    public DescriptionWordMap( NodeNavigatorInterface navigator ) {
        for ( int i = 0; i < navigator.getNumberOfNodes(); i++ ) {
            buildList( navigator.getNode( i ) );
        }
    }

    /**
     * Zips through the nodes and builds the word to node set map.
     */
    private void buildList( SortableDefaultMutableTreeNode startNode ) {
        if ( startNode == null ) {
            // nothing to do
            return;
        }
        SortableDefaultMutableTreeNode node;
        Object userObject;
        Enumeration nodes = startNode.breadthFirstEnumeration();
        while ( nodes.hasMoreElements() ) {
            node = (SortableDefaultMutableTreeNode) nodes.nextElement();
            userObject = node.getUserObject();
            if ( userObject instanceof PictureInfo ) {
                String description = ( (PictureInfo) userObject ).getDescription();
                splitAndAdd( description, node );
            }
        }
    }

    static String[] strikeWords = { "Die", "auf", "auf", "bei", "beim",
        "dem", "den", "der", "Der", "des", "die", "dsc", "mit", "nach", "vom", "von", "vor",
        "aus", "zum", "das", "Blick", "einem", "und", "Auf", "Das", "Ein",
        "ein", "eine", "einen", "für", "sich", "wird", "über", "zur", "einer", "unter",
        "hat", "the", "unterwegs", "ueber", "eines", "neben", "uns", "während", "zwischen",
        "nicht", "gesehen", "and", "als", "durch", "ist", "hinter", "Aufstieg", "Bei",
        "Beim", "Unterwegs", "Image", "man", "Nähe", "Richtung", "wurde", "noch", "nähert",
        "Mit", "meine", "mir", "ich", "wer", "wie", "was", "warum", "wie" };

    final static HashSet<String> strikeWordsSet = new HashSet<>( Arrays.asList( strikeWords ) );

    static String[] multiWordTerms = { "Saudi Arabien", "Petit Bateau", "Marigot Bay", "South Georgia",
        "South Africa", "Goldman Sachs", "New York", "New Zealand", "Quadra Island", "Washington State",
        "Empire State", "Aprés Ski", "Tel Aviv", "Hoch Ybrig", "Den Haag", "Groot Marico", "St Gallen", "Crans Montana" };

    /**
     * split the string and add the node to the map
     *
     * @param description The description of split
     * @param node The node where the description was found
     */
    private void splitAndAdd( String description, SortableDefaultMutableTreeNode node ) {
        // cleanup punctuation

        String fixAprostropheS = description.replaceAll( "\\'s", "" );
        String noPunctuation = fixAprostropheS.replaceAll( "[\\.:!,\\'\\\";\\?\\(\\)\\[\\]#\\$\\*\\+<>\\/&=]", "" );
        String noNumbers = noPunctuation.replaceAll( "\\d", "" );

        List<String> words = new ArrayList<>();
        for ( String multiWordTerm : multiWordTerms ) {
            if ( noNumbers.contains( multiWordTerm ) ) {
                noNumbers = noNumbers.replace( multiWordTerm, "" );
                words.add( multiWordTerm );
            }
        }

        words.addAll( Arrays.asList( noNumbers.split( "[\\s_\\-]+" ) ) );
        for ( String s : words ) {
            if ( ( s.length() > 2 ) && ( !strikeWordsSet.contains( s ) ) ) {

                HashSet<SortableDefaultMutableTreeNode> nodeSet;
                if ( !wordNodeMap.containsKey( s ) ) {
                    nodeSet = new HashSet<>();
                    wordNodeMap.put( s, nodeSet );
                } else {
                    nodeSet = wordNodeMap.get( s );
                }
                nodeSet.add( node );
            }
        }

    }

    /**
     * Returns the Word to Node Set to a caller
     *
     * @return the word to node set map
     */
    public TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> getWordNodeMap() {
        return wordNodeMap;
    }

    private HashMap<String, Integer> wordCountMap;

    /**
     * In this method we return a Map of description terms with the count of
     * nodes they appear in. In order to ensure that subsequent calls to this
     * method return quickly the list is build only once. If it should be
     * rebuilt the wordCountMap variable needs to be set to null;
     *
     * @return A Map with description terms and a count of nodes where the term
     * is found
     */
    @Override
    public Map<String, Integer> getWordValueMap() {
        if ( wordCountMap == null ) {
            LOGGER.fine( "Building wordCountMap" );
            wordCountMap = new HashMap<String, Integer>();
            Iterator<Entry<String, HashSet<SortableDefaultMutableTreeNode>>> it = wordNodeMap.entrySet().iterator();
            Entry<String, HashSet<SortableDefaultMutableTreeNode>> pairs;
            while ( it.hasNext() ) {
                pairs = it.next();
                wordCountMap.put( pairs.getKey(), pairs.getValue().size() );
            }
        }
        return wordCountMap;
    }

}
