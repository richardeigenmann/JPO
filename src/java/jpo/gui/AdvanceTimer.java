package jpo.gui;

import java.util.logging.Logger;
import jpo.dataModel.Settings;
import jpo.*;

/*
AdvanceTimer.java:  a class that can wait a period of time and wake up a caller class

Copyright (C) 2002-2009  Richard Eigenmann.
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
 *  This class implements a Thread that waits a period of time and then calls the method 
 *  {@link AdvanceTimerInterface#requestAdvance()} on it's calling object. 
 *
 *  @see AdvanceTimerInterface
 */
public class AdvanceTimer implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureInfoEditor.class.getName() );

    /**
     *  A reference back to the object that needs to be told to advance to the
     *  next picture.
     */
    private AdvanceTimerInterface caller;

    /**
     *  The amount of seconds to sleep.
     */
    private int delaySeconds;

    /**
     *  A variable to indicate that the thread should die. Anything can write false to
     *  this variable and upon the next loop the thread wil exit gracefully.
     */
    private boolean keepThreadRunning = true;


    /**
     *  Constructor which should be called with the object to wake up and the
     *  delay this is to be done after.
     *
     *  @param  caller    		The object to wake up after the time is reached
     *  @param  delaySeconds	period to sleep
     */
    public AdvanceTimer( AdvanceTimerInterface caller, int delaySeconds ) {

        this.caller = caller;
        this.delaySeconds = delaySeconds;
        Thread t = new Thread( this );
        t.start();
    }


    /**
     *  The Thread method which sleeps and then advances the picture.
     */
    public void run() {
        while ( keepThreadRunning ) {
            caller.requestAdvance();
            try {
                while ( !caller.readyToAdvance() ) {
                    logger.info( "AdvanceTimer: Last image is not yet ready. Giving it some more time" );
                    Thread.sleep( Settings.advanceTimerPollingInterval );
                }
                logger.info( "AdvanceTimer: Sleeping for " + Integer.toString( delaySeconds ) + " Seconds" );
                Thread.sleep( delaySeconds * 1000 );
            } catch ( InterruptedException x ) {
                logger.info( "The sleep statement in the AdvanceTimer was interrupted." );
                keepThreadRunning = false;
            }
        }
    }


    /**
     *  A call to this method will exit the thread gracefully.
     */
    public void stopThread() {
        keepThreadRunning = false;
    }
}
