package org.jpo.eventbus;

/**
 * An Exception to indicate that we have no PictureInfo node.
 */
public class NotPictureInfoException extends RuntimeException {
    /**
     * Constructs an Exception to indicate that there is no PictureInfo node
     *
     * @param errorMessage The error message for the exception
     */
    public NotPictureInfoException(final String errorMessage) {
        super(errorMessage);
    }
}
