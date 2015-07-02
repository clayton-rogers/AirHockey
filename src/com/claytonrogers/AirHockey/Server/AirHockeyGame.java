package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.Messages.*;
import com.claytonrogers.AirHockey.Protocol.Messages.PositionUpdate.ObjectType;
import com.claytonrogers.AirHockey.Protocol.Protocol;

/**
 * The air hockey game. Handles the propagation of pieces, collisions, and receiving and sending
 * information to the clients.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
class AirHockeyGame {

    private static final int    FRAME_TIME_MS    = 5;
    // These are the positions of the walls.
    // For checking collisions with the wall, you still have to account for the size of the puck.
    private static final double TOP_WALL_POS     = 31.0;
    private static final double LEFT_WALL_POS    = 8.0;
    private static final double BOTTOM_WALL_POS  = Protocol.FIELD_HEIGHT - 9;
    private static final double RIGHT_WALL_POS   = Protocol.FIELD_WIDTH - 9;

    // This is the sum of the radius of the puck and player.
    private static final double COLLISION_RADIUS = Protocol.PLAYER_RADIUS + Protocol.PUCK_RADIUS;

    // Number of frames before a collision can occur, after a collision has already occurred.
    private static final int COLLISION_COOLDOWN = 100;

    public void play (Connection[] playerConnections) {

        boolean gameOver = false;
        int winner = 0;
        Vector puckPosition = new Vector(50.0,50.0); // in pixel (x right, y down)
        Vector puckVelocity = new Vector(2.0,2.0);     // in pixel/frame
        Vector[] playerPositions = new Vector[2];
        playerPositions[0] = new Vector();
        playerPositions[1] = new Vector();
        int collisionCooldownValue = 0;
        int[] playerScore = new int[2];
        playerScore[0] = 0;
        playerScore[1] = 0;

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
            if (collisionCooldownValue > 0) {
                --collisionCooldownValue;
            }
            // Check for collision with the walls
            if (puckPosition.x < LEFT_WALL_POS + Protocol.PUCK_RADIUS) {
                // Left wall
                puckPosition.x = LEFT_WALL_POS + Protocol.PUCK_RADIUS;
                puckVelocity.x *= -1.0;
            }
            if (puckPosition.x > RIGHT_WALL_POS - Protocol.PUCK_RADIUS) {
                // Right wall
                puckPosition.x = RIGHT_WALL_POS - Protocol.PUCK_RADIUS;
                puckVelocity.x *= -1.0;
            }
            if (puckPosition.y < TOP_WALL_POS + Protocol.PUCK_RADIUS) {
                // Top wall
                puckPosition.y = TOP_WALL_POS + Protocol.PUCK_RADIUS;
                puckVelocity.y *= -1.0;
                playerScore[0]++;
                // Send the score to the client
                PlayerScore score = new PlayerScore(playerScore[0], playerScore[1]);
                for (int i = 0; i < 2; i++) {
                    playerConnections[i].send(score);
                }
            }
            if (puckPosition.y > BOTTOM_WALL_POS - Protocol.PUCK_RADIUS) {
                // Bottom wall
                puckPosition.y = BOTTOM_WALL_POS - Protocol.PUCK_RADIUS;
                puckVelocity.y *= -1.0;
                playerScore[1]++;
                // Send the score to the client
                PlayerScore score = new PlayerScore(playerScore[0], playerScore[1]);
                for (int i = 0; i < 2; i++) {
                    playerConnections[i].send(score);
                }
            }

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
                opponentIndex = i == 0 ? 1 : 0;
                message = new PositionUpdate(playerPositions[opponentIndex], ObjectType.OPPONENT);
                playerConnections[i].send(message);
            }

            // Check for player to puck collisions
            // TODO Fix collision handling
            for (int i = 0; i < 2; i++) {
                Vector puckToPlayer = new Vector(playerPositions[i]);
                puckToPlayer.subInPlace(puckPosition);

                if (puckToPlayer.magnitude() < COLLISION_RADIUS && collisionCooldownValue == 0) {
                    // A collision has occurred.
                    Vector collisionNormal = puckToPlayer.normal();

                    double numerator = puckVelocity.dotProduct(collisionNormal) * 2.0;
                    double denominator = collisionNormal.dotProduct(collisionNormal);
                    collisionNormal = collisionNormal.scalarMultiply(numerator/denominator);
                    collisionNormal.subInPlace(puckVelocity);
                    puckVelocity.assign(collisionNormal);

                    collisionCooldownValue = COLLISION_COOLDOWN;

                    // Only going to calculate one collision per frame.
                    break;
                }
            }
            // TODO check for winner

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
