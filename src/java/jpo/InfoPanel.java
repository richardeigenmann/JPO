package jpo; 
 
import javax.swing.*; 
import javax.swing.Timer;
import java.awt.event.*; 
import java.awt.*; 
 
/*
InfoPanel.java:  a JScrollPane that shows information after selection events.

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
 *  The infopanel shows interesting stuff about what has been selected. Such as either the
 *  picture details or the information about the group.
 */ 
  
public class InfoPanel 
	extends JScrollPane {
 
	/** 
	 * ThumbnailComponent to show the picture that has been selected
	 */ 
	private Thumbnail thumbnail; 


	/**
	 *  StatisticsPanel
	 */
	private	final CollectionPropertiesJPanel statsJPanel = new CollectionPropertiesJPanel();


	/**
	 *  how often to update the statistics panel
	 */
	private static final int delay = 500; //milliseconds

	/**
	 *  A timer to fire off the refresh of the Thumbnail Queue display
	 */
	private Timer t;

 
 
	/** 
	 *   creates a new JScrollPane with an embedded JPanel and provides a set of  
	 *   methods that allow thumbnails to be displayed. <p> 
	 *
	 *   The passing in of the caller is obsolete and should be removed when  
	 *   a better interface type solution has been built. 
	 *  
	 */ 
	public InfoPanel() { 
		statsJPanel.setBackground( Color.white ); 
		setWheelScrollingEnabled (true ); 
		
		//  set the amount by which the panel scrolls down when the user clicks the 
		//  little down or up arrow in the scrollbar
		getVerticalScrollBar().setUnitIncrement( 80 );
		
		thumbnail = new Thumbnail( Settings.thumbnailSize );
		final ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				statsJPanel.updateQueueCount();
		      	}
		};
		t = new Timer( delay, taskPerformer );
	} 
 
 
 

	/**
	 *   Invoked to tell that we should display something
	 *   @param node 	The Group or Picture node to be displayed.
	 */
	public void showInfo ( SortableDefaultMutableTreeNode node ) {
		if ( node.getUserObject() instanceof GroupInfo ) {
			statsJPanel.updateStats( node );  // everything
			setViewportView( statsJPanel ); 
			t.start();  // updates the queue-count
		} else {
			thumbnail.setNode( node );
			setViewportView( thumbnail ); 
			t.stop();
		}
	}

}
	


 
 
 
 
 
 
 
 
