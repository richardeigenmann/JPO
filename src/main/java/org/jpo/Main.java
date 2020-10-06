package org.jpo;

import org.jpo.datamodel.Settings;
import org.jpo.eventbus.ApplicationStartupRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.gui.ApplicationEventHandler;

import javax.swing.*;


/*
 org.jpo.Main.java:  starting point for the JPO application

 Copyright (C) 2002 - 2020  Richard Eigenmann.
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
 * The first class to be started to get the JPO application going.
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 * @version 0.14
 * @since JDK1.12.0
 */
public class Main {

   /**
     * The method verifies that the user has the correct Java Virtual Machine (&gt;
     * 1.7.0) and then creates a new {@link ApplicationEventHandler} object which
     * it asks to post a {@link ApplicationStartupRequest} to.
     * @return true is good, false if bad
     */
    private static boolean verifyJavaVersion() {
        final String jvmVersion = System.getProperty("java.version");
        String jvmMainVersion;
        if ( jvmVersion.lastIndexOf('.') > 0 ) {
            jvmMainVersion = jvmVersion.substring( 0, jvmVersion.lastIndexOf('.') );
        } else {
            // From Java 9 upward
            jvmMainVersion = jvmVersion;
        }
        final float jvmVersionFloat = Float.parseFloat(jvmMainVersion);
        return ( jvmVersionFloat >= 1.8f );
    }

    /**
     * The main method is the entry point for this application (or any) Java
     * application. No parameter passing is used in the Jpo application. It checks if
     * the most important classes can be loaded and then posts the
     * ApplicationStartupRequest.
     *
     * @see ApplicationStartupRequest
     * @param args The command line arguments
     */
    public static void main( String[] args ) {
        if (! verifyJavaVersion() ) {
            final String message = "The JPO application uses new features\nthat were added to the Java language in version 1.8.\nYour Java installation reports version " + System.getProperty("java.version") + "\n";
            System.out.println(message);
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(), message, "Old Version Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // somewhat rabid way of allowing the application access to the local filesystem. RE 13. Nov 2007
        System.setSecurityManager( null );

        System.out.println( "\nJPO version 0.14\n"
                + "Copyright (C) 2000-2020 Richard Eigenmann,\nZurich, Switzerland\n"
                + "JPO comes with ABSOLUTELY NO WARRANTY;\n"
                + "for details Look at the Help | License menu item.\n"
                + "This is free software, and you are welcome\n"
                + "to redistribute it under certain conditions;\n"
                + "see Help | License for details.\n\n" );

        System.out.println( "Checking that we have all jar/class files available..." );
        StringBuilder foundClasses = new StringBuilder( "These classes were found:\n" );
        StringBuilder missingClasses = new StringBuilder( "The Installation is faulty! The following classes and libraries are missing:\n" );

        isClassLoadable("org.jpo.gui.ApplicationEventHandler", "Jpo-0.14.jar", foundClasses, missingClasses );
        isClassLoadable("org.tagcloud.TagCloud", "TagCloud.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.compress.archivers.zip.ZipArchiveEntry", "commons-compress-1.8.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.io.IOUtils", "commons-io-2.4.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.jcs.JCS", "commons-jcs-core-2.0-beta-1.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.jcs.jcache.Asserts", "commons-jcs-jcache-2.0-beta-1.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.lang3.StringUtils", "commons-lang3-3.3.2.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.logging.Log", "commons-logging-1.1.3.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.net.SocketClient", "commons-net-3.3.jar", foundClasses, missingClasses );
        isClassLoadable("org.apache.commons.text.StringEscapeUtils", "commons-text-1.1.jar", foundClasses, missingClasses );
        isClassLoadable("bibliothek.gui.dock.common.CControl", "docking-frames-common.jar", foundClasses, missingClasses );
        isClassLoadable("bibliothek.gui.Dockable", "docking-frames-core.jar", foundClasses, missingClasses );
        isClassLoadable("com.google.gdata.util.AuthenticationException", "gdata-core-1.0.jar", foundClasses, missingClasses );
        isClassLoadable("com.google.gdata.client.media.MediaService", "gdata-media-1.0.jar", foundClasses, missingClasses );
        isClassLoadable("com.google.gdata.client.photos.PicasawebService", "gdata-photos-2.0.jar", foundClasses, missingClasses );
        isClassLoadable("com.google.common.math.IntMath", "guava-16.0.1.jar", foundClasses, missingClasses );
        isClassLoadable("com.google.common.eventbus.EventBus", "guava-16.0.1.jar", foundClasses, missingClasses );
        isClassLoadable("javax.mail.Message", "javax-mail-1.5.1.jar", foundClasses, missingClasses );
        isClassLoadable("com.jcraft.jsch.JSch", "jsch-0.1.51.jar", foundClasses, missingClasses );
        isClassLoadable("net.javaprog.ui.wizard.AbstractStep", "jwizz-0.1.4.jar", foundClasses, missingClasses );
        isClassLoadable("org.jxmapviewer.JXMapViewer", "jxmapviewer2-2.0.jar", foundClasses, missingClasses );
        isClassLoadable("com.drew.imaging.jpeg.JpegMetadataReader", "metadata-extractor-2.8.1.jar", foundClasses, missingClasses );
        isClassLoadable("net.miginfocom.swing.MigLayout", "miglayout-4.0.jar", foundClasses, missingClasses );
        isClassLoadable("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader","imageio-jpeg-3.4.1.jar", foundClasses, missingClasses );
        isClassLoadable("com.twelvemonkeys.imageio.plugins.hdr.HDRImageReaderSpi","imageio-hdr-3.4.1.jar", foundClasses, missingClasses );
        isClassLoadable("com.twelvemonkeys.imageio.plugins.tiff.TIFFImageReaderSpi","imageio-tiff-3.4.1.jar", foundClasses, missingClasses );

        if ( missingClasses.length() > 80 ) {
            System.out.println(missingClasses.toString() );
        } else {
            System.out.println("No missing classes identified.");
        }
        new ApplicationEventHandler();
        JpoEventBus.getInstance().post( new ApplicationStartupRequest() );
    }

    /**
     * Uses the Class.forName method to try to locate the class
     *
     * @param className      The class to test for
     * @param libraryName    The library where this is normally found
     * @param foundClasses   the StringBuilder to append the good message
     * @param missingClasses the StringBuilder to append the missing message
     */
    private static void isClassLoadable(final String className, final String libraryName, final StringBuilder foundClasses, final StringBuilder missingClasses) {
        try {
            Class.forName(className);
            foundClasses.append(className).append(" (from ").append(libraryName).append(")\n");
        } catch (ClassNotFoundException e) {
            missingClasses.append(className).append(" (from ").append(libraryName).append(")\n");
        }
    }
}
