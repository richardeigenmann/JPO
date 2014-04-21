package jpotestground.TagCloudExperiment;

import java.util.HashMap;
import java.util.Map;
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
 * This class extends the WordMap with a list of cities and their population
 * taken form Wikipedia to illustrate the TagCloud.
 *
 * @see <a
 * href="http://en.wikipedia.org/wiki/List_of_cities_proper_by_population">Wikipedia
 * Cities by Population</a>
 * @author Richard Eigenmann
 */
public class Cities extends WordMap {

    @Override
    public Map<String, Integer> getWordValueMap() {
        Map<String, Integer> cities = new HashMap<>();
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
