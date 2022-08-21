package org.jpo.cache;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jcs3.JCS;
import org.apache.commons.jcs3.access.CacheAccess;
import org.apache.commons.jcs3.access.exception.CacheException;
import org.apache.commons.jcs3.engine.control.CompositeCacheManager;
import org.jetbrains.annotations.TestOnly;
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
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 Copyright (C) 2014 - 2022 Richard Eigenmann.
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

/**
 * Cache for the Jpo application
 *
 * @author Richard Eigenmann
 */
public class JpoCache {

    private JpoCache() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(JpoCache.class.getName());

    private static final String HIGHRES_CACHE_REGION_NAME = "highresCache";
    private static final String THUMBNAIL_CACHE_REGION_NAME = "thumbnailCache";
    /**
     * The dimension for the group thumbnail i.e. the dimension of the icon_folder_large.jpg image. If there is
     * an ioerror the maximum size of the thumbnails.
     */
    private static Dimension groupThumbnailDimension;

    static {
        try (final BufferedInputStream bis = new BufferedInputStream(JpoCache.class.getClassLoader().getResourceAsStream("icon_folder_large.jpg"))) {
            final BufferedImage groupThumbnail = ImageIO.read(bis);
            groupThumbnailDimension = new Dimension(groupThumbnail.getWidth(), groupThumbnail.getHeight());

        } catch (final IOException | NullPointerException ex) {
            LOGGER.log(Level.SEVERE, "Exception while statically loading it icon_folder_large.jpg: {0}", ex.getMessage());
            groupThumbnailDimension = new Dimension(Settings.getThumbnailSize(), Settings.getThumbnailSize());
        }
    }

    private static CacheAccess<File, ImageBytes> highresMemoryCache;
    private static CacheAccess<String, ImageBytes> thumbnailMemoryAndDiskCache;

    static {
        LOGGER.info("Creating JpoCache");
        final var ccm = CompositeCacheManager.getUnconfiguredInstance();
        final var props = loadProperties();
        ccm.configure(props);

        try {
            highresMemoryCache = JCS.getInstance(HIGHRES_CACHE_REGION_NAME);
            thumbnailMemoryAndDiskCache = JCS.getInstance(THUMBNAIL_CACHE_REGION_NAME);
        } catch (final CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
    }

    /**
     * Returns the instance of the JpoCache singleton
     *
     * @return the instance of the cache object
     * <p>
     * public static JpoCache getInstance() {
     * LOGGER.log(Level.INFO, "Returning an instance of the cache");
     * //return JpoCacheHolder.INSTANCE;
     * return this;
     * }
     */

    @TestOnly
    public static Dimension getGroupThumbnailDimension() {
        return groupThumbnailDimension;
    }

    /**
     * Loads the properties from the cache.ccf file in the bundle
     *
     * @return The properties object created from the ccf file
     */
    public static Properties loadProperties() {
        final var CACHE_DEFINITION_FILE = "cache.ccf";
        final URL ccfUrl;
        final var properties = new Properties();
        try {
            ccfUrl = ClassLoader.getSystemResources(CACHE_DEFINITION_FILE).nextElement();
            LOGGER.log(Level.FINE, "Cache definition file found at: {0}", ccfUrl);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Classloader didn''t find file {0}", CACHE_DEFINITION_FILE);
            return properties;
        }

        try (final var inStream = ccfUrl.openStream();) {
            properties.load(inStream);
        } catch (final IOException e) {
            LOGGER.severe("Failed to load " + CACHE_DEFINITION_FILE + "IOException: " + e.getLocalizedMessage());
            return properties;
        }

        LOGGER.log(Level.FINE, "setting jcs.auxiliary.DC.attributes.DiskPath to: {0}", Settings.getThumbnailCacheDirectory());
        properties.setProperty("jcs.auxiliary.DC.attributes.DiskPath", Settings.getThumbnailCacheDirectory());

        return properties;
    }

    /**
     * Method to properly shut down the cache
     */
    public static void shutdown() {
        try {
            CompositeCacheManager.getInstance().shutDown();
        } catch (final CacheException ex) {
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
    public static ImageBytes getHighresImageBytes(final File file) throws IOException {
        LOGGER.log(Level.FINE, "Hitting cache for file {0}", file);
        var imageBytes = highresMemoryCache.get(file);
        if (imageBytes != null) {
            imageBytes.setRetrievedFromCache(true);
            try {
                final var lastModification = (Files.getLastModifiedTime(file.toPath()));
                if (lastModification.compareTo(imageBytes.getLastModification()) > 0) {
                    imageBytes = getHighresImageBytesFromFile(file);
                }
            } catch (final IOException ex) {
                LOGGER.severe(ex.getLocalizedMessage());
            }
        } else {
            imageBytes = getHighresImageBytesFromFile(file);
        }
        LOGGER.log(Level.FINE, "Returning {0} bytes from file {1} loaded from cache: {2}", new Object[]{imageBytes.getBytes().length, file, imageBytes.isRetrievedFromCache()});
        return imageBytes;
    }

    /**
     * Loads the image bytes from the supplied file and stores the loaded image in the highres cache
     *
     * @param file
     * @return
     */
    private static ImageBytes getHighresImageBytesFromFile(final File file) throws IOException {
        LOGGER.log(Level.FINE, "Loading file from disk file {0}", file);
        final var imageBytes = new ImageBytes(IOUtils.toByteArray(new BufferedInputStream(new FileInputStream(file))));
        imageBytes.setRetrievedFromCache(false);
        imageBytes.setLastModification(Files.getLastModifiedTime(file.toPath()));
        storeInHighresCache(file, imageBytes);
        return imageBytes;
    }

    /**
     * Stores the highres imageBytes object into the highres cache
     *
     * @param file       The file that was loaded
     * @param imageBytes the ImageBytes object to store
     */
    private static void storeInHighresCache(final File file, final ImageBytes imageBytes) {
        try {
            highresMemoryCache.put(file, imageBytes);
        } catch (final NullPointerException | CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
    }

    /**
     * Returns an ImageBytes object with thumbnail image data for the supplied
     * file
     *
     * @param file     The file of the highres picture for which a thumbnail is needed
     * @param rotation The rotation in degrees (0..360) for the thumbnail
     * @param maxSize  The maximum size of the thumbnail
     * @return The ImageBytes of the thumbnail
     */
    public static ImageBytes getThumbnailImageBytes(final File file, final double rotation, final Dimension maxSize) {
        final var key = String.format("%s-%fdeg-w:%dpx-h:%dpx", file, rotation, maxSize.width, maxSize.height);
        var imageBytes = thumbnailMemoryAndDiskCache.get(key);
        if (imageBytes != null) {
            imageBytes.setRetrievedFromCache(true);
            final var imagePath = Paths.get(file.toURI());
            try {
                final var lastModification = (Files.getLastModifiedTime(imagePath));
                if (lastModification.compareTo(imageBytes.getLastModification()) > 0) {
                    imageBytes = createThumbnailAndStoreInCache(key, file, rotation, maxSize);
                }
            } catch (final IOException ex) {
                // We might have the image in the cache but it disappeared from disk
                LOGGER.log(Level.SEVERE, "Hit IOException: {0}", ex.getLocalizedMessage());
                imageBytes = null;
            }
        } else {
            imageBytes = createThumbnailAndStoreInCache(key, file, rotation, maxSize);
        }
        return imageBytes;
    }

    /**
     * Creates a thumbnail and stores it in the cache
     *
     * @param key       the key to store it in the cache
     * @param imageFile The file of the picture
     * @param rotation  the rotation to apply
     * @param maxSize  the maximum size
     * @return the thumbnail
     */
    private static ImageBytes createThumbnailAndStoreInCache(final String key, final File imageFile, final double rotation, final Dimension maxSize) {
        final var imageBytes = createThumbnail(imageFile, rotation, maxSize);
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
     * @param maxSize the maximum size
     * @return the thumbnail
     */
    private static ImageBytes createThumbnail(final File file, final double rotation, final Dimension maxSize) {
        if (!org.jpo.datamodel.ImageIO.jvmHasReader(file)) {
            return null;
        }
        // create a new thumbnail from the highres
        final var scalablePicture = new ScalablePicture();
        if (Settings.isThumbnailFastScale()) {
            scalablePicture.setFastScale();
        } else {
            scalablePicture.setQualityScale();
        }
        scalablePicture.setScaleSize(maxSize);
        scalablePicture.loadPictureImd(file, rotation);
        if (scalablePicture.getSourcePicture() == null) {
            return null;
        }

        scalablePicture.scalePicture();

        if (scalablePicture.getScaledPicture() == null) {
            return null;
        }
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        scalablePicture.writeScaledJpg(byteArrayOutputStream);
        final var imageBytes = new ImageBytes(byteArrayOutputStream.toByteArray());
        imageBytes.setRetrievedFromCache(false);

        try {
            final var imagePath = Paths.get(file.toURI());
            imageBytes.setLastModification(Files.getLastModifiedTime(imagePath));

        } catch (final IOException ex) {
            Logger.getLogger(JpoCache.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return imageBytes;
    }


    /**
     * Returns a thumbnail for a group of pictures
     *
     * @param childPictureNodes The pictures that make up the group
     * @return The thumbnail
     * @throws IOException if something went wrong
     */
    public static ImageBytes getGroupThumbnailImageBytes(final List<SortableDefaultMutableTreeNode> childPictureNodes) throws IOException {
        final var leftMargin = 15;
        final var margin = 10;
        final var topMargin = 65;
        final var horizontalPics = (groupThumbnailDimension.width - leftMargin) / (Settings.miniThumbnailSize.width + margin);
        final var verticalPics = (groupThumbnailDimension.height - topMargin) / (Settings.miniThumbnailSize.height + margin);
        final var numberOfPics = horizontalPics * verticalPics;

        final List<PictureInfo> usablePictures = childPictureNodes
                .stream()
                .map(node -> (PictureInfo) node.getUserObject())
                .filter(pictureInfo -> org.jpo.datamodel.ImageIO.jvmHasReader(pictureInfo.getImageFile()))
                .limit(numberOfPics)
                .toList();

        final var sb = new StringBuilder("Group-");

        for (final var pictureInfo : usablePictures) {
            sb.append(String.format("%s-%fdeg-", pictureInfo.getImageFile().toString(), pictureInfo.getRotation()));
        }

        final var key = sb.toString();
        var imageBytes = thumbnailMemoryAndDiskCache.get(key);

        if (imageBytes != null) {
            try {
                final FileTime thumbnailLastModification = imageBytes.getLastModification();

                var thumbnailNeedsRefresh = false;
                for (final var pictureInfo : usablePictures) {
                    final var imagePath = Paths.get(pictureInfo.getImageURIOrNull());
                    final var lastModification = (Files.getLastModifiedTime(imagePath));
                    if (lastModification.compareTo(thumbnailLastModification) > 0) {
                        thumbnailNeedsRefresh = true;
                        break;
                    }
                }

                if (thumbnailNeedsRefresh) {
                    imageBytes = createGroupThumbnailAndStoreInCache(key, usablePictures);

                }
            } catch (final IOException ex) {
                throw (ex);
            }
        } else {
            imageBytes = createGroupThumbnailAndStoreInCache(key, usablePictures);
        }
        return imageBytes;
    }

    /**
     * Create a Group ThumbnailController by loading the nodes component images
     * and creating a folder icon with embedded images
     *
     * @param key            The key for the image in the cache
     * @param usablePictures The nodes from which to create the mini thumbnails
     * @return the image
     * @throws IOException when things go wrong
     */
    private static ImageBytes createGroupThumbnailAndStoreInCache(
            final String key,
            final List<PictureInfo> usablePictures)
    throws IOException {
        // start with a folder icon
        final var groupThumbnail = ImageIO.read(new BufferedInputStream(JpoCache.class.getClassLoader().getResourceAsStream("icon_folder_large.jpg")));
        final var groupThumbnailGraphics = groupThumbnail.createGraphics();

        var leftMargin = 15;
        var margin = 10;
        var topMargin = 65;
        var horizontalPics = (groupThumbnail.getWidth() - leftMargin) / (Settings.miniThumbnailSize.width + margin);

        var mostRecentPictureModification = FileTime.fromMillis(0);
        var picsProcessed = 0;
        for (final var pictureInfo : usablePictures) {

            final var imagePath = Paths.get(pictureInfo.getImageURIOrNull());
            final var lastModification = (Files.getLastModifiedTime(imagePath));
            if (lastModification.compareTo(mostRecentPictureModification) > 0) {
                mostRecentPictureModification = lastModification;
            }

            var x = margin + ((picsProcessed % horizontalPics) * (Settings.miniThumbnailSize.width + margin));
            final var yPos = (int) Math.round((picsProcessed / (double) horizontalPics) - 0.5f);
            var y = topMargin + (yPos * (Settings.miniThumbnailSize.height + margin));

            if (!org.jpo.datamodel.ImageIO.jvmHasReader(pictureInfo.getImageFile())) {
                continue;
            } else {
                LOGGER.log(Level.INFO, "We hava a reader for file: {0} it is class {1}",
                        new Object[]{pictureInfo.getImageFile(), org.jpo.datamodel.ImageIO.getImageIOReader(ImageIO.createImageInputStream(new FileInputStream(pictureInfo.getImageFile()))).getClass()});
            }

            final var scalablePicture = new ScalablePicture();
            scalablePicture.loadPictureImd(pictureInfo.getImageFile(), pictureInfo.getRotation());

            scalablePicture.setScaleSize(Settings.miniThumbnailSize);
            scalablePicture.scalePicture();
            x += (Settings.miniThumbnailSize.width - scalablePicture.getScaledWidth()) / 2;
            y += Settings.miniThumbnailSize.height - scalablePicture.getScaledHeight();

            groupThumbnailGraphics.drawImage(scalablePicture.getScaledPicture(), x, y, null);

            picsProcessed++;
        }

        final var byteArrayOutputStream = new ByteArrayOutputStream();
        ScalablePicture.writeJpg(byteArrayOutputStream, groupThumbnail, 0.8f);
        final var imageBytes = new ImageBytes(byteArrayOutputStream.toByteArray());

        imageBytes.setLastModification(mostRecentPictureModification);
        imageBytes.setRetrievedFromCache(false);

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
    public static String getHighresCacheStats() {
        return highresMemoryCache.getStats();
    }

    /**
     * Returns a text from the JCS with statistics on the cache
     *
     * @return a test with statistics from the cache
     */
    public static String getThumbnailCacheStats() {
        return thumbnailMemoryAndDiskCache.getStats();
    }

    /**
     * Clears the highres image cache
     */
    public static void clearHighresCache() {
        try {
            highresMemoryCache.clear();
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
    }

    /**
     * Clears the thumbnail image cache
     */
    public static void clearThumbnailCache() {
        try {
            thumbnailMemoryAndDiskCache.clear();
        } catch (CacheException ex) {
            LOGGER.severe(ex.getLocalizedMessage());

        }
    }

    /**
     * Accessor to the cache to facilitate unit tests.
     *
     * @param key the entry to remove
     */
    @TestOnly
    public static void removeFromThumbnailCache(final String key) {
        thumbnailMemoryAndDiskCache.remove(key);
    }

    /**
     * Accessor to the cache to facilitate unit tests.
     *
     * @param key the entry to remove
     */
    @TestOnly
    public static void removeFromHighresCache(final File key) {
        highresMemoryCache.remove(key);
    }

}
