package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.NodeStatistics;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.GenerateWebsiteRequest;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/*
 Copyright (C) 2008-2020  Richard Eigenmann.
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
 * This Wizard Step welcomes the user to the wizard and states what is about to
 * happen
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard1Welcome extends AbstractStep {

    /**
     * This label will read "Generate a Web Page showing 10 pictures"
     */
    private final JLabel welcomeLabel = new JLabel( "working..." );
    /**
     * This label will read "From: Trip to France August 2008"
     */
    private final JLabel fromLabel = new JLabel();

    /**
     * Welcome page for the generate website wizard
     *
     * @param options Options
     */
    public GenerateWebsiteWizard1Welcome( final GenerateWebsiteRequest options ) {
        super( Settings.jpoResources.getString( "welcomeTitle" ), Settings.jpoResources.getString( "HtmlDistillerJFrameHeading" ) );

        final SortableDefaultMutableTreeNode startNode = options.getStartNode();
        class GetCountWorker extends SwingWorker<Integer, Object> {

            @Override
            public Integer doInBackground() {
                return NodeStatistics.countPictures(startNode, true);
            }

            @Override
            protected void done() {
                try {
                    welcomeLabel.setText(String.format(Settings.jpoResources.getString("welcomeMsg"), get()));
                } catch ( InterruptedException | ExecutionException ignore ) {
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
        }
        (new GetCountWorker()).execute();

        final String labelText = Settings.jpoResources.getString( "generateFrom" )
                + ( ( startNode != null ) ? startNode.toString() : "null" );
        fromLabel.setText( labelText );
    }

    /**
     * Create the step component
     *
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel(new MigLayout());
        wizardPanel.add( welcomeLabel, "wrap" );
        wizardPanel.add( fromLabel, "wrap" );
        return wizardPanel;
    }

    /**
     * Required but not used here
     */
    @Override
    public void prepareRendering() {
        // noop
    }
}
