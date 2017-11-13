package org.voidptr.bionicgopher;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by errant on 11/11/17.
 * TCP interface to the gopher protocol
 */

public class GopherClient {
    public final static int GOPHER_PORT = 70;

    public GopherClient() {

    }

    public GopherPage fetch(Uri address) throws IOException{
        Socket connection = new Socket(address.getHost(),
                (address.getPort() == 0) ? address.getPort() : GOPHER_PORT);

        ArrayList<String> lineBuffer = new ArrayList<>();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        connection.getOutputStream().write(address.getPath().getBytes());
        connection.getOutputStream().write('\r');
        connection.getOutputStream().write('\n');

        String line = in.readLine();
        while (line != null) {
            lineBuffer.add(line);
            line = in.readLine();
        }

        return GopherPage.fromStringList(lineBuffer);
    }
}
