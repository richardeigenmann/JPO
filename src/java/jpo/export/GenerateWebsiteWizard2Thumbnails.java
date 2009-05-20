package jpo.export;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jpo.Settings;
import jpo.Tools;
import net.javaprog.ui.wizard.AbstractStep;

/*
GenerateWebsiteWizard2Thumbnails.java:  Specify stuff about the Thumbnails

Copyright (C) 2008-2009  Richard Eigenmann. Zürich

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
 * Asks all the questions we need to know in regard the thumbnails
 * on the web page.
 * 
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard2Thumbnails extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final HtmlDistillerOptions options;

    /**
     * Asks all the questions we need to know in regards to the thumbnails on the final website.
     */
    public GenerateWebsiteWizard2Thumbnails( HtmlDistillerOptions options ) {
        super( Settings.jpoResources.getString( "HtmlDistThumbnails" ), Settings.jpoResources.getString( "HtmlDistThumbnails" ) );
        this.options = options;

        // load the options into the GUI components
        ( (SpinnerNumberModel) ( picsPerRow.getModel() ) ).setValue( options.getPicsPerRow() );
        ( (SpinnerNumberModel) ( thumbWidth.getModel() ) ).setValue( options.getThumbnailWidth() );
        ( (SpinnerNumberModel) ( thumbHeight.getModel() ) ).setValue( options.getThumbnailHeight() );
        lowresJpgQualityJSlider.setValue( options.getLowresJpgQualityPercent() );
    }
    /**
     *  Records the number of columns to generate, 1 to 10, start at 3 increment 1
     **/
    private JSpinner picsPerRow = new JSpinner( new SpinnerNumberModel( 3, 1, 10, 1 ) );
    /**
     *  Modifies the width of the thumbnails, 100 to 1000, start with 300 increment 25
     **/
    private JSpinner thumbWidth = new JSpinner( new SpinnerNumberModel( 300, 100, 1000, 25 ) );
    /**
     *  Modifies the height of the thumbnails, 100 to 1000, start with 300 increment 25
     **/
    private JSpinner thumbHeight = new JSpinner( new SpinnerNumberModel( 300, 100, 1000, 25 ) );
    /**
     *  Slider that allows the quality of the lowres jpg's to be specified.
     */
    private JSlider lowresJpgQualityJSlider =
            new JSlider(
            JSlider.HORIZONTAL,
            0, 100,
            (int) ( Settings.defaultHtmlLowresQuality * 100 ) );
    /**
     *  Modifies the height of the thumbnails, 100 to 1000, start with 300 increment 25
     **/
    private JSpinner scalingSteps = new JSpinner( new SpinnerNumberModel( 8, 1, 20, 1 ) );

    /**
     * Creates the GUI widgets
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardPanel = new JPanel();
        wizardPanel.setLayout( new BoxLayout( wizardPanel, BoxLayout.PAGE_AXIS ) );
        wizardPanel.setAlignmentX( Component.LEFT_ALIGNMENT );

        JPanel columnsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        columnsPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        columnsPanel.setMaximumSize( GenerateWebsiteWizard.normalComponentSize );
        columnsPanel.add( new JLabel( Settings.jpoResources.getString( "picsPerRowText" ) ) );
        picsPerRow.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                options.setPicsPerRow( ( (SpinnerNumberModel) ( picsPerRow.getModel() ) ).getNumber().intValue() );
            }
        } );
        columnsPanel.add( picsPerRow );
        wizardPanel.add( columnsPanel );

        JPanel thumbnailSizeJPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        thumbnailSizeJPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        thumbnailSizeJPanel.setMaximumSize( GenerateWebsiteWizard.normalComponentSize );
        thumbnailSizeJPanel.add( new JLabel( Settings.jpoResources.getString( "thubnailSizeJLabel" ) ) );
        thumbWidth.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                options.setThumbnailWidth( ( (SpinnerNumberModel) ( thumbWidth.getModel() ) ).getNumber().intValue() );
            }
        } );
        thumbnailSizeJPanel.add( thumbWidth );
        thumbnailSizeJPanel.add( new JLabel( " x " ) );
        thumbHeight.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                options.setThumbnailHeight( ( (SpinnerNumberModel) ( thumbHeight.getModel() ) ).getNumber().intValue() );
            }
        } );
        thumbnailSizeJPanel.add( thumbHeight );
        wizardPanel.add( thumbnailSizeJPanel );

        // Thumbnail Quality Slider
        JPanel lowresQualitySliderJPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        lowresQualitySliderJPanel.setMaximumSize( GenerateWebsiteWizard.tallerComponentSize );
        lowresQualitySliderJPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        lowresQualitySliderJPanel.add(
                new JLabel(
                Settings.jpoResources.getString( "lowresJpgQualitySlider" ) ) );
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(
                new Integer( 0 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBad" ) ) );
        labelTable.put(
                new Integer( 80 ), new JLabel( Settings.jpoResources.getString( "jpgQualityGood" ) ) );
        labelTable.put(
                new Integer( 100 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBest" ) ) );
        lowresJpgQualityJSlider.setLabelTable( labelTable );
        lowresJpgQualityJSlider.setMajorTickSpacing(
                10 );
        lowresJpgQualityJSlider.setMinorTickSpacing(
                5 );
        lowresJpgQualityJSlider.setPaintTicks(
                true );
        lowresJpgQualityJSlider.setPaintLabels(
                true );
        lowresJpgQualityJSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged( ChangeEvent arg0 ) {
                        options.setLowresJpgQualityPercent( lowresJpgQualityJSlider.getValue() );
                    }
                } );

        lowresJpgQualityJSlider.setAlignmentX( Component.LEFT_ALIGNMENT );
        lowresQualitySliderJPanel.add( lowresJpgQualityJSlider );
        wizardPanel.add( lowresQualitySliderJPanel );

        JPanel scalingStepsPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        scalingStepsPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        scalingStepsPanel.setAlignmentY( Component.TOP_ALIGNMENT );
        scalingStepsPanel.setMaximumSize( GenerateWebsiteWizard.normalComponentSize );
        scalingStepsPanel.add( new JLabel( Settings.jpoResources.getString( "scalingSteps" ) ) );

        scalingSteps.setAlignmentX( Component.LEFT_ALIGNMENT );
        scalingSteps.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                options.setScalingSteps( ( (SpinnerNumberModel) ( scalingSteps.getModel() ) ).getNumber().intValue() );
            }
        } );
        scalingStepsPanel.add( scalingSteps );
        wizardPanel.add( scalingStepsPanel );

        wizardPanel.add( Box.createVerticalGlue() );

        return wizardPanel;
    }

    /**
     * Required but not needed here
     */
    public void prepareRendering() {
    }
}
