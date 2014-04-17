package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.JpoEventBus;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.NodeStatistics;
import net.miginfocom.swing.MigLayout;

/*
 ConsolidateGroupJFrame.java:  Controller and Visual to consoliodate
 pictures of a node into a directory.

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * Controller and Visual to consolidate pictures of a node into a directory.
 */
public class ConsolidateGroupJFrame extends JFrame {

    /**
     * The node from which to start the export
     */
    private final SortableDefaultMutableTreeNode startNode;
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ConsolidateGroupJFrame.class.getName() );
    /**
     * Chooser to pick the highres directory
     *
     */
    private final DirectoryChooser highresDirectoryChooser
            = new DirectoryChooser( Settings.jpoResources.getString( "highresTargetDirJTextField" ),
                    DirectoryChooser.DIR_MUST_BE_WRITABLE );

    /**
     * Tickbox that indicates whether pictures or the current group only should
     * be consolidated or whether the subgroups (if any) should be included.
     *
     */
    private final JCheckBox recurseSubgroupsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "RecurseSubgroupsLabel" ) );

    /**
     * Creates a GUI that allows the user to specify into which directory he or
     * she would like images to be moved physically.
     *
     * @param startNode The group node that the user wants the consolidation to
     * be done on.
     */
    public ConsolidateGroupJFrame( SortableDefaultMutableTreeNode startNode ) {
        super( Settings.jpoResources.getString( "ConsolidateGroupJFrameHeading" ) );
        this.startNode = startNode;
        initComponents();
    }

    /**
     * Creates a GUI that allows the user to specify into which directory he or
     * she would like images to be moved physically.
     *
     * @param startNode The group node that the user wants the consolidation to
     * be done on. TODO: make this use MIG Layout
     * @param targetDirectory the target directory
     */
    public ConsolidateGroupJFrame( SortableDefaultMutableTreeNode startNode, File targetDirectory ) {
        this( startNode );
        highresDirectoryChooser.setFile( targetDirectory );
    }

    private void initComponents() {

        Object userObject = startNode.getUserObject();
        if ( !( userObject instanceof GroupInfo ) ) {
            LOGGER.info( String.format( "Node %s is not a group", startNode.toString() ) );
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "ConsolidateFailure" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        setSize( 460, 500 );
        setLocationRelativeTo( Settings.anchorFrame );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        JPanel contentJPanel = new javax.swing.JPanel();
        contentJPanel.setLayout( new MigLayout() );

        JLabel consolidateGroupBlaBlaJLabel = new JLabel( Settings.jpoResources.getString( "ConsolidateGroupBlaBlaLabel" ) );
        contentJPanel.add( consolidateGroupBlaBlaJLabel, "span 2, wrap" );

        JLabel targetDirJLabel = new JLabel( Settings.jpoResources.getString( "genericTargetDirText" ) );
        contentJPanel.add( targetDirJLabel );

        contentJPanel.add( highresDirectoryChooser, "span 2, wrap" );

        recurseSubgroupsJCheckBox.setSelected( true );
        contentJPanel.add( recurseSubgroupsJCheckBox, "span 2, wrap" );

        JPanel buttonJPanel = new JPanel();

        // add the consolidate button
        final JButton consolidateJButton = new JButton( Settings.jpoResources.getString( "ConsolidateButton" ) );
        consolidateJButton.setPreferredSize( new Dimension( 120, 25 ) );
        consolidateJButton.setMinimumSize( Settings.defaultButtonDimension );
        consolidateJButton.setMaximumSize( new Dimension( 120, 25 ) );
        consolidateJButton.setDefaultCapable( true );
        this.getRootPane().setDefaultButton( consolidateJButton );
        buttonJPanel.add( consolidateJButton );

        // add the cancel button
        final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( new Dimension( 120, 25 ) );
        buttonJPanel.add( cancelJButton );
        contentJPanel.add( buttonJPanel, "span 2, wrap" );

        setContentPane( contentJPanel );

        pack();
        setVisible( true );

        consolidateJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                consolidateToDirectory();
                getRid();
            }
        } );
        cancelJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );

    }

    /**
     * method that gets rid of this JFrame
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * method that outputs the selected group to a directory
     */
    private void consolidateToDirectory() {
        Object userObject = startNode.getUserObject();
        if ( !( userObject instanceof GroupInfo ) ) {
            LOGGER.info( String.format( "Node %s is not a group", startNode.toString() ) );
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "ConsolidateFailure" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        File highresDirectory = highresDirectoryChooser.getDirectory();

        if ( !highresDirectory.exists() ) {
            try {
                if ( !highresDirectory.mkdirs() ) {
                    JOptionPane.showMessageDialog(
                            Settings.anchorFrame,
                            String.format( Settings.jpoResources.getString( "ConsolidateCreateDirFailure" ), highresDirectory ),
                            Settings.jpoResources.getString( "genericError" ),
                            JOptionPane.ERROR_MESSAGE );
                    return;
                }
            } catch ( SecurityException e ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        String.format( Settings.jpoResources.getString( "ConsolidateCreateDirFailure" ), highresDirectory ),
                        Settings.jpoResources.getString( "genericError" ),
                        JOptionPane.ERROR_MESSAGE );
                LOGGER.severe( String.format( "SecurityException when creating directory %s. Reason: %s", highresDirectory, e.getMessage() ) );
                return;
            }
        }

        if ( !highresDirectory.canWrite() ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    String.format( Settings.jpoResources.getString( "ConsolidateCantWrite" ), highresDirectory ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );
            return;
        }

        new ConsolidateGroup(
                highresDirectory,
                startNode,
                recurseSubgroupsJCheckBox.isSelected(),
                new ProgressGui( NodeStatistics.countPictures( startNode, recurseSubgroupsJCheckBox.isSelected() ),
                        Settings.jpoResources.getString( "ConsolitdateProgBarTitle" ),
                        Settings.jpoResources.getString( "ConsolitdateProgBarDone" ) ) );
        Settings.memorizeCopyLocation( highresDirectory.toString() );
        JpoEventBus.getInstance().post( new CopyLocationsChangedEvent() );

    }
}
