package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.WizardModelEvent;
import net.javaprog.ui.wizard.WizardModelListener;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.NodeStatistics;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright (C) 2012 -2024 Richard Eigenmann. Zürich
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
     * The link to the values that this panel should change
     */
    private final PicasaUploadRequest myRequest;

    /**
     * Upload the album
     *
     * @param myRequest My Request
     */
    public PicasaUploaderWizard3Upload( PicasaUploadRequest myRequest ) {
        super( "Upload", "Upload" );
        this.myRequest = myRequest;
    }

    /**
     * Wizard model listener
     *
     * @param model The Wizard Model
     */
    public void attachWizardModelListener(final WizardModel model) {
        model.addWizardModelListener(new WizardModelListener() {

            @Override
            public void wizardCanceled(WizardModelEvent wme) {
                myRequest.setInterrupt(true);
            }

            @Override
            public void wizardFinished(WizardModelEvent wme) {
                // noop
            }

            @Override
            public void wizardModelChanged( WizardModelEvent wme ) {
                // noop
            }

            @Override
            public void stepShown( WizardModelEvent wme ) {
                // noop
            }
        } );
    }
    private final JProgressBar progressBar = new JProgressBar();

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel();
        final MigLayout layout = new MigLayout("wrap 1");
        wizardPanel.setLayout(layout);

        progressBar.setStringPainted(true);
        progressBar.setMinimumSize(new Dimension(250, 30));
        progressBar.setMaximumSize(new Dimension(850, 30));

        wizardPanel.add(progressBar);

        final SortableDefaultMutableTreeNode node = myRequest.getNode();
        final int pics = NodeStatistics.countPictures(node, false);
        progressBar.setMinimum(0);
        progressBar.setMaximum(pics);

        (new PicasaUploaderWorker(myRequest, progressBar, this)).execute();

        return wizardPanel;
    }

    /**
     * Required but not needed here
     */
    @Override
    public void prepareRendering() {
        setCanCancel( true );
        setCanGoBack( false );
    }

    /**
     * Respond to upload complete
     */
    @Override
    public void uploadDone() {
        setCanFinish( true );
        setCanGoBack( false );
        setCanCancel( false );
        try {
            final String PICASA_URL = "https://picasaweb.google.com/home";
            Desktop.getDesktop().browse(new URI(PICASA_URL));
        } catch ( IOException | URISyntaxException ex ) {
            Logger.getLogger( PicasaUploaderWizard3Upload.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }
}
