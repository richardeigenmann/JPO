package jpo.gui.swing;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 *  JTableCopyPasteClipboardAdapter enables Copy-Paste Clipboard functionality on JTables.
 *  The clipboard data format used by the adapter is compatible with
 *  the clipboard format used by Excel. This provides for clipboard
 *  interoperability between enabled JTables and Excel.
 *
 *  @see <a href="http:////www.javaworld.com//javatips//jw-javatip77_p.html">http:////www.javaworld.com//javatips//jw-javatip77_p.html</a>
 */
public class JTableCopyPasteClipboardAdapter implements ActionListener {
    //private String rowstring,value;

    private final Clipboard systemClipboard;

    private StringSelection stringSelection;

    private JTable jTable;


    /**
     *   The Excel Adapter is constructed with a
     *   JTable on which it enables Copy-Paste and acts
     *   as a Clipboard listener.
     *
     * @param myJTable tabke
     */
    public JTableCopyPasteClipboardAdapter( JTable myJTable ) {
        jTable = myJTable;
        KeyStroke copy1 = KeyStroke.getKeyStroke( KeyEvent.VK_C, ActionEvent.CTRL_MASK, false );
        KeyStroke copy2 = KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, ActionEvent.CTRL_MASK, false );
        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste1 = KeyStroke.getKeyStroke( KeyEvent.VK_V, ActionEvent.CTRL_MASK, false );
        KeyStroke paste2 = KeyStroke.getKeyStroke( KeyEvent.VK_INSERT, ActionEvent.SHIFT_MASK, false );
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
    public void actionPerformed( ActionEvent event ) {
        if ( event.getActionCommand().compareTo( "Copy" ) == 0 ) {
            StringBuilder sbf = new StringBuilder();

            // Check to ensure we have selected only a contiguous block of
            // cells
            int numcols = jTable.getSelectedColumnCount();
            int numrows = jTable.getSelectedRowCount();
            int[] rowsselected = jTable.getSelectedRows();
            int[] colsselected = jTable.getSelectedColumns();
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
                    sbf.append( jTable.getValueAt( rowsselected[i], colsselected[j] ) );
                    if ( j < numcols - 1 ) {
                        sbf.append( "\t" );
                    }
                }
                sbf.append( "\n" );
            }
            stringSelection = new StringSelection( sbf.toString() );
            //systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents( stringSelection, stringSelection );
        }

        if ( event.getActionCommand().compareTo( "Paste" ) == 0 ) {
            int pasteStartRow = ( jTable.getSelectedRows() )[0];
            int pasteStartCol = ( jTable.getSelectedColumns() )[0];
            int pasteCols = jTable.getSelectedColumnCount();
            int pasteRows = jTable.getSelectedRowCount();

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
                        if ( ( pasteStartRow + i < jTable.getRowCount() ) && ( pasteStartCol + j < jTable.getColumnCount() ) ) {
                            jTable.setValueAt( sourceValues[i % sourceRowCount][j % sourceColumnCount], pasteStartRow + i, pasteStartCol + j );
                        }
                        TableModelEvent tme = new TableModelEvent( jTable.getModel(), pasteStartRow + i, pasteStartRow + i, pasteStartCol + j, TableModelEvent.UPDATE );
                        ( (AbstractTableModel) jTable.getModel() ).fireTableChanged( tme );
                    }
                }
            } catch ( UnsupportedFlavorException | IOException ex ) {
            }
        }
    }
}
