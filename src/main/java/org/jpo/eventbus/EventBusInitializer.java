package org.jpo.eventbus;

import io.github.classgraph.ClassGraph;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2023 Richard Eigenmann.
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

public class EventBusInitializer {

    private EventBusInitializer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(EventBusInitializer.class.getName());

    public static void registerEventHandlers() {
        findAndLoadEventhandlers("org.jpo.gui");
        findAndLoadEventhandlers("org.jpo.eventbus");
    }

    /**
     * Searches the indicated packages for classes that are tagged with the EVentHandler annotation. When such a
     * class is found it is instantiated and attached to the EventBus of the JPO application. Most if not all user
     * driven actions and many system driven actions in JPO use the EventBus to decouple the source of the event from
     * the part of the program that fulfills the request.
     * @param packageName The package name that should be searched for EventHandler tagges classes
     */
    public static void findAndLoadEventhandlers(final String packageName) {
        try (final var scanResult = new ClassGraph().enableAllInfo().acceptPackages(packageName)
                .scan()) {
            final var routeClassInfoList = scanResult.getClassesWithAnnotation("org.jpo.eventbus.EventHandler");
            for (final var routeClassInfo : routeClassInfoList) {
                LOGGER.log(Level.INFO, "Loading EventHandler class: {0}", routeClassInfo);
                final Class<?> type = routeClassInfo.loadClass();
                try {
                    JpoEventBus.getInstance().register(type.getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    LOGGER.log(Level.SEVERE, "Exception occurred while instantiating a JPO component class: {0}", e.getMessage());
                }
            }
        }
    }
}
