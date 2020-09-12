package org.jpo.gui;

import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 CameraWatchDaemon.java: Daemon Thread that monitors when a camera has been connected.

 Copyright (C) 2002 - 2017  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * Daemon Thread that monitors when a camera has been connected.
 *
 * @author Richard Eigenmann
 */
public class CameraWatchDaemon implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraWatchDaemon.class.getName() );

    /**
     * Creates a new instance of CameraWatchDaemon. The Thread iterates over the
     */
    public CameraWatchDaemon() {
        new Thread( this, "CameraWatchDaemon" ).start();
    }
    /**
     * A flag to indicate that the thread should stop at the next iteration.
     */
    private boolean gracefullyInterrupt;  // default is false

    /**
     * A method to call when you want to signal that the thread should stop at
     * the next iteration.
     */
    public void stopAsap() {
        gracefullyInterrupt = true;
    }

    /**
     * The run method enumerates the cameras configured and checks to see if a
     * camera has been added. If a camera was added it fires off the
     * CameraDownloadWizard.
     */
    @Override
    public void run() {

        while ( !gracefullyInterrupt ) {
            synchronized (Settings.getCameras()) {
                Settings.getCameras().forEach( (c ) -> {
                    boolean isConnected = c.isCameraConnected();
                    if ( c.getMonitorForNewPictures() && isConnected && ( !c.getLastConnectionStatus() ) ) {
                        LOGGER.log( Level.INFO, "{0}: Camera {1} has been connected ", new Object[]{ getClass().toString(), c.toString() } );
                        final CameraDownloadWizardData dm = new CameraDownloadWizardData();
                        dm.setCamera( c );
                        dm.setAnchorFrame( Settings.anchorFrame );
                        SwingUtilities.invokeLater(
                                () -> new CameraDownloadWizard( dm )
                        );
                    }
                    c.setLastConnectionStatus( isConnected );
                } );
            }
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException ex ) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
