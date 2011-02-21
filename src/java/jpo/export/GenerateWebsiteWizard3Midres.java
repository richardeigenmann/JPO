package jpo.export;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpo.dataModel.Settings;
import net.javaprog.ui.wizard.AbstractStep;

/*
GenerateWebsiteWizard3Midres.java:  Midres stuff

Copyright (C) 2008-2011  Richard Eigenmann.
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
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard3Midres extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final HtmlDistillerOptions options;

    /**
     * This step asks for all the midres stuff for the webpage generation
     * @param options The link to the options object with all the settings
     */
    public GenerateWebsiteWizard3Midres ( HtmlDistillerOptions options ) {
        super( Settings.jpoResources.getString( "HtmlDistMidres" ), Settings.jpoResources.getString( "HtmlDistMidres" ) );
        this.options = options;

        // populate the widgets with the values from the options
        generateMidresHtmlJCheckBox.setSelected( options.isGenerateMidresHtml() );
        generateMapJCheckBox.setSelected( options.isGenerateMap() );
        generateDHTMLJCheckBox.setSelected( options.isGenerateDHTML() );
        midresWidthSpinnerNumberModel.setValue( options.getMidresWidth() );
        midresHeightSpinnerNumberModel.setValue( options.getMidresHeight() );
        midresJpgQualityJSlider.setValue( options.getMidresJpgQualityPercent() );
    }
    /**
     * The number model for the width spinner.
     */
    private final SpinnerNumberModel midresWidthSpinnerNumberModel = new SpinnerNumberModel( 300, 100, 10000, 25 );
    /**
     *  The width of the midres images
     **/
    private final JSpinner midresWidthJSpinner = new JSpinner( midresWidthSpinnerNumberModel );
    /**
     * The number model for the width spinner.
     */
    private final SpinnerNumberModel midresHeightSpinnerNumberModel = new SpinnerNumberModel( 300, 100, 10000, 25 );
    /**
     *  The height of the midres images
     **/
    private final JSpinner midresHeightJSpinner = new JSpinner( midresHeightSpinnerNumberModel );
    /**
     * Checkbox that indicates whether to generate the midres html files or not.
     * Requested by Jay Christopherson, Nov 2008
     */
    private final JCheckBox generateMidresHtmlJCheckBox = new JCheckBox( Settings.jpoResources.getString( "HtmlDistMidresHtml" ) );
    /**
     * Checkbox that indicates whether to add a map ith the location or not.
     */
    private final JCheckBox generateMapJCheckBox = new JCheckBox( Settings.jpoResources.getString( "GenerateMap" ) );
    /**
     *  Tickbox that indicates whether DHTML tags and effects should be generated.
     **/
    private final JCheckBox generateDHTMLJCheckBox = new JCheckBox( Settings.jpoResources.getString( "generateDHTMLJCheckBox" ) );
    /**
     *  Tickbox that indicates whether a Zipfile should be created to download the highres pictures
     **/
    private final JCheckBox generateZipfileJCheckBox = new JCheckBox( Settings.jpoResources.getString( "generateZipfileJCheckBox" ) );
    /**
     *  Slider that allows the quality of the midres jpg's to be specified.
     */
    private final JSlider midresJpgQualityJSlider =
            new JSlider(
            JSlider.HORIZONTAL,
            0, 100,
            (int) (Settings.defaultHtmlMidresQuality * 100) );

    /**
     * Create the widgets.
     * @return A panel with the components
     */
    @Override
    protected JComponent createComponent () {
        JPanel wizardPanel = new JPanel();
        wizardPanel.setLayout( new BoxLayout( wizardPanel, BoxLayout.PAGE_AXIS ) );
        wizardPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

        generateMidresHtmlJCheckBox.addChangeListener( new ChangeListener() {

            public void stateChanged ( ChangeEvent arg0 ) {
                generateMapJCheckBox.setEnabled( generateMidresHtmlJCheckBox.isSelected() );
                generateDHTMLJCheckBox.setEnabled( generateMidresHtmlJCheckBox.isSelected() );
                options.setGenerateMidresHtml( generateMidresHtmlJCheckBox.isSelected() );
            }
        } );
        wizardPanel.add( generateMidresHtmlJCheckBox );

        generateMapJCheckBox.addChangeListener( new ChangeListener() {

            public void stateChanged ( ChangeEvent arg0 ) {
                options.setGenerateMap( generateMapJCheckBox.isSelected() );
            }
        } );
        wizardPanel.add( generateMapJCheckBox );


        generateDHTMLJCheckBox.addChangeListener( new ChangeListener() {

            public void stateChanged ( ChangeEvent arg0 ) {
                options.setGenerateDHTML( generateDHTMLJCheckBox.isSelected() );
            }
        } );
        wizardPanel.add( generateDHTMLJCheckBox );


        JPanel midresSizeJPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        midresSizeJPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        midresSizeJPanel.setMaximumSize( GenerateWebsiteWizard.normalComponentSize );
        midresSizeJPanel.add( new JLabel( Settings.jpoResources.getString( "thubnailSizeJLabel" ) ) );  // Deliberately taking lowres
        midresWidthJSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged ( ChangeEvent arg0 ) {
                        options.setMidresWidth( ((SpinnerNumberModel) (midresWidthJSpinner.getModel())).getNumber().intValue() );
                    }
                } );
        midresSizeJPanel.add( midresWidthJSpinner );
        midresSizeJPanel.add( new JLabel( " x " ) );
        midresHeightJSpinner.addChangeListener( new ChangeListener() {

            public void stateChanged ( ChangeEvent arg0 ) {
                options.setMidresHeight( ((SpinnerNumberModel) (midresHeightJSpinner.getModel())).getNumber().intValue() );
            }
        } );
        midresSizeJPanel.add( midresHeightJSpinner );
        wizardPanel.add( midresSizeJPanel );


        // Midres Quality Slider
        JPanel midresSliderJPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        midresSliderJPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        midresSliderJPanel.add( new JLabel( Settings.jpoResources.getString( "midresJpgQualitySlider" ) ) );
        final Dictionary<Integer, JLabel> labelTable1 = new Hashtable<Integer, JLabel>();
        labelTable1.put( new Integer( 0 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBad" ) ) );
        labelTable1.put( new Integer( 80 ), new JLabel( Settings.jpoResources.getString( "jpgQualityGood" ) ) );
        labelTable1.put( new Integer( 100 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBest" ) ) );
        midresJpgQualityJSlider.setLabelTable( labelTable1 );

        midresJpgQualityJSlider.setMajorTickSpacing( 10 );
        midresJpgQualityJSlider.setMinorTickSpacing( 5 );
        midresJpgQualityJSlider.setPaintTicks( true );
        midresJpgQualityJSlider.setPaintLabels( true );
        midresJpgQualityJSlider.addChangeListener( new ChangeListener() {

            public void stateChanged ( ChangeEvent arg0 ) {
                options.setMidresJpgQualityPercent( midresJpgQualityJSlider.getValue() );
            }
        } );
        midresSliderJPanel.add( midresJpgQualityJSlider );
        wizardPanel.add( midresSliderJPanel );
        return wizardPanel;
    }

    // required but not used
    public void prepareRendering () {
    }
}
