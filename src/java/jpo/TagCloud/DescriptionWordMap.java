package jpo.TagCloud;

import jpo.dataModel.*;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

/*
DescrpitionWordMap.java:  Builds a list of description Words and links to a set of nodes where they were found

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
 * Builds a list of description Words and links to a set of nodes where they were found
 * @author Richard Eigenmann
 */
public class DescriptionWordMap extends WordMap {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( DescriptionWordMap.class.getName() );

    /**
     * A reference to the node from where we start analysing
     *
     */
    private SortableDefaultMutableTreeNode startNode;

    /**
     * Map that holds the words and a set of the nodes where the words were found.
     */
    private TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> wordMap = new TreeMap<String, HashSet<SortableDefaultMutableTreeNode>>();


    /**
     * Constructor
     * @param startNode The node from which to start the analysis
     */
    public DescriptionWordMap( SortableDefaultMutableTreeNode startNode ) {
        this.startNode = startNode;
        buildList();

    }


    /**
     * Zips through the nodes and builds the word to node set map.
     */
    private void buildList() {
        if ( startNode == null ) {
            // nothing to do
            return;
        }
        SortableDefaultMutableTreeNode n;
        Object o;
        Enumeration nodes = startNode.breadthFirstEnumeration();
        while ( nodes.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            o = n.getUserObject();
            String description = "";
            if ( o instanceof PictureInfo ) {
                description = ( (PictureInfo) o ).getDescription();
                splitAndAdd( description, n );
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

    final static HashSet<String> strikeWordsSet = new HashSet<String>( Arrays.asList( strikeWords ) );

    static String[] multiWordTerms = { "Saudi Arabien", "Petit Bateau", "Marigot Bay", "South Georgia",
        "South Africa", "Goldman Sachs", "New York", "New Zealand", "Quadra Island", "Washington State",
        "Empire State", "Aprés Ski", "Tel Aviv", "Hoch Ybrig", "Den Haag", "Groot Marico", "St Gallen", "Crans Montana" };


    /**
     * split the string and add the node to the map
     * @param description  The description ot split
     * @param n The node where the description was fouä
     */
    private void splitAndAdd( String description, SortableDefaultMutableTreeNode n ) {
        // cleanup punctuation

        String fixAprostropheS = description.replaceAll( "\\'s", "" );
        String noPunctuation = fixAprostropheS.replaceAll( "[\\.:!,\\'\\\";\\?\\(\\)\\[\\]#\\$\\*\\+<>\\/&=]", "" );
        String noNumbers = noPunctuation.replaceAll( "\\d", "" );

        ArrayList<String> words = new ArrayList<String>();
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
                if ( !wordMap.containsKey( s ) ) {
                    nodeSet = new HashSet<SortableDefaultMutableTreeNode>();
                    wordMap.put( s, nodeSet );
                } else {
                    nodeSet = wordMap.get( s );
                }
                nodeSet.add( n );
            }
        }

    }


    /**
     * Returns the Word to Node Set to a caller
     * @return the word to node set map
     */
    public TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> getMap() {
        return wordMap;
    }


    /**
     * Returns the number of nodes behind a specific key
     * @param key
     * @return The number of nodes in the HashSet
     */
    public int getCountOfNodes( String key ) {
        HashSet hs = wordMap.get( key );
        return hs.size();
    }


    /**
     * Returns the largest count of modes in the Value of the supplied Map
     * @param map
     * @return
     */
    public static int getMaximumNodes( AbstractMap<String, HashSet<SortableDefaultMutableTreeNode>> map ) {
        int maxNodes = 0;
        Iterator<Entry<String, HashSet<SortableDefaultMutableTreeNode>>> it = map.entrySet().iterator();
        Entry<String, HashSet<SortableDefaultMutableTreeNode>> pairs;
        while ( it.hasNext() ) {
            pairs = it.next();
            int nodes = ( (AbstractCollection) pairs.getValue() ).size();
            if ( nodes > maxNodes ) {
                maxNodes = nodes;
            }
        }
        return maxNodes;
    }

    private TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> truncatedMap;


    /**
     * Returns a TreeMap of the top truncated nodes.
     * @return
     */
    public TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> getTruncatedMap() {
        return truncatedMap;
    }


    /**
     * Cuts the list of words down to the top terms such as top 30
     * @param limit The maximum allowed terms
     */
    public void truncateToTop( int limit ) {
        if ( valueSortedMap == null ) {
            buildValueSortedMap();
        }
        truncatedMap = new TreeMap<String, HashSet<SortableDefaultMutableTreeNode>>();

        Iterator<Entry<String, HashSet<SortableDefaultMutableTreeNode>>> it = valueSortedMap.entrySet().iterator();
        Entry<String, HashSet<SortableDefaultMutableTreeNode>> pairs;
        int i = 0;
        while ( it.hasNext() && i < limit ) {
            pairs = it.next();
            truncatedMap.put( pairs.getKey(), pairs.getValue() );
            i++;
        }
    }

    private TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> valueSortedMap;


    private void buildValueSortedMap() {
        valueSortedMap = new TreeMap<String, HashSet<SortableDefaultMutableTreeNode>>( new Comparator<String>() {

            public int compare( String k1, String k2 ) {
                int s1 = wordMap.get( k1 ).size();
                int s2 = wordMap.get( k2 ).size();
                if ( !( s1 == s2 ) ) {
                    return ( (Integer) s2 ).compareTo( s1 );
                } else {
                    return k2.compareTo( k1 );
                }
            }
        } );
        valueSortedMap.putAll( wordMap );
    }


    /**
     * The implementing class must return a map of words and their number of occurrences.
     * @return
     */
    public Map<String, Integer> getWordCountMap() {
        HashMap<String, Integer> wordCountMap = new HashMap<String, Integer>();
        Iterator<Entry<String, HashSet<SortableDefaultMutableTreeNode>>> it = wordMap.entrySet().iterator();
        Entry<String, HashSet<SortableDefaultMutableTreeNode>> pairs;
        while ( it.hasNext() ) {
            pairs = it.next();
            wordCountMap.put( pairs.getKey(), pairs.getValue().size() );
        }
        return wordCountMap;
    }


   


    /**
     * Lists the contents of the word to set map
     * @return a dump of the map
     */
    @Override
    public String toString() {
        StringBuffer output = new StringBuffer( "" );
        output.append( "Number of Words: " + Integer.toString( wordMap.size() ) + "\n" );
        output.append( "Larget number of nodes per word: " + Integer.toString( getMaximumNodes( wordMap ) ) + "\n" );
        Iterator<Entry<String, HashSet<SortableDefaultMutableTreeNode>>> it = wordMap.entrySet().iterator();
        Entry<String, HashSet<SortableDefaultMutableTreeNode>> pairs;
        while ( it.hasNext() ) {
            pairs = it.next();
            output.append( pairs.getKey().toString() + " found in  " + Integer.toString( pairs.getValue().size() ) + " nodes.\n" );
        }

        return output.toString();
    }
}
