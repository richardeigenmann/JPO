package jpo.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import jpo.dataModel.ArrayListNavigator;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.miginfocom.swing.MigLayout;

/*
 EmailerGui.java:  creates a GUI to allow the user to specify his search

 Copyright (C) 2004-2014  Richard Eigenmann.
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
 * EmailerGui.java: Creates a GUI to edit the categories of the collection
 *
 *
 */
public class EmailerGui extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( EmailerGui.class.getName() );

    /**
     * Internal array that holds the nodes to be send by email.
     */
    private final List<SortableDefaultMutableTreeNode> emailSelected;

    /**
     * Creates a GUI to send the selected pictures as an email
     *
     *
     */
    public EmailerGui() {
        emailSelected = Settings.pictureCollection.getMailSelectedNodes();

        // if no picutres have been selected pop up an error message
        if ( emailSelected.size() < 1 ) {
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "emailNoNodes" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }


        if ( Settings.emailServer.equals( "" ) ) { //perhaps make this a better test of the server
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "emailNoServer" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            //getRid();
            return;
        }

        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );

        initComponents();

        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );

        getSettings();
    }

    /**
     * Panel showing the images to be emailed.
     */
    private final JPanel imagesJPanel = new JPanel();

    /**
     * Field for the Sender address
     */
    private final JComboBox<String> fromJComboBox = new JComboBox<>();

    /**
     * Field for the Recipient's address
     */
    private final JComboBox<String> toJComboBox = new JComboBox<>();

    /**
     * Field for the Subject of the mail
     */
    private final JTextField subjectJTextField = new JTextField( 200 );

    /**
     * Box for an optional message
     */
    private final JTextArea messageJTextArea = new JTextArea();

    /**
     * tickbox that indicates whether to send the originals
     */
    private final JCheckBox scalePicturesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "emailResizeJLabel" ) );

    /**
     * Maximum width for emailed images
     */
    private final WholeNumberField imageWidthWholeNumberField = new WholeNumberField( 0, 6 );

    /**
     * Maximum height for emailed images
     */
    private final WholeNumberField imageHeightWholeNumberField = new WholeNumberField( 0, 6 );

    /**
     * tickbox that indicates whether to send the originals
     */
    private JCheckBox sendOriginalsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "emailOriginals" ) );

    /**
     * The Jscrollpane to show the images panel
     */
    private JScrollPane imagesJScrollPane = new JScrollPane( imagesJPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );

    private void initComponents() {
        setTitle( Settings.jpoResources.getString( "EmailerJFrame" ) );
        final JPanel jPanel = new JPanel();
        jPanel.setLayout( new MigLayout() );

        final JLabel imagesCountJLabel
                = new JLabel( Settings.jpoResources.getString( "imagesCountJLabel" ) + Integer.toString( emailSelected.size() ) );
        jPanel.add( imagesCountJLabel, "spanx 2, wrap" );


        imagesJPanel.setLayout( new GridBagLayout() );
        imagesJScrollPane.setMinimumSize( new Dimension( 300, 165 ) );
        imagesJScrollPane.setPreferredSize( new Dimension( 500, 165 ) );

        jPanel.add( imagesJScrollPane, "spanx 2, wrap" );

        jPanel.add( new JLabel( Settings.jpoResources.getString( "fromJLabel" ) ), "" );

        fromJComboBox.setEditable( true );
        fromJComboBox.setPreferredSize( Settings.filenameFieldPreferredSize );
        fromJComboBox.setMinimumSize( Settings.filenameFieldMinimumSize );
        fromJComboBox.setMaximumSize( Settings.filenameFieldMaximumSize );
        jPanel.add( fromJComboBox, "wrap" );

        jPanel.add( new JLabel( Settings.jpoResources.getString( "toJLabel" ) ), "" );

        toJComboBox.setEditable( true );
        toJComboBox.setPreferredSize( Settings.filenameFieldPreferredSize );
        toJComboBox.setMinimumSize( Settings.filenameFieldMinimumSize );
        toJComboBox.setMaximumSize( Settings.filenameFieldMaximumSize );
        jPanel.add( toJComboBox, "wrap" );

        jPanel.add( new JLabel( Settings.jpoResources.getString( "subjectJLabel" ) ), "" );

        subjectJTextField.setPreferredSize( Settings.filenameFieldPreferredSize );
        subjectJTextField.setMinimumSize( Settings.filenameFieldMinimumSize );
        subjectJTextField.setMaximumSize( Settings.filenameFieldMaximumSize );
        jPanel.add( subjectJTextField, "wrap" );

        jPanel.add( new JLabel( Settings.jpoResources.getString( "messageJLabel" ) ), "" );

        //messageJTextArea.setRows( 12 );
        JScrollPane messageJScrollPane = new JScrollPane( messageJTextArea );
        messageJScrollPane.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) );
        messageJScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        messageJScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        messageJTextArea.setWrapStyleWord( true );
        messageJTextArea.setLineWrap( true );
        messageJTextArea.setEditable( true );
        messageJTextArea.setBorder( BorderFactory.createEmptyBorder( 2, 2, 8, 2 ) );
        messageJScrollPane.setPreferredSize( new Dimension( 550, 200 ) );
        messageJScrollPane.setMinimumSize( new Dimension( 300, 150 ) );
        messageJScrollPane.setMaximumSize( new Dimension( 800, 500 ) );
        jPanel.add( messageJScrollPane, "wrap" );

        jPanel.add( new JLabel( Settings.jpoResources.getString( "emailSizesJLabel" ) ), "" );

        JComboBox<String> sizesJComboBox = new JComboBox<String>();
        sizesJComboBox.addItem( Settings.jpoResources.getString( "emailSize1" ) );
        sizesJComboBox.addItem( Settings.jpoResources.getString( "emailSize2" ) );
        sizesJComboBox.addItem( Settings.jpoResources.getString( "emailSize3" ) );
        sizesJComboBox.addItem( Settings.jpoResources.getString( "emailSize4" ) );
        sizesJComboBox.addItem( Settings.jpoResources.getString( "emailSize5" ) );
        sizesJComboBox.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JComboBox cb = (JComboBox) e.getSource();
                String cbSelection = (String) cb.getSelectedItem();
                if ( cbSelection.equals( Settings.jpoResources.getString( "emailSize1" ) ) ) {
                    imageWidthWholeNumberField.setText( "350" );
                    imageHeightWholeNumberField.setText( "300" );
                    scalePicturesJCheckBox.setSelected( true );
                    sendOriginalsJCheckBox.setSelected( false );
                } else if ( cbSelection.equals( Settings.jpoResources.getString( "emailSize2" ) ) ) {
                    imageWidthWholeNumberField.setText( "700" );
                    imageHeightWholeNumberField.setText( "550" );
                    scalePicturesJCheckBox.setSelected( true );
                    sendOriginalsJCheckBox.setSelected( false );
                } else if ( cbSelection.equals( Settings.jpoResources.getString( "emailSize3" ) ) ) {
                    imageWidthWholeNumberField.setText( "700" );
                    imageHeightWholeNumberField.setText( "550" );
                    sendOriginalsJCheckBox.setSelected( true );
                    scalePicturesJCheckBox.setSelected( true );
                } else if ( cbSelection.equals( Settings.jpoResources.getString( "emailSize4" ) ) ) {
                    imageWidthWholeNumberField.setText( "1000" );
                    imageHeightWholeNumberField.setText( "800" );
                    scalePicturesJCheckBox.setSelected( true );
                    sendOriginalsJCheckBox.setSelected( false );
                } else if ( cbSelection.equals( Settings.jpoResources.getString( "emailSize5" ) ) ) {
                    imageWidthWholeNumberField.setText( "0" );
                    imageHeightWholeNumberField.setText( "0" );
                    scalePicturesJCheckBox.setSelected( false );
                    sendOriginalsJCheckBox.setSelected( true );
                }
            }
        } );
        sizesJComboBox.setPreferredSize( Settings.filenameFieldPreferredSize );
        sizesJComboBox.setMinimumSize( Settings.filenameFieldMinimumSize );
        sizesJComboBox.setMaximumSize( Settings.filenameFieldMaximumSize );
        jPanel.add( sizesJComboBox, "wrap" );

        JPanel scaleSizeJPanel = new JPanel();
        FlowLayout fl = new FlowLayout();
        fl.setAlignment( FlowLayout.LEADING );
        fl.setHgap( 0 );
        scaleSizeJPanel.setLayout( fl );
        scaleSizeJPanel.setPreferredSize( new Dimension( 550, 25 ) );
        scaleSizeJPanel.setMinimumSize( new Dimension( 450, 25 ) );
        scaleSizeJPanel.setMaximumSize( new Dimension( 1000, 25 ) );
        //scaleSizeJPanel.setInsets( new Insets(0,0,0,0) );

        scalePicturesJCheckBox.addItemListener( new ItemListener() {

            @Override
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.DESELECTED ) {
                    imageWidthWholeNumberField.setEnabled( false );
                    imageHeightWholeNumberField.setEnabled( false );
                } else if ( e.getStateChange() == ItemEvent.SELECTED ) {
                    imageWidthWholeNumberField.setEnabled( true );
                    imageHeightWholeNumberField.setEnabled( true );
                }
            }
        } );
        //scalePicturesJCheckBox.setInsets( new Insets(0,0,0,0) );
        scaleSizeJPanel.add( scalePicturesJCheckBox );

        imageWidthWholeNumberField.setPreferredSize( Settings.shortNumberPreferredSize );
        imageWidthWholeNumberField.setMinimumSize( Settings.shortNumberMinimumSize );
        imageWidthWholeNumberField.setMaximumSize( Settings.shortNumberMaximumSize );
        imageWidthWholeNumberField.setVisible( true );
        scaleSizeJPanel.add( imageWidthWholeNumberField );

        scaleSizeJPanel.add( new JLabel( " x " ) );

        imageHeightWholeNumberField.setPreferredSize( Settings.shortNumberPreferredSize );
        imageHeightWholeNumberField.setMinimumSize( Settings.shortNumberMinimumSize );
        imageHeightWholeNumberField.setMaximumSize( Settings.shortNumberMaximumSize );
        imageHeightWholeNumberField.setVisible( true );
        scaleSizeJPanel.add( imageHeightWholeNumberField );

        jPanel.add( scaleSizeJPanel, "spanx 2, wrap" );

        jPanel.add( sendOriginalsJCheckBox, "spanx 2, wrap" );

        final JPanel buttonJPanel = new JPanel();
        final JButton emailJButton = new JButton( Settings.jpoResources.getString( "emailJButton" ) );
        emailJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent evt ) {
                prepareSend();
                getRid();
            }
        } );
        buttonJPanel.add( emailJButton );

        final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent evt ) {
                getRid();
            }
        } );
        buttonJPanel.add( cancelJButton );

        jPanel.add( buttonJPanel, "spanx 2, wrap" );

        jPanel.setPreferredSize( new Dimension( 600, 600 ) );
        jPanel.setMaximumSize( new Dimension( 600, 600 ) );
        jPanel.setMinimumSize( new Dimension( 600, 600 ) );
        
        getContentPane().add( jPanel );
    }

    /**
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * Populates the comboboxes with values that came from the Settings.
     */
    private void getSettings() {
        Iterator i = Settings.emailSenders.iterator();
        while ( i.hasNext() ) {
            fromJComboBox.addItem( (String) i.next() );
        }

        i = Settings.emailRecipients.iterator();
        while ( i.hasNext() ) {
            toJComboBox.addItem( (String) i.next() );
        }

        scalePicturesJCheckBox.setSelected( Settings.emailScaleImages );
        sendOriginalsJCheckBox.setSelected( Settings.emailSendOriginal );
        imageWidthWholeNumberField.setValue( Settings.emailDimensions.width );
        imageHeightWholeNumberField.setValue( Settings.emailDimensions.height );

        loadThumbnails();
    }

    /**
     * makes sure that the new addresses (if any) are recorded in the TreeSets
     * of the Settings.
     */
    private void putSettings() {
        Settings.emailSenders.add( fromJComboBox.getSelectedItem().toString() );
        Settings.emailRecipients.add( toJComboBox.getSelectedItem().toString() );
        Settings.emailScaleImages = scalePicturesJCheckBox.isSelected();
        Settings.emailSendOriginal = sendOriginalsJCheckBox.isSelected();
        Settings.emailDimensions = new Dimension( imageWidthWholeNumberField.getValue(), imageHeightWholeNumberField.getValue() );
    }

    /**
     * loads the thumbnails into the preview panel
     */
    private void loadThumbnails() {
        int thumbnailSize = Settings.thumbnailSize;
        int desiredSize = 140;
        float factor = (float) desiredSize / (float) thumbnailSize;
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets( 4, 4, 4, 4 );
        c.gridy = 0;
        ArrayListNavigator arrayListNavigator = new ArrayListNavigator();

        for ( int i = 0; i < emailSelected.size(); i++ ) {
            //logger.info("EmailerGui.loadThumbnails: running on " + emailSelected[i].toString() );
            arrayListNavigator.addNode( (SortableDefaultMutableTreeNode) emailSelected.get( i ) );
            ThumbnailController thumbnailController = new ThumbnailController( thumbnailSize );
            thumbnailController.setNode( arrayListNavigator, i );
            thumbnailController.setDecorateThumbnails( false );
            thumbnailController.determineMailSlectionStatus();
            thumbnailController.setFactor( factor );
            c.gridx = i;
            //logger.info( c.toString() + " x= " + Integer.toString( c.gridx ) + " y= " + Integer.toString( c.gridy ) );
            imagesJPanel.add( thumbnailController.getThumbnail(), c );
            thumbnailController.getThumbnail().setVisible( true );
        }
        imagesJPanel.revalidate();
    }

    /**
     * method that analyses the GUI fields and prepares stuff for sending
     */
    private void prepareSend() {
        if ( emailSelected.size() < 1 ) {
            JOptionPane.showMessageDialog( this,
                    Settings.jpoResources.getString( "noNodesSelected" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        InternetAddress senderAddress;
        try {
            senderAddress = new InternetAddress( fromJComboBox.getSelectedItem().toString() );
        } catch ( AddressException x ) {
            JOptionPane.showMessageDialog( this,
                    x.getLocalizedMessage(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        InternetAddress destinationAddress;
        try {
            destinationAddress = new InternetAddress( toJComboBox.getSelectedItem().toString() );
        } catch ( AddressException x ) {
            JOptionPane.showMessageDialog( this,
                    x.getLocalizedMessage(),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        boolean scaleImages = scalePicturesJCheckBox.isSelected();
        boolean sendOriginal = sendOriginalsJCheckBox.isSelected();

        Dimension scaleSize = new Dimension( imageWidthWholeNumberField.getValue(), imageWidthWholeNumberField.getValue() );

        putSettings(); // placed here so that we don't store addresses that fail in the AddressExceptions
        Emailer et = new Emailer( emailSelected, senderAddress, destinationAddress, subjectJTextField.getText(), messageJTextArea.getText(), scaleImages, scaleSize, sendOriginal );
    }
}
