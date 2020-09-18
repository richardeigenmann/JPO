package org.jpo.testground;

import org.jpo.gui.swing.FontAwesomeFont;

import javax.swing.*;
import java.awt.*;

/**
 * @see <a href="https://stackoverflow.com/questions/24177348/font-awesome-with-swing">https://stackoverflow.com/questions/24177348/font-awesome-with-swing</a>
 */
public class PlayWithFontAwesome {

    public PlayWithFontAwesome() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                JLabel label = new JLabel("\uf146\uf057\uf0fe");
                label.setFont(FontAwesomeFont.getFontAwesomeFont24());

                JFrame frame = new JFrame("Testing Font Awesome");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new GridBagLayout());
                frame.add(label);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        new PlayWithFontAwesome();
    }

}
