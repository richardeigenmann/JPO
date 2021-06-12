package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jpo.eventbus.CategoryAssignmentWindowRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 *
 */
public class CategroyAssignmentWindow {

    public CategroyAssignmentWindow(final CategoryAssignmentWindowRequest request) {
        EventQueue.invokeLater(() -> {
            final var outerPanel = new JPanel();
            outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
            final var titlePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            final var title = new JLabel(String.format("%d Nodes selected", request.nodes().size()));
            title.setFont(RobotoFont.getFontRobotoThin24());
            titlePanel.add(title);
            outerPanel.add(titlePanel);

            final var panel = new JPanel();
            final int COLUMNS = 8;
            panel.setLayout(new MigLayout("wrap " + COLUMNS));


            for (int i = 1; i <= 50; i++) {
                final var selectedTristateCheckBox = new TristateCheckBox("Selected TristateCheckBox", TristateCheckBox.TCheckBoxInitialState.SELECTED);
                final var unselectedTristateCheckBox = new TristateCheckBox("Unselected TristateCheckBox", TristateCheckBox.TCheckBoxInitialState.UNSELECTED);
                final var mixedTristateCheckBox = new TristateCheckBox("Mixed TristateCheckBox", TristateCheckBox.TCheckBoxInitialState.MIXED);
                panel.add(selectedTristateCheckBox);
                panel.add(unselectedTristateCheckBox);
                panel.add(mixedTristateCheckBox);
            }

            final var jscrollPanel = new JScrollPane(panel);
            outerPanel.add(jscrollPanel);

            final var frame = new JFrame(String.format("Category assignment for %d pictures", request.nodes().size()));

            final var buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            final var cancelJButton = new JButton("Cancel");
            cancelJButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            });
            final var OKJButton = new JButton("Save");
            buttonPanel.add(cancelJButton);
            buttonPanel.add(OKJButton);
            outerPanel.add(buttonPanel);

            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(outerPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


}
