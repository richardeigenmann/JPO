package jpo.gui;

import jpo.dataModel.Settings;
import jpo.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/*
Copyright (C) 2002-2007  Richard Eigenmann.
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
 *
 * A class to test the ResizableJFrame. This can be problematic
 * because different users are likely to have different screen configurations.
 */
public class ResizableJFrameTest {
    
    /**
     *  An entry point for standalone screen size testing.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        final ResizableJFrame rjf = new ResizableJFrame( "Title", true );
        Settings.loadSettings();
        rjf.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        rjf.setSize( new Dimension( 800, 600 ) );
        
        JPanel buttonPanel = new JPanel();
        JButton fullScreen = new JButton( "FullScreen");
        fullScreen.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.maximise();
                rjf.resizeTo( ResizableJFrame.WINDOW_FULLSCREEN );
            }
        } );
        buttonPanel.add( fullScreen );
        
        JButton normalScreen = new JButton( "Normal");
        normalScreen.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.unMaximise();
                rjf.resizeTo( ResizableJFrame.WINDOW_DEFAULT );
                
            }
        } );
        buttonPanel.add( normalScreen );
        
        JButton leftSize = new JButton( "Left");
        leftSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.reziseToLeft();
                rjf.resizeTo( ResizableJFrame.WINDOW_LEFT );
            }
        } );
        buttonPanel.add( leftSize );
        
        JButton topLeftSize = new JButton( "Top Left");
        topLeftSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.reziseToTopLeft();
                rjf.resizeTo( ResizableJFrame.WINDOW_TOP_LEFT );
                
            }
        } );
        buttonPanel.add( topLeftSize );
        
        JButton bottomLeftSize = new JButton( "Bottom Left");
        bottomLeftSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.reziseToBottomLeft();
                rjf.resizeTo( ResizableJFrame.WINDOW_BOTTOM_LEFT );
                
            }
        } );
        buttonPanel.add( bottomLeftSize );
        
        
        JButton rightSize = new JButton( "Right");
        rightSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.reziseToRight();
                rjf.resizeTo( ResizableJFrame.WINDOW_RIGHT );
            }
        } );
        buttonPanel.add( rightSize );
        
        JButton topRightSize = new JButton( "Top Right");
        topRightSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.reziseToTopRight();
                rjf.resizeTo( ResizableJFrame.WINDOW_TOP_RIGHT );
            }
        } );
        buttonPanel.add( topRightSize );
        
        JButton bottomRightSize = new JButton( "Bottom Right");
        bottomRightSize.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //rjf.reziseToBottomRight();
                rjf.resizeTo( ResizableJFrame.WINDOW_BOTTOM_RIGHT );
            }
        } );
        buttonPanel.add( bottomRightSize );
        
        
        final JTextArea jta = new JTextArea( 20, 80 );
        jta.setCaret( new NonFocussedCaret() );
        
        
        JButton refresh = new JButton( "Refresh");
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                jta.setText( ScreenHelper.explainGraphicsEnvironment().toString() );
            }
        } );
        buttonPanel.add( refresh );
        
        
        JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );
        p.add( buttonPanel, BorderLayout.NORTH );
        final JScrollPane jsp = new JScrollPane( jta );
        p.add( jsp, BorderLayout.CENTER );
        
        rjf.getContentPane().add( p );
        rjf.validate();
        
        jta.setText( ScreenHelper.explainGraphicsEnvironment().toString() );
    }
    
}
