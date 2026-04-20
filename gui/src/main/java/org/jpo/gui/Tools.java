package org.jpo.gui;

import org.jpo.gui.swing.EdtViolationException;

import javax.swing.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2026 Richard Eigenmann.
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
 * separate class to hold a collection of static methods that are frequently
 * needed.
 */
public class Tools {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(Tools.class.getName());

    /**
     * Private constructor to hide implicit public one. Explanation: Utility
     * classes, which are collections of static members, are not meant to be
     * instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors. From Sonarcloud bug report
     * <p>
     * Java adds an implicit public constructor to every class which does not
     * define at least one explicitly. Hence, at least one non-public
     * constructor should be defined.
     */
    private Tools() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Counts the number of real files in the array of files.
     *
     * @param fileArray The files to count
     * @return the number of real files in the array of files
     */
    public static int countFiles(final File[] fileArray) {
        if (fileArray == null) {
            return 0;
        }

        int numFiles = 0;
        for (final File fileEntry : fileArray) {
            try {
                if (!fileEntry.isDirectory()) {
                    numFiles++;
                } else {
                    numFiles += countFiles(fileEntry.listFiles());
                }
            } catch (final SecurityException x) {
                // Log the error and ignore it and continue
                LOGGER.log(Level.INFO, "Got a SecurityException on file: {0} \n{1}", new Object[]{fileEntry, x.getMessage()});
            }
        }
        return numFiles;
    }




    /**
     * convenience method to log the amount of free memory
     */
    public static void freeMem() {
        int memory = (int) (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        LOGGER.log(Level.INFO, "Free memory: {0}MB", memory);
    }

    /**
     * convenience method to log the amount of free memory. Shows freeMemory,
     * totalMemory and maxMemory
     *
     * @return free memory
     */
    public static String freeMemory() {
        int freeMemory = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        int totalMemory = (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024 / 1024;
        return (JpoResources.getResource("freeMemory") + freeMemory + "MB/" + totalMemory + "MB/" + maxMemory + "MB");
    }


    /*
     * This method tries its best to parse the supplied date into a Java Date
     * object. If the date can't be parsed it returns null.
     *
     * @param dateString the String to be parsed
     * @return the Java Calendar object with the parsed date
     */
    public static Calendar parseDate(final String dateString) {
        final String[] patterns = {
                "uuuu[:L:dd[ HH:mm[:ss]]]",
                "uu[:L:dd[ HH:mm[:ss]]]",
                "[dd.L.]uuuu[ HH:mm[:ss]]",
                "L/dd/uuuu[ HH:mm[:ss]]",
                "L/dd/uu[ HH:mm[:ss]]",
                "L.uuuu",
                "d.L.uuuu",
                "dd.L.uu",
                "L-uuuu",
                "dd-L-uuuu",
                "dd LLL uu",
                "dd LLL uuuu",
                "dd-L-uu",
                "uuuu-L-dd[ HH:mm[:ss]]",
                "uuuu-L-dd[ HH.mm[.ss]]",
                "uuuu-L-dd[ 'at' HH.mm[.ss]]",
                "uuuuLLdd"
        };
        for (final String pattern : patterns) {
            try {
                final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern(pattern)
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                        .toFormatter();
                final LocalDateTime parsedResult = LocalDateTime.parse(dateString, formatter);
                LOGGER.log(Level.FINE, "Matched {0} on pattern {1}", new Object[]{dateString, pattern});
                return GregorianCalendar.from(ZonedDateTime.of(parsedResult, ZoneId.systemDefault()));
            } catch (final DateTimeParseException _) {
                // skip and continue with the next pattern
            }
        }
        return null;
    }


    /**
     * This helper method checks if the execution is on the EventDisplayThread
     * and throws an Error if it is not. It is a Java Swing requirement that
     * graphical operations must be done on the EDT or strange things SOMETIMES
     * happen. This method allows easy checking by writing:
     * <code>Tools.checkEDT()</code>
     */
    public static void checkEDT() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new EdtViolationException("Not on EDT! Throwing error.");
        }
    }


}
