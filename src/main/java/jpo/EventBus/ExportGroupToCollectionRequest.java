package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

import java.io.File;

/*
 Copyright (C) 2019 Richard Eigenmann.
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
 * Request to fulfill the ExportGroupToCollectionRequest
 *
 * @author Richard Eigenmann
 */
public class ExportGroupToCollectionRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final File targetFile;
    private final boolean exportPictures;

    /**
     * Request to fulfill the ExportGroupToCollectionRequest
     *
     * @param node The node for which the user would like the dialog to be done
     */
    public ExportGroupToCollectionRequest(SortableDefaultMutableTreeNode node, File targetFile, boolean exportPictures) {
        this.node = node;
        this.targetFile = targetFile;
        this.exportPictures = exportPictures;
    }

    /**
     * The node to be exported
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * Returns the target directory
     * @return the target directory
     */
    public File getTargetFile() {
        return targetFile;
    }

    /**
     * Returns if pictures should be exported.
     * @return True if pictures should be exported
     */
    public boolean getExportPictures() {
        return exportPictures;
    }

}
