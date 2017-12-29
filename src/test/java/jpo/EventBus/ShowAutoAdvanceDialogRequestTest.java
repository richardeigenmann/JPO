package jpo.EventBus;

import com.google.common.eventbus.Subscribe;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.gui.ComponentMock;
import jpo.gui.PictureViewer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/*
 Copyright (C) 2017  Richard Eigenmann.
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
 * Test for Group Selection Events
 *
 * @author Richard Eigenmann
 */
public class ShowAutoAdvanceDialogRequestTest {

    /**
     * Constructor
     */
    public ShowAutoAdvanceDialogRequestTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    private final JpoEventBus jpoEventBus;

    /**
     * Test receiving an event.
     */
    @Test
    public void testReceivingEvent() {
        EventBusSubscriber myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        try {
            SwingUtilities.invokeAndWait( () -> {
                final ComponentMock componentMock = new ComponentMock();
                final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode();
                final PictureViewer pictureViewer = new PictureViewer();

                ShowAutoAdvanceDialogRequest showAutoAdvanceDialogRequest = new ShowAutoAdvanceDialogRequest( componentMock, node, pictureViewer );
                jpoEventBus.post( showAutoAdvanceDialogRequest );

                assertEquals( showAutoAdvanceDialogRequest, responseEvent );
                assertEquals( componentMock, responseEvent.parentComponent );
                assertEquals( node, responseEvent.currentNode );
                assertEquals( pictureViewer, responseEvent.autoAdvanceTarget);
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "Failed to send the ShowAutoAdvanceDialogRequest. Exception was: " + ex.getMessage() );
        }
    }

    /**
     * Receives the event.
     */
    private ShowAutoAdvanceDialogRequest responseEvent;

    /**
     * Subscribes to the vent.
     */
    private class EventBusSubscriber {

        @Subscribe
        public void handleGroupSelectionEvent( ShowAutoAdvanceDialogRequest event ) {
            responseEvent = event;
        }
    }

}
