package jpo.TagCloud;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
WordMap.java:  A class that spplies the list of words to the tag cloud

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
 * This class defines the abstract getWordCountMap method that must be
 * written when extending this class to feed a list of words into the Tag Cloud.
 * It has the functionality in the getValueSortedMap method to order the words by
 * the count so that a list of the top n words can be selected.
 * In order to optimise speed this class caches the valueSortedTreeMap. To signal
 * that the map of words has changed, call the @see #rebuild method.
 *
 * @author Richard Eigenmann
 */
public abstract class WordMap {

    /**
     * The implementing class must return a map of words and their number of occurrences.
     * @return
     */
    public abstract Map<String, Integer> getWordCountMap();

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( WordMap.class.getName() );


    {
        logger.setLevel( Level.ALL );
    }


    public void rebuild() {
        valueSortedTreeMap = null;
        getValueSortedMap();
    }

    private TreeMap<String, Integer> valueSortedTreeMap = null;


    /**
     * This method returns a TreeMap of the words retrieved from @see #getWordCountMap
     * sorted descendingly by the number in the value of each entry. It caches the result
     * in a private TreeMap variable and returns this on each subsequent call. If
     * the source words change you need to call @see #rebuild.
     *
     * @return The Map retrieved from getWordCountMap sorted by the count.
     */
    public TreeMap<String, Integer> getValueSortedMap() {
        logger.entering( this.getClass().getName(), "getValueSortedMap" );
        if ( valueSortedTreeMap == null ) {
            logger.fine( "valueSortedTreeMap doesn't exist. Building..." );
            valueSortedTreeMap = new TreeMap<String, Integer>( new Comparator<String>() {

                public int compare( String key1, String key2 ) {
                    int s1 = getWordCountMap().get( key1 );
                    int s2 = getWordCountMap().get( key2 );
                    if ( !( s1 == s2 ) ) {
                        return ( (Integer) s2 ).compareTo( s1 );
                    } else {
                        return key2.compareTo( key1 );
                    }
                }
            } );
            valueSortedTreeMap.putAll( getWordCountMap() );
            logger.fine( String.format( "valueSortedTreeMap built with %d entries", valueSortedTreeMap.size() ) );
        }
        return valueSortedTreeMap;
    }


    /**
     * Cuts the list of words down to the top terms such as top 30
     * @param valueSortedTreeMap
     * @param limit The maximum allowed terms
     * @return
     */
    public static TreeSet<String> getTopWords( TreeMap<String, Integer> valueSortedTreeMap, int limit ) {
        TreeSet<String> topWords = new TreeSet<String>();

        Iterator<Entry<String, Integer>> it = valueSortedTreeMap.entrySet().iterator();
        Entry<String, Integer> pairs;
        int i = 0;
        while ( it.hasNext() && i < limit ) {
            pairs = it.next();
            topWords.add( pairs.getKey() );
            i++;
        }
        return topWords;
    }


    /**
     * Returns the largest count of modes in the Value of the supplied Map
     * @return
     */
    public int getMaximumCount() {
        return getValueSortedMap().firstEntry().getValue();
    }
}
