package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by clayton on 2015-06-06.
 */
public class Server {
    public static void main (String[] args) {
        System.out.println("Server starting...");

        try (ServerSocket serverSocket = new ServerSocket(Protocol.PORT_NUMBER)) {
            Socket player1 = null;
            Socket player2;
            while (true) {
                try (Socket socket = serverSocket.accept()){
                    if (player1 == null) {
                        System.out.println("Player 1 connected.");
                        player1 = socket;
                    } else {
                        System.out.println("Player 2 connected.");
                        player2 = socket;
                        Game game = new Game(player1, player2);
                        game.start();
                        player1 = null;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Problem starting the server.");
        }
    }
}
