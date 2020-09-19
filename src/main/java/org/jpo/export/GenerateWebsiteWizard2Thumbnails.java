package org.jpo.export;

import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.GenerateWebsiteRequest;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.Dictionary;
import java.util.Hashtable;

/*
 GenerateWebsiteWizard2Thumbnails.java:  Specify stuff about the Thumbnails

 Copyright (C) 2008-2020  Richard Eigenmann. Zürich

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
 * Asks all the questions we need to know in regard the thumbnails on the web
 * page.
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard2Thumbnails extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final GenerateWebsiteRequest options;

    /**
     * Asks all the questions we need to know in regards to the thumbnails on
     * the final website.
     *
     * @param options Options
     */
    public GenerateWebsiteWizard2Thumbnails(final GenerateWebsiteRequest options) {
        super(Settings.getJpoResources().getString("HtmlDistThumbnails"), Settings.getJpoResources().getString("HtmlDistThumbnails"));
        this.options = options;

        // load the options into the GUI components
        picsPerRow.getModel().setValue(options.getPicsPerRow());
        thumbWidth.getModel().setValue(options.getThumbnailWidth());
        thumbHeight.getModel().setValue(options.getThumbnailHeight());
        lowresJpgQualityJSlider.setValue(options.getLowresJpgQualityPercent());
    }
    /**
     * Records the number of columns to generate, 1 to 10, start at 3 increment
     * 1
     *
     */
    private final JSpinner picsPerRow = new JSpinner( new SpinnerNumberModel( 3, 1, 10, 1 ) );
    /**
     * Modifies the width of the thumbnails, 100 to 1000, start with 300
     * increment 25
     *
     */
    private final JSpinner thumbWidth = new JSpinner( new SpinnerNumberModel( 300, 100, 1000, 25 ) );
    /**
     * Modifies the height of the thumbnails, 100 to 1000, start with 300
     * increment 25
     *
     */
    private final JSpinner thumbHeight = new JSpinner(new SpinnerNumberModel(300, 100, 1000, 25));
    /**
     * Slider that allows the quality of the lowres jpg's to be specified.
     */
    private final JSlider lowresJpgQualityJSlider
            = new JSlider(
            SwingConstants.HORIZONTAL,
            0, 100,
            (int) (Settings.getDefaultHtmlLowresQuality() * 100));
    /**
     * Modifies the height of the thumbnails, 100 to 1000, start with 300
     * increment 25
     */
    private final JSpinner scalingSteps = new JSpinner(new SpinnerNumberModel(8, 1, 20, 1));

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel( new MigLayout( "", "[][250:250:800]" ) );
        final String ALIGN_LABEL= "align label";
        wizardPanel.add(new JLabel(Settings.getJpoResources().getString("picsPerRowText")), ALIGN_LABEL);
        picsPerRow.addChangeListener((ChangeEvent arg0) -> options.setPicsPerRow(((SpinnerNumberModel) (picsPerRow.getModel())).getNumber().intValue()));
        wizardPanel.add( picsPerRow, "wrap" );

        wizardPanel.add(new JLabel(Settings.getJpoResources().getString("thumbnailSizeJLabel")), ALIGN_LABEL);
        thumbWidth.addChangeListener((ChangeEvent arg0) -> options.setThumbnailWidth(((SpinnerNumberModel) (thumbWidth.getModel())).getNumber().intValue()));
        wizardPanel.add(thumbWidth, "split 3");
        wizardPanel.add(new JLabel(" x "));
        thumbHeight.addChangeListener((ChangeEvent arg0) -> options.setThumbnailHeight(((SpinnerNumberModel) (thumbHeight.getModel())).getNumber().intValue()));
        wizardPanel.add(thumbHeight, "wrap");

        // Thumbnail Quality Slider
        wizardPanel.add(
                new JLabel(
                        Settings.getJpoResources().getString("lowresJpgQualitySlider")), ALIGN_LABEL);
        // The JSlider wants a Dictionary which can only be a Hashtable
        Dictionary<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(
                0, new JLabel(Settings.getJpoResources().getString("jpgQualityBad")));
        labelTable.put(
                80, new JLabel(Settings.getJpoResources().getString("jpgQualityGood")));
        labelTable.put(
                100, new JLabel(Settings.getJpoResources().getString("jpgQualityBest")));
        lowresJpgQualityJSlider.setLabelTable(labelTable);
        lowresJpgQualityJSlider.setMajorTickSpacing(
                10);
        lowresJpgQualityJSlider.setMinorTickSpacing(
                5 );
        lowresJpgQualityJSlider.setPaintTicks(
                true );
        lowresJpgQualityJSlider.setPaintLabels(
                true );
        lowresJpgQualityJSlider.addChangeListener( ( ChangeEvent arg0 ) -> options.setLowresJpgQualityPercent( lowresJpgQualityJSlider.getValue() ));

        final JPanel sliderOwningPanel = new JPanel();
        sliderOwningPanel.add(lowresJpgQualityJSlider);
        wizardPanel.add(sliderOwningPanel, "growx, wrap");
        wizardPanel.add(new JLabel(Settings.getJpoResources().getString("scalingSteps")));

        scalingSteps.addChangeListener( ( ChangeEvent arg0 ) -> options.setScalingSteps( ( (SpinnerNumberModel) ( scalingSteps.getModel() ) ).getNumber().intValue() ));
        wizardPanel.add( scalingSteps, "wrap" );
        return wizardPanel;
    }

    /**
     * Required but not needed here
     */
    @Override
    public void prepareRendering() {
        // do nothing
    }
}
