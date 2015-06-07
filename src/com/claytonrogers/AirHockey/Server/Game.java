package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.Messages.Disconnect;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Messages.VersionRequest;
import com.claytonrogers.AirHockey.Protocol.Messages.VersionResponse;
import com.claytonrogers.AirHockey.Protocol.Protocol;

/**
 * Created by clayton on 2015-06-06.
 */
public class Game extends Thread {

    private Connection[] players = new Connection[2];

    public Game(Connection player1, Connection player2) {
        players[0] = player1;
        players[1] = player2;
    }

    @Override
    public void run() {
        super.run();

        // Validate that both players have to correct version.
        Message requestVer = new VersionRequest();
        for (Connection player : players) {
            player.send(requestVer);
        }

        for (Connection player : players) {
            Message message = null;
            while (message == null) {
                // Wait around until we get a message from the client.
                message = player.receivedMessages.poll();
            }
            VersionResponse versionResponse = (VersionResponse) message;
            if (!versionResponse.getVersion().equals(Protocol.PROTOCOL_VERSION)) {
                System.out.println("Client does not have correct version, disconnecting.");
                System.out.println("Expected: |" + Protocol.PROTOCOL_VERSION + "| Got: |" + versionResponse.getVersion() + "|");
                Disconnect disconnectMessage = new Disconnect();
                for (Connection myPlayer : players) {
                    myPlayer.send(disconnectMessage);
                }
                return;
            } else {
                System.out.println("Client does have correct version.");
            }
        }

        System.out.println("Clients have been validated.");

        // Start the actual game
        new AirHockeyGame().play(players);
    }
}
