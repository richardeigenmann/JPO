package jpo.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *  ExcelAdapter enables Copy-Paste Clipboard functionality on JTables.
 *  The clipboard data format used by the adapter is compatible with
 *  the clipboard format used by Excel. This provides for clipboard
 *  interoperability between enabled JTables and Excel.
 *
 *  from http://www.javaworld.com/javatips/jw-javatip77_p.html
 */
public class ExcelAdapter implements ActionListener {
    //private String rowstring,value;

    private Clipboard systemClipboard;

    private StringSelection stsel;

    private JTable jTable1;


    /**
     *   The Excel Adapter is constructed with a
     *   JTable on which it enables Copy-Paste and acts
     *   as a Clipboard listener.
     *
     * @param myJTable
     */
    public ExcelAdapter( JTable myJTable ) {
        jTable1 = myJTable;
        KeyStroke copy1 = KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.CTRL_MASK, false );
        KeyStroke copy2 = KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, ActionEvent.CTRL_MASK, false );
        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste1 = KeyStroke.getKeyStroke( KeyEvent.VK_V, ActionEvent.CTRL_MASK, false );
        KeyStroke paste2 = KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, ActionEvent.SHIFT_MASK, false );
        // Identifying the Paste KeyStroke user can modify this
        //to copy on some other Key combination.
        jTable1.registerKeyboardAction( this, "Copy", copy1, JComponent.WHEN_FOCUSED );
        jTable1.registerKeyboardAction( this, "Copy", copy2, JComponent.WHEN_FOCUSED );
        jTable1.registerKeyboardAction( this, "Paste", paste1, JComponent.WHEN_FOCUSED );
        jTable1.registerKeyboardAction( this, "Paste", paste2, JComponent.WHEN_FOCUSED );
        systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }


    /**
     *   Public Accessor methods for the Table on which this adapter acts.
     *
     * @return
     */
    public JTable getJTable() {
        return jTable1;
    }


    /**
     *
     * @param jTable1
     */
    public void setJTable( JTable jTable1 ) {
        this.jTable1 = jTable1;
    }


    /**
     *   This method is activated on the Keystrokes we are listening to
     *   in this implementation. Here it listens for Copy and Paste ActionCommands.
     *   Selections comprising non-adjacent cells result in invalid selection and
     *   then copy action cannot be performed.
     *   Paste is done by aligning the upper left corner of the selection with the
     *   1st element in the current selection of the JTable.
     *
     * @param e
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().compareTo( "Copy" ) == 0 ) {
            StringBuffer sbf = new StringBuffer();

            // Check to ensure we have selected only a contiguous block of
            // cells
            int numcols = jTable1.getSelectedColumnCount();
            int numrows = jTable1.getSelectedRowCount();
            int[] rowsselected = jTable1.getSelectedRows();
            int[] colsselected = jTable1.getSelectedColumns();
            if ( !( ( numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] &&
                    numrows == rowsselected.length ) &&
                    ( numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] &&
                    numcols == colsselected.length ) ) ) {

                JOptionPane.showMessageDialog( null,
                        "Invalid Copy Selection",
                        "Invalid Copy Selection",
                        JOptionPane.ERROR_MESSAGE );
                return;
            }


            for ( int i = 0; i < numrows; i++ ) {
                for ( int j = 0; j < numcols; j++ ) {
                    sbf.append( jTable1.getValueAt( rowsselected[i], colsselected[j] ) );
                    if ( j < numcols - 1 ) {
                        sbf.append( "\t" );
                    }
                }
                sbf.append( "\n" );
            }
            stsel = new StringSelection( sbf.toString() );
            //systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents( stsel, stsel );
        }

        if ( e.getActionCommand().compareTo( "Paste" ) == 0 ) {
            int pasteStartRow = ( jTable1.getSelectedRows() )[0];
            int pasteStartCol = ( jTable1.getSelectedColumns() )[0];
            int pasteCols = jTable1.getSelectedColumnCount();
            int pasteRows = jTable1.getSelectedRowCount();

            try {
                String transferableString =
                        (String) ( systemClipboard.getContents( this ).getTransferData( DataFlavor.stringFlavor ) );

                StringTokenizer st1 = new StringTokenizer( transferableString, "\n" );
                int sourceRowCount = st1.countTokens();
                String rowString = st1.nextToken();
                StringTokenizer st2 = new StringTokenizer( rowString, "\t" );
                int sourceColumnCount = st2.countTokens();

                // the underlying assumption is that the transferable is a rectangular array
                String[][] sourceValues = new String[sourceRowCount][sourceColumnCount];


                st1 = new StringTokenizer( transferableString, "\n" );
                for ( int i = 0; st1.hasMoreTokens(); i++ ) {
                    rowString = st1.nextToken();
                    st2 = new StringTokenizer( rowString, "\t" );
                    for ( int j = 0; st2.hasMoreTokens(); j++ ) {
                        sourceValues[i][j] = st2.nextToken();
                    }
                }


                if ( ( pasteRows == 1 ) && ( pasteCols == 1 ) ) {
                    pasteRows = sourceRowCount;
                    pasteCols = sourceColumnCount;
                }

                for ( int i = 0; i < pasteRows; i++ ) {
                    for ( int j = 0; j < pasteCols; j++ ) {
                        if ( ( pasteStartRow + i < jTable1.getRowCount() ) && ( pasteStartCol + j < jTable1.getColumnCount() ) ) {
                            jTable1.setValueAt( sourceValues[i % sourceRowCount][j % sourceColumnCount], pasteStartRow + i, pasteStartCol + j );
                        }
                        TableModelEvent tme = new TableModelEvent( jTable1.getModel(), pasteStartRow + i, pasteStartRow + i, pasteStartCol + j, TableModelEvent.UPDATE );
                        ( (AbstractTableModel) jTable1.getModel() ).fireTableChanged( tme );
                    }
                }
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }
    }
}
