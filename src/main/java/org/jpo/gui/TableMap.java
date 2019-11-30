package org.jpo.gui;

/*
 * In a chain of data manipulators some behaviour is common. TableMap provides
 * most of this behavour and can be subclassed by filters that only need to
 * override a handful of specific methods. TableMap implements TableModel by
 * routing all requests to its model, and TableModelListener by routing all
 * events to its listeners. Inserting a TableMap which has not been subclassed
 * into a chain of table filters should have no effect.
 *
 * @version 1.4 12/17/97
 * @author Philip Milne
 */
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Richard Eigenmann
 */
public class TableMap extends AbstractTableModel
        implements TableModelListener {

    protected TableModel model;

    public TableModel getModel() {
        return model;
    }

    public void setModel( TableModel model ) {
        this.model = model;
        model.addTableModelListener( this );
    }

    /**
     * By default, implement TableModel by forwarding all messages to the model.
     * @param aRow a row
     * @param aColumn a column
     * @return the value
     */
    @Override
    public Object getValueAt( int aRow, int aColumn ) {
        return model.getValueAt( aRow, aColumn );
    }

    /**
     * Set cell value
     * @param aValue a value
     * @param aRow a row
     * @param aColumn a column
     */
    @Override
    public void setValueAt( Object aValue, int aRow, int aColumn ) {
        model.setValueAt( aValue, aRow, aColumn );
    }

    @Override
    public int getRowCount() {
        return ( model == null ) ? 0 : model.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return ( model == null ) ? 0 : model.getColumnCount();
    }

    @Override
    public String getColumnName( int aColumn ) {
        return model.getColumnName( aColumn );
    }

    @Override
    public Class getColumnClass( int aColumn ) {
        return model.getColumnClass( aColumn );
    }

    @Override
    public boolean isCellEditable( int row, int column ) {
        return model.isCellEditable( row, column );
    }

    /**
     * Implementation of the TableModelListener interface, By default forward
     * all events to all the listeners.
     * @param e event
     */
    @Override
    public void tableChanged( TableModelEvent e ) {
        fireTableChanged( e );
    }
}
