package jpo;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

/*
ThumbnailDescriptionJPanel.java:  class that creates a panel showing the details of a thumbnail

Copyright (C) 2002  Richard Eigenmann.
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
	 *  a link to the SortableDefaultMutableTreeNode in the data model.
	 *  This allows thumbnails to be selected by sending a 
	 *  nodeSelected event to the data model.
	 **/
	protected  SortableDefaultMutableTreeNode referringNode;

	/**
	 *  The GridBagConstrains for this ThumbnailDescriptionJPanel which help to 
	 *  position it in the panel
	 */
	protected GridBagConstraints c = new GridBagConstraints(); 


	/** 
	 *  This object holds the description
	 */
	private JTextArea pictureDescriptionJTA = new JTextArea();;

	/**
	 *   This JScrollPane holds the JTextArea pictureDescriptionJTA so that it can have 
	 *   multiple lines of text if this is required.
	 */
	private JScrollPane pictureDescriptionJSP = new JScrollPane( pictureDescriptionJTA );


	
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
	 *  This variable indicates the position number the Thumbnail component is on the panel.
	 *  The grid is arranged as follows:
	 *  <pre>
	 *    0   1   2   3
	 *    4   5   6   7
	 *    8   9   10
	 *  </pre>
	 */
	private int position;


	
	/**
	 *  This is a reference to the panel in which the component is being shown
	 */
	private ThumbnailJScrollPane associatedPanel;
	



	/**
	 *   Constant that indicates that the description should be formatted as
	 *   a large description meaning large font and just the image description
	 */
	public static final int  LARGE_DESCRIPTION = 1;
	
	/**
	 *   Constant that indicates that the descriptions should be formatted as
	 *   a small info panel meaning small font and much information
	 */
	public static final int MINI_INFO = LARGE_DESCRIPTION + 1;


	/**
	 *  Font to be used for Large Texts:
	 */	 
	private static Font largeFont = new Font ( "Arial", Font.PLAIN, 12 );
	

	/**
	 *  Font to be used for small texts:
	 */	 
	private static Font smallFont = new Font ( "Arial", Font.PLAIN, 9 );
	

	/**
	 *   This field controls how the description panel is shown. It can be set to 
	 *   ThumbnailDescriptionJPanel.LARGE_DESCRIPTION, 
	 *   ThumbnailDescriptionJPanel.MINI_INFO,
	 */
	//private int displayMode = MINI_INFO;
	private int displayMode = LARGE_DESCRIPTION;
		
	
	/**
	 *   Construct a new ThumbnailDescrciptionJPanel
	 *
	 *   @param   position   The position this component is on the panel
 	 *   @param   associatedPanel   The ThumbnailJScrollPane it is being displayed on
	 **/
	public ThumbnailDescriptionJPanel ( int position, ThumbnailJScrollPane associatedPanel  ) {
		this.position = position;
		this.associatedPanel = associatedPanel;
		
		// attach this panel to the tree model so that it is notified about changes
		Settings.top.getTreeModel().addTreeModelListener( this );

		this.setBackground( Color.WHITE );
		
		//pictureDescriptionJTA.setFont( Settings.captionFont ); 
		pictureDescriptionJTA.setWrapStyleWord( true ); 
		pictureDescriptionJTA.setLineWrap( true ); 
		pictureDescriptionJTA.setEditable( true ); 
		pictureDescriptionJTA.setBorder( BorderFactory.createEmptyBorder(2,2,8,2) ); 
		pictureDescriptionJTA.setCaret( dumbCaret ); 
		//pictureDescriptionJTA.setMaximumSize( new Dimension( Settings.thumbnailSize, Settings.thumbnailDescriptionHeight) );
		//pictureDescriptionJTA.setPreferredSize( new Dimension( Settings.thumbnailSize, 50) );
		pictureDescriptionJTA.setMinimumSize( new Dimension( Settings.thumbnailSize, 20) );
		pictureDescriptionJTA.setAlignmentX( Component.CENTER_ALIGNMENT );
		pictureDescriptionJTA.setInputVerifier( new InputVerifier() {
			public boolean verify ( JComponent component ) {
				// doUpdate();
				return true;
			}
			public boolean shouldYieldFocus ( JComponent component ) {
				doUpdate();
				return true;
			}
		} );

		c.fill = c.BOTH; 
		c.anchor = c.NORTH;


//		this.setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		//add ( pictureDescriptionJTA, BorderLayout.NORTH);
		pictureDescriptionJSP.setBorder( BorderFactory.createEmptyBorder(0,0,0,0) );
		add ( pictureDescriptionJSP );
		
		

		highresLocationJTextField.setMinimumSize( new Dimension( Settings.thumbnailSize, 20) );
		highresLocationJTextField.setMaximumSize( new Dimension( Settings.thumbnailSize, 20) );
		highresLocationJTextField.setPreferredSize( new Dimension( Settings.thumbnailSize, 20) );
//		add ( highresLocationJTextField );


		lowresLocationJTextField.setMinimumSize( new Dimension( Settings.thumbnailSize, 20) );
		lowresLocationJTextField.setMaximumSize( new Dimension( Settings.thumbnailSize, 20) );
		lowresLocationJTextField.setPreferredSize( new Dimension( Settings.thumbnailSize, 20) );
//		add ( lowresLocationJTextField );
		
	}



	/**
	 *    doUpdate writes the changed text back to the data model and submits an nodeChanged
	 *    notification on the model. It get's called by the Inputverifier on the text area.
	 */
	public void doUpdate() {
		if ( referringNode == null ) {
			return;
		}
		pictureDescriptionJTA.setBackground( Color.white );
		if ( ! pictureDescriptionJTA.getText().equals( referringNode.getUserObject().toString() ) ) {
			if ( referringNode.getUserObject() instanceof PictureInfo )
				( (PictureInfo) referringNode.getUserObject() ).setDescription( pictureDescriptionJTA.getText() );
			else if ( referringNode.getUserObject() instanceof GroupInfo )
				( (GroupInfo) referringNode.getUserObject() ).setGroupName( pictureDescriptionJTA.getText() );
		}
		setTextAreaSize();
		pictureDescriptionJTA.revalidate();
		referringNode.getTreeModel().nodeChanged( referringNode );
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
		
		// unattach the change Listener
		if ( ( this.referringNode != null ) 
		  && ( this.referringNode.getUserObject() instanceof  PictureInfo ) ) {
			PictureInfo pi = (PictureInfo) this.referringNode.getUserObject();
			pi.removePictureInfoChangeListener( this );
		}

		
		this.referringNode = referringNode;

		// attach the change Listener
		if ( ( referringNode != null ) 
		  && ( referringNode.getUserObject() instanceof  PictureInfo ) ) {
			PictureInfo pi = (PictureInfo) referringNode.getUserObject();
			pi.addPictureInfoChangeListener( this );
		}


		String legend;
		if ( referringNode == null ) {
			legend = "No node for this position";
			setVisible( false );
		} else if ( referringNode.getUserObject() instanceof  PictureInfo ) {
			PictureInfo pi = (PictureInfo) referringNode.getUserObject(); 
			legend = pi.getDescription();
			highresLocationJTextField.setText( pi.getHighresLocation() );
			lowresLocationJTextField.setText( pi.getLowresLocation() );
			setVisible( true );
		} else {
			// GroupInfo
			legend = ((GroupInfo) referringNode.getUserObject()).getGroupName();
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
		
		if ( ( referringNode != null )
		  && ( referringNode.getUserObject() instanceof  PictureInfo )
		  && ( displayMode == MINI_INFO ) ) {
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
		Dimension pdDimension = Tools.getJTextAreaDimension( pictureDescriptionJTA, Settings.thumbnailSize );
		pictureDescriptionJTA.setPreferredSize( pdDimension );
		pictureDescriptionJTA.setMaximumSize( pdDimension );
	}

	/**
	 *   Overridden method to allow the better tuning of visibility
	 */
	public void setVisible( boolean visibility ) {
		super.setVisible( visibility );
		pictureDescriptionJTA.setVisible( visibility );
		pictureDescriptionJSP.setVisible( visibility );
		validate();
	}


	/**
	 *  returns the current node
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
			pictureDescriptionJTA.setText( e.getPictureInfo().getDescription () );
		}
		if ( e.getHighresLocationChanged() ) {
			highresLocationJTextField.setText( e.getPictureInfo().getHighresLocation () );
		}
		if ( e.getLowresLocationChanged() ) {
			lowresLocationJTextField.setText( e.getPictureInfo().getLowresLocation () );
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
	 */
	public void treeNodesChanged ( TreeModelEvent e ) {
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
					String legend = ((GroupInfo) userObject).getGroupName();
					if ( ! legend.equals( pictureDescriptionJTA.getText() ) ) {
						pictureDescriptionJTA.setText( legend );
					}
				}
			}
		}
	}


	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeNodesInserted ( TreeModelEvent e ) {
	}

	/**
	 *  The TreeModelListener interface tells us of tree node removal events. 
	 */
	public void treeNodesRemoved ( TreeModelEvent e ) {
	}

	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeStructureChanged ( TreeModelEvent e ) {
	}




	
}
