package org.jpo.testground;

import org.jpo.gui.swing.FontAwesomeFont;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://stackoverflow.com/questions/24177348/font-awesome-with-swing">https://stackoverflow.com/questions/24177348/font-awesome-with-swing</a>
 */
public class PlayWithFontAwesome {

    /**
     * Constructs a little GUI showing some "characters" from FontAwesome
     */
    public PlayWithFontAwesome() {
        EventQueue.invokeLater(() -> {

            JLabel labelRegular = new JLabel("\uf146\uf057\uf0fe\uf1c9\uf073");
            labelRegular.setFont(FontAwesomeFont.getFontAwesomeRegular24());

            JLabel labelSolid = new JLabel("\uf146\uf057\uf0fe\uf002\uf1c9\uf133");
            labelSolid.setFont(FontAwesomeFont.getFontAwesomeSolid24());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(labelRegular);
            panel.add(labelSolid);

            JFrame frame = new JFrame("Testing Font Awesome");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Makes this class directly runnable
     *
     * @param args Standard Java
     */
    public static void main(String[] args) {
        new PlayWithFontAwesome();
    }

}
