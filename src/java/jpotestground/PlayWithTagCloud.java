/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpotestground;

import java.util.Map;
import jpo.TagCloud.TagClickListener;
import jpo.TagCloud.WordBrowser;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jpo.TagCloud.WordMap;

/**
 *Test
 * @author richi
 */
public class PlayWithTagCloud extends JFrame implements TagClickListener {

    private static Logger logger = Logger.getLogger( PlayWithTagCloud.class.getName() );


    /**
     * Static Initialiser to turn on finest logging
     */
    {
        Handler[] handlers =
                Logger.getLogger( "" ).getHandlers();
        for ( int index = 0; index < handlers.length; index++ ) {
            handlers[index].setLevel( Level.FINEST );
        }
    }


    public PlayWithTagCloud() {
        setPreferredSize( new Dimension( 400, 400 ) );
        wb = new WordBrowser( words, this, 10 );

        this.getContentPane().add( wb );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        pack();
        setVisible( true );
    }


    public static void main( String[] args ) {
        Runnable r = new Runnable() {

            public void run() {
                new PlayWithTagCloud();
            }
        };
        SwingUtilities.invokeLater( r );


    }

    WordBrowser wb;

    public void tagClicked( String key ) {
        logger.info( "Tag Clicked: " + key + " associated count: " + words.getWordCountMap().get( key ).toString() );
        words = new Countries();
        wb.setWordMap( words );
    }

    private WordMap words = new Cities();

    public class Cities extends WordMap {

        @Override
        public Map<String, Integer> getWordCountMap() {
            // taken from http://en.wikipedia.org/wiki/List_of_cities_proper_by_population
            Map<String, Integer> cities = new HashMap<String, Integer>();
            cities.put( "Mumbai", 13922125 );
            cities.put( "Shanghai", 13831900 );
            cities.put( "Karachi", 12991000 );
            cities.put( "Delhi", 12259230 );
            cities.put( "Istanbul", 11372613 );
            cities.put( "São Paulo", 10990249 );
            cities.put( "Moscow", 10452000 );
            cities.put( "Seoul", 10421782 );
            cities.put( "Beijing", 10123000 );
            cities.put( "Mexico City", 8836045 );
            cities.put( "Tokyo", 8731000 );
            cities.put( "Jakarta", 8489910 );
            cities.put( "New York City", 8310212 );
            cities.put( "Wuhan", 8001541 );
            cities.put( "Lagos", 7937932 );
            cities.put( "Kinshasa", 784000 );
            cities.put( "Tehran", 7797520 );
            cities.put( "Lima", 7605742 );
            cities.put( "London", 7556900 );
            cities.put( "Bogotá", 7155052 );
            cities.put( "Hong Kong", 6985200 );
            cities.put( "Bangkok", 6972000 );
            cities.put( "Cairo", 6758581 );
            cities.put( "Dhaka", 6737774 );
            cities.put( "Ho Chi Minh City", 6650942 );
            cities.put( "Lahore", 6318745 );
            cities.put( "Guangzhou", 6172839 );
            cities.put( "Rio de Janeiro", 6161047 );
            cities.put( "Tianjin", 5800000 );
            cities.put( "Baghdad", 5337684 );
            cities.put( "Bangalore", 5310318 );
            cities.put( "Kolkata", 5080519 );
            cities.put( "Santiago", 4985893 );
            cities.put( "Singapore", 4839400 );
            cities.put( "Chongqing", 4776027 );
            cities.put( "Saint Petersburg", 4596000 );
            cities.put( "Chennai", 4590267 );
            cities.put( "Riyadh", 4465000 );
            cities.put( "Surat", 4274429 );
            cities.put( "Alexandria", 4110015 );
            cities.put( "Shenyang", 4101197 );
            cities.put( "Yangon", 4088000 );
            cities.put( "Hyderabad", 4025335 );
            cities.put( "Ahmedabad", 3913793 );
            cities.put( "Ankara", 3901201 );
            cities.put( "Johannesburg", 3888180 );
            cities.put( "Los Angeles", 3849378 );
            cities.put( "Abidjan", 3802000 );
            cities.put( "Yokohama", 3650000 );
            cities.put( "Busan", 3615101 );
            cities.put( "Cape Town", 3497097 );
            cities.put( "Durban", 3468086 );
            cities.put( "Berlin", 3426354 );
            cities.put( "Pune", 3337481 );
            cities.put( "Pyongyang", 3255388 );
            cities.put( "Madrid", 3213271 );
            cities.put( "Kanpur", 3144267 );
            cities.put( "Jaipur", 3102808 );
            cities.put( "Buenos Aires", 3050728 );
            cities.put( "Nairobi", 3038553 );
            cities.put( "Jeddah", 3012000 );

            return cities;
        }
    }

    class Countries extends WordMap {

        @Override
        public Map<String, Integer> getWordCountMap() {
            // taken from http://en.wikipedia.org/wiki/List_of_cities_proper_by_population
            Map<String, Integer> countries = new HashMap<String, Integer>();

            countries.put( "People's Republic of China", 1332120000 );
            countries.put( "India", 1167020000 );
            countries.put( "United States", 307033000 );
            countries.put( "Indonesia", 230781846 );
            countries.put( "Brazil", 191615000 );
            countries.put( "Pakistan", 167047000 );
            countries.put( "Bangladesh", 162221000 );
            countries.put( "Nigeria", 154729000 );
            countries.put( "Russia", 141868000 );
            countries.put( "Japan", 127580000 );
            countries.put( "Mexico", 107550697 );
            countries.put( "Philippines", 92226600 );
            countries.put( "Vietnam", 88069000 );
            countries.put( "Germany", 82046000 );
            countries.put( "Ethiopia", 79221000 );
            countries.put( "Egypt", 76978937 );
            countries.put( "Iran", 74196000 );
            countries.put( "Turkey", 71517100 );
            countries.put( "Dem. Rep. of Congo", 66020000 );
            countries.put( "France", 65073482 );
            countries.put( "Thailand", 63389730 );
            countries.put( "United Kingdom", 61634599 );
            countries.put( "Italy", 60067554 );
            countries.put( "Myanmar (Burma)", 50020000 );
            countries.put( "South Africa", 48697000 );
            countries.put( "South Korea", 48333000 );
            countries.put( "Ukraine", 46143700 );
            countries.put( "Spain", 45828172 );
            countries.put( "Colombia", 45025388 );
            countries.put( "Tanzania", 43739000 );
            return countries;
        }
    }
}
