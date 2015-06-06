package com.claytonrogers.AirHockey.Server;

import java.io.*;
import java.net.Socket;

/**
 * Created by clayton on 2015-06-06.
 */
public class Player implements Closeable {

    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;

    public Player(Socket s) {
        socket = s;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            reader = null;
            writer = null;
        }
    }

    public BufferedReader reader() {
        return reader;
    }

    public BufferedWriter writer() {
        return writer;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
