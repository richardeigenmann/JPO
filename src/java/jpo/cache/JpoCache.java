package jpo.cache;

import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpo.dataModel.Settings;
import jpo.gui.ScalablePicture;
import org.apache.commons.io.IOUtils;
import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.control.CompositeCacheManager;

/**
 *
 * @author Richard Eigenmann
 */
public class JpoCache {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoCache.class.getName() );

    private JCS highresMemoryCache;
    private JCS thumbnailMemoryAndDiskCache;

    private JpoCache() {
        try {
            highresMemoryCache = JCS.getInstance( "highresCache" );
            thumbnailMemoryAndDiskCache = JCS.getInstance( "thumbnailCache" );
        } catch ( CacheException ex ) {
            highresMemoryCache = null;
            thumbnailMemoryAndDiskCache = null;
            LOGGER.severe( ex.getLocalizedMessage() );
            return;
        }
    }

    public static JpoCache getInstance() {
        return JpoCacheHolder.INSTANCE;
    }

    private static class JpoCacheHolder {

        private static final JpoCache INSTANCE = new JpoCache();
    }

    public void shutdown() {
        CompositeCacheManager.getInstance().shutDown();
    }

    public ImageBytes getHighresImageBytes( URL url ) throws IOException {
        ImageBytes imageBytes = (ImageBytes) highresMemoryCache.get( url );
        if ( imageBytes != null ) {
            LOGGER.info( "Found url: " + url + " in highres cache. Returning this." );
            LOGGER.info( "Must now check if it is up to date" );
            try {
                Path imagePath = Paths.get( url.toURI() );
                FileTime lastModification = ( Files.getLastModifiedTime( imagePath ) );
                LOGGER.info( url.toString() + " last modification is: " + lastModification.toString() );
                LOGGER.info( "Cache last modification is: " + imageBytes.getLastModification().toString() );
                LOGGER.info( "CompareTo returns: " + lastModification.compareTo( imageBytes.getLastModification() ) );
                if ( lastModification.compareTo( imageBytes.getLastModification() ) > 0 ) {
                    LOGGER.info( "Source picture: " + url.toString() + " has a more recent last modification timestamp need to update the cache..." );
                    imageBytes = new ImageBytes( url.toString(), IOUtils.toByteArray( url.openStream() ) );
                }
            } catch ( URISyntaxException | IOException ex ) {
                Logger.getLogger( JpoCache.class.getName() ).log( Level.SEVERE, null, ex );
            }
        } else {
            LOGGER.info( "Url: " + url + " not in highres cache. Doing file read..." );
            imageBytes = new ImageBytes( url.toString(), IOUtils.toByteArray( url.openStream() ) );
            LOGGER.info( "Putting url: " + url + " into highres cache." );
            try {
                highresMemoryCache.put( url, imageBytes );
            } catch ( CacheException ex ) {
                LOGGER.severe( ex.getLocalizedMessage() );
            }
        }
        return imageBytes;
    }

    public ImageBytes getThumbnailImageBytes( URL url, double rotation, int maxWidth, int maxHeight ) throws IOException {
        String key = String.format( "%s-%fdeg-w:%dpx-h:%dpx", url, rotation, maxWidth, maxHeight );
        ImageBytes imageBytes = (ImageBytes) thumbnailMemoryAndDiskCache.get( key );
        if ( imageBytes != null ) {
            LOGGER.info( "Found key: " + key + " in thumbnail cache." );
            LOGGER.info( "Must now check if it is up to date" );
            try {
                Path imagePath = Paths.get( url.toURI() );
                FileTime lastModification = ( Files.getLastModifiedTime( imagePath ) );
                LOGGER.info( url.toString() + " last modification is: " + lastModification.toString() );
                LOGGER.info( key + " last modification is: " + imageBytes.getLastModification().toString() );
                LOGGER.info( "CompareTo returns: " + lastModification.compareTo( imageBytes.getLastModification() ) );
                if ( lastModification.compareTo( imageBytes.getLastModification() ) > 0 ) {
                    LOGGER.info( "Source picture: " + url.toString() + " has a more recent last modification timestamp need to update the thumbnail..." );
                    imageBytes = createThumbnailAndStoreInCache( key, url, rotation, maxWidth, maxHeight );
                }
            } catch ( URISyntaxException | IOException ex ) {
                Logger.getLogger( JpoCache.class.getName() ).log( Level.SEVERE, null, ex );
            }

        } else {
            LOGGER.info( "Key: " + key + " not in thumbnail cache. Creating..." );
            imageBytes = createThumbnailAndStoreInCache( key, url, rotation, maxWidth, maxHeight );
        }
        return imageBytes;
    }

    private ImageBytes createThumbnailAndStoreInCache( String key, URL imageURL, double rotation, int maxWidth, int maxHeight ) {
        LOGGER.info( "Creating: " + key + " by loading and scaling..." );
        ImageBytes imageBytes = createThumbnail( key, imageURL, rotation, maxWidth, maxHeight );
        LOGGER.info( "Putting key: " + key + " into thumbnail cache." );
        try {
            thumbnailMemoryAndDiskCache.put( key, imageBytes );
        } catch ( CacheException ex ) {
            LOGGER.severe( ex.getLocalizedMessage() );
        }
        return imageBytes;
    }

    private ImageBytes createThumbnail( String key, URL imageURL, double rotation, int maxWidth, int maxHeight ) {
        Dimension maxDimension = new Dimension( maxWidth, maxHeight );

        // create a new thumbnail from the highres
        ScalablePicture scalablePicture = new ScalablePicture();
        if ( Settings.thumbnailFastScale ) {
            scalablePicture.setFastScale();
        } else {
            scalablePicture.setQualityScale();
        }
        scalablePicture.setScaleSize( maxDimension );

        scalablePicture.loadPictureImd( imageURL, rotation );

        scalablePicture.scalePicture();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scalablePicture.writeScaledJpg( bos );
        ImageBytes imageBytes = new ImageBytes( key, bos.toByteArray() );

        try {
            Path imagePath = Paths.get( imageURL.toURI() );
            imageBytes.setLastModification( Files.getLastModifiedTime( imagePath ) );
        } catch ( URISyntaxException | IOException ex ) {
            Logger.getLogger( JpoCache.class.getName() ).log( Level.SEVERE, null, ex );
        }

        return imageBytes;
    }

}
