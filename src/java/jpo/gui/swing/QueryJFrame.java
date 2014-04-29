package jpo.gui.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.TextQuery;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowQueryRequest;
import net.miginfocom.swing.MigLayout;


/*
 QueryJFrame.java:  creates a GUI to allow the user to specify his search

 Copyright (C) 2002-2014  Richard Eigenmann.
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
 * QueryJFrame.java: creates a GUI to allow the user to specify his search
 *
 *
 */
public class QueryJFrame
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( QueryJFrame.class.getName() );

    /**
     * reference to the node that should be checked
     */
    private final SortableDefaultMutableTreeNode startSearchNode;


    //private static final Dimension compactSize = new Dimension( 300, 350 );
    //private static final Dimension advancedSize = new Dimension( 300, 550 );
    /**
     * Creates a Frame to specify the search criteria.
     *
     *
     * @param startSearchNode
     */
    public QueryJFrame( SortableDefaultMutableTreeNode startSearchNode ) {
        if ( !( startSearchNode.getUserObject() instanceof GroupInfo ) ) {
            LOGGER.log( Level.INFO, "Method can only be invoked on GroupInfo nodes! Ignoring request. You are on node: {0}", this.toString());
            getRid();
        }

        this.startSearchNode = startSearchNode;
        initWidgets();
    }

    /**
     * the string that is searched for in all texts.
     */
    private final JTextField searchStringJTextField = new JTextField( 20 );

    private final JButton advancedFindJButton = new JButton( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );
    
    private final JLabel dateRangeJLabel = new JLabel( Settings.jpoResources.getString( "lowerDateJLabel" ) );
    /**
     * the lower date for a specified range
     */
    private final JTextField lowerDateJTextField = new JTextField( 10 );

    /**
     * the upper date for a specified range
     */
    private final JTextField upperDateJTextField = new JTextField( 10 );


    private void initWidgets() {
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        setTitle( Settings.jpoResources.getString( "searchDialogTitle" ) );

        final JPanel jPanel = new JPanel();
        jPanel.setLayout( new MigLayout() );

        jPanel.add( new JLabel( Settings.jpoResources.getString( "searchDialogLabel" ) ) );
        jPanel.add( searchStringJTextField, "wrap, pushx, growx" );

        final JPanel dateRange = new JPanel();

        advancedFindJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent evt ) {
                toggleAdvancedCriteria( dateRange );
            }
        } );
        jPanel.add( advancedFindJButton, "wrap" );

        jPanel.add( dateRangeJLabel, "hidemode 1");
        jPanel.add( lowerDateJTextField, "wrap, hidemode 1" );
        jPanel.add( upperDateJTextField, "skip, wrap, hidemode 1" );
        setAdvancedOptionsVisible( false );

        JButton okJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
        okJButton.setPreferredSize( new Dimension( 120, 25 ) );
        okJButton.setDefaultCapable( true );
        this.getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                handleOkButtonClick();
            }
        } );
        jPanel.add( okJButton, "tag ok, skip, right" );

        add( jPanel );
        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    private String savedLowerDateValue;
    private String savedUpperDateValue;

    /**
     * Show or hide the advanced options
     * @param choice 
     */
    private void setAdvancedOptionsVisible( boolean choice ) {
        dateRangeJLabel.setVisible( choice );
        lowerDateJTextField.setVisible( choice );
        upperDateJTextField.setVisible( choice );
    }

    private void toggleAdvancedCriteria( JPanel dateRange ) {
        savedLowerDateValue = Tools.currentDate( "dd.MM.yyyy" );
        savedUpperDateValue = Tools.currentDate( "dd.MM.yyyy" );

        if ( dateRangeJLabel.isVisible() ) {
            savedLowerDateValue = lowerDateJTextField.getText();
            lowerDateJTextField.setText( "" );

            savedUpperDateValue = upperDateJTextField.getText();
            upperDateJTextField.setText( "" );

            advancedFindJButton.setText( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );
        } else {
            lowerDateJTextField.setText( savedLowerDateValue );
            lowerDateJTextField.setVisible( true );

            upperDateJTextField.setText( savedUpperDateValue );
            upperDateJTextField.setVisible( true );
            advancedFindJButton.setText( Settings.jpoResources.getString( "advancedFindJButtonClose" ) );
        }
        setAdvancedOptionsVisible( ! dateRangeJLabel.isVisible() );
        pack();
    }

    /**
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * method that runs the Query
     */
    private void handleOkButtonClick() {
        TextQuery textQuery = new TextQuery( searchStringJTextField.getText() );
        textQuery.setLowerDateRange( Tools.parseDate( lowerDateJTextField.getText() ) );
        textQuery.setUpperDateRange( Tools.parseDate( upperDateJTextField.getText() ) );
        textQuery.setStartNode( startSearchNode );

        if ( ( textQuery.getLowerDateRange() != null ) && ( textQuery.getUpperDateRange() != null ) && ( textQuery.getLowerDateRange().compareTo( textQuery.getUpperDateRange() ) > 0 ) ) {
            JOptionPane.showMessageDialog(
                    this,
                    Settings.jpoResources.getString( "dateRangeError" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        executeQuery (textQuery);
        getRid();
    }
    
    private void executeQuery (TextQuery textQuery) {
        Settings.getPictureCollection().addQueryToTreeModel( textQuery );
        JpoEventBus.getInstance().post( new ShowQueryRequest( textQuery ));
    }
    
}
