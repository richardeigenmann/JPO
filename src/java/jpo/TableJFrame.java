package jpo;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
TableDemo.java:  class that creates a JFrame and shows the children of a SortableDefaultMutableTreeNode in it

Originally lifted from the Swing Tutorial on the java.sun.com website. In as far as no prior copyright
exists the following copyright shall apply. (This code was heavily modified.)

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


public class TableJFrame extends JFrame {

	/**
	 *   handle to the node being edited
	 */    
	private SortableDefaultMutableTreeNode groupNode;



	/**  
	 *  constructs the Frame and records the group node for which 
	 *  this is being performed.
	 *
	 *  @param	groupNode	The node whose elements are to be shown in the table
	 */
	public TableJFrame( SortableDefaultMutableTreeNode groupNode ) {
		this.groupNode = groupNode;
		setTitle( ( (GroupInfo) groupNode.getUserObject() ).getGroupName() );

	        MyTableModel myModel = new MyTableModel();
		TableSorter sorter = new TableSorter( myModel);
		JTable table = new JTable( sorter );
		//JTable table = new JTable( myModel );
		//table.setShowHorizontalLines( false );
		//table.setRowSelectionAllowed( true );
		//table.setColumnSelectionAllowed( true );
		//table.setSelectionBackground( Color.red );
		table.setCellSelectionEnabled( true );

		ExcelAdapter myExcelAdapter = new ExcelAdapter( table );

		
		sorter.addMouseListenerToHeaderInTable( table );
	        table.setPreferredScrollableViewportSize(new Dimension(1000, 700));

	        //Create the scroll pane and add the table to it. 
	        JScrollPane scrollPane = new JScrollPane(table);

        	//Add the scroll pane to this window.
	        getContentPane().add(scrollPane, BorderLayout.CENTER);

        	addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible ( false );
				dispose ();
			}
        	});
	}




	class MyTableModel extends AbstractTableModel {
    
	        final String[] columnNames = {"Nr.",
				      "Description", 
                                      "Highres Location",
                                      "Lowres Location",
				      "Film Reference",
				      "Creation Time", 
				      "Comment",
				      "Photographer",
				      "Copyright Holder"};

		public int getColumnCount() {
			return 9;
	        }
        
        	public int getRowCount() {
			return groupNode.getChildCount();
	        }

	        public String getColumnName(int col) {
			return columnNames[col];
	        }


	        /**
	         * JTable uses this method to determine the default renderer/
        	 * editor for each cell.  If we didn't implement this method,
	         * then the last column would contain text ("true"/"false"),
        	 * rather than a check box.
	         */
        	public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
	        }



	        /**
        	 *  Determines whether the user is allowed to edit the cell
	         */
        	public boolean isCellEditable(int row, int col) {
			if ( col == 0 ) return false; // don't bother with expensive lookups when it's the index
		
			SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
			Object userObject = queryNode.getUserObject();
			if ( userObject instanceof PictureInfo ) 
				return true;
			else 
				return ( col < 2 );  // if col = 1 edit if col > 1 don't edit
	        }



	        /**
        	 *   method that interfaces between the row and colum of the table and the
		 *   underlying data model in the JTree
        	 */
	        public Object getValueAt(int row, int col) {
			if ( col == 0 ) return new Integer( row + 1); // index
		
			SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
			Object userObject = queryNode.getUserObject();
			if ( userObject instanceof PictureInfo ) {
				switch( col ) {
					case 1:
						return ((PictureInfo) userObject).getDescription();
					case 2:
						return ((PictureInfo) userObject).getHighresLocation();
					case 3:
						return ((PictureInfo) userObject).getLowresLocation();
					case 4: 
						return ((PictureInfo) userObject).getFilmReference();
					case 5: 
						return ((PictureInfo) userObject).getCreationTime();
					case 6: 
						return ((PictureInfo) userObject).getComment();
					case 7: 
						return ((PictureInfo) userObject).getPhotographer();
					case 8: 
						return ((PictureInfo) userObject).getCopyrightHolder();
					default:
						return "Column? " + Integer.toString( col );
				}
			} else {
				// GroupInfo
				switch( col ) {
					case 1:
						return ((GroupInfo) userObject).getGroupName();
					default:
						return "";
				}
			}
	        }


	        /**
        	 *   method that interfaces between the row and colum of the table and the
		 *   underlying data model in the JTree
        	 */
	        public void setValueAt( Object value, int row, int col ) {
			SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
			Object userObject = queryNode.getUserObject();
			String newString = value.toString();
			if ( userObject instanceof PictureInfo ) {
				switch( col ) {
					case 1:
						((PictureInfo) userObject).setDescription( newString );
						break;
					case 2:
						((PictureInfo) userObject).setHighresLocation( newString );
						break;
					case 3:
						((PictureInfo) userObject).setLowresLocation( newString );
						break;
					case 4:
						((PictureInfo) userObject).setFilmReference( newString );
						break;
					case 5:
						((PictureInfo) userObject).setCreationTime( newString );
						break;
					case 6:
						((PictureInfo) userObject).setComment( newString );
						break;
					case 7:
						((PictureInfo) userObject).setPhotographer( newString );
						break;
					case 8:
						((PictureInfo) userObject).setCopyrightHolder( newString );
						break;
					default:
						Tools.log ("TableDemo.java invoked setValueAt on PictureInfo with bad column: " + Integer.toString( col ) );
						break;
				}
			} else {
				// GroupInfo
				switch( col ) {
					case 1:
						((GroupInfo) userObject).setGroupName( newString );
						break;
					default:
						Tools.log ("TableDemo.java invoked setValueAt on GroupInfo with bad column: " + Integer.toString( col ) );
						break;
				}
			}
			queryNode.setUnsavedUpdates();
    		}
	}

}
