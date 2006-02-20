package jpo;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.beans.*;
import javax.swing.filechooser.*;
import java.util.*;
import java.awt.event.*;

/*
PictureAdder.java:  a class that first brings up a filechooser and then invokes itself as a thread


Copyright (C) 2002, 2006  Richard Eigenmann.
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
 *   Brings up a Filechooser and then loads the selected pictures into the collection. It has 
 *   an asseccory panel that shows a thumbnail of the image.
 */
public class PictureAdder implements PropertyChangeListener, CategoryGuiListenerInterface {
	/**
	 *  Checkbox that allows the user to specifiy whether pictures 
	 *  in the subdirectories should be added or not.
	 */
	private JCheckBox recurseJCheckBox = new JCheckBox( Settings.jpoResources.getString("recurseJCheckBox") );

	/**
	 *  Checkbox that allows the user to specifiy whether directory structures should be retained
	 */
	private JCheckBox retainDirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString("retainDirectoriesJCheckBox") );
	
	
	/**
	 *  Checkbox that allows the user to specify that pictures already 
	 *  in the collection should be ignored.
	 */
	private JCheckBox newOnlyJCheckBox = new JCheckBox( Settings.jpoResources.getString("newOnlyJCheckBox") );


	/**
	 *  Checkbox that allows the user to specify that pictures already 
	 *  in the collection should be ignored.
	 */
	private JCheckBox showThumbnailJCheckBox = new JCheckBox( Settings.jpoResources.getString("showThumbnailJCheckBox") );


	/**
	 *  Category Button
	 **/
	private JButton categoriesJButton = new JButton ( Settings.jpoResources.getString("categoriesJButton") );


	/**
	 *   static global variable that remembers whether the user wanted to see thumbnails or not.
	 */
	private static boolean showThumbnail = true;

	/**
	 *  This component shows the thumbnail. It is a JLabel as we can thus use the 
	 *  ImageIcon to display the pciture.
	 */
	private JLabel thumbnailJLabel = new JLabel();


	/**
	 *  preferred width of accessory panel
	 */
	private static final int PREFERRED_WIDTH = 200;

	/**
	 *  preferred height of accessory panel
	 */
	private static final int PREFERRED_HEIGHT = 180;

	
	/**
	 *  node to display after adding pictures
	 */
	private SortableDefaultMutableTreeNode displayNode = null;
	

	/**
	 *  this vector holds the list of categories to be applied to newly loaded pictures.
	 */
	private HashSet selectedCategories = null;


	/**
	 *  Constructor for a PictureAdder. It creates a JFilechooser GUI and then fires off a 
	 *  thread to load the pictures in the background.
	 *
	 *  @param  startNode   The node to which the selected pictures are to be added. 
	 *                      It must be a GroupInfo Node.
	 */
	public PictureAdder ( final SortableDefaultMutableTreeNode startNode ) {
		
		if ( ! ( startNode.getUserObject() instanceof GroupInfo ) ) {
			JOptionPane.showMessageDialog( 
				Settings.anchorFrame, 
				Settings.jpoResources.getString("notGroupInfo"), 
				Settings.jpoResources.getString("genericError"),
				JOptionPane.ERROR_MESSAGE);
			return;
		}


		recurseJCheckBox.setSelected( true );
		newOnlyJCheckBox.setSelected( true );
		showThumbnailJCheckBox.setSelected( showThumbnail );
		retainDirectoriesJCheckBox.setSelected( true );

		/*categoriesJButton.setPreferredSize( Settings.defaultButtonDimension );
	        categoriesJButton.setMinimumSize( Settings.defaultButtonDimension );
	        categoriesJButton.setMaximumSize( Settings.defaultButtonDimension );
		categoriesJButton.setBorder(BorderFactory.createRaisedBevelBorder());
	        categoriesJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				CategoryUsageJFrame cujf = new CategoryUsageJFrame();
				cujf.updateCategories();
				cujf.addCategoryGuiListener( PictureAdder.this );
			}
		} );*/


		JPanel optionsJPanel = new JPanel();
		optionsJPanel.setLayout( new BoxLayout( optionsJPanel, BoxLayout.Y_AXIS ) );
		optionsJPanel.add( recurseJCheckBox, BorderLayout.WEST );
		optionsJPanel.add( newOnlyJCheckBox, BorderLayout.WEST );
		optionsJPanel.add( showThumbnailJCheckBox, BorderLayout.WEST );
		optionsJPanel.add( retainDirectoriesJCheckBox, BorderLayout.WEST );
		//optionsJPanel.add( categoriesJButton, BorderLayout.WEST );
		optionsJPanel.setPreferredSize( new Dimension( PREFERRED_WIDTH, PREFERRED_HEIGHT ) );

		thumbnailJLabel.setPreferredSize( new Dimension( PREFERRED_WIDTH, PREFERRED_HEIGHT ) );


		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
		tabbedPane.add( "Options", optionsJPanel );
		tabbedPane.add( "Thumbnail", thumbnailJLabel );
		
		
 		JFileChooser jFileChooser = new JFileChooser();
    
		jFileChooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		jFileChooser.setMultiSelectionEnabled( true );
		jFileChooser.setApproveButtonText( Settings.jpoResources.getString("fileChooserAddButtonLabel") );
		jFileChooser.setDialogTitle( Settings.jpoResources.getString("PictureAdderDialogTitle") );
		jFileChooser.setAccessory( tabbedPane );
		jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
		jFileChooser.addPropertyChangeListener( this );
		
		if( jFileChooser.showOpenDialog( Settings.anchorFrame ) == JFileChooser.APPROVE_OPTION ) {
			final File[] chosenFiles = jFileChooser.getSelectedFiles();
			Settings.memorizeCopyLocation( jFileChooser.getCurrentDirectory().getPath() );
			
			Thread t = new Thread() {
				public void run() {
					displayNode = startNode.addPictures( chosenFiles, newOnlyJCheckBox.isSelected(), recurseJCheckBox.isSelected(), retainDirectoriesJCheckBox.isSelected() );
					if ( target != null ) {
						target.requestShowGroup( displayNode );
					}
				}
			};
				
			t.start();
		}		
		showThumbnail = showThumbnailJCheckBox.isSelected();
	}			


	/**
	 *  This method is invoked from the FileChooser and creates the thumbnail.
	 *
	 *  See http://java.sun.com/developer/JDCTechTips/index.html for the March 16 issue on 
	 *  Preview panels in the JFileChooser.
	 */
	public void propertyChange( PropertyChangeEvent changeEvent ) {
		String changeName = changeEvent.getPropertyName();
		if ( changeName.equals( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY ) ) {
			File file = (File) changeEvent.getNewValue();
			if ( ( file != null )  && ( showThumbnailJCheckBox.isSelected() ) ) {
				ImageIcon icon = new ImageIcon( file.getPath() );
				if ( icon.getIconWidth() > PREFERRED_WIDTH) {
					icon = new ImageIcon( icon.getImage().getScaledInstance
						(PREFERRED_WIDTH, -1, 
						Image.SCALE_DEFAULT));
				if (icon.getIconHeight() > PREFERRED_HEIGHT) {
					icon = new ImageIcon( icon.getImage().getScaledInstance
						(-1, PREFERRED_HEIGHT, 
						Image.SCALE_DEFAULT));
				}
     				}
				thumbnailJLabel.setIcon(icon);
			}
		}
	}
	


	/**
	 *  This object refers to the target object that wil receive notification when the adding is done.
	 */
	private GroupPopupInterface target = null;
	
	/**
	 *  this method logs the object to call back when the pictures have been added.
	 */
	public void setNotificationTarget( GroupPopupInterface target ) {
		this.target = target;
	}


	
	/**
	 *  This method gets invoked from the CategoryUsageJFrame object when a selection has been made.
	 */
	public void categoriesChosen(  HashSet selectedCategories  ) {
		this.selectedCategories = selectedCategories;
	}

}
