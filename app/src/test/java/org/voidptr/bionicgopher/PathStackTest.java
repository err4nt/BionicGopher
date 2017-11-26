package org.voidptr.bionicgopher;

import org.junit.Test;
import org.voidptr.bionicgopher.model.GopherUri;
import org.voidptr.bionicgopher.model.PathStack;

import static junit.framework.Assert.assertEquals;

/**
 * Created by errant on 11/19/17.
 */

public class PathStackTest {
    @Test
    public void test_create() throws Exception {
        PathStack stack1 = new PathStack();
    }

    @Test
    public void test_add_path() throws Exception {
        PathStack stack1 = new PathStack();

        GopherUri uri1 = new GopherUri("protocol", "host1", 7777);

        stack1.push(uri1);

        GopherUri tUri1 = stack1.getCurrent();

        assertEquals(tUri1.getHost(), "host1");

        GopherUri uri2 = new GopherUri("protocol", "host2", 7777);
        GopherUri uri3 = new GopherUri("protocol", "host3", 7777);
        GopherUri uri4 = new GopherUri("protocol", "host4", 7777);

        stack1.push(uri2);
        stack1.push(uri3);
        stack1.push(uri4);

        tUri1 = stack1.getCurrent();

        assertEquals(tUri1.getHost(), "host4");

        stack1.back();

        tUri1 = stack1.getCurrent();

        assertEquals(tUri1.getHost(), "host3");

        stack1.back();

        tUri1 = stack1.getCurrent();

        assertEquals(tUri1.getHost(), "host2");

    }
}
