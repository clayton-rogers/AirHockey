package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * Created by clayton on 2015-06-06.
 */
public class PingRequest extends Message {
    public PingRequest() {
        super(MessageType.PING_REQUEST);
    }
}
