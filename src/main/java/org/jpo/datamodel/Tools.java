package org.jpo.datamodel;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jpo.gui.SourcePicture;
import org.jpo.gui.swing.EdtViolationException;

import javax.swing.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;


/*
 Copyright (C) 2002-2020  Richard Eigenmann.
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
 * separate class to hold a collection of static methods that are frequently
 * needed.
 */
public class Tools {

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
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(Tools.class.getName());


    /**
     * method that tests the file extension of a File object for being the
     * correct extension. Either the same file object is returned or a new one
     * is created with the correct extension. If not the correct extension is
     * added. The case of the extension is ignored.
     *
     * @param extension The extension
     * @param testFile  File to test
     * @return the file
     */
    public static File correctFilenameExtension(String extension, File testFile) {
        if (!testFile.getName().toUpperCase().endsWith(extension.toUpperCase())) {
            return new File(testFile.getPath() + "." + extension);
        }
        return testFile;
    }

    /**
     * Counts the number of real files in the array of files.
     *
     * @param fileArray The files to count
     * @return the number of real files in the array of files
     */
    public static int countfiles(final File[] fileArray) {
        if (fileArray == null) {
            return 0;
        }

        int numFiles = 0;
        for (final File fileEntry : fileArray) {
            try {
                if (!fileEntry.isDirectory()) {
                    numFiles++;
                } else {
                    numFiles += countfiles(fileEntry.listFiles());
                }
            } catch (final SecurityException x) {
                // Log the error and ignore it and continue
                LOGGER.log(Level.INFO, "Got a SecurityException on file: {0} \n{1}", new Object[]{fileEntry, x.getMessage()});
            }
        }
        return numFiles;
    }


    /**
     * This method looks into the supplied subdirectory and tries to see if
     * there is at least one picture in it for which our Java Environment has a
     * decoder.
     *
     * @param subDirectory The File representing the subdirectory to be
     *                     recursively searched
     * @return true if there is at least one picture in the subdirectory, false
     * if there is nothing.
     */
    public static boolean hasPictures(final File subDirectory) {
        final File[] fileArray = subDirectory.listFiles();
        if (fileArray == null) {
            return false;
        }

        for (final File file : fileArray) {
            if (file.isDirectory()) {
                if (hasPictures(file)) {
                    return true;
                }
            } else if (SourcePicture.jvmHasReader(file)) {
                return true;
            }
        }
        return false;
    }


    /**
     * method to copy any file from sourceUrl source location to sourceUrl
     * target File location. Works better because files are writable whilst most
     * URL are read only.
     *
     * @param sourceFile source URL
     * @param targetFile target file
     */
    public static void copyPicture(final File sourceFile, final File targetFile) {
        try (
                final InputStream in = new FileInputStream(sourceFile);
                final OutputStream out = new FileOutputStream(targetFile)) {

            final BufferedInputStream bin = new BufferedInputStream(in);
            final BufferedOutputStream bout = new BufferedOutputStream(out);

            copyBufferedStream(bin, bout);
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(
                    Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("copyPictureError1")
                            + sourceFile.toString()
                            + Settings.getJpoResources().getString("copyPictureError2")
                            + targetFile.toString()
                            + Settings.getJpoResources().getString("copyPictureError3")
                            + e.getMessage(),
                    Settings.getJpoResources().getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * method to copy any file from a source stream to a output stream
     *
     * @param bin  Buffered Input Stream
     * @param bout Buffered Output Stream
     * @return the crc of the file
     * @throws IOException Exception of error
     */
    public static long copyBufferedStream(final BufferedInputStream bin,
                                          final BufferedOutputStream bout)
            throws IOException {

        Adler32 crc = new Adler32();
        int c;

        while ((c = bin.read()) != -1) {
            bout.write(c);
            crc.update(c);
        }

        bin.close();
        bout.close();
        return crc.getValue();

    }


    /**
     * Method that returns a file handle for a picture that does not exist in
     * the target directory. It tries the combination of path and name first and
     * then tries to suffix _0 _1 _2 etc to the name. If it returns null then it
     * failed
     *
     * @param targetDir the directory in which the picture needs to go
     * @param startName the name to start from
     * @return the new picture filename
     */
    @NotNull
    public static File inventPicFilename(final File targetDir, final String startName) {
        File testFile = new File(targetDir, startName);
        if (!testFile.exists()) {
            return testFile;
        }

        int dotPoint = startName.lastIndexOf('.');
        final String startNameRoot = startName.substring(0, dotPoint);
        final String startNameSuffix = startName.substring(dotPoint);

        for (int i = 1; i < 50; i++) {
            testFile = new File(targetDir, startNameRoot + "_" + i + startNameSuffix);
            if (!testFile.exists()) {
                return testFile;
            }
        }

        for (int i = 1; i < 50; i++) {
            testFile = new File(targetDir, startNameRoot + "_" + RandomStringUtils.random(10, true, true));
            if (!testFile.exists()) {
                return testFile;
            }
        }

        LOGGER.log(Level.SEVERE, "Could not invent a picture filename for the directory {0} and the name {1} returning any long string", new Object[]{targetDir, startName});

        return new File(targetDir, RandomStringUtils.random(50, true, true));
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
        return (Settings.getJpoResources().getString("freeMemory") + freeMemory + "MB/" + totalMemory + "MB/" + maxMemory + "MB");
    }

    /**
     * Brings up a popup about having run out of memory and runs a Garbage
     * Collection
     */
    public static void dealOutOfMemoryError() {
        Tools.freeMem();
        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("outOfMemoryError"),
                        Settings.getJpoResources().getString("genericError"),
                        JOptionPane.ERROR_MESSAGE)
        );

        System.runFinalization();
        LOGGER.info("ScalablePicture.scalePicture: JPO has now run a garbage collection and finalization.");
        Tools.freeMem();
    }


    /**
     * Returns a checksum out of the contents of the the supplied File
     *
     * @param file The file to checksum
     * @return returns the checksum as a Long or Long.MIN_VALUE to indicate
     * failure.
     */
    public static long calculateChecksum(final File file) {
        long checksum;
        try {
            checksum = calculateChecksum(new BufferedInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException x) {
            checksum = Long.MIN_VALUE;
        }
        return checksum;
    }

    /**
     * Returns a checksum from the supplied input stream using Adler32 crc.
     * Originally taken from: Java ist auch eine Insel (2. Aufl.) von Christian
     * Ullenboom Programmieren für die Java 2-Plattform inputStream der Version
     * 1.4
     *
     * @param inputStream The InputStream to read
     * @return returns the checksum as a Long or Long.MIN_VALUE to indicate
     * failure.
     */
    public static long calculateChecksum(final InputStream inputStream) {
        warnOnEDT();
        final Adler32 crc = new Adler32();
        int blockLen;

        try {
            while ((blockLen = inputStream.available()) > 0) {
                byte[] ba = new byte[blockLen];
                int read = inputStream.read(ba);
                crc.update(ba, 0, read);
            }
            return crc.getValue();
        } catch (IOException x) {
            LOGGER.log(Level.INFO, "Tools.calculateChecksum trapped an IOException. Aborting. Reason:\n{0}", x.getMessage());
            return Long.MIN_VALUE;
        }

    }

    /**
     * returns the current date and time formatted per the formatting string.
     * See the API doc on SimpleDateFormat for the meaning of the letters.
     *
     * @param formatString The format string
     * @return current date and time
     */
    public static String currentDate(final String formatString) {
        final SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        final Date currentTime = new Date();
        return formatter.format(currentTime);
    }

    /**
     * This method tries it's best to parse the supplied date into a Java Date
     * object.
     *
     * @param dateString the String to be parsed
     * @return the Java Calendar object or null if it could not be parsed.
     */
    public static Calendar parseDate(final String dateString) {
        final SimpleDateFormat df = new SimpleDateFormat();
        df.setLenient(true);
        final String[] patterns = {
                "dd.MM.yyyy HH:mm:ss",
                "dd.MM.yyyy HH:mm",
                "dd.MM.yyyy",
                "yyyy:MM:dd HH:mm:ss",
                "yyyy:MM:dd HH:mm",
                "yyyy:MM:dd",
                "MM.yyyy",
                "MM-yyyy",
                "dd-MM-yyyy",
                "dd.MM.yy",
                "dd-MM-yy",
                "MM/dd/yy HH:mm:ss",
                "MM/dd/yy HH:mm",
                "MM/dd/yy",
                "MM/dd/yyyy HH:mm:ss",
                "MM/dd/yyyy HH:mm",
                "MM/dd/yyyy",
                "dd MMM yyyy",
                "dd MMM yy",
                "yyyy"
        };
        Date d = null;
        boolean notFound = true;
        for (int i = 0; (i < patterns.length) && notFound; i++) {
            try {
                df.applyPattern(patterns[i]);
                d = df.parse(dateString);
                notFound = false;
            } catch (ParseException x) {
                // skip and continue with next pattern
            }
        }
        if (d != null) {
            final Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        } else {
            return null;
        }
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

    /**
     * This method writes a warning to the log that we are on the EDT and should
     * not be. It also dumps a stack trace. Intended for debugging slow running
     * processes that should not be on the EDT.
     */
    public static void warnOnEDT() {
        if (SwingUtilities.isEventDispatchThread()) {
            LOGGER.warning("We are on the EDT and should not be! This is inefficient Continuing normally.");
            Thread.dumpStack();
        }
    }


}
