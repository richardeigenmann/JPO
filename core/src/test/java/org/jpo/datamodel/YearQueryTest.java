package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class YearQueryTest {

    @Test
    void getTitle() {
        final YearQuery yearQuery = new YearQuery("2021");
        assertNotNull(yearQuery);
        assertEquals("2021", yearQuery.getTitle());
        assertEquals("2021", yearQuery.toString());
    }


}