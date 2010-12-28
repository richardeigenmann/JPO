package jpo.gui;

import jpo.dataModel.SingleNodeNavigator;
import jpo.gui.swing.NonFocussedCaret;
import jpo.dataModel.Tools;
import jpo.dataModel.ExifInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.Category;
import jpo.dataModel.PictureInfo;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Logger;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.gui.swing.ThreeDotButton;
import net.miginfocom.swing.MigLayout;
import webserver.Webserver;

/*
PictureInfoEditor:  Edits the details of a picture

Copyright (C) 2002-2010  Richard Eigenmann.
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
 * An editor window that allows the attributes of a picutre to be modified
 *
 * @author 	Richard Eigenmann
 */
public class PictureInfoEditor
        extends JFrame {

    /**
     *  Dimension for the edit fields
     */
    private final static Dimension TEXT_FIELD_DIMENSION = new Dimension( 400, 20 );

    /**
     *  Dimension for the time, latitude and longitude
     */
    private final static Dimension SHORT_FIELD_DIMENSION = new Dimension( 180, 20 );

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureInfoEditor.class.getName() );

    private ThumbnailController thumbnailController = new ThumbnailController( Settings.thumbnailSize );

    /**
     *   The description of the picture
     */
    private JTextArea descriptionJTextArea = new JTextArea();

    /**
     *   The location of the image file
     */
    private JTextField creationTimeJTextField = new JTextField();

    /**
     *   This label will hold the parsed date of what whas in the creation time.
     */
    private JLabel parsedCreationTimeJLabel = new JLabel();

    /**
     *   The location of the image file
     */
    private JTextField highresLocationJTextField = new JTextField();

    /**
     *   An informative message about what sort of error we have if any on the highres image
     */
    private JLabel highresErrorJLabel = new JLabel( "" );

    /**
     *   An informative message about what sort of error we have if any on the lowres image
     */
    private JLabel lowresErrorJLabel = new JLabel( "" );

    /**
     *   Label to display the checksum of the image file. Gets updated so it's here.
     */
    private JLabel checksumJLabel = new JLabel();

    /**
     *   The location of the lowres image file
     */
    private JTextField lowresLocationJTextField = new JTextField();

    /**
     *   The location of the image file
     */
    private JTextField filmReferenceJTextField = new JTextField();

    /**
     *   The latitude of the image
     */
    private JFormattedTextField latitudeJTextField;

    /**
     *   The longitude of the image
     */
    private JFormattedTextField longitudeJTextField;

    /**
     *   The comment field
     */
    private JTextField commentJTextField = new JTextField();

    /**
     *   The photographer field
     */
    private JTextField photographerJTextField = new JTextField();

    /**
     *   The copyright field
     */
    private JTextField copyrightHolderJTextField = new JTextField();

    /**
     *   The category assignments
     */
    private final JLabel categoryAssignmentsJLabel = new JLabel();

    /**
     *  The list model supporting the category assignments
     */
    private final DefaultListModel listModel = new DefaultListModel();

    /**
     *  JList to hold the categories
     */
    private final JList categoriesJList = new JList( listModel );

    private final JScrollPane listJScrollPane = new JScrollPane( categoriesJList );

    private static final Object setupCategories = new Object() {

        @Override
        public String toString() {
            return Settings.jpoResources.getString( "setupCategories" );
        }
    };

    private static final Object noCategories = new Object() {

        @Override
        public String toString() {
            return Settings.jpoResources.getString( "noCategories" );
        }
    };

    /**
     * The text area to use for showing the Exif data
     */
    private JTextArea exifTagsJTextArea = new JTextArea();

    private final SpinnerModel angleModel = new MySpinnerNumberModel();

    /**
     *  the node being edited
     */
    private SortableDefaultMutableTreeNode myNode;

    /**
     *  the PictureInfo obejct being displayed
     */
    private PictureInfo pi;

    /**
     *  Font used to show the error label
     */
    private static Font errorLabelFont = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelLargeFont" ) );


    /**
     * Constructs a Picture Properties Dialog
     * @param setOfNodes
     * @param index
     */
    public PictureInfoEditor( NodeNavigatorInterface setOfNodes, int index ) {
        this( setOfNodes.getNode( index ) );
    }


    /**
     *   Constructor that creates the JFrame and objects.
     *   @param   editNode	The node being edited.
     */
    public PictureInfoEditor( final SortableDefaultMutableTreeNode editNode ) {
        super( Settings.jpoResources.getString( "PictureInfoEditorHeading" ) );

        try {
            pi = (PictureInfo) editNode.getUserObject();
        } catch ( ClassCastException x ) {
            logger.severe( "This class can only be called with a PictureInfo bearning node." );
            x.printStackTrace();
            return;
        }
        this.myNode = editNode;

        // set this up so that we can close the GUI if the picture node is removed while we
        // are displaying it.
        editNode.getPictureCollection().getTreeModel().addTreeModelListener( myTreeModelListener );
        pi.addPictureInfoChangeListener( myPictureInfoChangeListener );

        thumbnailController.setNode( new SingleNodeNavigator( editNode ), 0 );

        initComponents();

        loadData();

        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }


    private void initComponents() {
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        JPanel mainPanel = new JPanel( new MigLayout() );

        mainPanel.add( thumbnailController.getThumbnail() );

        JTabbedPane tabs = new JTabbedPane();
        mainPanel.add( tabs, "wrap" );

        JPanel rotationPanel = new JPanel();
        JLabel rotationJLabel = new JLabel( Settings.jpoResources.getString( "rotationLabel" ) );
        rotationPanel.add( rotationJLabel );

        NumberFormat nf = new DecimalFormat( "###.##" );


        JSpinner spinner = new JSpinner( angleModel );
        spinner.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                saveRotation();
            }
        } );
        //Make the angle formatted without a thousands separator.
        spinner.setEditor( new JSpinner.NumberEditor( spinner, "###.##" ) );
        rotationPanel.add( spinner );




        JButton rotateLeftJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCCDown.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_L );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        angleModel.setValue( ( (Double) angleModel.getValue() + 270 ) % 360 );
                        saveRotation();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "rotateLeftJButton.ToolTipText" ) );
            }
        };
        rotationPanel.add( rotateLeftJButton );

        /**
         *  Button to rotate right
         */
        JButton rotateRightJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCWDown.gif" ) ) ) {

            {
                setMnemonic( KeyEvent.VK_R );
                addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        angleModel.setValue( ( (Double) angleModel.getValue() + 90 ) % 360 );
                        saveRotation();
                    }
                } );
                setToolTipText( Settings.jpoResources.getString( "rotateRightJButton.ToolTipText" ) );
            }
        };
        rotationPanel.add( rotateRightJButton );

        mainPanel.add( rotationPanel );


        JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new FlowLayout() );

        JButton OkJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
        OkJButton.setPreferredSize( Settings.defaultButtonDimension );
        OkJButton.setMinimumSize( Settings.defaultButtonDimension );
        OkJButton.setMaximumSize( Settings.defaultButtonDimension );
        OkJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        OkJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                saveFieldData();
                getRid();
            }
        } );
        OkJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( OkJButton );
        buttonJPanel.add( OkJButton );

        JButton CancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        CancelButton.setPreferredSize( Settings.defaultButtonDimension );
        CancelButton.setMinimumSize( Settings.defaultButtonDimension );
        CancelButton.setMaximumSize( Settings.defaultButtonDimension );
        CancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        CancelButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        buttonJPanel.add( CancelButton );

        JButton resetJButton = new JButton( Settings.jpoResources.getString( "resetLabel" ) );
        resetJButton.setPreferredSize( Settings.defaultButtonDimension );
        resetJButton.setMinimumSize( Settings.defaultButtonDimension );
        resetJButton.setMaximumSize( Settings.defaultButtonDimension );
        resetJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        resetJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                loadData();
            }
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


        JLabel creationTimeJLabel = new JLabel( Settings.jpoResources.getString( "creationTimeLabel" ) );
        infoTab.add( creationTimeJLabel, "spany 2, aligny top" );

        creationTimeJTextField.setPreferredSize( SHORT_FIELD_DIMENSION );
        creationTimeJTextField.addFocusListener( new FocusListener() {

            public void focusGained( FocusEvent e ) {
                parseit();
            }


            public void focusLost( FocusEvent e ) {
                parseit();
            }


            private void parseit() {
                parsedCreationTimeJLabel.setText( String.format( "%tc", Tools.parseDate( creationTimeJTextField.getText() ) ) );
            }
        } );
        infoTab.add( creationTimeJTextField, "wrap" );

        parsedCreationTimeJLabel.setFont( errorLabelFont );
        infoTab.add( parsedCreationTimeJLabel, "wrap" );


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

        JLabel longitudeJLabel = new JLabel( Settings.jpoResources.getString( "longitudeLabel" ) );
        infoTab.add( longitudeJLabel, "spany 2, aligny top" );

        longitudeJTextField = new JFormattedTextField( nfl );
        longitudeJTextField.setPreferredSize( SHORT_FIELD_DIMENSION );
        infoTab.add( longitudeJTextField, "wrap" );

        JButton mapButton = new JButton( "Open Map (in Browser)" );
        mapButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                Webserver.getInstance().browse( myNode );
            }
        } );
        infoTab.add( mapButton, "span 2, wrap" );

        JScrollPane jScrollPane = new JScrollPane( infoTab );
        jScrollPane.setWheelScrollingEnabled( true );
        tabs.add( "Info", jScrollPane );





        JPanel fileTab = new JPanel( new MigLayout() );
        JLabel highresLocationJLabel = new JLabel( Settings.jpoResources.getString( "highresLocationLabel" ) );
        fileTab.add( highresLocationJLabel, "span 2, wrap" );
        highresErrorJLabel.setFont( errorLabelFont );
        highresLocationJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        fileTab.add( highresLocationJTextField );

        //JButton highresLocationJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );
        JButton highresLocationJButton = new ThreeDotButton();
        highresLocationJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                chooseFile();
            }
        } );
        fileTab.add( highresLocationJButton, "wrap" );
        fileTab.add( highresErrorJLabel, "span 2, wrap" );


        JButton checksumJButton = new JButton( Settings.jpoResources.getString( "checksumJButton" ) );
        checksumJButton.setPreferredSize( new Dimension( 80, 25 ) );
        checksumJButton.setMinimumSize( new Dimension( 80, 25 ) );
        checksumJButton.setMaximumSize( new Dimension( 80, 25 ) );
        checksumJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                pi.calculateChecksum();
            }
        } );

        fileTab.add( checksumJLabel );
        fileTab.add( checksumJButton, "wrap" );

        JLabel lowresLocationJLabel = new JLabel( Settings.jpoResources.getString( "lowresLocationLabel" ) );
        lowresErrorJLabel.setFont( errorLabelFont );
        fileTab.add( lowresLocationJLabel, "wrap" );
        lowresLocationJTextField.setPreferredSize( TEXT_FIELD_DIMENSION );
        fileTab.add( lowresLocationJTextField, "wrap" );
        fileTab.add( lowresErrorJLabel, "wrap" );

        tabs.add( "File", fileTab );






        JPanel categoriesTab = new JPanel( new MigLayout() );
        JLabel categoriesJLabel = new JLabel( Settings.jpoResources.getString( "categoriesJLabel-2" ) );
        categoriesTab.add( categoriesJLabel, "wrap" );

        categoriesTab.add( categoryAssignmentsJLabel, "wrap" );

        categoriesJList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        categoriesJList.addListSelectionListener( new ListSelectionListener() {

            /**
             *  Method from the ListSelectionListener implementation that tracks when an
             *  element was selected.
             * @param e
             */
            public void valueChanged( ListSelectionEvent e ) {
                if ( e.getValueIsAdjusting() ) {
                    return;
                }
                JList theList = (JList) e.getSource();
                if ( theList.isSelectedIndex( ( (DefaultListModel) theList.getModel() ).indexOf( setupCategories ) ) ) {
                    new CategoryEditorJFrame();
                } else if ( theList.isSelectedIndex( ( (DefaultListModel) theList.getModel() ).indexOf( noCategories ) ) ) {
                    categoriesJList.clearSelection();
                }
                categoryAssignmentsJLabel.setText( selectedJListCategoriesToString( theList ) );
            }
        } );


        categoriesTab.add( listJScrollPane, "wrap" );
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



        setLayout( new MigLayout() );
        getContentPane().add( mainPanel );
    }


    /**
     *  populates the text fields with the values from the PictureInfo object
     */
    private void loadData() {
        creationTimeJTextField.setText( pi.getCreationTime() );
        parsedCreationTimeJLabel.setText( pi.getFormattedCreationTime() );
        descriptionJTextArea.setText( pi.getDescription() );
        highresLocationJTextField.setText( pi.getHighresLocation() );
        checksumJLabel.setText( Settings.jpoResources.getString( "checksumJLabel" ) + pi.getChecksumAsString() );
        lowresLocationJTextField.setText( pi.getLowresLocation() );
        filmReferenceJTextField.setText( pi.getFilmReference() );
        angleModel.setValue( pi.getRotation() );
        latitudeJTextField.setText( Double.toString( pi.getLatLng().x ) );
        longitudeJTextField.setText( Double.toString( pi.getLatLng().y ) );
        commentJTextField.setText( pi.getComment() );
        photographerJTextField.setText( pi.getPhotographer() );
        copyrightHolderJTextField.setText( pi.getCopyrightHolder() );

        listModel.removeAllElements();
        categoriesJList.clearSelection();
        listModel.addElement( setupCategories );
        listModel.addElement( noCategories );

        //TODO: can the iterator and enumeration use generics and be written nicer?
        Iterator i = myNode.getPictureCollection().getCategoryIterator();
        Integer key;
        String category;
        Category categoryObject;
        Vector<Integer> selections = new Vector<Integer>();
        while ( i.hasNext() ) {
            key = (Integer) i.next();
            category = myNode.getPictureCollection().getCategory( key );
            categoryObject = new Category( key, category );
            listModel.addElement( categoryObject );

            if ( ( pi.categoryAssignments != null ) && ( pi.categoryAssignments.contains( key ) ) ) {
                selections.add( new Integer( listModel.indexOf( categoryObject ) ) );
            }
        }
        Enumeration e = selections.elements();
        int selectionsArray[] = new int[selections.size()];
        Object o;
        int j = 0;
        while ( e.hasMoreElements() ) {
            o = e.nextElement();
            selectionsArray[j] = ( (Integer) o ).intValue();
            j++;
        }
        categoriesJList.setSelectedIndices( selectionsArray );
        categoryAssignmentsJLabel.setText( selectedJListCategoriesToString( categoriesJList ) );

        ExifInfo ei = new ExifInfo( pi.getHighresURLOrNull() );
        ei.decodeExifTags();
        exifTagsJTextArea.append( Settings.jpoResources.getString( "ExifTitle" ) );
        exifTagsJTextArea.append( ei.getComprehensivePhotographicSummary() );
        exifTagsJTextArea.append( "-------------------------\nAll Tags:\n" );
        exifTagsJTextArea.append( ei.getAllTags() );

        setColorIfError();
    }

    /**
     * Set up a PictureInfoChangeListener to get updated on change events in the Picture Metadata
     */
    private PictureInfoChangeListener myPictureInfoChangeListener = new PictureInfoChangeListener() {

        /**
         *  here we get notified by the PictureInfo object that something has
         *  changed.
         */
        public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
            if ( e.getDescriptionChanged() ) {
                descriptionJTextArea.setText( pi.getDescription() );
            }
            if ( e.getHighresLocationChanged() ) {
                highresLocationJTextField.setText( pi.getHighresLocation() );
            }
            if ( e.getChecksumChanged() ) {
                checksumJLabel.setText( Settings.jpoResources.getString( "checksumJLabel" ) + pi.getChecksumAsString() );
            }
            if ( e.getLowresLocationChanged() ) {
                lowresLocationJTextField.setText( pi.getLowresLocation() );
            }
            if ( e.getCreationTimeChanged() ) {
                creationTimeJTextField.setText( pi.getCreationTime() );
                parsedCreationTimeJLabel.setText( pi.getFormattedCreationTime() );
            }
            if ( e.getFilmReferenceChanged() ) {
                filmReferenceJTextField.setText( pi.getFilmReference() );
            }
            if ( e.getRotationChanged() ) {
                angleModel.setValue( pi.getRotation() );
            }
            if ( e.getLatLngChanged() ) {
                latitudeJTextField.setText( Double.toString( pi.getLatLng().x ) );
                longitudeJTextField.setText( Double.toString( pi.getLatLng().y ) );
            }
            if ( e.getCommentChanged() ) {
                commentJTextField.setText( pi.getComment() );
            }
            if ( e.getPhotographerChanged() ) {
                photographerJTextField.setText( pi.getPhotographer() );
            }
            if ( e.getCopyrightHolderChanged() ) {
                copyrightHolderJTextField.setText( pi.getCopyrightHolder() );
            }

        }
    };


    /**
     *  This utility method builds a string from the selected categories in a supplied JList
     * @param theList
     * @return
     */
    private static String selectedJListCategoriesToString( JList theList ) {
        StringBuffer resultString = new StringBuffer( "" );
        if ( !theList.isSelectionEmpty() ) {
            Object[] selectedCategories = theList.getSelectedValues();
            for ( int i = 0; i < selectedCategories.length; i++ ) {
                if ( ( selectedCategories[i] == setupCategories ) || ( selectedCategories[i] == noCategories ) ) {
                    // skip them
                } else if ( i == 0 ) {
                    resultString = new StringBuffer( selectedCategories[i].toString() );
                } else {
                    resultString.append( ", " + selectedCategories[i].toString() );
                }
            }
        }
        return resultString.toString();
    }


    /**
     *  method that sets the URL fields to red if the file is not found and
     *  organge if the URL is not a valid URL
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

        try {
            testFile( lowresLocationJTextField.getText() );
            lowresLocationJTextField.setForeground( Color.black );
            lowresErrorJLabel.setText( "" );
        } catch ( Exception xl ) {
            lowresLocationJTextField.setForeground( Color.red );
            lowresErrorJLabel.setText( xl.getMessage() );
        }
    }


    /**
     * Returns true if the file is good, an Exception if bad.
     * @return true if the file is good, an Exception if bad.
     */
    private boolean testFile( String fileToTest ) throws Exception {
        try {
            URL pictureUrl = new URL( fileToTest );
            InputStream inputStream = pictureUrl.openStream();
            inputStream.close();
            return true;
        } catch ( MalformedURLException x ) {
            logger.info( "Exception trapped: " + x.getMessage() );
            throw new Exception( x );
        } catch ( IOException x ) {
            logger.info( "Exception trapped: " + x.getMessage() );
            throw new Exception( x );
        }

    }


    /**
     *  Close the editor window and release all listeners.
     */
    private void getRid() {
        if ( myNode.getPictureCollection().getTreeModel() != null ) {
            myNode.getPictureCollection().getTreeModel().removeTreeModelListener( myTreeModelListener );
        }
        pi.removePictureInfoChangeListener( myPictureInfoChangeListener );
        setVisible( false );
        dispose();


    }


    /**
     * saves the data in the fields back to the PictureInfo object
     */
    private void saveFieldData() {
        pi.setDescription( descriptionJTextArea.getText() );
        pi.setCreationTime( creationTimeJTextField.getText() );
        pi.setHighresLocation( highresLocationJTextField.getText() );
        pi.setLowresLocation( lowresLocationJTextField.getText() );
        pi.setComment( commentJTextField.getText() );
        pi.setPhotographer( photographerJTextField.getText() );
        pi.setFilmReference( filmReferenceJTextField.getText() );
        pi.setCopyrightHolder( copyrightHolderJTextField.getText() );

        saveRotation();

        Double latitude;


        try {
            latitude = Double.parseDouble( latitudeJTextField.getText() );


        } catch ( NumberFormatException ex ) {
            latitude = pi.getLatLng().x;
            logger.info( String.format( "Latitude String %s could not be parsed: %s --> leaving at old value: %f", latitudeJTextField.getText(), ex.getMessage(), latitude ) );


        }

        Double longitude;


        try {
            longitude = Double.parseDouble( longitudeJTextField.getText() );


        } catch ( NumberFormatException ex ) {
            longitude = pi.getLatLng().y;
            logger.info( String.format( "Longitude String %s could not be parsed: %s --> leaving at old value: %f", longitudeJTextField.getText(), ex.getMessage(), longitude ) );


        }
        pi.setLatLng( new Point2D.Double( latitude, longitude ) );



        int[] indexes = categoriesJList.getSelectedIndices();
        Object o;
        pi.clearCategoryAssignments();


        for ( int i = 0; i < indexes.length; i++ ) {
            o = listModel.getElementAt( indexes[i] );


            if ( o instanceof Category ) {
                pi.addCategoryAssignment( ( (Category) o ).getKey() );


            }
        }
        /*myNode.getPictureCollection().sendNodeChanged( myNode );
        myNode.getPictureCollection().setUnsavedUpdates();*/
    }


    /**
     * This method saves the rotation value
     */
    private void saveRotation() {
        pi.setRotation( (Double) angleModel.getValue() );
    }


    /**
     *  method that brings up a JFileChooser and places the path of the file selected into the
     *  JTextField of the highres locations
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
        /*if ( jFileChooser.getSelectedFile().exists() ) {
        highresLocationJTextField.setForeground( Color.black );
        } else {
        highresLocationJTextField.setForeground( Color.red );
        }*/

    }

    /**
     * Set up a TreeModelListener to learn of updates to the tree and be
     * able to close the window if the node we are editing has been removed or
     * to update the fields if it was changed.
     */
    private TreeModelListener myTreeModelListener = new TreeModelListener() {

        // Here we are not that interested in TreeModel change events other than to find out if our
        // current node was removed in which case we close the Window.
        /**
         *   implemented here to satisfy the TreeModelListener interface; not used.
         * @param e
         */
        public void treeNodesChanged( TreeModelEvent e ) {
        }


        /**
         *   implemented here to satisfy the TreeModelListener interface; not used.
         * @param e
         */
        public void treeNodesInserted( TreeModelEvent e ) {
        }


        /**
         *  The TreeModelListener interface tells us of tree node removal events. We use this
         *  here to determine if the node being displayed is the one removed or whether it is a child
         *  of the removed nodes. If so we close the window.
         * @param e
         */
        public void treeNodesRemoved( TreeModelEvent e ) {
            if ( SortableDefaultMutableTreeNode.wasNodeDeleted( myNode, e ) ) {
                getRid();


            }
        }


        /**
         *   implemented here to satisfy the TreeModelListener interface; not used.
         * @param e
         */
        public void treeStructureChanged( TreeModelEvent e ) {
        }
    };

    private class MySpinnerNumberModel
            extends SpinnerNumberModel {

        public MySpinnerNumberModel() {
            super( 0.0, //initial value
                    0.0, //min
                    359.9, //max
                    .1f );                //step
        }


        @Override
        public Object getNextValue() {
            Object o = super.getNextValue();
            if ( o == null ) {
                o = 0.0;
            }
            return o;
        }


        @Override
        public Object getPreviousValue() {
            Object o = super.getPreviousValue();
            if ( o == null ) {
                o = 359.9;
            }
            return o;
        }
    }
}
