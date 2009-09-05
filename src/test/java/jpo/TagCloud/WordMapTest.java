package jpo.TagCloud;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class WordMapTest extends TestCase {

    public WordMapTest( String testName ) {
        super( testName );
    }

    private class Fruit extends WordMap {

        @Override
        public Map<String, Integer> getWordValueMap() {
            Map<String, Integer> fruit = new HashMap<String, Integer>();
            fruit.put( "Apples", 4 );
            fruit.put( "Pears", 2 );
            fruit.put( "Oranges", 9 );
            fruit.put( "Lemons", 5 );
            fruit.put( "Pineapples", 1 );
            return fruit;
        }
    }

    private class EmptyWordMap extends WordMap {

        @Override
        public Map<String, Integer> getWordValueMap() {
            Map<String, Integer> emptyMap = new HashMap<String, Integer>();
            return emptyMap;
        }
    }

    private class ZeroValueMap extends WordMap {

        @Override
        public Map<String, Integer> getWordValueMap() {
            Map<String, Integer> zeroValueMap = new HashMap<String, Integer>();
            zeroValueMap.put( "Apples", -1 );
            zeroValueMap.put( "Pears", -8 );
            zeroValueMap.put( "Oranges", -2 );
            zeroValueMap.put( "Lemons", 0 );
            return zeroValueMap;
        }
    }


    /**
     * Test of getMaximumWordValue method, of class DescriptionWordMap.
     */
    public void testGetMaximumCount() {
        WordMap fruit = new Fruit();
        int max = fruit.getMaximumWordValue();
        assertEquals( "Expecting Oranges to be the top counter with a count of 9", 9, max );

        WordMap emptyMap = new EmptyWordMap();
        int maxEmpty = emptyMap.getMaximumWordValue();
        assertEquals( "Expecting MAXINT as the maximum of an empty map", Integer.MAX_VALUE, maxEmpty );

        WordMap zeroMap = new ZeroValueMap();
        int maxZero = zeroMap.getMaximumWordValue();
        assertEquals( "Expecting MAXINT as the maximum of a map with max value 0", Integer.MAX_VALUE, maxZero );
    }


    /**
     * Test of getTruncatedMap method, of class DescriptionWordMap.
     */
    public void testGetTopWords() {
        WordMap fruit = new Fruit();
        TreeSet<String> topTwoWords = fruit.getTopWords( 2 );
        String firstWord = topTwoWords.first();
        // Leomons come first because they are alphabetically before Oranges
        assertEquals( "Verifying the first word", "Lemons", firstWord );

        String lastWord = topTwoWords.last();
        assertEquals( "Verifying the last word", "Oranges", lastWord );
    }
}
