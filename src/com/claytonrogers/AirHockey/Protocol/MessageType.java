package com.claytonrogers.AirHockey.Protocol;

/**
 * The enumeration of all the possible message types. If you change this file, you should increment
 * the protocol version number in the Protocol class.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
public enum MessageType {
    VERSION_REQUEST,
    VERSION_RESPONSE,
    DISCONNECT,
    NULL,

    PING_REQUEST,
    PING_RESPONSE,

    POSITION_UPDATE,
    PLAYER_SCORE,
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
