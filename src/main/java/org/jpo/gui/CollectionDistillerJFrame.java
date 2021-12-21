package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/*
 Copyright (C) 2002 - 2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Frame to capture the details of the collection export
 */
public class CollectionDistillerJFrame extends JFrame {

    /**
     * Size for this frame
     */
    private static final Dimension FRAME_SIZE = new Dimension(460, 300);

    /**
     * the request
     */
    private final ExportGroupToNewCollectionRequest request;

    /**
     * text field that holds the directory that the group is to be exported to
     */
    private final DirectoryChooser targetDirChooser
            = new DirectoryChooser(Settings.getJpoResources().getString("collectionExportChooserTitle"),
            DirectoryChooser.DIR_MUST_BE_WRITABLE);

    /**
     * text field that holds the filename of the target XML file
     */
    private final JTextField xmlFileNameJTextField = new JTextField();

    /**
     * JCheckBox that indicates whether the pictures are to be copied to the
     * target directory structure.
     */
    private final JCheckBox exportPicsJCheckBox = new JCheckBox(Settings.getJpoResources().getString("collectionExportPicturesText"));

    /**
     * Constructor for the Export Dialog window.
     *
     * @param request The request with the details
     *                on.
     */
    CollectionDistillerJFrame(final ExportGroupToNewCollectionRequest request) {
        super(Settings.getJpoResources().getString("CollectionDistillerJFrameFrameHeading"));
        this.request = request;
        initComponents();
    }

    private void initComponents() {
        setSize(FRAME_SIZE);
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

        contentJPanel.add(new JLabel(Settings.getJpoResources().getString("genericTargetDirText")));
        contentJPanel.add(targetDirChooser, "wrap");

        contentJPanel.add(new JLabel(Settings.getJpoResources().getString("xmlFileNameLabel")));

        xmlFileNameJTextField.setPreferredSize(new Dimension(240, 20));
        xmlFileNameJTextField.setMinimumSize(new Dimension(240, 20));
        xmlFileNameJTextField.setMaximumSize(new Dimension(400, 20));
        xmlFileNameJTextField.setText("PictureList.xml");
        xmlFileNameJTextField.setInputVerifier(new InputVerifier() {

            @Override
            public boolean shouldYieldFocus(final JComponent source, final JComponent target) {
                final String validationFile = ((JTextComponent) source).getText();
                if (!validationFile.toUpperCase().endsWith(".XML")) {
                    ((JTextComponent) source).setText(validationFile + ".xml");
                }
                return true;
            }

            @Override
            public boolean verify(final JComponent input) {
                return true;
            }
        });
        contentJPanel.add(xmlFileNameJTextField, "wrap");

        exportPicsJCheckBox.setSelected(true);
        contentJPanel.add(exportPicsJCheckBox, "spanx 2, wrap");

        final JPanel buttonJPanel = new JPanel();

        final JButton exportJButton = new JButton(Settings.getJpoResources().getString("genericExportButtonText"));
        exportJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        exportJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        exportJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        exportJButton.setDefaultCapable(true);
        this.getRootPane().setDefaultButton(exportJButton);
        exportJButton.addActionListener((ActionEvent e) -> {
            exportToDirectory();
            getRid();
        });
        buttonJPanel.add(exportJButton);

        final JButton cancelJButton = new JButton(Settings.getJpoResources().getString("genericCancelText"));
        cancelJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        cancelJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        cancelJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        cancelJButton.addActionListener((ActionEvent e) -> getRid());
        buttonJPanel.add(cancelJButton);

        contentJPanel.add(buttonJPanel, "spanx 2, wrap");

        setContentPane(contentJPanel);

        pack();
        setVisible(true);
    }

    /**
     * method that gets rid of this JFrame
     */
    private void getRid() {
        setVisible(false);
        dispose();
    }

    /**
     * method that outputs the selected group to a directory
     */
    private void exportToDirectory() {
        final File exportDirectory = targetDirChooser.getDirectory();

        if (!exportDirectory.exists()) {
            try {
                boolean ok = exportDirectory.mkdirs();
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Could not create directory " + exportDirectory,
                            "SecurityException",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(this, "Could not create directory " + exportDirectory,
                        "SecurityException",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        final File targetFile = new File(exportDirectory, xmlFileNameJTextField.getText());

        if (targetFile.exists()) {
            int answer = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("confirmSaveAs"),
                    Settings.getJpoResources().getString("genericWarning"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        JpoEventBus.getInstance().post(new ExportGroupToCollectionRequest(request.node(), targetFile, exportPicsJCheckBox.isSelected()));

        Settings.memorizeCopyLocation(targetFile.getParent());
        JpoEventBus.getInstance().post(new CopyLocationsChangedEvent());
        Settings.pushRecentCollection(targetFile.toString());
        JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                Settings.getJpoResources().getString("collectionSaveBody") + targetFile.toString(),
                Settings.getJpoResources().getString("collectionSaveTitle"),
                JOptionPane.INFORMATION_MESSAGE);

    }

}
