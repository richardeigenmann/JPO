package jpo.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RepaintManager;
import jpo.EventBus.DebugEventListener;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.OpenMainWindowRequest;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.StartCameraWatchDaemonRequest;
import jpo.EventBus.StartNewCollectionRequest;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.gui.swing.CollectionJTree;
import jpotestground.CheckThreadViolationRepaintManager;


/*
 Jpo.java:  The collection controller object of the JPO application

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * This is the collection controller the Java Picture Organizer application that
 * lets a user view a collection of pictures in as thumbnails, in a separate
 * window or in a full sized mode.<p>
 *
 *
 * <p>
 * <img src=../Overview.png border=0><p>
 *
 * It uses a list of pictures (PictureList file) to create a hierarchical model
 * of <code>SortableDefaultMutableTreeNode</code>s that represent the structure
 * of the collection. Each node has an associated object of {@link GroupInfo} or
 * {@link PictureInfo} type.
 *
 * The Jpo class creates the following main objects:
 *
 * The {@link CollectionJTreeController} visualises the model and allows the
 * user to expand and collapse branches of the tree with the mouse. If a node is
 * clicked this generates a <code>valueChanged</code> event from the model which
 * is sent to all listening objects.<p>
 *
 * Listening objects are the thumbnail pane which displays the group if a node
 * of type <code>GroupInfo</code> has been selected.<p>
 *
 * This listener architecture allows fairly easy expansion of the application
 * since all that is required is that any additional objects that need to be
 * change the picture or need to be informed of a change can connect to the
 * model in this manner and need no other controls.
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 * @version 0.12
 * @see CollectionJTree
 * @see ThumbnailsPanelController
 * @see PictureViewer
 * @since JDK1.7.0
 */
public class Jpo {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( Jpo.class.getName() );

    /**
     * Constructor for the Jpo application that creates the main JFrame,
     * attaches an {@link ApplicationJMenuBar}, adds a JSplitPane to which it
     * adds the {@link CollectionJTreeController} on the left side and a
     * {@link ThumbnailsPanelController} on the right side.
     */
    public Jpo() {

        LOGGER.info( "------------------------------------------------------------\n      Starting JPO" );

        // Check for EDT violations
        RepaintManager.setCurrentManager( new CheckThreadViolationRepaintManager() );

        Settings.loadSettings();

        new ApplicationEventHandler();

        JpoEventBus.getInstance().register( new DebugEventListener() );
        JpoEventBus.getInstance().post( new OpenMainWindowRequest() );

        if ( !loadAutoloadCollection() ) {
            JpoEventBus.getInstance().post( new StartNewCollectionRequest() );
        }

        final List<ThumbnailCreationFactory> THUMBNAIL_FACTORIES = new ArrayList<>();
        for ( int i = 1; i <= Settings.numberOfThumbnailCreationThreads; i++ ) {
            THUMBNAIL_FACTORIES.add( new ThumbnailCreationFactory() );
        }
        JpoEventBus.getInstance().post( new StartCameraWatchDaemonRequest() );

    }

    /**
     * This method looks if it can find a file called
     * autostartJarPicturelist.xml in the classpath; failing that it loads the
     * file indicated in Settings.autoLoad.
     *
     * @return returns whether this was successful or not.
     */
    public boolean loadAutoloadCollection() {
        if ( ( Settings.autoLoad != null ) && ( Settings.autoLoad.length() > 0 ) ) {
            File xmlFile = new File( Settings.autoLoad );
            LOGGER.log( Level.FINE, "File to Autoload: {0}", Settings.autoLoad );
            if ( !xmlFile.exists() ) {
                LOGGER.fine( String.format( "File %s doesn't exist. not loading", xmlFile.toString() ) );
                return false;
            } else {
                try {
                    Settings.pictureCollection.fileLoad( xmlFile );
                } catch ( FileNotFoundException ex ) {
                    Logger.getLogger( Jpo.class.getName() ).log( Level.SEVERE, null, ex );
                    return false;
                }

                JpoEventBus.getInstance().post( new ShowGroupRequest( Settings.pictureCollection.getRootNode() ) );

                return true;
            }
        } else {
            return false;
        }
    }

}
