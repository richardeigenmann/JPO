package org.jpo.gui;

/*
Copyright (C) 2025 Richard Eigenmann, Zürich, Switzerland
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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Facilitates access to the internationalised labels and texts for the JPO application
 */
public class JpoResources {

    private JpoResources() {
        // prevent instantiation
    }

    private static ResourceBundle jpoResources = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.ENGLISH);

    public static ResourceBundle getJpoResources() {
        return jpoResources;
    }

    public static void setJpoResources(final ResourceBundle jpoResources) {
        JpoResources.jpoResources = jpoResources;
    }

    public static String getResource(final String key) {
        return jpoResources.getString(key);
    }

}
