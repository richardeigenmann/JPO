package jpo.dataModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    // Max byte count is 3/4 max string length (see Preferences
    // documentation).

    static private final int pieceLength
            = ( ( 3 * Preferences.MAX_VALUE_LENGTH ) / 4 );

    static private byte[] object2Bytes( Object o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        return baos.toByteArray();
    }

    static private byte[][] breakIntoPieces( byte raw[] ) {
        int numPieces = ( raw.length + pieceLength - 1 ) / pieceLength;
        byte pieces[][] = new byte[numPieces][];
        for ( int i = 0; i < numPieces; ++i ) {
            int startByte = i * pieceLength;
            int endByte = startByte + pieceLength;
            if ( endByte > raw.length ) {
                endByte = raw.length;
            }
            int length = endByte - startByte;
            pieces[i] = new byte[length];
            System.arraycopy( raw, startByte, pieces[i], 0, length );
        }
        return pieces;
    }

    static private void writePieces( Preferences prefs, String key,
            byte pieces[][] ) throws BackingStoreException {
        Preferences node = prefs.node( key );
        node.clear();
        for ( int i = 0; i < pieces.length; ++i ) {
            node.putByteArray( "" + i, pieces[i] );
        }
    }

    static private byte[][] readPieces( Preferences prefs, String key )
            throws BackingStoreException {
        Preferences node = prefs.node( key );
        String keys[] = node.keys();
        int numPieces = keys.length;
        byte pieces[][] = new byte[numPieces][];
        for ( int i = 0; i < numPieces; ++i ) {
            pieces[i] = node.getByteArray( "" + i, null );
        }
        return pieces;
    }

    static private byte[] combinePieces( byte pieces[][] ) {
        int length = 0;
        for (byte[] piece : pieces) {
            length += piece.length;
        }
        byte raw[] = new byte[length];
        int cursor = 0;
        for (byte[] piece : pieces) {
            System.arraycopy(piece, 0, raw, cursor, piece.length);
            cursor += piece.length;
        }
        return raw;
    }

    static private Object bytes2Object( byte raw[] )
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream( raw );
        ObjectInputStream ois = new ObjectInputStream( bais );
        Object o = ois.readObject();
        return o;
    }

    /**
     * Puts an object in the preferences
     *
     * @param prefs the preferences to store into
     * @param key the key Key under which to store
     * @param o the object Object to store
     * @throws IOException Error when no good
     * @throws BackingStoreException Error when no good
     * @throws ClassNotFoundException Error when no good
     */
    static public void putObject( Preferences prefs, String key, Object o )
            throws IOException, BackingStoreException, ClassNotFoundException {
        byte raw[] = object2Bytes( o );
        byte pieces[][] = breakIntoPieces( raw );
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
    static public Object getObject( Preferences prefs, String key )
            throws IOException, BackingStoreException, ClassNotFoundException {
        byte pieces[][] = readPieces( prefs, key );
        byte raw[] = combinePieces( pieces );
        Object o = bytes2Object( raw );
        return o;
    }
}
