package org.jpo.testground;

import org.jpo.gui.swing.TristateCheckBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TristateCheckBoxTest {

    @Test
    void getNextStateForPureCheckbox() {
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNSELECT, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.TCheckBoxInitialState.SELECTED));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.UNSELECT, TristateCheckBox.TCheckBoxInitialState.SELECTED));

        assertEquals(TristateCheckBox.TCheckBoxChosenState.SELECT, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.TCheckBoxInitialState.UNSELECTED));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.getNextStateForPureCheckbox(TristateCheckBox.TCheckBoxChosenState.SELECT, TristateCheckBox.TCheckBoxInitialState.UNSELECTED));
    }

    @Test
    void getNextStateForMixedCheckbox() {
        assertEquals(TristateCheckBox.TCheckBoxChosenState.SELECT, TristateCheckBox.getNextStateForMixedCheckbox(TristateCheckBox.TCheckBoxChosenState.UNCHANGED));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNSELECT, TristateCheckBox.getNextStateForMixedCheckbox(TristateCheckBox.TCheckBoxChosenState.SELECT));
        assertEquals(TristateCheckBox.TCheckBoxChosenState.UNCHANGED, TristateCheckBox.getNextStateForMixedCheckbox(TristateCheckBox.TCheckBoxChosenState.UNSELECT));
    }
}