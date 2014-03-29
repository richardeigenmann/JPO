package jpo.gui.swing;

import java.util.logging.Logger;
import jpo.dataModel.Settings;
import javax.swing.JOptionPane;
import jpo.dataModel.Tools;

/*
HelpAboutWindow.java:  Creates the Help about window

Copyright (C) 2007 - 2010 Richard Eigenmann, Zürich, Switzerland
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
 * This class creates the Help About window
 */
public class HelpAboutWindow {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( HelpAboutWindow.class.getName() );


    /**
     *  Constructs a Help About Window
     */
    public HelpAboutWindow() {
        Tools.checkEDT();
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                Settings.jpoResources.getString( "HelpAboutText" ) + Settings.jpoResources.getString( "HelpAboutUser" ) + System.getProperty( "user.name" ) + "\n" + Settings.jpoResources.getString( "HelpAboutOs" ) + System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" ) + " " + System.getProperty( "os.arch" ) + "\n" + Settings.jpoResources.getString( "HelpAboutJvm" ) + System.getProperty( "java.vendor" ) + " " + System.getProperty( "java.version" ) + "\n" + Settings.jpoResources.getString( "HelpAboutJvmMemory" ) + Long.toString( Runtime.getRuntime().maxMemory() / 1024 / 1024, 0 ) + " MB\n" + Settings.jpoResources.getString( "HelpAboutJvmFreeMemory" ) + Long.toString( Runtime.getRuntime().freeMemory() / 1024 / 1024, 0 ) + " MB\n" );

        // while we're at it dump the stuff to the log
        LOGGER.info( "HelpAboutWindow: Help About showed the following information" );
        LOGGER.info( "User: " + System.getProperty( "user.name" ) );
        LOGGER.info( "Operating System: " + System.getProperty( "os.name" ) + "  " + System.getProperty( "os.version" ) );
        LOGGER.info( "Java: " + System.getProperty( "java.version" ) );
        LOGGER.info( "Max Memory: " + Long.toString( Runtime.getRuntime().maxMemory() / 1024 / 1024, 0 ) + " MB" );
        LOGGER.info( "Free Memory: " + Long.toString( Runtime.getRuntime().freeMemory() / 1024 / 1024, 0 ) + " MB" );
    }
}

