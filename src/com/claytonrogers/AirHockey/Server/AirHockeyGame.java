package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.Messages.GameEnd;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Messages.PingResponse;
import com.claytonrogers.AirHockey.Protocol.Messages.PositionUpdate;
import com.claytonrogers.AirHockey.Protocol.Messages.PositionUpdate.ObjectType;

/**
 * Created by clayton on 2015-06-06.
 */
public class AirHockeyGame {

    private static int FRAME_TIME_MS = 10;

    public void play (Connection[] playerConnections) {

        boolean gameOver = false;
        int winner = 0;
        Vector puckPosition = new Vector(10.0,10.0); // in pixel (x right, y down)
        Vector puckVelocity = new Vector(0.5,0.7);     // in pixel/frame
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
                        case POSITION_UPDATE:
                            PositionUpdate positionUpdate = (PositionUpdate) message;
                            if (positionUpdate.getType() == ObjectType.PLAYER) {
                                playerPositions[i].assign(positionUpdate.getPosition());
                            }
                            break;
                        case DISCONNECT:
                            gameOver = true;
                            break;
                        case PING_REQUEST:
                            Message message1 = new PingResponse();
                            playerConnections[i].send(message1);
                            break;
                    }
                    playerConnections[i].receivedMessages.remove();
                }
            }

            // Calculate the next state of the game
            puckPosition.addInPlace(puckVelocity);

            // Check for collisions.
            for (int i = 0; i < 2; i++) {
                Vector puckToPlayer = new Vector(playerPositions[i]);
                puckToPlayer.subInPlace(puckPosition);

                if (puckToPlayer.magnitude() < 50.0) {
                    // A collision has occurred.
                    System.out.println("Collision before vel: " + puckVelocity.x + ' ' + puckVelocity.y);
                    // We're going to multiply the velocity by 100 000 then divide it back out later.
                    final int MULT_CONST = 10000;
                    puckVelocity = puckVelocity.scalarMultiply(MULT_CONST);
                    double numerator = puckVelocity.dotProduct(puckToPlayer) * 2.0;
                    double denominator = puckToPlayer.dotProduct(puckToPlayer);
                    puckToPlayer = puckToPlayer.scalarMultiply(numerator/denominator);
                    puckToPlayer.subInPlace(puckVelocity);
                    puckVelocity.assign(puckToPlayer);

                    puckVelocity = puckVelocity.scalarDivide(MULT_CONST);

                    System.out.println("Collision after vel: " + puckVelocity.x + ' ' + puckVelocity.y);

                    // Only going to calculate one collision per frame.
                    break;
                }
            }

            // TODO check for collisions
            // TODO check for winner

            // Send the state to the players
            for (int i = 0; i < 2; i++) {
                if (!playerConnections[i].isGood()) {
                    gameOver = true;
                }
                Message message;

                // Send the puck position
                message = new PositionUpdate(puckPosition, ObjectType.PUCK);
                playerConnections[i].send(message);

                // Send the opponent position
                int opponentIndex;
                if (i == 0) {
                    opponentIndex = 1;
                } else {
                    opponentIndex = 0;
                }
                message = new PositionUpdate(playerPositions[opponentIndex], ObjectType.OPPONENT);
                playerConnections[i].send(message);
            }

            // Wait around for the next frame.
            long endTime = System.currentTimeMillis();
            long waitTime = FRAME_TIME_MS - (endTime - startTime);
            if (waitTime < 0L) {
                waitTime = 0L;
                System.out.println("Wait time was negative. Is the server overloaded?");
            }

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
