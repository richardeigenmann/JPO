package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard Eigenmann
 */
public class DebugEventListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( DebugEventListener.class.getName() );

    @Subscribe
    public void handleAllEvents( Object o ) {
        LOGGER.log( Level.INFO, "Event propagating: {0}", o.getClass().toString());
    }

}
