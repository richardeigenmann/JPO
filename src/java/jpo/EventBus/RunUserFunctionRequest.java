package jpo.EventBus;

import jpo.dataModel.PictureInfo;

/**
 * Requests a user function to be run
 *
 * @author Richard eigenmann
 */
public class RunUserFunctionRequest implements Request {

    private final int userFunctionIndex;
    private final PictureInfo pictureInfo;

    /**
     * A request to run a user function
     *
     * @param userFunctionIndex The user function to run
     * @param pictureInfo the picture against which we want to run the user function
     */
    public RunUserFunctionRequest( int userFunctionIndex, PictureInfo pictureInfo ) {
        this.userFunctionIndex = userFunctionIndex;
        this.pictureInfo = pictureInfo;
    }

    /**
     * Returns the number of the user function to run
     *
     * @return the user function
     */
    public int getUserFunctionIndex() {
        return userFunctionIndex;
    }

    /**
     * Returns the node against which to run the user function
     *
     * @return the user function node
     */
    public PictureInfo getPictureInfo() {
        return pictureInfo;
    }

}
