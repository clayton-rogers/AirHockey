package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This message is used to update the clients when one of them scores.
 *
 * <br><br>Created by clayton on 2015-07-01.
 */
public class PlayerScore extends Message{

    private int player1Score;
    private int player2Score;

    public PlayerScore(BufferedReader reader) {
        super(MessageType.PLAYER_SCORE);

        try {
            String temp;
            temp = reader.readLine();
            player1Score = Integer.parseInt(temp);
            temp = reader.readLine();
            player2Score = Integer.parseInt(temp);
        } catch (IOException e) {
            System.out.print(e);
        }
    }

    public PlayerScore(int player1Score, int player2Score) {
        super(MessageType.PLAYER_SCORE);
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    @Override
    public void send(PrintWriter writer) throws IOException {
        super.send(writer);
        writer.println(player1Score);
        writer.println(player2Score);
        writer.flush();
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }
}
