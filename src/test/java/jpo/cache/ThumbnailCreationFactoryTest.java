package jpo.cache;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailCreationFactoryTest {


    @Test
    public void thumbnailCreationFactoryTest() {
        ThumbnailCreationFactory tcf = new ThumbnailCreationFactory( 500 );
        Assert.assertNotNull(tcf);
        tcf.endThread = true;
    }
}
