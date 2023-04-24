package org.jpo.gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel)
 * and itself implements TableModel. TableSorter does not store or copy the data
 * in the TableModel, instead it maintains an array of integers which it keeps
 * the same size as the number of rows in its model. When the model changes it
 * notifies the sorter that something has changed e.g. "rowsAdded" so that its
 * internal array of integers can be reallocated. As requests are made of the
 * sorter (like getValueAt(row, col) it redirects them to its model via the
 * mapping array. That way the TableSorter appears to hold another copy of the
 * table with the rows in a different order. The sorting algorithm used is
 * stable which means that it does not move around rows when its comparison
 * function returns 0 to denote that they are equivalent.
 *
 * @version 1.5 12/17/97
 * @author Philip Milne
 */
public final class TableSorter extends TableMap {

    int[] indexes;
    final transient List<Integer> sortingColumns = new ArrayList<>();
    boolean ascending = true;
    int compares;
    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( TableSorter.class.getName() );

    public TableSorter() {
        indexes = new int[0]; // for consistency
    }

    public TableSorter( TableModel model ) {
        setModel( model );
    }

    @Override
    public void setModel( TableModel model ) {
        super.setModel( model );
        reallocateIndexes();
    }

    public int compareRowsByColumn( int row1, int row2, int column ) {
        final Class<?> type = model.getColumnClass(column);
        final TableModel data = model;

        // Check for nulls.
        final Object o1 = data.getValueAt(row1, column);
        final Object o2 = data.getValueAt(row2, column);

        // If both values are null, return 0.
        if (o1 == null && o2 == null) {
            return 0;
        } else if (o1 == null) { // Define null less than everything.
            return -1;
        } else if (o2 == null) {
            return 1;
        }

        /*
         * We copy all returned values from the getValue call in case
         * an optimised model is reusing one object to return many
         * values.  The Number subclasses in the JDK are immutable and
         * so will not be used in this way but other subclasses of
         * Number might want to do this to save space and avoid
         * unnecessary heap allocation.
         */
        if (type.getSuperclass() == Number.class) {
            final Number n1 = (Number) data.getValueAt(row1, column);
            final double d1 = n1.doubleValue();
            final Number n2 = (Number) data.getValueAt(row2, column);
            final double d2 = n2.doubleValue();

            return Double.compare(d1, d2);
        } else if (type == Date.class) {
            final Date d1 = (Date) data.getValueAt(row1, column);
            final long n1 = d1.getTime();
            final Date d2 = (Date) data.getValueAt(row2, column);
            final long n2 = d2.getTime();

            return Long.compare(n1, n2);
        } else if (type == String.class) {
            final String s1 = (String) data.getValueAt(row1, column);
            final String s2 = (String) data.getValueAt(row2, column);
            final int result = s1.compareTo(s2);

            return Integer.compare(result, 0);
        } else if (type == Boolean.class) {
            final boolean b1 = (Boolean) data.getValueAt(row1, column);
            final boolean b2 = (Boolean) data.getValueAt(row2, column);

            if (b1 == b2) {
                return 0;
            } else if (b1) { // Define false < true
                return 1;
            } else {
                return -1;
            }
        } else {
            final Object v1 = data.getValueAt(row1, column);
            final String s1 = v1.toString();
            final Object v2 = data.getValueAt(row2, column);
            final String s2 = v2.toString();
            final int result = s1.compareTo(s2);

            return Integer.compare(result, 0);
        }
    }

    public int compare(final int row1, final int row2) {
        compares++;
        for (final Integer column : sortingColumns) {
            final int result = compareRowsByColumn(row1, row2, column);
            if (result != 0) {
                return ascending ? result : -result;
            }
        }
        return 0;
    }

    public void reallocateIndexes() {
        final int rowCount = model.getRowCount();

        //Set up a new array of indexes with the right number of elements
        // for the new data model.
        indexes = new int[rowCount];

        // Initialise with the identity mapping.
        for ( int row = 0; row < rowCount; row++ ) {
            indexes[row] = row;
        }
    }

    @Override
    public void tableChanged( TableModelEvent e ) {
        reallocateIndexes();

        super.tableChanged( e );
    }

    public void checkModel() {
        if ( indexes.length != model.getRowCount() ) {
            LOGGER.info( "TableSorter.checkModel: not informed of a change in model." );
        }
    }

    public void sort() {
        checkModel();

        compares = 0;
        shuttlesort(indexes.clone(), indexes, 0, indexes.length);
    }

    /**
     *
     */
    public void n2sort() {
        for ( int i = 0; i < getRowCount(); i++ ) {
            for ( int j = i + 1; j < getRowCount(); j++ ) {
                if ( compare( indexes[i], indexes[j] ) == -1 ) {
                    swap( i, j );
                }
            }
        }
    }

    /**
     * This is a home-grown implementation which we have not had time to
     * research - it may perform poorly in some circumstances. It requires twice
     * the space of an in-place algorithm and makes NlogN assignments shuttling
     * the values between the two arrays. The number of compares appears to vary
     * between N-1 and NlogN depending on the initial order but the main reason
     * for using it here is that, unlike qsort, it is stable.
     *
     * @param from From
     * @param to To
     * @param low Low
     * @param high Hight
     */
    public void shuttlesort(int[] from, int[] to, int low, int high ) {
        if ( high - low < 2 ) {
            return;
        }

        int middle = ( low + high ) / 2;
        shuttlesort( to, from, low, middle );
        shuttlesort( to, from, middle, high );

        int p = low;
        int q = middle;

        /* This is an optional short-cut; at each recursive call,
         check to see if the elements in this subset are already
         ordered.  If so, no further comparisons are needed; the
         sub-array can just be copied.  The array must be copied rather
         than assigned otherwise sister calls in the recursion might
         get out of sync.  When the number of elements is three they
         are partitioned so that the first set, [low, mid), has one
         element and the second, [mid, high), has two. We skip the
         optimisation when the number of elements is three or less, as
         the first compare in the normal merge will produce the same
         sequence of steps. This optimisation seems to be worthwhile
         for partially ordered lists but some analysis is needed to
         find out how the performance drops to Nlog(N) as the initial
         order diminishes - it may drop very quickly.  */
        if ( high - low >= 4 && compare( from[middle - 1], from[middle] ) <= 0 ) {
            System.arraycopy( from, low, to, low, high - low );
            return;
        }

        // A normal merge.
        for ( int i = low; i < high; i++ ) {
            if ( q >= high || ( p < middle && compare( from[p], from[q] ) <= 0 ) ) {
                to[i] = from[p++];
            } else {
                to[i] = from[q++];
            }
        }
    }

    public void swap( int i, int j ) {
        int tmp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = tmp;
    }

    /**
     * The mapping only affects the contents of the data rows. Pass all requests
     * to these rows through the mapping array: "indexes".
     *
     * @return the value at the coordinates
     */
    @Override
    public Object getValueAt( int aRow, int aColumn ) {
        checkModel();
        return model.getValueAt( indexes[aRow], aColumn );
    }

    @Override
    public void setValueAt( Object aValue, int aRow, int aColumn ) {
        checkModel();
        model.setValueAt( aValue, indexes[aRow], aColumn );
    }

    public void sortByColumn( int column ) {
        sortByColumn( column, true );
    }

    public void sortByColumn( int column, boolean ascending ) {
        this.ascending = ascending;
        sortingColumns.clear();
        sortingColumns.add(column);
        sort();
        super.tableChanged(new TableModelEvent(this));
    }

    /**
     * There is no-where else to put this. Add a mouse listener to the Table to
     * trigger a table sort when a column heading is clicked in the JTable.
     *
     * @param table Table
     */
    public void addMouseListenerToHeaderInTable(final JTable table) {
        final TableSorter sorter = this;
        final MouseAdapter listMouseListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                final TableColumnModel columnModel = table.getColumnModel();
                final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                final int column = table.convertColumnIndexToModel(viewColumn);
                if (e.getClickCount() == 1 && column != -1) {
                    final int shiftPressed = e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK;
                    final boolean sortAscending = (shiftPressed == 0);
                    sorter.sortByColumn(column, sortAscending);
                }
            }
        };
        final JTableHeader th = table.getTableHeader();
        th.addMouseListener( listMouseListener );
    }
}
