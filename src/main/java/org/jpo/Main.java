package org.jpo;

import org.jpo.eventbus.ApplicationStartupRequest;
import org.jpo.eventbus.EventBusInitializer;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.generated.GeneratedVersion;

/*
 Copyright (C) 2002-2025 Richard Eigenmann.
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
 * @version 0.25
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
        System.out.println(String.format("""
                JPO Version %s
                Copyright (C) 2000-2025 Richard Eigenmann,
                Zurich, Switzerland.
                JPO comes with ABSOLUTELY NO WARRANTY.
                For details look at the Help > License menu.
                This is free software, and you are welcome
                to redistribute it under certain conditions.
                See Help > License for details.
                """, GeneratedVersion.JPO_VERSION ));

        EventBusInitializer.registerEventHandlers();
        JpoEventBus.getInstance().post(new ApplicationStartupRequest() );
    }

}
