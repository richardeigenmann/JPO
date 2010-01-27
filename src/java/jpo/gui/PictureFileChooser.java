package jpo.gui;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import jpo.dataModel.Settings;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import jpo.dataModel.Tools;


/*
PictureFileChooser.java:  a controller that brings up a filechooser and then adds the pictures


Copyright (C) 2002, 2010  Richard Eigenmann.
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
 *   This controller class brings up a Filechooser which allows the user to
 *   select pictures and directories. If the user clicks OK the pictures and
 *   subdirectories are added to the previously indicated group node.
 */
public class PictureFileChooser
        implements PropertyChangeListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureFileChooser.class.getName() );

    /**
     * We use the java filechooser and customise it.
     */
    private final JFileChooser jFileChooser = new JFileChooser();


    /**
     *  Construct the PictureFileChooser and show it.
     *
     *  @param  startNode   The node to which the selected pictures are to be added.
     *                      It must be a GroupInfo Node.
     */
    public PictureFileChooser( final SortableDefaultMutableTreeNode startNode ) {
        Tools.checkEDT();

        if ( !( startNode.getUserObject() instanceof GroupInfo ) ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "notGroupInfo" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        initComponents();

        if ( jFileChooser.showOpenDialog( Settings.anchorFrame ) == JFileChooser.APPROVE_OPTION ) {
            final File[] chosenFiles = jFileChooser.getSelectedFiles();
            Settings.memorizeCopyLocation( jFileChooser.getCurrentDirectory().getPath() );



            Settings.showThumbOnFileChooser = showThumbnailJCheckBox.isSelected();


            PictureAdder pas = new PictureAdder( startNode, chosenFiles, newOnlyJCheckBox.isSelected(), recurseJCheckBox.isSelected(), retainDirectoriesJCheckBox.isSelected(), categoryJScrollPane.getSelectedCategories() );
            // TODO: Why do these 2 statements have to be executed? Things change in the model so it should find out itself, no?
            pas.execute();
            //Jpo.positionToNode( startNode );
            //startNode.refreshThumbnail();

            //logger.info( "Before Execute" );
            /*int added = 0;
            try {
            added = pas.get();
            } catch ( InterruptedException ex ) {
            Logger.getLogger( PictureFileChooser.class.getName() ).log( Level.SEVERE, null, ex );
            } catch ( ExecutionException ex ) {
            Logger.getLogger( PictureFileChooser.class.getName() ).log( Level.SEVERE, null, ex );
            }
            logger.info( String.format( "After Execute: %d", added ) );*/

            /*       Runnable r = new Runnable() {

            @Override
            public void run() {
            //SortableDefaultMutableTreeNode displayNode = PictureAdder.addPictures( startNode, chosenFiles, newOnlyJCheckBox.isSelected(), recurseJCheckBox.isSelected(), retainDirectoriesJCheckBox.isSelected(), categoryJScrollPane.getSelectedCategories() );
            //Jpo.positionToNode( displayNode );
            //displayNode.refreshThumbnail();
            
            }
            };

            SwingUtilities.invokeLater( r );*/
        }
    }


    /**
     * This method is invoked from the FileChooser and creates the thumbnail.
     *
     * See <a href="http://java.sun.com/developer/JDCTechTips/index.html">Core Java Technologies Tech Tips</a> for the March 16 2004 issue on
     * Preview panels in the JFileChooser.
     * @param changeEvent The event from the FileChooser that changed
     */
    public void propertyChange( PropertyChangeEvent changeEvent ) {
        String changeName = changeEvent.getPropertyName();
        if ( changeName.equals( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY ) ) {
            File file = (File) changeEvent.getNewValue();
            if ( ( file != null ) && ( showThumbnailJCheckBox.isSelected() ) ) {
                ImageIcon icon = new ImageIcon( file.getPath() );
                if ( icon.getIconWidth() > OPTIONS_PANEL_DIMENSION.getWidth() ) {
                    icon = new ImageIcon( icon.getImage().getScaledInstance( (int) OPTIONS_PANEL_DIMENSION.getWidth(), -1,
                            Image.SCALE_DEFAULT ) );
                    if ( icon.getIconHeight() > OPTIONS_PANEL_DIMENSION.getHeight() ) {
                        icon = new ImageIcon( icon.getImage().getScaledInstance( -1, (int) OPTIONS_PANEL_DIMENSION.getHeight(),
                                Image.SCALE_DEFAULT ) );
                    }
                }
                final ImageIcon setIcon = icon;
                Tools.checkEDT();
                thumbnailJLabel.setIcon( setIcon );
            }
        }
    }

    /**
     *  Checkbox that allows the user to specify that pictures already
     *  in the collection should be ignored.
     */
    private JCheckBox showThumbnailJCheckBox = new JCheckBox( Settings.jpoResources.getString( "showThumbnailJCheckBox" ) );

    /**
     *  preferred size of accessory panel
     */
    private static final Dimension OPTIONS_PANEL_DIMENSION = new Dimension( 200, 180 );

    /**
     * Checkbox that allows the user to specifiy whether pictures in the subdirectories should be added or not.
     */
    private final JCheckBox recurseJCheckBox = new JCheckBox( Settings.jpoResources.getString( "recurseJCheckBox" ) );

    /**
     *
     *  Checkbox that allows the user to specify that pictures already in the collection should be ignored.
     */
    private final JCheckBox newOnlyJCheckBox = new JCheckBox( Settings.jpoResources.getString( "newOnlyJCheckBox" ) );

    /**
     * Checkbox that allows the user to specifiy whether directory structures should be retained
     */
    final JCheckBox retainDirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "retainDirectoriesJCheckBox" ) );

    /**
     *  This component shows the thumbnail. It is a JLabel as we can thus use the
     *  ImageIcon to display the pciture.
     */
    private JLabel thumbnailJLabel = new JLabel();

    /**
     * Allow the user to choose categories
     */
    private final CategoryJScrollPane categoryJScrollPane = new CategoryJScrollPane();


    /**
     * Creates the GUI components for the PictureFileChooser controller
     */
    private void initComponents() {
        recurseJCheckBox.setSelected( true );
        newOnlyJCheckBox.setSelected( true );
        showThumbnailJCheckBox.setSelected( Settings.showThumbOnFileChooser );
        retainDirectoriesJCheckBox.setSelected( true );

        JPanel optionsJPanel = new JPanel();
        optionsJPanel.setLayout( new BoxLayout( optionsJPanel, BoxLayout.Y_AXIS ) );
        optionsJPanel.add( recurseJCheckBox, BorderLayout.WEST );
        optionsJPanel.add( newOnlyJCheckBox, BorderLayout.WEST );
        optionsJPanel.add( showThumbnailJCheckBox, BorderLayout.WEST );
        optionsJPanel.add( retainDirectoriesJCheckBox, BorderLayout.WEST );
        optionsJPanel.setPreferredSize( OPTIONS_PANEL_DIMENSION );
        thumbnailJLabel.setPreferredSize( OPTIONS_PANEL_DIMENSION );

        categoryJScrollPane.loadCategories();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
        tabbedPane.add( Settings.jpoResources.getString( "pictureAdderOptionsTab" ), optionsJPanel );
        tabbedPane.add( Settings.jpoResources.getString( "pictureAdderThumbnailTab" ), thumbnailJLabel );
        tabbedPane.add( Settings.jpoResources.getString( "pictureAdderCategoryTab" ), categoryJScrollPane );

        jFileChooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
        jFileChooser.setMultiSelectionEnabled( true );
        jFileChooser.setApproveButtonText( Settings.jpoResources.getString( "fileChooserAddButtonLabel" ) );
        jFileChooser.setDialogTitle( Settings.jpoResources.getString( "PictureAdderDialogTitle" ) );
        jFileChooser.setAccessory( tabbedPane );
        jFileChooser.setCurrentDirectory( Settings.getMostRecentCopyLocation() );
        jFileChooser.addPropertyChangeListener( this );
    }
}
