package org.jpo.gui;

import org.jpo.datamodel.Camera;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Settings.FieldCodes;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;

import javax.swing.tree.TreeModel;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.logging.Logger;

/*
 CameraDownloadWizardData.java:  holds the data being shown in the Camera download wizard

 Copyright (C) 2007 - 2014  Richard Eigenmann.
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
 * This class was designed to hold the data that the the
 * {@link CameraDownloadWizard} needs. It is made accessible to all Wizard
 * steps.
 *
 * @author Richard Eigenmann
 * @see CameraDownloadWizard
 */
public class CameraDownloadWizardData {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraDownloadWizardData.class.getName() );

    /**
     * The Camera from which to read the pictures.
     */
    private Camera camera;

    /**
     * sets the Camera from which the pictures are to be read. This is required.
     *
     * @param camera The camera from which the pictures are to be read.
     * @see #setCamera
     */
    public void setCamera( Camera camera ) {
        this.camera = camera;
    }

    /**
     * returns the Camera from which the pictures are to be read.
     *
     * @return the camera from which the pictures are to be read.
     * @see #setCamera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * The Component to which the wizard should be "set relative" to. Is not
     * required but visually more appealing in set correctly.
     */
    private Component anchorComponent;

    /**
     * The Component to which the wizard should be "set relative" to. Is not
     * required but visually more appealing in set correctly. If it is not set
     * it will be null and the JVM will probably set the Wizard to the
     * coordinates 0/0.
     *
     * @param newAnchorComponent The component that the Wizard should be set
     * relative to.
     */
    public void setAnchorFrame( Component newAnchorComponent ) {
        anchorComponent = newAnchorComponent;
    }

    /**
     * Returns the Component to which the wizard should be "set relative" to. If
     * it is not set it will return null and the JVM will probably set the
     * Wizard to the coordinates 0/0.
     *
     * @return The component that the Wizard should be set relative to.
     */
    public Component getAnchorFrame() {
        return anchorComponent;
    }

    /**
     * The Collection of new Pictures. This is built by the Wizard when it
     * identifies the new pictures on the camera.
     */
    private Collection<File> newPictures;

    /**
     * Sets the Collection of new Pictures. This is set by the Wizard when it
     * identifies the new pictures on the camera.
     *
     * @param newPictures A Collection of picture files
     */
    public void setNewPictures( Collection<File> newPictures ) {
        this.newPictures = newPictures;
    }

    /**
     * Returns the new pictures identified by the wizard.
     *
     * @return The collection of new picture files.
     */
    public Collection<File> getNewPictures() {
        return newPictures;
    }

    /**
     * A Flag to indicate whether to copy or move the pictures from the camera
     * to the hard disk. Default is set to copy.
     */
    private boolean copyMode = Settings.lastCameraWizardCopyMode;

    /**
     * Sets whether to copy or move the pictures from the camera to the
     * computer. This does not have to be set before calling the wizard. By
     * default the operation is set to copy.
     *
     * @param copyMode Set to true if the wizard should copy the pictures, false
     * if it should move them.
     */
    public void setCopyMode( boolean copyMode ) {
        this.copyMode = copyMode;
    }

    /**
     * Returns whether to copy or move the pictures in the Wizard.
     *
     * @return true if the pictures should be copied, false if they should be
     * moved.
     */
    public boolean getCopyMode() {
        return copyMode;
    }

    /**
     * The description of the new Group. It defaults to "Download &lt;&lt;date&gt;&gt;"
     */
    private String newGroupDescription = "Download " + Tools.currentDate( Settings.ADD_FROM_CAMERA_DATE_FORMAT);

    /**
     * Sets the name for the new group, if a new group should be created. If
     * this is not set a default of "Download date" is proposed. Calling
     * this method can change that name. If the (@link #shouldCreateNewGroup}
     * flag has been set a new group is created, otherwise this String will not
     * be used.
     *
     * @param newGroupDescription name for the new Group
     */
    public void setNewGroupDescription( String newGroupDescription ) {
        this.newGroupDescription = newGroupDescription;
    }

    /**
     * Returns the description for the new group.
     *
     * @return the description of the new group.
     */
    public String getNewGroupDescription() {
        return newGroupDescription;
    }

    /**
     * A Flag to indicate whether to create a new group or whether to add
     * pictures to an existing group.
     */
    private boolean shouldCreateNewGroup;  // default is false

    /**
     * Sets whether to create a new Group or not.
     *
     * @param createNewGroup set to true to create a new group node or false if
     * not desired
     */
    public void setShouldCreateNewGroup( boolean createNewGroup ) {
        this.shouldCreateNewGroup = createNewGroup;
    }

    /**
     * returns whether a new Group should be created.
     *
     * @return true if a new group should be created, false if not.
     * @see #setShouldCreateNewGroup
     */
    public boolean getShouldCreateNewGroup() {
        return shouldCreateNewGroup;
    }

    /**
     * The TreeModel of the Collection. Since we only have one Collection at a
     * time this is hard wired to the one collection. But if there were more
     * than one collection you could specify which collection to add the
     * pictures to here.
     */
    private final TreeModel treeModel = Settings.getPictureCollection().getTreeModel();

    /**
     * Returns the TreeModel of the collection for which the Wizard is running.
     *
     * @return the treemodel for the collection which is used by the wizard
     */
    public TreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * The target node for the import operation
     */
    private SortableDefaultMutableTreeNode targetNode;

    /**
     * Sets the target node for the picture download. Will be set by the wizard
     *
     * @return the target node for the picture being added
     * @see #setTargetNode
     */
    public SortableDefaultMutableTreeNode getTargetNode() {
        return targetNode;
    }

    /**
     * Returns the target node of the picture download.
     *
     * @param targetNode The target node for the import operation
     * @see #getTargetNode
     */
    public void setTargetNode( SortableDefaultMutableTreeNode targetNode ) {
        LOGGER.fine( String.format( "Setting target node to: %s", targetNode == null ? "null" : targetNode.toString() ) );
        this.targetNode = targetNode;
    }

    /**
     * Which field to sort on
     */
    private FieldCodes sortCode = Settings.lastSortChoice;

    /**
     * Sets the sort Code choice
     *
     * @param sortCode the new sort code
     */
    public void setSortCode( FieldCodes sortCode ) {
        this.sortCode = sortCode;
    }

    /**
     * Returns the sort Code choice
     *
     * @return the sort code
     */
    public FieldCodes getSortCode() {
        return sortCode;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * Holds the target directory where the images are to be copied to.
     */
    private File targetDir;

}
