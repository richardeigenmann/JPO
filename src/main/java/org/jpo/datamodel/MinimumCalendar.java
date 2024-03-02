package org.jpo.datamodel;

import java.util.Calendar;

/*
 Copyright (C) 2024 Richard Eigenmann.
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
 * A singleton to get the smallest date the Calendar object allows so that we can compare
 * null timestamps with real timestamps and make them come out as lower.
 *
 * The code was written by Google Gemini and modified by Richard Eigenmann with some
 * input from the Sonar Linter.
 */
public class MinimumCalendar {

    private static MinimumCalendar instance;
    private final Calendar lazyMinimumCalendar;

    /**
     * Becomes 0001-01-01 00:00:00
     **/
    private MinimumCalendar() {
        lazyMinimumCalendar = Calendar.getInstance();
        lazyMinimumCalendar.set(Calendar.YEAR, lazyMinimumCalendar.getMinimum(Calendar.YEAR));
        lazyMinimumCalendar.set(Calendar.MONTH, lazyMinimumCalendar.getMinimum(Calendar.MONTH));
        lazyMinimumCalendar.set(Calendar.DAY_OF_MONTH, lazyMinimumCalendar.getMinimum(Calendar.DAY_OF_MONTH));
        lazyMinimumCalendar.set(Calendar.HOUR, lazyMinimumCalendar.getMinimum(Calendar.HOUR));
        lazyMinimumCalendar.set(Calendar.MINUTE, lazyMinimumCalendar.getMinimum(Calendar.MINUTE));
        lazyMinimumCalendar.set(Calendar.SECOND, lazyMinimumCalendar.getMinimum(Calendar.SECOND));
        lazyMinimumCalendar.set(Calendar.MILLISECOND, lazyMinimumCalendar.getMinimum(Calendar.MILLISECOND));
    }

    public static synchronized MinimumCalendar getInstance() {
        if (instance == null) {
            instance = new MinimumCalendar();
        }
        return instance;
    }

    public Calendar getMinimumCalendar() {
        return (Calendar) lazyMinimumCalendar.clone();
    }
}