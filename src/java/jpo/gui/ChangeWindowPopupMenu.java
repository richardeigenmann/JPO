package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import java.awt.event.*;
import javax.swing.*;

/*
GroupPopupMenu.java: popup menu for groups
Copyright (C) 2002-2007  Richard Eigenmann.
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
 * A class that generates a popup menu for a group node. This became nescessary primarily because
 * the code was getting a bit long and was clutterin up a different class. Seperating out the 
 * popup menu and making it an object and forcing an interface on the object instantiating
 * it is propbably more in line with the OO philosophy.
 *
 */

public class ChangeWindowPopupMenu extends JPopupMenu
			implements ActionListener {


	/** 
	 *  Menu item that indicates that a Fullscreen window should be created.
	 **/
	private JMenuItem fullScreenJMenuItem = new JMenuItem( Settings.jpoResources.getString("fullScreenLabel") ); 


	/** 
	 *  Menu item that indicates that the window should be created on the LEFT half of the display.
	 **/
	private JMenuItem leftWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("leftWindowLabel") ); 


	/** 
	 *  Menu item that indicates that the window should be created on the RIGHT half of the display.
	 **/
	private JMenuItem rightWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("rightWindowLabel") ); 


	/** 
	 *  Menu item that indicates that the window should be created on the TOP LEFT quarter of the display.
	 **/
	private JMenuItem topLeftWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("topLeftWindowLabel") ); 
	

	/** 
	 *  Menu item that indicates that the window should be created on the TOP RIGHT quarter of the display.
	 **/
	private JMenuItem topRightWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("topRightWindowLabel") ); 


	/** 
	 *  Menu item that indicates that the window should be created on the BOTTOM LEFT quarter of the display.
	 **/
	private JMenuItem bottomLeftWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("bottomLeftWindowLabel") ); 
	

	/** 
	 *  Menu item that indicates that the window should be created on the BOTTOM RIGHT quarter of the display.
	 **/
	private JMenuItem bottomRightWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("bottomRightWindowLabel") ); 


	/** 
	 *  Menu item that indicates that the window should be created on the BOTTOM RIGHT quarter of the display.
	 **/
	private JMenuItem defaultWindowJMenuItem = new JMenuItem( Settings.jpoResources.getString("defaultWindowLabel") ); 

	/** 
	 *  Menu item that indicates that the window decorations should be shown.
	 **/
	private JMenuItem windowDecorationsJMenuItem = new JMenuItem( Settings.jpoResources.getString("windowDecorationsLabel") ); 


	/** 
	 *  Menu item that indicates that the window decorations should not be shown.
	 **/
	private JMenuItem windowNoDecorationsJMenuItem = new JMenuItem( Settings.jpoResources.getString("windowNoDecorationsLabel") ); 

	
	/**
	 *  Object that must implement the functions dealing with the user
	 *  request.
	 */
	private ChangeWindowInterface caller;


	/**
	 *  Creates a popup menu which allows the user to choose how he would like
	 *  his window to be positioned and whether it should have decorations.
	 *  @param caller	The object requesting the menu.
	 */
	public ChangeWindowPopupMenu ( ChangeWindowInterface caller ) {
		this.caller = caller;

		fullScreenJMenuItem.addActionListener(this);
		add( fullScreenJMenuItem );

		leftWindowJMenuItem.addActionListener(this);
		add( leftWindowJMenuItem );

		rightWindowJMenuItem.addActionListener(this);
		add( rightWindowJMenuItem );

		topLeftWindowJMenuItem.addActionListener(this);
		add( topLeftWindowJMenuItem );

		topRightWindowJMenuItem.addActionListener(this);
		add( topRightWindowJMenuItem );

		bottomLeftWindowJMenuItem.addActionListener(this);
		add( bottomLeftWindowJMenuItem );

		bottomRightWindowJMenuItem.addActionListener(this);
		add( bottomRightWindowJMenuItem );

		defaultWindowJMenuItem.addActionListener(this);
		add( defaultWindowJMenuItem );

		addSeparator();

		windowDecorationsJMenuItem.addActionListener(this);
		add( windowDecorationsJMenuItem );
		
		windowNoDecorationsJMenuItem.addActionListener(this);
		add( windowNoDecorationsJMenuItem );

		
	}



	/** 
	 *  Method that analyses the user initiated action and performs what the user reuqested.
	 *  @param e The Action Event recieved.
	 **/
	public void actionPerformed( ActionEvent e ) {
		// Group popup menu				

		if ( e.getSource() == fullScreenJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_FULLSCREEN );				
		else if ( e.getSource() == leftWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_LEFT );				
		else if ( e.getSource() == rightWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_RIGHT );				
		else if ( e.getSource() == topLeftWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_TOP_LEFT );				
		else if ( e.getSource() == topRightWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_TOP_RIGHT );				
		else if ( e.getSource() == bottomLeftWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_BOTTOM_LEFT );				
		else if ( e.getSource() == bottomRightWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_BOTTOM_RIGHT );				
		else if ( e.getSource() == defaultWindowJMenuItem )
			caller.switchWindowMode( ResizableJFrame.WINDOW_DEFAULT );				

			
		else if ( e.getSource() == windowDecorationsJMenuItem ) 
			caller.switchDecorations( true );
		else if ( e.getSource() == windowNoDecorationsJMenuItem ) 
			caller.switchDecorations( false );

		else 
			JOptionPane.showMessageDialog(null, 
				"Unknown event", 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
			
	}

	

}
