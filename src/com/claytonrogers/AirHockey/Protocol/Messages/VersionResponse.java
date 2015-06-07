package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by clayton on 2015-06-06.
 */
public class VersionResponse extends Message{

    private String version;

    public VersionResponse (BufferedReader reader) {
        super(MessageType.VERSION_RESPONSE);

        try {
            version = reader.readLine();
        } catch (IOException e) {
            System.out.print(e);
        }
    }

    public VersionResponse (String version) {
        super(MessageType.VERSION_RESPONSE);
        this.version = version;
    }

    @Override
    public void send(PrintWriter writer) throws IOException {
        super.send(writer);
        writer.println(version);
        writer.flush();
    }

    public String getVersion() {
        return version;
    }
}
