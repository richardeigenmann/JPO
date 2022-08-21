package org.jpo.cache;

import org.jpo.datamodel.Settings;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.StartThumbnailCreationDaemonRequest;

/**
 * instance of this class will monitor whether we have run out of live ThumbnailCreationDaemons
 */
public class ThumbnailCreationDaemonWatchDog implements Runnable {

    @Override
    public void run() {
        while (true) {
            var missingDaemons = Settings.getDefaultThumbnailCreationThreads() - ThumbnailCreationExecutor.getInstance().getLiveDaemonsCount();
            for (var i = missingDaemons; i > 0; i--) {
                JpoEventBus.getInstance().post(new StartThumbnailCreationDaemonRequest());
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
