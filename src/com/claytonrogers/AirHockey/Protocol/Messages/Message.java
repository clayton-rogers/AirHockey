package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by clayton on 2015-06-06.
 */
public abstract class Message {
    private final MessageType messageType;

    Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public static Message parseMessage (BufferedReader reader) {
        MessageType messageType;
        try {
            messageType = MessageType.parse(reader.readLine());
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("IO error occurred while reading the message type.");
            return null;
        }

        switch (messageType) {
            case VERSION_REQUEST:
                return new VersionRequest();
            case VERSION_RESPONSE:
                return new VersionResponse(reader);
            case DISCONNECT:
                return new Disconnect();
            case PING_REQUEST:
                return new PingRequest();
            case PING_RESPONSE:
                return new PingResponse();
            case POSITION_UPDATE:
                return new PositionUpdate(reader);
            case GAME_END:
                return new GameEnd(reader);
            case NULL:
                return null;
        }

        System.out.println("Could not find the message type. This should never happen. Message type: " + messageType);
        return null;
    }

    public void send(PrintWriter writer) throws IOException {
        writer.println(messageType.ordinal());
        writer.flush();
    }
    public MessageType getMessageType() {
        return messageType;
    }
}
