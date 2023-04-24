package org.jpo.cache;

/*
 Copyright (C) 2002 - 2023 Richard Eigenmann.
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
 * Priority enum for the Thumbnail Queue.
 * Enums use natural ordering.
 */
public enum QUEUE_PRIORITY {
    /**
     * Highest queue priority
     */
    HIGH_PRIORITY,
    /** Medium queue priority */
    MEDIUM_PRIORITY,
    /** Lower queue priority */
    LOW_PRIORITY,
    /** Lowest queue priority */
    LOWEST_PRIORITY
}