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
    GAME_END;

    public static MessageType parseMessageType(int a) {
        return MessageType.values()[a];
    }
}
