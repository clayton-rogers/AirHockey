package com.claytonrogers.AirHockey.Protocol;

/**
 * Created by clayton on 2015-06-06.
 */
public enum MessageType {
    VERSION_REQUEST,
    VERSION_RESPONSE,
    DISCONNECT,
    NULL,

    PING_REQUEST,
    PING_RESPONSE,

    POSITION_UPDATE,
    GAME_END;

    public static MessageType parse(String messageTypeString) {
        MessageType ret;
        int a = Integer.parseInt(messageTypeString);
        try {
            ret = MessageType.values()[a];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught bad message. This should never happen. Message #: " + a);
            ret = NULL;
        }
        return ret;
    }
}
