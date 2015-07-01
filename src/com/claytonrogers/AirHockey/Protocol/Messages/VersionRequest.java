package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * A server request for the client to respond with the version response.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
public class VersionRequest extends Message {
    public VersionRequest() {
        super(MessageType.VERSION_REQUEST);
    }
}
