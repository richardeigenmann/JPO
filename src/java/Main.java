
import javax.swing.JOptionPane;
import jpo.dataModel.Settings;
import jpo.gui.Jpo;

/*
 Main.java:  starting point for the JPO application

 Copyright (C) 2002 - 2012  Richard Eigenmann.
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
 * @version 0.10
 * @since JDK1.6.0
 */
public class Main {

    /**
     * The main method is the entry point for this application (or any) Java
     * application. No parameter passing is used in the Jpo application. <p>
     *
     * The method verifies that the user has the correct Java Virtual Machine (>
     * 1.6.0) and then created a new {@link Jpo} object.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        // Verify that we have to correct version of the jvm
        String jvmVersion = System.getProperty("java.version");
        String jvmMainVersion = jvmVersion.substring(0, jvmVersion.lastIndexOf("."));
        float jvmVersionFloat = Float.parseFloat(jvmMainVersion);
        if (jvmVersionFloat < 1.6f) {
            String message = "The JPO application uses new features\nthat were added to the Java language in version 1.6.\nYour Java installation reports version " + jvmVersion + "\n";
            System.out.println(message);
            JOptionPane.showMessageDialog(Settings.anchorFrame, message, "Old Version Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // somewhat rabid way of allowing the application access to the local filesystem. RE 13. Nov 2007
        System.setSecurityManager(null);

        System.out.println("\nJPO version 0.10\n" + "Copyright (C) 2000-2012 Richard Eigenmann\n"
                + "JPO comes with ABSOLUTELY NO WARRANTY;\n"
                + "for details Look at the Help | License menu item.\n"
                + "This is free software, and you are welcome\n"
                + "to redistribute it under certain conditions;\n"
                + "see Help | License for details.\n\n");

        try {
            Class.forName("jpo.gui.Jpo");
            System.out.println("Class jpo.gui.Jpo exists");
        } catch (Exception e) {
            System.out.println("Class jpo.gui.Jpo missing");
        }

        try {
            Class.forName("com.drew.imaging.jpeg.JpegMetadataReader");
            System.out.println("Class com.drew.imaging.jpeg.JpegMetadataReader exists");
        } catch (Exception e) {
            System.out.println("Class com.drew.imaging.jpeg.JpegMetadataReader missing");
        }

        new Jpo();
    }
}