package jpo.export;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.NodeStatistics;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.javaprog.ui.wizard.AbstractStep;
import net.miginfocom.swing.MigLayout;

/*
 * Copyright (C) 2012-2017 Richard Eigenmann. Zürich
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 * Performs the login to Google
 *
 * @author Richard Eigenmann
 */
public class PicasaUploaderWizard2Confirm extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    //private static final Logger LOGGER = Logger.getLogger( PicasaUploaderWizard2Confirm.class.getName() );
    /**
     * The link to the values that this panel should change
     */
    private final PicasaUploadRequest myRequest;

    /**
     * Upload the album
     * @param myRequest My request
     */
    public PicasaUploaderWizard2Confirm( PicasaUploadRequest myRequest ) {
        super( "Confirm upload", "Confirm upload" );
        this.myRequest = myRequest;
    }
    private final JTextArea info = new JTextArea();

    /**
     * Creates the GUI widgets
     *
     * @return The component to be shown
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardPanel = new JPanel();
        MigLayout layout = new MigLayout( "wrap 1" );
        wizardPanel.setLayout( layout );

        wizardPanel.add( info );
        SortableDefaultMutableTreeNode node = myRequest.getNode();
        String albumName = ( (GroupInfo) node.getUserObject() ).getGroupName();
        int pics = NodeStatistics.countPictures( node, false );
        info.setText( String.format( "Uploading Album%n%s%nwith %d pictures to Picasa", albumName, pics ) );

        return wizardPanel;
    }

    /**
     * Required but not needed here
     */
    @Override
    public void prepareRendering() {
    }
}
