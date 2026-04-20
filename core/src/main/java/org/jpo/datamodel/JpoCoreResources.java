package org.jpo.datamodel;

/*
Copyright (C) 2025-2026 Richard Eigenmann, Zürich, Switzerland
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
 * Facilitates access to the internationalised labels and texts for the core JPO application
 */
public class JpoCoreResources {

    private JpoCoreResources() {
        // prevent instantiation
    }

    private static ResourceBundle jpoCoreResources = ResourceBundle.getBundle("org.jpo.datamodel.JpoCoreResources", Locale.ENGLISH);

    public static ResourceBundle getJpoCoreResources() {
        return jpoCoreResources;
    }

    public static void setJpoCoreResources(final ResourceBundle jpoCoreResources) {
        JpoCoreResources.jpoCoreResources = jpoCoreResources;
    }

    public static String getResource(final String key) {
        return jpoCoreResources.getString(key);
    }

}
