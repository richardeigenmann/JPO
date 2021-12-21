package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import org.jpo.eventbus.FileLoadRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShowGroupRequest;
import org.jpo.eventbus.UpdateApplicationTitleRequest;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/*
Copyright (C) 2021 Richard Eigenmann, Zürich, Switzerland
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
 * Listens to the EventBus events and prints them in the Panel
 */
public class EventBusViewer extends JPanel {

    private final JTextArea textArea = new JTextArea();

    /**
     * Constructs the EventViewer panel and connects to the EventBus
     */
    public EventBusViewer() {
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        JpoEventBus.getInstance().register(this);
        final DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    private void logTimestamp() {
        textArea.append(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        textArea.append(" ");
    }

    private void logTimeAndClass(final Object o) {
        logTimestamp();
        var s = o.getClass().toString().split(Pattern.quote("."));
        textArea.append(s[s.length - 1]);
    }

    /**
     * Handles the FileLoadRequest
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final FileLoadRequest request) {
        logTimeAndClass(request);
        textArea.append(String.format(" %s", request.fileToLoad()));
        textArea.append(System.lineSeparator());
    }

    /**
     * Handles the UpdateApplicationTitleRequest by loggin the title
     *
     * @param request the request to log
     */
    @Subscribe
    public void handleEvent(final UpdateApplicationTitleRequest request) {
        logTimeAndClass(request);
        textArea.append(String.format(" %s", request.newTitle()));
        textArea.append(System.lineSeparator());
    }

    /**
     * Handles the ShowGroupRequest by logging the node in the request
     *
     * @param request the Request
     */
    @Subscribe
    public void handleEvent(final ShowGroupRequest request) {
        logTimeAndClass(request);
        textArea.append(String.format(" %s", request.node()));
        textArea.append(System.lineSeparator());
    }

    /**
     * Handles all the other events by logging them.
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final Object request) {
        if (request instanceof FileLoadRequest
                || request instanceof UpdateApplicationTitleRequest
                || request instanceof ShowGroupRequest) {
            // do nothing
        } else {
            logTimeAndClass(request);
            textArea.append(System.lineSeparator());
        }
    }

}
