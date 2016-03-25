package jpo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.gui.swing.JTableCopyPasteClipboardAdapter;

/*
 TableDemo.java:  class that creates a JFrame and shows the children of a SortableDefaultMutableTreeNode in it

 Originally lifted from the Swing Tutorial on the java.sun.com website. In as far as no prior copyright
 exists the following copyright shall apply. (This code was heavily modified.)

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 *
 * @author Richard Eigenmann
 */
public class TableJFrame extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( TableJFrame.class.getName() );
    
    /**
     * handle to the node being edited
     */
    private final SortableDefaultMutableTreeNode groupNode;

    /**
     * constructs the Frame and records the group node for which this is being
     * performed.
     *
     * @param	groupNode	The node whose elements are to be shown in the table
     */
    public TableJFrame( SortableDefaultMutableTreeNode groupNode ) {
        this.groupNode = groupNode;
        setTitle( ( (GroupInfo) groupNode.getUserObject() ).getGroupName() );

        MyTableModel myModel = new MyTableModel();
        TableSorter sorter = new TableSorter( myModel );
        JTable table = new JTable( sorter );
        table.setCellSelectionEnabled( true );

        JTableCopyPasteClipboardAdapter myExcelAdapter = new JTableCopyPasteClipboardAdapter( table );

        sorter.addMouseListenerToHeaderInTable( table );
        table.setPreferredScrollableViewportSize( new Dimension( 1000, 700 ) );

        final JScrollPane scrollPane = new JScrollPane( table );
        getContentPane().add( scrollPane, BorderLayout.CENTER );

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                setVisible( false );
                dispose();
            }
        } );

        addKeyListener( new KeyListener() {
            @Override
            public void keyTyped( KeyEvent e ) {
            }

            //TODO Doesn't work! Attach to the Cell Editor instead?
            @Override
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_DELETE ) {
                    LOGGER.info( "delete pressed" );
                }
            }

            @Override
            public void keyReleased( KeyEvent e ) {
            }
        } );

    }

    class MyTableModel extends AbstractTableModel {

        final String[] columnNames = { "Nr.",
            "Description",
            "Highres Location",
            "Film Reference",
            "Creation Time",
            "Comment",
            "Photographer",
            "Copyright Holder",
            "Latitude x Longitude" };

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return groupNode.getChildCount();
        }

        @Override
        public String getColumnName( int col ) {
            return columnNames[col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass( int c ) {
            return getValueAt( 0, c ).getClass();
        }

        /**
         * Determines whether the user is allowed to edit the cell
         */
        @Override
        public boolean isCellEditable( int row, int col ) {
            if ( col == 0 ) {
                return false; // don't bother with expensive lookups when it's the index
            }
            SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
            Object userObject = queryNode.getUserObject();
            if ( userObject instanceof PictureInfo ) {
                return true;
            } else {
                return ( col < 2 );  // if col = 1 edit if col > 1 don't edit
            }
        }

        /**
         * method that interfaces between the row and colum of the table and the
         * underlying data model in the JTree
         */
        @Override
        public Object getValueAt( int row, int col ) {
            if ( col == 0 ) {
                return row + 1; // index
            }
            SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
            Object userObject = queryNode.getUserObject();
            if ( userObject instanceof PictureInfo ) {
                switch ( col ) {
                    case 1:
                        return ( (PictureInfo) userObject ).getDescription();
                    case 2:
                        return ( (PictureInfo) userObject ).getImageLocation();
                    case 3:
                        return ( (PictureInfo) userObject ).getFilmReference();
                    case 4:
                        return ( (PictureInfo) userObject ).getCreationTime();
                    case 5:
                        return ( (PictureInfo) userObject ).getComment();
                    case 6:
                        return ( (PictureInfo) userObject ).getPhotographer();
                    case 7:
                        return ( (PictureInfo) userObject ).getCopyrightHolder();
                    case 8:
                        return ( (PictureInfo) userObject ).getLatLngString();
                    default:
                        return "Unknown Column: " + Integer.toString( col );
                }
            } else {
                // GroupInfo
                switch ( col ) {
                    case 1:
                        return ( (GroupInfo) userObject ).getGroupName();
                    default:
                        return "";
                }
            }
        }

        /**
         * method that interfaces between the row and colum of the table and the
         * underlying data model in the JTree
         */
        @Override
        public void setValueAt( Object value, int row, int col ) {
            SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
            Object userObject = queryNode.getUserObject();
            String newString = value.toString();
            if ( userObject instanceof PictureInfo ) {
                switch ( col ) {
                    case 1:
                        ( (PictureInfo) userObject ).setDescription( newString );
                        break;
                    case 2:
                        ( (PictureInfo) userObject ).setImageLocation( newString );
                        break;
                    case 3:
                        ( (PictureInfo) userObject ).setFilmReference( newString );
                        break;
                    case 4:
                        ( (PictureInfo) userObject ).setCreationTime( newString );
                        break;
                    case 5:
                        ( (PictureInfo) userObject ).setComment( newString );
                        break;
                    case 6:
                        ( (PictureInfo) userObject ).setPhotographer( newString );
                        break;
                    case 7:
                        ( (PictureInfo) userObject ).setCopyrightHolder( newString );
                        break;
                    case 8:
                        ( (PictureInfo) userObject ).setLatLng( newString );
                        break;
                    default:
                        LOGGER.log( Level.INFO, "Bad column: {0}", Integer.toString( col ));
                        break;
                }
            } else {
                // GroupInfo
                switch ( col ) {
                    case 1:
                        ( (GroupInfo) userObject ).setGroupName( newString );
                        break;
                    default:
                        LOGGER.log( Level.INFO, "Bad column: {0}", Integer.toString( col ));
                        break;
                }
            }
            queryNode.getPictureCollection().setUnsavedUpdates();
        }
    }
}
