package jpo.export;

import java.awt.Dimension;
import javax.swing.SwingUtilities;

import jpo.EventBus.GenerateWebsiteRequest;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.WizardModelEvent;
import net.javaprog.ui.wizard.WizardModelListener;

/*
 GenerateWebsiteWizard.java: Creates a Wizard for generating a web page

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
 * This class runs the user through a series of Wizard steps to define the
 * choices for the Web page rendering
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard {

    /**
     * The object that holds all the settings for the generation. Each Wizard
     * automatically gets a reference to the options object upon creation. Each
     * Wizard step performs component initialisation originating from these
     * Options. Since we are creating a new DefaultOptions Object the
     * instantiated class will pull in the defaults from the Settings object.
     */
    private final GenerateWebsiteRequest options = new GenerateWebsiteRequestDefaultOptions();
    /**
     * Defines the maximum size for horizontally combined objects on a step
     * panel
     */
    public static final Dimension normalComponentSize = new Dimension( 350, 25 );

    /**
     * Creates a Wizard for the generation of a website
     *
     * @param startNode The node for which the website should be generated
     */
    public GenerateWebsiteWizard( SortableDefaultMutableTreeNode startNode ) {
        options.setStartNode( startNode );

        // JWizz stuff
        WizardModel model = new DefaultWizardModel( new Step[]{
            new GenerateWebsiteWizard1Welcome( options ),
            new GenerateWebsiteWizard2Thumbnails( options ),
            new GenerateWebsiteWizard3Midres( options ),
            new GenerateWebsiteWizard4Highres( options ),
            new GenerateWebsiteWizard5Options( options ),
            new GenerateWebsiteWizard6Where( options ),
            new GenerateWebsiteWizard7Summary( options ), } );

        // the listener traps all
        model.addWizardModelListener( new WizardModelListener() {
            @Override
            public void stepShown( WizardModelEvent arg0 ) {
            }

            @Override
            public void wizardCanceled( WizardModelEvent arg0 ) {
            }

            @Override
            public void wizardFinished( WizardModelEvent arg0 ) {
                options.saveToSettings();
                JpoEventBus.getInstance().post(options);
            }

            @Override
            public void wizardModelChanged( WizardModelEvent arg0 ) {
            }
        } );

        Wizard wizard = new Wizard( model, Settings.jpoResources.getString( "HtmlDistillerJFrameHeading" ) );

        wizard.pack();
        wizard.setLocationRelativeTo( Settings.anchorFrame );
        wizard.setVisible( true );
    }
}
