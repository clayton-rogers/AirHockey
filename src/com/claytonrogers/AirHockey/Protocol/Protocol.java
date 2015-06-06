package com.claytonrogers.AirHockey.Protocol;

/**
 * Created by clayton on 2015-06-06.
 */
public class Protocol {
    public static String PROTOCOL_VERSION = "v0.1";

    public enum MessageType {
        VERSION_REQUEST,
        VERSION_RESPONSE,
        DISCONNECT,

        PING_REQUEST,
        PING_RESPONSE,

        PUCK_UPDATE,
        OPPONENT_UPDATE,
        GAME_END
    }
}
