package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.TextQuery;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.dataModel.QueryNavigator;
import jpo.gui.Jpo.ApplicationEventHandler;


/*
QueryJFrame.java:  creates a GUI to allow the user to specify his search

Copyright (C) 2002-2010  Richard Eigenmann.
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
 * QueryJFrame.java:  creates a GUI to allow the user to specify his search
 *
 **/
public class QueryJFrame
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( QueryJFrame.class.getName() );

    /**
     *  reference to the node that should be checked
     */
    SortableDefaultMutableTreeNode startSearchNode;

    /**
     *  the string that is searched for in all texts.
     */
    private JTextField anyFieldJTextField = new JTextField();

    /**
     *  the component that says whether the results should be added to the
     *  tree or not
     */
    //private JCheckBox saveResults = new JCheckBox( Settings.jpoResources.getString("searchDialogSaveResultsLabel"), true);
    /**
     *  the lower date for a specified range
     */
    private JTextField lowerDateJTextField = new JTextField( "" );

    /**
     *  the upper date for a specified range
     */
    private JTextField upperDateJTextField = new JTextField( "" );

    //private static final Dimension compactSize = new Dimension( 300, 350 );
    //private static final Dimension advancedSize = new Dimension( 300, 550 );

    /**
     *  Creates a GUI to specify the search criteria.
     *
     *
     * @param startSearchNode
     * @param collectioController The main controller for the collection
     */
    public QueryJFrame( SortableDefaultMutableTreeNode startSearchNode, ApplicationEventHandler collectioController ) {
        this.startSearchNode = startSearchNode;
        this.applicationEventHandler = collectioController;
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );

        setTitle( Settings.jpoResources.getString( "searchDialogTitle" ) );

        JPanel jPanel = new JPanel();
        jPanel.setLayout( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;

        //JLabel searchJLabel = new JLabel( Settings.jpoResources.getString("searchDialogLabel") );
        //jPanel.add( searchJLabel );

        anyFieldJTextField.setPreferredSize( new Dimension( 200, 40 ) );
        anyFieldJTextField.setMinimumSize( new Dimension( 200, 40 ) );
        anyFieldJTextField.setMaximumSize( new Dimension( 600, 40 ) );
        anyFieldJTextField.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "searchDialogLabel" ) ) );

        jPanel.add( anyFieldJTextField, c );

        final JLabel lowerDateJLabel = new JLabel( Settings.jpoResources.getString( "lowerDateJLabel" ) );

        final JPanel dateRange = new JPanel();


        final JButton advancedFindJButton = new JButton( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );
        advancedFindJButton.addActionListener( new ActionListener() {

            private String savedLowerDateValue = Tools.currentDate( "dd.MM.yyyy" );

            private String savedUpperDateValue = Tools.currentDate( "dd.MM.yyyy" );


            public void actionPerformed( ActionEvent evt ) {
                if ( dateRange.isVisible() ) {
                    dateRange.setVisible( false );

                    savedLowerDateValue = lowerDateJTextField.getText();
                    lowerDateJTextField.setText( "" );

                    savedUpperDateValue = upperDateJTextField.getText();
                    upperDateJTextField.setText( "" );

                    advancedFindJButton.setText( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );
                    //setSize( compactSize );
                } else {
                    dateRange.setVisible( true );

                    lowerDateJTextField.setText( savedLowerDateValue );
                    lowerDateJTextField.setVisible( true );

                    upperDateJTextField.setText( savedUpperDateValue );
                    upperDateJTextField.setVisible( true );
                    advancedFindJButton.setText( Settings.jpoResources.getString( "advancedFindJButtonClose" ) );
                    //setSize( advancedSize );
                }
                //validate();
                pack();
            }
        } );
        c.gridx++;
        jPanel.add( advancedFindJButton, c );


        lowerDateJTextField.setPreferredSize( new Dimension( 100, 25 ) );
        lowerDateJTextField.setMinimumSize( new Dimension( 100, 25 ) );
        dateRange.add( lowerDateJTextField );

        upperDateJTextField.setPreferredSize( new Dimension( 100, 25 ) );
        upperDateJTextField.setMinimumSize( new Dimension( 100, 25 ) );
        dateRange.add( upperDateJTextField );

        dateRange.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "lowerDateJLabel" ) ) );


        //lowerDateJLabel.setVisible( false );
        dateRange.setVisible( false );
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        jPanel.add( dateRange, c );



        //c.gridy++;
        //jPanel.add( saveResults, c );


        JButton okJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
        JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );

        // crate a JPanel for the buttons
        JPanel buttonJPanel = new JPanel();

        // add the ok button
        okJButton.setPreferredSize( new Dimension( 120, 25 ) );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( new Dimension( 120, 25 ) );
        okJButton.setDefaultCapable( true );
        this.getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                runQuery();
            }
        } );
        buttonJPanel.add( okJButton );

        // add the cancel button
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        buttonJPanel.add( cancelJButton );
        c.gridy++;
        jPanel.add( buttonJPanel, c );

        getContentPane().add( jPanel, BorderLayout.CENTER );
        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible(true);
    }

    /**
     * reference to the main application Event Handler so that we can delegate stuff to
     */
    private ApplicationEventHandler applicationEventHandler;


    /**
     *  method that closes te frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }


    /**
     *  method that runs the Query
     */
    private void runQuery() {
        if ( !( startSearchNode.getUserObject() instanceof GroupInfo ) ) {
            logger.info( "QueryJFrame.runQuery: can only be invoked on GroupInfo nodes! Ignoring request. You are on node: " + this.toString() );
            return;
        }

        TextQuery q = new TextQuery( anyFieldJTextField.getText() );
        q.setLowerDateRange( Tools.parseDate( lowerDateJTextField.getText() ) );
        q.setUpperDateRange( Tools.parseDate( upperDateJTextField.getText() ) );
        q.setStartNode( startSearchNode );

        if ( ( q.getLowerDateRange() != null ) && ( q.getUpperDateRange() != null ) && ( q.getLowerDateRange().compareTo( q.getUpperDateRange() ) > 0 ) ) {
            JOptionPane.showMessageDialog(
                    this,
                    Settings.jpoResources.getString( "dateRangeError" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }


        DefaultMutableTreeNode newNode = Settings.pictureCollection.addQueryToTreeModel( q );
        applicationEventHandler.showQuery( newNode );

        QueryNavigator queryBrowser = new QueryNavigator( q );
        Jpo.showThumbnails( queryBrowser );

        getRid();
    }
}
