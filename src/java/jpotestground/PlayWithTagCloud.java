/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpotestground;

import jpo.TagCloud.TagClickListener;
import jpo.TagCloud.WordBrowser;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 *Test
 * @author richi
 */
public class PlayWithTagCloud extends JFrame implements TagClickListener {

    private static Logger logger = Logger.getLogger( PlayWithTagCloud.class.getName() );


    /**
     * Static Initialiser to turn on finest logging>
     */
    {
        Handler[] handlers =
                Logger.getLogger( "" ).getHandlers();
        for ( int index = 0; index < handlers.length; index++ ) {
            handlers[index].setLevel( Level.FINEST );
        }
    }


    public PlayWithTagCloud() {

        setPreferredSize( new Dimension( 400, 400 ) );
        WordBrowser wb = new WordBrowser( getSomeNodes(), this, 10 );
        this.getContentPane().add( wb );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        pack();
        setVisible( true );

    }


    public static void main( String[] args ) {
        Runnable r = new Runnable() {

            public void run() {
                new PlayWithTagCloud();
            }
        };
        SwingUtilities.invokeLater( r );


    }


    public void tagClicked( String key, HashSet<SortableDefaultMutableTreeNode> hs ) {
        logger.info( "Tag Clicked: " + key + " number of nodes: " + Integer.toString( hs.size() ) );
    }


    public static SortableDefaultMutableTreeNode getSomeNodes() {
        SortableDefaultMutableTreeNode rootNode = new SortableDefaultMutableTreeNode( new GroupInfo( "Root Node" ) );

        PictureInfo pi1 = new PictureInfo();
        pi1.setDescription( "Welcome to Anchorage" );
        SortableDefaultMutableTreeNode pictureNode1 = new SortableDefaultMutableTreeNode( pi1 );

        PictureInfo pi2 = new PictureInfo();
        pi2.setDescription( "Welcome to Zürich" );
        SortableDefaultMutableTreeNode pictureNode2 = new SortableDefaultMutableTreeNode( pi2 );

        PictureInfo pi3 = new PictureInfo();
        pi3.setDescription( "Welcome to New York" );
        SortableDefaultMutableTreeNode pictureNode3 = new SortableDefaultMutableTreeNode( pi3 );

        PictureInfo pi4 = new PictureInfo();
        pi4.setDescription( "New York by Night" );
        SortableDefaultMutableTreeNode pictureNode4 = new SortableDefaultMutableTreeNode( pi4 );

        PictureInfo pi5 = new PictureInfo();
        pi5.setDescription( "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, At accusam aliquyam diam diam dolore dolores duo eirmod eos erat, et nonumy sed tempor et et invidunt justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat. Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet." );
        SortableDefaultMutableTreeNode pictureNode5 = new SortableDefaultMutableTreeNode( pi5 );

        rootNode.add( pictureNode1 );
        rootNode.add( pictureNode2 );
        rootNode.add( pictureNode3 );
        rootNode.add( pictureNode4 );
        rootNode.add( pictureNode5 );

        return rootNode;
    }
}
