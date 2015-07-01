package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * Response message which is sent when a ping request message is received.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
public class PingResponse extends Message {
    public PingResponse () {
        super(MessageType.PING_RESPONSE);
    }
}
