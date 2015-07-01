package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

/**
 * Null message which is not used and would generally be considered an ICD failure.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
public class NullMessage extends Message{
    public NullMessage() {
        super(MessageType.NULL);
    }
}
