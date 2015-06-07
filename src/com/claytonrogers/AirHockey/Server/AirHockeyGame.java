package com.claytonrogers.AirHockey.Server;


import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Messages.GameEnd;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Messages.PuckUpdate;

import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public class AirHockeyGame {

    private static int FRAME_TIME_MS = 10;



    public void play (Player[] players) {

        boolean gameOver = false;
        int winner = 0;
        Vector puckPosition = new Vector(100,100); // in pixel (x right, y down)
        Vector puckVelocity = new Vector(2,2);     // in pixel/ms
        Vector[] playerPositions = new Vector[2];
        playerPositions[0] = new Vector();
        playerPositions[1] = new Vector();

        // Main game loop
        while (!gameOver) {

            long startTime = System.currentTimeMillis();

            // Process inputs
            for (Player player : players) {
                while (true) {
                    Message message = player.messageQueue.peek();
                    if (message == null) {
                        break;
                    }

                    switch (message.getMessageType()) {
                        case DISCONNECT:
                            gameOver = true;
                            winner = 0;
                    }
                }
            }

            // Calculate the next state of the game
            Vector puckDisplacement = puckVelocity.scalarMultiply(FRAME_TIME_MS);
            puckPosition.addInPlace(puckDisplacement);

            // TODO check for collisions
            // TODO check for winner

            // Send the state to the players
            Message message = new PuckUpdate(puckPosition);
            for (Player player : players) {
                try {
                    message.send(player.writer);
                } catch (IOException e) {
                    System.out.println("Exception while trying to send players update.");
                    gameOver = true;
                    winner = 0;
                }
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
        for (Player player : players) {
            try {
                gameEndMessage.send(player.writer);
            } catch (IOException e) {
                // We don't care if they get it.
            }
        }
    }
}
