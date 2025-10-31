package org.jpo.export;

import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.WizardModel;
import org.jpo.gui.Settings;

/*
 * Copyright (C) 2012-2024 Richard Eigenmann.
 *
 * This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * This class runs the user through a series of Wizard steps to define the
 * choices for the Web page rendering
 *
 * @author Richard Eigenmann
 */
public class PicasaUploaderWizard {

    /**
     * Creates a Wizard for the Picasa upload
     *
     * @param myRequest the request
     */
    public PicasaUploaderWizard(final PicasaUploadRequest myRequest) {

        final PicasaUploaderWizard3Upload step3 = new PicasaUploaderWizard3Upload(myRequest);

        // JWizz stuff
        final WizardModel model = new DefaultWizardModel(new Step[]{
                new PicasaUploaderWizard1Login(myRequest),
                new PicasaUploaderWizard2Confirm(myRequest),
                step3
        });

        step3.attachWizardModelListener( model );

        Wizard wizard = new Wizard( model, "Upload to Picasa" );

        wizard.pack();
        wizard.setLocationRelativeTo(Settings.getAnchorFrame());
        wizard.setVisible(true);
    }
}
