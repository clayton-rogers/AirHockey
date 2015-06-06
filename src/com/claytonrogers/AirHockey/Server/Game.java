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

    private Player[] players;

    private final Socket player1;
    private final Socket player2;

    public Game(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        super.run();

        private BufferedReader player1reader;
        private BufferedWriter player1writer;
        private BufferedReader player2reader;
        private BufferedWriter player2writer;



        // Get the streams from the sockets.
        try {
            player1writer = new BufferedWriter(new OutputStreamWriter(player1.getOutputStream()));
            player1reader = new BufferedReader(new InputStreamReader(player1.getInputStream()));

            player2writer = new BufferedWriter(new OutputStreamWriter(player2.getOutputStream()));
            player2reader = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        } catch (IOException e) {
            closeGameConnection();
            return;
        }

        // Validate that both players have to correct version.
        Message requestVer = new VersionRequest();
        try {
            requestVer.send(player1writer);
            requestVer.send(player2writer);

            Disconnect disconnect = new Disconnect();

            VersionResponse response = (VersionResponse) Message.parseMessage(player1reader);
            if (! new String (response.getVersion()).equals(Protocol.PROTOCOL_VERSION)) {
                disconnect.send(player1writer);
            }

            response = (VersionResponse) Message.parseMessage(player2reader);
            if (! new String(response.getVersion()).equals(Protocol.PROTOCOL_VERSION)) {
                disconnect.send(player2writer);
            }
        } catch (IOException e) {
            closeGameConnection();
            return;
        }

        // Start the actual game
        new AirHockeyGame().play();


        // TODO game stuff here....
    }

    private void closeGameConnection () {
        try {
            player1writer.close();
            player1reader.close();
            player2writer.close();
            player2reader.close();

            player1.close();
            player2.close();
        } catch (IOException e) {
            System.out.println("Could not close game connections.");
        }
    }

    private void playGame() {


    }
}
