/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpotestground;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author Richard Eigenmann
 */
public class Box extends JComponent {

    /**
     * The scale factor > 0 and <= 1
     */
    float scaleFactor = 1;
    int maxWidth = 350;

    public Box() {
        setPreferredSize( new Dimension( 350, 200 ) );
        setMinimumSize( new Dimension( 350, 200 ) );
        setBackground( Color.orange );
        setOpaque( true );
        //setVisible( true);
        add( new JLabel( "I exist" ) );
    //setText("I Exist");
    //setBorder( BorderFactory.createBevelBorder( TOP ));
    }

    @Override
    public Dimension getPreferredSize() {
        //System.out.println( "gps called" + super.getPreferredSize().toString() );
        //return super.getPreferredSize();
        return new Dimension( getWidth(), getHeight() );
    }

    @Override
    public int getWidth() {
        //System.out.println( "gwidth called" + Integer.toString(  super.getWidth() ) );
        //return super.getWidth();
        return (int) ( maxWidth * scaleFactor );
    }

    @Override
    public int getHeight() {
        //System.out.println( "gwidth called" + Integer.toString(  super.getWidth() ) );
        //return super.getWidth();
        return (int) ( 200 * scaleFactor );
    }

    @Override
    public void paintComponent( Graphics g ) {
        bigPaint( g );

    }

    private void bigPaint( Graphics g ) {
        System.out.println( "paint called" );
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        g.fillRect( 0, 0, getWidth(), getHeight() );
        Random r = new Random();

        for ( int i = 0; i < 2000; i++ ) {
            g.drawOval( r.nextInt( getWidth() - 100 ), r.nextInt( getHeight() - 100 ), 100, 100 );
            g.setColor( new Color( r.nextInt( 255 ), r.nextInt( 255 ), r.nextInt( 255 ), r.nextInt( 255 ) ) );
        }

    }

    /**
     *  This method sets the scaling factor for the display of a thumbnail.
     *  0 .. 1
     */
    public void setFactor( float scaleFactor ) {
        if ( scaleFactor != this.scaleFactor ) {
            this.scaleFactor = scaleFactor;
            repaint();
        } else {
            System.out.println( "same" );
        }
    }
}
