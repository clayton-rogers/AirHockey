package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public abstract class Message {
    protected MessageType messageType;

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public static Message parseMessage (MessageType messageType, BufferedReader reader) {
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
            case PUCK_UPDATE:
                return new PuckUpdate(reader);
            case OPPONENT_UPDATE:
                return new OpponentUpdate(reader);
            case NULL:
            default:
                return new NullMessage();
        }
    }

    public void send(BufferedWriter writer) throws IOException {
        writer.write(messageType.ordinal());
    }
    public MessageType getMessageType() {
        return messageType;
    }
}
