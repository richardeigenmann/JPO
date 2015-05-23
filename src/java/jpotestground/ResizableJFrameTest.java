package jpotestground;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpo.gui.swing.ScreenHelper;
import jpo.gui.swing.ResizableJFrame;
import jpo.gui.swing.NonFocussedCaret;
import jpo.dataModel.Settings;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_BOTTOM_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_BOTTOM_RIGHT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_DEFAULT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_FULLSCREEN;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_RIGHT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_TOP_LEFT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_TOP_RIGHT;


/*
 Copyright (C) 2002-2014  Richard Eigenmann.
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
 * A class to test the ResizableJFrame. This can be problematic because
 * different users are likely to have different screen configurations.
 */
public class ResizableJFrameTest {

    /**
     * An entry point for standalone screen size testing.
     *
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        Settings.loadSettings();
        try {
            SwingUtilities.invokeAndWait( new Runnable() {

                @Override
                public void run() {
                    new ResizableJFrameTest();
                }
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ResizableJFrameTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    /**
     * Constructor for the test window
     */
    public ResizableJFrameTest() {

        final JPanel p = new JPanel();
        p.setLayout( new BorderLayout() );

        final ResizableJFrame resizableJFrame = new ResizableJFrame("ResizableJFrameTest", p );
        resizableJFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

        JPanel buttonPanel = new JPanel();
        JButton fullScreen = new JButton( "FullScreen" );
        fullScreen.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_FULLSCREEN );
            }
        } );
        buttonPanel.add( fullScreen );

        JButton normalScreen = new JButton( "Normal" );
        normalScreen.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_DEFAULT );

            }
        } );
        buttonPanel.add( normalScreen );

        JButton leftSize = new JButton( "Left" );
        leftSize.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_LEFT );
            }
        } );
        buttonPanel.add( leftSize );

        JButton topLeftSize = new JButton( "Top Left" );
        topLeftSize.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_TOP_LEFT );

            }
        } );
        buttonPanel.add( topLeftSize );

        JButton bottomLeftSize = new JButton( "Bottom Left" );
        bottomLeftSize.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_BOTTOM_LEFT );

            }
        } );
        buttonPanel.add( bottomLeftSize );

        JButton rightSize = new JButton( "Right" );
        rightSize.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_RIGHT );
            }
        } );
        buttonPanel.add( rightSize );

        JButton topRightSize = new JButton( "Top Right" );
        topRightSize.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_TOP_RIGHT );
            }
        } );
        buttonPanel.add( topRightSize );

        JButton bottomRightSize = new JButton( "Bottom Right" );
        bottomRightSize.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.resizeTo( WINDOW_BOTTOM_RIGHT );
            }
        } );
        buttonPanel.add( bottomRightSize );

        JButton decorateButton = new JButton( "Decorate" );
        decorateButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.showWindowDecorations( true );
            }
        } );
        buttonPanel.add( decorateButton );

        JButton undecorateButton = new JButton( "Undecorate" );
        undecorateButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                resizableJFrame.showWindowDecorations(false );
            }
        } );
        buttonPanel.add( undecorateButton );

        final JTextArea jta = new JTextArea( 20, 80 );
        jta.setCaret( new NonFocussedCaret() );

        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                jta.setText( ScreenHelper.explainGraphicsEnvironment().toString() );
            }
        } );
        buttonPanel.add( refresh );

        p.add( buttonPanel, BorderLayout.NORTH );

        final JScrollPane jsp = new JScrollPane( jta );
        p.add( jsp, BorderLayout.CENTER );

        resizableJFrame.validate();

        jta.setText( ScreenHelper.explainGraphicsEnvironment().toString() );

    }
}
