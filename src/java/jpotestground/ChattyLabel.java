/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpotestground;

import java.awt.Dimension;
import javax.swing.JLabel;

/**
 *
 * @author richi
 */
public class ChattyLabel extends JLabel {

    @Override
    public Dimension getPreferredSize() {
        System.out.println( "ChattyLabel.getPreferredSize called. Sending: " + super.getPreferredSize().toString() );
        return super.getPreferredSize();
    }

    @Override
    public int getWidth() {
        System.out.println( "ChattyLabel.getWidth called. Sending: " + Integer.toString( super.getWidth() ) );
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        System.out.println( "ChattyLabel.getHeight called. Sending: " + Integer.toString( super.getHeight() ) );
        return super.getHeight();
    }

    @Override
    public void setText( String arg0 ) {
        System.out.println( "ChattyLabel.setText called. Sending: " + arg0 );
        super.setText( arg0 );
    }





}
