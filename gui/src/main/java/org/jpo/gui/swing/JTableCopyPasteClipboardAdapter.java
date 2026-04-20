package org.jpo.gui.swing;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.StringTokenizer;

/*
Copyright (C) 2024 Richard Eigenmann.
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
 *  JTableCopyPasteClipboardAdapter enables Copy-Paste Clipboard functionality on JTables.
 *  The clipboard data format used by the adapter is compatible with
 *  the clipboard format used by Excel. This provides for clipboard
 *  interoperability between enabled JTables and Excel.
 *
 *  @see <a href="http:////www.javaworld.com//javatips//jw-javatip77_p.html">http:////www.javaworld.com//javatips//jw-javatip77_p.html</a>
 */
public class JTableCopyPasteClipboardAdapter implements ActionListener {
    private final Clipboard systemClipboard;

    private JTable jTable;


    /**
     *   The Excel Adapter is constructed with a
     *   JTable on which it enables Copy-Paste and acts
     *   as a Clipboard listener.
     *
     * @param myJTable table
     */
    public JTableCopyPasteClipboardAdapter( JTable myJTable ) {
        jTable = myJTable;
        KeyStroke copy1 = KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK, false );
        KeyStroke copy2 = KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, InputEvent.CTRL_DOWN_MASK, false );
        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste1 = KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK, false );
        KeyStroke paste2 = KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, InputEvent.SHIFT_DOWN_MASK, false );
        // Identifying the Paste KeyStroke user can modify this
        //to copy on some other Key combination.
        jTable.registerKeyboardAction( JTableCopyPasteClipboardAdapter.this, "Copy", copy1, JComponent.WHEN_FOCUSED );
        jTable.registerKeyboardAction( JTableCopyPasteClipboardAdapter.this, "Copy", copy2, JComponent.WHEN_FOCUSED );
        jTable.registerKeyboardAction( JTableCopyPasteClipboardAdapter.this, "Paste", paste1, JComponent.WHEN_FOCUSED );
        jTable.registerKeyboardAction( JTableCopyPasteClipboardAdapter.this, "Paste", paste2, JComponent.WHEN_FOCUSED );
        systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }


    /**
     *   Public Accessor methods for the Table on which this adapter acts.
     *
     * @return the JTable
     */
    public JTable getJTable() {
        return jTable;
    }


    /**
     *  Sets the Jtable
     * @param jTable1 the jtable
     */
    public void setJTable( JTable jTable1 ) {
        this.jTable = jTable1;
    }


    /**
     *   This method is activated on the Keystrokes we are listening to
     *   in this implementation. Here it listens for Copy and Paste ActionCommands.
     *   Selections comprising non-adjacent cells result in invalid selection and
     *   then copy action cannot be performed.
     *   Paste is done by aligning the upper left corner of the selection with the
     *   1st element in the current selection of the JTable.
     *
     * @param event event
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (event.getActionCommand().compareTo("Copy") == 0) {
            handleCopyAction();
        }

        if (event.getActionCommand().compareTo("Paste") == 0) {
            handlePasteAction();
        }
    }

    private void handleCopyAction() {
        final StringBuilder sbf = new StringBuilder();

        // Check to ensure we have selected only a contiguous block of
        // cells
        final int numcols = jTable.getSelectedColumnCount();
        final int numrows = jTable.getSelectedRowCount();
        final int[] rowsselected = jTable.getSelectedRows();
        final int[] colsselected = jTable.getSelectedColumns();
        if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] &&
                numrows == rowsselected.length) &&
                (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] &&
                        numcols == colsselected.length))) {

            JOptionPane.showMessageDialog(null,
                    "Invalid Copy Selection",
                    "Invalid Copy Selection",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        for (int i = 0; i < numrows; i++) {
            for (int j = 0; j < numcols; j++) {
                sbf.append(jTable.getValueAt(rowsselected[i], colsselected[j]));
                if (j < numcols - 1) {
                    sbf.append("\t");
                }
            }
            sbf.append("\n");
        }
        final StringSelection stringSelection = new StringSelection(sbf.toString());
        systemClipboard.setContents(stringSelection, stringSelection);
    }

    private void handlePasteAction() {
        final int pasteStartRow = (jTable.getSelectedRows())[0];
        final int pasteStartCol = (jTable.getSelectedColumns())[0];
        int pasteCols = jTable.getSelectedColumnCount();
        int pasteRows = jTable.getSelectedRowCount();

        try {
            final String transferableString =
                    (String) (systemClipboard.getContents(this).getTransferData(DataFlavor.stringFlavor));

            StringTokenizer st1 = new StringTokenizer(transferableString, "\n");
            final int sourceRowCount = st1.countTokens();
            String rowString = st1.nextToken();
            StringTokenizer st2 = new StringTokenizer(rowString, "\t");
            final int sourceColumnCount = st2.countTokens();

            // the underlying assumption is that the transferable is a rectangular array
            String[][] sourceValues = new String[sourceRowCount][sourceColumnCount];


            st1 = new StringTokenizer(transferableString, "\n");
            for (int i = 0; st1.hasMoreTokens(); i++) {
                rowString = st1.nextToken();
                st2 = new StringTokenizer(rowString, "\t");
                for (int j = 0; st2.hasMoreTokens(); j++) {
                    sourceValues[i][j] = st2.nextToken();
                }
            }


            if ((pasteRows == 1) && (pasteCols == 1)) {
                pasteRows = sourceRowCount;
                pasteCols = sourceColumnCount;
            }

            for (int i = 0; i < pasteRows; i++) {
                for (int j = 0; j < pasteCols; j++) {
                    if ((pasteStartRow + i < jTable.getRowCount()) && (pasteStartCol + j < jTable.getColumnCount())) {
                        jTable.setValueAt(sourceValues[i % sourceRowCount][j % sourceColumnCount], pasteStartRow + i, pasteStartCol + j);
                    }
                    TableModelEvent tme = new TableModelEvent(jTable.getModel(), pasteStartRow + i, pasteStartRow + i, pasteStartCol + j, TableModelEvent.UPDATE);
                    ((AbstractTableModel) jTable.getModel()).fireTableChanged(tme);
                }
            }
        } catch (final UnsupportedFlavorException | IOException ex) {
            // Ignore it then
        }

    }
}
