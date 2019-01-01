package jpo.dataModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.commons.text.StringEscapeUtils;


/*
 GroupInfo.java:  definitions for the group objects

 Copyright (C) 2002 - 2018 Richard Eigenmann.
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
 * A class which holds information about the group and has been given the
 * intelligence of how to write itself and it's pictures to an html document.
 * <p>
 * This class must implement the Serializable interface or Drag and Drop will
 * not work.
 *
 * @see PictureInfo
 */
public class GroupInfo implements Serializable {

    /**
     * Keep serialisation happy
     */
    private static final long serialVersionUID = 1;

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( GroupInfo.class.getName() );

    /**
     * The description of the GroupInfo.
     *
     */
    private StringBuffer groupName = new StringBuffer();

    /**
     * Constructor to create a new GroupInfo object.
     *
     * @param	description	The description of the Group
     *
     */
    public GroupInfo( String description ) {
        setGroupName( description );
    }

    /**
     * toString method that returns the description of the group
     *
     * @return description of the group
     */
    @Override
    public String toString() {
        return groupName.toString();
    }

    /**
     * Returns the description of the group.
     *
     * @return	The description of the Group.
     * @see #setGroupName
     *
     */
    public String getGroupName() {
        return groupName.toString();
    }

    /**
     * Returns the description of the group with the HTML characters safely
     * escaped.
     *
     * @return	The description of the Group.
     * @see #getGroupName
     *
     */
    public String getGroupNameHtml() {
        return StringEscapeUtils.escapeHtml4( groupName.toString() );
    }

    /**
     * Set name of the GroupInfo
     *
     * @param name The new description of the GroupInfo
     * @see #getGroupName
     *
     */
    public void setGroupName( String name ) {
        if ( !groupName.toString().equals( name ) ) {
            groupName = new StringBuffer( name );
            sendGroupNameChangedEvent();
        }

    }

    /**
     * Creates a GroupInfoChangedEvent and sends it to inform listening objects
     * that the description was updated.
     */
    private void sendGroupNameChangedEvent() {
        LOGGER.fine( "preparing to send GroupName changed event" );
        if ( ( Settings.getPictureCollection() != null ) && ( Settings.getPictureCollection().getSendModelUpdates() ) ) {
            GroupInfoChangeEvent gice = new GroupInfoChangeEvent( this );
            gice.setGroupNameChanged();
            sendGroupInfoChangedEvent( gice );
            LOGGER.fine( "sent description changed event" );
            Settings.getPictureCollection().setUnsavedUpdates();
        }
    }

    /**
     * this method writes all attributes of the picture in the JPO xml data
     * format with the highres and lowres locations passed in as parameters.
     * This became necessary because when the XmlDistiller copies the pictures
     * to a new location we don't want to write the URLs of the original
     * pictures whilst all other attributes are retained.
     *
     * @param out	The Bufferer Writer receiving the xml data
     * @param rootNode The starting node
     * @param protection Whether the collection is protected or not
     * @throws IOException If there was an IO error
     */
    public void dumpToXml( BufferedWriter out, boolean rootNode, boolean protection )
            throws IOException {

        if ( rootNode ) {
            out.write( "<collection collection_name=\""
                    + StringEscapeUtils.escapeXml11( getGroupName() )
                    + "\" collection_created=\""
                    + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() )
                    + "\""
                    + ( protection ? " collection_protected=\"No\"" : " collection_protected=\"Yes\"" ) );
        } else {
            out.write( "<group group_name=\"" + StringEscapeUtils.escapeXml11( getGroupName() ) + "\"" );
        }
        out.newLine();

        out.write( ">" );
        out.newLine();
    }

    /**
     * Closes the xml output
     *
     * @param out The output stream
     * @param rootNode true if this si the root node
     * @throws IOException if something went wrong
     */
    public void endGroupXML( BufferedWriter out, boolean rootNode )
            throws IOException {

        if ( !rootNode ) {  // if it is root Node then the XmlDistiller adds the categories and end collection tag.
            out.write( "</group>" );
        }
        out.newLine();
    }

    /**
     * Creates a GroupInfoChangedEvent and sends it to inform listening objects
     * that the node was selected. Strictly speaking this is not a GroupInfo
     * level event but a node level event. However, because I have the
     * GroupInfoChangeEvent structure in place this is a good place to put this
     * notification.
     */
    public void sendWasSelectedEvent() {
        GroupInfoChangeEvent gice = new GroupInfoChangeEvent( this );
        gice.setWasSelected();
        sendGroupInfoChangedEvent( gice );
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the node was unselected. Strictly speaking this is not a PictureInfo
     * level event but a node level event. However, because I have the
     * PictureInfoChangeEvent structure in place this is a good place to put
     * this notification.
     */
    public void sendWasUnselectedEvent() {
        LOGGER.fine( "Sending unselected event" );
        GroupInfoChangeEvent gice = new GroupInfoChangeEvent( this );
        gice.setWasUnselected();
        sendGroupInfoChangedEvent( gice );
    }

    /**
     * The listeners to notify about changes on this GroupInfo object
     */
    private final transient Set<GroupInfoChangeListener> groupInfoListeners = Collections.synchronizedSet(new HashSet<>() );

    /**
     * Adds a change listener
     *
     * @param groupInfoChangeListener	The object that will receive
     * notifications.
     */
    public void addGroupInfoChangeListener( GroupInfoChangeListener groupInfoChangeListener ) {
        groupInfoListeners.add( groupInfoChangeListener );
    }

    /**
     * Removes the change listener
     *
     * @param groupInfoChangeListener	The listener that doesn't want to
     * notifications any more.
     */
    public void removeGroupInfoChangeListener( GroupInfoChangeListener groupInfoChangeListener ) {
        groupInfoListeners.remove( groupInfoChangeListener );
    }

    /**
     * Send PictureInfoChangeEvents.
     *
     * @param groupInfoChangeEvent The Event we want to notify.
     */
    private void sendGroupInfoChangedEvent( GroupInfoChangeEvent groupInfoChangeEvent ) {
        if ( Settings.getPictureCollection().getSendModelUpdates() ) {
            synchronized ( groupInfoListeners ) {
                groupInfoListeners.stream().forEach( groupInfoChangeListener
                        -> groupInfoChangeListener.groupInfoChangeEvent( groupInfoChangeEvent )
                );
            }
        }
    }

    /**
     * Returns a new GroupInfo object which is identical to the current one.
     *
     * @return a clone of the current PictureInfo object.
     */
    public GroupInfo getClone() {
        return new GroupInfo( this.getGroupName() );
    }
}
