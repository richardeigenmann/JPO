package jpo.gui;

/*
 InterruptSemaphore.java:  class used to signal an interrupt

 Copyright (C) 2007-2014 Richard Eigenmann.
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
 * This class can be used as a semaphore to indicate to a thread that it should
 * be interrupted at a convenient time.
 *
 * @author Richard Eigenmann
 */
public class InterruptSemaphore {

    private boolean shouldInterrupt;  // default is false

    /**
     * use this method to find out if the thread should be interrupted.
     *
     * @return true if it should be interrupted.
     */
    public boolean getShouldInterrupt() {
        return shouldInterrupt;
    }

    /**
     * use this method to indicate that the thread should be interrupted at the
     * next safe point
     *
     * @param shouldInterrupt
     */
    public void setShouldInterrupt( boolean shouldInterrupt ) {
        this.shouldInterrupt = shouldInterrupt;
    }

}
