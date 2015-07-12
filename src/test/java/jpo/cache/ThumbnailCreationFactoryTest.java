/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.cache;


import jpo.dataModel.Settings;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailCreationFactoryTest {


    @Test
    public void thumbnailCreationFactoryTest() {
        ThumbnailCreationFactory tcf = new ThumbnailCreationFactory( Settings.ThumbnailCreationThreadPollingTime );
        Assert.assertNotNull(tcf);
        tcf.endThread = true;
    }
}
