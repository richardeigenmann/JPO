package jpo.export;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import jpo.dataModel.Settings;
import static jpo.export.HtmlDistillerOptions.OutputTarget.OUTPUT_FTP_LOCATION;
import static jpo.export.HtmlDistillerOptions.OutputTarget.OUTPUT_LOCAL_DIRECTORY;
import static jpo.export.HtmlDistillerOptions.OutputTarget.OUTPUT_SSH_LOCATION;
import jpo.gui.DirectoryChooser;
import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/*
 GenerateWebsiteWizard6Where: Ask where to generate the website

 Copyright (C) 2008-2016  Richard Eigenmann.
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
    private static final Logger LOGGER = Logger.getLogger( GenerateWebsiteWizard6Where.class.getName() );
    /**
     * The link to the values that this panel should change
     */
    private final HtmlDistillerOptions options;

    /**
     * This Wizard prompts for the options regarding Highres
     *
     * @param options The data object with all the settings
     */
    public GenerateWebsiteWizard6Where( HtmlDistillerOptions options ) {
        super( Settings.jpoResources.getString( "HtmlDistTarget" ), Settings.jpoResources.getString( "HtmlDistTarget" ) );
        this.options = options;

        // load the options into the GUI components
        switch ( options.getOutputTarget() ) {
            case OUTPUT_FTP_LOCATION:
                finalTarget.setSelectedIndex( 1 );
                break;
            case OUTPUT_SSH_LOCATION:
                finalTarget.setSelectedIndex( 2 );
                break;
            default : // case OUTPUT_LOCAL_DIRECTORY:
                finalTarget.setSelectedIndex( 0 );
                break;
        }

    }
    private static final String[] TARGET_OPTIONS = { "Local Directory", "FTP Location", "SSH Location" };
    private final JComboBox finalTarget = new JComboBox<>( TARGET_OPTIONS );
    /**
     * Text field that holds the directory that the html is to be exported to.
     *
     */
    private final DirectoryChooser targetDirJTextField
            = new DirectoryChooser( Settings.jpoResources.getString( "HtmlDistillerChooserTitle" ),
                    DirectoryChooser.DIR_MUST_BE_WRITABLE );
    /**
     * The ftp Server
     *
     */
    private JTextField ftpServer = new JTextField();
    /**
     * The FTP port
     */
    private final JSpinner ftpPort = new JSpinner( new SpinnerNumberModel( 21, 0, 65_535, 1 ) );
    /**
     * The ftp user
     *
     */
    private JTextField ftpUser = new JTextField();
    /**
     * The ftp password
     *
     */
    private JTextField ftpPassword = new JTextField();
    /**
     * The ftp target dir
     *
     */
    private JTextField ftpTargetDir = new JTextField();
    /**
     * The ftp error message
     *
     */
    private JMultilineLabel ftpError = new JMultilineLabel();
    /**
     * The ssh Server
     *
     */
    private JTextField sshServer = new JTextField();
    /**
     * ssh Port
     */
    private final JSpinner sshPort = new JSpinner( new SpinnerNumberModel( 22, 0, 65_535, 1 ) );
    /**
     * The ssh user
     *
     */
    private JTextField sshUser = new JTextField();
    /**
     * SSH Authentication Options
     */
    private static final String[] SSH_AUTH_OPTIONS = { "Password", "SSH KEY File" };
    /**
     * SSH Authentication Options
     */
    private final JComboBox sshAuthOoptionChooser = new JComboBox<>( SSH_AUTH_OPTIONS );
    /**
     * The ssh password
     *
     */
    private JTextField sshPassword = new JTextField();
    /**
     * The ssh key file
     *
     */
    private JTextField sshKeyFile = new JTextField();
    /**
     * The ssh target dir
     *
     */
    private JTextField sshTargetDir = new JTextField();
    /**
     * The ssh error message
     *
     */
    private JMultilineLabel sshError = new JMultilineLabel();

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        final JPanel wizardPanel = new JPanel( new MigLayout() );
        final JPanel ftpPanel = new JPanel( new MigLayout( "", "[][250:250:800]", "" ) );
        final JPanel sshPanel = new JPanel( new MigLayout( "", "[][250:250:800]", "" ) );

        finalTarget.addActionListener(( ActionEvent arg0 ) -> {
            switch ( finalTarget.getSelectedIndex() ) {
                case 1:
                    ftpPanel.setVisible( true );
                    sshPanel.setVisible( false );
                    GenerateWebsiteWizard6Where.this.options.setOutputTarget( HtmlDistillerOptions.OutputTarget.OUTPUT_FTP_LOCATION );
                    break;
                case 2:
                    ftpPanel.setVisible( false );
                    sshPanel.setVisible( true );
                    GenerateWebsiteWizard6Where.this.options.setOutputTarget( HtmlDistillerOptions.OutputTarget.OUTPUT_SSH_LOCATION );
                    break;
                default: // case 0:
                    ftpPanel.setVisible( false );
                    sshPanel.setVisible( false );
                    GenerateWebsiteWizard6Where.this.options.setOutputTarget( HtmlDistillerOptions.OutputTarget.OUTPUT_LOCAL_DIRECTORY );
                    break;
            }
        });

        wizardPanel.add( finalTarget, "wrap" );
        wizardPanel.add( new JLabel( Settings.jpoResources.getString( "genericTargetDirText" ) ), "align label, wrap" );

        wizardPanel.add( targetDirJTextField, "wrap" );

        JButton checkButton = new JButton( Settings.jpoResources.getString( "check" ) );
        checkButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        checkButton.setMaximumSize( Settings.defaultButtonDimension );
        checkButton.addActionListener(( ActionEvent arg0 ) -> {
            options.setTargetDirectory( targetDirJTextField.getDirectory() );
            setCanGoNext( check( options.getTargetDirectory() ) );
        });
        wizardPanel.add( checkButton, "wrap" );

        ftpPanel.add( new JLabel( "Ftp Server:" ), "align label" );

        ftpServer.setAlignmentX( Component.LEFT_ALIGNMENT );
        ftpServer.addFocusListener( new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent e ) {
                options.setFtpServer( ftpServer.getText() );
            }
        } );
        ftpPanel.add( ftpServer, "growx, wrap" );

        ftpPanel.add( new JLabel( "FTP Port " ), "align label" );
        // Records the ftp port number, 0 to 65535, start at 21 increment 1

        ftpPort.addChangeListener(( ChangeEvent arg0 ) -> {
            options.setFtpPort( ( (SpinnerNumberModel) ( ftpPort.getModel() ) ).getNumber().intValue() );
        });
        ftpPanel.add( ftpPort, "wrap" );

        ftpPanel.add( new JLabel( "FTP user:" ), "align label" );
        ftpUser.addFocusListener( new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent e ) {
                options.setFtpUser( ftpUser.getText() );
            }
        } );

        ftpPanel.add( ftpUser, "growx, wrap" );

        ftpPanel.add( new JLabel( "FTP password:" ), "align label" );
        ftpPassword.addFocusListener( new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent e ) {
                options.setFtpPassword( ftpPassword.getText() );
            }
        } );
        ftpPanel.add( ftpPassword, "growx, wrap" );

        ftpPanel.add( new JLabel( "Target directory:" ), "align label" );
        ftpTargetDir.addFocusListener( new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent e ) {
                String targetDir = ftpTargetDir.getText();
                if ( !( targetDir.endsWith( "/" ) || targetDir.endsWith( "\\" ) ) ) {
                    targetDir += '/';
                    ftpTargetDir.setText( targetDir );
                }
                options.setFtpTargetDir( targetDir );
            }
        } );
        ftpPanel.add( ftpTargetDir,
                "growx, wrap" );

        final JButton ftpTestJButton = new JButton( "Test" );
        ftpTestJButton.addActionListener(( ActionEvent e ) -> {
            String returnString = testFtpConnection();
            ftpError.setText( returnString );
        });
        ftpPanel.add( ftpTestJButton, "align label" );

        ftpPanel.add( ftpError,
                "grow, spany2, wrap" );

        final JButton ftpMkdirJButton = new JButton( "mkdir" );
        ftpMkdirJButton.addActionListener(( ActionEvent e ) -> {
            String returnString = ftpMkdir();
            ftpError.setText( returnString );
        });
        ftpPanel.add( ftpMkdirJButton, "align label, wrap" );

        wizardPanel.add( ftpPanel,
                "hidemode 3, wrap" );

        sshPanel.add(
                new JLabel( "SSH Server:" ), "align label" );
        sshServer.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost( FocusEvent e ) {
                        options.setSshServer( sshServer.getText() );
                    }
                } );
        sshPanel.add( sshServer,
                "growx, wrap" );

        sshPanel.add(
                new JLabel( "SSH Port " ), "align label" );
        sshPort.addChangeListener(( ChangeEvent arg0 ) -> {
            options.setSshPort( ( (SpinnerNumberModel) ( sshPort.getModel() ) ).getNumber().intValue() );
        });
        sshPanel.add( sshPort,
                "wrap" );

        sshPanel.add(
                new JLabel( "SSH user:" ), "align label" );
        sshUser.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost( FocusEvent e ) {
                        options.setSshUser( sshUser.getText() );
                    }
                } );
        sshPanel.add( sshUser,
                "growx, wrap" );

        final JLabel sshPasswordLabel = new JLabel( "SSH password:" );
        final JLabel sshKeyfileLabel = new JLabel( "SSH keyfile:" );

        sshPanel.add(
                new JLabel( "SSH Auth:" ), "align label" );
        sshAuthOoptionChooser.addActionListener(( ActionEvent arg0 ) -> {
            switch ( sshAuthOoptionChooser.getSelectedIndex() ) {
                case 1:
                    sshPasswordLabel.setVisible( false );
                    sshPassword.setVisible( false );
                    sshKeyfileLabel.setVisible( true );
                    sshKeyFile.setVisible( true );
                    GenerateWebsiteWizard6Where.this.options.setSshAuthType( HtmlDistillerOptions.SshAuthType.SSH_AUTH_KEYFILE );
                    break;
                default: // case 0:
                    sshPasswordLabel.setVisible( true );
                    sshPassword.setVisible( true );
                    sshKeyfileLabel.setVisible( false );
                    sshKeyFile.setVisible( false );
                    GenerateWebsiteWizard6Where.this.options.setSshAuthType( HtmlDistillerOptions.SshAuthType.SSH_AUTH_PASSWORD );
                    break;
            }
        });
        sshPanel.add( sshAuthOoptionChooser,
                "wrap" );

        sshPanel.add( sshPasswordLabel,
                "hidemode 3, align label" );
        sshPassword.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost( FocusEvent e ) {
                        options.setSshPassword( sshPassword.getText() );
                        LOGGER.info( String.format( "Saving SSH Password: %s into options: %s", sshPassword.getText(), options.getSshPassword() ) );
                    }
                } );
        sshPanel.add( sshPassword,
                "hidemode 3, growx, wrap" );

        sshPanel.add( sshKeyfileLabel,
                "hidemode 3, align label" );
        sshKeyFile.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost( FocusEvent e ) {
                        options.setSshKeyFile( sshKeyFile.getText() );
                    }
                } );
        sshPanel.add( sshKeyFile,
                "hidemode 3, growx, wrap" );

        sshPanel.add(
                new JLabel( "SSH Target dir:" ), "align label" );
        sshTargetDir.addFocusListener(
                new FocusAdapter() {
                    @Override
                    public void focusLost( FocusEvent e ) {
                        options.setSshTargetDir( sshTargetDir.getText() );
                    }
                } );
        sshPanel.add( sshTargetDir,
                "growx, wrap" );

        final JButton sshTestJButton = new JButton( "Test" );

        sshTestJButton.addActionListener(( ActionEvent e ) -> {
            testSshConnection();
        });
        sshPanel.add( sshTestJButton,
                "align label" );
        sshPanel.add( sshError,
                "grow, spany2, wrap" );

        JButton sshMkdirJButton = new JButton( "mkdir" );

        sshMkdirJButton.addActionListener(( ActionEvent e ) -> {
            sshMkdir();
        });
        sshPanel.add( sshMkdirJButton,
                "align label, wrap" );

        final Font errorLabelFont = Font.decode( Settings.jpoResources.getString( "ThumbnailDescriptionJPanelLargeFont" ) );

        sshError.setFont( errorLabelFont );

        wizardPanel.add( sshPanel,
                "hidemode 3, wrap" );

        setWidgets( options );
        return wizardPanel;
    }

    /**
     * Enforces that the user must check the directory before he can go next
     */
    @Override
    public void prepareRendering() {
        setCanGoNext( false );
    }

    /**
     * Loads the data from the options into the GUI widgets
     * @param options options
     */
    private void setWidgets( HtmlDistillerOptions options ) {
        // load the options into the GUI components
        switch ( options.getOutputTarget() ) {
            case OUTPUT_FTP_LOCATION:
                finalTarget.setSelectedIndex( 1 );
                break;
            case OUTPUT_SSH_LOCATION:
                finalTarget.setSelectedIndex( 2 );
                break;
            default : //case OUTPUT_LOCAL_DIRECTORY:
                finalTarget.setSelectedIndex( 0 );
                break;
        }

        ftpServer.setText( options.getFtpServer() );
        ftpPort.setValue( options.getFtpPort() );
        ftpUser.setText( options.getFtpUser() );
        ftpPassword.setText( options.getFtpPassword() );
        ftpTargetDir.setText( options.getFtpTargetDir() );

        sshServer.setText( options.getSshServer() );
        sshPort.setValue( options.getSshPort() );
        sshUser.setText( options.getSshUser() );
        switch ( options.getSshAuthType() ) {
            case SSH_AUTH_PASSWORD:
                sshAuthOoptionChooser.setSelectedIndex( 0 );
                break;
            case SSH_AUTH_KEYFILE:
                sshAuthOoptionChooser.setSelectedIndex( 1 );
                break;
        }
        sshPassword.setText( options.getSshPassword() );
        sshTargetDir.setText( options.getSshTargetDir() );
        sshKeyFile.setText( options.getSshKeyFile() );
    }

    /**
     * Checks whether the supplied file is good for webpage generation and spams
     * popups if not
     *
     * @param targetDirectory Target directory
     * @return true if ok, false if not
     */
    public static boolean check( File targetDirectory ) {
        if ( !targetDirectory.exists() ) {
            try {
                targetDirectory.mkdirs();
            } catch ( SecurityException e ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistCrtDirError" ) + "\n" + e.getMessage(),
                        Settings.jpoResources.getString( "genericSecurityException" ),
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
        } else {
            if ( !targetDirectory.isDirectory() ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistIsDirError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
            if ( !targetDirectory.canWrite() ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistCanWriteError" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
            if ( targetDirectory.listFiles().length > 0 ) {
                int option = JOptionPane.showConfirmDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "htmlDistIsNotEmptyWarning" ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE );
                if ( option == JOptionPane.CANCEL_OPTION ) {
                    return false;
                }
            }
        }
        return true;
    }

    private void testSshConnection() {
        String command = "if [ -d " + options.getSshTargetDir() + " ]; then  echo \"Directory exists\"; else echo \"No such directory\"; fi;\n";
        sshError.setText( executeSshCommand( command ) );
    }

    private void sshMkdir() {
        String command = "mkdir -pv " + options.getSshTargetDir();
        sshError.setText( executeSshCommand( command ) );
    }

    private String executeSshCommand( String command ) {
        LOGGER.info( "Testing ssh connection:" );
        String response = "";
        JSch jsch = new JSch();
        try {
            LOGGER.info( String.format( "Setting up session for user: %s server: %s port: %d and connecting...", options.getSshUser(), options.getSshServer(), options.getSshPort() ) );
            Session session = jsch.getSession( options.getSshUser(), options.getSshServer(), options.getSshPort() );
            if ( options.getSshAuthType().equals( HtmlDistillerOptions.SshAuthType.SSH_AUTH_PASSWORD ) ) {
                session.setPassword( options.getSshPassword() );
            } else {
                jsch.addIdentity( options.getSshKeyFile() );
            }
            //jsch.setKnownHosts( "/home"+ options.getSshUser() + "/.ssh/known_hosts");
            Properties config = new Properties();
            config.put( "StrictHostKeyChecking", "no" );
            session.setConfig( config );
            session.connect();

            LOGGER.info( "Opening Channel \"exec\"..." );
            Channel channel = session.openChannel( "exec" );
            LOGGER.log( Level.INFO, "Setting command: {0}", command );
            ( (ChannelExec) channel ).setCommand( command );

            InputStream in = channel.getInputStream();

            LOGGER.info( "Connecting Channel..." );
            channel.connect();

            byte[] tmp = new byte[1024];
            while ( true ) {
                while ( in.available() > 0 ) {
                    int i = in.read( tmp, 0, 1024 );
                    if ( i < 0 ) {
                        break;
                    }
                    response = new String( tmp, 0, i );
                    LOGGER.info( response );
                }
                if ( channel.isClosed() ) {
                    response += "exit-status: " + channel.getExitStatus();
                    LOGGER.log( Level.INFO, "exit-status: {0}", channel.getExitStatus() );
                    break;
                }
            }

            channel.disconnect();
            session.disconnect();
        } catch ( JSchException | IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            response = ex.getMessage();
        }
        return response;

    }

    /**
     * From
     * http://stackoverflow.com/questions/2152742/java-swing-multiline-labels
     */
    private class JMultilineLabel extends JTextArea {

        private static final long serialVersionUID = 1L;

        JMultilineLabel() {
            super();
            setEditable( false );
            setCursor( null );
            setOpaque( false );
            setFocusable( false );
            setWrapStyleWord( true );
            setLineWrap( true );
        }
    }

    private String testFtpConnection() {
        LOGGER.info( "Setting up ftp connection:" );
        String returnString = "Connection OK.";
        final FTPClient ftp = new FTPClient();
        int reply;
        try {
            ftp.connect( options.getFtpServer(), options.getFtpPort() );
            reply = ftp.getReplyCode();
            if ( !FTPReply.isPositiveCompletion( reply ) ) {
                ftp.disconnect();
                LOGGER.severe( "FTP server refused connection." );
                return "FTP server refused connection.";
            }

            LOGGER.info( "Good connection:" );
            boolean error = false;
            __main:
            {
                if ( !ftp.login( options.getFtpUser(), options.getFtpPassword() ) ) {
                    ftp.logout();
                    error = true;
                    LOGGER.info( "Could not log in." );
                    returnString += " But could not log in.";
                    break __main;
                }

                LOGGER.log( Level.INFO, "Remote system is {0}", ftp.getSystemType());
                ftp.setFileType( FTP.BINARY_FILE_TYPE );
                ftp.enterLocalPassiveMode();

                boolean dirExists = ftp.changeWorkingDirectory( options.getFtpTargetDir() );
                if ( !dirExists ) {
                    returnString += " But directory doesn't exist.";
                } else {
                    returnString += " And directory exists.";
                }
            }
            ftp.disconnect();

        } catch ( SocketException ex ) {
            LOGGER.severe( ex.getMessage() );
            return ex.getMessage();
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            return ex.getMessage();
        }
        return returnString;
    }

    private String ftpMkdir() {
        LOGGER.info( "Setting up ftp connection:" );
        String returnString = "Connection OK.";
        final FTPClient ftp = new FTPClient();
        int reply;
        try {
            ftp.connect( options.getFtpServer(), options.getFtpPort() );
            reply = ftp.getReplyCode();
            if ( !FTPReply.isPositiveCompletion( reply ) ) {
                ftp.disconnect();
                LOGGER.severe( "FTP server refused connection." );
                return "FTP server refused connection.";
            }

            LOGGER.info( "Good connection:" );
            boolean error = false;
            __main:
            {
                if ( !ftp.login( options.getFtpUser(), options.getFtpPassword() ) ) {
                    ftp.logout();
                    error = true;
                    LOGGER.info( "Could not log in." );
                    returnString += " But could not log in.";
                    break __main;
                }

                LOGGER.log( Level.INFO, "Remote system is {0}", ftp.getSystemType());
                ftp.setFileType( FTP.BINARY_FILE_TYPE );
                ftp.enterLocalPassiveMode();

                boolean dirExists = ftp.makeDirectory( options.getFtpTargetDir() );
                if ( !dirExists ) {
                    returnString += " But could not make the directory.";
                } else {
                    returnString += " And created die directory.";
                }
            }
            ftp.disconnect();

        } catch ( SocketException ex ) {
            LOGGER.severe( ex.getMessage() );
            return ex.getMessage();
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            return ex.getMessage();
        }
        return returnString;
    }
}
