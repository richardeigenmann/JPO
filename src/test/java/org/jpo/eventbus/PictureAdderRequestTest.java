package org.jpo.eventbus;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.assertj.core.api.Fail.fail;

class PictureAdderRequestTest {
    @Test
    void testCreateBadRequest() {
        final var node = new SortableDefaultMutableTreeNode();
        final var file = new File("no such file.jpg");
        final var files = new File[]{file};
        final var emptyCollection = new ArrayList<Integer>();
        try {
            new PictureAdderRequest(node, files, true, false, false, emptyCollection);
        } catch (IllegalArgumentException ex) {
            // expected
            return;
        }
        fail("a node without a GroupInfo can't add pictures!");
    }

    @Test
    void testCreateGoodRequest() {
        final var groupInfo = new GroupInfo("GroupInfo");
        final var node = new SortableDefaultMutableTreeNode(groupInfo);
        final var file = new File("no such file.jpg");
        final var files = new File[]{file};
        final var emptyCollection = new ArrayList<Integer>();
        try {
            new PictureAdderRequest(node, files, true, false, false, emptyCollection);
        } catch (IllegalArgumentException ex) {
            fail("Wasn't supposed to hit the exception: " + ex.getMessage());
        }
    }

}