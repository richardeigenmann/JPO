/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui;


import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author richi
 */
public class ThumbnailCreationFactoryTest {
    
    public ThumbnailCreationFactoryTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void ThumbnailCreationFactoryTest() {
        ThumbnailCreationFactory tcf = new ThumbnailCreationFactory();
        Assert.assertNotNull(tcf);
        tcf.endThread = true;
    }
}
