package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Common.Position;
import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by clayton on 2015-06-06.
 */
public class PuckUpdate extends Message {
    private Position position = new Position();

    // For receiving the update.
    public PuckUpdate (BufferedReader reader) {
        super(MessageType.PUCK_UPDATE);
        try {
            position.x = reader.read();
            position.y = reader.read();
        } catch (IOException e) {
            // Just return the default position if there is a problem.
            position = new Position();
        }
    }

    // For creating an update to send out.
    public PuckUpdate (Position position) {
        super(MessageType.PUCK_UPDATE);
        this.position = new Position(position);
    }

    @Override
    public void send(BufferedWriter writer) throws IOException {
        super.send(writer);
        writer.write(position.x);
        writer.write(position.y);
    }

    public Position getPosition() {
        return position;
    }
}
