package org.jpo.dataModel;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2009-2017  Richard Eigenmann.
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
 * Builds a TreeMap by year and month of the nodes under the input node. The
 * first TreeMap has keys by year and the value is a second TreeMap. The second
 * map has the key by month (starting with 0) and a value with the nodes that
 * fall into that year and month.
 *
 * @author Richard Eigenmann
 */
public class YearlyAnalysis implements Serializable {

    /**
     * Keep serialisation happy
     */
    private static final long serialVersionUID = 1;

    /**
     * The starting node in the model to analyse.
     */
    private final DefaultMutableTreeNode startNode;


    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( YearlyAnalysis.class.getName() );

    /**
     * Constructor for a YearlyAnalysis Object. Create it with a startNode and
     * then pick up the TreeMap with the getMap() method.
     *
     * @param startNode Start Node
     */
    public YearlyAnalysis( DefaultMutableTreeNode startNode ) {
        this.startNode = startNode;
        buildMaps();
    }

    /**
     * The target data structure.
     */
    private TreeMap<Integer, TreeMap<Integer, HashSet<DefaultMutableTreeNode>>> yearsMap;

    /**
     * Builds the TreeMaps.
     */
    private void buildMaps() {
        DefaultMutableTreeNode testNode;
        Object nodeObject;
        PictureInfo pi;
        Calendar cal;
        yearsMap = new TreeMap<>();
        for ( Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            testNode = (DefaultMutableTreeNode) e.nextElement();
            nodeObject = testNode.getUserObject();
            if ( ( nodeObject instanceof PictureInfo ) ) {
                pi = (PictureInfo) nodeObject;
                if ( pi.getCreationTimeAsDate() != null ) {
                    LOGGER.log( Level.FINE, "IntegrityChecker.checkDates:{0} from {1} from Node: {2}", new Object[]{ pi.getFormattedCreationTime(), pi.getCreationTime(), pi.getDescription() });
                    cal = pi.getCreationTimeAsDate();
                    if ( cal != null ) {
                        int year = cal.get( Calendar.YEAR );
                        int month = cal.get( Calendar.MONTH );
                        TreeMap<Integer, HashSet<DefaultMutableTreeNode>> monthMap = yearsMap.computeIfAbsent(year, k -> new TreeMap<>());
                        //monthMap.put( new Integer( month ), new HashSet<SortableDefaultMutableTreeNode>() );
                        HashSet<DefaultMutableTreeNode> nodes = monthMap.get( month );
                        if ( nodes == null ) {
                            nodes = new HashSet<>();
                        }
                        nodes.add( testNode );
                        if ( nodes.size() > maxNodes ) {
                            maxNodes = nodes.size();
                        }
                        monthMap.put( month, nodes );
                    }
                }
            }
        }
    }

    /**
     * Counter that keeps track of the highest number of nodes in the map
     */
    private int maxNodes;  // default is 0

    /**
     * Returns the maximum number of nodes in all years
     *
     * @return The maximum number of nodes in all years
     */
    public int maxNodesPerMonthInAllYears() {
        return maxNodes;
    }

    /**
     * This method returns the results of the analysis. It is a TreeMap where
     * each element is another TreeMap where the next level down is a HashSet of
     * nodes.
     *
     * @return the data model with the nodes
     */
    public TreeMap<Integer, TreeMap<Integer, HashSet<DefaultMutableTreeNode>>> getYearMap() {
        return yearsMap;
    }

    /**
     * Returns a set of Integers representing the years in the analysis.
     *
     * @return The years in the analysis.
     */
    public Set<Integer> getYears() {
        return getYearMap().keySet();
    }

    /**
     * Returns the number of nodes in a year
     *
     * @param year The year to be counted
     * @return The number of nodes in a year
     */
    public int getYearNodeCount( Integer year ) {
        int count = 0;
        for ( Integer month : getMonths( year ) ) {
            count += getMonthNodeCount( year, month );
        }
        return count;
    }

    /**
     * Returns the number of nodes in a month of a year
     *
     * @param year The year to be counted
     * @param month The month to be counted
     * @return The number of nodes in a month of the year
     */
    public int getMonthNodeCount( Integer year, Integer month ) {
        LOGGER.log( Level.FINE, "{0}  {1}", new Object[]{ year, month });
        try {
            return getNodes( year, month ).size();
        } catch ( NullPointerException ex ) {
            LOGGER.log( Level.INFO, "Got a NPE on Year {0} Month {1}", new Object[]{ year, month });
            return 0;
        }
    }

    /**
     * Returns a map with the months of the specified year and a set of the
     * nodes in each months
     *
     * @param year The year for which the months are to be returned
     * @return The map with the results
     */
    public TreeMap<Integer, HashSet<DefaultMutableTreeNode>> getMonthMap( Integer year ) {
        return yearsMap.get( year );
    }

    /**
     * Returns a set of Integers representing the months in the analysis.
     *
     * @param year The year for which the months set should be returned
     * @return The months in the analysis.
     */
    public Set<Integer> getMonths( Integer year ) {
        return getMonthMap( year ).keySet();
    }

    /**
     * Returns a string representing the name of the month
     *
     * @param month Month
     * @return The name of the month
     */
    public static String getMonthName( Integer month ) {
        final String[] monthName = { "January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December" };

        return monthName[month];
    }

    /**
     * Returns the set of nodes for a year and month.
     *
     * @param year The year for which to provide the nodes
     * @param month The month for which to provide the nodes
     * @return The set for the year and month
     */
    public Set<DefaultMutableTreeNode> getNodes( Integer year, Integer month ) {
        return getMonthMap( year ).get( month );
    }

    /**
     * Simple dump method to help debug the contents of the map.
     *
     * @return a long string with the dump of the maps.
     */
    @Override
    public String toString() {
        String[] monthName = { "January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December" };
        StringBuilder sb = new StringBuilder();
        for ( Integer year : getYears() ) {
            sb.append( String.format( "Year: %4d%n", year ) );
            for ( Integer month : getMonths( year ) ) {
                sb.append( String.format( "      %s  has  %d  nodes%n", monthName[month], getNodes( year, month ).size() ) );
            }

        }
        return sb.toString();
    }
}
