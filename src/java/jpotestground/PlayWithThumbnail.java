/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpotestground;

import jpo.dataModel.Settings;
import jpo.gui.ThumbnailController;
import jpo.*;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *Test
 * @author Richard Eigenmann
 */
public class PlayWithThumbnail extends JFrame implements ChangeListener {

    static final int MIN = 100;

    static final int MAX = 350;

    static final int INIT = 200;

    JPanel p = new JPanel();

    JSlider s = new JSlider( JSlider.HORIZONTAL, MIN, MAX, INIT );
    //JLabel l = new ChattyLabel();

    JLabel l = new JLabel();

    ThumbnailController thumbnailController = new ThumbnailController( 350 );

    Box box = new Box();

    ImageIcon testimage = new ImageIcon( Settings.cl.getResource( "jpo/images/testimage.jpg" ) );


    public PlayWithThumbnail() {
        s.addChangeListener( this );
        p.setLayout( new BoxLayout( p, BoxLayout.PAGE_AXIS ) );

        p.add( s );
        p.add( l );
        p.add( thumbnailController.getThumbnail() );
        //p.add( box );
        this.getContentPane().add( p );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        l.setText( "Starting with: " + Integer.toString( INIT ) );
        thumbnailController.getThumbnail().setThumbnail( testimage );
        setPreferredSize( new Dimension( 400, 400 ) );
        pack();
        setVisible( true );

    }


    public static void main( String[] args ) {
        new PlayWithThumbnail();

    }


    public void stateChanged( ChangeEvent e ) {
        JSlider source = (JSlider) e.getSource();
        //if ( !source.getValueIsAdjusting() ) {
        int value = source.getValue();
        float scale = (float) value / MAX;
        l.setText( Integer.toString( value ) + " --> " + Float.toString( scale * 100 ) + "%" );

        thumbnailController.setFactor( scale );
        //}
    }
}
