package org.jpo.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @see <a href="https://stackoverflow.com/a/26749506/804766>https://stackoverflow.com/a/26749506/804766</a>
 * Copyright: https://creativecommons.org/licenses/by-sa/3.0/
 */
public class TristateCheckBox extends JCheckBox implements Icon, ActionListener {

    public static final String SELECTION_STATE = "selectionState";
    public static final String INITIAL_STATE = "initialState";

    public enum TCheckBoxInitialState {SELECTED, UNSELECTED, MIXED}

    public enum TCheckBoxChosenState {SELECT_ALL, UNSELECT_ALL, UNCHANGED}

    public TristateCheckBox(final String text, final TCheckBoxInitialState initialState) {
        super(text, initialState == TCheckBoxInitialState.SELECTED);
        putClientProperty(INITIAL_STATE, initialState);
        setSelectionState(TCheckBoxChosenState.UNCHANGED);
        setIcon(this);
        addActionListener(this);
    }

    public TCheckBoxChosenState getSelectionState() {
        return (TCheckBoxChosenState) getClientProperty(SELECTION_STATE);
    }

    public TCheckBoxInitialState getInitialState() {
        return (TCheckBoxInitialState) getClientProperty(INITIAL_STATE);
    }

    public void setSelectionState(final TCheckBoxChosenState state) {
        putClientProperty(SELECTION_STATE, state);
        switch (state) {
            case SELECT_ALL:
                super.setSelected(true);
                break;
            case UNSELECT_ALL:
                super.setSelected(false);
                break;
            default:
                super.setSelected(getInitialState() == TCheckBoxInitialState.SELECTED);
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

        if ((getSelectionState() == TCheckBoxChosenState.UNCHANGED) && (getInitialState() == TCheckBoxInitialState.MIXED)) {
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
        final var tcb = (TristateCheckBox) e.getSource();
        final var oldState = (TCheckBoxChosenState) tcb.getClientProperty(SELECTION_STATE);
        if (((TCheckBoxInitialState) tcb.getClientProperty(INITIAL_STATE)) == TCheckBoxInitialState.MIXED) {
            setSelectionState(getNextStateForMixedCheckbox(oldState));
        } else {
            setSelectionState(getNextStateForPureCheckbox(oldState, tcb.getInitialState()));
        }
    }

    public static TCheckBoxChosenState getNextStateForPureCheckbox(final TCheckBoxChosenState oldState, final TCheckBoxInitialState initialState) {
        if (oldState == TCheckBoxChosenState.UNCHANGED) {
            return initialState == TCheckBoxInitialState.SELECTED ? TCheckBoxChosenState.UNSELECT_ALL : TCheckBoxChosenState.SELECT_ALL;
        } else {
            return TCheckBoxChosenState.UNCHANGED;
        }
    }

    public static TCheckBoxChosenState getNextStateForMixedCheckbox(final TCheckBoxChosenState oldState) {
        switch (oldState) {
            case UNCHANGED:
                return TCheckBoxChosenState.SELECT_ALL;
            case SELECT_ALL:
                return TCheckBoxChosenState.UNSELECT_ALL;
            default:
                return TCheckBoxChosenState.UNCHANGED;
        }
    }
}
