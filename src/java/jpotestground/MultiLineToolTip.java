/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jpotestground;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 * $Header$
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * Found on http://www.koders.com/java/fidF47D0FF86FA20992E7C6C1F02E8DBB745B11AA52.aspx
 * 
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class MultiLineToolTip
    extends JToolTip {

	public MultiLineToolTip() {
        setUI(new MultiLineToolTipUI());
    }

    public MultiLineToolTip(MetalToolTipUI toolTipUI) {
        setUI(toolTipUI);
    }

    private class MultiLineToolTipUI
        extends MetalToolTipUI {
        private String[] strs;

        @Override
        public void paint(Graphics g, JComponent c) {
            FontMetrics metrics = c.getFontMetrics(c.getFont());
            Dimension size = c.getSize();
            g.setColor(c.getBackground());
            g.fillRect(0, 0, size.width, size.height);
            g.setColor(c.getForeground());
            if (strs != null) {
                int length = strs.length;
                for (int i = 0; i < length; i++) {
                    g.drawString(strs[i], 3, (metrics.getHeight()) * (i + 1));
                }
            }
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            FontMetrics metrics = c.getFontMetrics(c.getFont());
            String tipText = ( (JToolTip) c).getTipText();
            if (tipText == null) {
                tipText = "";
            }
            StringTokenizer st = new StringTokenizer(tipText, "|");
            int maxWidth = 0;
            Vector<String> v = new Vector<String>();
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int width = SwingUtilities.computeStringWidth(metrics, token);
                maxWidth = (maxWidth < width) ? width : maxWidth;
                v.addElement(token);
            }
            int lines = v.size();
            if (lines < 1) {
                strs = null;
                lines = 1;
            }
            else {
                strs = new String[lines];
                int i = 0;
                for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
                    strs[i] = (String) e.nextElement();
                }
            }
            int height = metrics.getHeight() * lines;
            return new Dimension(maxWidth + 6, height + 4);
        }
    }
}
