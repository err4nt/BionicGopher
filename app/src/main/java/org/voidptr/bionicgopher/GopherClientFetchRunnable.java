package org.voidptr.bionicgopher;

import android.net.Uri;

import java.io.IOException;

/**
 * Created by errant on 11/11/17.
 */

public class GopherClientFetchRunnable implements Runnable {
    @Override
    public void run() {
        GopherClient client = new GopherClient();

        try {
            client.fetch(Uri.parse("gopher://voidptr.org:70"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
