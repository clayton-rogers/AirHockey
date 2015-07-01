package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * A request message from the server or client to respond with a ping response as soon an possible.
 * Reserved for future use.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
class PingRequest extends Message {
    PingRequest() {
        super(MessageType.PING_REQUEST);
    }
}
