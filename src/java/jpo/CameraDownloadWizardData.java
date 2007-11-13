package jpo;

import java.awt.Component;
import java.io.File;
import java.util.Collection;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/*
CameraDownloadWizardData.java:  holds the data being shown in the Camera download wizard
 
Copyright (C) 2007  Richard Eigenmann.
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
 *  This class was designed to hold the data that the the {@link CameraDownloadWizard} needs.
 *  It is made accessible to all Wizard steps.
 *
 *  @author Richard Eigenmann
 *  @see CameraDownloadWizard
 */
public class CameraDownloadWizardData {
    
    
    /**
     *  The Camera from which to read the pictures.
     */
    private Camera c = null;
    /**
     *  sets the Camera from which the pictures are to be read. This is required.
     *  @param c The camera from which the pictures are to be read.
     *  @see #setCamera
     */
    public void setCamera( Camera c ) {
        this.c = c;
    }
    /**
     *  returns the Camera from which the pictures are to be read.
     *  @return the camera from which the pictures are to be read.
     *  @see #setCamera
     */
    public Camera getCamera() {
        return c;
    }
    
    /**
     * The Component to which the wizard should be set relative to. Is not required but visually
     * more appealing in set correctly.
     */
    private Component anchorComponent = null;;
    /**
     * The Component to which the wizard should be "set relative" to. Is not required but visually
     * more appealing in set correctly. If it is not set it will be null and the JVM will probably
     * set the Wizard to the coordinates 0.0.
     * @param  newAnchorComponent The component that the Wizard should be set relative to.
     */
    public void setAnchorFrame( Component newAnchorComponent ) {
        anchorComponent = newAnchorComponent;
    }
    /**
     * Returns the Component to which the wizard should be "set relative" to.
     * If it is not set it will return null and the JVM will probably
     * set the Wizard to the coordinates 0.0.
     * @return The component that the Wizard should be set relative to.
     */
    public Component getAnchorFrame() {
        return anchorComponent;
    }
    
    
    /**
     *  The Collection of new Pictures. This is built by the Wizard when it identifies
     *  the new pictures on the camera.
     */
    private Collection<File> newPictures = null;
    /**
     *  Sets the Collection of new Pictures. This is set by the Wizard when it identifies
     *  the new pictures on the camera.
     */
    public void setNewPictures( Collection<File> newPictures ) {
        this.newPictures = newPictures;
    }
    /**
     *  Returns the new pictures identified by the wizard.
     *  @return  The collection of new pictures.
     */
    public Collection<File>  getNewPictures() {
        return newPictures;
    }
    
    /**
     *  A Flag to indicate whether to copy or move the pictures. Default is set
     *  to copy.
     */
    private boolean copyMode = true;
    /**
     *  Sets whether to copy or move the pictures from the camera to the computer.
     *  This does not have to be set before calling the wizard . By default the
     *  operation is set to copy.
     *  @param  copyMode    Set to true if the wizard should copy the pictures, false if it should move them.
     */
    public void setCopyMode(boolean copyMode) {
        this.copyMode = copyMode;
    }
    /**
     *  Returns whether to copy or move the pictures in the Wizard.
     *  @return  true if the pictures should be copied, false if they should be moved.
     */
    public boolean getCopyMode() {
        return copyMode;
    }
    
    
    /**
     *  The description of the new Group. It defaults to "Download <<date>>"
     */
    private String newGroupDescription = "Download " + Tools.currentDate( Settings.addFromCameraDateFormat );
    /**
     *  Sets the name for the new group, if a new group should be created. If this is not set
     *  a default of "Download <<date>>" is proposed. Calling this method can change that name.
     *  If the (@link #createNewGroup} flag has been set a new group is created, otherwise this String
     *  will not be used.
     */
    public void setNewGroupDescription( String newGroupDescription ) {
        this.newGroupDescription = newGroupDescription;
    }
    /**
     *  Returns the description for the new group.
     *  @return the description of the new group.
     */
    public String getNewGroupDescription() {
        return newGroupDescription;
    }
    
    
    /**
     *  A Flag to indicate whether to create a new group or whether to add pictures to an existing group.
     */
    private boolean createNewGroup = false;
    /**
     *  Sets whether to create a new Group or not.
     *  @param  createNewGroup  If set to true a new group will be created, if false, not.
     */
    public void setCreateNewGroup(boolean createNewGroup) {
        this.createNewGroup = createNewGroup;
    }
    /**
     *  returns whether a new Group should be created.
     *  @return true if a new group should be created, false if not.
     *  @see #setCreateNewGroup
     */
    public boolean getCreateNewGroup() {
        return createNewGroup;
    }
    
    /**
     *  The TreeModel of the Collection. Since we only have on Collection at a time this is hard wired to
     *  the one collection. But if there were more than one collection you could specify which collection
     *  to add the pictures to here.
     */
    private TreeModel treeModel = Settings.pictureCollection.getTreeModel();
    /**
     *  Sets the TreeModel of the collection to which the pictures should be added.
     *  For the time being this need not be set since JPO only supports a single collection at any one time.
     */
    public void setTreeModel( TreeModel t ) {
        treeModel = t;
    }
    /**
     *  Returns the TreeModel of the collection for which the Wizard is running.
     */
    public TreeModel getTreeModel() {
        return treeModel;
    }
    
    
    /**
     *  The target node for the import operation
     */
    private SortableDefaultMutableTreeNode targetNode = null;
    /**
     *  Sets the target node for the picture download. Will be set by the wizard
     *  @see #setTargetNode
     */
    public SortableDefaultMutableTreeNode getTargetNode() {
        return targetNode;
    }
    /**
     *  Returns the target node of the picture download.
     *  @see #getTargetNode
     */
    public void setTargetNode(SortableDefaultMutableTreeNode targetNode) {
        this.targetNode = targetNode;
    }
    
    /**
     *   Holds the target directory where the images are to be copied to.
     *   It's a bit revolting to have such a GUI component in a data class but
     *   I can't guarantee an event will be fired and if no event is fired then
     *   How am I going to get the target directory out of the gui field
     *   into a variable when the user clicks next? (JWIZZ 1.4 doesn't have
     *   a listenable event.)
     */
    public final DirectoryChooser targetDir =
            new DirectoryChooser( Settings.jpoResources.getString("targetDirJLabel"),
            DirectoryChooser.DIR_MUST_BE_WRITABLE );
    
    /**
     *  An optional reference to the collection controller that will allow the wizard at the end to request the
     *  added groups to be shown.
     */
    private CollectionJTreeController collectionJTreeController = null;
    /**
     * returns the collectionJTreeController
     */
    public CollectionJTreeController getCollectionJTreeController() {
        return collectionJTreeController;
    }
    /**
     *sets the collectionJTreeController
     */
    public void setCollectionJTreeController(CollectionJTreeController collectionJTreeController) {
        this.collectionJTreeController = collectionJTreeController;
    }
    
}
