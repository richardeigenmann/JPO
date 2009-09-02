package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.PictureInfo;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import java.util.logging.Logger;

/*
ThumbnailDescriptionJPanel.java:  class that creates a panel showing the details of a thumbnail

Copyright (C) 2002 - 2009  Richard Eigenmann, Zürich, Switzerland
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
 *   ThumbnailDescriptionJPanel is a JPanel that displays the metadata of a thumbnail.
 *   It knows the node it is representing.
 *   It can be told to change the node it is showing.
 *   It can be mute.
 *   It knows it's x and y position.
 */
public class ThumbnailDescriptionJPanel
        extends JPanel
        implements PictureInfoChangeListener,
        TreeModelListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ThumbnailDescriptionJPanel.class.getName() );

    /**
     *  a link to the SortableDefaultMutableTreeNode in the data model.
     *  This allows thumbnails to be selected by sending a
     *  nodeSelected event to the data model.
     **/
    protected SortableDefaultMutableTreeNode referringNode;

    /**
     *  The GridBagConstrains for this ThumbnailDescriptionJPanel which help to
     *  position it in the panel
     */
    //protected GridBagConstraints c = new GridBagConstraints();
    /**
     *  This object holds the description
     */
    private JTextArea pictureDescriptionJTA = new JTextArea();


    ;

    /**
     *   This JScrollPane holds the JTextArea pictureDescriptionJTA so that it can have
     *   multiple lines of text if this is required.
     */
    private JScrollPane pictureDescriptionJSP = new JScrollPane( pictureDescriptionJTA,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

    /**
     *   The location of the image file
     */
    private JTextField highresLocationJTextField = new JTextField();

    /**
     *   The location of the lowres image file
     */
    private JTextField lowresLocationJTextField = new JTextField();

    /**
     * create a dumbCaret object which prevents undesirable scrolling behaviour
     *
     * @see NonFocussedCaret
     */
    private NonFocussedCaret dumbCaret = new NonFocussedCaret();

    /**
     *   Constant that indicates that the description should be formatted as
     *   a large description meaning large font and just the image description
     */
    public static final int LARGE_DESCRIPTION = 1;

    /**
     *   Constant that indicates that the descriptions should be formatted as
     *   a small info panel meaning small font and much information
     */
    public static final int MINI_INFO = LARGE_DESCRIPTION + 1;

    /**
     *  Font to be used for Large Texts:
     */
    private static Font largeFont = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelLargeFont" ) );

    /**
     *  Font to be used for small texts:
     */
    private static Font smallFont = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelSmallFont" ) );

    /**
     *   This field controls how the description panel is shown. It can be set to
     *   ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     *   ThumbnailDescriptionJPanel.MINI_INFO,
     */
    //private int displayMode = MINI_INFO;
    private int displayMode = LARGE_DESCRIPTION;

    /**
     *   The factor which is multiplied with the ThumbnailDescription to determine how large it is shown.
     */
    private float thumbnailSizeFactor = 1;


    /**
     *   Construct a new ThumbnailDescrciptionJPanel
     *
     **/
    public ThumbnailDescriptionJPanel() {
        // attach this panel to the tree model so that it is notified about changes
        Settings.pictureCollection.getTreeModel().addTreeModelListener( this );

        setBackground( Color.WHITE );
        //setBorder( BorderFactory.createLineBorder( Color.RED ) );


        //pictureDescriptionJTA.setFont( Settings.captionFont );
        pictureDescriptionJTA.setWrapStyleWord( true );
        pictureDescriptionJTA.setLineWrap( true );
        pictureDescriptionJTA.setEditable( true );
        pictureDescriptionJTA.setCaret( dumbCaret );

        // it is the Scrollpane you must constrain, not the TextArea
        pictureDescriptionJSP.setMinimumSize( new Dimension( Settings.thumbnailSize, 25 ) );
        pictureDescriptionJSP.setMaximumSize( new Dimension( Settings.thumbnailSize, 250 ) );

        //pictureDescriptionJTA.setAlignmentX( Component.CENTER_ALIGNMENT );
        pictureDescriptionJTA.setInputVerifier( new InputVerifier() {

            public boolean verify( JComponent component ) {
                // doUpdate();
                return true;
            }


            @Override
            public boolean shouldYieldFocus( JComponent component ) {
                doUpdate();
                return true;
            }
        } );

        pictureDescriptionJTA.getDocument().addDocumentListener( new DocumentListener() {

            public void insertUpdate( DocumentEvent e ) {
                setTextAreaSize();
            }


            public void removeUpdate( DocumentEvent e ) {
                setTextAreaSize();
            }


            public void changedUpdate( DocumentEvent e ) {
                setTextAreaSize();
            }
        } );

        pictureDescriptionJSP.getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener() {

            public void adjustmentValueChanged( AdjustmentEvent e ) {
                setTextAreaSize();
            }
        } );

        setVisible( false );


        //c.fill = c.BOTH;
        //c.anchor = c.NORTH;


//		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        //add ( pictureDescriptionJTA, BorderLayout.NORTH);
        //pictureDescriptionJSP.setBorder( BorderFactory.createEmptyBorder(0,0,0,0) );
        //pictureDescriptionJSP.setBorder( BorderFactory.createLineBorder( Color.GREEN ) );
        add( pictureDescriptionJSP );



        /*highresLocationJTextField.setMinimumSize( new Dimension( (int) ( Settings.thumbnailSize * thumbnailSizeFactor ), 20) );
        highresLocationJTextField.setMaximumSize( new Dimension( (int) ( Settings.thumbnailSize * thumbnailSizeFactor ), 20) );
        highresLocationJTextField.setPreferredSize( new Dimension( (int) ( Settings.thumbnailSize * thumbnailSizeFactor ), 20) );
        //		add ( highresLocationJTextField );


        lowresLocationJTextField.setMinimumSize( new Dimension( (int) ( Settings.thumbnailSize * thumbnailSizeFactor ), 20) );
        lowresLocationJTextField.setMaximumSize( new Dimension( (int) ( Settings.thumbnailSize * thumbnailSizeFactor ), 20) );
        lowresLocationJTextField.setPreferredSize( new Dimension( (int) ( Settings.thumbnailSize * thumbnailSizeFactor ), 20) );
        //		add ( lowresLocationJTextField );*/

    }


    /**
     *    doUpdate writes the changed text back to the data model and submits an nodeChanged
     *    notification on the model. It get's called by the Inputverifier on the text area.
     */
    public void doUpdate() {
        if ( referringNode == null ) {
            return;
        }
        if ( !pictureDescriptionJTA.getText().equals( referringNode.getUserObject().toString() ) ) {
            if ( referringNode.getUserObject() instanceof PictureInfo ) {
                ( (PictureInfo) referringNode.getUserObject() ).setDescription( pictureDescriptionJTA.getText() );
            } else if ( referringNode.getUserObject() instanceof GroupInfo ) {
                ( (GroupInfo) referringNode.getUserObject() ).setGroupName( pictureDescriptionJTA.getText() );
            }
        }
        //Todo: The GroupInfo should be sending the notifications around
        //Settings.pictureCollection.getTreeModel().nodeChanged( referringNode );
    }


    /**
     *  This method sets the node which the ThumbnailDescriptionJPanel should display. If it should
     *  display nothing then set it to null.
     *
     *  @param referringNode  The Node to be displayed
     */
    public void setNode( SortableDefaultMutableTreeNode referringNode ) {
        if ( this.referringNode == referringNode ) {
            // Don't refresh the node if it hasn't changed
            return;
        }

        // flush any uncommitted changes
        doUpdate();

        // unattach the change Listener
        if ( ( this.referringNode != null ) && ( this.referringNode.getUserObject() instanceof PictureInfo ) ) {
            PictureInfo pi = (PictureInfo) this.referringNode.getUserObject();
            pi.removePictureInfoChangeListener( this );
        }


        this.referringNode = referringNode;

        // attach the change Listener
        if ( ( referringNode != null ) && ( referringNode.getUserObject() instanceof PictureInfo ) ) {
            PictureInfo pi = (PictureInfo) referringNode.getUserObject();
            pi.addPictureInfoChangeListener( this );
        }


        String legend;
        if ( referringNode == null ) {
            legend = Settings.jpoResources.getString( "ThumbnailDescriptionNoNodeError" );
            setVisible( false );
        } else if ( referringNode.getUserObject() instanceof PictureInfo ) {
            PictureInfo pi = (PictureInfo) referringNode.getUserObject();
            legend = pi.getDescription();
            highresLocationJTextField.setText( pi.getHighresLocation() );
            lowresLocationJTextField.setText( pi.getLowresLocation() );
            setVisible( true );
        } else {
            // GroupInfo
            legend = ( (GroupInfo) referringNode.getUserObject() ).getGroupName();
            highresLocationJTextField.setText( "" );
            lowresLocationJTextField.setText( "" );
            setVisible( true );
        }
        pictureDescriptionJTA.setText( legend );

        formatDescription();
    }


    /**
     *   This method how the description panel is shown. It can be set to
     *   ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     *   ThumbnailDescriptionJPanel.MINI_INFO,
     * @param displayMode
     */
    public void setDisplayMode( int displayMode ) {
        this.displayMode = displayMode;
    }


    /**
     *  This method formats the text information fields for the indicated node.
     */
    public void formatDescription() {
        if ( displayMode == LARGE_DESCRIPTION ) {
            pictureDescriptionJTA.setFont( largeFont );
        } else {
            // i.e.  MINI_INFO
            pictureDescriptionJTA.setFont( smallFont );
        }
        setTextAreaSize();

        if ( ( referringNode != null ) && ( referringNode.getUserObject() instanceof PictureInfo ) && ( displayMode == MINI_INFO ) ) {
            highresLocationJTextField.setVisible( true );
            lowresLocationJTextField.setVisible( true );
        } else {
            highresLocationJTextField.setVisible( false );
            lowresLocationJTextField.setVisible( false );
        }

    }


    /**
     *  sets the size of the TextArea
     */
    public void setTextAreaSize() {
        Dimension textAreaSize = pictureDescriptionJTA.getPreferredSize();

        int targetHeight = 0;
        if ( textAreaSize.height < pictureDescriptionJSP.getMinimumSize().height ) {
            targetHeight = pictureDescriptionJSP.getMinimumSize().height;
        } else if ( textAreaSize.height > pictureDescriptionJSP.getMaximumSize().height ) {
            targetHeight = pictureDescriptionJSP.getMaximumSize().height;
        } else {
            targetHeight = ( ( ( textAreaSize.height / 30 ) + 1 ) * 30 );
        }

        Dimension scrollPaneSize = pictureDescriptionJSP.getPreferredSize();
        int targetWidth = (int) ( Settings.thumbnailSize * thumbnailSizeFactor );
        if ( ( targetHeight != scrollPaneSize.height ) || ( targetWidth != scrollPaneSize.width ) ) {
            pictureDescriptionJSP.setPreferredSize( new Dimension( targetWidth, targetHeight ) );
            logger.fine( "ThumbnailDescriptionJPanel.setTextAreaSize set to: " + Integer.toString( targetWidth ) + " / " + Integer.toString( targetHeight ) );
        }
        //pictureDescriptionJSP.getParent().validate();
    }


    /**
     *   Overridden method to allow the better tuning of visibility
     * @param visibility
     */
    @Override
    public void setVisible( boolean visibility ) {
        super.setVisible( visibility );
        pictureDescriptionJTA.setVisible( visibility );
        pictureDescriptionJSP.setVisible( visibility );
        //validate();
    }


    /**
     *   Returns the preferred size for the ThumbnailDescription as a Dimension using the thumbnailSize
     *   as width and height.
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        int height = 0;
        if ( isVisible() ) {
            height = d.height;
        }
        return new Dimension( d.width, height );
    }


    /**
     *  This method sets the scaling factor for the display of a thumbnail description
     *
     * @param thumbnailSizeFactor
     */
    public void setFactor( float thumbnailSizeFactor ) {
        this.thumbnailSizeFactor = thumbnailSizeFactor;
        setTextAreaSize();
        //setVisible( isVisible() );
    }


    /**
     *  returns the current node
     *
     * @return
     */
    public SortableDefaultMutableTreeNode getNode() {
        return referringNode;
    }


    /**
     *  here we get notified by the PictureInfo object that something has
     *  changed.
     */
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        if ( e.getDescriptionChanged() ) {
            pictureDescriptionJTA.setText( e.getPictureInfo().getDescription() );
        }
        if ( e.getHighresLocationChanged() ) {
            highresLocationJTextField.setText( e.getPictureInfo().getHighresLocation() );
        }
        if ( e.getLowresLocationChanged() ) {
            lowresLocationJTextField.setText( e.getPictureInfo().getLowresLocation() );
        }
        /*		if ( e.getChecksumChanged() ) {
        checksumJLabel.setText( Settings.jpoResources.getString("checksumJLabel") + pi.getChecksumAsString () );
        }
        if ( e.getCreationTimeChanged() ) {
        creationTimeJTextField.setText( pi.getCreationTime () );
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
        } */

    }


    // Here we are not that interested in TreeModel change events other than to find out if our
    // current node was removed in which case we close the Window.
    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeNodesChanged( TreeModelEvent e ) {
        // find out whether our node was changed
        Object[] children = e.getChildren();
        if ( children == null ) {
            // the root node does not have children as it doesn't have a parent
            return;
        }

        for ( int i = 0; i < children.length; i++ ) {
            if ( children[i] == referringNode ) {
                // we are displaying a changed node. What changed?
                Object userObject = referringNode.getUserObject();
                if ( userObject instanceof GroupInfo ) {
                    String legend = ( (GroupInfo) userObject ).getGroupName();
                    if ( !legend.equals( pictureDescriptionJTA.getText() ) ) {
                        pictureDescriptionJTA.setText( legend );
                    }
                }
            }
        }
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeNodesInserted( TreeModelEvent e ) {
    }


    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     * @param e 
     */
    public void treeNodesRemoved( TreeModelEvent e ) {
    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     */
    public void treeStructureChanged( TreeModelEvent e ) {
    }
}
