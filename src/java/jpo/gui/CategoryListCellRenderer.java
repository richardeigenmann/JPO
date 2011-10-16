package jpo.gui;

import jpo.dataModel.Category;
import javax.swing.*;
import java.awt.*;

/*
CategoryListCellRenderer.java:  A class which formats Category

Copyright (C) 2002-2011  Richard Eigenmann.
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
 * A class which formats a Category
 *
 */
public class CategoryListCellRenderer extends JCheckBox implements ListCellRenderer {

    public CategoryListCellRenderer() {
        setOpaque( true );
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object listObject,
            int index,
            boolean isSelected,
            boolean cellHasFocus ) {

        if ( listObject instanceof Category ) {
            Category c = ( (Category) listObject );
            int status = c.getStatus();
            if ( status == Category.undefined ) {
                setSelected( false );
                setEnabled( true );
                //setBackground( Color.gray );
            } else if ( status == Category.selected ) {
                setSelected( true );
                setEnabled( true );
                //setBackground( Color.white );
            } else if ( status == Category.both ) {
                setSelected( false );
                setEnabled( false );
                //setBackground( Color.gray );
            } else if ( status == Category.unSelected ) {
                setSelected( false );
                setEnabled( true );
                //setBackground( Color.white );
            }
        }
        setText( listObject.toString() );

        return this;
    }
}
