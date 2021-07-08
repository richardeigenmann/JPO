package org.jpo.testground;


public class TransposeTester {

    /**
     * An entry point for standalone screen size testing.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        printTable(8, 4);
        printTable(9, 4);
        printTable(10, 4);
        printTable(11, 4);
        printTable(12, 4);
        printTable(13, 4);
        for (var n = 0; n < 90; n++) {
            printTable(n, 8);
        }
    }

    private static void printTable(final int n, final int COLS) {
        System.out.println(String.format("%nTable n=%d COLS=%d", n, COLS));
        int[] myArray = new int[n];
        for (var i = 0; i < n; i++) {
            myArray[i] = i;
        }

        final var ROWS = (n + COLS - 1) / COLS;
        System.out.println(String.format("ROWS: %d", ROWS));
        int[] transposedArray = new int[n + COLS];
        for (var i = 0; i < transposedArray.length; i++) {
            transposedArray[i] = -1;
        }

        for (var i = 0; i < n; i++) {
            int maxrows = (int) Math.ceil(n / ((double) COLS));
            int col = i / maxrows;
            int row = i % maxrows;
            int pos = row * COLS + col;
            transposedArray[pos] = myArray[i];
        }

        for (var i = 0; i < transposedArray.length; i++) {
            if (i % COLS == 0 && i > 0) {
                System.out.println("");
            }
            if (transposedArray[i] > -1) {
                System.out.print(transposedArray[i]);
                System.out.print("\t");
            }
        }

    }
}
