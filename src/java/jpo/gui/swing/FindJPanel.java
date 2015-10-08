package jpo.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import net.miginfocom.swing.MigLayout;

/*
 FindJPanel.java:  creates a GUI to allow the user to specify the search criteria

 Copyright (C) 2002-2015  Richard Eigenmann.
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
 * FindJPanel.java: creates a GUI to allow the user to specify the search
 * criteria
 *
 *
 * @author Richard Eigenmann
 */
public class FindJPanel extends JPanel {

    /**
     * the search string
     */
    private final JTextField searchStringJTextField = new JTextField( 20 );

    /**
     * The Advanced Find button
     */
    private final JButton advancedFindJButton = new JButton( Settings.jpoResources.getString( "advancedFindJButtonOpen" ) );

    /**
     * The label saying date range
     */
    private final JLabel dateRangeJLabel = new JLabel( Settings.jpoResources.getString( "lowerDateJLabel" ) );

    /**
     * the lower date for a specified range
     */
    private final JTextField lowerDateJTextField = new JTextField( 10 );

    /**
     * the upper date for a specified range
     */
    private final JTextField upperDateJTextField = new JTextField( 10 );

    /**
     * Constructs the panel
     */
    public FindJPanel() {
        setLayout( new MigLayout() );

        add( new JLabel( Settings.jpoResources.getString( "searchDialogLabel" ) ) );
        add( searchStringJTextField, "wrap, pushx, growx" );

        advancedFindJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent evt ) {
                toggleAdvancedCriteria();
            }
        } );
        add( advancedFindJButton, "wrap" );

        add( dateRangeJLabel, "hidemode 0" );
        add( lowerDateJTextField, "wrap, hidemode 0" );
        add( upperDateJTextField, "skip, wrap, hidemode 0" );

        setAdvancedOptionsVisible( false );

    }

    /**
     * Show or hide the advanced options
     *
     * @param visible flag
     */
    private void setAdvancedOptionsVisible( boolean visible ) {
        dateRangeJLabel.setVisible( visible );
        lowerDateJTextField.setVisible( visible );
        upperDateJTextField.setVisible( visible );
    }

    private String savedLowerDateValue;
    private String savedUpperDateValue;

    private void toggleAdvancedCriteria() {
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
        setAdvancedOptionsVisible( !dateRangeJLabel.isVisible() );
    }

    /**
     * Returns the search string the user keyed in
     *
     * @return The search string
     */
    public String getSearchArgument() {
        return searchStringJTextField.getText();
    }

    /**
     * Returns if the user keyed in a date range string
     *
     * @return true if search by date desired
     */
    public boolean getSearchByDate() {
        return dateRangeJLabel.isVisible();
    }

    /**
     * Returns the lower date field text
     *
     * @return the lower date field text
     */
    public String getLowerDate() {
        return lowerDateJTextField.getText();
    }

    /**
     * Returns the higher date field text
     *
     * @return the higher date field text
     */
    public String getHigherDate() {
        return upperDateJTextField.getText();
    }

}
