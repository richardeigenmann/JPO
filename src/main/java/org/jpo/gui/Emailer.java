package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2006 - 2017  Richard Eigenmann.
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
 * This thread sends the emails.
 */
public class Emailer
        extends SwingWorker<String, String> {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( Emailer.class.getName() );

    /**
     * Frame to show what the thread is doing.
     */
    private final JFrame progressFrame;

    /**
     * Label to show what is being processed.
     */
    private final JLabel progressLabel;

    /**
     * Progress Indicator.
     */
    private final JProgressBar progBar;

    /**
     * Variable that signals to the thread to stop immediately.
     */
    private boolean interrupted;  // default is false

    private final List<SortableDefaultMutableTreeNode> emailSelected;

    private final InternetAddress senderAddress;

    private final InternetAddress destinationAddress;

    private final String subjectLine;

    private final String bodyText;

    private final boolean scaleImages;

    private final Dimension scaleSize;

    private final boolean sendOriginal;

    /**
     * Creates and starts a Thread that writes the picture nodes from the
     * specified startNode to the target directory.
     *
     * @param emailSelected List of nodes for emailing
     * @param senderAddress The sender address
     * @param destinationAddress The destination Address
     * @param subjectLine The subject line
     * @param bodyText The body Text
     * @param scaleImages Whether to scale images
     * @param scaleSize The size to scale them to
     * @param sendOriginal Whether to include originals
     */
    public Emailer( List<SortableDefaultMutableTreeNode> emailSelected,
            InternetAddress senderAddress,
            InternetAddress destinationAddress,
            String subjectLine,
            String bodyText,
            boolean scaleImages,
            Dimension scaleSize,
            boolean sendOriginal ) {

        this.emailSelected = emailSelected;
        this.senderAddress = senderAddress;
        this.destinationAddress = destinationAddress;
        this.subjectLine = subjectLine;
        this.bodyText = bodyText;
        this.scaleImages = scaleImages;
        this.scaleSize = scaleSize;
        this.sendOriginal = sendOriginal;

        Tools.checkEDT();

        JPanel progPanel = new JPanel();
        progPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        progPanel.setLayout( new MigLayout() );

        progressLabel = new JLabel();
        progressLabel.setPreferredSize( new Dimension( 400, 20 ) );
        progressLabel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        progPanel.add( progressLabel, "wrap" );

        progBar = new JProgressBar( 0, emailSelected.size() + 3 ); // 3 extra steps
        progBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
        progBar.setStringPainted( true );
        progBar.setPreferredSize( new Dimension( 140, 20 ) );
        progBar.setMaximumSize( new Dimension( 240, 20 ) );
        progBar.setMinimumSize( new Dimension( 140, 20 ) );
        progBar.setValue( 0 );
        progPanel.add( progBar );

        JButton cancelButton = new JButton( "Cancel" );
        cancelButton.addActionListener( ( ActionEvent e ) -> {
            progressLabel.setText( Settings.jpoResources.getString( "htmlDistillerInterrupt" ) );
            interrupted = true;
        } );
        cancelButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelButton.setMinimumSize( Settings.defaultButtonDimension );

        progPanel.add( cancelButton, "tag[cancel]" );

        progressFrame = new JFrame( Settings.jpoResources.getString( "EmailerJFrame" ) );
        progressFrame.getContentPane().add( progPanel );
        progressFrame.pack();
        progressFrame.setVisible( true );
        progressFrame.setLocationRelativeTo( Settings.anchorFrame );

        execute();
    }

    /**
     * This is where the SwingWorker does it's stuff
     *
     * @return "Done"
     */
    @Override
    protected String doInBackground() {
        switch ( Settings.emailAuthentication ) {
            case 1:
                sendEmailAuth();
                break; // Password
            case 2:
                sendEmailSSL();
                break;// SSL
            default:
                sendEmailNoAuth();
                break; // No Authentication
        }
        return ( "Done" );
    }

    @Override
    protected void done() {
        progressFrame.dispose();
        if ( "".equals( error ) ) {
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "emailOK" ),
                    Settings.jpoResources.getString( "genericOKText" ),
                    JOptionPane.INFORMATION_MESSAGE );
        } else {
            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "emailSendError" ) + error,
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
        }
    }

    /**
     * Error message to show if there is an issue:
     */
    private String error = "";

    /**
     *
     * @param chunks Chunks
     */
    @Override
    protected void process( java.util.List<String> chunks ) {
        for ( String s : chunks ) {
            progBar.setValue( progBar.getValue() + 1 );
            progressLabel.setText( s );
        }

    }

    /**
     * This method returns the MimeMessage object that should be emailed.
     *
     * @param session the session
     * @return The MimeMessage for the email.
     */
    private MimeMessage buildMessage( Session session ) {
        // Define message
        MimeMessage message;
        try {
            message = new MimeMessage( session );
            message.setFrom( senderAddress );
            message.addRecipient( Message.RecipientType.TO, destinationAddress );
            message.setSubject( subjectLine );

            Multipart mp = new MimeMultipart();

            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText( bodyText );
            mp.addBodyPart( mbp1 );

            MimeBodyPart scaledPictureMimeBodyPart;
            MimeBodyPart originalPictureMimeBodyPart;
            MimeBodyPart pictureDescriptionMimeBodyPart;
            ScalablePicture scalablePicture = new ScalablePicture();
            scalablePicture.setScaleSize( scaleSize );

            File highresFile;
            PictureInfo pi;
            DataSource ds;
            ByteArrayOutputStream baos;
            EncodedDataSource encds;
            for ( int i = 0; ( i < emailSelected.size() ) && ( !interrupted ); i++ ) {
                publish( String.format( "%d / %d", progBar.getValue(), progBar.getMaximum() ) );
                pictureDescriptionMimeBodyPart = new MimeBodyPart();
                pi = (PictureInfo) emailSelected.get( i ).getUserObject();

                pictureDescriptionMimeBodyPart.setText( pi.getDescription(), "iso-8859-1" );
                mp.addBodyPart( pictureDescriptionMimeBodyPart );

                if ( scaleImages ) {
                    LOGGER.log( Level.INFO, "{0}{1}", new Object[]{ Settings.jpoResources.getString( "EmailerLoading" ), pi.getImageFile().toString() } );
                    scalablePicture.loadPictureImd( pi.getImageFile(), pi.getRotation() );
                    LOGGER.log( Level.INFO, "{0}{1}", new Object[]{ Settings.jpoResources.getString( "EmailerScaling" ), pi.getImageFile().toString() } );
                    scalablePicture.scalePicture();
                    baos = new ByteArrayOutputStream();
                    LOGGER.log( Level.INFO, "{0}{1}", new Object[]{ Settings.jpoResources.getString( "EmailerWriting" ), pi.getImageFile().toString() } );
                    scalablePicture.writeScaledJpg( baos );
                    encds = new EncodedDataSource("image/jpeg", pi.getImageFile().getName(), baos);
                    scaledPictureMimeBodyPart = new MimeBodyPart();
                    scaledPictureMimeBodyPart.setDataHandler( new DataHandler( encds ) );
                    scaledPictureMimeBodyPart.setFileName( pi.getImageFile().getName() );
                    LOGGER.log( Level.INFO, "{0}{1}", new Object[]{ Settings.jpoResources.getString( "EmailerAdding" ), pi.getImageFile().toString() } );
                    mp.addBodyPart( scaledPictureMimeBodyPart );
                }

                if ( sendOriginal ) {
                    // create the message part fro the original image
                    originalPictureMimeBodyPart = new MimeBodyPart();
                    highresFile = pi.getImageFile();
                    // attach the file to the message
                    ds = new FileDataSource( highresFile );
                    originalPictureMimeBodyPart.setDataHandler( new DataHandler( ds ) );
                    originalPictureMimeBodyPart.setFileName( pi.getImageFile().getName() );
                    // create the Multipart and add its parts to it
                    LOGGER.log( Level.INFO, "{0}{1}", new Object[]{ Settings.jpoResources.getString( "EmailerAdding" ), pi.getImageFile().toString() } );
                    mp.addBodyPart( originalPictureMimeBodyPart );
                }

            }
            // add the Multipart to the message
            message.setContent( mp );
            publish( "Sending..." );

        } catch ( MessagingException x ) {
            LOGGER.severe( x.getMessage() );
            return null;
        }
        return message;
    }

    /**
     * method that sends the email
     */
    private void sendEmailNoAuth() {
        Properties props = System.getProperties();
        props.setProperty( "mail.smtp.host", Settings.emailServer );
        props.setProperty( "mail.smtp.port", Settings.emailPort );
        //props.put( "mail.debug", "true" );
        props.setProperty( "mail.smtp.socketFactory.port", Settings.emailPort );
        Session session = Session.getDefaultInstance( props, null );
        //session.setDebug( true );

        // Send message
        if ( interrupted ) {
            LOGGER.info( "EmailerThread: message not sent due to user clicking cancel." );
        } else {
            try {
                publish( Settings.jpoResources.getString( "EmailerSending" ) );
                MimeMessage msg = buildMessage(session);
                Objects.requireNonNull(msg, "msg must not be null");
                Transport.send(msg);
                publish( Settings.jpoResources.getString( "EmailerSent" ) );
            } catch ( MessagingException x ) {
                LOGGER.severe( x.getMessage() );
            }
        }
    }

    /**
     * method that sends the email
     */
    private void sendEmailAuth() {
        Properties props = System.getProperties();
        props.setProperty( "mail.smtp.host", Settings.emailServer );
        props.setProperty( "mail.smtp.port", Settings.emailPort );
        //props.setProperty( "mail.debug", "true" );
        props.setProperty( "mail.smtp.socketFactory.port", Settings.emailPort );
        props.setProperty( "mail.smtp.auth", "true" );
        props.setProperty( "mail.smtp.socketFactory.fallback", "false" );
        props.setProperty( "mail.smtp.socketFactory.port", Settings.emailPort );
        props.setProperty( "mail.smtp.starttls.enable", "true" );

        Session session = Session.getDefaultInstance( props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( Settings.emailUser, Settings.emailPassword );
            }
        } );
        //session.setDebug( true );

        // Send message
        if ( interrupted ) {
            LOGGER.info( "EmailerThread: message not sent due to user clicking cancel." );
        } else {
            try {
                publish( Settings.jpoResources.getString( "EmailerSending" ) );
                MimeMessage msg = buildMessage(session);
                Objects.requireNonNull(msg, "msg must not be null");
                Transport.send(msg);
                publish( Settings.jpoResources.getString( "EmailerSent" ) );
            } catch ( MessagingException x ) {
                LOGGER.severe( x.getLocalizedMessage() );
                error = x.getMessage();
            }
        }
    }

    /**
     * method that sends the email via SSL
     */
    private void sendEmailSSL() {
        Properties props = System.getProperties();
        props.setProperty( "mail.smtp.host", Settings.emailServer );
        props.setProperty( "mail.smtp.port", Settings.emailPort );
        props.setProperty( "mail.smtp.auth", "true" );
        //props.put( "mail.debug", "true" );
        props.setProperty( "mail.smtp.socketFactory.port", Settings.emailPort );
        props.setProperty( "mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory" );
        props.setProperty( "mail.smtp.socketFactory.fallback", "false" );

        Session session = Session.getDefaultInstance( props,
                new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication( Settings.emailUser, Settings.emailPassword );
            }
        } );
        //session.setDebug( true );
        if ( interrupted ) {
            LOGGER.info( "EmailerThread: message not sent due to user clicking cancel." );
        } else {
            try {
                publish( Settings.jpoResources.getString( "EmailerSending" ) );
                MimeMessage msg = buildMessage(session);
                Objects.requireNonNull(msg, "msg must not be null");
                Transport.send(msg);
                publish( Settings.jpoResources.getString( "EmailerSent" ) );
            } catch ( MessagingException x ) {
                LOGGER.severe( x.getLocalizedMessage() );
                error = x.getMessage();
            }
        }
    }

    /**
     * A class that somehow helps with the emailing
     */
    private static class EncodedDataSource
            implements DataSource {

        EncodedDataSource( String contentType, String filename,
                ByteArrayOutputStream baos ) {
            this.contentType = contentType;
            this.filename = filename;
            this.baos = baos;
        }

        private final String contentType;

        private final String filename;

        private final ByteArrayOutputStream baos;

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream( baos.toByteArray() );
        }

        @Override
        public OutputStream getOutputStream() {
            return null;//new OutputStream();
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            return filename;
        }
    }
}
