package jpo.dataModel;


/*
NodeNavigatorListener.java:  interface for notification

Copyright (C) 2007-2011 Richard Eigenmann, Zürich, Switzerland
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
 * This interface allows the Navigators to inform a client that the nodes have
 * changed and that they need to update the screen.
 */
public interface NodeNavigatorListener {

    /**
     *  Gets called when the node need to be laid out again because the
     *  layout changed.
     */
    public void nodeLayoutChanged();
}

