package jpo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/*
GroupInfoEditor.java:  GUI for editing groups
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
 *   class that creates a Frame and allows the field(s) of a group to be edited. It seems to run
 *   in it's own thread because it creates a frame and those objects are waiting on input on the
 *   main event queue.
 */

public class GroupInfoEditor {

	/**
	 *   JFrame that holds all the dialog components for editing the window.
	 */
	private JFrame jFrame = new JFrame( Settings.jpoResources.getString("GroupInfoEditorHeading") );
	
	/**
	 *  the text field in which the user can change the label
	 */
	private JTextArea descriptionJTextArea = new JTextArea();
	
	/**
	 *   The location of the lowres image file
	 */
	private JTextField lowresLocationJTextField = new JTextField();

	/**
	 *  Dimension for the edit fields
	 */
	private static Dimension inputDimension = new Dimension(400, 20);

	/**
	 *   An informative message about what sort of error we have if any on the lowres image
	 */
	private JLabel lowresErrorJLabel = new JLabel( "" );




	/**
	 *  the OK button
	 */
	private JButton OkJButton = new JButton ( Settings.jpoResources.getString("genericOKText") );

	/**
	 *  the Cancel button
	 */	
	private JButton CancelButton = new JButton ( Settings.jpoResources.getString("genericCancelText") );

	
	/**
	 *  the node being edited
	 */
	private SortableDefaultMutableTreeNode editNode;




	/**
	 *   Constructor that creates the JFrame and objects.
	 *
	 *   @param   editNode	The node being edited.
	 */
	public GroupInfoEditor( final SortableDefaultMutableTreeNode editNode ) {
		this.editNode = editNode;
	
		jFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				getRid();
			}
	        });  

		JPanel jPanel = new JPanel();
		jFrame.getContentPane().add(jPanel, BorderLayout.CENTER);
		jFrame.setLocationRelativeTo( Settings.anchorFrame );


		GridBagConstraints c = new GridBagConstraints(); 
		c.fill = c.NONE; 
		c.gridx = 0; 
		c.insets = new Insets(4,4,4,4); 

		jPanel.setLayout( new GridBagLayout() );

		JLabel descriptionJLabel = new JLabel ( Settings.jpoResources.getString("groupDescriptionLabel") );
		c.gridy = 1; 
		c.anchor = GridBagConstraints.WEST;  
		jPanel.add( descriptionJLabel, c );


		final GroupInfo gi = ( (GroupInfo) editNode.getUserObject() );
	
		descriptionJTextArea.setText( gi.getGroupName ());
	        descriptionJTextArea.setPreferredSize( new Dimension(400, 150) );
	        descriptionJTextArea.setWrapStyleWord( true ); 
	        descriptionJTextArea.setLineWrap( true ); 
	        descriptionJTextArea.setEditable( true ); 
		c.gridy++; 
		jPanel.add( descriptionJTextArea, c );
		

		JPanel lowresJPanel = new JPanel();
		
		JLabel lowresLocationJLabel = new JLabel ( Settings.jpoResources.getString("lowresLocationLabel") );
		lowresErrorJLabel.setFont ( new Font ( "Arial", Font.PLAIN, 10 ) );
		lowresJPanel.add ( lowresLocationJLabel );
		lowresJPanel.add ( lowresErrorJLabel );
		
		c.gridx = 0; c.gridy++;
		c.insets = new Insets(4,0,0,0); 
		jPanel.add( lowresJPanel, c );

		
	        lowresLocationJTextField.setPreferredSize( inputDimension );
		lowresLocationJTextField.setText( gi.getLowresLocation() );
		c.gridy++;
		c.insets = new Insets(0,0,0,0); 
		jPanel.add( lowresLocationJTextField, c );



		JPanel buttonJPanel = new JPanel();

	        OkJButton.setPreferredSize( Settings.defaultButtonDimension );
	        OkJButton.setMinimumSize( Settings.defaultButtonDimension );
	        OkJButton.setMaximumSize( Settings.defaultButtonDimension );
		OkJButton.setBorder(BorderFactory.createRaisedBevelBorder());
		OkJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
	        OkJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				gi.setGroupName( descriptionJTextArea.getText() );
				gi.setLowresLocation( lowresLocationJTextField.getText() );
				editNode.getPictureCollection().getTreeModel().nodeChanged( editNode );
				getRid();
			}
		});
		OkJButton.setDefaultCapable( true );
		jFrame.getRootPane().setDefaultButton ( OkJButton );
		buttonJPanel.add( OkJButton );
		
		
	        CancelButton.setPreferredSize( Settings.defaultButtonDimension );
	        CancelButton.setMinimumSize( Settings.defaultButtonDimension );
	        CancelButton.setMaximumSize( Settings.defaultButtonDimension );
		CancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
		CancelButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
	        CancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				getRid();				
			}
		});
		buttonJPanel.add( CancelButton );

		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		jPanel.add( buttonJPanel, c );
			
		jFrame.pack();
		jFrame.setVisible(true);
        }



	/**  
	 *  method that closes the window.
	 */
	private void getRid() {
		jFrame.setVisible ( false );
		jFrame.dispose ();
	}


}
