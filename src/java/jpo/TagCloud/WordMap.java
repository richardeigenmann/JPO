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
WordMap.java:  A class that spplies the list of words and a count to the tag cloud

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
 * This class defines the abstract getWordValueMap method that must be
 * written when extending this class to feed a list of words into the Tag Cloud.
 * It has the functionality to order the words by
 * the count so that a list of the top n words can be selected.
 * In order to optimise speed this class caches the valueSortedTreeMap. To signal
 * that the map of words has changed, call the {@link #rebuild} method.
 * TODO: Write testcases for this
 * @author Richard Eigenmann
 */
public abstract class WordMap {

    /**
     * The implementing class must return a map of words and a count. The list of
     * words does not have to be sorted in any way.
     * @return the map of word and value pairs
     */
    public abstract Map<String, Integer> getWordValueMap();

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( WordMap.class.getName() );


    /**
     * Empties the cache of the sorted-by-value word list and rebuilds it,
     */
    public void rebuild() {
        valueSortedTreeMap = null;
        getValueSortedMap();
    }

    /**
     * Cache of the sorted-by-value list of words.
     */
    private TreeMap<String, Integer> valueSortedTreeMap = null;


    /**
     * This method returns a TreeMap of the words retrieved from @see #getWordValueMap
     * sorted descendingly by the number in the value of each entry. It caches the result
     * in a private TreeMap variable and returns this on each subsequent call. If
     * the source words change you need to call @see #rebuild.
     * TODO: This is very slow!
     *
     * @return The Map retrieved from getWordValueMap sorted by the count.
     */
    public TreeMap<String, Integer> getValueSortedMap() {
        //logger.fine( this.getClass().getName(), "getValueSortedMap" );
        if ( valueSortedTreeMap == null ) {
            logger.fine( "valueSortedTreeMap doesn't exist. Building..." );
            valueSortedTreeMap = new TreeMap<String, Integer>( new Comparator<String>() {

                /**
                 * override the compare method so that we can have the TreeMap sorted by value instead of by word
                 */
                @Override
                public int compare( String key1, String key2 ) {
                    //logger.finest( String.format( "Comparing %s with %s", key1, key2 ));
                    int s1 = getWordValueMap().get( key1 );
                    int s2 = getWordValueMap().get( key2 );
                    if ( !( s1 == s2 ) ) {
                        return ( (Integer) s2 ).compareTo( s1 );
                    } else {
                        return key2.compareTo( key1 );
                    }
                }
            } );
            valueSortedTreeMap.putAll( getWordValueMap() );
            logger.fine( String.format( "valueSortedTreeMap built with %d entries", valueSortedTreeMap.size() ) );
        }
        return valueSortedTreeMap;
    }


    /**
     * Returns a alphabetical set of the highest ranking n words
     * @param limit The maximum allowed terms
     * @return an alphabetical set of the highest ranking n words
     */
    public TreeSet<String> getTopWords( int limit ) {
        TreeSet<String> topWords = new TreeSet<String>();
        if ( limit == 0 ) {
            topWords.add( "" );
            return topWords;
        }

        Iterator<Entry<String, Integer>> it = getValueSortedMap().entrySet().iterator();
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
     * Returns the highest number associated with a word. If the list of words is
     * empty then Integer.MAX_VALUE is returned (this is better than 0 which is likely to
     * cause a division by zero error in someone's code). Likewise if the largest number
     * is 0 then we will also return Integer.MAX_VALUE for the same reason.
     * @return the highest number associated with a word
     */
    public int getMaximumWordValue() {
        TreeMap<String, Integer> valueSortedMap = getValueSortedMap();
        if ( valueSortedMap.isEmpty() ) {
            return Integer.MAX_VALUE;
        }
        int maxValue = valueSortedMap.firstEntry().getValue();
        if ( maxValue == 0 ) {
            maxValue = Integer.MAX_VALUE;
        }
        return maxValue;
    }


    /**
     * Enumerates the WordMap which helps with debugging
     * @return Returns all the words for debugging
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer( "" );
        Iterator<Entry<String, Integer>> it = getWordValueMap().entrySet().iterator();
        Entry<String, Integer> pairs;
        int i = 0;
        while ( it.hasNext() ) {
            pairs = it.next();
            sb.append( String.format( "%s %d, ", pairs.getKey(), pairs.getValue() ) );
            i++;
        }
        return sb.toString();
    }

  
}
