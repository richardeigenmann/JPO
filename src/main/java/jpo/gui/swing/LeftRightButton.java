package jpo.gui.swing;

import javax.swing.ImageIcon;

/**
 *
 * @author richi
 */
public class LeftRightButton extends NavBarButton {

    /**
     * The state of the button
     */
    public enum BUTTON_STATE {

        /**
         * Indicates that we are at the beginning of a set
         */
        BEGINNING,
        /**
         * Indicates that we are at a set boundary but can go back
         */
        HAS_LEFT,
        /**
         * Indicates that we can go back
         */
        HAS_PREVIOUS,
        /**
         * Indicates that we can go forward
         */
        HAS_RIGHT,
        /**
         * Indicates that we are at a boundary but can go forward
         */
        HAS_NEXT,
        /**
         * Indicates that we are at the end of set
         */
        END
    };

    private static final ClassLoader CLASS_LOADER = LeftRightButton.class.getClassLoader();

    /**
     * Icon pointing left
     */
    private static final ImageIcon ICON_ARROW_LEFT = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_previous.gif" ) );
    /**
     * Double left pointing icon
     */
    private static final ImageIcon ICON_DOUBLE_ARROW_LEFT = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_prevprev.gif" ) );
    /**
     * Icon pointing left with a bar to indicate you can't go left
     */
    private static final ImageIcon ICON_ARROW_LEFT_STOP = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_noprev.gif" ) );

    /**
     * Icon pointing right
     */
    private static final ImageIcon ICON_ARROW_RIGHT = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_next.gif" ) );
    /**
     * Double right pointing icon
     */
    private static final ImageIcon ICON_DOUBLE_ARROW_RIGHT = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_nextnext.gif" ) );
    /**
     * Icon pointing right at a bar to indicate you can't go right
     */
    private static final ImageIcon ICON_ARROW_RIGHT_STOP = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_nonext.gif" ) );

    /**
     * Constructs the left button
     */
    public LeftRightButton() {
        super( ICON_ARROW_LEFT );
    }

    /**
     * Sets the appropriate icon for the state
     *
     * @param state the state from the enum to set
     */
    public void setDecoration( BUTTON_STATE state ) {
        switch ( state ) {
            case BEGINNING:
                setIcon( ICON_ARROW_LEFT_STOP );
                break;
            case HAS_PREVIOUS:
                setIcon( ICON_DOUBLE_ARROW_LEFT );
                break;
            case HAS_LEFT:
                setIcon( ICON_ARROW_LEFT );
                break;
            case HAS_RIGHT:
                setIcon( ICON_ARROW_RIGHT );
                break;
            case HAS_NEXT:
                setIcon( ICON_DOUBLE_ARROW_RIGHT );
                break;
            case END:
                setIcon( ICON_ARROW_RIGHT_STOP );
                break;
        }
    }

}
