package org.jpo.datamodel;

/*
 NodeStatisticsBean.java: A class that holds the stats of the NodeStatistics

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
public class NodeStatisticsBean {

    private String numberOfNodes = "";

    /**
     * The number of nodes
     *
     * @return the number of nodes
     */
    public String getNumberOfNodes() {
        return numberOfNodes;
    }

    /**
     * Remembers the number of nodes
     *
     * @param numberOfNodes the number of nodes
     */
    public void setNumberOfNodes( String numberOfNodes ) {
        this.numberOfNodes = numberOfNodes;
    }

    private String numberOfGroups = "";

    /**
     * Returns the number of groups
     *
     * @return the number of groups
     */
    public String getNumberOfGroups() {
        return numberOfGroups;
    }

    /**
     * Remembers the number of group nodes
     *
     * @param numberOfGroups the number of group nodes
     */
    public void setNumberOfGroups( String numberOfGroups ) {
        this.numberOfGroups = numberOfGroups;
    }

    private String numberOfPictures = "";

    /**
     * Returns the number of pictures
     *
     * @return the number of pictures
     */
    public String getNumberOfPictures() {
        return numberOfPictures;
    }

    /**
     * Sets the number of pictures
     *
     * @param numberOfPictures the number of pictures
     */
    public void setNumberOfPictures( String numberOfPictures ) {
        this.numberOfPictures = numberOfPictures;
    }

    private String sizeOfPictures = "";

    /**
     * Returns a string with the memory occupied by the pictures
     *
     * @return a string with the bytes used by the pictures
     */
    public String getSizeOfPictures() {
        return sizeOfPictures;
    }

    /**
     * Sets the sie of Pictures string
     *
     * @param sizeOfPictures the new string
     */
    public void setSizeOfPictures( String sizeOfPictures ) {
        this.sizeOfPictures = sizeOfPictures;
    }

    private String freeMemory = "";

    /**
     * Returns the free memory
     *
     * @return the free memory string
     */
    public String getFreeMemory() {
        return freeMemory;
    }

    /**
     * Stores the free memory string
     *
     * @param freeMemory the free memory string
     */
    public void setFreeMemory( String freeMemory ) {
        this.freeMemory = freeMemory;
    }

    private String queueCount = "";

    /**
     * Returns the number of thumbnails on the queue
     *
     * @return the number of thumbnails on the queue
     */
    public String getQueueCount() {
        return queueCount;
    }

    /**
     * Stores the number of thumbnails on the queue
     *
     * @param queueCount the number of thumbnails on the queue
     */
    public void setQueueCount( String queueCount ) {
        this.queueCount = queueCount;
    }
    private String selectedCount = "";

    /**
     * Returns the number of selected nodes
     *
     * @return the number of selected nodes
     */
    public String getSelectedCount() {
        return selectedCount;
    }

    /**
     * Stores the number of selected nodes
     *
     * @param selectedCount the number of selected nodes
     */
    public void setSelectedCount( String selectedCount ) {
        this.selectedCount = selectedCount;
    }

}
