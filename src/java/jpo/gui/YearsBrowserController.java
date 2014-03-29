package jpo.gui;

import jpo.dataModel.YearlyAnalysis;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.dataModel.Settings;

/*
YearlyAnalysisGuiController.java:  The controller that makes the GUI work

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
 * The controller that makes the GUI work
 *
 * @author Richard Eigenmann
 */
public class YearsBrowserController
        implements Serializable {

    private final YearlyAnalysis ya;

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( YearsBrowserController.class.getName() );


    /**
     * Constructor to call to create a new YearlyAnalysisGui
     * @param startNode
     */
    public YearsBrowserController( DefaultMutableTreeNode startNode ) {
        ya = new YearlyAnalysis( startNode );
        YearsBrowser yb = new YearsBrowser();
        yb.setLocationRelativeTo( Settings.anchorFrame );
        yb.setVisible( true );



        JPanel panel = yb.getDisplayPanel();
        panel.setLayout( new FlowLayout() );
        Dimension panelSize = new Dimension( 500, 500 );
        panel.setMaximumSize( panelSize );

        for ( Integer year : ya.getYears() ) {
            panel.add( new YearButton( year ) );
        }
        panel.revalidate();

    }

    /**
     *
     * @author Richard Eigenmann
     */
    class YearButton
            extends JButton
            implements Serializable {

        /**
         * The maximum dynamic width we want to give this button in addition to the minimum width
         */
        int width = 80;

        /**
         * The height for the button
         */
        int height = 80;


        public YearButton( final Integer year ) {
            super( "Button" );
            int count = ya.getYearNodeCount( year );
            setText( String.format( "<html>%d<br>(%d)</html>", year, count ) );
            addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    LOGGER.info( Integer.toString( year ) );
                }
            } );
            setBackground( GradientColor.getColor( GradientColor.BLACK_WHITE_COLORS, (double) count / ya.maxNodesPerMonthInAllYears() ) );
        }


        @Override
        public Dimension getPreferredSize() {
            return new Dimension( width, height );
        }
    }
}
