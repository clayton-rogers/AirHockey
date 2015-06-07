package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.Messages.GameEnd;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Messages.PlayerUpdate;
import com.claytonrogers.AirHockey.Protocol.Messages.PuckUpdate;

/**
 * Created by clayton on 2015-06-06.
 */
public class AirHockeyGame {

    private static int FRAME_TIME_MS = 10;

    public void play (Connection[] playerConnections) {

        boolean gameOver = false;
        int winner = 0;
        Vector puckPosition = new Vector(100,100); // in pixel (x right, y down)
        Vector puckVelocity = new Vector(1,1);     // in pixel/frame
        Vector[] playerPositions = new Vector[2];
        playerPositions[0] = new Vector();
        playerPositions[1] = new Vector();

        // Main game loop
        while (!gameOver) {

            long startTime = System.currentTimeMillis();

            // Process inputs
            for (int i = 0; i < 2; i++) {
                while (true) {
                    Message message = playerConnections[i].receivedMessages.peek();
                    if (message == null) {
                        break;
                    }

                    switch (message.getMessageType()) {
                        // TODO make server handle all requests
                        case PLAYER_UPDATE:
                            playerPositions[i].assign(
                                    ((PlayerUpdate) message).getPosition()
                            );
                            break;
                        case DISCONNECT:
                            gameOver = true;
                            winner = 0;
                    }
                    playerConnections[i].receivedMessages.remove();
                }
            }

            // Calculate the next state of the game
            puckPosition.addInPlace(puckVelocity);

            // TODO check for collisions
            // TODO check for winner

            // Send the state to the players
            Message message = new PuckUpdate(puckPosition);
            for (Connection player : playerConnections) {
                player.send(message);
            }

            // Wait around for the next frame.
            long endTime = System.currentTimeMillis();
            long waitTime = FRAME_TIME_MS - (endTime - startTime);

            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                System.out.println("AirHockey was interrupted while sleeping.");
            }
        }


        Message gameEndMessage = new GameEnd(winner);
        for (Connection player : playerConnections) {
            player.send(gameEndMessage);
        }
    }
}
