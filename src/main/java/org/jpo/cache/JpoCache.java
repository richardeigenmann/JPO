package org.jpo.cache;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.apache.commons.jcs.engine.control.CompositeCacheManager;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.ScalablePicture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 Copyright (C) 2014 - 2020 Richard Eigenmann.
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
 * Cache for the Jpo application
 *
 * @author Richard Eigenmann
 */
public class JpoCache {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(JpoCache.class.getName());

    private static final String HIGHRES_CACHE_REGION_NAME = "highresCache";
    private static final String THUMBNAIL_CACHE_REGION_NAME = "thumbnailCache";

    /**
     * Returns the instance of the JpoCache singleton
     *
     * @return the instance of the cache object
     */
    public static JpoCache getInstance() {
        return JpoCacheHolder.INSTANCE;

    }

    private CacheAccess<File, ImageBytes> highresMemoryCache;
    private CacheAccess<String, ImageBytes> thumbnailMemoryAndDiskCache;
    /**
     * The dimension for the group thumbnail
     */
    private Dimension groupThumbnailDimension;

    private JpoCache() {
        LOGGER.fine("Creating JpoCache");
        CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
        Properties props = loadProperties();
        ccm.configure(props);

        try {
            highresMemoryCache = JCS.getInstance(HIGHRES_CACHE_REGION_NAME);
            thumbnailMemoryAndDiskCache = JCS.getInstance(THUMBNAIL_CACHE_REGION_NAME);
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
    }

    /**
     * Loads the properties from the cache .ccf file in the bundle
     *
     * @return The properties object created from the ccf file
     */
    public static Properties loadProperties() {
        final String CACHE_DEFINITION_FILE = "cache.ccf";
        URL ccfUrl = JpoCache.class.getClassLoader().getResource(CACHE_DEFINITION_FILE);
        if (ccfUrl == null) {
            LOGGER.log(Level.SEVERE,"Classloader didn''t find file {0}", CACHE_DEFINITION_FILE);
            return null;
        } else {
            LOGGER.log(Level.FINE,"Cache definition file found at: {0}", ccfUrl);
        }

        Properties props = new Properties();
        try (final InputStream inStream = ccfUrl.openStream();) {
            props.load(inStream);
        } catch (IOException e) {
            LOGGER.severe("Failed to load " + CACHE_DEFINITION_FILE + "IOException: " + e.getLocalizedMessage());
            return null;
        }

        LOGGER.log(Level.FINE,"setting jcs.auxiliary.DC.attributes.DiskPath to: {0}", Settings.getThumbnailCacheDirectory());
        props.setProperty("jcs.auxiliary.DC.attributes.DiskPath", Settings.getThumbnailCacheDirectory());

        return props;
    }

    /**
     * Method to properly shut down the cache
     */
    public void shutdown() {
        try {
            CompositeCacheManager.getInstance().shutDown();
        } catch (CacheException ex) {
            Logger.getLogger(JpoCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the highres image bytes from the cache or disk
     *
     * @param file The file of the image
     * @return and ImageBytes object
     * @throws IOException if something went wrong
     */
    public ImageBytes getHighresImageBytes(File file) throws IOException {
        ImageBytes imageBytes = highresMemoryCache.get(file);
        if (imageBytes != null) {
            try {
                FileTime lastModification = (Files.getLastModifiedTime(file.toPath()));
                if (lastModification.compareTo(imageBytes.getLastModification()) > 0) {
                    imageBytes = new ImageBytes(IOUtils.toByteArray(new BufferedInputStream(new FileInputStream(file))));
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }
        } else {
            imageBytes = new ImageBytes(IOUtils.toByteArray(new BufferedInputStream(new FileInputStream(file))));
            try {
                highresMemoryCache.put(file, imageBytes);
            } catch (NullPointerException | CacheException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }
        }
        return imageBytes;
    }

    /**
     * Returns an ImageBytes object with thumbnail image data for the supplied
     * file
     *
     * @param file     The file of the highres picture for which a thumbnail is needed
     * @param rotation The rotation in degrees (0..360) for the thumbnail
     * @param size     The maximum size of the thumbnail
     * @return The ImageBytes of the thumbnail
     */
    public ImageBytes getThumbnailImageBytes(File file, double rotation, Dimension size) {
        int maxWidth = size.width;
        int maxHeight = size.height;
        String key = String.format("%s-%fdeg-w:%dpx-h:%dpx", file, rotation, maxWidth, maxHeight);
        ImageBytes imageBytes = thumbnailMemoryAndDiskCache.get(key);
        if (imageBytes != null) {
            try {
                Path imagePath = Paths.get(file.toURI());
                FileTime lastModification = (Files.getLastModifiedTime(imagePath));
                if (lastModification.compareTo(imageBytes.getLastModification()) > 0) {
                    imageBytes = createThumbnailAndStoreInCache(key, file, rotation, maxWidth, maxHeight);

                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }

        } else {
            imageBytes = createThumbnailAndStoreInCache(key, file, rotation, maxWidth, maxHeight);
        }
        return imageBytes;
    }

    /**
     * Creates a thumbnail and stores it in the cache
     *
     * @param key       the key to store it in the cache
     * @param imageFile The file of the picture
     * @param rotation  the rotation to apply
     * @param maxWidth  the maximum width
     * @param maxHeight the maximum height
     * @return the thumbnail
     */
    private ImageBytes createThumbnailAndStoreInCache(String key, File imageFile, double rotation, int maxWidth, int maxHeight) {
        ImageBytes imageBytes = createThumbnail(imageFile, rotation, maxWidth, maxHeight);
        try {
            thumbnailMemoryAndDiskCache.put(key, imageBytes);
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
        return imageBytes;
    }

    /**
     * Creates a thumbnail
     *
     * @param file      The the picture file
     * @param rotation  the rotation to apply
     * @param maxWidth  the maximum width
     * @param maxHeight the maximum height
     * @return the thumbnail
     */
    private ImageBytes createThumbnail(final File file, final double rotation, final int maxWidth, final int maxHeight) {
        final Dimension maxDimension = new Dimension(maxWidth, maxHeight);

        // create a new thumbnail from the highres
        final ScalablePicture scalablePicture = new ScalablePicture();
        if (Settings.isThumbnailFastScale()) {
            scalablePicture.setFastScale();
        } else {
            scalablePicture.setQualityScale();
        }
        scalablePicture.setScaleSize(maxDimension);
        scalablePicture.loadPictureImd(file, rotation);
        if (scalablePicture.getSourcePicture() == null) {
            return null;
        }

        scalablePicture.scalePicture();

        if (scalablePicture.getScaledPicture() == null) {
            return null;
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scalablePicture.writeScaledJpg(bos);
        final ImageBytes imageBytes = new ImageBytes(bos.toByteArray());

        try {
            final Path imagePath = Paths.get(file.toURI());
            imageBytes.setLastModification(Files.getLastModifiedTime(imagePath));

        } catch (final IOException ex) {
            Logger.getLogger(JpoCache.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return imageBytes;
    }

    /**
     * Returns the Dimension of the icon_folder_large.jpg image and if there is
     * an ioerror the maximum size of the thumbnails.
     *
     * @return the dimension for the icon folder image
     */
    private Dimension getThumbnailDimensions() {
        if (groupThumbnailDimension == null) {
            BufferedImage groupThumbnail;
            try (BufferedInputStream bis = new BufferedInputStream(JpoCache.class.getClassLoader().getResourceAsStream("org/jpo/images/icon_folder_large.jpg"))) {
                groupThumbnail = ImageIO.read(bis);
                groupThumbnailDimension = new Dimension(groupThumbnail.getWidth(), groupThumbnail.getHeight());

            } catch (IOException ex) {
                Logger.getLogger(JpoCache.class
                        .getName()).log(Level.SEVERE, null, ex);
                groupThumbnailDimension = new Dimension(Settings.getThumbnailSize(), Settings.getThumbnailSize());
            }
        }
        return groupThumbnailDimension;
    }

    /**
     * Returns a thumbnail for a group of pictures
     *
     * @param childPictureNodes The pictures that make up the group
     * @return The thumbnail
     * @throws IOException if something went wrong
     */
    public ImageBytes getGroupThumbnailImageBytes(final List<SortableDefaultMutableTreeNode> childPictureNodes) throws IOException {
        final int leftMargin = 15;
        final int margin = 10;
        final int topMargin = 65;
        final int horizontalPics = (getThumbnailDimensions().width - leftMargin) / (Settings.miniThumbnailSize.width + margin);
        final int verticalPics = (getThumbnailDimensions().height - topMargin) / (Settings.miniThumbnailSize.height + margin);
        final int numberOfPics = horizontalPics * verticalPics;

        final StringBuilder sb = new StringBuilder("Group-");
        for (int i = 0; (i < numberOfPics) && (i < childPictureNodes.size()); i++) {
            PictureInfo pictureInfo = (PictureInfo) childPictureNodes.get(i).getUserObject();
            sb.append(String.format("%s-%fdeg-", pictureInfo.getImageFile().toString(), pictureInfo.getRotation()));
        }

        final String key = sb.toString();
        ImageBytes imageBytes = thumbnailMemoryAndDiskCache.get(key);

        if (imageBytes != null) {
            try {
                FileTime thumbnailLastModification = imageBytes.getLastModification();

                boolean thumbnailNeedsRefresh = false;
                for (int i = 0; (i < numberOfPics) && (i < childPictureNodes.size()); i++) {
                    final PictureInfo pictureInfo = (PictureInfo) childPictureNodes.get(i).getUserObject();
                    final Path imagePath = Paths.get(pictureInfo.getImageURIOrNull());
                    final FileTime lastModification = (Files.getLastModifiedTime(imagePath));
                    if (lastModification.compareTo(thumbnailLastModification) > 0) {
                        thumbnailNeedsRefresh = true;
                        break;
                    }
                }
                if (thumbnailNeedsRefresh) {
                    imageBytes = createGroupThumbnailAndStoreInCache(key, numberOfPics, childPictureNodes);

                }
            } catch (final IOException ex) {
                throw (ex);
            }
        } else {
            imageBytes = createGroupThumbnailAndStoreInCache(key, numberOfPics, childPictureNodes);
        }
        return imageBytes;
    }

    /**
     * Create a Group ThumbnailController by loading the nodes component images
     * and creating a folder icon with embedded images
     *
     * @param key               The key for the image in the cache
     * @param numberOfPics      the number of pictures to include
     * @param childPictureNodes The nodes from which to create the mini
     *                          thumbnails
     * @return the image
     * @throws IOException when things go wrong
     */
    private ImageBytes createGroupThumbnailAndStoreInCache(
            final String key,
            final int numberOfPics,
            final List<SortableDefaultMutableTreeNode> childPictureNodes)
            throws IOException {
        final BufferedImage groupThumbnail = ImageIO.read(new BufferedInputStream(JpoCache.class.getClassLoader().getResourceAsStream("icon_folder_large.jpg")));
        final Graphics2D groupThumbnailGraphics = groupThumbnail.createGraphics();

        int leftMargin = 15;
        int margin = 10;
        int topMargin = 65;
        int horizontalPics = (groupThumbnail.getWidth() - leftMargin) / (Settings.miniThumbnailSize.width + margin);

        final ScalablePicture scalablePicture = new ScalablePicture();
        FileTime mostRecentPictureModification = FileTime.fromMillis(0);
        for (int picsProcessed = 0; (picsProcessed < numberOfPics) && (picsProcessed < childPictureNodes.size()); picsProcessed++) {
            final PictureInfo pi = (PictureInfo) childPictureNodes.get(picsProcessed).getUserObject();

            final Path imagePath = Paths.get(pi.getImageURIOrNull());
            final FileTime lastModification = (Files.getLastModifiedTime(imagePath));
            if (lastModification.compareTo(mostRecentPictureModification) > 0) {
                mostRecentPictureModification = lastModification;
            }

            int x = margin + ((picsProcessed % horizontalPics) * (Settings.miniThumbnailSize.width + margin));
            final int yPos = (int) Math.round((picsProcessed / (double) horizontalPics) - 0.5f);
            int y = topMargin + (yPos * (Settings.miniThumbnailSize.height + margin));

            scalablePicture.loadPictureImd(pi.getImageFile(), pi.getRotation());

            scalablePicture.setScaleSize(Settings.miniThumbnailSize);
            scalablePicture.scalePicture();
            x += (Settings.miniThumbnailSize.width - scalablePicture.getScaledWidth()) / 2;
            y += Settings.miniThumbnailSize.height - scalablePicture.getScaledHeight();

            groupThumbnailGraphics.drawImage(scalablePicture.getScaledPicture(), x, y, null);
        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ScalablePicture.writeJpg(bos, groupThumbnail, 0.8f);
        final ImageBytes imageBytes = new ImageBytes(bos.toByteArray());

        imageBytes.setLastModification(mostRecentPictureModification);

        try {
            thumbnailMemoryAndDiskCache.put(key, imageBytes);
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }

        return imageBytes;
    }

    /**
     * Returns a text from the JCS with statistics on the cache
     *
     * @return a test with statistics from the cache
     */
    public String getHighresCacheStats() {
        return highresMemoryCache.getStats();
    }

    /**
     * Returns a text from the JCS with statistics on the cache
     *
     * @return a test with statistics from the cache
     */
    public String getThumbnailCacheStats() {
        return thumbnailMemoryAndDiskCache.getStats();
    }

    /**
     * Clears the highres image cache
     */
    public void clearHighresCache() {
        try {
            highresMemoryCache.clear();
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
    }

    /**
     * Clears the thumbnail image cache
     */
    public void clearThumbnailCache() {
        try {
            thumbnailMemoryAndDiskCache.clear();
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());

        }
    }

    /**
     * Singleton
     */
    private static class JpoCacheHolder {

        private static final JpoCache INSTANCE = new JpoCache();
    }

}
