package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public class OpponentUpdate extends Message{
    private Vector position = new Vector();

    // For receiving the update.
    public OpponentUpdate (BufferedReader reader) {
        super(MessageType.OPPONENT_UPDATE);
        try {
            position.x = reader.read();
            position.y = reader.read();
        } catch (IOException e) {
            // Just return the default position if there is a problem.
            position = new Vector();
        }
    }

    // For creating an update to send out.
    public OpponentUpdate (Vector position) {
        super(MessageType.OPPONENT_UPDATE);
        this.position = new Vector(position);
    }

    @Override
    public void send(BufferedWriter writer) throws IOException {
        super.send(writer);
        writer.write(position.x);
        writer.write(position.y);
        writer.flush();
    }

    public Vector getPosition() {
        return position;
    }
}
