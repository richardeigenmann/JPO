package jpo.export;

import jpo.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.io.*;
import javax.swing.event.*;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.hslf.model.*;

/**
 * A GUI for the HtmlDistiller which then populates an HtmlDistillerOptions object 
 * and then fires of the HtmlDistillerThread.
 *
 * @author  Richard Eigenmann
 */
public class HtmlDistillerJFrame extends JFrame {

    /**
     *  Text field that holds the directory that the html is to be exported to.
     **/
    private DirectoryChooser targetDirJTextField =
            new DirectoryChooser( Settings.jpoResources.getString( "HtmlDistillerChooserTitle" ),
            DirectoryChooser.DIR_MUST_BE_WRITABLE );
    /**
     *  Text field that holds the directory that the html is to be exported to.
     **/
    //private WholeNumberField picsPerRow = new WholeNumberField( 0, 4 );
    private JSpinner picsPerRow = new JSpinner( new SpinnerNumberModel( 3, 1, 10, 1 ) );
    /**
     *  Text field that holds the width of the small overview pictures.
     **/
    private WholeNumberField thubWidthWholeNumberField = new WholeNumberField( 0, 6 );
    /**
     *  Text field that holds the width of the small overview pictures.
     **/
    private WholeNumberField thubHeightWholeNumberField = new WholeNumberField( 0, 6 );
    /**
     *  Text field that holds the size of the midres pictures.
     **/
    private WholeNumberField midresWidthWholeNumberField = new WholeNumberField( 0, 6 );
    /**
     *  Text field that holds the size of the midres pictures.
     **/
    private WholeNumberField midresHeightWholeNumberField = new WholeNumberField( 0, 6 );
    /**
     *  Tickbox that indicates whether the highes pictures are to be copied to the
     *  target directory structure.
     **/
    private JCheckBox exportHighresJCheckBox = new JCheckBox( Settings.jpoResources.getString( "exportHighresJCheckBox" ) );
    /**
     *  Tickbox that indicates whether the highes picture should be linked to at the current location.
     **/
    private JCheckBox linkToHighresJCheckBox = new JCheckBox( Settings.jpoResources.getString( "linkToHighresJCheckBox" ) );
    /**
     * Checkbox that indicates whether to generate the midres html files or not.
     * Requested by Jay Christopherson, Nov 2008
     */
    private final JCheckBox generateMidresHtml = new JCheckBox( Settings.jpoResources.getString( "HtmlDistMidresHtml" ) );
    /**
     *  Tickbox that indicates whether DHTML tags and effects should be generated.
     **/
    private JCheckBox generateDHTMLJCheckBox = new JCheckBox( Settings.jpoResources.getString( "generateDHTMLJCheckBox" ) );
    /**
     *  Tickbox that indicates whether a Zipfile should be created to download the highres pictures
     **/
    private JCheckBox generateZipfileJCheckBox = new JCheckBox( Settings.jpoResources.getString( "generateZipfileJCheckBox" ) );
    /**
     *  Slider that allows the quality of the lowres jpg's to be specified.
     */
    private JSlider lowresJpgQualityJSlider =
            new JSlider(
            JSlider.HORIZONTAL,
            0, 100,
            (int) ( Settings.defaultJpgQuality * 100 ) );
    /**
     *  Slider that allows the quality of the lowres jpg's to be specified.
     */
    private JSlider midresJpgQualityJSlider =
            new JSlider(
            JSlider.HORIZONTAL,
            0, 100,
            (int) ( Settings.defaultJpgQuality * 100 ) );
    /**
     *   This table will act as a preview to the color chooser.
     */
    private JLabel previewJLabel;
    /**
     *  The preview Panel
     */
    private final JPanel previewPanel = new PreviewPanel();
    /**
     * Radio Button to indicate that the java hash code should be used to get the image name
     */
    private JRadioButton hashcodeRadioButton = new JRadioButton( Settings.jpoResources.getString( "hashcodeRadioButton" ) );
    /**
     * Radio Button to indicate that the original name should be used to get the image name
     */
    private JRadioButton originalNameRadioButton = new JRadioButton( Settings.jpoResources.getString( "originalNameRadioButton" ) );
    /**
     * Radio Button to indicate that a sequential number should be used to get the image name
     */
    private JRadioButton sequentialRadioButton = new JRadioButton( Settings.jpoResources.getString( "sequentialRadioButton" ) );
    /**
     * Allow the user to specify a start number for the sequential numbering.
     * Requested by Jay Christopherson, Nov 2008
     */
    private final JSpinner sequentialStartJSpinner = new JSpinner( new SpinnerNumberModel( 1, 1, 999999999, 1 ) );
    /**
     * The options that this GUI will set
     */
    private final HtmlDistillerOptions options = new HtmlDistillerDefaultOptions();
    /**
     *  Tickbox that indicates whether to write a robots.txt
     **/
    private JCheckBox generateRobotsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "generateRobotsJCheckBox" ) );

    /** 
     *   Constructor to create the GUI.
     *
     *   @param  startNode  The node for which the html extract is to be performed.
     */
    public HtmlDistillerJFrame( SortableDefaultMutableTreeNode startNode ) {
        options.setStartNode( startNode );
        initGui();
        loadOptionsToGui();
    }

    /**
     * Creates all the required widgets.
     */
    private void initGui() {
        setTitle( Settings.jpoResources.getString( "HtmlDistillerJFrameHeading" ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets( 4, 4, 4, 4 );

        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout( new GridBagLayout() );


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;


        JPanel targetJPanel = new JPanel();
        targetJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "HtmlDistTarget" ) ) );
        targetJPanel.setLayout( new BoxLayout( targetJPanel, BoxLayout.PAGE_AXIS ) );

        JLabel infoLabel = new JLabel( "Generate a Web Page showing x pictures" );
        infoLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        targetJPanel.add( infoLabel );
        targetJPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );

        String[] finalTargetOptions = { "Local Directory", "FTP Location", "SCP Location" };
        final JComboBox finalTarget = new JComboBox( finalTargetOptions );
        finalTarget.setSelectedIndex( 0 );
        finalTarget.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent arg0 ) {
                Tools.log( "Other delivery types are not yet supported" );
                finalTarget.setSelectedIndex( 0 );
            }
        } );
        finalTarget.setAlignmentX( Component.LEFT_ALIGNMENT );
        finalTarget.setMaximumSize( new Dimension( 170, 80 ) );
        targetJPanel.add( finalTarget );
        targetJPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );


        // create the JTextField that holds the reference to the targetDirJTextField
        targetDirJTextField.setAlignmentX( Component.LEFT_ALIGNMENT );
        targetJPanel.add( targetDirJTextField );
        constraints.anchor = GridBagConstraints.NORTHWEST;
        contentJPanel.add( targetJPanel, constraints ); // y = 0, x = 0



        JPanel picsPerRowJPanel = new JPanel();
        picsPerRowJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "picsPerRowText" ) ) );




        JPanel thumbnailJPanel = new JPanel();
        thumbnailJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "HtmlDistThumbnails" ) ) );
        thumbnailJPanel.setLayout( new BoxLayout( thumbnailJPanel, BoxLayout.PAGE_AXIS ) );



        // create the JTextField that holds the number of pictures per row
        picsPerRow.setPreferredSize( new Dimension( 100, 20 ) );
        picsPerRow.setMinimumSize( new Dimension( 100, 20 ) );
        picsPerRow.setMaximumSize( new Dimension( 200, 20 ) );
        picsPerRow.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                previewPanel.repaint();
            }
        } ); //picsPerRow.getDocument().addDocumentListener( myListener );

        picsPerRowJPanel.add( picsPerRow );
        constraints.gridy++; // y = 1, x = 0
        constraints.gridwidth = 1;
        thumbnailJPanel.add( picsPerRowJPanel );
        contentJPanel.add( thumbnailJPanel, constraints );


        // create the JTextField that holds the size of the thumbnails
        thubWidthWholeNumberField.setPreferredSize( new Dimension( 100, 20 ) );
        thubWidthWholeNumberField.setMinimumSize( new Dimension( 100, 20 ) );
        thubWidthWholeNumberField.setMaximumSize( new Dimension( 200, 20 ) );

        thubHeightWholeNumberField.setPreferredSize( new Dimension( 100, 20 ) );
        thubHeightWholeNumberField.setMinimumSize( new Dimension( 100, 20 ) );
        thubHeightWholeNumberField.setMaximumSize( new Dimension( 200, 20 ) );

        JPanel thumbnailSizeJPanel = new JPanel();
        thumbnailSizeJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "thubnailSizeJLabel" ) ) );
        thumbnailSizeJPanel.add( thubWidthWholeNumberField );
        thumbnailSizeJPanel.add( new JLabel( " x " ) );
        thumbnailSizeJPanel.add( thubHeightWholeNumberField );

        //constraints.gridx++;  // y = 1, x = 1
        //contentJPanel.add( thumbnailSizeJPanel, constraints );
        thumbnailJPanel.add( thumbnailSizeJPanel );

        // Thumbnail Quality Slider
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBad" ) ) );
        labelTable.put( new Integer( 80 ), new JLabel( Settings.jpoResources.getString( "jpgQualityGood" ) ) );
        labelTable.put( new Integer( 100 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBest" ) ) );
        lowresJpgQualityJSlider.setLabelTable( labelTable );

        lowresJpgQualityJSlider.setMajorTickSpacing( 10 );
        lowresJpgQualityJSlider.setMinorTickSpacing( 5 );
        lowresJpgQualityJSlider.setPaintTicks( true );
        lowresJpgQualityJSlider.setPaintLabels( true );
        lowresJpgQualityJSlider.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "lowresJpgQualitySlider" ) ) );
        //constraints.gridx = 0;
        //constraints.gridy++;
        //constraints.gridwidth = 1;
        //contentJPanel.add( lowresJpgQualityJSlider, constraints );
        thumbnailJPanel.add( lowresJpgQualityJSlider );


        JPanel midresJPanel = new JPanel();
        midresJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "HtmlDistMidres" ) ) );
        midresJPanel.setLayout( new BoxLayout( midresJPanel, BoxLayout.PAGE_AXIS ) );

        generateMidresHtml.setSelected( true );
        generateMidresHtml.setAlignmentX( Component.LEFT_ALIGNMENT );
        generateMidresHtml.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                generateDHTMLJCheckBox.setEnabled( generateMidresHtml.isSelected() );
                if ( !generateMidresHtml.isSelected() ) {
                    exportHighresJCheckBox.setSelected( false );
                    linkToHighresJCheckBox.setSelected( false );
                }
                exportHighresJCheckBox.setEnabled( generateMidresHtml.isSelected() );
                linkToHighresJCheckBox.setEnabled( generateMidresHtml.isSelected() );
            }
        } );
        midresJPanel.add( generateMidresHtml );

        generateDHTMLJCheckBox.setSelected( true );
        generateDHTMLJCheckBox.setAlignmentX( Component.LEFT_ALIGNMENT );
        midresJPanel.add( generateDHTMLJCheckBox );


        // create the JTextField that holds the size of the target picture
        midresWidthWholeNumberField.setPreferredSize( new Dimension( 100, 20 ) );
        midresWidthWholeNumberField.setMinimumSize( new Dimension( 100, 20 ) );
        midresWidthWholeNumberField.setMaximumSize( new Dimension( 200, 20 ) );

        midresHeightWholeNumberField.setPreferredSize( new Dimension( 100, 20 ) );
        midresHeightWholeNumberField.setMinimumSize( new Dimension( 100, 20 ) );
        midresHeightWholeNumberField.setMaximumSize( new Dimension( 200, 20 ) );

        JPanel midresSizeJPanel = new JPanel();
        midresSizeJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "midresSizeJLabel" ) ) );
        midresSizeJPanel.add( midresWidthWholeNumberField );
        midresSizeJPanel.add( new JLabel( " x " ) );
        midresSizeJPanel.add( midresHeightWholeNumberField );

        constraints.gridx++;  // y = 1, x = 2
        midresJPanel.add( midresSizeJPanel );
        contentJPanel.add( midresJPanel, constraints );

        // Midres Quality Slider
        Hashtable labelTable1 = new Hashtable();
        labelTable1.put( new Integer( 0 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBad" ) ) );
        labelTable1.put( new Integer( 80 ), new JLabel( Settings.jpoResources.getString( "jpgQualityGood" ) ) );
        labelTable1.put( new Integer( 100 ), new JLabel( Settings.jpoResources.getString( "jpgQualityBest" ) ) );
        midresJpgQualityJSlider.setLabelTable( labelTable1 );

        midresJpgQualityJSlider.setMajorTickSpacing( 10 );
        midresJpgQualityJSlider.setMinorTickSpacing( 5 );
        midresJpgQualityJSlider.setPaintTicks( true );
        midresJpgQualityJSlider.setPaintLabels( true );
        midresJpgQualityJSlider.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "midresJpgQualitySlider" ) ) );
        //constraints.gridx = 1; // constraints.gridy++;
        //constraints.gridwidth = 1;
        midresJPanel.add( midresJpgQualityJSlider );



        JPanel highresJPanel = new JPanel();
        highresJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "HtmlDistHighres" ) ) );
        highresJPanel.setLayout( new BoxLayout( highresJPanel, BoxLayout.PAGE_AXIS ) );

        // create checkbox for highres export
        highresJPanel.add( exportHighresJCheckBox );

        generateZipfileJCheckBox.setSelected( false );
        highresJPanel.add( generateZipfileJCheckBox );

        // create checkbox for linking to highres
        highresJPanel.add( linkToHighresJCheckBox );

        constraints.gridx++;
        contentJPanel.add( highresJPanel, constraints );



        JPanel optionsJPanel = new JPanel();
        optionsJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "HtmlDistOptions" ) ) );
        optionsJPanel.setLayout( new BoxLayout( optionsJPanel, BoxLayout.PAGE_AXIS ) );
        constraints.gridx = 0;
        constraints.gridy++;
        contentJPanel.add( optionsJPanel, constraints );


        //Set up color chooser for setting background color
        JPanel colorButtonPanel = new JPanel(); //use FlowLayout
        JButton fgc = new JButton( "Foreground Color" );
        fgc.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                JColorChooser tcc = new JColorChooser();
                //Remove the preview panel
                tcc.setPreviewPanel( new JPanel() );
                Color newColor = JColorChooser.showDialog(
                        HtmlDistillerJFrame.this,
                        "Choose Foreground Color",
                        previewPanel.getForeground() );
                if ( newColor != null ) {
                    previewPanel.setForeground( newColor );
                }
            }
        } );
        colorButtonPanel.add( fgc );
        JButton bgc = new JButton( "Background Color" );
        bgc.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                JColorChooser tcc = new JColorChooser();
                //Remove the preview panel
                tcc.setPreviewPanel( new JPanel() );
                Color newColor = JColorChooser.showDialog(
                        HtmlDistillerJFrame.this,
                        "Choose Foreground Color",
                        previewPanel.getBackground() );
                if ( newColor != null ) {
                    previewPanel.setBackground( newColor );
                }
            }
        } );
        colorButtonPanel.add( bgc );
        optionsJPanel.add( colorButtonPanel );


        JPanel filenameingJPanel = new JPanel();
        filenameingJPanel.setBorder(
                BorderFactory.createTitledBorder(
                Settings.jpoResources.getString( "HtmlDistillerNumbering" ) ) );
        filenameingJPanel.setLayout( new BoxLayout( filenameingJPanel, BoxLayout.PAGE_AXIS ) );


        // Image Name Source Radio Buttons
        //constraints.gridx = 0;
        //constraints.gridy++;
        //constraints.gridwidth = 2;
        //contentJPanel.add( new JLabel( Settings.jpoResources.getString( "HtmlDistillerNumbering" ) ), constraints );
        ButtonGroup bg = new ButtonGroup();
        bg.add( hashcodeRadioButton );
        //constraints.gridy++;
        //contentJPanel.add( hashcodeRadioButton, constraints );
        filenameingJPanel.add( hashcodeRadioButton );

        bg.add( originalNameRadioButton );
        //constraints.gridy++;
        //contentJPanel.add( originalNameRadioButton, constraints );
        filenameingJPanel.add( originalNameRadioButton );


        final JLabel sequentialStartLabel = new JLabel( Settings.jpoResources.getString( "sequentialRadioButtonStart" ) );
        bg.add( sequentialRadioButton );
        ChangeListener radioButtonChangeListener = new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                if ( sequentialStartJSpinner.isEnabled() != sequentialRadioButton.isSelected() ) {
                    sequentialStartJSpinner.setEnabled( sequentialRadioButton.isSelected() );
                    sequentialStartLabel.setEnabled( sequentialRadioButton.isSelected() );
                }
            }
        };
        sequentialRadioButton.addChangeListener( radioButtonChangeListener );
        hashcodeRadioButton.addChangeListener( radioButtonChangeListener );
        originalNameRadioButton.addChangeListener( radioButtonChangeListener );

        //constraints.gridy++;
        //contentJPanel.add( sequentialRadioButton, constraints );
        JPanel sequentialNumberJPanel = new JPanel();
        sequentialNumberJPanel.add( sequentialRadioButton );
        sequentialNumberJPanel.add( sequentialStartLabel );
        sequentialNumberJPanel.add( sequentialStartJSpinner );
        filenameingJPanel.add( sequentialNumberJPanel );
        optionsJPanel.add( filenameingJPanel );

        optionsJPanel.add( generateRobotsJCheckBox );

        JPanel wrappingPreviewJPanel = new JPanel();
        wrappingPreviewJPanel.setLayout( new BorderLayout() );
        wrappingPreviewJPanel.add( previewPanel, BorderLayout.CENTER );
        wrappingPreviewJPanel.setBorder( BorderFactory.createTitledBorder( "Preview" ) );
        previewPanel.setPreferredSize( new Dimension( 300, 150 ) );
        previewPanel.setMinimumSize( new Dimension( 200, 100 ) );
        previewPanel.setBackground( Settings.htmlBackgroundColor );
        previewPanel.setForeground( Settings.htmlFontColor );
        previewPanel.repaint();
        constraints.gridx++;
        //constraints.gridwidth = 3;
        //constraints.gridy++; 
        contentJPanel.add( wrappingPreviewJPanel, constraints );





        JButton okJButton = new JButton( Settings.jpoResources.getString( "genericExportButtonText" ) );
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        okJButton.setDefaultCapable( true );
        this.getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                exportToHtml();
            }
        } );


        JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        cancelJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );

        //JButton pptJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        JButton pptJButton = new JButton( "Experiment PPT" );
        pptJButton.setPreferredSize( Settings.defaultButtonDimension );
        pptJButton.setMinimumSize( Settings.defaultButtonDimension );
        pptJButton.setMaximumSize( Settings.defaultButtonDimension );
        pptJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        pptJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                exportToPpt();
            }
        } );



        JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new FlowLayout() );
        buttonJPanel.add( okJButton );
        buttonJPanel.add( cancelJButton );
        buttonJPanel.add( pptJButton );
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 3;
        contentJPanel.add( buttonJPanel, constraints );




        getContentPane().add( contentJPanel );


        //  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
        Runnable runner = new FrameShower( this, Settings.anchorFrame );
        EventQueue.invokeLater( runner );
    }

    /**
     *  Method that closes te frame and gets rid of it.
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     *  This method tries to creates the target directory (or fails with errors) 
     *  and then fires off the HtmlDistiller thread.
     */
    private void exportToHtml() {
        File htmlDirectory = new File( targetDirJTextField.getText() );
        if ( !htmlDirectory.exists() ) {
            try {
                htmlDirectory.mkdirs();
            } catch ( SecurityException e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        Settings.jpoResources.getString( "htmlDistCrtDirError" ) + "\n" + e.getMessage(),
                        Settings.jpoResources.getString( "genericSecurityException" ),
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
        } else {
            if ( !htmlDirectory.isDirectory() ) {
                JOptionPane.showMessageDialog(
                        this,
                        Settings.jpoResources.getString( "htmlDistIsDirError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( !htmlDirectory.canWrite() ) {
                JOptionPane.showMessageDialog(
                        this,
                        Settings.jpoResources.getString( "htmlDistCanWriteError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( htmlDirectory.listFiles().length > 0 ) {
                /*
                // shows which files exist
                File[] f = htmlDirectory.listFiles();
                for ( int i = 0; i < f.length; i++ ) {
                    System.out.println( f[i].getAbsolutePath() );
                } */
                int option = JOptionPane.showConfirmDialog(
                        this,
                        Settings.jpoResources.getString( "htmlDistIsNotEmptyWarning" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE );
                if ( option == JOptionPane.CANCEL_OPTION ) {
                    return;
                }
            }
        }

        getRid();

        storeSettings();

        HtmlDistillerThread h = new HtmlDistillerThread( options );
    }

    /**
     *  This method tries to creates the target directory (or fails with errors)
     *  and then fires off the HtmlDistiller thread.
     */
    private void exportToPpt() {
        File pptFile = new File( targetDirJTextField.getText() );
        try {
            SlideShow ppt = new SlideShow();

            Enumeration e = options.getStartNode().postorderEnumeration();
            while ( e.hasMoreElements() ) {
                Slide s = ppt.createSlide();

                Object o = ( (SortableDefaultMutableTreeNode) e.nextElement() ).getUserObject();
                if ( o instanceof GroupInfo ) {
                    GroupInfo gi = (GroupInfo) o;
                    TextBox txt = new TextBox();
                    txt.setText( gi.getGroupName() );
                    txt.setAnchor( new java.awt.Rectangle( 300, 100, 300, 50 ) );

                    //use RichTextRun to work with the text format
                    RichTextRun rt = txt.getTextRun().getRichTextRuns()[0];
                    rt.setFontSize( 32 );
                    rt.setFontName( "Arial" );
                    rt.setBold( true );
                    rt.setItalic( true );
                    rt.setUnderlined( true );
                    rt.setFontColor( Color.red );
                    rt.setAlignment( TextBox.AlignRight );

                    s.addShape( txt );
                } else if ( o instanceof PictureInfo ) {
                    PictureInfo pi = (PictureInfo) o;

                    // add a new picture to this slideshow and insert it in a  new slide
                    int idx = ppt.addPicture( pi.getLowresFile(), Picture.JPEG );
                    Picture pict = new Picture( idx );
                    //set image position in the slide
                    pict.setAnchor( new java.awt.Rectangle( 100, 100, 300, 200 ) );
                    s.addShape( pict );

                    TextBox txt = new TextBox();
                    txt.setText( pi.getDescription() );
                    txt.setAnchor( new java.awt.Rectangle( 300, 100, 300, 50 ) );

                    //use RichTextRun to work with the text format
                    RichTextRun rt = txt.getTextRun().getRichTextRuns()[0];
                    rt.setFontSize( 32 );
                    rt.setFontName( "Arial" );
                    rt.setBold( true );
                    rt.setItalic( true );
                    rt.setUnderlined( true );
                    rt.setFontColor( Color.blue );
                    rt.setAlignment( TextBox.AlignRight );
                    s.addShape( txt );

                }

            }

            FileOutputStream out = new FileOutputStream( pptFile );
            ppt.write( out );
            out.close();
        } catch ( IOException ex ) {
            Logger.getLogger( HtmlDistillerJFrame.class.getName() ).log( Level.SEVERE, null, ex );
        }

        getRid();

    }

    /**
     * Loads the options into the GUI fields. Note that the defaults come from
     * the settings. They are loaded when the options object is created. They are
     * populated by the HtmlDistillerDefaultOptions.
     */
    private void loadOptionsToGui() {
        targetDirJTextField.setText( options.getHtmlDirectory().toString() );
        //picsPerRow.setValue( options.getPicsPerRow() );
        ( (SpinnerNumberModel) ( picsPerRow.getModel() ) ).setValue( options.getPicsPerRow() );
        thubWidthWholeNumberField.setValue( options.getThumbnailWidth() );
        thubHeightWholeNumberField.setValue( options.getThumbnailHeight() );
        generateMidresHtml.setSelected( options.isGenerateMidresHtml() );
        midresWidthWholeNumberField.setValue( options.getMidresWidth() );
        midresHeightWholeNumberField.setValue( options.getMidresHeight() );
        switch ( options.getPictureNaming() ) {
            case HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME:
                originalNameRadioButton.setSelected( true );
                break;

            case HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER:
                sequentialRadioButton.setSelected( true );
                break;

            default:
                hashcodeRadioButton.setSelected( true );
                break;
        }
        generateRobotsJCheckBox.setSelected( options.isWriteRobotsTxt() );

    }

    /**
     * Extracts the settings from the GUI components and puts the ones to remember in 
     * the applications Settings object and also the HtmlDistillerOptions.
     */
    private void storeSettings() {
        File htmlDirectory = new File( targetDirJTextField.getText() );
        Settings.memorizeCopyLocation( htmlDirectory.getPath() );
        Settings.defaultHtmlPicsPerRow = ( (SpinnerNumberModel) ( picsPerRow.getModel() ) ).getNumber().intValue();
        Settings.defaultHtmlThumbnailWidth = thubWidthWholeNumberField.getValue();
        Settings.defaultHtmlThumbnailHeight = thubHeightWholeNumberField.getValue();
        Settings.defaultGenerateMidresHtml = generateMidresHtml.isSelected();
        Settings.defaultHtmlMidresWidth = midresWidthWholeNumberField.getValue();
        Settings.defaultHtmlMidresHeight = midresHeightWholeNumberField.getValue();
        Settings.unsavedSettingChanges = true;
        Settings.htmlBackgroundColor = previewPanel.getBackground();
        Settings.htmlFontColor = previewPanel.getForeground();
        Settings.writeRobotsTxt = generateRobotsJCheckBox.isSelected();

        options.setHtmlDirectory( htmlDirectory );
        options.setPicsPerRow( Settings.defaultHtmlPicsPerRow );
        options.setThumbnailWidth( thubWidthWholeNumberField.getValue() );
        options.setThumbnailHeight( thubHeightWholeNumberField.getValue() );
        options.setGenerateMidresHtml( generateMidresHtml.isSelected() );
        options.setMidresWidth( midresWidthWholeNumberField.getValue() );
        options.setMidresHeight( midresHeightWholeNumberField.getValue() );
        options.setExportHighres( exportHighresJCheckBox.isSelected() );
        options.setLinkToHighres( linkToHighresJCheckBox.isSelected() );
        options.setLowresJpgQualityPercent( lowresJpgQualityJSlider.getValue() );
        options.setMidresJpgQualityPercent( midresJpgQualityJSlider.getValue() );
        options.setGenerateDHTML( generateDHTMLJCheckBox.isSelected() );
        options.setGenerateZipfile( generateZipfileJCheckBox.isSelected() );
        options.setBackgroundColor( previewPanel.getBackground() );
        options.setFontColor( previewPanel.getForeground() );
        if ( originalNameRadioButton.isSelected() ) {
            options.setPictureNaming( HtmlDistillerOptions.PICTURE_NAMING_BY_ORIGINAL_NAME );
        } else if ( sequentialRadioButton.isSelected() ) {
            options.setPictureNaming( HtmlDistillerOptions.PICTURE_NAMING_BY_SEQUENTIAL_NUMBER );
        } else {
            options.setPictureNaming( HtmlDistillerOptions.PICTURE_NAMING_BY_HASH_CODE );
        }
        options.setSequentialStartNumber( ( (SpinnerNumberModel) sequentialStartJSpinner.getModel() ).getNumber().intValue() );
        options.setWriteRobotsTxt( generateRobotsJCheckBox.isSelected() );
    }

    /**
     *  Creates a Preview Panel for the HTML Export
     */
    public class PreviewPanel extends JPanel {

        //private final Font previewFont = new Font( "SansSerif", Font.BOLD, 18  );
        private final Font previewFont = Font.decode( Settings.jpoResources.getString( "HtmlDistillerPreviewFont" ) );

        /**
         *  Constructor
         */
        private PreviewPanel() {
        }

        /**
         *   This draws an image of what the HTML could look like
         */
        public void paintComponent( Graphics g ) {
            int WindowWidth = getSize().width;
            int WindowHeight = getSize().height;

            Graphics2D g2d = (Graphics2D) g;

            // draw the number of thumbnails
            int columns = ( (SpinnerNumberModel) ( picsPerRow.getModel() ) ).getNumber().intValue();
            if ( columns < 1 ) {
                columns = 1;
            }
            int imgWidth = (int) ( WindowWidth / ( columns + 2 ) );
            int halfImgWidth = (int) ( imgWidth / 2 );

            int rows = 2;
            int imgHeight = (int) ( WindowHeight / rows );
            int halfImgHeight = (int) ( imgHeight / 2 );


            g2d.setColor( getBackground() );
            g2d.fillRect( 0,
                    0,
                    WindowWidth,
                    WindowHeight );


            g2d.setColor( Color.black );

            for ( int j = 0; j < rows; j++ ) {
                for ( int i = 1; i <= columns; i++ ) {
                    g2d.fillRect( i * imgWidth,
                            j * imgHeight + halfImgHeight,
                            halfImgWidth,
                            halfImgHeight );
                }
            }

            g2d.setColor( getForeground() );
            g2d.setFont( previewFont );
            g2d.drawString( "Collection", 30, 30 );
        }
    }
}
