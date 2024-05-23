import org.jpo.Main;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShutdownApplicationRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.fail;

/*
 Copyright (C) 2002-2024 Richard Eigenmann.
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
 * Tests for the Main Class
 * @author Richard Eigenmann
 */
class MainTest {

    @Test
    @Disabled("Crashes but I don't know where")
    void constructorTest() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                final String[] args = {""};
                Main.main(args);
                JpoEventBus.getInstance().post(new ShutdownApplicationRequest());
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}
