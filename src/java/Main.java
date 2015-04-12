
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import javax.swing.JOptionPane;
import jpo.EventBus.ApplicationStartupRequest;
import jpo.EventBus.JpoEventBus;
import jpo.dataModel.Settings;
import jpo.gui.ApplicationEventHandler;


/*
 Main.java:  starting point for the JPO application

 Copyright (C) 2002 - 2015  Richard Eigenmann.
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
 * @version 0.12
 * @since JDK1.7.0
 */
public class Main {

    /**
     * The main method is the entry point for this application (or any) Java
     * application. No parameter passing is used in the Jpo application.
     * <p>
     *
     * The method verifies that the user has the correct Java Virtual Machine (>
     * 1.7.0) and then created a new {@link Jpo} object.
     *
     * @param args The command line arguments
     */
    public static void main( String[] args ) {
        // Verify that we have to correct version of the jvm
        String jvmVersion = System.getProperty( "java.version" );
        String jvmMainVersion = jvmVersion.substring( 0, jvmVersion.lastIndexOf( "." ) );
        float jvmVersionFloat = Float.parseFloat( jvmMainVersion );
        if ( jvmVersionFloat < 1.7f ) {
            String message = "The JPO application uses new features\nthat were added to the Java language in version 1.7.\nYour Java installation reports version " + jvmVersion + "\n";
            System.out.println( message );
            JOptionPane.showMessageDialog( Settings.anchorFrame, message, "Old Version Error", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        }

        // somewhat rabid way of allowing the application access to the local filesystem. RE 13. Nov 2007
        System.setSecurityManager( null );

        System.out.println( "\nJPO version 0.12\n" + "Copyright (C) 2000-2014 Richard Eigenmann\n"
                + "JPO comes with ABSOLUTELY NO WARRANTY;\n"
                + "for details Look at the Help | License menu item.\n"
                + "This is free software, and you are welcome\n"
                + "to redistribute it under certain conditions;\n"
                + "see Help | License for details.\n\n" );

        System.out.println( "Checking installation." );
        StringBuilder good = new StringBuilder( "These classes were found:\n" );
        StringBuilder missing = new StringBuilder( "The Installation is faulty! The following classes and libraries are missing:\n" );

        testClass( "jpo.gui.ApplicationEventHandler", "Jpo-0.12.jar", good, missing );
        testClass( "org.apache.commons.compress.archivers.zip.ZipArchiveEntry", "commons-compress-1.8.jar", good, missing );
        testClass( "org.apache.commons.io.IOUtils", "commons-io-2.4.jar", good, missing );
        testClass( "org.apache.commons.lang3.StringUtils", "commons-lang3-3.3.2.jar", good, missing );
        testClass( "org.apache.commons.logging.Log", "commons-logging-1.1.3.jar", good, missing );
        testClass( "org.apache.commons.net.SocketClient", "commons-new-3.3.jar", good, missing );
        testClass( "com.google.gdata.util.AuthenticationException", "gdata-core-1.0.jar", good, missing );
        testClass( "com.google.gdata.client.maps.MapsService", "gdata-maps-2.0.jar", good, missing );
        testClass( "com.google.gdata.client.media.MediaService", "gdata-media-1.0.jar", good, missing );
        testClass( "com.google.gdata.client.photos.PicasawebService", "gdata-photos-2.0.jar", good, missing );
        testClass( "com.google.common.math.IntMath", "guava-16.0.1.jar", good, missing );
        testClass( "com.google.common.eventbus.EventBus", "guava-16.0.1.jar", good, missing );
        testClass( "javax.mail.Message", "javax-mail-1.5.1.jar", good, missing );
        testClass( "org.apache.jcs.JCS", "jcs-1.3.jar", good, missing );
        testClass( "com.jcraft.jsch.JSch", "jsch-0.1.51.jar", good, missing );
        testClass( "net.javaprog.ui.wizard.AbstractStep", "jwizz-0.1.4.jar", good, missing );
        testClass( "org.jdesktop.swingx.JXMapViewer", "jxmapviewer2-1.1.jar", good, missing );
        testClass( "com.drew.imaging.jpeg.JpegMetadataReader", "metadata-extractor-2.6.4.jar", good, missing );
        testClass( "net.miginfocom.swing.MigLayout", "miglayout-4.0.jar", good, missing );
        testClass( "com.adobe.xmp.XMPUtils", "xmpcore.jar", good, missing );

        if ( missing.length() > 80 ) {
            System.out.println( missing.toString() );
        }

        new ApplicationEventHandler();
        JpoEventBus.getInstance().post( new ApplicationStartupRequest() );

    }

    /**
     * A little helper method which checks if the class can be loaded and then
     * appends a little text to the good or missing strings.
     *
     * @param className The class to test for
     * @param libraryName The library where this is normally found
     * @param good the string to append the good message
     * @param missing the string to append the missing message
     */
    private static void testClass( String className, String libraryName, StringBuilder good, StringBuilder missing ) {
        try {
            Class.forName( className );
            good.append( className ).append( " (from " ).append( libraryName ).append( ")\n" );
        } catch ( ClassNotFoundException e ) {
            missing.append( className ).append( " (from " ).append( libraryName ).append( ")\n" );
        }
    }
}
