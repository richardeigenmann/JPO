package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.GenerateWebsiteRequest;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Dictionary;
import java.util.Hashtable;

import static javax.swing.SwingConstants.HORIZONTAL;

/*
 GenerateWebsiteWizard3Midres.java:  Midres stuff

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
 * This Wizard Step asks and stores the Midres Stuff
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard3Midres extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final GenerateWebsiteRequest request;

    /**
     * This step asks for all the midres stuff for the webpage generation
     *
     * @param request The link to the options object with all the settings
     */
    public GenerateWebsiteWizard3Midres(final GenerateWebsiteRequest request) {
        super(Settings.getJpoResources().getString("HtmlDistMidres"), Settings.getJpoResources().getString("HtmlDistMidres"));
        this.request = request;

        // populate the widgets with the values from the options
        generateMidresHtmlJCheckBox.setSelected(request.isGenerateMidresHtml());
        generateMapJCheckBox.setSelected(request.isGenerateMap());
        googleMapsApiKeyJTextField.setText(request.getGoogleMapsApiKey());
        mouseoverJCheckBox.setSelected(request.isGenerateMouseover());
        midresWidthSpinnerNumberModel.setValue(request.getMidresWidth());
        midresHeightSpinnerNumberModel.setValue(request.getMidresHeight());
        midresJpgQualityJSlider.setValue(request.getMidresJpgQualityPercent());
    }
    /**
     * The number model for the width spinner.
     */
    private final SpinnerNumberModel midresWidthSpinnerNumberModel = new SpinnerNumberModel( 300, 100, 10000, 25 );
    /**
     * The width of the midres images
     *
     */
    private final JSpinner midresWidthJSpinner = new JSpinner( midresWidthSpinnerNumberModel );
    /**
     * The number model for the width spinner.
     */
    private final SpinnerNumberModel midresHeightSpinnerNumberModel = new SpinnerNumberModel( 300, 100, 10000, 25 );
    /**
     * The height of the midres images
     *
     */
    private final JSpinner midresHeightJSpinner = new JSpinner( midresHeightSpinnerNumberModel );

    /**
     * Checkbox that indicates whether to generate the midres html files or not.
     * Requested by Jay Christopherson, Nov 2008
     */
    private final JCheckBox generateMidresHtmlJCheckBox = new JCheckBox(Settings.getJpoResources().getString("HtmlDistMidresHtml"));

    /**
     * Checkbox that indicates whether to add a map ith the location or not.
     */
    private final JCheckBox generateMapJCheckBox = new JCheckBox(Settings.getJpoResources().getString("GenerateMap"));

    /**
     * The ftp Server
     */
    private final JTextField googleMapsApiKeyJTextField = new JTextField();

    /**
     * Tickbox that indicates whether DHTML tags and effects should be
     * generated.
     */
    private final JCheckBox mouseoverJCheckBox = new JCheckBox(Settings.getJpoResources().getString("org.jpo.export.GenerateWebsiteWizard3Midres.generateMouseoverJCheckBox"));

    /**
     * Slider that allows the quality of the midres jpg's to be specified.
     */
    private final JSlider midresJpgQualityJSlider
            = new JSlider(
            HORIZONTAL,
            0, 100,
            (int) (Settings.getDefaultHtmlMidresQuality() * 100));

    /**
     * Create the widgets.
     *
     * @return A panel with the components
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel( new MigLayout( "", "[][250:250:800]", "" ) );

        generateMidresHtmlJCheckBox.addChangeListener(( ChangeEvent arg0 ) -> {
            generateMapJCheckBox.setEnabled(generateMidresHtmlJCheckBox.isSelected());
            mouseoverJCheckBox.setEnabled(generateMidresHtmlJCheckBox.isSelected());
            request.setGenerateMidresHtml(generateMidresHtmlJCheckBox.isSelected());
        });
        final String SPANX_WRAP = "spanx, wrap";
        wizardPanel.add(generateMidresHtmlJCheckBox, SPANX_WRAP);

        generateMapJCheckBox.addChangeListener(changeListener -> request.setGenerateMap(generateMapJCheckBox.isSelected()));
        wizardPanel.add(generateMapJCheckBox, SPANX_WRAP);

        wizardPanel.add(new JLabel("Google Maps API Key:"), SPANX_WRAP);
        googleMapsApiKeyJTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
        googleMapsApiKeyJTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                request.setGoogleMapsApiKey(googleMapsApiKeyJTextField.getText());
            }
        });
        wizardPanel.add(googleMapsApiKeyJTextField, "growx, wrap");


        mouseoverJCheckBox.addChangeListener(changeListener -> request.setGenerateMouseover(mouseoverJCheckBox.isSelected()));
        wizardPanel.add(mouseoverJCheckBox, SPANX_WRAP);

        wizardPanel.add(new JLabel(Settings.getJpoResources().getString("thumbnailSizeJLabel")), "align label");
        midresWidthJSpinner.addChangeListener(changeListener -> request.setMidresWidth(((SpinnerNumberModel) (midresWidthJSpinner.getModel())).getNumber().intValue()));
        wizardPanel.add(midresWidthJSpinner, "split 3");
        wizardPanel.add(new JLabel(" x "));
        midresHeightJSpinner.addChangeListener(changeListener -> request.setMidresHeight(((SpinnerNumberModel) (midresHeightJSpinner.getModel())).getNumber().intValue()));
        wizardPanel.add(midresHeightJSpinner, "wrap");

        // Midres Quality Slider
        wizardPanel.add(new JLabel(Settings.getJpoResources().getString("midresJpgQualitySlider")), "align label");
        final Dictionary<Integer, JLabel> labelTable1 = new Hashtable<>();
        labelTable1.put(0, new JLabel(Settings.getJpoResources().getString("jpgQualityBad")));
        labelTable1.put(80, new JLabel(Settings.getJpoResources().getString("jpgQualityGood")));
        labelTable1.put(100, new JLabel(Settings.getJpoResources().getString("jpgQualityBest")));
        midresJpgQualityJSlider.setLabelTable(labelTable1);

        midresJpgQualityJSlider.setMajorTickSpacing(10);
        midresJpgQualityJSlider.setMinorTickSpacing(5);
        midresJpgQualityJSlider.setPaintTicks(true);
        midresJpgQualityJSlider.setPaintLabels(true);
        midresJpgQualityJSlider.addChangeListener((ChangeEvent arg0) -> request.setMidresJpgQualityPercent(midresJpgQualityJSlider.getValue()));
        wizardPanel.add(midresJpgQualityJSlider, "growx, wrap");
        return wizardPanel;
    }

    /**
     * required but not used
     */
    @Override
    public void prepareRendering() {
         // do nothing
    }
}
