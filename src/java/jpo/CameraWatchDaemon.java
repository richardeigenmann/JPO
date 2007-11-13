package jpo;

/*
CameraWatchDaemon.java: Daamon Thread that monitors when a camera has been connected.
 
Copyright (C) 2002-2007  Richard Eigenmann.
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
 * @author richi
 */
public class CameraWatchDaemon extends Thread {
    
    /** Creates a new instance of CameraWatchDaemon. The Thread iterates over the  */
    public CameraWatchDaemon() {
        start();
    }
    
    
    /**
     *  A Flag to indicate that the thread should stop at the next iteration.
     */
    private boolean gracefullyInterrupt = false;
    
    /**
     *  A method to call when you want to signal that the thread should stop at the next iteration.
     */
    public void stopAsap() {
        gracefullyInterrupt = true;
    }
    
    
    /**
     *  The run method enumerates the Cameras configured and checks to see if a
     *  camera has been added. If a camera was added it fires off the CameraDownloadWizard.
     */
    public void run() {
        
        while ( ! gracefullyInterrupt ) {
            synchronized ( Settings.Cameras ) {
                for ( Camera c : Settings.Cameras ) {
                    boolean isConnected = c.isCameraConnected();
                    if ( c.getMonitorForNewPictures() && isConnected && ( ! c.getLastConnectionStatus() ) ) {
                        Tools.log( getClass().toString() + ": Camera " + c.toString() + " has been connected " );
                        CameraDownloadWizardData dm = new CameraDownloadWizardData();
                        dm.setCamera( c );
                        dm.setAnchorFrame( Settings.anchorFrame );
                        dm.setCollectionJTreeController( Jpo.collectionJTreeController );
                        new CameraDownloadWizard( dm );
                    }
                    c.setLastConnectionStatus( isConnected );
                }
            }
            try {
                sleep( 5000 );
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
