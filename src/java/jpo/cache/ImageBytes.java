package jpo.cache;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.file.attribute.FileTime;

/**
 *
 * @author Richard Eigenmann
 */
public class ImageBytes implements Serializable {

    private static final long serialVersionUID = 3;
    private long lastModification;
    private final byte[] bytes;

    
    /**
     * Constructs a new ImageBytes object with the key and the bytes as read from disk
     * @param bytes the bytes
     */
    public ImageBytes(  byte[] bytes ) {
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
