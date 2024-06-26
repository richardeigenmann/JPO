package org.jpo.export;

import com.jcraft.jsch.*;
import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.GenerateWebsiteRequest;
import org.jpo.gui.DirectoryChooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.eventbus.GenerateWebsiteRequest.OutputTarget.*;

/*
 Copyright (C) 2008-2024 Richard Eigenmann.
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
 * Asks where we should create the website
 *
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard6Where extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(GenerateWebsiteWizard6Where.class.getName());
    private static final String[] TARGET_OPTIONS = {"Local Directory", "FTP Location", "SSH Location"};
    /**
     * SSH Authentication Options
     */
    private static final String[] SSH_AUTH_OPTIONS = {"Password", "SSH KEY File"};
    /**
     * The link to the values that this panel should change
     */
    private final GenerateWebsiteRequest request;
    private final JComboBox<String> finalTarget = new JComboBox<>(TARGET_OPTIONS);
    /**
     * Text field that holds the directory that the html is to be exported to.
     */
    private final DirectoryChooser targetDirJTextField
            = new DirectoryChooser(Settings.getJpoResources().getString("HtmlDistillerChooserTitle"),
            DirectoryChooser.DIR_MUST_BE_WRITABLE);
    /**
     * The ftp Server
     */
    private final JTextField ftpServer = new JTextField();
    /**
     * The FTP port
     */
    private final JSpinner ftpPort = new JSpinner(new SpinnerNumberModel(21, 0, 65_535, 1));
    /**
     * The ftp user
     */
    private final JTextField ftpUser = new JTextField();
    /**
     * The ftp password
     */
    private final JTextField ftpPassword = new JTextField();
    /**
     * The ftp target dir
     */
    private final JTextField ftpTargetDir = new JTextField();
    /**
     * The ftp error message
     */
    private final JMultilineLabel ftpError = new JMultilineLabel();
    /**
     * The ssh Server
     */
    private final JTextField sshServer = new JTextField();
    /**
     * ssh Port
     */
    private final JSpinner sshPort = new JSpinner(new SpinnerNumberModel(22, 0, 65_535, 1));
    /**
     * The ssh user
     */
    private final JTextField sshUser = new JTextField();
    /**
     * SSH Authentication Options
     */
    private final JComboBox<String> sshAuthOptionChooser = new JComboBox<>(SSH_AUTH_OPTIONS);
    /**
     * The ssh password
     */
    private final JTextField sshPassword = new JTextField();
    /**
     * The ssh key file
     */
    private final JTextField sshKeyFile = new JTextField();
    /**
     * The ssh target dir
     */
    private final JTextField sshTargetDir = new JTextField();
    /**
     * The ssh error message
     */
    private final JMultilineLabel sshError = new JMultilineLabel();

    /**
     * This Wizard prompts for the options regarding Highres
     *
     * @param request The data object with all the settings
     */
    public GenerateWebsiteWizard6Where(final GenerateWebsiteRequest request) {
        super(Settings.getJpoResources().getString("HtmlDistTarget"), Settings.getJpoResources().getString("HtmlDistTarget"));
        this.request = request;

        // load the options into the GUI components
        switch (request.getOutputTarget()) {
            case OUTPUT_FTP_LOCATION -> finalTarget.setSelectedIndex(1);
            case OUTPUT_SSH_LOCATION -> finalTarget.setSelectedIndex(2);
            default -> finalTarget.setSelectedIndex(0);
        }

    }

    /**
     * Checks whether the supplied file is good for webpage generation and spams
     * popups if not
     *
     * @param targetDirectory Target directory
     * @return true if ok, false if not
     */
    public static boolean check(final File targetDirectory) {
        if (!targetDirectory.exists()) {
            try {
                if (!targetDirectory.mkdirs()) {
                    JOptionPane.showMessageDialog(
                            Settings.getAnchorFrame(),
                            "Could not create directory",
                            Settings.getJpoResources().getString("genericSecurityException"),
                            JOptionPane.ERROR_MESSAGE);
                    return false;

                }
            } catch (SecurityException e) {
                JOptionPane.showMessageDialog(
                        Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("htmlDistCrtDirError") + "\n" + e.getMessage(),
                        Settings.getJpoResources().getString("genericSecurityException"),
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            final String GENERIC_ERROR = Settings.getJpoResources().getString("genericError");
            if (!targetDirectory.isDirectory()) {
                JOptionPane.showMessageDialog(
                        Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("htmlDistIsDirError"),
                        GENERIC_ERROR,
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (!targetDirectory.canWrite()) {
                JOptionPane.showMessageDialog(
                        Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("htmlDistCanWriteError"),
                        GENERIC_ERROR,
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (Objects.requireNonNull(targetDirectory.listFiles()).length > 0) {
                int option = JOptionPane.showConfirmDialog(
                        Settings.getAnchorFrame(),
                        Settings.getJpoResources().getString("htmlDistIsNotEmptyWarning"),
                        GENERIC_ERROR,
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                return option != JOptionPane.CANCEL_OPTION;
            }
        }
        return true;
    }

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel(new MigLayout());
        final JPanel ftpPanel = new JPanel(new MigLayout("", "[][250:250:800]", ""));
        final JPanel sshPanel = new JPanel(new MigLayout("", "[][250:250:800]", ""));

        finalTarget.addActionListener((ActionEvent arg0) -> {
            switch (finalTarget.getSelectedIndex()) {
                case 1 -> {
                    ftpPanel.setVisible(true);
                    sshPanel.setVisible(false);
                    GenerateWebsiteWizard6Where.this.request.setOutputTarget(OUTPUT_FTP_LOCATION);
                }
                case 2 -> {
                    ftpPanel.setVisible(false);
                    sshPanel.setVisible(true);
                    GenerateWebsiteWizard6Where.this.request.setOutputTarget(OUTPUT_SSH_LOCATION);
                }
                default -> {
                    ftpPanel.setVisible(false);
                    sshPanel.setVisible(false);
                    GenerateWebsiteWizard6Where.this.request.setOutputTarget(OUTPUT_LOCAL_DIRECTORY);
                }
            }
        });

        wizardPanel.add(finalTarget, "wrap");
        final var ALIGN_LABEL = "align label";
        wizardPanel.add(new JLabel(Settings.getJpoResources().getString("genericTargetDirText")), ALIGN_LABEL + ", wrap");

        wizardPanel.add(targetDirJTextField, "wrap");

        final var checkButton = new JButton(Settings.getJpoResources().getString("check"));
        checkButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkButton.setMaximumSize(Settings.getDefaultButtonDimension());
        checkButton.addActionListener((ActionEvent arg0) -> {
            request.setTargetDirectory(targetDirJTextField.getDirectory());
            setCanGoNext(check(request.getTargetDirectory()));
        });
        wizardPanel.add(checkButton, "wrap");

        ftpPanel.add(new JLabel("Ftp Server:"), ALIGN_LABEL);

        ftpServer.setAlignmentX(Component.LEFT_ALIGNMENT);
        ftpServer.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                request.setFtpServer(ftpServer.getText());
            }
        });
        ftpPanel.add(ftpServer, "growx, wrap");

        ftpPanel.add(new JLabel("FTP Port "), ALIGN_LABEL);
        // Records the ftp port number, 0 to 65535, start at 21 increment 1

        ftpPort.addChangeListener((ChangeEvent arg0) -> request.setFtpPort(((SpinnerNumberModel) (ftpPort.getModel())).getNumber().intValue()));
        ftpPanel.add(ftpPort, "wrap");

        ftpPanel.add(new JLabel("FTP user:"), ALIGN_LABEL);
        ftpUser.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                request.setFtpUser(ftpUser.getText());
            }
        });

        ftpPanel.add(ftpUser, "growx, wrap");

        ftpPanel.add(new JLabel("FTP password:"), ALIGN_LABEL);
        ftpPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                request.setFtpPassword(ftpPassword.getText());
            }
        });
        ftpPanel.add(ftpPassword, "growx, wrap");

        ftpPanel.add(new JLabel("Target directory:"), ALIGN_LABEL);
        ftpTargetDir.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String targetDir = ftpTargetDir.getText();
                if (!(targetDir.endsWith("/") || targetDir.endsWith("\\"))) {
                    targetDir += '/';
                    ftpTargetDir.setText(targetDir);
                }
                request.setFtpTargetDir(targetDir);
            }
        });
        ftpPanel.add(ftpTargetDir,
                "growx, wrap");

        final var ftpTestJButton = new JButton("Test");
        ftpTestJButton.addActionListener((ActionEvent e) -> {
            String returnString = testFtpConnection();
            ftpError.setText(returnString);
        });
        ftpPanel.add(ftpTestJButton, ALIGN_LABEL);

        ftpPanel.add(ftpError,
                "grow, spany2, wrap");

        final var ftpMkdirJButton = new JButton("mkdir");
        ftpMkdirJButton.addActionListener((ActionEvent e) -> {
            String returnString = ftpMkdir();
            ftpError.setText(returnString);
        });
        ftpPanel.add(ftpMkdirJButton, ALIGN_LABEL + ", wrap");

        wizardPanel.add(ftpPanel,
                "hidemode 3, wrap");

        sshPanel.add(
                new JLabel("SSH Server:"), ALIGN_LABEL);
        sshServer.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        request.setSshServer(sshServer.getText());
                    }
                });
        sshPanel.add(sshServer,
                "growx, wrap");

        sshPanel.add(
                new JLabel("SSH Port "), ALIGN_LABEL);
        sshPort.addChangeListener((ChangeEvent arg0) -> request.setSshPort(((SpinnerNumberModel) (sshPort.getModel())).getNumber().intValue()));
        sshPanel.add(sshPort,
                "wrap");

        sshPanel.add(
                new JLabel("SSH user:"), ALIGN_LABEL);
        sshUser.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        request.setSshUser(sshUser.getText());
                    }
                });
        sshPanel.add(sshUser,
                "growx, wrap");

        final var sshPasswordLabel = new JLabel("SSH password:");
        final var sshKeyfileLabel = new JLabel("SSH keyfile:");

        sshPanel.add(
                new JLabel("SSH Auth:"), ALIGN_LABEL);
        sshAuthOptionChooser.addActionListener((ActionEvent arg0) -> {
            if (sshAuthOptionChooser.getSelectedIndex() == 1) {
                sshPasswordLabel.setVisible(false);
                sshPassword.setVisible(false);
                sshKeyfileLabel.setVisible(true);
                sshKeyFile.setVisible(true);
                GenerateWebsiteWizard6Where.this.request.setSshAuthType(GenerateWebsiteRequest.SshAuthType.SSH_AUTH_KEYFILE);
            } else {
                sshPasswordLabel.setVisible(true);
                sshPassword.setVisible(true);
                sshKeyfileLabel.setVisible(false);
                sshKeyFile.setVisible(false);
                GenerateWebsiteWizard6Where.this.request.setSshAuthType(GenerateWebsiteRequest.SshAuthType.SSH_AUTH_PASSWORD);
            }
        });
        sshPanel.add(sshAuthOptionChooser,
                "wrap");

        sshPanel.add(sshPasswordLabel,
                "hidemode 3, align label");
        sshPassword.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        request.setSshPassword(sshPassword.getText());
                        LOGGER.info(String.format("Saving SSH Password: %s into options: %s", sshPassword.getText(), request.getSshPassword()));
                    }
                });
        sshPanel.add(sshPassword,
                "hidemode 3, growx, wrap");

        sshPanel.add(sshKeyfileLabel,
                "hidemode 3, align label");
        sshKeyFile.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        request.setSshKeyFile(sshKeyFile.getText());
                    }
                });
        sshPanel.add(sshKeyFile,
                "hidemode 3, growx, wrap");

        sshPanel.add(
                new JLabel("SSH Target dir:"), ALIGN_LABEL);
        sshTargetDir.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        request.setSshTargetDir(sshTargetDir.getText());
                    }
                });
        sshPanel.add(sshTargetDir,
                "growx, wrap");

        final var sshTestJButton = new JButton("Test");

        sshTestJButton.addActionListener((ActionEvent e) -> testSshConnection());
        sshPanel.add(sshTestJButton,
                ALIGN_LABEL);
        sshPanel.add(sshError,
                "grow, spany2, wrap");

        JButton sshMkdirJButton = new JButton("mkdir");

        sshMkdirJButton.addActionListener((ActionEvent e) -> sshMkdir());
        sshPanel.add(sshMkdirJButton,
                "align label, wrap");

        final var errorLabelFont = Font.decode(Settings.getJpoResources().getString("ThumbnailDescriptionJPanelLargeFont"));

        sshError.setFont(errorLabelFont);

        wizardPanel.add(sshPanel,
                "hidemode 3, wrap");

        setWidgets(request);
        return wizardPanel;
    }

    /**
     * Enforces that the user must check the directory before he can go next
     */
    @Override
    public void prepareRendering() {
        setCanGoNext(false);
    }

    /**
     * Loads the data from the options into the GUI widgets
     *
     * @param request options
     */
    private void setWidgets(final GenerateWebsiteRequest request) {
        switch (request.getOutputTarget()) {
            case OUTPUT_FTP_LOCATION -> finalTarget.setSelectedIndex(1);
            case OUTPUT_SSH_LOCATION -> finalTarget.setSelectedIndex(2);
            default -> finalTarget.setSelectedIndex(0);
        }

        ftpServer.setText(request.getFtpServer());
        ftpPort.setValue(request.getFtpPort());
        ftpUser.setText(request.getFtpUser());
        ftpPassword.setText(request.getFtpPassword());
        ftpTargetDir.setText(request.getFtpTargetDir());

        sshServer.setText(request.getSshServer());
        sshPort.setValue(request.getSshPort());
        sshUser.setText(request.getSshUser());
        if (request.getSshAuthType() == GenerateWebsiteRequest.SshAuthType.SSH_AUTH_KEYFILE) {
            sshAuthOptionChooser.setSelectedIndex(1);
        } else {
            sshAuthOptionChooser.setSelectedIndex(0);
        }
        sshPassword.setText(request.getSshPassword());
        sshTargetDir.setText(request.getSshTargetDir());
        sshKeyFile.setText(request.getSshKeyFile());
    }

    private void testSshConnection() {
        final String command = "if [ -d " + request.getSshTargetDir() + " ]; then  echo \"Directory exists\"; else echo \"No such directory\"; fi;\n";
        sshError.setText(executeSshCommand(command));
    }

    private void sshMkdir() {
        final String command = "mkdir -pv " + request.getSshTargetDir();
        sshError.setText(executeSshCommand(command));
    }

    private String executeSshCommand(final String command) {
        LOGGER.info("Testing ssh connection:");
        var response = "";
        final JSch jsch = new JSch();
        try {
            final Session session = WebsiteGenerator.getSshSession(jsch, request);

            LOGGER.info("Opening Channel \"exec\"...");
            final var channel = session.openChannel("exec");
            LOGGER.log(Level.INFO, "Setting command: {0}", command);
            ((ChannelExec) channel).setCommand(command);

            final InputStream in = channel.getInputStream();

            LOGGER.info("Connecting Channel...");
            channel.connect();

            final byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    response = new String(tmp, 0, i);
                    LOGGER.info(response);
                }
                if (channel.isClosed()) {
                    response += "exit-status: " + channel.getExitStatus();
                    LOGGER.log(Level.INFO, "exit-status: {0}", channel.getExitStatus());
                    break;
                }
            }

            channel.disconnect();
            session.disconnect();
        } catch (final JSchException | IOException ex) {
            LOGGER.severe(ex.getMessage());
            response = ex.getMessage();
        }
        return response;

    }

    private String testFtpConnection() {
        LOGGER.info("Setting up ftp connection:");
        var returnString = "Connection OK.";
        final var ftp = new FTPClient();
        int reply;
        try {
            ftp.connect(request.getFtpServer(), request.getFtpPort());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                LOGGER.severe("FTP server refused connection.");
                return "FTP server refused connection.";
            }

            LOGGER.info("Good connection:");
            if (!ftp.login(request.getFtpUser(), request.getFtpPassword())) {
                ftp.logout();
                LOGGER.info("Could not log in.");
                returnString += " But could not log in.";
            } else {
                LOGGER.log(Level.INFO, "Remote system is {0}", ftp.getSystemType());
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();

                boolean dirExists = ftp.changeWorkingDirectory(request.getFtpTargetDir());
                if (!dirExists) {
                    returnString += " But directory doesn't exist.";
                } else {
                    returnString += " And directory exists.";
                }
            }
            ftp.disconnect();

        } catch (IOException ex) {
            LOGGER.severe(ex.getMessage());
            return ex.getMessage();
        }
        return returnString;
    }

    private String ftpMkdir() {
        LOGGER.info("Setting up ftp connection:");
        var returnString = "Connection OK.";
        final var ftp = new FTPClient();
        int reply;
        try {
            ftp.connect(request.getFtpServer(), request.getFtpPort());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                LOGGER.severe("FTP server refused connection.");
                return "FTP server refused connection.";
            }

            LOGGER.info("Good connection:");
            if (!ftp.login(request.getFtpUser(), request.getFtpPassword())) {
                ftp.logout();
                LOGGER.info("Could not log in.");
                returnString += " But could not log in.";
            } else {
                LOGGER.log(Level.INFO, "Remote system is {0}", ftp.getSystemType());
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();

                final boolean dirExists = ftp.makeDirectory(request.getFtpTargetDir());
                if (!dirExists) {
                    returnString += " But could not make the directory.";
                } else {
                    returnString += " And created die directory.";
                }
            }
            ftp.disconnect();

        } catch (final IOException ex) {
            LOGGER.severe(ex.getMessage());
            return ex.getMessage();
        }
        return returnString;
    }

    /**
     * From
     * <a href="http://stackoverflow.com/questions/2152742/java-swing-multiline-labels">...</a>
     */
    private static class JMultilineLabel extends JTextArea {

        @Serial
        private static final long serialVersionUID = 1L;

        JMultilineLabel() {
            super();
            setEditable(false);
            setCursor(null);
            setOpaque(false);
            setFocusable(false);
            setWrapStyleWord(true);
            setLineWrap(true);
        }
    }
}
