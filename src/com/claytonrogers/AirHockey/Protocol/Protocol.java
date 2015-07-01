package com.claytonrogers.AirHockey.Protocol;

/**
 * A list of constants which are common to the server and client, and the protocol version number
 * which must be the same on the client and server.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
public final class Protocol {
    public static final String PROTOCOL_VERSION = "v0.5";
    public static final int PORT_NUMBER = 60046;

    public static final int FIELD_WIDTH  = 600;
    public static final int FIELD_HEIGHT = 900;

    public static final int PLAYER_RADIUS = 30;
    public static final int PUCK_RADIUS   = 10;

    public static final boolean NET_DEBUG = false;

    private Protocol() {
    }
}
