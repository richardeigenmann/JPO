package jpo.export;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.NodeStatistics;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.WizardModelEvent;
import net.javaprog.ui.wizard.WizardModelListener;
import net.miginfocom.swing.MigLayout;

/*
 * PicasaUploaderWizard2Upload.java:
 *
 * Copyright (C) 2012 Richard Eigenmann. Zürich
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * Performs the login to Google
 *
 * @author Richard Eigenmann
 */
public class PicasaUploaderWizard3Upload extends AbstractStep implements PicasaUploaderDoneInterface {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger ( PicasaUploaderWizard3Upload.class.getName () );
    /**
     * The link to the values that this panel should change
     */
    private final PicasaUploadRequest myRequest;
    private WizardModel model;

    /**
     * Upload the album
     */
    public PicasaUploaderWizard3Upload ( PicasaUploadRequest myRequest ) {
        super ( "Upload", "Upload" );
        this.myRequest = myRequest;
    }

    public void attachWizardModelListener ( WizardModel model ) {
        this.model = model;
        model.addWizardModelListener ( new WizardModelListener () {

            @Override
            public void wizardCanceled ( WizardModelEvent wme ) {
                myRequest.setInterrupt ( true );
            }

            @Override
            public void wizardFinished ( WizardModelEvent wme ) {
            }

            @Override
            public void wizardModelChanged ( WizardModelEvent wme ) {
            }

            @Override
            public void stepShown ( WizardModelEvent wme ) {
            }
        } );
    }
    private final JProgressBar progressBar = new JProgressBar ();

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent () {
        JPanel wizardPanel = new JPanel ();
        MigLayout layout = new MigLayout ( "wrap 1" );
        wizardPanel.setLayout ( layout );

        progressBar.setStringPainted ( true );
        progressBar.setMinimumSize ( new Dimension ( 250, 30 ) );
        progressBar.setMaximumSize ( new Dimension ( 850, 30 ) );

        wizardPanel.add ( progressBar );

        SortableDefaultMutableTreeNode node = myRequest.getNode ();
        String albumName = ( (GroupInfo) node.getUserObject () ).getGroupName ();
        int pics = NodeStatistics.countPictures ( node, false );
        progressBar.setMinimum ( 0 );
        progressBar.setMaximum ( pics );

        ( new PicasaUploaderWorker ( myRequest, progressBar, this ) ).execute ();



        return wizardPanel;
    }

    /**
     * Required but not needed here
     */
    @Override
    public void prepareRendering () {
        setCanCancel ( true );
        setCanGoBack ( false );
    }

    @Override
    public void uploadDone () {
        setCanFinish ( true );
        setCanGoBack ( false );
        setCanCancel ( false );
        try {
            Desktop.getDesktop ().browse ( new URI ( "https://picasaweb.google.com/home" ) );
        } catch ( IOException ex ) {
            Logger.getLogger ( PicasaUploaderWizard3Upload.class.getName () ).log ( Level.SEVERE, null, ex );
        } catch ( URISyntaxException ex ) {
            Logger.getLogger ( PicasaUploaderWizard3Upload.class.getName () ).log ( Level.SEVERE, null, ex );
        }

    }
}
