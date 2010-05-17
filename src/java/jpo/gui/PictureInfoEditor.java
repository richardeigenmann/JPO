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
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Logger;
import jpo.dataModel.NodeNavigatorInterface;

/*
PictureInfoEditor:  Edits the description of an image

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
 *   class that creates a JFrame and displays the fields of a picture.
 *
 * @author 	Richard Eigenmann
 * @since       JDK1.4
 */
public class PictureInfoEditor
        extends JFrame
        implements ActionListener,
        TreeModelListener,
        PictureInfoChangeListener,
        ListSelectionListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureInfoEditor.class.getName() );

    /**
     *   ThumbnailController to show the image
     */
    private ThumbnailController thumbnailController;

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
     *   Button to change the location of the image file
     */
    private JButton highresLocationJButton = new JButton( Settings.jpoResources.getString( "threeDotText" ) );

    /**
     *   Button to calculate the checksum of the image file
     */
    private JButton checksumJButton = new JButton( Settings.jpoResources.getString( "checksumJButton" ) );

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
     *   The rotation to be applied to the image
     */
    private JFormattedTextField rotationJTextField;

    /**
     *   The location of the image file
     */
    private JTextField commentJTextField = new JTextField();

    /**
     *   The location of the image file
     */
    private JTextField photographerJTextField = new JTextField();

    /**
     *   The location of the image file
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
     *  The OK button
     */
    private JButton OkJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );

    /**
     *  The cancel button
     */
    private JButton CancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );

    /**
     *  The reset button
     */
    private JButton resetJButton = new JButton( Settings.jpoResources.getString( "resetLabel" ) );

    /**
     *  the node being edited
     */
    private SortableDefaultMutableTreeNode editNode;

    /**
     *  the PictureInfo obejct being displayed
     */
    private PictureInfo pi;

    /**
     *  Font used to show the error label
     */
    //private static Font errorLabelFont = new Font ( "Arial", Font.PLAIN, 10 );
    private static Font errorLabelFont = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelLargeFont" ) );

    /**
     *  Dimension for the edit fields
     */
    private static Dimension inputDimension = new Dimension( 400, 20 );


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
    @SuppressWarnings( "static-access" )
    public PictureInfoEditor( SortableDefaultMutableTreeNode editNode ) {
        super( Settings.jpoResources.getString( "PictureInfoEditorHeading" ) );

        // set this up so that we can close the GUI if the picture node is removed while we
        // are displaying it.
        editNode.getPictureCollection().getTreeModel().addTreeModelListener( this );


        if ( !( editNode.getUserObject() instanceof PictureInfo ) ) {
            logger.info( "PictureInfoEditor called on a non PictureInfo obejct! Rejected." );
            return;
        }

        pi = (PictureInfo) editNode.getUserObject();
        pi.addPictureInfoChangeListener( this );

        this.editNode = editNode;

        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets( 4, 4, 4, 4 );

        JPanel jPanel = new JPanel();
        jPanel.setLayout( new GridBagLayout() );



        JPanel leftJPanel = new JPanel();
        leftJPanel.setLayout( new GridBagLayout() );


        thumbnailController = new ThumbnailController( new SingleNodeNavigator( editNode ), 0, Settings.thumbnailSize, ThumbnailQueueRequest.MEDIUM_PRIORITY, null );
        c.gridy = 0;
        c.gridx = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.CENTER;
        leftJPanel.add( thumbnailController.getThumbnail(), c );


        JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new FlowLayout() );

        OkJButton.setPreferredSize( Settings.defaultButtonDimension );
        OkJButton.setMinimumSize( Settings.defaultButtonDimension );
        OkJButton.setMaximumSize( Settings.defaultButtonDimension );
        OkJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        OkJButton.addActionListener( this );
        OkJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( OkJButton );
        buttonJPanel.add( OkJButton );

        CancelButton.setPreferredSize( Settings.defaultButtonDimension );
        CancelButton.setMinimumSize( Settings.defaultButtonDimension );
        CancelButton.setMaximumSize( Settings.defaultButtonDimension );
        CancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        CancelButton.addActionListener( this );
        buttonJPanel.add( CancelButton );

        resetJButton.setPreferredSize( Settings.defaultButtonDimension );
        resetJButton.setMinimumSize( Settings.defaultButtonDimension );
        resetJButton.setMaximumSize( Settings.defaultButtonDimension );
        resetJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        resetJButton.addActionListener( this );
        buttonJPanel.add( resetJButton );

        c.gridy++;
        leftJPanel.add( buttonJPanel, c );



        // Add Exif panel
        JTextArea exifTagsJTextArea = new JTextArea(); // out here so we can use it in the catch stmt
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

        ExifInfo ei = new ExifInfo( ( (PictureInfo) editNode.getUserObject() ).getHighresURLOrNull() );
        ei.decodeExifTags();
        exifTagsJTextArea.append( Settings.jpoResources.getString( "ExifTitle" ) );
        exifTagsJTextArea.append( ei.getComprehensivePhotographicSummary() );
        exifTagsJTextArea.append( "-------------------------\nAll Tags:\n" );
        exifTagsJTextArea.append( ei.getAllTags() );


        c.gridy++;
        leftJPanel.add( exifJScrollPane, c );


        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        jPanel.add( leftJPanel, c );



        JPanel rightJPanel = new JPanel();
        rightJPanel.setLayout( new GridBagLayout() );




        c.gridx = 0;
        c.gridy = 0;

        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets( 0, 0, 0, 0 );
        JLabel descriptionJLabel = new JLabel( Settings.jpoResources.getString( "pictureDescriptionLabel" ) );
        rightJPanel.add( descriptionJLabel, c );

        descriptionJTextArea.setPreferredSize( new Dimension( 400, 150 ) );
        descriptionJTextArea.setWrapStyleWord( true );
        descriptionJTextArea.setLineWrap( true );
        descriptionJTextArea.setEditable( true );
        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( descriptionJTextArea, c );


        JLabel creationTimeJLabel = new JLabel( Settings.jpoResources.getString( "creationTimeLabel" ) );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( creationTimeJLabel, c );

        creationTimeJTextField.setPreferredSize( inputDimension );
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
        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( creationTimeJTextField, c );

        c.gridx = 0;
        c.gridy++;
        parsedCreationTimeJLabel.setFont( errorLabelFont );
        rightJPanel.add( parsedCreationTimeJLabel, c );

        JPanel highresJPanel = new JPanel();

        JLabel highresLocationJLabel = new JLabel( Settings.jpoResources.getString( "highresLocationLabel" ) );

        highresJPanel.add( highresLocationJLabel );
        highresJPanel.add( highresErrorJLabel );
        highresErrorJLabel.setFont( errorLabelFont );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( highresJPanel, c );

        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        highresLocationJTextField.setPreferredSize( inputDimension );
        rightJPanel.add( highresLocationJTextField, c );

        c.gridx++;
        highresLocationJButton.setPreferredSize( Settings.threeDotButtonDimension );
        highresLocationJButton.setMinimumSize( Settings.threeDotButtonDimension );
        highresLocationJButton.setMaximumSize( Settings.threeDotButtonDimension );
        highresLocationJButton.addActionListener( this );
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( highresLocationJButton, c );


        // Checksum
        checksumJButton.setPreferredSize( new Dimension( 80, 25 ) );
        checksumJButton.setMinimumSize( new Dimension( 80, 25 ) );
        checksumJButton.setMaximumSize( new Dimension( 80, 25 ) );
        checksumJButton.addActionListener( this );

        JPanel checksumJPanel = new JPanel();
        checksumJPanel.add( checksumJLabel );
        checksumJPanel.add( checksumJButton );
        c.gridy++;
        c.gridx = 0;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( checksumJPanel, c );



        JPanel lowresJPanel = new JPanel();

        JLabel lowresLocationJLabel = new JLabel( Settings.jpoResources.getString( "lowresLocationLabel" ) );
        lowresErrorJLabel.setFont( errorLabelFont );
        lowresJPanel.add( lowresLocationJLabel );
        lowresJPanel.add( lowresErrorJLabel );

        c.gridx = 0;
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( lowresJPanel, c );


        lowresLocationJTextField.setPreferredSize( inputDimension );
        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( lowresLocationJTextField, c );


        JLabel filmReferenceJLabel = new JLabel( Settings.jpoResources.getString( "filmReferenceLabel" ) );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( filmReferenceJLabel, c );

        filmReferenceJTextField.setPreferredSize( inputDimension );
        c.insets = new Insets( 0, 0, 0, 0 );
        c.gridy++;
        rightJPanel.add( filmReferenceJTextField, c );


        JLabel rotationJLabel = new JLabel( Settings.jpoResources.getString( "rotationLabel" ) );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( rotationJLabel, c );

        NumberFormat nf = new DecimalFormat( "###.##" );
        rotationJTextField = new JFormattedTextField( nf );
        rotationJTextField.setPreferredSize( new Dimension( 100, 20 ) );
        c.insets = new Insets( 0, 0, 0, 0 );
        c.gridy++;
        rightJPanel.add( rotationJTextField, c );




        JLabel commentJLabel = new JLabel( Settings.jpoResources.getString( "commentLabel" ) );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( commentJLabel, c );

        commentJTextField.setPreferredSize( inputDimension );
        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( commentJTextField, c );


        JLabel photographerJLabel = new JLabel( Settings.jpoResources.getString( "photographerLabel" ) );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( photographerJLabel, c );

        photographerJTextField.setPreferredSize( inputDimension );
        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( photographerJTextField, c );


        JLabel copyrightHolderJLabel = new JLabel( Settings.jpoResources.getString( "copyrightHolderLabel" ) );
        c.gridy++;
        c.insets = new Insets( 4, 0, 0, 0 );
        rightJPanel.add( copyrightHolderJLabel, c );

        copyrightHolderJTextField.setPreferredSize( inputDimension );
        c.gridy++;
        c.insets = new Insets( 0, 0, 0, 0 );
        rightJPanel.add( copyrightHolderJTextField, c );



        c.gridy++;
        JLabel categoriesJLabel = new JLabel( Settings.jpoResources.getString( "categoriesJLabel-2" ) );
        rightJPanel.add( categoriesJLabel, c );

        c.gridy++;
        rightJPanel.add( categoryAssignmentsJLabel, c );

        //categoriesJList.setPreferredSize( new Dimension( 130, 150) );
        //categoriesJList.setMinimumSize( new Dimension( 80, 50) );
        //categoriesJList.setMaximumSize( new Dimension( 1000, 500) );
        categoriesJList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
        categoriesJList.addListSelectionListener( this );

        //listJScrollPane.setPreferredSize( new Dimension( 150, 150) );
        //listJScrollPane.setMinimumSize( new Dimension( 100, 50) );

        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.6;
        c.weighty = 0.6;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets( 0, 0, 0, 0 );
        c.gridy++;
        rightJPanel.add( listJScrollPane, c );


        c.gridx = 1;
        c.gridy = 0;
        //c.anchor = GridBagConstraints.NORTHWEST;
        jPanel.add( rightJPanel, c );


        JScrollPane jScrollPane = new JScrollPane( jPanel );
        jScrollPane.setWheelScrollingEnabled( true );
        getContentPane().add( jScrollPane, BorderLayout.CENTER );

        loadData();
        setColorIfError();

        pack();
        setVisible( true );
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
        rotationJTextField.setText( Double.toString( pi.getRotation() ) );
        commentJTextField.setText( pi.getComment() );
        photographerJTextField.setText( pi.getPhotographer() );
        copyrightHolderJTextField.setText( pi.getCopyrightHolder() );

        listModel.removeAllElements();
        categoriesJList.clearSelection();
        listModel.addElement( setupCategories );
        listModel.addElement( noCategories );

        //TODO: chan the iterator and enumberation use generis and be written nicer?
        Iterator i = editNode.getPictureCollection().getCategoryIterator();
        Integer key;
        String category;
        Category categoryObject;
        Vector<Integer> selections = new Vector<Integer>();
        while ( i.hasNext() ) {
            key = (Integer) i.next();
            category = editNode.getPictureCollection().getCategory( key );
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
        //listJScrollPane.revalidate();
    }


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
            rotationJTextField.setText( Double.toString( pi.getRotation() ) );
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


    /**
     *  This utility method builds a string from the selected categories in a supplied JList
     * @param theList
     * @return
     */
    public static String selectedJListCategoriesToString( JList theList ) {
        String resultString = "";
        if ( !theList.isSelectionEmpty() ) {
            Object[] selectedCategories = theList.getSelectedValues();
            for ( int i = 0; i < selectedCategories.length; i++ ) {
                if ( ( selectedCategories[i] == setupCategories ) || ( selectedCategories[i] == noCategories ) ) {
                    // skip them
                } else if ( i == 0 ) {
                    resultString = selectedCategories[i].toString();
                } else {
                    resultString = resultString + ", " + selectedCategories[i].toString();
                }
            }
        }
        return resultString;
    }


    /**
     *  method that sets the URL fields to red if the file is not found and
     *  organge if the URL is not a valid URL
     */
    private void setColorIfError() {

        PictureInfo pi = (PictureInfo) editNode.getUserObject();

        try {
            URL pictureUrl = pi.getHighresURL();
            InputStream inputStream = pictureUrl.openStream();
            inputStream.close();
            highresLocationJTextField.setForeground( Color.black );
        } catch ( MalformedURLException x ) {
            logger.info( "MalformedURLException on " + pi.getHighresLocation() + x.getMessage() );
            highresErrorJLabel.setText( "(MalformedURLException " + x.getMessage() + ")" );
            highresLocationJTextField.setForeground( Color.orange );
        } catch ( IOException x ) {
            logger.info( "IOException on " + pi.getHighresLocation() + x.getMessage() );
            highresErrorJLabel.setText( "(IOException " + x.getMessage() + ")" );
            highresLocationJTextField.setForeground( Color.red );
        }


        try {
            URL pictureUrl = pi.getLowresURL();
            InputStream inputStream = pictureUrl.openStream();
            inputStream.close();
            lowresLocationJTextField.setForeground( Color.black );
        } catch ( MalformedURLException x ) {
            logger.info( "MalformedURLException on " + pi.getLowresLocation() + x.getMessage() );
            lowresErrorJLabel.setText( "(MalformedURLException  " + x.getMessage() + ")" );
            lowresLocationJTextField.setForeground( Color.orange );
        } catch ( IOException x ) {
            logger.info( "IOException on " + pi.getLowresLocation() + x.getMessage() );
            lowresErrorJLabel.setText( "(IOException " + x.getMessage() + ")" );
            lowresLocationJTextField.setForeground( Color.red );
        }

    }


    /**
     *  method that closes the window.
     */
    private void getRid() {
        if ( editNode.getPictureCollection().getTreeModel() != null ) {
            editNode.getPictureCollection().getTreeModel().removeTreeModelListener( this );
        }
        pi.removePictureInfoChangeListener( this );
        setVisible( false );
        dispose();
    }


    /**
     *  method that writes the changed data back to the PictureInfo object
     */
    private void okButtonEvent() {

        if ( pi.getRotation() != Double.parseDouble( rotationJTextField.getText() ) ) {
            pi.setRotation( Double.parseDouble( rotationJTextField.getText() ) );
            editNode.getPictureCollection().setUnsavedUpdates();
            //editNode.refreshThumbnail();
        }

        boolean nothingChanged =
                pi.getDescription().equals( descriptionJTextArea.getText() ) && pi.getCreationTime().equals( creationTimeJTextField.getText() ) && pi.getHighresLocation().equals( highresLocationJTextField.getText() ) && pi.getLowresLocation().equals( lowresLocationJTextField.getText() ) && pi.getComment().equals( commentJTextField.getText() ) && pi.getPhotographer().equals( photographerJTextField.getText() ) && pi.getFilmReference().equals( filmReferenceJTextField.getText() ) && pi.getCopyrightHolder().equals( copyrightHolderJTextField.getText() );

        if ( !nothingChanged ) {
            pi.setDescription( descriptionJTextArea.getText() );
            pi.setCreationTime( creationTimeJTextField.getText() );
            pi.setHighresLocation( highresLocationJTextField.getText() );
            pi.setLowresLocation( lowresLocationJTextField.getText() );
            pi.setComment( commentJTextField.getText() );
            pi.setPhotographer( photographerJTextField.getText() );
            pi.setFilmReference( filmReferenceJTextField.getText() );
            pi.setCopyrightHolder( copyrightHolderJTextField.getText() );

            editNode.getPictureCollection().sendNodeChanged( editNode );
            editNode.getPictureCollection().setUnsavedUpdates();
        }

        int[] indexes = categoriesJList.getSelectedIndices();
        Object o;
        pi.clearCategoryAssignments();
        for ( int i = 0; i < indexes.length; i++ ) {
            o = listModel.getElementAt( indexes[i] );
            if ( o instanceof Category ) {
                pi.addCategoryAssignment( ( (Category) o ).getKey() );
            }
        }


        getRid();
    }


    /**
     *  method that brings up a JFileChooser and places the path of the file selected into the
     *  JTextField of the highres locations
     */
    private void highresChooser() {
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
        if ( jFileChooser.getSelectedFile().exists() ) {
            highresLocationJTextField.setForeground( Color.black );
        } else {
            highresLocationJTextField.setForeground( Color.red );
        }

    }


    /**
     *  method that analyses the user initiated action and performs what the user reuqested
     *
     * @param e
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == OkJButton ) {
            okButtonEvent();
        } else if ( e.getSource() == CancelButton ) {
            getRid();
        } else if ( e.getSource() == resetJButton ) {
            loadData();
        } else if ( e.getSource() == highresLocationJButton ) {
            highresChooser();
        } else if ( e.getSource() == checksumJButton ) {
            pi.calculateChecksum();
        }


    }


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
        if ( SortableDefaultMutableTreeNode.wasNodeDeleted( editNode, e ) ) {
            getRid();
        }
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeStructureChanged( TreeModelEvent e ) {
    }
}
