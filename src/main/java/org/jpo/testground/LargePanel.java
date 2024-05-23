package org.jpo.testground;

import org.jpo.datamodel.Settings;
import org.jpo.gui.ThumbnailLayoutManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import static java.awt.Component.TOP_ALIGNMENT;

/**
 * @see <a href="https://stackoverflow.com/questions/24177348/font-awesome-with-swing">https://stackoverflow.com/questions/24177348/font-awesome-with-swing</a>
 */
public class LargePanel {

    private final JPanel thumbnailsPane = new JPanel();
    private final JScrollPane thumbnailJScrollPane = new JScrollPane();

    private final ThumbnailLayoutManager thumbnailLayoutManager = new ThumbnailLayoutManager(thumbnailJScrollPane.getViewport());

    /**
     * The logger for the class
     */
    private static final Logger LOGGER = Logger.getLogger(LargePanel.class.getName());

    /**
     * Constructs a little GUI showing some "characters" from FontAwesome
     */
    public LargePanel() {
        EventQueue.invokeLater(() -> {
            thumbnailsPane.setLayout(thumbnailLayoutManager);
            thumbnailsPane.setAlignmentY(TOP_ALIGNMENT);

            //final var max = 100000;
            //final var max = 1000;
            final var max = 30;
            for (int i=0; i< max; i++) {
                thumbnailsPane.add(createLabel(i));
            }

            final var layer = new JLayer<JComponent>(thumbnailsPane, layerUI);
            thumbnailJScrollPane.setViewportView(layer);
            //thumbnailJScrollPane.setViewportView(thumbnailsPane);

            JFrame frame = new JFrame("Testing Large Panel");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(thumbnailJScrollPane);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    //final var dim = thumbnailsPane.getPreferredSize();
                    //LOGGER.log(Level.INFO, "thumbnailsPane getPreferredSize is width: {0} height: {1}", new Object[]{dim.width, dim.height});
                    //final var lpdim = layeredPane.getPreferredSize();
                    //LOGGER.log(Level.INFO, "layeredPane getPreferredSize is width: {0} height: {1}", new Object[]{lpdim.width, dim.height});
                    //final var vvdim = thumbnailJScrollPane.getViewport().getView().getPreferredSize();
                    //LOGGER.log(Level.INFO, "Viewport.view getPreferredSize is width: {0} height: {1}", new Object[]{vvdim.width, dim.height});
                    thumbnailJScrollPane.getViewport().getView().validate();
                    System.out.println(thumbnailJScrollPane.getViewport().getView().getWidth());
                }
            });
        });
    }

    // This custom layerUI will fill the layer with translucent green
    // and print out all mouseMotion events generated within its borders
    LayerUI<JComponent> layerUI = new MyLayerUI();

    private class MyLayerUI extends LayerUI<JComponent> {

        boolean paintOverlay = false;

        private static final Color DIMMED_COLOR = new Color(45, 45, 45, 180);

        private Rectangle overlayRectangle = new Rectangle(130,100,200,350);

        @Override
        public void paint(Graphics g, JComponent c) {
            // paint the layer as is
            super.paint(g, c);

            if (paintOverlay) {
                final var outerRect = new Rectangle(0, 0, c.getWidth(), c.getHeight());
                g.setColor(DIMMED_COLOR);
                Graphics2D g2d = (Graphics2D) g;
                Area a = new Area(outerRect);
                a.subtract(new Area(overlayRectangle));
                g2d.fill(a);
                g2d.dispose();
                g.dispose();
             }
        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            // enable mouse motion events for the layer's subcomponents
            ((JLayer) c).setLayerEventMask(
                    AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK
                    | AWTEvent.MOUSE_EVENT_MASK
                    | AWTEvent.MOUSE_MOTION_EVENT_MASK
            );
        }

        @Override
        public void uninstallUI(JComponent c) {
            super.uninstallUI(c);
            // reset the layer event mask
            ((JLayer) c).setLayerEventMask(0);
        }

        // overridden method which catches MouseMotion events
        public void eventDispatched(AWTEvent e, JLayer<? extends JComponent> l) {
            if ( e instanceof HierarchyEvent he &&  (he.getID() & HierarchyEvent.ANCESTOR_RESIZED) != 0) {
                thumbnailsPane.doLayout();
            } else if ( e instanceof MouseEvent me) {
                if ( me.getID() == MouseEvent.MOUSE_PRESSED)  {
                    mousePressed( me );
                } else if ( me.getID() == MouseEvent.MOUSE_DRAGGED ) {
                    handleMouseDragged(me);
                }
            }
        }

        public void mousePressed(final MouseEvent e) {
            mousePressedPoint = e.getPoint();
        }

        private void handleMouseDragged(final MouseEvent e) {
            // do the overlay painting
            paintOverlay = true;
            final var mouseMovedToPoint = e.getPoint();
            overlayRectangle = getMouseRectangle(mouseMovedToPoint);
            thumbnailsPane.repaint();

            final var viewRect = thumbnailJScrollPane.getViewport().getViewRect();
            final var verticalScrollBar = thumbnailJScrollPane.getVerticalScrollBar();
            final var scrolltrigger = 40;
            if (mouseMovedToPoint.y - viewRect.y - viewRect.height > -scrolltrigger) {
                final var increment = verticalScrollBar.getUnitIncrement(1);
                final var position = verticalScrollBar.getValue();
                if (position < verticalScrollBar.getMaximum()) {
                    verticalScrollBar.setValue(position + increment);
                }
            } else if (mouseMovedToPoint.y - viewRect.y < scrolltrigger) {
                final var increment = verticalScrollBar.getUnitIncrement(1);
                final var position = verticalScrollBar.getValue();
                if (position > verticalScrollBar.getMinimum()) {
                    verticalScrollBar.setValue(position - increment);
                }
            }
        }

        private void handleMouseReleased(final MouseEvent e) {
            /*if (e.isPopupTrigger() && mySetOfNodes instanceof GroupNavigator gn) {
                JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest(gn.getGroupNode(), e.getComponent(), e.getX(), e.getY()));
                return;
            }*/

            thumbnailJScrollPane.requestFocusInWindow();

            // undo the overlay painting
            paintOverlay = false;
            thumbnailsPane.repaint();

            final var mouseRectangle = getMouseRectangle(e.getPoint());

            // I wonder why they don't put the following two lines into the SWING library but
            // let you work out this binary math on your own from the unhelpful description?
            final var ctrlpressed = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK;
            final var shiftpressed = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK;

            if (!(ctrlpressed || shiftpressed)) {
                Settings.getPictureCollection().clearSelection();
            }

            final var thumbnailRectangle = new Rectangle();
            /*for (final var thumbnailController : thumbnailControllers) {
                final var node = thumbnailController.getNode();
                if (node == null) {
                    continue;
                }
                thumbnailController.getThumbnail().getBounds(thumbnailRectangle);
                if (mouseRectangle.intersects(thumbnailRectangle)) {
                    Settings.getPictureCollection().addToSelectedNodes(node);
                }
            }*/
        }

        /**
         * Point where the mouse was pressed so that we can figure out the rectangle
         * that is being selected.
         */
        private Point mousePressedPoint = new Point(0,0);

        /**
         * Returns the rectangle marked by the area which the mouse marked by
         * dragging. If the destination is to the left or higher than the
         * mousePressedPoint the rectangle corrects this.
         *
         * @param mousePoint mouse point
         * @return The rectangle in the coordinate space of the parent component
         */
        private Rectangle getMouseRectangle(final Point mousePoint) {
            final var rectangle = new Rectangle(mousePressedPoint,
                    new Dimension(mousePoint.x - mousePressedPoint.x,
                            mousePoint.y - mousePressedPoint.y));
            if (mousePoint.x < mousePressedPoint.x) {
                rectangle.x = mousePoint.x;
                rectangle.width = mousePressedPoint.x - mousePoint.x;
            }
            if (mousePoint.y < mousePressedPoint.y) {
                rectangle.y = mousePoint.y;
                rectangle.height = mousePressedPoint.y - mousePoint.y;
            }

            return rectangle;
        }
    }

    private final Dimension dim = new Dimension (150,100);
    private final EmptyBorder eBorder = new EmptyBorder(10, 10, 10, 10);
    private final LineBorder lBorder = new LineBorder(new Color(100, 100, 100));
    private final Border border = BorderFactory.createCompoundBorder(lBorder, eBorder);


    private JLabel createLabel(int i) {
        JLabel label = new JLabel(Integer.toString(i) + (i%2 == 0 ? ": Thumbnail" : ": Description"));
        label.setBorder(border);
        label.setPreferredSize(dim);
        label.setMinimumSize(dim);
        label.setMaximumSize(dim);
        return label;
    }

    /**
     * Makes this class directly runnable
     *
     * @param args Standard Java
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> new LargePanel());
    }

}
