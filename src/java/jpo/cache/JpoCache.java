package jpo.cache;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
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
        Properties props = new Properties();

        try {
            // load a properties file
            props.load( JpoCache.class.getClassLoader().getResourceAsStream( "cache.ccf" ) );
        } catch ( IOException e ) {
            LOGGER.severe( e.getLocalizedMessage() );
        }

        props.setProperty( "jcs.auxiliary.DC.attributes.DiskPath", Settings.thumbnailCacheDirectory );

        CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
        ccm.configure( props );

        try {
            highresMemoryCache = JCS.getInstance( "highresCache" );
            thumbnailMemoryAndDiskCache = JCS.getInstance( "thumbnailCache" );
        } catch ( CacheException ex ) {
            highresMemoryCache = null;
            thumbnailMemoryAndDiskCache = null;
            LOGGER.severe( ex.getLocalizedMessage() );
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
            try {
                Path imagePath = Paths.get( url.toURI() );
                FileTime lastModification = ( Files.getLastModifiedTime( imagePath ) );
                if ( lastModification.compareTo( imageBytes.getLastModification() ) > 0 ) {
                    imageBytes = new ImageBytes( url.toString(), IOUtils.toByteArray( url.openStream() ) );

                }
            } catch ( URISyntaxException | IOException ex ) {
                LOGGER.severe( ex.getLocalizedMessage() );
            }
        } else {
            imageBytes = new ImageBytes( url.toString(), IOUtils.toByteArray( url.openStream() ) );
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
            try {
                Path imagePath = Paths.get( url.toURI() );
                FileTime lastModification = ( Files.getLastModifiedTime( imagePath ) );
                if ( lastModification.compareTo( imageBytes.getLastModification() ) > 0 ) {
                    imageBytes = createThumbnailAndStoreInCache( key, url, rotation, maxWidth, maxHeight );

                }
            } catch ( URISyntaxException | IOException ex ) {
                LOGGER.severe( ex.getLocalizedMessage() );
            }

        } else {
            imageBytes = createThumbnailAndStoreInCache( key, url, rotation, maxWidth, maxHeight );
        }
        return imageBytes;
    }

    private ImageBytes createThumbnailAndStoreInCache( String key, URL imageURL, double rotation, int maxWidth, int maxHeight ) {
        ImageBytes imageBytes = createThumbnail( key, imageURL, rotation, maxWidth, maxHeight );
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
            Logger.getLogger( JpoCache.class
                    .getName() ).log( Level.SEVERE, null, ex );
        }

        return imageBytes;
    }

    private Dimension groupThumbnailDimension = null;

    /**
     * Returns the Dimension of the icon_folder_large.jpg image and if there is
     * an ioerror the maximum size of the thumbnails.
     *
     * @return
     */
    private Dimension getThumbnailDimensions() {
        if ( groupThumbnailDimension == null ) {
            BufferedImage groupThumbnail;
            try ( BufferedInputStream bis = new BufferedInputStream( Settings.CLASS_LOADER.getResourceAsStream( "jpo/images/icon_folder_large.jpg" ) ) ) {
                groupThumbnail = ImageIO.read( bis );
                groupThumbnailDimension = new Dimension( groupThumbnail.getWidth(), groupThumbnail.getHeight() );

            } catch ( IOException ex ) {
                Logger.getLogger( JpoCache.class
                        .getName() ).log( Level.SEVERE, null, ex );
                groupThumbnailDimension = new Dimension( Settings.thumbnailSize, Settings.thumbnailSize );
            }
        }
        return groupThumbnailDimension;
    }

    public ImageBytes getGroupThumbnailImageBytes( ArrayList<SortableDefaultMutableTreeNode> childPictureNodes ) throws IOException {
        int leftMargin = 15;
        int margin = 10;
        int topMargin = 65;
        int horizontalPics = ( getThumbnailDimensions().width - leftMargin ) / ( Settings.miniThumbnailSize.width + margin );
        int verticalPics = ( getThumbnailDimensions().height - topMargin ) / ( Settings.miniThumbnailSize.height + margin );
        int numberOfPics = horizontalPics * verticalPics;

        StringBuilder sb = new StringBuilder( "Group:" );
        for ( int i = 0; ( i < numberOfPics ) && ( i < childPictureNodes.size() ); i++ ) {
            PictureInfo pictureInfo = (PictureInfo) childPictureNodes.get( i ).getUserObject();
            sb.append( String.format( "%s-%fdeg", pictureInfo.getHighresURL().toString(), pictureInfo.getRotation() ) );
        }

        String key = sb.toString();
        ImageBytes imageBytes = (ImageBytes) thumbnailMemoryAndDiskCache.get( key );

        if ( imageBytes != null ) {
            try {
                FileTime thumbnailLastModification = imageBytes.getLastModification();

                boolean thumbnailNeedsRefresh = false;
                for ( int i = 0; ( i < numberOfPics ) && ( i < childPictureNodes.size() ); i++ ) {
                    PictureInfo pictureInfo = (PictureInfo) childPictureNodes.get( i ).getUserObject();
                    Path imagePath = Paths.get( pictureInfo.getHighresURIOrNull() );
                    FileTime lastModification = ( Files.getLastModifiedTime( imagePath ) );
                    if ( lastModification.compareTo( thumbnailLastModification ) > 0 ) {
                        thumbnailNeedsRefresh = true;
                        break;
                    }
                }
                if ( thumbnailNeedsRefresh ) {
                    imageBytes = createGroupThumbnailAndStoreInCache( key, numberOfPics, childPictureNodes );

                }
            } catch ( IOException ex ) {
                LOGGER.severe(  ex.getLocalizedMessage() );
                throw ( ex );
            }
        } else {
            imageBytes = createGroupThumbnailAndStoreInCache( key, numberOfPics, childPictureNodes );
        }
        return imageBytes;
    }

    /**
     * Create a Group ThumbnailController by loading the nodes component images
     * and creating a folder icon with embedded images
     */
    private ImageBytes createGroupThumbnailAndStoreInCache( String key, int numberOfPics, ArrayList<SortableDefaultMutableTreeNode> childPictureNodes ) throws IOException {

        //logger.info("ThumbnailCreationFactory.createNewGroupThumbnail: Creating ThumbnailController " + ((GroupInfo) myNode.getUserObject()).getLowresLocation() + " from " + ((GroupInfo) myNode.getUserObject()).getLowresLocation());
        BufferedImage groupThumbnail = ImageIO.read( new BufferedInputStream( Settings.CLASS_LOADER.getResourceAsStream( "jpo/images/icon_folder_large.jpg" ) ) );
        Graphics2D groupThumbnailGraphics = groupThumbnail.createGraphics();

        int leftMargin = 15;
        int margin = 10;
        int topMargin = 65;
        int horizontalPics = ( groupThumbnail.getWidth() - leftMargin ) / ( Settings.miniThumbnailSize.width + margin );
        int verticalPics = ( groupThumbnail.getHeight() - topMargin ) / ( Settings.miniThumbnailSize.height + margin );

        int x, y;
        int yPos;
        ScalablePicture scalablePicture = new ScalablePicture();
        FileTime mostRecentPictureModification = FileTime.fromMillis( 0 );
        for ( int picsProcessed = 0; ( picsProcessed < numberOfPics ) && ( picsProcessed < childPictureNodes.size() ); picsProcessed++ ) {
            PictureInfo pi = (PictureInfo) childPictureNodes.get( picsProcessed ).getUserObject();

            Path imagePath = Paths.get( pi.getHighresURIOrNull() );
            FileTime lastModification = ( Files.getLastModifiedTime( imagePath ) );
            if ( lastModification.compareTo( mostRecentPictureModification ) > 0 ) {
                mostRecentPictureModification = lastModification;
            }

            x = margin + ( ( picsProcessed % horizontalPics ) * ( Settings.miniThumbnailSize.width + margin ) );
            yPos = (int) Math.round( ( (double) picsProcessed / (double) horizontalPics ) - 0.5f );
            y = topMargin + ( yPos * ( Settings.miniThumbnailSize.height + margin ) );
            //logger.info(Integer.toString(picsProcessed) +": " + Integer.toString(x) + "/" +Integer.toString(y)+ " - " + Integer.toString(yPos) );

            scalablePicture.loadPictureImd( pi.getHighresURL(), pi.getRotation() );

            scalablePicture.setScaleSize( Settings.miniThumbnailSize );
            scalablePicture.scalePicture();
            x += ( Settings.miniThumbnailSize.width - scalablePicture.getScaledWidth() ) / 2;
            y += Settings.miniThumbnailSize.height - scalablePicture.getScaledHeight();

            groupThumbnailGraphics.drawImage( scalablePicture.getScaledPicture(), x, y, null );
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ScalablePicture.writeJpg( bos, groupThumbnail, 0.8f );
        ImageBytes imageBytes = new ImageBytes( key, bos.toByteArray() );

        imageBytes.setLastModification( mostRecentPictureModification );

        try {
            thumbnailMemoryAndDiskCache.put( key, imageBytes );
        } catch ( CacheException ex ) {
            LOGGER.severe( ex.getLocalizedMessage() );
        }

        return imageBytes;
    }

    public String getHighresCacheStats() {
        return highresMemoryCache.getStats();
    }

    public String getThumbnailCacheStats() {
        return thumbnailMemoryAndDiskCache.getStats();
    }

    public void clearHighresCache() {
        try {
            highresMemoryCache.clear();
        } catch ( CacheException ex ) {
            LOGGER.severe( ex.getLocalizedMessage() );
        }
    }

    public void clearThumbnailCache() {
        try {
            thumbnailMemoryAndDiskCache.clear();
        } catch ( CacheException ex ) {
            LOGGER.severe( ex.getLocalizedMessage() );

        }
    }

}
