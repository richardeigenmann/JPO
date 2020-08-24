package org.jpo.testground;

import org.jpo.datamodel.Settings;
import org.jpo.gui.swing.NonFocussedCaret;
import org.jpo.gui.swing.ResizableJFrame;
import org.jpo.gui.swing.ScreenHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.swing.ResizableJFrame.WindowSize.*;


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
            SwingUtilities.invokeAndWait(ResizableJFrameTest::new
            );
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

        final ResizableJFrame resizableJFrame = new ResizableJFrame( "ResizableJFrameTest", p );
        resizableJFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

        JPanel buttonPanel = new JPanel();
        JButton fullScreen = new JButton( "FullScreen" );
        fullScreen.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_FULLSCREEN ));
        buttonPanel.add( fullScreen );

        JButton normalScreen = new JButton( "Normal" );
        normalScreen.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_DEFAULT ));
        buttonPanel.add( normalScreen );

        JButton leftSize = new JButton( "Left" );
        leftSize.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_LEFT ));
        buttonPanel.add( leftSize );

        JButton topLeftSize = new JButton( "Top Left" );
        topLeftSize.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_TOP_LEFT ));
        buttonPanel.add( topLeftSize );

        JButton bottomLeftSize = new JButton( "Bottom Left" );
        bottomLeftSize.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_BOTTOM_LEFT ));
        buttonPanel.add( bottomLeftSize );

        JButton rightSize = new JButton( "Right" );
        rightSize.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_RIGHT ));
        buttonPanel.add( rightSize );

        JButton topRightSize = new JButton( "Top Right" );
        topRightSize.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_TOP_RIGHT ));
        buttonPanel.add( topRightSize );

        JButton bottomRightSize = new JButton( "Bottom Right" );
        bottomRightSize.addActionListener( ( ActionEvent e ) -> resizableJFrame.resizeTo( WINDOW_BOTTOM_RIGHT ));
        buttonPanel.add( bottomRightSize );

        JButton decorateButton = new JButton( "Decorate" );
        decorateButton.addActionListener( ( ActionEvent e ) -> resizableJFrame.showWindowDecorations( true ));
        buttonPanel.add( decorateButton );

        JButton undecorateButton = new JButton( "Undecorate" );
        undecorateButton.addActionListener( ( ActionEvent e ) -> resizableJFrame.showWindowDecorations( false ));
        buttonPanel.add( undecorateButton );

        final JTextArea jta = new JTextArea( 20, 80 );
        jta.setCaret( new NonFocussedCaret() );

        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( ( ActionEvent e ) -> jta.setText( ScreenHelper.explainGraphicsEnvironment().toString() ));
        buttonPanel.add( refresh );

        p.add( buttonPanel, BorderLayout.NORTH );

        final JScrollPane jsp = new JScrollPane( jta );
        p.add( jsp, BorderLayout.CENTER );

        resizableJFrame.validate();

        jta.setText( ScreenHelper.explainGraphicsEnvironment().toString() );

    }
}
