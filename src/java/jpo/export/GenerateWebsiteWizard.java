package jpo.export;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import jpo.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.WizardModelEvent;
import net.javaprog.ui.wizard.WizardModelListener;

/*
GenerateWebsiteWizard.java: Creates a Wizard for generating a web page

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
 * This class runs the user through a series of Wizard steps to define the
 * choices for the Web page rendering
 * 
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard {

    /**
     * The object that holds all the settings for the generation. Each Wizard automatically
     * gets a reference to the options object upon creation. Each Wizard step
     * performs component initialisation originating from these Options. Since
     * we are creating a new DefaultOptions Object the instantiated class will pull
     * in the defaults from the Settings object.
     */
    private final HtmlDistillerOptions options = new HtmlDistillerDefaultOptions();
    /**
     * Defines the maximum size for horizontally combined objects on a step panel
     */
    public static final Dimension normalComponentSize = new Dimension( 350, 25 );
    /**
     * Defines the maximum size for taller horizontally combined objects on astep panel
     */
    public static final Dimension tallerComponentSize = new Dimension( 350, 50 );

    /**
     * Creates a Wizard for the generation of a website
     * @param startNode  The node for which the ebsite should be generated
     */
    public GenerateWebsiteWizard( SortableDefaultMutableTreeNode startNode ) {
        options.setStartNode( startNode );

        // JWizz stuff
        WizardModel model = new DefaultWizardModel( new Step[] {
                    new GenerateWebsiteWizard1Welcome( options ),
                    new GenerateWebsiteWizard2Thumbnails( options ),
                    new GenerateWebsiteWizard3Midres( options ),
                    new GenerateWebsiteWizard4Highres( options ),
                    new GenerateWebsiteWizard5Options( options ),
                    new GenerateWebsiteWizard6Where( options ),
                    new GenerateWebsiteWizard7Summary( options ), } );

        // the listener traps all
        model.addWizardModelListener( new WizardModelListener() {

            public void stepShown( WizardModelEvent arg0 ) {
            }

            public void wizardCanceled( WizardModelEvent arg0 ) {
            }

            public void wizardFinished( WizardModelEvent arg0 ) {
                options.saveToSettings();
                HtmlDistiller h = new HtmlDistiller( options );
                Thread t = new Thread( h );
                t.start();
            }

            public void wizardModelChanged( WizardModelEvent arg0 ) {
            }
        } );

       /* try {
            final String Metal = "javax.swing.plaf.metal.MetalLookAndFeel";
            UIManager.setLookAndFeel( Metal );
        } catch ( ClassNotFoundException ex ) {
            Logger.getLogger( GenerateWebsiteWizard.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( InstantiationException ex ) {
            Logger.getLogger( GenerateWebsiteWizard.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( IllegalAccessException ex ) {
            Logger.getLogger( GenerateWebsiteWizard.class.getName() ).log( Level.SEVERE, null, ex );
        } catch ( UnsupportedLookAndFeelException ex ) {
            Logger.getLogger( GenerateWebsiteWizard.class.getName() ).log( Level.SEVERE, null, ex );
        } */

        Wizard wizard = new Wizard( model, Settings.jpoResources.getString( "HtmlDistillerJFrameHeading" ) );

        wizard.pack();
        wizard.setLocationRelativeTo( Settings.anchorFrame );
        wizard.setVisible( true );
    }
}
