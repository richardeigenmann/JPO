package org.jpo.cache;

/*
 Copyright (C) 2023-2025 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

import org.jpo.gui.Settings;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A singleton to create, track and monitor the ThumbnailCreationDaemon thread
 * objects.
 */
public class ThumbnailCreationExecutor {

    private static ThumbnailCreationExecutor thumbnailCreationExecutor;

    private ThumbnailCreationExecutor() {
    }

    public static ThumbnailCreationExecutor getInstance() {

        // create object if it's not already created
        if (thumbnailCreationExecutor == null) {
            thumbnailCreationExecutor = new ThumbnailCreationExecutor();
        }

        // returns the singleton object
        return thumbnailCreationExecutor;
    }

    /**
     * A collection to track the created ThumbnailCreationDaemons
     */
    private final Collection<Thread> startedDaemonThreads = new ArrayList<>();

    public void spawnThumbnailCreationDaemon() {
        final var newDaemon = new ThumbnailCreationDaemon(Settings.THUMBNAIL_CREATION_THREAD_POLLING_TIME);
        final var thread = new Thread(newDaemon, "ThumbnailCreationDaemon");
        startedDaemonThreads.add(thread);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public int getLiveDaemonsCount() {
        return (int) startedDaemonThreads.stream().filter(Thread::isAlive).count();
    }

}
