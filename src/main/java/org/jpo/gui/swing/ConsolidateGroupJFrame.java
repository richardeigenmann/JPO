package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.gui.ConsolidateGroupActionCallback;
import org.jpo.gui.DirectoryChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/*
 Copyright (C) 2015-2020  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * GUI to ask for the target directory and whether to recurse the sub-groups in
 * a consolidation.
 *
 * @author Richard Eigenmann
 */
public class ConsolidateGroupJFrame extends JFrame {

    /**
     * Handler for the success action
     */
    private final ConsolidateGroupActionCallback consolidateGroupAction;
    /**
     * Chooser to pick the highres directory
     *
     */
    private final DirectoryChooser highresDirectoryChooser = new DirectoryChooser(Settings.jpoResources.getString("highresTargetDirJTextField"), DirectoryChooser.DIR_MUST_BE_WRITABLE);
    /**
     * Tickbox that indicates whether pictures or the current group only should
     * be consolidated or whether the subgroups (if any) should be included.
     *
     */
    private final JCheckBox recurseSubgroupsJCheckBox = new JCheckBox(Settings.jpoResources.getString("RecurseSubgroupsLabel"));

    public ConsolidateGroupJFrame(ConsolidateGroupActionCallback consolidateGroupAction) {
        super(Settings.jpoResources.getString("ConsolidateGroupJFrameHeading"));
        this.consolidateGroupAction = consolidateGroupAction;
        initComponents();
    }

    public void setTargetDir(File targetDir) {
        highresDirectoryChooser.setFile(targetDir);
    }

    private void initComponents() {
        setSize(460, 500);
        setLocationRelativeTo(Settings.getAnchorFrame());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                getRid();
            }
        });
        final JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout(new MigLayout());
        final JLabel consolidateGroupBlaBlaJLabel = new JLabel(Settings.jpoResources.getString("ConsolidateGroupBlaBlaLabel"));
        contentJPanel.add(consolidateGroupBlaBlaJLabel, "span 2, wrap");
        final JLabel targetDirJLabel = new JLabel(Settings.jpoResources.getString("genericTargetDirText"));
        contentJPanel.add(targetDirJLabel);
        contentJPanel.add(highresDirectoryChooser, "span 2, wrap");
        recurseSubgroupsJCheckBox.setSelected(true);
        contentJPanel.add(recurseSubgroupsJCheckBox, "span 2, wrap");

        final JPanel buttonJPanel = new JPanel();
        // add the consolidate button
        final JButton consolidateJButton = new JButton(Settings.jpoResources.getString("ConsolidateButton"));
        consolidateJButton.setPreferredSize(new Dimension(120, 25));
        consolidateJButton.setMinimumSize(Settings.defaultButtonDimension);
        consolidateJButton.setMaximumSize(new Dimension(120, 25));
        consolidateJButton.setDefaultCapable(true);
        this.getRootPane().setDefaultButton(consolidateJButton);
        buttonJPanel.add(consolidateJButton);

        // add the cancel button
        final JButton cancelJButton = new JButton(Settings.jpoResources.getString("genericCancelText"));
        cancelJButton.setMinimumSize(Settings.defaultButtonDimension);
        cancelJButton.setMaximumSize(new Dimension(120, 25));
        buttonJPanel.add(cancelJButton);
        contentJPanel.add(buttonJPanel, "span 2, wrap");
        setContentPane(contentJPanel);
        pack();
        setVisible(true);
        consolidateJButton.addActionListener(( ActionEvent e ) -> {
            consolidateGroupAction.consolidateGroupCallback(highresDirectoryChooser.getDirectory(), recurseSubgroupsJCheckBox.isSelected());
            getRid();
        });
        cancelJButton.addActionListener(( ActionEvent e ) -> getRid());
    }

    /**
     * method that gets rid of this JFrame
     */
    private void getRid() {
        setVisible(false);
        dispose();
    }

}
