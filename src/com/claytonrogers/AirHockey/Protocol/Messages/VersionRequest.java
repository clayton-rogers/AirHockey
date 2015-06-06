package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * Created by clayton on 2015-06-06.
 */
public class VersionRequest extends Message {
    public VersionRequest() {
        super(MessageType.VERSION_REQUEST);
        messageType = MessageType.VERSION_REQUEST;
    }
}
