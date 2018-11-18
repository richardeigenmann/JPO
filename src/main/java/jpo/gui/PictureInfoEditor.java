package jpo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.SetPictureRotationRequest;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;
import jpo.dataModel.Category;
import jpo.dataModel.ExifInfo;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.Settings;
import jpo.dataModel.SingleNodeNavigator;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.gui.swing.MapViewer;
import jpo.gui.swing.NonFocussedCaret;
import jpo.gui.swing.ThreeDotButton;
import jpo.gui.swing.Thumbnail;
import net.miginfocom.swing.MigLayout;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.GeoPosition;

/*
 Copyright (C) 2002-2017  Richard Eigenmann.
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
 * An editor window that allows the attributes of a picture to be modified
 *
 * @author Richard Eigenmann
 */
public class PictureInfoEditor extends JFrame {

    /**
     * Dimension for the edit fields
     */
    private final static Dimension TEXT_FIELD_DIMENSION = new Dimension( 400, 20 );
    /**
     * Dimension for the time, latitude and longitude
     */
    private final static Dimension SHORT_FIELD_DIMENSION = new Dimension( 180, 20 );
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PictureInfoEditor.class.getName() );
    private final ThumbnailController thumbnailController = new ThumbnailController( new Thumbnail(), Settings.thumbnailSize );
    /**
     * The description of the picture
     */
    private JTextArea descriptionJTextArea = new JTextArea();
    /**
     * The location of the image file
     */
    private JTextField creationTimeJTextField = new JTextField();
    /**
     * This label will hold the parsed date of what was in the creation time.
     */
    private final JLabel parsedCreationTimeJLabel = new JLabel();
    /**
     * The location of the image file
     */
    private JTextField highresLocationJTextField = new JTextField();
    /**
     * An informative message about what sort of error we have if any on the
     * highres image
     */
    private final JLabel highresErrorJLabel = new JLabel( "" );

    /**
     * Label to display the checksum of the image file. Gets updated so it's
     * here.
     */
    private JLabel checksumJLabel = new JLabel();
    /**
     * The location of the lowres image file
     */
    //private JTextField lowresLocationJTextField = new JTextField();
    /**
     * The location of the image file
     */
    private JTextField filmReferenceJTextField = new JTextField();
    /**
     * The latitude of the image
     */
    private JFormattedTextField latitudeJTextField;
    /**
     * The longitude of the image
     */
    private JFormattedTextField longitudeJTextField;
    /**
     * The comment field
     */
    private JTextField commentJTextField = new JTextField();
    /**
     * The photographer field
     */
    private JTextField photographerJTextField = new JTextField();
    /**
     * The copyright field
     */
    private JTextField copyrightHolderJTextField = new JTextField();
    /**
     * Shows the Exif size
     */
    private final JLabel sizeJLabel = new JLabel( "" );
    /**
     * The category assignments
     */
    private final JLabel categoryAssignmentsJLabel = new JLabel();
    /**
     * The list model supporting the category assignments
     */
    private final DefaultListModel<Category> listModel = new DefaultListModel<>();
    /**
     * JList to hold the categories
     */
    private final JList<Category> categoriesJList = new JList<>( listModel );
    private final JScrollPane listJScrollPane = new JScrollPane( categoriesJList );
    private static final Category setupCategories = new Category( Integer.MIN_VALUE, Settings.jpoResources.getString( "setupCategories" ) );
    private static final Category noCategories = new Category( Integer.MIN_VALUE, Settings.jpoResources.getString( "noCategories" ) );
    /**
     * The text area to use for showing the Exif data
     */
    private final JTextArea exifTagsJTextArea = new JTextArea();
    private final SpinnerModel angleModel = new MySpinnerNumberModel();
    /**
     * the node being edited
     */
    private SortableDefaultMutableTreeNode myNode;
    /**
     * the PictureInfo object being displayed
     */
    private PictureInfo pictureInfo;
    /**
     * Font used to show the error label
     */
    private static final Font ERROR_LABEL_FONT = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelLargeFont" ) );

    /**
     * Constructs a Picture Properties Dialog
     *
     * @param setOfNodes Set of nodes
     * @param index index
     */
    public PictureInfoEditor( NodeNavigatorInterface setOfNodes, int index ) {
        this( setOfNodes.getNode( index ) );
    }

    /**
     * Constructor that creates the JFrame and objects.
     *
     * @param editNode	The node being edited.
     */
    public PictureInfoEditor( final SortableDefaultMutableTreeNode editNode ) {
        super( Settings.jpoResources.getString( "PictureInfoEditorHeading" ) );

        pictureInfo = (PictureInfo) editNode.getUserObject();
        this.myNode = editNode;

        // set this up so that we can close the GUI if the picture node is removed while we
        // are displaying it.
        editNode.getPictureCollection().getTreeModel().addTreeModelListener( myTreeModelListener );
        pictureInfo.addPictureInfoChangeListener( myPictureInfoChangeListener );

        thumbnailController.setNode( new SingleNodeNavigator( editNode ), 0 );

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        initComponents();

        loadData();
        positionMap();

        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    /**
     * initialise the Swing components and place them.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel( new MigLayout() );

        mainPanel.add( thumbnailController.getThumbnail() );

        JTabbedPane tabs = new JTabbedPane();
        mainPanel.add( tabs, "wrap" );

        JPanel rotationPanel = new JPanel();
        JLabel rotationJLabel = new JLabel( Settings.jpoResources.getString( "rotationLabel" ) );
        rotationPanel.add( rotationJLabel );

        //NumberFormat nf = new DecimalFormat( "###.##" );
        JSpinner spinner = new JSpinner( angleModel );
        spinner.addChangeListener( ( ChangeEvent e ) -> {
            //saveRotation();
        } );
        //Make the angle formatted without a thousands separator.
        spinner.setEditor( new JSpinner.NumberEditor( spinner, "###.##" ) );
        rotationPanel.add( spinner );

        JButton rotateLeftJButton = new JButton( new ImageIcon( PictureInfoEditor.class.getClassLoader().getResource( "jpo/images/icon_RotCCDown.gif" ) ) );
        rotateLeftJButton.setMnemonic( KeyEvent.VK_L );
        rotateLeftJButton.addActionListener( ( ActionEvent e ) -> {
            angleModel.setValue( ( (Double) angleModel.getValue() + 270 ) % 360 );
            saveRotation();
        } );
        rotateLeftJButton.setToolTipText( Settings.jpoResources.getString( "rotateLeftJButton.ToolTipText" ) );
        rotationPanel.add( rotateLeftJButton );

        /**
         * Button to rotate right
         */
        JButton rotateRightJButton = new JButton( new ImageIcon( PictureInfoEditor.class.getClassLoader().getResource( "jpo/images/icon_RotCWDown.gif" ) ) );
        rotateRightJButton.setMnemonic( KeyEvent.VK_R );
        rotateRightJButton.addActionListener( ( ActionEvent e ) -> {
            angleModel.setValue( ( (Double) angleModel.getValue() + 90 ) % 360 );
            saveRotation();
        } );
        rotateRightJButton.setToolTipText( Settings.jpoResources.getString( "rotateRightJButton.ToolTipText" ) );
        rotationPanel.add( rotateRightJButton );

        mainPanel.add( rotationPanel );

        JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new FlowLayout() );

        JButton OkJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
        OkJButton.setPreferredSize( Settings.defaultButtonDimension );
        OkJButton.setMinimumSize( Settings.defaultButtonDimension );
        OkJButton.setMaximumSize( Settings.defaultButtonDimension );
        OkJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        OkJButton.addActionListener( ( ActionEvent e ) -> {
            saveFieldData();
            getRid();
        } );
        OkJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( OkJButton );
        buttonJPanel.add( OkJButton );

        JButton CancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        CancelButton.setPreferredSize( Settings.defaultButtonDimension );
        CancelButton.setMinimumSize( Settings.defaultButtonDimension );
        CancelButton.setMaximumSize( Settings.defaultButtonDimension );
        CancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        CancelButton.addActionListener( ( ActionEvent e ) -> {
            getRid();
        } );
        buttonJPanel.add( CancelButton );

        JButton resetJButton = new JButton( Settings.jpoResources.getString( "resetLabel" ) );
        resetJButton.setPreferredSize( Settings.defaultButtonDimension );
        resetJButton.setMinimumSize( Settings.defaultButtonDimension );
        resetJButton.setMaximumSize( Settings.defaultButtonDimension );
        resetJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        resetJButton.addActionListener( ( ActionEvent e ) -> {
            loadData();
        } );
        buttonJPanel.add( resetJButton );
        mainPanel.add( buttonJPanel );

        JPanel infoTab = new JPanel();
        infoTab.setLayout( new MigLayout() );

        JLabel descriptionJLabel = new JLabel( Settings.jpoResources.getString( "pictureDescriptionLabel" ) );
        infoTab.add( descriptionJLabel, "span 2, wrap" );

        descriptionJTextArea.setPreferredSize( new Dimension( 400, 150 ) );
        descriptionJTextArea.setWrapStyleWord( true );
        descriptionJTextArea.setLineWrap( true );
        descriptionJTextArea.setEditable( true );
        infoTab.add( descriptionJTextArea, "span 2, wrap" );

        final JLabel sizeLabelJLabel = new JLabel( "Size:" );
        infoTab.add( sizeLabelJLabel, "aligny top" );

        infoTab.add( sizeJLabel, "aligny top, wrap" );
        
        JLabel creationTimeJLabel = new JLabel( Settings.jpoResources.getString( "creationTimeLabel" ) );
        infoTab.add( creationTimeJLabel, "spany 3, aligny top" );

        creationTimeJTextField.setPreferredSize( SHORT_FIELD_DIMENSION );
        creationTimeJTextField.addFocusListener( new FocusListener() {
            @Override
            public void focusGained( FocusEvent e ) {
                parseTimestamp( creationTimeJTextField.getText() );
            }

            @Override
            public void focusLost( FocusEvent e ) {
                parseTimestamp( creationTimeJTextField.getText() );
            }
        } );
        infoTab.add( creationTimeJTextField, "wrap" );

        parsedCreationTimeJLabel.setFont( ERROR_LABEL_FONT );
        infoTab.add( parsedCreationTimeJLabel, "wrap" );

        JButton reparseButton = new JButton( "reparse" );
        infoTab.add( reparseButton, "wrap" );
        reparseButton.addActionListener( ( ActionEvent e ) -> {
            doReparseDate();
        } );

        JLabel filmReferenceJLabel = new JLabel( Settings.jpoResources.getString( "filmReferenceLabel" ) );
        infoTab.add( filmReferenceJLabel, "span 2, wrap" );

        filmReferenceJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        infoTab.add( filmReferenceJTextField, "span 2, wrap" );

        JLabel commentJLabel = new JLabel( Settings.jpoResources.getString( "commentLabel" ) );
        infoTab.add( commentJLabel, "span 2, wrap" );

        commentJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        infoTab.add( commentJTextField, "span 2, wrap" );

        JLabel photographerJLabel = new JLabel( Settings.jpoResources.getString( "photographerLabel" ) );
        infoTab.add( photographerJLabel, "span 2, wrap" );

        photographerJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        infoTab.add( photographerJTextField, "span 2, wrap" );

        JLabel copyrightHolderJLabel = new JLabel( Settings.jpoResources.getString( "copyrightHolderLabel" ) );
        infoTab.add( copyrightHolderJLabel, "span 2, wrap" );

        copyrightHolderJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        infoTab.add( copyrightHolderJTextField, "span 2, wrap" );

        JLabel latitudeJLabel = new JLabel( Settings.jpoResources.getString( "latitudeLabel" ) );
        infoTab.add( latitudeJLabel, "aligny top" );

        NumberFormat nfl = new DecimalFormat( "###.#####################" );
        latitudeJTextField = new JFormattedTextField( nfl );
        latitudeJTextField.setPreferredSize( SHORT_FIELD_DIMENSION );
        infoTab.add( latitudeJTextField, "wrap" );

        final JLabel longitudeJLabel = new JLabel( Settings.jpoResources.getString( "longitudeLabel" ) );
        infoTab.add( longitudeJLabel, "aligny top" );

        longitudeJTextField = new JFormattedTextField( nfl );
        longitudeJTextField.setPreferredSize( SHORT_FIELD_DIMENSION );
        infoTab.add( longitudeJTextField, "wrap" );

        JScrollPane jScrollPane = new JScrollPane( infoTab );
        jScrollPane.setWheelScrollingEnabled( true );
        tabs.add( "Info", jScrollPane );

        JPanel fileTab = new JPanel( new MigLayout() );
        JLabel highresLocationJLabel = new JLabel( Settings.jpoResources.getString( "highresLocationLabel" ) );
        fileTab.add( highresLocationJLabel, "span 2, wrap" );
        highresErrorJLabel.setFont( ERROR_LABEL_FONT );
        highresLocationJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        fileTab.add( highresLocationJTextField );

        //JButton highresLocationJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );
        JButton highresLocationJButton = new ThreeDotButton();
        highresLocationJButton.addActionListener( ( ActionEvent e ) -> {
            chooseFile();
        } );
        fileTab.add( highresLocationJButton, "wrap" );
        fileTab.add( highresErrorJLabel, "span 2, wrap" );

        JButton refreshChecksumJButton = new JButton( Settings.jpoResources.getString( "checksumJButton" ) );
        refreshChecksumJButton.setPreferredSize( new Dimension( 80, 25 ) );
        refreshChecksumJButton.setMinimumSize( new Dimension( 80, 25 ) );
        refreshChecksumJButton.setMaximumSize( new Dimension( 80, 25 ) );
        refreshChecksumJButton.addActionListener( ( ActionEvent e ) -> {
            new Thread( () -> pictureInfo.calculateChecksum() ).start();
        } );

        fileTab.add( checksumJLabel );
        fileTab.add( refreshChecksumJButton, "wrap" );

        tabs.add( "File", fileTab );

        JPanel categoriesTab = new JPanel( new MigLayout() );
        JLabel categoriesJLabel = new JLabel( Settings.jpoResources.getString( "categoriesJLabel-2" ) );
        categoriesTab.add( categoriesJLabel, "wrap" );

        categoriesTab.add( categoryAssignmentsJLabel, "wrap" );

        categoriesJList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        categoriesJList.addListSelectionListener( ( ListSelectionEvent e ) -> {
            if ( e.getValueIsAdjusting() ) {
                return;
            }
            if ( categoriesJList.isSelectedIndex( ( (DefaultListModel) categoriesJList.getModel() ).indexOf( setupCategories ) ) ) {
                new CategoryEditorJFrame();
            } else if ( categoriesJList.isSelectedIndex( ( (DefaultListModel) categoriesJList.getModel() ).indexOf( noCategories ) ) ) {
                categoriesJList.clearSelection();
            }
            categoryAssignmentsJLabel.setText( selectedJListCategoriesToString( categoriesJList ) );
        } /**
         * Method from the ListSelectionListener implementation that tracks when
         * an element was selected.
         *
         * @param e
         */
        );

        categoriesTab.add( listJScrollPane, "push, grow, wrap" );
        tabs.add( "Categories", categoriesTab );

        // Add Exif panel
        exifTagsJTextArea.setWrapStyleWord( true );
        exifTagsJTextArea.setLineWrap( false );
        exifTagsJTextArea.setEditable( true );
        exifTagsJTextArea.setRows( 17 );
        exifTagsJTextArea.setColumns( 35 );

        // stop undesired scrolling in the window when doing append
        NonFocussedCaret dumbCaret = new NonFocussedCaret();
        exifTagsJTextArea.setCaret( dumbCaret );

        JScrollPane exifJScrollPane = new JScrollPane();
        exifJScrollPane.setViewportView( exifTagsJTextArea );//, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
        exifJScrollPane.setWheelScrollingEnabled( true );

        tabs.add( "Exif", exifJScrollPane );

        tabs.add( "Map", mapViewer.getJXMapViewer() );
        mapViewer.getJXMapViewer().addMouseListener( new MyMapClickListener( mapViewer.getJXMapViewer() ) );

        setLayout( new MigLayout() );
        getContentPane().add( mainPanel );
    }

    private final MapViewer mapViewer = new MapViewer();

    /**
     * populates the text fields with the values from the PictureInfo object
     */
    private void loadData() {
        creationTimeJTextField.setText( pictureInfo.getCreationTime() );
        parsedCreationTimeJLabel.setText( pictureInfo.getFormattedCreationTime() );
        descriptionJTextArea.setText( pictureInfo.getDescription() );
        highresLocationJTextField.setText( pictureInfo.getImageLocation() );
        checksumJLabel.setText( Settings.jpoResources.getString( "checksumJLabel" ) + pictureInfo.getChecksumAsString() );
        filmReferenceJTextField.setText( pictureInfo.getFilmReference() );
        LOGGER.info( String.format( "Retrieving angle: %f", pictureInfo.getRotation() ) );
        angleModel.setValue( pictureInfo.getRotation() );
        latitudeJTextField.setText( Double.toString( pictureInfo.getLatLng().x ) );
        longitudeJTextField.setText( Double.toString( pictureInfo.getLatLng().y ) );
        commentJTextField.setText( pictureInfo.getComment() );
        photographerJTextField.setText( pictureInfo.getPhotographer() );
        copyrightHolderJTextField.setText( pictureInfo.getCopyrightHolder() );

        listModel.removeAllElements();
        categoriesJList.clearSelection();
        listModel.addElement( setupCategories );
        listModel.addElement( noCategories );

        List<Integer> selections = new ArrayList<>();
        myNode.getPictureCollection().getCategoryKeySet().stream().forEach( ( key ) -> {
            String category = myNode.getPictureCollection().getCategory( key );
            Category categoryObject = new Category( key, category );
            listModel.addElement( categoryObject );
            if ( ( pictureInfo.categoryAssignments != null ) && ( pictureInfo.categoryAssignments.contains( key ) ) ) {
                selections.add( listModel.indexOf( categoryObject ) );
            }
        } );

        int selectionsArray[] = new int[selections.size()];
        int j = 0;
        for ( Integer key : selections ) {
            selectionsArray[j] = ( key );
            j++;
        }
        categoriesJList.setSelectedIndices( selectionsArray );
        categoryAssignmentsJLabel.setText( selectedJListCategoriesToString( categoriesJList ) );

        ExifInfo exifInfo = new ExifInfo( pictureInfo.getImageURLOrNull() );
        exifInfo.decodeExifTags();

        sizeJLabel.setText( String.format( "%s x %s", exifInfo.exifWidth, exifInfo.exifHeight ) );

        exifTagsJTextArea.append( Settings.jpoResources.getString( "ExifTitle" ) );
        exifTagsJTextArea.append( exifInfo.getComprehensivePhotographicSummary() );
        exifTagsJTextArea.append( "-------------------------\nAll Tags:\n" );
        exifTagsJTextArea.append( exifInfo.getAllTags() );

        setColorIfError();
    }
    /**
     * Set up a PictureInfoChangeListener to get updated on change events in the
     * Picture Metadata
     */
    private final PictureInfoChangeListener myPictureInfoChangeListener = new PictureInfoChangeListener() {
        /**
         * here we get notified by the PictureInfo object that something has
         * changed.
         */
        @Override
        public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
            if ( e.getDescriptionChanged() ) {
                descriptionJTextArea.setText( pictureInfo.getDescription() );
            }
            if ( e.getHighresLocationChanged() ) {
                highresLocationJTextField.setText( pictureInfo.getImageLocation() );
            }
            if ( e.getChecksumChanged() ) {
                checksumJLabel.setText( Settings.jpoResources.getString( "checksumJLabel" ) + pictureInfo.getChecksumAsString() );
            }
            /*if ( e.getLowresLocationChanged() ) {
             lowresLocationJTextField.setText( pictureInfo.getLowresLocation() );
             }*/
            if ( e.getCreationTimeChanged() ) {
                creationTimeJTextField.setText( pictureInfo.getCreationTime() );
                parsedCreationTimeJLabel.setText( pictureInfo.getFormattedCreationTime() );
            }
            if ( e.getFilmReferenceChanged() ) {
                filmReferenceJTextField.setText( pictureInfo.getFilmReference() );
            }
            if ( e.getRotationChanged() ) {
                LOGGER.info( String.format( "Processing a Rotation Changed notification: angle is: %f", pictureInfo.getRotation() ) );
                angleModel.setValue( pictureInfo.getRotation() );
            }
            if ( e.getLatLngChanged() ) {
                latitudeJTextField.setText( Double.toString( pictureInfo.getLatLng().x ) );
                longitudeJTextField.setText( Double.toString( pictureInfo.getLatLng().y ) );
                positionMap();
            }
            if ( e.getCommentChanged() ) {
                commentJTextField.setText( pictureInfo.getComment() );
            }
            if ( e.getPhotographerChanged() ) {
                photographerJTextField.setText( pictureInfo.getPhotographer() );
            }
            if ( e.getCopyrightHolderChanged() ) {
                copyrightHolderJTextField.setText( pictureInfo.getCopyrightHolder() );
            }

        }
    };

    /**
     * This utility method builds a string from the selected categories in a
     * supplied JList
     * 
     * TODO: Make it a Java 8 stream?
     *
     * @param theList the List
     * @return a string for the selected categories
     */
    private static String selectedJListCategoriesToString( JList<Category> theList ) {
        StringBuilder resultString = new StringBuilder( "" );
        if ( !theList.isSelectionEmpty() ) {
            List<Category> selectedCategories = theList.getSelectedValuesList();
            String comma = "";
            for ( Category c : selectedCategories ) {
                if ( ! ( ( c.equals( setupCategories) ) || ( c.equals(noCategories) ) ) ) {
                    resultString.append( comma ).append( c.toString() );
                    comma = ", ";
                }
            }
        }
        return resultString.toString();
    }

    /**
     * method that sets the URL fields to red if the file is not found and red
     * if the URL is not a valid URL
     */
    private void setColorIfError() {
        try {
            testFile( highresLocationJTextField.getText() );
            highresLocationJTextField.setForeground( Color.black );
            highresErrorJLabel.setText( "" );
        } catch ( Exception xh ) {
            highresLocationJTextField.setForeground( Color.red );
            highresErrorJLabel.setText( xh.getMessage() );
        }

        /*try {
         testFile( lowresLocationJTextField.getText() );
         lowresLocationJTextField.setForeground( Color.black );
         lowresErrorJLabel.setText( "" );
         } catch ( Exception xl ) {
         lowresLocationJTextField.setForeground( Color.red );
         lowresErrorJLabel.setText( xl.getMessage() );
         }*/
    }

    /**
     * Returns true if the file is good, an Exception if bad.
     *
     * @param fileToTest File to test
     * @return true if the file is good, an Exception if bad.
     * @throws Exception if inputs were no good
     */
    private boolean testFile( String fileToTest ) throws Exception {
        try {
            URL pictureUrl = new URL( fileToTest );
            InputStream inputStream = pictureUrl.openStream();
            inputStream.close();
            return true;
        } catch ( MalformedURLException x ) {
            LOGGER.log( Level.INFO, "MalformedURLException: {0}", x.getMessage() );
            throw new Exception( x );
        } catch ( IOException x ) {
            LOGGER.log( Level.INFO, "IOException: {0}", x.getMessage() );
            throw new Exception( x );
        }

    }

    /**
     * Close the editor window and release all listeners.
     */
    private void getRid() {
        if ( myNode.getPictureCollection().getTreeModel() != null ) {
            myNode.getPictureCollection().getTreeModel().removeTreeModelListener( myTreeModelListener );
        }
        pictureInfo.removePictureInfoChangeListener( myPictureInfoChangeListener );
        setVisible( false );
        dispose();
    }

    /**
     * Reparse the date from the EXIF information
     */
    private void doReparseDate() {
        try {
            ExifInfo exifInfo = new ExifInfo( new URL( highresLocationJTextField.getText() ) );
            exifInfo.decodeExifTags();
            String timestamp = exifInfo.getCreateDateTime();
            creationTimeJTextField.setText( timestamp );
            parseTimestamp( timestamp );

        } catch ( MalformedURLException ex ) {
            Logger.getLogger( PictureInfoEditor.class
                    .getName() ).log( Level.SEVERE, null, ex );
        }
    }

    /**
     * Parses the supplied timestamp and shows the result in the
     * parsedCreationTimeJLabel
     *
     * @param timestamp Timestamp
     */
    private void parseTimestamp( String timestamp ) {
        parsedCreationTimeJLabel.setText( String.format( "%tc", Tools.parseDate( timestamp ) ) );
    }

    /**
     * saves the data in the fields back to the PictureInfo object
     */
    private void saveFieldData() {
        pictureInfo.setDescription( descriptionJTextArea.getText() );
        pictureInfo.setCreationTime( creationTimeJTextField.getText() );
        pictureInfo.setImageLocation( highresLocationJTextField.getText() );
        pictureInfo.setComment( commentJTextField.getText() );
        pictureInfo.setPhotographer( photographerJTextField.getText() );
        pictureInfo.setFilmReference( filmReferenceJTextField.getText() );
        pictureInfo.setCopyrightHolder( copyrightHolderJTextField.getText() );

        saveRotation();

        pictureInfo.setLatLng( getLatLng() );
        int[] indexes = categoriesJList.getSelectedIndices();
        Object o;
        pictureInfo.clearCategoryAssignments();

        for (int indexe : indexes) {
            o = listModel.getElementAt(indexe);

            if (o instanceof Category) {
                pictureInfo.addCategoryAssignment(((Category) o).getKey());

            }
        }
    }

    /**
     * Returns a point with the latitude and longitude of the values in the
     * textfields. If the text can't be parsed properly the previous value is
     * returned.
     *
     * @return the point on the globe
     */
    private Point2D.Double getLatLng() {
        Double latitude;
        try {
            latitude = Double.parseDouble( latitudeJTextField.getText() );

        } catch ( NumberFormatException ex ) {
            latitude = pictureInfo.getLatLng().x;
            LOGGER.info( String.format( "Latitude String %s could not be parsed: %s --> leaving at old value: %f", latitudeJTextField.getText(), ex.getMessage(), latitude ) );
        }
        Double longitude;
        try {
            longitude = Double.parseDouble( longitudeJTextField.getText() );
        } catch ( NumberFormatException ex ) {
            longitude = pictureInfo.getLatLng().y;
            LOGGER.info( String.format( "Longitude String %s could not be parsed: %s --> leaving at old value: %f", longitudeJTextField.getText(), ex.getMessage(), longitude ) );
        }
        return new Point2D.Double( latitude, longitude );
    }

    /**
     * This method saves the rotation value
     */
    private void saveRotation() {
        JpoEventBus.getInstance().post( new SetPictureRotationRequest( myNode, (double) angleModel.getValue(), QUEUE_PRIORITY.HIGH_PRIORITY ) );
        //pictureInfo.setRotation( (Double) angleModel.getValue() );
    }

    /**
     * method that brings up a JFileChooser and places the path of the file
     * selected into the JTextField of the highres locations
     */
    private void chooseFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter( new ImageFilter() );

        jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "genericSelectText" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "highresChooserTitle" ) );
        jFileChooser.setCurrentDirectory( new File( highresLocationJTextField.getText() ) );

        int returnVal = jFileChooser.showOpenDialog( this );

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            try {
                highresLocationJTextField.setText( jFileChooser.getSelectedFile().toURI().toURL().toString() );

            } catch ( MalformedURLException x ) {
                // don't update the text field then
            }
        }
        setColorIfError();
    }
    /**
     * Set up a TreeModelListener to learn of updates to the tree and be able to
     * close the window if the node we are editing has been removed or to update
     * the fields if it was changed.
     */
    private final TreeModelListener myTreeModelListener = new TreeModelListener() {
        // Here we are not that interested in TreeModel change events other than to find out if our
        // current node was removed in which case we close the Window.
        /**
         * implemented here to satisfy the TreeModelListener interface; not
         * used.
         *
         * @param e
         */
        @Override
        public void treeNodesChanged( TreeModelEvent e ) {
        }

        /**
         * implemented here to satisfy the TreeModelListener interface; not
         * used.
         *
         * @param e
         */
        @Override
        public void treeNodesInserted( TreeModelEvent e ) {
        }

        /**
         * The TreeModelListener interface tells us of tree node removal events.
         * We use this here to determine if the node being displayed is the one
         * removed or whether it is a child of the removed nodes. If so we close
         * the window.
         *
         * @param e
         */
        @Override
        public void treeNodesRemoved( TreeModelEvent e ) {
            if ( SortableDefaultMutableTreeNode.wasNodeDeleted( myNode, e ) ) {
                getRid();

            }
        }

        /**
         * implemented here to satisfy the TreeModelListener interface; not
         * used.
         *
         * @param e
         */
        @Override
        public void treeStructureChanged( TreeModelEvent e ) {
        }
    };

    private class MySpinnerNumberModel
            extends SpinnerNumberModel {

        MySpinnerNumberModel() {
            super( 0.0, //initial value
                    0.0, //min
                    359.9, //max
                    .1f );                //step
        }

        @Override
        public Object getNextValue() {
            Object object = super.getNextValue();
            if ( object == null ) {
                object = 0.0;
            }
            return object;
        }

        @Override
        public Object getPreviousValue() {
            Object object = super.getPreviousValue();
            if ( object == null ) {
                object = 359.9;
            }
            return object;
        }
    }

    private void positionMap() {
        mapViewer.setMarker( getLatLng() );

    }

    private class MyMapClickListener extends MapClickListener {

        MyMapClickListener( JXMapViewer viewer ) {
            super( viewer );
        }

        @Override
        public void mapClicked( GeoPosition location ) {
            latitudeJTextField.setText( Double.toString( location.getLatitude() ) );
            longitudeJTextField.setText( Double.toString( location.getLongitude() ) );
            positionMap();
        }

    }

}
