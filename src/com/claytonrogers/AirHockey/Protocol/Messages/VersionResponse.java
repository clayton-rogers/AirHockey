package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public class VersionResponse extends Message{

    private int length;
    private char[] version;
    private boolean versionValid = false;

    public VersionResponse (BufferedReader reader) {
        super(MessageType.VERSION_RESPONSE);

        try {
            length = reader.read();
            version = new char[length];
            reader.read(version, 0, length);
            versionValid = true;
        } catch (IOException e) {
            System.out.print(e);
        }
    }

    public VersionResponse (String version) {
        super(MessageType.VERSION_RESPONSE);

        length = version.length();
        this.version = version.toCharArray();
        versionValid = true;
    }

    @Override
    public void send(BufferedWriter writer) throws IOException {
        super.send(writer);
        writer.write(length);
        writer.write(version);
        writer.flush();
    }

    public char[] getVersion() {
        return versionValid ? version : "".toCharArray();
    }
}
