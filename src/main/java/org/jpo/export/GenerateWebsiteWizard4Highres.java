package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.GenerateWebsiteRequest;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/*
 GenerateWebsiteWizard4Highres.java: Ask for Highres stuff

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
 * Asks all the questions we need to know in regard the highres images on the
 * target page.
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard4Highres extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final GenerateWebsiteRequest options;

    /**
     * This Wizard prompts for the options regarding Highres
     *
     * @param options The data object with all the settings
     */
    public GenerateWebsiteWizard4Highres(final GenerateWebsiteRequest options) {
        super(Settings.getJpoResources().getString("HtmlDistHighres"), Settings.getJpoResources().getString("HtmlDistHighres"));
        this.options = options;

        // load the options into the GUI components
        exportHighresJCheckBox.setSelected(options.isExportHighres());
        rotateHighresJCheckBox.setSelected(options.isRotateHighres());
        generateZipfileJCheckBox.setSelected(options.isGenerateZipfile());
        linkToHighresJCheckBox.setSelected(options.isLinkToHighres());
    }

    /**
     * Tickbox that indicates whether the highes pictures are to be copied to
     * the target directory structure.
     */
    private final JCheckBox exportHighresJCheckBox = new JCheckBox(Settings.getJpoResources().getString("exportHighresJCheckBox"));
    /**
     * Tickbox that indicates whether the highes pictures are to be copied to
     * the target directory structure.
     */
    private final JCheckBox rotateHighresJCheckBox = new JCheckBox(Settings.getJpoResources().getString("rotateHighresJCheckBox"));
    /**
     * Tickbox that indicates whether a Zipfile should be created to download
     * the highres pictures
     */
    private final JCheckBox generateZipfileJCheckBox = new JCheckBox(Settings.getJpoResources().getString("generateZipfileJCheckBox"));
    /**
     * Tickbox that indicates whether the highes picture should be linked to at
     * the current location.
     */
    private final JCheckBox linkToHighresJCheckBox = new JCheckBox(Settings.getJpoResources().getString("linkToHighresJCheckBox"));

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel( new MigLayout() );

        // create checkbox for highres export
        exportHighresJCheckBox.addChangeListener(( ChangeEvent arg0 ) -> options.setExportHighres( exportHighresJCheckBox.isSelected() ));
        wizardPanel.add( exportHighresJCheckBox, "wrap" );

        // create checkbox for highres rotate
        rotateHighresJCheckBox.addChangeListener(( ChangeEvent arg0 ) -> options.setRotateHighres( rotateHighresJCheckBox.isSelected() ));
        wizardPanel.add( rotateHighresJCheckBox, "wrap" );


        generateZipfileJCheckBox.addChangeListener(( ChangeEvent arg0 ) -> options.setGenerateZipfile( generateZipfileJCheckBox.isSelected() ));
        wizardPanel.add( generateZipfileJCheckBox, "wrap" );

        // create checkbox for linking to highres
        linkToHighresJCheckBox.addChangeListener(( ChangeEvent arg0 ) -> options.setLinkToHighres( linkToHighresJCheckBox.isSelected() ));
        wizardPanel.add( linkToHighresJCheckBox, "wrap" );

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
