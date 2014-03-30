package jpo.EventBus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Logger;

/**
 *
 * @author Richard Eigenmann
 */
public class JpoEventBus extends EventBus {

    private static final Logger LOGGER = Logger.getLogger( JpoEventBus.class.getName() );

    private JpoEventBus() {
        register( new DeadEventSubscriber() );
    }

    public static JpoEventBus getInstance() {
        return JpoEventBusHolder.INSTANCE;
    }

    private static class JpoEventBusHolder {

        private static final JpoEventBus INSTANCE = new JpoEventBus();

    }

    private class DeadEventSubscriber {

        @Subscribe
        public void handleDeadEvent( DeadEvent deadEvent ) {
            LOGGER.warning( "Dead event of class: " + deadEvent.getClass().getCanonicalName() );
        }

    }

}
