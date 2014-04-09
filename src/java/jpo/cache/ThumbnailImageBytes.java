package jpo.cache;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.file.attribute.FileTime;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailImageBytes implements Serializable {

    private static final long serialVersionUID = 1;    
    private final String URI;
    FileTime sourceFileTime;
    private final byte[] bytes;

    public ThumbnailImageBytes( String URI, FileTime sourceFileTime,  byte[] bytes ) {
        this.URI = URI;
        this.sourceFileTime = sourceFileTime;
        this.bytes = bytes;
    }

    /**
     * Returns the image bytes from the stored bytes
     * @return the bytes as a ByteArrayInputStream
     */
    public ByteArrayInputStream getByteArrayInputStream() {
        return new ByteArrayInputStream( bytes );
    }
    
    public FileTime getSourceFileTime() {
        return sourceFileTime;
    }

}
