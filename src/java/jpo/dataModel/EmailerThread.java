package jpo.dataModel;

import jpo.gui.ScalablePicture;
import java.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.*;

/*
EmailerThread.java:  class that sends the emails

Copyright (C) 2006 - 2009  Richard Eigenmann.
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
 *  This thread sends the emails.
 */
public class EmailerThread implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( EmailerThread.class.getName() );

    /**
     *   Frame to show what the thread is doing.
     */
    private JFrame progressFrame;

    /**
     *  Lablel to show what is being processed.
     */
    private JLabel progressLabel;

    /**
     *  Progress Indicator.
     */
    private JProgressBar progBar;

    /**
     *  Cancel Button.
     */
    private JButton cancelButton;

    /**
     *   Variable that signals to the thread to stop immediately.
     */
    public boolean interrupted = false;

    private Object[] emailSelected;

    private InternetAddress senderAddress;

    private InternetAddress destinationAddress;

    private String subjectLine;

    private String bodyText;

    private boolean scaleImages;

    private Dimension scaleSize;

    private boolean sendOriginal;


    /**
     *  Creates and starts a Thread that writes the picture nodes from the specified
     *  startNode to the target directory.
     *
     *
     */
    public EmailerThread( Object[] emailSelected,
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
        Thread t = new Thread( this );
        t.start();
    }


    /**
     *  Method that is invoked by the thread to do things asynchroneousely.
     */
    public void run() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets( 4, 4, 4, 4 );

        JPanel progPanel = new JPanel();
        progPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        progPanel.setLayout( new GridBagLayout() );


        progressLabel = new JLabel();
        progressLabel.setPreferredSize( new Dimension( 400, 20 ) );
        progressLabel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        progPanel.add( progressLabel, c );

        progBar = new JProgressBar( 0, emailSelected.length );
        progBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
        progBar.setStringPainted( true );
        progBar.setPreferredSize( new Dimension( 140, 20 ) );
        progBar.setMaximumSize( new Dimension( 240, 20 ) );
        progBar.setMinimumSize( new Dimension( 140, 20 ) );
        progBar.setValue( 0 );
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        progPanel.add( progBar, c );

        cancelButton = new JButton( "Cancel" );
        cancelButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                progressLabel.setText( Settings.jpoResources.getString( "htmlDistillerInterrupt" ) );
                interrupted = true;
            }
        } );
        cancelButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelButton.setMinimumSize( Settings.defaultButtonDimension );

        c.gridx++;
        c.anchor = GridBagConstraints.EAST;
        progPanel.add( cancelButton, c );

        progressFrame = new JFrame( Settings.jpoResources.getString( "HtmlDistillerThreadTitle" ) );
        progressFrame.getContentPane().add( progPanel );
        progressFrame.pack();
        progressFrame.setVisible( true );
        progressFrame.setLocationRelativeTo( Settings.anchorFrame );

        sendEmail();

        progressFrame.dispose();

    }


    /**
     *  method that sends the email
     */
    private void sendEmail() {


        // Get system properties
        Properties props = System.getProperties();

        // Setup mail server
        props.setProperty( "mail.smtp.host", Settings.emailServer );


        // Get session
        Session session = Session.getDefaultInstance( props, null );

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

            URL highresURL;
            PictureInfo pi;
            DataSource ds;
            ByteArrayOutputStream baos;
            EncodedDataSource encds;
            for ( int i = 0; ( i < emailSelected.length ) && ( !interrupted ); i++ ) {
                progBar.setValue( progBar.getValue() + 1 );
                progBar.setString( Integer.toString( progBar.getValue() ) + "/" + Integer.toString( progBar.getMaximum() ) );
                pictureDescriptionMimeBodyPart = new MimeBodyPart();
                pi = (PictureInfo) ( (SortableDefaultMutableTreeNode) emailSelected[i] ).getUserObject();

                pictureDescriptionMimeBodyPart.setText( pi.getDescription(), "iso-8859-1" );
                mp.addBodyPart( pictureDescriptionMimeBodyPart );

                if ( scaleImages ) {
                    progressLabel.setText( Settings.jpoResources.getString( "EmailerLoading" ) + pi.getHighresFilename() );
                    scalablePicture.loadPictureImd( pi.getHighresURLOrNull(), pi.getRotation() );
                    progressLabel.setText( Settings.jpoResources.getString( "EmailerScaling" ) + pi.getHighresFilename() );
                    scalablePicture.scalePicture();
                    baos = new ByteArrayOutputStream();
                    progressLabel.setText( Settings.jpoResources.getString( "EmailerWriting" ) + pi.getHighresFilename() );
                    scalablePicture.writeScaledJpg( baos );
                    encds = new EncodedDataSource( "image/jpeg", "filename.jpg", baos );
                    scaledPictureMimeBodyPart = new MimeBodyPart();
                    scaledPictureMimeBodyPart.setDataHandler( new DataHandler( encds ) );
                    scaledPictureMimeBodyPart.setFileName( pi.getHighresFilename() );
                    progressLabel.setText( Settings.jpoResources.getString( "EmailerAdding" ) + pi.getHighresFilename() );
                    mp.addBodyPart( scaledPictureMimeBodyPart );
                }


                if ( sendOriginal ) {
                    // create the message part fro the original image
                    originalPictureMimeBodyPart = new MimeBodyPart();
                    highresURL = pi.getHighresURLOrNull();
                    // attach the file to the message
                    ds = new URLDataSource( highresURL );
                    originalPictureMimeBodyPart.setDataHandler( new DataHandler( ds ) );
                    originalPictureMimeBodyPart.setFileName( pi.getHighresFilename() );
                    // create the Multipart and add its parts to it
                    progressLabel.setText( Settings.jpoResources.getString( "EmailerAdding" ) + pi.getHighresFilename() );
                    mp.addBodyPart( originalPictureMimeBodyPart );
                }

            }
            // add the Multipart to the message
            message.setContent( mp );


        } catch ( MessagingException x ) {
            logger.info( "EmailerJFrame trapped a MessagingException while preparing the message: " + x.getMessage() );
            return;
        }

        // Send message
        if ( interrupted ) {
            logger.info( "EmailerThread: message not sent due to user clicking cancel." );
        } else {
            try {
                progressLabel.setText( "Connecting to Mail Server" );
                //Transport.send( message );
                Transport transport = session.getTransport( "smtp" );
                transport.connect( Settings.emailServer, Settings.emailUser, Settings.emailPassword );
                progressLabel.setText( Settings.jpoResources.getString( "EmailerSending" ) );
                Transport.send( message );
                progressLabel.setText( Settings.jpoResources.getString( "EmailerSent" ) );
            } catch ( MessagingException x ) {
                logger.info( "EmailerJFrame trapped a MessagingException while sending the message: " + x.getMessage() );
                x.printStackTrace();
                JOptionPane.showMessageDialog( Settings.anchorFrame,
                        Settings.jpoResources.getString( "emailSendError" ) + x.getMessage(),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                return;
            }

            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "emailOK" ),
                    Settings.jpoResources.getString( "genericOKText" ),
                    JOptionPane.INFORMATION_MESSAGE );
        }
    }

    /**
     *  A class that somehow helps with the emailing
     */
    private class EncodedDataSource implements DataSource {

        EncodedDataSource( String contentType, String filename, ByteArrayOutputStream baos ) {
            this.contentType = contentType;
            this.filename = filename;
            this.baos = baos;
        }

        String contentType;

        String filename;

        ByteArrayOutputStream baos;


        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream( baos.toByteArray() );
        }


        public OutputStream getOutputStream() throws IOException {
            return null;//new OutputStream();
        }


        public String getContentType() {
            return contentType;
        }


        public String getName() {
            return filename;
        }
    }
}
