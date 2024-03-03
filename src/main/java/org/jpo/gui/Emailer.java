package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.*;

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
 Copyright (C) 2006-2023 Richard Eigenmann.
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
     * specified receivingNode to the target directory.
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
    public Emailer(final List<SortableDefaultMutableTreeNode> emailSelected,
                   final InternetAddress senderAddress,
                   final InternetAddress destinationAddress,
                   final String subjectLine,
                   final String bodyText,
                   final boolean scaleImages,
                   final Dimension scaleSize,
                   final boolean sendOriginal) {

        this.emailSelected = emailSelected;
        this.senderAddress = senderAddress;
        this.destinationAddress = destinationAddress;
        this.subjectLine = subjectLine;
        this.bodyText = bodyText;
        this.scaleImages = scaleImages;
        this.scaleSize = scaleSize;
        this.sendOriginal = sendOriginal;

        Tools.checkEDT();

        final var progPanel = new JPanel();
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
        progBar.setMaximumSize(new Dimension(240, 20));
        progBar.setMinimumSize(new Dimension(140, 20));
        progBar.setValue(0);
        progPanel.add(progBar);

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            progressLabel.setText(Settings.getJpoResources().getString("htmlDistillerInterrupt"));
            interrupted = true;
        });
        cancelButton.setPreferredSize(Settings.getDefaultButtonDimension());
        cancelButton.setMaximumSize(Settings.getDefaultButtonDimension());
        cancelButton.setMinimumSize(Settings.getDefaultButtonDimension());

        progPanel.add(cancelButton, "tag[cancel]");

        progressFrame = new JFrame(Settings.getJpoResources().getString("EmailerJFrame"));
        progressFrame.getContentPane().add(progPanel);
        progressFrame.pack();
        progressFrame.setVisible(true);
        progressFrame.setLocationRelativeTo(Settings.getAnchorFrame());

        execute();
    }

    /**
     * This is where the SwingWorker does its stuff
     *
     * @return "Done"
     */
    @Override
    protected String doInBackground() {
        switch (Settings.getEmailAuthentication()) {
            // Password
            case 1 -> sendEmailAuth();
            // SSL
            case 2 -> sendEmailSSL();
            // No Authentication
            default -> sendEmailNoAuth();
        }
        return ("Done");
    }

    @Override
    protected void done() {
        progressFrame.dispose();
        if ( "".equals( error ) ) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("emailOK"),
                    Settings.getJpoResources().getString("genericOKText"),
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("emailSendError") + error,
                    Settings.getJpoResources().getString("genericError"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Error message to show if there is an issue:
     */
    private String error = "";

    /**
     * @param chunks Chunks
     */
    @Override
    protected void process(List<String> chunks) {
        for (final var s : chunks) {
            progBar.setValue(progBar.getValue() + 1);
            progressLabel.setText(s);
        }

    }

    /**
     * This method returns the MimeMessage object that should be emailed.
     *
     * @param session the session
     * @return The MimeMessage for the email.
     */
    private MimeMessage buildMessage(final Session session) {
        MimeMessage message;
        try {
            message = new MimeMessage(session);
            message.setFrom(senderAddress);
            message.addRecipient(Message.RecipientType.TO, destinationAddress);
            message.setSubject(subjectLine);

            final Multipart mp = new MimeMultipart();

            final MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText( bodyText );
            mp.addBodyPart( mbp1 );

            MimeBodyPart scaledPictureMimeBodyPart;
            MimeBodyPart originalPictureMimeBodyPart;
            MimeBodyPart pictureDescriptionMimeBodyPart;
            final ScalablePicture scalablePicture = new ScalablePicture();
            scalablePicture.setScaleSize( scaleSize );

            for ( int i = 0; ( i < emailSelected.size() ) && ( !interrupted ); i++ ) {
                publish( String.format( "%d / %d", progBar.getValue(), progBar.getMaximum() ) );
                pictureDescriptionMimeBodyPart = new MimeBodyPart();
                final var pictureInfo = (PictureInfo) emailSelected.get( i ).getUserObject();

                pictureDescriptionMimeBodyPart.setText( pictureInfo.getDescription(), "iso-8859-1" );
                mp.addBodyPart( pictureDescriptionMimeBodyPart );

                if ( scaleImages ) {
                    LOGGER.log(Level.INFO, "{0}{1}", new Object[]{Settings.getJpoResources().getString("EmailerLoading"), pictureInfo.getImageFile()});
                    scalablePicture.loadPictureImd(pictureInfo.getSha256(), pictureInfo.getImageFile(), pictureInfo.getRotation());
                    LOGGER.log(Level.INFO, "{0}{1}", new Object[]{Settings.getJpoResources().getString("EmailerScaling"), pictureInfo.getImageFile()});
                    scalablePicture.scalePicture();
                    final var byteArrayOutputStream = new ByteArrayOutputStream();
                    LOGGER.log(Level.INFO, "{0}{1}", new Object[]{Settings.getJpoResources().getString("EmailerWriting"), pictureInfo.getImageFile()});
                    scalablePicture.writeScaledJpg(byteArrayOutputStream);
                    final var encodedDataSource = new EncodedDataSource("image/jpeg", pictureInfo.getImageFile().getName(), byteArrayOutputStream);
                    scaledPictureMimeBodyPart = new MimeBodyPart();
                    scaledPictureMimeBodyPart.setDataHandler( new DataHandler( encodedDataSource ) );
                    scaledPictureMimeBodyPart.setFileName(pictureInfo.getImageFile().getName());
                    LOGGER.log(Level.INFO, "{0}{1}", new Object[]{Settings.getJpoResources().getString("EmailerAdding"), pictureInfo.getImageFile()});
                    mp.addBodyPart(scaledPictureMimeBodyPart);
                }

                if ( sendOriginal ) {
                    // create the message part for the original image
                    originalPictureMimeBodyPart = new MimeBodyPart();
                    final var highresFile = pictureInfo.getImageFile();
                    // attach the file to the message
                    final var dataSource = new FileDataSource( highresFile );
                    originalPictureMimeBodyPart.setDataHandler( new DataHandler( dataSource ) );
                    originalPictureMimeBodyPart.setFileName( pictureInfo.getImageFile().getName() );
                    // create the Multipart and add its parts to it
                    LOGGER.log(Level.INFO, "{0}{1}", new Object[]{Settings.getJpoResources().getString("EmailerAdding"), pictureInfo.getImageFile()});
                    mp.addBodyPart(originalPictureMimeBodyPart);
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
        final var props = System.getProperties();
        props.setProperty("mail.smtp.host", Settings.getEmailServer());
        props.setProperty("mail.smtp.port", Settings.getEmailPort());
        //props.put( "mail.debug", "true" );
        props.setProperty("mail.smtp.socketFactory.port", Settings.getEmailPort());
        Session session = Session.getDefaultInstance(props, null);
        //session.setDebug( true );

        // Send message
        if (interrupted) {
            LOGGER.info("Message not sent due to user clicking cancel.");
        } else {
            try {
                publish(Settings.getJpoResources().getString("EmailerSending"));
                final MimeMessage msg = buildMessage(session);
                Objects.requireNonNull(msg, "msg must not be null");
                Transport.send(msg);
                publish(Settings.getJpoResources().getString("EmailerSent"));
            } catch (final MessagingException x) {
                LOGGER.severe(x.getMessage());
            }
        }
    }

    /**
     * method that sends the email
     */
    private void sendEmailAuth() {
        final Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", Settings.getEmailServer());
        props.setProperty("mail.smtp.port", Settings.getEmailPort());
        //props.setProperty( "mail.debug", "true" );
        props.setProperty("mail.smtp.socketFactory.port", Settings.getEmailPort());
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", Settings.getEmailPort());
        props.setProperty("mail.smtp.starttls.enable", "true");

        final var session = Session.getDefaultInstance(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Settings.getEmailUser(), Settings.getEmailPassword());
            }
        });
        //session.setDebug( true );

        // Send message
        if ( interrupted ) {
            LOGGER.info("Message not sent due to user clicking cancel.");
        } else {
            try {
                publish(Settings.getJpoResources().getString("EmailerSending"));
                final MimeMessage msg = buildMessage(session);
                Objects.requireNonNull(msg, "msg must not be null");
                Transport.send(msg);
                publish(Settings.getJpoResources().getString("EmailerSent"));
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
        final var props = System.getProperties();
        props.setProperty("mail.smtp.host", Settings.getEmailServer());
        props.setProperty("mail.smtp.port", Settings.getEmailPort());
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.port", Settings.getEmailPort());
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");

        final Session session = Session.getDefaultInstance(props,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Settings.getEmailUser(), Settings.getEmailPassword());
                    }
                });
        //session.setDebug( true );
        if ( interrupted ) {
            LOGGER.info("Message not sent due to user clicking cancel.");
        } else {
            try {
                publish(Settings.getJpoResources().getString("EmailerSending"));
                final var msg = buildMessage(session);
                Objects.requireNonNull(msg, "msg must not be null");
                Transport.send(msg);
                publish(Settings.getJpoResources().getString("EmailerSent"));
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

        EncodedDataSource(final String contentType, final String filename,
                          final ByteArrayOutputStream baos) {
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
