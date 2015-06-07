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

    PUCK_UPDATE,
    OPPONENT_UPDATE,
    PLAYER_UPDATE,
    GAME_END;

    public static MessageType parseMessageType(int a) {
        MessageType ret;
        try {
            ret = MessageType.values()[a];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught bad message. This should never happen.");
            ret = NULL;
        }
        return ret;
    }
}
