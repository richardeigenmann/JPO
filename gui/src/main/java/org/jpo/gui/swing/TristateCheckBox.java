package org.jpo.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 Copyright (C) 2023-2024 Richard Eigenmann.
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
 * The TristateCheckBox is a JCheckBox with a third state.
 * It has an initial state of SELECTED, UNSELECTED or MIXED which it shows with a tick, no tick or a square in the icon.
 * The click logic depends on whether there was a mixed initial state or not.
 * If there was no mixed state then the user can only go from UNCHANGED to SELECT/UNSELECT and back to UNCHANGED.
 * If there was a mixed state then the user can go from UNCHANGED to SELECT to UNSELECT and back to UNCHANGED.
 * <p>
 * Original code from StackOverflow customised by Richard Eigenmann
 *
 * @author Richard Eigenmann
 * @see <a href="https://stackoverflow.com/a/26749506/804766">https://stackoverflow.com/a/26749506/804766</a>
 * Copyright: https://creativecommons.org/licenses/by-sa/3.0/
 */
public class TristateCheckBox extends JCheckBox implements Icon, ActionListener {

    public static final String SELECTION = "selection";
    public static final String INITIAL_STATE = "initialState";

    public enum TCheckBoxInitialState {SELECTED, UNSELECTED, MIXED}

    public enum TCheckBoxChosenState {SELECT, UNSELECT, UNCHANGED}

    public TristateCheckBox(final String text, final TCheckBoxInitialState initialState) {
        super(text, initialState == TCheckBoxInitialState.SELECTED);
        putClientProperty(INITIAL_STATE, initialState);
        setSelectionState(TCheckBoxChosenState.UNCHANGED);
        setIcon(this);
        addActionListener(this);
    }

    public TCheckBoxChosenState getSelection() {
        return (TCheckBoxChosenState) getClientProperty(SELECTION);
    }

    public TCheckBoxInitialState getInitialState() {
        return (TCheckBoxInitialState) getClientProperty(INITIAL_STATE);
    }

    public void setSelectionState(final TCheckBoxChosenState state) {
        putClientProperty(SELECTION, state);
        switch (state) {
            case SELECT -> super.setSelected(true);
            case UNSELECT -> super.setSelected(false);
            default -> super.setSelected(getInitialState() == TCheckBoxInitialState.SELECTED);
        }

        if (state == TCheckBoxChosenState.UNCHANGED) {
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.BLUE);
        }
    }


    static final Icon icon = UIManager.getIcon("CheckBox.icon");

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        // paints the supercalss checkbox icon
        icon.paintIcon(c, g, x, y);

        if ((getSelection() == TCheckBoxChosenState.UNCHANGED) && (getInitialState() == TCheckBoxInitialState.MIXED)) {
            int w = getIconWidth();
            int h = getIconHeight();
            g.setColor(c.isEnabled() ? new Color(51, 51, 51) : new Color(122, 138, 153));
            g.fillRect(x + 4, y + 4, w - 8, h - 8);
        }
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }

    public void actionPerformed(final ActionEvent e) {
        final var tristateCheckBox = (TristateCheckBox) e.getSource();
        final var oldState = (TCheckBoxChosenState) tristateCheckBox.getClientProperty(SELECTION);
        if (tristateCheckBox.getClientProperty(INITIAL_STATE) == TCheckBoxInitialState.MIXED) {
            setSelectionState(getNextStateForMixedCheckbox(oldState));
        } else {
            setSelectionState(getNextStateForPureCheckbox(oldState, tristateCheckBox.getInitialState()));
        }
    }

    public static TCheckBoxChosenState getNextStateForPureCheckbox(final TCheckBoxChosenState oldState, final TCheckBoxInitialState initialState) {
        if (oldState == TCheckBoxChosenState.UNCHANGED) {
            return initialState == TCheckBoxInitialState.SELECTED ? TCheckBoxChosenState.UNSELECT : TCheckBoxChosenState.SELECT;
        } else {
            return TCheckBoxChosenState.UNCHANGED;
        }
    }

    public static TCheckBoxChosenState getNextStateForMixedCheckbox(final TCheckBoxChosenState oldState) {
        return switch (oldState) {
            case UNCHANGED -> TCheckBoxChosenState.SELECT;
            case SELECT -> TCheckBoxChosenState.UNSELECT;
            default -> TCheckBoxChosenState.UNCHANGED;
        };
    }
}
