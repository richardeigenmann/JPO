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

    public void setLastModification( FileTime lastModification ) {
        this.lastModification = lastModification.toMillis();
    }

    public FileTime getLastModification() {
        return FileTime.fromMillis( lastModification );
    }

}
