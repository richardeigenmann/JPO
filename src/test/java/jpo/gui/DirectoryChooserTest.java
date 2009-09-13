package jpo.gui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class DirectoryChooserTest extends TestCase {

    public DirectoryChooserTest(String testName) {
        super(testName);
    }
    int changesReceived = 0;
    File result = null;

    public void testListener() {
        Runnable r = new Runnable() {

            public void run() {
                DirectoryChooser dc = new DirectoryChooser("Title", DirectoryChooser.DIR_MUST_EXIST);
                dc.addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        changesReceived++;
                    }
                });
                dc.setText("/");
                result = dc.getDirectory();
            }
        };
        try {
            SwingUtilities.invokeAndWait(r);
        } catch (InterruptedException ex) {
            Logger.getLogger(DirectoryChooserTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DirectoryChooserTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertEquals("Checking that what went in is what comes out", new File("/"), result);
        assertEquals("Checking that the changeEvent was fired", 1, changesReceived);

    }
}
