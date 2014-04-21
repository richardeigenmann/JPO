package jpo.cache;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.file.attribute.FileTime;

/**
 *
 * @author Richard Eigenmann
 */
public class ImageBytes implements Serializable {

    private static final long serialVersionUID = 2;
    private final String key;
    private long lastModification;
    private final byte[] bytes;

    
    /**
     * Constructs a new ImageBytes object with the key and the bytes as read from disk
     * @param key the Key for the object
     * @param bytes the bytes
     */
    public ImageBytes( String key, byte[] bytes ) {
        this.key = key;
        this.bytes = bytes;
    }

    /**
     * Returns the image bytes from the stored bytes
     *
     * @return the bytes as a ByteArrayInputStream
     */
    public ByteArrayInputStream getByteArrayInputStream() {
        return new ByteArrayInputStream( bytes );
    }

    /**
     * Returns the image bytes from the stored bytes array
     *
     * @return the bytes[]
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Remembers the last modification time of the source file
     * @param lastModification the last modification time
     */
    public void setLastModification( FileTime lastModification ) {
        this.lastModification = lastModification.toMillis();
    }

    /**
     * Returns the last modification time of the data that was stored in the byte array
     * @return the last modification time
     */
    public FileTime getLastModification() {
        return FileTime.fromMillis( lastModification );
    }

}
