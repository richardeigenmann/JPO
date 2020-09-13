package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.GenerateWebsiteRequest;

import javax.swing.*;

/*
 GenerateWebsiteWizard7Summary.java:  Summarise before you go and do it

 Copyright (C) 2008-2013  Richard Eigenmann.
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
 * This Wizard step summarises the settings and then goes off and does them
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard7Summary extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final GenerateWebsiteRequest options;

    /**
     * This Wizard step summarises the settings and then goes off and does them
     *
     * @param options Options
     */
    public GenerateWebsiteWizard7Summary( GenerateWebsiteRequest options ) {
        super(Settings.getJpoResources().getString("summary"), Settings.getJpoResources().getString("summary"));
        this.options = options;
    }
    /**
     * Shows the summaryTextArea
     */
    protected final JTextArea summaryTextArea = new JTextArea();

    /**
     * Returns the widgets to show the summaryTextArea
     *
     * @return the component that shows the summaryTextArea
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardStep = new JPanel( new MigLayout() );

        JScrollPane scrollPane = new JScrollPane( summaryTextArea );
        wizardStep.add( scrollPane, "grow" );
        return wizardStep;
    }

    /**
     * Refreshes the contents of the widgets
     */
    @Override
    public void prepareRendering() {
        summaryTextArea.setText( options.toString() );
        setCanFinish( true );
    }
}
