package jpo.cache;

import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard Eigenmann
 */
public class JpoCacheTest {

    public JpoCacheTest() {
    }

    @Test
    public void testLoadProperties() {
        Properties props = JpoCache.loadProperties();
        assertTrue( "Expecting more than 30 properties to be defined", props.entrySet().size()  > 30);
    }
}
