package org.jpo.datamodel;

/*
 Copyright (C) 2025 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

import java.awt.*;

public class CacheSettings {

    private CacheSettings(){
        // Utility Class
    }

    /**
     * Setting for the width of the thumbnails. Set by default to 350 pixels.
     */
    private static int thumbnailSize = 350;

    public static int getThumbnailSize() {
        return thumbnailSize;
    }

    public static void setThumbnailSize(int newThumbnailSize) {
        thumbnailSize = newThumbnailSize;
    }

    /**
     * the dimension of mini thumbnails in the group folders
     */
    public static final Dimension miniThumbnailSize = new Dimension(100, 75);

    private static String thumbnailCacheDirectory = System.getProperty("java.io.tmpdir")
            + System.getProperty("file.separator")
            + "Jpo-Thumbnail-Cache";


    public static String getThumbnailCacheDirectory() {
        return thumbnailCacheDirectory;
    }

    public static void setThumbnailCacheDirectory(String newThumbnailCacheDirectory) {
        thumbnailCacheDirectory=newThumbnailCacheDirectory;
    }


    /**
     * true when thumbnails are supposed to scale fast
     */
    private static boolean thumbnailFastScale = true;


    /**
     * returns if thumbnails should be rendered faster instead of better quality
     *
     * @return true if speed is desired
     */
    public static boolean isThumbnailFastScale() {
        return thumbnailFastScale;
    }

    /**
     * Stores the default choice for fast scaling
     *
     * @param newThumbnailFastScale true is fast scaling should be used false if not.
     */
    public static void setThumbnailFastScale(boolean newThumbnailFastScale) {
        thumbnailFastScale = newThumbnailFastScale;
    }

    /**
     * a flag that indicates that small images should not be enlarged
     */
    private static boolean dontEnlargeSmallImages = true;

    /**
     * Returns whether to enlarge small images to screen size or not
     *
     * @return whether to enlarge images that are smaller than the screen
     */
    public static boolean isDontEnlargeSmallImages() {
        return dontEnlargeSmallImages;
    }

    public static void setDontEnlargeSmallImages(boolean newDontEnlargeSmallImages) {
        dontEnlargeSmallImages = newDontEnlargeSmallImages;
    }


}
