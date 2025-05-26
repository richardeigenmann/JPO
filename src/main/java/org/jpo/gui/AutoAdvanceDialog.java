package org.jpo.gui;

import org.jpo.datamodel.*;
import org.jpo.eventbus.ShowAutoAdvanceDialogRequest;
import org.jpo.gui.swing.WholeNumberField;

import javax.swing.*;
import java.awt.*;


/*
 Copyright (C) 2017-2025 Richard Eigenmann, Zürich
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Brings up an Auto Advance Dialog and sends off a new request if successful.
 * Note that if the request's parentComponent is a ComponentMock then the actual
 * modal dialog is skipped.
 *
 * @author Richard Eigenmann
 */
public class AutoAdvanceDialog {

    private final ShowAutoAdvanceDialogRequest request;

    /**
     * Constructs an AutoAdvance dialog for the supplied node in the request
     *
     * @param request the request with the node for the AutoAdvanceDialog
     */
    public AutoAdvanceDialog(final ShowAutoAdvanceDialogRequest request) {
        this.request = request;
        doAutoAdvanceDialog();
    }

    private int showDialog(final Component parentComponent, final Object message) {
        return JOptionPane.showOptionDialog(
                parentComponent,
                message,
                Settings.getJpoResources().getString("autoAdvanceDialogTitle"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);
    }

    /**
     * method that brings up a dialog box and asks the user how he would like
     * auto advance to work
     */
    private void doAutoAdvanceDialog() {
        final var randomAdvanceJRadioButton = new JRadioButton(Settings.getJpoResources().getString("randomAdvanceJRadioButtonLabel"));
        final var sequentialAdvanceJRadioButton = new JRadioButton(Settings.getJpoResources().getString("sequentialAdvanceJRadioButtonLabel"));
        final var advanceButtonGroup = new ButtonGroup();
        advanceButtonGroup.add(randomAdvanceJRadioButton);
        advanceButtonGroup.add(sequentialAdvanceJRadioButton);
        randomAdvanceJRadioButton.setSelected(true);

        final var restrictToGroupJRadioButton = new JRadioButton(Settings.getJpoResources().getString("restrictToGroupJRadioButtonLabel"));
        final var useAllPicturesJRadioButton = new JRadioButton(Settings.getJpoResources().getString("useAllPicturesJRadioButtonLabel"));
        final var cycleButtonGroup = new ButtonGroup();
        cycleButtonGroup.add(restrictToGroupJRadioButton);
        cycleButtonGroup.add(useAllPicturesJRadioButton);
        useAllPicturesJRadioButton.setSelected(true);

        final var timerSecondsJLabel = new JLabel(Settings.getJpoResources().getString("timerSecondsJLabelLabel"));
        final var timerSecondsField = new WholeNumberField(4, 3);
        timerSecondsField.setPreferredSize(new Dimension(50, 20));
        timerSecondsField.setMaximumSize(new Dimension(50, 20));
        final Object[] objects = {randomAdvanceJRadioButton,
                sequentialAdvanceJRadioButton,
                restrictToGroupJRadioButton,
                useAllPicturesJRadioButton,
                timerSecondsJLabel,
                timerSecondsField
        };

        final var parentComponent = request.parentComponent();
        var selectedValue = showDialog(parentComponent, objects);
        if (selectedValue == 0) {
            boolean randomAdvanceSelected = randomAdvanceJRadioButton.isSelected();
            boolean useAllPicturesSelected = useAllPicturesJRadioButton.isSelected();
            int timerSeconds = timerSecondsField.getValue();

            final var navigator = getNavigator(request.currentNode(), randomAdvanceSelected, useAllPicturesSelected);
            request.autoAdvanceTarget().showNode(navigator, 0);
            request.autoAdvanceTarget().startAdvanceTimer(timerSeconds);
        }
    }

    private static NodeNavigatorInterface getNavigator(SortableDefaultMutableTreeNode currentNode, boolean randomAdvanceSelected, boolean useAllPicturesSelected) {
        if (randomAdvanceSelected) {
            if (useAllPicturesSelected) {
                final var rootNode = currentNode.getRoot();
                return new RandomNavigator(
                        rootNode.getChildPictureNodes(true),
                        String.format("Randomised pictures from %s",
                                rootNode));
            } else {
                return new RandomNavigator(
                        currentNode.getParent().getChildPictureNodes(true),
                        String.format("Randomised pictures from %s",
                                (currentNode.getParent()).toString()));
            }
        } else {
            if (useAllPicturesSelected) {
                return new FlatGroupNavigator(currentNode.getRoot());
            } else {
                return new FlatGroupNavigator(currentNode.getParent());
            }
        }
    }


}
