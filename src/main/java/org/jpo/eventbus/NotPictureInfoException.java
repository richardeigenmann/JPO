package org.jpo.eventbus;

/**
 * An Exception to indicate that we have no PictureInfo node.
 */
public class NotPictureInfoException extends RuntimeException {
    public NotPictureInfoException(final String errorMessage) {
        super(errorMessage);
    }
}
