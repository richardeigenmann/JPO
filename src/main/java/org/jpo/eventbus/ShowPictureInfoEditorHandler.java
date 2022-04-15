package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.gui.swing.PictureInfoEditor;


public class ShowPictureInfoEditorHandler {
    /**
     * When the app sees a ShowPictureInfoEditorRequest it will open the
     * PictureInfoEditor for the supplied node
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final ShowPictureInfoEditorRequest request) {
        new PictureInfoEditor(request.node());
    }
}
