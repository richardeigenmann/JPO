package jpo.export;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import jpo.dataModel.NodeStatistics;
import jpo.dataModel.Settings;
import net.javaprog.ui.wizard.AbstractStep;

/*
GenerateWebsiteWizard1Welcome.java:  Welcome Step

Copyright (C) 2008  Richard Eigenmann.
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
 * This Wizard Step welcomes the user to the wizard and states what is about to happen
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard1Welcome extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final HtmlDistillerOptions options;
    /**
     * This label will read "Generate a Web Page showing 10 pictures"
     */
    private final JLabel welcomeLabel = new JLabel("working...");
    /**
     * This label will read "From: Trip to France August 2008"
     */
    private JLabel fromLabel = new JLabel();

    public GenerateWebsiteWizard1Welcome( final HtmlDistillerOptions options ) {
        super( Settings.jpoResources.getString( "welcomeTitle" ), Settings.jpoResources.getString( "HtmlDistillerJFrameHeading" ) );
        this.options = options;

        class getCountWorker extends SwingWorker<Integer, Object> {

            @Override
            public Integer doInBackground() {
                return NodeStatistics.countPictures( options.getStartNode(), true );
            }

            @Override
            protected void done() {
                try {
                    welcomeLabel.setText( String.format( Settings.jpoResources.getString( "welcomeMsg" ), get() ) );
                } catch ( Exception ignore ) {
                }
            }
        }
        ( new getCountWorker() ).execute();

        fromLabel.setText( Settings.jpoResources.getString( "generateFrom" ) + options.getStartNode().toString() );
    }

    /**
     * Create the step component
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardPanel = new JPanel();
        wizardPanel.setLayout( new BoxLayout( wizardPanel, BoxLayout.PAGE_AXIS ) );
        wizardPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

        wizardPanel.add( welcomeLabel );
        wizardPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );
        wizardPanel.add( fromLabel );
        return wizardPanel;
    }

    /**
     * Required but not used here
     */
    public void prepareRendering() {
    }
}
