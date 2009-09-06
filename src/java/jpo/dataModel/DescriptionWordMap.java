package jpo.dataModel;

import jpo.TagCloud.*;
import jpo.dataModel.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    private TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> wordNodeMap = new TreeMap<String, HashSet<SortableDefaultMutableTreeNode>>();


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
                if ( !wordNodeMap.containsKey( s ) ) {
                    nodeSet = new HashSet<SortableDefaultMutableTreeNode>();
                    wordNodeMap.put( s, nodeSet );
                } else {
                    nodeSet = wordNodeMap.get( s );
                }
                nodeSet.add( n );
            }
        }

    }


    /**
     * Returns the Word to Node Set to a caller
     * @return the word to node set map
     */
    public TreeMap<String, HashSet<SortableDefaultMutableTreeNode>> getWordNodeMap() {
        return wordNodeMap;
    }

  
   
    private HashMap<String, Integer> wordCountMap = null;


    /**
     * In this method we return a Map of description terms with the count
     * of nodes they appear in. In order to ensure that subsequent calls to this
     * method return quickly the list is build only once. If it should be
     * rebuilt the wordCountMap variable needs to be set to null;
     * @return A Map with description terms and a count of nodes where the term is found
     */
    public Map<String, Integer> getWordValueMap() {
        if ( wordCountMap == null ) {
            logger.fine( "Building wordCountMap" );
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
