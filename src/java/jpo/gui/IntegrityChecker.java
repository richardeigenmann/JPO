package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.Timer;


/*
IntegrityChecker.java:  creates a frame and checks the integrity of the collection

Copyright (C) 2002-2009  Richard Eigenmann, Zurich, Switzerland
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
 * IntegrityChecker.java:  creates a frame and checks the integrity of the collection
 *
 **/
public class IntegrityChecker extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( IntegrityChecker.class.getName() );

    JCheckBox checkDatesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "check1" ) );

    JCheckBox check2 = new JCheckBox( Settings.jpoResources.getString( "check2" ) );

    JCheckBox check3 = new JCheckBox( Settings.jpoResources.getString( "check2" ) );

    JButton closeButton =
            new JButton( Settings.jpoResources.getString( "genericOKText" ) );

    Timer timer = new Timer( 15000, new ActionListener() {

        public void actionPerformed( ActionEvent evt ) {
            getRid();
        }
    } );

    /**
     *  reference to the node that should be checked
     */
    SortableDefaultMutableTreeNode startNode;


    /**
     *  Constructor for the window that shows the various checks being performed.
     *
     *
     * @param startNode The node from which to start
     */
    public IntegrityChecker( SortableDefaultMutableTreeNode startNode ) {
        this.startNode = startNode;
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );

        setLocationRelativeTo( Settings.anchorFrame );
        setTitle( Settings.jpoResources.getString( "IntegrityCheckerTitle" ) );

        JPanel jPanel = new JPanel();
        JLabel integrityCheckerLabel =
                new JLabel( Settings.jpoResources.getString( "integrityCheckerLabel" ) );
        jPanel.add( integrityCheckerLabel );
        jPanel.setLayout( new GridLayout( 0, 1 ) );
        checkDatesJCheckBox.setEnabled( true );
        jPanel.add( checkDatesJCheckBox );
        check2.setEnabled( false );
        jPanel.add( check2 );
        check3.setEnabled( false );
        jPanel.add( check3 );
        closeButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        //closeButton.setEnabled( false );
        closeButton.setMaximumSize( Settings.defaultButtonDimension );
        closeButton.setMinimumSize( Settings.defaultButtonDimension );
        closeButton.setPreferredSize( Settings.defaultButtonDimension );
        jPanel.add( closeButton );

        getContentPane().add( jPanel, BorderLayout.CENTER );
        setSize( new Dimension( 300, 150 ) );

        //  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
        Runnable runner = new FrameShower( this );
        EventQueue.invokeLater( runner );

        IntegrityCheckerThread ict = new IntegrityCheckerThread();
    }


    /**
     *  method that closes the frame and gets rid of it
     */
    private void getRid() {
        stopChecks();
        startNode = null;
        setVisible( false );
        dispose();
    }


    /**
     *  This method does the checking
     */
    private void checkIntegrity() {
        // from http://www.rgagnon.com/javadetails/java-0349.html
        Font f = checkDatesJCheckBox.getFont();
        // bold
        //checkDatesJCheckBox.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        // unbold
        //checkDatesJCheckBox.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
        checkDatesJCheckBox.setFont( f.deriveFont( f.getStyle() | Font.ITALIC ) );
        checkDatesJCheckBox.setForeground( Color.black );
        int badDates = checkDates();
        checkDatesJCheckBox.setText( Settings.jpoResources.getString( "check1done" ) + Integer.toString( badDates ) );
        checkDatesJCheckBox.setSelected( true );

        check2.setFont( f.deriveFont( f.getStyle() | Font.ITALIC ) );
        check2.setForeground( Color.black );
        int verifyChecksums = verifyChecksums();
        check2.setSelected( true );
        check2.setText( Settings.jpoResources.getString( "check2done" ) + Integer.toString( verifyChecksums ) );

        check3.setForeground( Color.black );
        check3.setSelected( true );
        //closeButton.setEnabled( true );

        timer.setRepeats( false );
        timer.start();

    }

    /**
     *   Variable that allows the checkDates method to be terminated gracefully
     *   by setting it to false;
     */
    public boolean checkDates = true;


    /**
     *  Checks all the nodes and reports those nodes whose date can't be parsed properly
     * @return The number of dates that could not be parsed correctly.
     */
    private int checkDates() {
        int count = 0;
        //new YearlyAnalysisGuiController( startNode );
        new YearsBrowserController( startNode );


        /*       SortableDefaultMutableTreeNode testNode;
        Object nodeObject;
        PictureInfo pi;
        Calendar cal;
        HashMap<Integer, HashMap<Integer, HashSet<SortableDefaultMutableTreeNode>>> yearsMap = new HashMap<Integer, HashMap<Integer, HashSet<SortableDefaultMutableTreeNode>>>();
        for ( Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements() && checkDates; ) {
        testNode = (SortableDefaultMutableTreeNode) e.nextElement();
        nodeObject = testNode.getUserObject();
        if ( ( nodeObject instanceof PictureInfo ) ) {
        pi = (PictureInfo) nodeObject;
        if ( pi.getCreationTimeAsDate() == null ) {
        //logger.info( "IntegrityChecker.checkDates: Can't parse date: " + pi.getCreationTime() + " from Node: " + pi.getDescription() );
        count++;
        } else {
        //logger.info( "IntegrityChecker.checkDates:" + pi.getFormattedCreationTime() + " from " + pi.getCreationTime() + " from Node: " + pi.getDescription() );
        cal = pi.getCreationTimeAsDate();
        if ( cal != null ) {
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH );
        HashMap<Integer, HashSet<SortableDefaultMutableTreeNode>> monthMap = yearsMap.get( year );
        if ( monthMap == null ) {
        monthMap = new HashMap<Integer, HashSet<SortableDefaultMutableTreeNode>>();
        //monthMap.put( new Integer( month ), new HashSet<SortableDefaultMutableTreeNode>() );
        yearsMap.put( year, monthMap );
        }
        HashSet<SortableDefaultMutableTreeNode> nodes = monthMap.get( month );
        if ( nodes == null ) {
        nodes = new HashSet<SortableDefaultMutableTreeNode>();
        }
        nodes.add( testNode );
        monthMap.put( month, nodes );
        //System.out.printf( "Picture %s date %tc has year %d month %d\n", pi.getHighresFilename(), cal, year, month );
        }
        }
        }
        }
        for ( Integer year : yearsMap.keySet() ) {
        HashMap<Integer, HashSet<SortableDefaultMutableTreeNode>> monthMap = yearsMap.get( year );
        for ( Integer month : monthMap.keySet() ) {
        HashSet<SortableDefaultMutableTreeNode> nodes = monthMap.get( month );
        System.out.printf( "Found year %d - %d with count %d\n", year, month, nodes.size() );
        }
        } */
        return count;
    }

    /**
     *   Variable that allows the verifyChecksumsFlag method to be terminated gracefully
     *   by setting it to false;
     */
    public boolean verifyChecksumsFlag = false;


    /**
     *  Checks all the nodes and adds checksums for those nodes that didn't have one
     */
    private int verifyChecksums() {
        int count = 0;
        SortableDefaultMutableTreeNode testNode;
        PictureInfo pi;
        Object nodeObject;
        long checksum;
        for ( Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements() && verifyChecksumsFlag; ) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = testNode.getUserObject();
            if ( ( nodeObject instanceof PictureInfo ) ) {
                pi = (PictureInfo) nodeObject;
                File f = pi.getHighresFile();
                if ( f != null ) {
                    checksum = Tools.calculateChecksum( f );
                    if ( pi.getChecksum() != checksum ) {
                        pi.setChecksum( checksum );
                        count++;
                        if ( count % 10 == 0 ) {
                            check2.setText( Settings.jpoResources.getString( "check2progress" ) + Integer.toString( count ) );
                        }
                    }
                }
            }
        }
        return count;
    }


    /**
     *  This method sets the boolean variables to false that will stop the integrity checkers.
     */
    public void stopChecks() {
        checkDates = false;
        verifyChecksumsFlag = false;
    }

    /**
     *  This class allows the integrity check to run in it's own thread.
     *
     */
    private class IntegrityCheckerThread implements Runnable {

        /**
         *  Constructor for the thread
         */
        public IntegrityCheckerThread() {
            Thread t = new Thread( this );
            t.start();
        }


        /**
         *  this is run in it's own thread
         */
        public void run() {
            checkIntegrity();
        }
    }
}
