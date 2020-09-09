package org.jpo.gui;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Originally lifted from the Swing Tutorial on the java.sun.com website. In as far as no prior copyright
 exists the following copyright shall apply. (This code was heavily modified.)

 Copyright (C) 2002 - 2019  Richard Eigenmann.
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

        final MyTableModel myModel = new MyTableModel();
        final TableSorter sorter = new TableSorter( myModel );
        final JTable table = new JTable( sorter );
        table.setCellSelectionEnabled( true );

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
                // noop
            }

            @Override
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_DELETE ) {
                    LOGGER.info( "delete pressed" );
                }
            }

            @Override
            public void keyReleased( KeyEvent e ) {
                // noop
            }
        } );

    }

    private class MyTableModel extends AbstractTableModel {

        private final String[] columnNames = { "Nr.",
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
         * method that interfaces between the row and column of the table and the
         * underlying data model in the JTree
         */
        @Override
        public Object getValueAt( final int row, final int col ) {
            if ( col == 0 ) {
                return row + 1; // index
            }
            final SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
            final Object userObject = queryNode.getUserObject();
            if ( userObject instanceof PictureInfo pi ) {
                switch ( col ) {
                    case 1:
                        return pi.getDescription();
                    case 2:
                        return pi.getImageLocation();
                    case 3:
                        return pi.getFilmReference();
                    case 4:
                        return pi.getCreationTime();
                    case 5:
                        return pi.getComment();
                    case 6:
                        return pi.getPhotographer();
                    case 7:
                        return pi.getCopyrightHolder();
                    case 8:
                        return pi.getLatLngString();
                    default:
                        return "Unknown Column: " + col;
                }
            } else {
                // GroupInfo
                if (col == 1) {
                    return ((GroupInfo) userObject).getGroupName();
                }
                return "";
            }
        }

        /**
         * method that interfaces between the row and column of the table and the
         * underlying data model in the JTree
         */
        @Override
        public void setValueAt( final Object value, final int row, final int col ) {
            final SortableDefaultMutableTreeNode queryNode = (SortableDefaultMutableTreeNode) groupNode.getChildAt( row );
            final Object userObject = queryNode.getUserObject();
            final String newString = value.toString();
            if ( userObject instanceof PictureInfo pi) {
                switch ( col ) {
                    case 1:
                        pi.setDescription( newString );
                        break;
                    case 2:
                        pi.setImageLocation( new File( newString ) );
                        break;
                    case 3:
                        pi.setFilmReference( newString );
                        break;
                    case 4:
                        pi.setCreationTime( newString );
                        break;
                    case 5:
                        pi.setComment( newString );
                        break;
                    case 6:
                        pi.setPhotographer( newString );
                        break;
                    case 7:
                        pi.setCopyrightHolder( newString );
                        break;
                    case 8:
                        pi.setLatLng( newString );
                        break;
                    default:
                        LOGGER.log( Level.INFO, "Bad column: {0}", Integer.toString( col ));
                        break;
                }
            } else {
                // GroupInfo
                if (col == 1) {
                    ((GroupInfo) userObject).setGroupName(newString);
                } else {
                    LOGGER.log(Level.INFO, "Bad column: {0}", Integer.toString(col));
                }
            }
            queryNode.getPictureCollection().setUnsavedUpdates();
        }
    }
}
