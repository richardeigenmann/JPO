package jpo;

import junit.framework.*;

/*
 * ApplicationJMenuBarTest.java
 * JUnit based test
 *
 */

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailBrowserTest extends TestCase implements RelayoutListener{
    
    public ThumbnailBrowserTest(String testName) {
        super(testName);
    }
    
    private int countAssignThumbnailInvocations;
    
    protected void setUp() throws Exception {
        countAssignThumbnailInvocations = 0;
    }
    
    protected void tearDown() throws Exception {
    }
    
    
    
    public void testRelayoutListener() {
        MockThumbnailBrowser tb = new MockThumbnailBrowser();
        tb.addRelayoutListener( this );
        tb.notifyRelayoutListeners();
        tb.removeRelayoutListener( this );
        tb.notifyRelayoutListeners();
        assertEquals("Checking that the RelayoutListener was fired off the correct number of times", 1, countAssignThumbnailInvocations );
    }
    
    public void assignThumbnails() {
        countAssignThumbnailInvocations++;
    }
    
    
    
}