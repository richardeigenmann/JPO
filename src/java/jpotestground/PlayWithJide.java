/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpotestground;

import com.jidesoft.swing.JideBoxLayout;
import com.jidesoft.swing.JideSplitPane;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

/**
 *
 * @author richi
 */
public class PlayWithJide extends JFrame {

    /**
     * @param args the command line arguments
     */
    // <editor-fold defaultstate="collapsed" desc="main">
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new PlayWithJide().setVisible(true);
            }
        });
    }// </editor-fold>

    /** Creates new form LeftPanel */
    public PlayWithJide() {
        JideSplitPane sp = new JideSplitPane(JideSplitPane.VERTICAL_SPLIT);
        sp.setContinuousLayout(true);
        sp.setOneTouchExpandable(true);
        sp.setDividerStepSize(8);
        sp.setShowGripper(true);

        getContentPane().add(sp);

        JPanel camerasPanel = new JPanel();
        camerasPanel.setPreferredSize(new Dimension(150, 200));
        camerasPanel.setMinimumSize(new Dimension(100, 25));
        camerasPanel.setLayout(new BoxLayout(camerasPanel, BoxLayout.Y_AXIS));

        JButton cameras = new JButton("Cameras");
        camerasPanel.add(cameras);

        JTree camerasTree = new JTree();
        camerasTree.setPreferredSize(new Dimension(150, 200));
        camerasTree.setMinimumSize(new Dimension(150, 200));
        camerasPanel.add(camerasTree);
        sp.add(camerasPanel, JideBoxLayout.FLEXIBLE);

        JButton Collection = new JButton("Collection");
        sp.add(Collection, JideBoxLayout.FIX);

        JButton Views = new JButton("Views");
        sp.addPane(Views);

        JButton Searches = new JButton("Searches");
        sp.addPane(Searches);

        JButton History = new JButton("History");
        sp.addPane(History);

        JPanel infoBox = new JPanel();
        infoBox.setPreferredSize(new Dimension(150, 200));
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.add(new JLabel("Stats"));
        infoBox.add(new JLabel("Stats"));
        infoBox.add(new JLabel("Stats"));
        infoBox.add(new JLabel("Stats"));
        infoBox.add(new JLabel("Stats"));
        sp.add(infoBox, JideBoxLayout.FIX);

     

        pack();
    }
}
