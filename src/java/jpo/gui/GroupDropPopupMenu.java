package jpo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import jpo.dataModel.Settings;

/*
GroupDropPopupMenu.java:  class that pops up a menu to ask for the desired action on a group drop
Copyright (C) 2010  Richard Eigenmann, Zürich
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
 * A popup menu that asks the user what exactly he had in mind with the drop
 * onto a group node.
 * @author Richard Eigenmann
 */
public class GroupDropPopupMenu extends JPopupMenu {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( GroupDropPopupMenu.class.getName() );

    /**
     * Value to indicate that nothing was chosen yet
     */
    public static final int ACTION_PENDING = -1;

    /**
     * Value to indicate that the drop should be cancelled
     */
    public static final int ACTION_CANCEL = ACTION_PENDING + 1;

    /**
     * Value to indicate that the node should be dropped before the current group
     */
    public static final int ACTION_DROP_BEFORE = ACTION_CANCEL + 1;

    /**
     * Value to indicate that the node should be dropped after the current group
     */
    public static final int ACTION_DROP_AFTER = ACTION_DROP_BEFORE + 1;

    /**
     * Value to indicate that the node should be dropped into the first place
     * of the drop node group
     */
    public static final int ACTION_DROP_INTO_TOP = ACTION_DROP_AFTER + 1;

    /**
     * Value to indicate that the node should be appended into the last place
     * of the drop node group
     */
    public static final int ACTION_DROP_INTO_BOTTOM = ACTION_DROP_INTO_TOP + 1;

    /**
     * A variable for callers to find out the chosen action
     */
    public int chosenAction = ACTION_PENDING;


    /**
     * Creates a popup menu that asks whether the node should be dropped before,
     * after or into the drop node at the beginning or end or whether the whole
     * thing should be cancelled
     */
    public GroupDropPopupMenu() {

        //  menu item that allows the user to edit the group description
        JMenuItem dropBefore = new JMenuItem( Settings.jpoResources.getString( "GDPMdropBefore" ) );
        dropBefore.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                chosenAction = ACTION_DROP_BEFORE;
            }
        } );
        add( dropBefore );

        // menu item that allows the user to edit the group description
        JMenuItem dropAfter = new JMenuItem( Settings.jpoResources.getString( "GDPMdropAfter" ) );
        dropAfter.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                chosenAction = ACTION_DROP_AFTER;
            }
        } );
        add( dropAfter );

        //  menu item that allows the user to edit the group description
        JMenuItem dropIntoFirst = new JMenuItem( Settings.jpoResources.getString( "GDPMdropIntoFirst" ) );
        dropIntoFirst.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                chosenAction = ACTION_DROP_INTO_TOP;
            }
        } );
        add( dropIntoFirst );

        //  menu item that allows the user to edit the group description
        JMenuItem dropIntoLast = new JMenuItem( Settings.jpoResources.getString( "GDPMdropIntoLast" ) );
        dropIntoLast.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                chosenAction = ACTION_DROP_INTO_BOTTOM;
            }
        } );
        add( dropIntoLast );

        // menu item that allows the user to edit the group description
        JMenuItem dropCancel = new JMenuItem( Settings.jpoResources.getString( "GDPMdropCancel" ) );
        dropCancel.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                chosenAction = ACTION_CANCEL;
            }
        } );
        add( dropCancel );
    }
}
