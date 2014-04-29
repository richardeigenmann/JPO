package jpo.gui.swing;

import javax.swing.ImageIcon;

/**
 * An icon of a clock
 */
public class ClockButton extends NavBarButton {

     private static final ClassLoader CLASS_LOADER = LeftRightButton.class.getClassLoader();
    
    /**
     * Icon to indicate that the timer is active
     */
    private static final ImageIcon ICON_CLOCK_ON = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_clock_on.gif" ) );

    /**
     * Icon to indicate that the timer is available
     */
    private static final ImageIcon ICON_CLOCK_OFF = new ImageIcon( CLASS_LOADER.getResource( "jpo/images/icon_clock_off.gif" ) );

    private final boolean onOff;

    /**
     * Constructs clock icon in the off state.
     *
     * @param onOff true if on, false if off
     */
    ClockButton( boolean onOff ) {
        super( ICON_CLOCK_OFF );
        this.onOff = onOff;

    }

    /**
     * Switches the clock to busy mode
     */
    public void setClockBusy() {
        setIcon( ICON_CLOCK_ON );
    }

    /**
     * Switches the clock icon to idle mode
     */
    public void setClockIdle() {
        setIcon( ICON_CLOCK_OFF );
    }

}
