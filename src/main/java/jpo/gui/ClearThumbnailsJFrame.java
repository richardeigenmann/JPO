package jpo.gui;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import jpo.dataModel.Settings;
import org.apache.commons.lang3.StringUtils;

/*
 Copyright (C) 2017  Richard Eigenmann.
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
 * GUI to remove old thumbnails that have been discovered when loading a
 * collection
 *
 * @author Richard Eigenmann
 */
public class ClearThumbnailsJFrame extends javax.swing.JFrame {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ClearThumbnailsJFrame.class.getName() );

    /**
     * Creates new form ClearThumbnailsJFrame
     *
     * @param lowresUrls The lowres Urls
     */
    public ClearThumbnailsJFrame( StringBuilder lowresUrls ) {
        initComponents();
        setLocationRelativeTo( Settings.anchorFrame );
        this.lowresUrls.setText( lowresUrls.toString() );
        stopButton.setVisible( false );
        closeButton.setVisible( false );
        setVisible( true );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lowresUrls = new javax.swing.JTextArea();
        closeButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        ignoreButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("JPO now has an improved cache for thumbnail pictures. The old thumbnail images should be removed.");
        jTextArea1.setFocusable(false);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Remove Old Thumbnails");

        jLabel2.setText("You will only be asked once!");

        lowresUrls.setColumns(20);
        lowresUrls.setRows(5);
        jScrollPane2.setViewportView(lowresUrls);

        closeButton.setText("Close");
        closeButton.addActionListener(this::closeButtonActionPerformed);

        removeButton.setText("Remove");
        removeButton.addActionListener(this::removeButtonActionPerformed);

        stopButton.setText("Stop");
        stopButton.addActionListener(this::stopButtonActionPerformed);

        ignoreButton.setText("Ignore");
        ignoreButton.addActionListener(this::ignoreButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ignoreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton))
                    .addComponent(jScrollPane2)
                    .addComponent(jTextArea1))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(325, 325, 325))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(closeButton)
                    .addComponent(removeButton)
                    .addComponent(stopButton)
                    .addComponent(ignoreButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible( false );
        dispose();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        stopButton.setVisible( true );
        removeButton.setVisible( false );
        ignoreButton.setVisible( false );
        thumbnailRemover.execute();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        thumbnailRemover.cancel( true );
    }//GEN-LAST:event_stopButtonActionPerformed

    private void ignoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreButtonActionPerformed
        setVisible( false );
        dispose();
    }//GEN-LAST:event_ignoreButtonActionPerformed

    private final ThumbnailRemover thumbnailRemover = new ThumbnailRemover();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton ignoreButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea lowresUrls;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables

    private class ThumbnailRemover extends SwingWorker<Void, String> {

        private boolean firsttime = true;

        /**
         * Reads each line from the textarea and deletes the thumbnail file if
         * it exists and is writable. After the deletion the parent directory is
         * deleted too if it is empty and writable.
         *
         * @return nothing.
         */
        @Override
        protected Void doInBackground() {
            publish( "Log:\n" );
            String[] lines = lowresUrls.getText().split( System.getProperty( "line.separator" ) );
            for ( String line : lines ) {
                if ( isCancelled() ) {
                    publish( "Removal cancelled." );
                    break;
                }

                line = StringUtils.chomp( line );
                if ( "".equals( line ) ) {
                    continue;
                }

                URI uri;
                try {
                    uri = new URI( line );
                } catch ( URISyntaxException ex ) {
                    LOGGER.severe( ex.getLocalizedMessage() );
                    publish( line + "   doesn't parse to a file --> nothing to delete --> OK\n" );
                    continue;
                }

                File thumbnail = new File( uri );

                if ( !thumbnail.exists() ) {
                    publish( line + "   doesn't exist. --> nothing to delete --> OK\n" );
                } else if ( !thumbnail.canWrite() ) {
                    publish( line + "   isn't modifiable --> Can't delete --> you have to delete this file yourself\n" );
                } else {
                    if ( thumbnail.delete() ) {
                        publish( line + "   successfully deleted.\n" );
                    } else {
                        publish( line + "   failed to delete --> you have to delete this file yourself\n" );
                    }
                }

                // check if the parent directory is empty and writable and then delete it
                File parentDirectory = thumbnail.getParentFile();
                if ( parentDirectory != null && parentDirectory.canWrite() && parentDirectory.list().length == 0 ) {
                    if ( parentDirectory.delete() ) {
                        publish( String.format( "Parent directory %s successfully deleted%n", parentDirectory.toString() ) );
                    } else {
                        publish( String.format( "Parent directory %s failed to delete --> you have to delete this directory yourself%n", parentDirectory.toString() ) );
                    }
                }

            }
            return null;
        }

        @Override
        protected void done() {
            stopButton.setVisible( false );
            closeButton.setVisible( true );
            if ( !isCancelled() ) {
                publish( "\nRemoval complete.\n" );
            }
        }

        @Override
        protected void process( List<String> chunks ) {
            if ( firsttime ) {
                lowresUrls.setText( "" );
                firsttime = false;
            }
            chunks.stream().forEach( ( chunk ) -> {
                lowresUrls.append( chunk );
            } );
        }

    }

}
