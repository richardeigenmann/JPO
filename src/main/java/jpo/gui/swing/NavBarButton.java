package jpo.gui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JButton;
import jpo.dataModel.Settings;

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
