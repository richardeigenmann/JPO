package org.jpo.testground;

import org.jpo.gui.swing.TristateCheckBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TristateCheckBoxTest {

    @Test
    void getNextStateForPureCheckbox() {
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNSELECT_ALL, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.TCheckBoxInitialState.SELECTED));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.UNSELECT_ALL, TristateCheckBox.TCheckBoxInitialState.SELECTED));

        assertEquals(TristateCheckBox.TCheckBoxChosenState.SELECT_ALL, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.TCheckBoxInitialState.UNSELECTED));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.SELECT_ALL, TristateCheckBox.TCheckBoxInitialState.UNSELECTED));
    }

    @Test
    void getNextStateForMixedCheckbox() {
        assertEquals(TristateCheckBox.TCheckBoxChosenState.SELECT_ALL, TristateCheckBox.getNextStateForMixedCheckbox(TristateCheckBox.TCheckBoxChosenState.UNCHANGED));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNSELECT_ALL, TristateCheckBox.getNextStateForMixedCheckbox(TristateCheckBox.TCheckBoxChosenState.SELECT_ALL));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.getNextStateForMixedCheckbox(TristateCheckBox.TCheckBoxChosenState.UNSELECT_ALL));
    }
}