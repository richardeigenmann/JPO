package jpo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import static jpo.gui.ThumbnailDescriptionJPanel.DescriptionSize.LARGE_DESCRIPTION;
import static jpo.gui.ThumbnailDescriptionJPanel.DescriptionSize.MINI_INFO;
import jpo.gui.swing.NonFocussedCaret;

/*
 ThumbnailDescriptionJPanel.java:  class that creates a panel showing the description of a thumbnail

 Copyright (C) 2002 - 2017  Richard Eigenmann, Zürich, Switzerland
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
 * ThumbnailDescriptionJPanel is a JPanel that displays the metadata of a
 * thumbnail. It knows the node it is representing. It can be told to change the
 * node it is showing. It can be mute. It knows it's x and y position in the
 * grid
 */
public class ThumbnailDescriptionJPanel
        extends JPanel
        implements PictureInfoChangeListener,
        TreeModelListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailDescriptionJPanel.class.getName() );

    /**
     * a link to the SortableDefaultMutableTreeNode in the data model. This
     * allows thumbnails to be selected by sending a nodeSelected event to the
     * data model.
     *
     */
    protected SortableDefaultMutableTreeNode referringNode;

    /**
     * This object holds the description
     */
    private final JTextArea pictureDescriptionJTA = new JTextArea();

    /**
     * This JScrollPane holds the JTextArea pictureDescriptionJTA so that it can
     * have multiple lines of text if this is required.
     */
    private final JScrollPane pictureDescriptionJSP = new JScrollPane( pictureDescriptionJTA,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

    /**
     * The location of the image file
     */
    private final JTextField highresLocationJTextField = new JTextField();

    /**
     * create a dumbCaret object which prevents undesirable scrolling behaviour
     *
     * @see NonFocussedCaret
     */
    private final NonFocussedCaret dumbCaret = new NonFocussedCaret();

    /**
     * choices for the Description size
     */
    public static enum DescriptionSize {

        /**
         * Descriptions should be in a large font
         */
        LARGE_DESCRIPTION,
        /**
         * Descriptions should be in a small font
         */
        MINI_INFO
    };

    /**
     * This field controls how the description panel is shown. It can be set to
     * ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     * ThumbnailDescriptionJPanel.MINI_INFO,
     */
    private DescriptionSize displayMode = LARGE_DESCRIPTION;

    /**
     * Font to be used for Large Texts:
     */
    private static final Font LARGE_FONT = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelLargeFont" ) );

    /**
     * Font to be used for small texts:
     */
    private static final Font SMALL_FONT = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelSmallFont" ) );

    /**
     * The factor which is multiplied with the ThumbnailDescription to determine
     * how large it is shown.
     */
    private float thumbnailSizeFactor = 1;

    /**
     * Construct a new ThumbnailDescrciptionJPanel
     *
     *
     */
    public ThumbnailDescriptionJPanel() {
        initComponents();
    }

    private void initComponents() {
        // attach this panel to the tree model so that it is notified about changes
        Settings.getPictureCollection().getTreeModel().addTreeModelListener( this );

        setBackground( Color.WHITE );

        pictureDescriptionJTA.setWrapStyleWord( true );
        pictureDescriptionJTA.setLineWrap( true );
        pictureDescriptionJTA.setEditable( true );
        pictureDescriptionJTA.setCaret( dumbCaret );

        // it is the Scrollpane you must constrain, not the TextArea
        pictureDescriptionJSP.setMinimumSize( new Dimension( Settings.thumbnailSize, 25 ) );
        pictureDescriptionJSP.setMaximumSize( new Dimension( Settings.thumbnailSize, 250 ) );

        pictureDescriptionJTA.setInputVerifier( new InputVerifier() {

            @Override
            public boolean verify( JComponent component ) {
                return true;
            }

            @Override
            public boolean shouldYieldFocus( JComponent component ) {
                doUpdate();
                return true;
            }
        } );

        pictureDescriptionJTA.getDocument().addDocumentListener( new DocumentListener() {

            @Override
            public void insertUpdate( DocumentEvent e ) {
                setTextAreaSize();
            }

            @Override
            public void removeUpdate( DocumentEvent e ) {
                setTextAreaSize();
            }

            @Override
            public void changedUpdate( DocumentEvent e ) {
                setTextAreaSize();
            }
        } );

        pictureDescriptionJSP.getVerticalScrollBar().addAdjustmentListener(( AdjustmentEvent e ) -> {
            setTextAreaSize();
        });

        setVisible( false );
        add( pictureDescriptionJSP );
    }

    /**
     * doUpdate writes the changed text back to the data model and submits an
     * nodeChanged notification on the model. It gets called by the
     * Inputverifier on the text area.
     */
    public void doUpdate() {
        if ( referringNode == null ) {
            return;
        }
        Object userObject = referringNode.getUserObject();
        if ( !pictureDescriptionJTA.getText().equals( userObject.toString() ) ) {
            // the description was changed
            if ( userObject instanceof PictureInfo ) {
                ( (PictureInfo) referringNode.getUserObject() ).setDescription( pictureDescriptionJTA.getText() );
            } else if ( userObject instanceof GroupInfo ) {
                ( (GroupInfo) referringNode.getUserObject() ).setGroupName( pictureDescriptionJTA.getText() );
            }
        }
    }

    /**
     * This method sets the node which the ThumbnailDescriptionJPanel should
     * display. If it should display nothing then set it to null.
     *
     * @param referringNode The Node to be displayed
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
            PictureInfo pictureInfo = (PictureInfo) referringNode.getUserObject();
            pictureInfo.addPictureInfoChangeListener( this );
        }

        String legend;
        if ( referringNode == null ) {
            legend = Settings.jpoResources.getString( "ThumbnailDescriptionNoNodeError" );
            setVisible( false );
        } else if ( referringNode.getUserObject() instanceof PictureInfo ) {
            PictureInfo pi = (PictureInfo) referringNode.getUserObject();
            legend = pi.getDescription();
            highresLocationJTextField.setText( pi.getImageLocation() );
            setVisible( true );
        } else {
            // GroupInfo
            legend = ( (GroupInfo) referringNode.getUserObject() ).getGroupName();
            highresLocationJTextField.setText( "" );
            setVisible( true );
        }
        pictureDescriptionJTA.setText( legend );

        formatDescription();
        showSlectionStatus();
    }

    /**
     * This method how the description panel is shown. It can be set to
     * ThumbnailDescriptionJPanel.LARGE_DESCRIPTION,
     * ThumbnailDescriptionJPanel.MINI_INFO,
     *
     * @param displayMode display Mode
     */
    public void setDisplayMode( DescriptionSize displayMode ) {
        this.displayMode = displayMode;
    }

    /**
     * This method formats the text information fields for the indicated node.
     */
    public void formatDescription() {
        if ( displayMode == LARGE_DESCRIPTION ) {
            pictureDescriptionJTA.setFont( LARGE_FONT );
        } else {
            // i.e.  MINI_INFO
            pictureDescriptionJTA.setFont( SMALL_FONT );
        }
        setTextAreaSize();

        if ( ( referringNode != null ) && ( referringNode.getUserObject() instanceof PictureInfo ) && ( displayMode == MINI_INFO ) ) {
            highresLocationJTextField.setVisible( true );
        } else {
            highresLocationJTextField.setVisible( false );
        }

    }

    /**
     * sets the size of the TextArea
     */
    public void setTextAreaSize() {
        Dimension textAreaSize = pictureDescriptionJTA.getPreferredSize();

        int targetHeight;
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
            LOGGER.log( Level.FINE, "ThumbnailDescriptionJPanel.setTextAreaSize set to: {0} / {1}", new Object[]{ Integer.toString( targetWidth ), Integer.toString( targetHeight ) } );
        }
    }

    /**
     * Overridden method to allow the better tuning of visibility
     *
     * @param visibility Send in true or false
     */
    @Override
    public void setVisible( boolean visibility ) {
        super.setVisible( visibility );
        pictureDescriptionJTA.setVisible( visibility );
        pictureDescriptionJSP.setVisible( visibility );
    }

    /**
     * changes the colour so that the user sees whether the thumbnail is part of
     * the selection
     */
    public void showSlectionStatus() {
        if ( Settings.getPictureCollection().isSelected( referringNode ) ) {
            showAsSelected();
        } else {
            showAsUnselected();
        }
    }

    /**
     * changes the color so that the user sees that the thumbnail is part of the
     * selection.<p>
     * This method is EDT safe.
     */
    public void showAsSelected() {
        Runnable r = () -> {
            pictureDescriptionJTA.setBackground( Settings.SELECTED_COLOR_TEXT );
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }

    /**
     * Changes the color so that the user sees that the thumbnail is not part of
     * the selection<p>
     * This method is EDT safe
     */
    public void showAsUnselected() {
        Runnable runnable = () -> {
            pictureDescriptionJTA.setBackground( Settings.UNSELECTED_COLOR );
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable );
        }

    }

    /**
     * Returns the preferred size for the ThumbnailDescription as a Dimension
     * using the thumbnailSize as width and height.
     *
     * @return Returns the preferred size for the ThumbnailDescription as a
     * Dimension using the thumbnailSize as width and height.
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
     * This method sets the scaling factor for the display of a thumbnail
     * description
     *
     * @param thumbnailSizeFactor Factor
     */
    public void setFactor( float thumbnailSizeFactor ) {
        this.thumbnailSizeFactor = thumbnailSizeFactor;
        setTextAreaSize();
    }

    /**
     * returns the current node
     *
     * @return the current node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return referringNode;
    }

    /**
     * here we get notified by the PictureInfo object that something has
     * changed.
     */
    @Override
    public void pictureInfoChangeEvent( final PictureInfoChangeEvent pictureInfoChangeEvent ) {
        Runnable runnable = () -> {
            if ( pictureInfoChangeEvent.getDescriptionChanged() ) {
                pictureDescriptionJTA.setText( pictureInfoChangeEvent.getPictureInfo().getDescription() );
            }
            
            if ( pictureInfoChangeEvent.getHighresLocationChanged() ) {
                highresLocationJTextField.setText( pictureInfoChangeEvent.getPictureInfo().getImageLocation() );
            }
            
            if ( pictureInfoChangeEvent.getWasSelected() ) {
                showAsSelected();
            } else if ( pictureInfoChangeEvent.getWasUnselected() ) {
                showAsUnselected();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater( runnable );
        }

    }

    // Here we are not that interested in TreeModel change events other than to find out if our
    // current node was removed in which case we close the Window.
    /**
     * implemented here to satisfy the TreeModelListener interface; not used.
     *
     * @param e event
     */
    @Override
    public void treeNodesChanged( TreeModelEvent e ) {
        // find out whether our node was changed
        Object[] children = e.getChildren();
        if ( children == null ) {
            // the root node does not have children as it doesn't have a parent
            return;
        }

        for ( Object child : children ) {
            if ( child.equals( referringNode ) ) {
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
     * implemented here to satisfy the TreeModelListener interface; not used.
     *
     * @param e event
     */
    @Override
    public void treeNodesInserted( TreeModelEvent e ) {
    }

    /**
     * The TreeModelListener interface tells us of tree node removal events.
     *
     * @param e event
     */
    @Override
    public void treeNodesRemoved( TreeModelEvent e ) {
    }

    /**
     * implemented here to satisfy the TreeModelListener interface; not used.
     *
     * @param e event
     */
    @Override
    public void treeStructureChanged( TreeModelEvent e ) {
    }
}
