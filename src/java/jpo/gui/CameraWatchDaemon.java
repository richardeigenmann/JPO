package jpo.gui;

import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import jpo.dataModel.Settings;
import jpo.dataModel.Camera;

/*
 CameraWatchDaemon.java: Daemon Thread that monitors when a camera has been connected.

 Copyright (C) 2002 - 2009  Richard Eigenmann.
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
        Thread t = new Thread( this );
        t.start();
    }
    /**
     * A flag to indicate that the thread should stop at the next iteration.
     */
    private boolean gracefullyInterrupt = false;

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
            synchronized ( Settings.cameras ) {
                for ( Camera c : Settings.cameras ) {
                    boolean isConnected = c.isCameraConnected();
                    if ( c.getMonitorForNewPictures() && isConnected && ( !c.getLastConnectionStatus() ) ) {
                        LOGGER.info( getClass().toString() + ": Camera " + c.toString() + " has been connected " );
                        final CameraDownloadWizardData dm = new CameraDownloadWizardData();
                        dm.setCamera( c );
                        dm.setAnchorFrame( Settings.anchorFrame );
                        Runnable r;
                        r = new Runnable() {

                            @Override
                            public void run() {
                                new CameraDownloadWizard( dm );
                            }
                        };
                        SwingUtilities.invokeLater( r );
                    }
                    c.setLastConnectionStatus( isConnected );
                }
            }
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException ex ) {
                ex.printStackTrace();
            }
        }
    }
}
