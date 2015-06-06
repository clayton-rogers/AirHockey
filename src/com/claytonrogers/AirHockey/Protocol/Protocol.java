package com.claytonrogers.AirHockey.Protocol;

import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Messages.NullMessage;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public class Protocol {
    public static String PROTOCOL_VERSION = "v0.1";

    Message ReceiveMessage(BufferedReader reader) {
        try {
            MessageType messageType = MessageType.parseMessageType(reader.read());

        } catch (IOException e) {
            System.out.print(e);
        }

        return new NullMessage();
    }
}
