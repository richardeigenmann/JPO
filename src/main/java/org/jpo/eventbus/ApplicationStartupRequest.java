package org.jpo.eventbus;

/*
 Copyright (C) 2017 -2021 Richard Eigenmann.
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

/**
 * This request indicates that the application should start up and initialise itself.
 * Typically the ApplicationEventHandler listens to the requests and takes the necessary
 * steps to start up the application.
 *
 * @author Richard Eigenmann
 * @see org.jpo.gui.ApplicationEventHandler
 * @see org.jpo.gui.ApplicationEventHandler#handleEvent(ApplicationStartupRequest)
 */
public class ApplicationStartupRequest {
}
