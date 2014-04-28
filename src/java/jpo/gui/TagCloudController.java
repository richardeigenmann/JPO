package jpo.gui;

import com.google.common.eventbus.Subscribe;
import jpo.TagCloud.TagClickListener;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowQueryRequest;
import jpo.dataModel.DescriptionWordMap;
import jpo.TagCloud.TagCloud;
import jpo.dataModel.Settings;
import jpo.dataModel.StaticNodesQuery;

/*
 TagCloudController.java:  The Controller for the TagCloud

 Copyright (C) 2009-2014  Richard Eigenmann.
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
 * Manages the Tag Cloud
 */
public class TagCloudController implements TagClickListener {

    /**
     * Reference to the TagCloud widget
     */
    private final TagCloud tagCloud = new TagCloud();

    public TagCloudController() {
        JpoEventBus.getInstance().register( this );
        tagCloud.addTagClickListener( TagCloudController.this );
        tagCloud.setMaxWordsToShow( Settings.tagCloudWords );
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( TagCloudController.class.getName() );

    private DescriptionWordMap descriptionWordMap;

    public JComponent getTagCloud() {
        return tagCloud;
    }

    /**
     * Handles the ShowGroupRequest by updating the display...
     *
     * @param event
     */
    @Subscribe
    public void handleGroupSelectionEvent( final ShowGroupRequest event ) {
        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {
                descriptionWordMap = new DescriptionWordMap( event.getNode() );
                tagCloud.setWordMap( descriptionWordMap );
                tagCloud.showWords();
            }
        } );
    }


    @Override
    public void tagClicked( String key ) {
        Set<SortableDefaultMutableTreeNode> set = descriptionWordMap.getWordNodeMap().get( key );
        StaticNodesQuery query = new StaticNodesQuery( "Word: " + key, new ArrayList<SortableDefaultMutableTreeNode>( set ) );
        JpoEventBus.getInstance().post( new ShowQueryRequest( query ) );
    }
}
