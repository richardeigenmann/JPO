package org.jpo.export;

import net.javaprog.ui.wizard.*;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.jpo.eventbus.JpoEventBus;

import java.awt.*;

/*
 Copyright (C) 2008-2024 Richard Eigenmann.
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
    private final GenerateWebsiteRequest request = new GenerateWebsiteRequestDefaultOptions();
    /**
     * Defines the maximum size for horizontally combined objects on a step
     * panel
     */
    public static final Dimension NORMAL_COMPONENT_SIZE = new Dimension(350, 25);

    /**
     * Creates a Wizard for the generation of a website
     *
     * @param startNode The node for which the website should be generated
     */
    public GenerateWebsiteWizard(final SortableDefaultMutableTreeNode startNode) {
        request.setStartNode(startNode);

        final WizardModel model = new DefaultWizardModel(new Step[]{
                new GenerateWebsiteWizard1Welcome(request),
                new GenerateWebsiteWizard2Thumbnails(request),
                new GenerateWebsiteWizard3Midres(request),
                new GenerateWebsiteWizard4Highres(request),
                new GenerateWebsiteWizard5Options(request),
                new GenerateWebsiteWizard6Where(request),
                new GenerateWebsiteWizard7Summary(request),});

        // the listener traps all
        model.addWizardModelListener( new WizardModelListener() {
            @Override
            public void stepShown(final WizardModelEvent arg0) {
                // noop
            }

            @Override
            public void wizardCanceled(final WizardModelEvent arg0) {
                // noop
            }

            @Override
            public void wizardFinished(final WizardModelEvent arg0) {
                request.saveToSettings();
                JpoEventBus.getInstance().post(request);
            }

            @Override
            public void wizardModelChanged(final WizardModelEvent arg0) {
                // noop
            }
        } );

        final Wizard wizard = new Wizard(model, Settings.getJpoResources().getString("HtmlDistillerJFrameHeading"));

        wizard.pack();
        wizard.setLocationRelativeTo(Settings.getAnchorFrame());
        wizard.setVisible(true);
    }
}
