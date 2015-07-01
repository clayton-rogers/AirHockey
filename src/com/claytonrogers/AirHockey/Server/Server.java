package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server which listens for new connections then starts a game when there are enough players connected.
 *
 * <br><br>Created by clayton on 2015-06-06.
 */
final class Server {
    private Server() {
    }

    public static void main (String[] args) {
        System.out.println("Server starting...");

        try (ServerSocket serverSocket = new ServerSocket(Protocol.PORT_NUMBER)) {
            Connection player1 = null;
            while (true) {
                try {
                    System.out.println("Waiting for players to connect...");
                    Socket socket = serverSocket.accept();
                    if (player1 == null) {
                        System.out.println("Player 1 connected.");
                        player1 = new Connection(socket);
                    } else {
                        System.out.println("Player 2 connected.");
                        Connection player2 = new Connection(socket);
                        System.out.println("Two players connected, starting game.");
                        Game game = new Game(player1, player2);
                        game.start();
                        player1 = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Problem starting the server.");
        }
    }
}
