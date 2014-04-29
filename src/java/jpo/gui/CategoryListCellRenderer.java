package jpo.gui;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import jpo.dataModel.Category;

/*
 CategoryListCellRenderer.java:  A class which formats Category for use in a JList

 Copyright (C) 2002-2012  Richard Eigenmann.
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
 * A class which formats a Category for use in a JList
 *
 */
public class CategoryListCellRenderer extends JCheckBox implements ListCellRenderer<Category> {

    /**
     * Constructor
     */
    public CategoryListCellRenderer() {
        setOpaque( true );
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Category> list,
            Category category,
            int index,
            boolean isSelected,
            boolean cellHasFocus ) {

        int status = category.getStatus();
        if ( status == Category.UNDEFINED ) {
            setSelected( false );
            setEnabled( true );
            //setBackground( Color.gray );
        } else if ( status == Category.SELECTED ) {
            setSelected( true );
            setEnabled( true );
            //setBackground( Color.white );
        } else if ( status == Category.BOTH ) {
            setSelected( false );
            setEnabled( false );
            //setBackground( Color.gray );
        } else if ( status == Category.UN_SELECTED ) {
            setSelected( false );
            setEnabled( true );
            //setBackground( Color.white );
        }
        setText( category.toString() );

        return this;
    }
}
