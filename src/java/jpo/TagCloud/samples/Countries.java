package jpo.TagCloud.samples;

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
 * This class extends the WordMap with a list of countries and their population
 * taken form Wikipedia to illustrate the TagCloud.
 *
 * @see <a href="http://en.wikipedia.org/wiki/List_of_countries_by_population">Wikipedia List of countries by population</a>
 * @author Richard Eigenmann
 */
public class Countries extends WordMap {

    @Override
    public Map<String, Integer> getWordValueMap() {
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
