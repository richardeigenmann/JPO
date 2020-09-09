package org.jpo.datamodel;

import java.io.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/* This class was downloaded from IBM developer works: http://www-128.ibm.com/developerworks/java/library/j-prefapi.html */

/* The licensing is not clear. There are no copyright notices in the code yet 
 there is a lot of standard legal stuff on the download
 page. My take is that this is pretty low key stuff so IBM will not get 
 overly upset by my using their sample code. If I am wrong 
 then please let me know and I will have to re-invent the wheel on 
 these preference things. */
/**
 * Class used to serialise the memorised pictures of a camera
 *
 * @author Richard Eigenmann
 */
public class PrefObj {

    private PrefObj() {
        throw new IllegalStateException("Utility class");
    }

    // Max byte count is 3/4 max string length (see Preferences
    // documentation).

    private static final int PIECE_LENGTH
            = ( ( 3 * Preferences.MAX_VALUE_LENGTH ) / 4 );

    private static byte[] object2Bytes( Object o ) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        return baos.toByteArray();
    }

    private static byte[][] breakIntoPieces(byte[] raw) {
        final int numPieces = ( raw.length + PIECE_LENGTH - 1 ) / PIECE_LENGTH;
        final byte[][] pieces = new byte[numPieces][];
        for ( int i = 0; i < numPieces; ++i ) {
            int startByte = i * PIECE_LENGTH;
            int endByte = startByte + PIECE_LENGTH;
            if ( endByte > raw.length ) {
                endByte = raw.length;
            }
            int length = endByte - startByte;
            pieces[i] = new byte[length];
            System.arraycopy( raw, startByte, pieces[i], 0, length );
        }
        return pieces;
    }

    private static void writePieces( Preferences prefs, String key,
                                     byte[][] pieces) throws BackingStoreException {
        final Preferences node = prefs.node( key );
        node.clear();
        for ( int i = 0; i < pieces.length; ++i ) {
            node.putByteArray( "" + i, pieces[i] );
        }
    }

    private static byte[][] readPieces( Preferences prefs, String key )
            throws BackingStoreException {
        final Preferences node = prefs.node( key );
        final String[] keys = node.keys();
        final int numPieces = keys.length;
        final byte[][] pieces = new byte[numPieces][];
        for ( int i = 0; i < numPieces; ++i ) {
            pieces[i] = node.getByteArray( "" + i, null );
        }
        return pieces;
    }

    private static byte[] combinePieces(byte[][] pieces) {
        int length = 0;
        for (byte[] piece : pieces) {
            length += piece.length;
        }
        byte[] raw = new byte[length];
        int cursor = 0;
        for (byte[] piece : pieces) {
            System.arraycopy(piece, 0, raw, cursor, piece.length);
            cursor += piece.length;
        }
        return raw;
    }

    private static Object bytes2Object(byte[] raw)
            throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bais = new ByteArrayInputStream( raw );
        final ObjectInputStream ois = new ObjectInputStream( bais );
        return ois.readObject();
    }

    /**
     * Puts an object in the preferences
     *
     * @param prefs the preferences to store into
     * @param key the key Key under which to store
     * @param o the object Object to store
     * @throws IOException Error when no good
     * @throws BackingStoreException Error when no good
     */
    public static void putObject( Preferences prefs, String key, Object o )
            throws IOException, BackingStoreException {
        final byte[] raw = object2Bytes(o);
        final byte[][] pieces = breakIntoPieces(raw);
        writePieces( prefs, key, pieces );
    }

    /**
     * Retrieves the object from the preferences
     *
     * @param prefs the preferences to retrieve from
     * @param key the key the key
     * @return the object The object
     * @throws IOException Error if no good
     * @throws BackingStoreException Error if no good
     * @throws ClassNotFoundException Error if no good
     */
    public static Object getObject( Preferences prefs, String key )
            throws IOException, BackingStoreException, ClassNotFoundException {
        final byte[][] pieces = readPieces(prefs, key);
        final byte[] raw = combinePieces(pieces);
        return bytes2Object( raw );
    }
}
