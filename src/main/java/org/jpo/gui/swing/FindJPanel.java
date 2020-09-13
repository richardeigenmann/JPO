package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
    private final JButton advancedFindJButton = new JButton(Settings.getJpoResources().getString("advancedFindJButtonOpen"));

    /**
     * The label saying date range
     */
    private final JLabel dateRangeJLabel = new JLabel(Settings.getJpoResources().getString("lowerDateJLabel"));

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

        add(new JLabel(Settings.getJpoResources().getString("searchDialogLabel")));
        add(searchStringJTextField, "wrap, pushx, growx");

        advancedFindJButton.addActionListener(( ActionEvent evt ) -> toggleAdvancedCriteria());
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

    private void toggleAdvancedCriteria() {
        String savedLowerDateValue = Tools.currentDate( "dd.MM.yyyy" );
        String savedUpperDateValue = Tools.currentDate( "dd.MM.yyyy" );

        if ( dateRangeJLabel.isVisible() ) {
            lowerDateJTextField.setText( "" );
            upperDateJTextField.setText("");
            advancedFindJButton.setText(Settings.getJpoResources().getString("advancedFindJButtonOpen"));
        } else {
            lowerDateJTextField.setText( savedLowerDateValue );
            lowerDateJTextField.setVisible( true );

            upperDateJTextField.setText( savedUpperDateValue );
            upperDateJTextField.setVisible(true);
            advancedFindJButton.setText(Settings.getJpoResources().getString("advancedFindJButtonClose"));
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
