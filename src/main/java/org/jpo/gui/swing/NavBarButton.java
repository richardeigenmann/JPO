package org.jpo.gui.swing;

import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Extends the default JButton with no border, standard background color,
 * standard dimensions of 24 pixels and tooltip at 0, -20 Uses the
 * Settings.PICTUREVIEWER_BACKGROUND_COLOR for the background.
 */
public class NavBarButton extends JButton {

    /**
     * Constructs the NavBarButton
     * @param icon The icon to show
     */
    public NavBarButton( final Icon icon ) {
        super( icon );
        setBorderPainted( false );
        setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        Dimension navButtonSize = new Dimension(24, 24);
        setMinimumSize(navButtonSize);
        setPreferredSize(navButtonSize);
        setMaximumSize(navButtonSize);
    }

    /**
     * Overriding the position of the tooltip so that it comes 20 pixels above
     * the mouse pointer
     *
     * @return the point
     */
    @Override
    public Point getToolTipLocation( MouseEvent event ) {
        return new Point( 0, -20 );
    }
}
