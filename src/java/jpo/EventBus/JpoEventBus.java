package jpo.EventBus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard Eigenmann
 */
public class JpoEventBus extends EventBus {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( JpoEventBus.class.getName() );

    /**
     * The EventBus singleton
     */
    private JpoEventBus() {
        register( new DeadEventSubscriber() );
    }

    /**
     * Returns the EventBus for the JPO application
     *
     * @return the EventBus singleton
     */
    public static JpoEventBus getInstance() {
        return JpoEventBusHolder.INSTANCE;
    }

    /**
     * Singleton for the EventBus
     */
    private static class JpoEventBusHolder {

        /**
         * The instance of the event bus
         */
        private static final JpoEventBus INSTANCE = new JpoEventBus();

    }

    /**
     * A subscriber for a dead event
     */
    private static class DeadEventSubscriber {

        /**
         * Gets called with dead events
         *
         * @param deadEvent the dead event
         */
        @Subscribe
        public void handleDeadEvent( DeadEvent deadEvent ) {
            LOGGER.log( Level.WARNING, "Dead event of class: {0}", deadEvent.getClass().getCanonicalName() );
        }

    }

}
