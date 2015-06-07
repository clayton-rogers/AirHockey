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

    public Game(Player player1, Player player2) {
        players[0] = player1;
        players[1] = player2;
    }

    @Override
    public void run() {
        super.run();

        // Validate that both players have to correct version.
        Message requestVer = new VersionRequest();
        try {
            for (Player player : players) {
                player.send(requestVer);
            }

            for (Player player : players) {
                Message message = null;
                while (message == null) {
                    // Wait around until we get a message from the client.
                    message = player.messageQueue.poll();
                }
                VersionResponse versionResponse = (VersionResponse) message;
                String versionString = new String(versionResponse.getVersion());
                if (!versionString.equals(Protocol.PROTOCOL_VERSION)) {
                    Disconnect disconnectMessage = new Disconnect();
                    player.send(disconnectMessage);
                    player.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            System.out.println("There was an issue validating the clients.");
            return;
        }

        System.out.println("Clients have been validated.");

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
