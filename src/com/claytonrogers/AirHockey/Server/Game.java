package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Protocol.Messages.Disconnect;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Messages.VersionRequest;
import com.claytonrogers.AirHockey.Protocol.Messages.VersionResponse;
import com.claytonrogers.AirHockey.Protocol.Protocol;

import java.io.*;
import java.net.Socket;

/**
 * Created by clayton on 2015-06-06.
 */
public class Game extends Thread {

    private Player[] players = new Player[2];

    private final Socket player1;
    private final Socket player2;

    public Game(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        super.run();

        // Get the streams from the sockets.
        players[0] = new Player(player1);
        players[1] = new Player(player2);
        for (Player player : players) {
            if (player.writer == null) {
                return;
            }
        }

        // Validate that both players have to correct version.
        Message requestVer = new VersionRequest();
        try {
            for (Player player : players) {
                requestVer.send(player.writer);
            }

            for (Player player : players) {
                VersionResponse versionResponse =
                        (VersionResponse) Message.parseMessage(player.reader);
                String versionString = new String(versionResponse.getVersion());
                if (!versionString.equals(Protocol.PROTOCOL_VERSION)) {
                    Disconnect disconnectMessage = new Disconnect();
                    disconnectMessage.send(player.writer);
                    player.close();
                }
            }
        } catch (IOException e) {
            return;
        }

        // Start the actual game
        new AirHockeyGame().play(players);

        // Kill the connections
        for (Player player : players) {
            try {
                player.close();
            } catch (IOException e) {
                System.out.println("There was a problem closing the connection with the clients.");
            }
        }
    }
}
