package jpotestground;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class AlignX {

  private static Container makeIt(String labelChar, float alignment) {
    javax.swing.Box box = javax.swing.Box.createVerticalBox();

    for (int i=1; i<6; i++) {
      String label = makeLabel(labelChar, i*2);
      JButton button = new JButton(label);
      button.setAlignmentX(alignment);
      box.add(button);
    }
    JLabel extraLabel = new JLabel ("jdf ");
    extraLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
    box.add(extraLabel);
    return box;
  }

  private static String makeLabel(String s, int length) {
    StringBuffer buff = new StringBuffer(length);
    for (int i=0; i<length; i++) {
      buff.append(s);
    }
    return buff.toString();
  }

  public static void main(String args[]) {
    JFrame frame = new JFrame("X Alignment");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container panel1 = makeIt("L", Component.LEFT_ALIGNMENT);
    //Container panel2 = makeIt("C", Component.CENTER_ALIGNMENT);
    //Container panel3 = makeIt("R", Component.RIGHT_ALIGNMENT);

    frame.setLayout(new FlowLayout());
    frame.add(panel1);
    //frame.add(panel2);
    //frame.add(panel3);

    frame.pack();
    frame.setVisible(true);
  }
}