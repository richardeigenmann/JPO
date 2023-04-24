package org.jpo.eventbus;

/*
 Copyright (C) 2021-2022 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 In the hope that it will be useful, but WITHOUT ANY WARRANTY.
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * This request asks the handler to check whether there is a new version of
 * the application available. If so it presents a dialog box offering to take
 * the user to the download website. The alerts can be snoozed for a fortnight
 * or can be turned off.
 *
 * @param forceCheck set to true if the check should bypass the snooze period check and the neverCheck flag
 * @author Richard Eigenmann
 */
public record CheckForUpdatesRequest(boolean forceCheck) {
}
