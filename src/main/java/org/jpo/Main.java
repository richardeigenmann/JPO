package org.jpo;

import org.jpo.datamodel.Settings;
import org.jpo.eventbus.ApplicationStartupRequest;
import org.jpo.eventbus.EventBusInitializer;
import org.jpo.eventbus.JpoEventBus;


/*
 Copyright (C) 2002-2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * The first class to be started to get the JPO application going.
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 * @version 0.18
 */
public class Main {

    /**
     * The main method is the entry point for this application (or any) Java
     * application. No parameter passing is used in the Jpo application. It checks if
     * the most important classes can be loaded and then posts the
     * ApplicationStartupRequest.
     *
     * @see ApplicationStartupRequest
     * @param args The command line arguments
     */
    public static void main( String[] args ) {
        System.out.println("\nJPO version " + Settings.JPO_VERSION + "\n"
                + "Copyright (C) 2000-2023 Richard Eigenmann,\nZurich, Switzerland\n"
                + "JPO comes with ABSOLUTELY NO WARRANTY;\n"
                + "for details Look at the Help | License menu item.\n"
                + "This is free software, and you are welcome\n"
                + "to redistribute it under certain conditions;\n"
                + "see Help | License for details.\n\n");

        EventBusInitializer.registerEventHandlers();
        JpoEventBus.getInstance().post(new ApplicationStartupRequest() );
    }

}
