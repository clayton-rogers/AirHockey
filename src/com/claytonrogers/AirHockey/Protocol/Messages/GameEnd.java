package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public class GameEnd extends Message{

    private int winner; // 1 = player1, 2 = player2

    public GameEnd(BufferedReader reader) {
        super(MessageType.GAME_END);

        try {
            winner = reader.read();
        } catch (IOException e) {
            winner = -1;
        }
    }

    public GameEnd (int winner) {
        super(MessageType.GAME_END);
        this.winner = winner;
    }

    @Override
    public void send(BufferedWriter writer) throws IOException {
        super.send(writer);
        writer.write(winner);
    }
}
