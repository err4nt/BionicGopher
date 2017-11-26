package org.voidptr.bionicgopher.network;

import org.voidptr.bionicgopher.model.GopherPage;
import org.voidptr.bionicgopher.model.GopherUri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by errant on 11/11/17.
 * TCP interface to the gopher protocol
 */

public class GopherClient {
    private final static int GOPHER_PORT = 70;

    /**
     * Fetch a gopher page and return a <code>GopherPage</code> object
     * @param address URL of the address to fetch
     * @return <code>GopherPage</code> object containing he contents of the page
     * @throws IOException thrown in the case of a network error
     */
    public GopherPage fetch(GopherUri address) throws IOException{
        Socket connection = new Socket(address.getHost(),
                (address.getPort() != null) ? address.getPort() : GOPHER_PORT);

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

        return GopherPage.fromStringList(address, lineBuffer);
    }

    public void downloadTemporary(File target, GopherUri address) throws IOException{
        Socket connection = new Socket(address.getHost(),
                (address.getPort() != null) ? address.getPort() : GOPHER_PORT);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        FileOutputStream out = new FileOutputStream(target);

        connection.getOutputStream().write(address.getPath().getBytes());
        connection.getOutputStream().write('\r');
        connection.getOutputStream().write('\n');

        int data = in.read();
        while(data != -1){
            out.write(data);
            data = in.read();
        }

        out.flush();
    }
}
