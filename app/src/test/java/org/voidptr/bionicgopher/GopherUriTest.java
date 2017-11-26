package org.voidptr.bionicgopher;

import org.junit.Test;
import org.voidptr.bionicgopher.model.GopherUri;

import static junit.framework.Assert.assertEquals;

/**
 * Created by errant on 11/19/17.
 */

public class GopherUriTest {
    @Test
    public void test_create() throws Exception {
        GopherUri t1 = new GopherUri();

        GopherUri t2 = new GopherUri("protocol", "host", 7777);

        assertEquals(t2.getProtocol(), "protocol");
        assertEquals(t2.getHost(), "host");
        assertEquals(t2.getPort(), Integer.valueOf(7777));
    }

    @Test
    public void test_parse_correct_full_url() throws Exception {
        GopherUri t1 = new GopherUri("gopher://voidptr.org:70/errant");

        assertEquals(t1.getProtocol(), "gopher");
        assertEquals(t1.getHost(), "voidptr.org");
        assertEquals(t1.getPort(), Integer.valueOf(70));
        assertEquals(t1.getPathElements().get(0), "errant");
    }

    @Test
    public void test_parse_correct_url_without_port() throws Exception {
        GopherUri t1 = new GopherUri("gopher://voidptr.org/errant");

        assertEquals(t1.getProtocol(), "gopher");
        assertEquals(t1.getHost(), "voidptr.org");
        assertEquals(t1.getPathElements().get(0), "errant");
    }

    @Test
    public void test_parse_correct_url_without_protocol() throws Exception {
        GopherUri t1 = new GopherUri("voidptr.org:70/errant");

        assertEquals(t1.getHost(), "voidptr.org");
        assertEquals(t1.getPort(), Integer.valueOf(70));
        assertEquals(t1.getPathElements().get(0), "errant");
    }

    @Test
    public void test_parse_correct_url_without_protocol_or_port() throws Exception {
        GopherUri t1 = new GopherUri("voidptr.org/errant");

        assertEquals(t1.getHost(), "voidptr.org");
        assertEquals(t1.getPathElements().get(0), "errant");
    }

    @Test
    public void test_parse_correct_url_without_path() throws Exception {
        GopherUri t1 = new GopherUri("gopher://voidptr.org:70");

        assertEquals(t1.getProtocol(), "gopher");
        assertEquals(t1.getHost(), "voidptr.org");
        assertEquals(t1.getPort(), Integer.valueOf(70));
    }

    @Test
    public void test_parse_correct_url_without_path_or_port() throws Exception {
        GopherUri t1 = new GopherUri("gopher://voidptr.org");

        assertEquals(t1.getProtocol(), "gopher");
        assertEquals(t1.getHost(), "voidptr.org");
    }

    @Test
    public void test_parse_correct_url_without_path_or_port_or_protocol() throws Exception {
        GopherUri t1 = new GopherUri("voidptr.org");

        assertEquals(t1.getHost(), "voidptr.org");
    }
}
