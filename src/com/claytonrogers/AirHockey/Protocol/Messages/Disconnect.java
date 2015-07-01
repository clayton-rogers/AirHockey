package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * Message which tells the other end of the connection that the connection will be terminated.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
public class Disconnect extends Message {
    public Disconnect() {
        super(MessageType.DISCONNECT);
    }
}
