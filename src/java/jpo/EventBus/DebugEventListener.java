package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import java.util.logging.Logger;

/**
 * A dummy event listener that listens to all events and simply logs them.
 * Should be helpful to debug what is going on.
 *
 * @author Richard Eigenmann
 */
public class DebugEventListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( DebugEventListener.class.getName() );

    /**
     * This event listener registers so it receives all types of event objects
     * from the EventBus and then logs them on the console.
     *
     * @param o the event
     */
    @Subscribe
    public void handleAllEvents( Object o ) {
        //LOGGER.log( Level.INFO, "Event propagating: {0}", o.getClass().toString() );
        System.out.println( String.format( "Event propagating: %s", o.getClass().toString() ) );
    }

}
