package jpotestground;

import jpo.gui.*;
import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.*;
import jpo.dataModel.JarDistiller;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.logging.Logger;


/*
JarDistillerJFrame.java:  GUI for the writing to a Jar file

Copyright (C) 2002-2009  Richard Eigenmann.
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
 *  Creates a GUI that allows the export of a subtree to a 
 *  Jar file. This is not properly thought through and is currently 
 *  diabled.
 *  @deprecated	This is not peoperly thought through and should not be used.
 */
 class JarDistillerJFrame extends JFrame implements ActionListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger(JarDistillerJFrame.class.getName());
    /**
     *  the node from which to start the export
     */
    private SortableDefaultMutableTreeNode startNode;
    /**
     *  text field that holds the filename and path of the jar file
     **/
    private JTextField targetJarFilenameJTextField = new JTextField();
    /**
     *  button that brings up a directory jFileChooser and puts the value back into the targetJarFilenameJTextField field
     **/
    private JButton chooseDirJButton = new JButton();
    /**
     *  button to start the export
     **/
    private JButton exportJButton = new JButton(Settings.jpoResources.getString("genericExportButtonText"));
    /**
     *  button to cancel the dialog
     **/
    private JButton cancelJButton = new JButton(Settings.jpoResources.getString("genericCancelText"));

    /**
     *   Creates a Window and asks for the name of the jar file to generate.
     *
     *   @param startNode  The group node that the user wants the export to be done on.
     */
    public JarDistillerJFrame(SortableDefaultMutableTreeNode startNode) {
        super(Settings.jpoResources.getString("groupExportJarTitleText"));
        this.startNode = startNode;

        setSize(460, 300);
        setLocationRelativeTo(Settings.anchorFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                getRid();
            }
        });

        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        JLabel targetDirJLabel = new JLabel(Settings.jpoResources.getString("JarDistillerLabel"));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(4, 4, 4, 4);
        contentJPanel.add(targetDirJLabel, constraints);

        // create the JTextField that holds the reference to the targetJarFilenameJTextField
        targetJarFilenameJTextField.setPreferredSize(new Dimension(240, 20));
        targetJarFilenameJTextField.setMinimumSize(new Dimension(240, 20));
        targetJarFilenameJTextField.setMaximumSize(new Dimension(400, 20));
        targetJarFilenameJTextField.setText(Settings.getMostRecentCopyLocation().toString());
        targetJarFilenameJTextField.setInputVerifier(new InputVerifier() {

            @Override
            public boolean shouldYieldFocus(JComponent input) {
                String validationFile = ((JTextField) input).getText();
                if (!validationFile.toUpperCase().endsWith(".JAR")) {
                    ((JTextField) input).setText(validationFile + ".jar");
                }
                return true;
            }

            public boolean verify(JComponent input) {
                return true;
            }
        });

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.weightx = 0.8;
        constraints.insets = new Insets(4, 4, 4, 4);
        contentJPanel.add(targetJarFilenameJTextField, constraints);

        // add button to choose groupTarget Directory
        chooseDirJButton.setPreferredSize(Settings.threeDotButtonDimension);
        chooseDirJButton.setMinimumSize(Settings.threeDotButtonDimension);
        chooseDirJButton.setMaximumSize(Settings.threeDotButtonDimension);
        chooseDirJButton.setText(Settings.jpoResources.getString("threeDotText"));
        chooseDirJButton.addActionListener(this);
        constraints.gridx++;
        ;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.insets = new Insets(4, 4, 4, 4);
        contentJPanel.add(chooseDirJButton, constraints);

        // crate a JPanel for the buttons
        JPanel buttonJPanel = new JPanel();

        // add the export button
        exportJButton.setPreferredSize(Settings.defaultButtonDimension);
        exportJButton.setMinimumSize(Settings.defaultButtonDimension);
        exportJButton.setMaximumSize(Settings.defaultButtonDimension);
        exportJButton.setDefaultCapable(true);
        this.getRootPane().setDefaultButton(exportJButton);
        exportJButton.addActionListener(this);
        buttonJPanel.add(exportJButton);

        // add the cancel button
        cancelJButton.setPreferredSize(Settings.defaultButtonDimension);
        cancelJButton.setMinimumSize(Settings.defaultButtonDimension);
        cancelJButton.setMaximumSize(Settings.defaultButtonDimension);
        cancelJButton.addActionListener(this);
        buttonJPanel.add(cancelJButton);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.weightx = 0.5;
        constraints.insets = new Insets(4, 4, 4, 4);
        contentJPanel.add(buttonJPanel, constraints);

        setContentPane(contentJPanel);

        pack();
        setVisible(true);
    }

    /**
     *  Closes and disposes of the JarDistillerJFrame
     */
    private void getRid() {
        setVisible(false);
        dispose();
    }

    /**
     *  Bring up a jFileChooser and puts the selected directory
     *  back into the targetJarFilenameJTextField.
     */
    private void selectJarFile() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser.setDialogTitle(Settings.jpoResources.getString("SelectJarFileTitle"));
        jFileChooser.setMultiSelectionEnabled(false);
        jFileChooser.setFileFilter(new JarFilter());
        jFileChooser.setCurrentDirectory(Settings.getMostRecentCopyLocation());

        int returnVal = jFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            targetJarFilenameJTextField.setText(jFileChooser.getSelectedFile().getPath());
            Tools.setFilenameExtension("jar", targetJarFilenameJTextField);
        }
    }

    /**
     *  method that outputs the selected group to a directory
     */
    private boolean exportToDirectory() {
        Tools.setFilenameExtension("jar", targetJarFilenameJTextField);
        File targetFile = new File(targetJarFilenameJTextField.getText());
        if (!targetFile.exists()) {
            Settings.memorizeCopyLocation(targetFile.getPath());
            new JarDistiller(targetFile, startNode);
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Target file exists! Save aborted.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     *  method that analyses the user initiated action and performs what the user requested
     **/
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chooseDirJButton) {
            selectJarFile();
        }
        if (e.getSource() == cancelJButton) {
            getRid();
        }
        if (e.getSource() == exportJButton) {
            if (exportToDirectory()) {
                getRid();
            }
        }
    }
    ;
} 