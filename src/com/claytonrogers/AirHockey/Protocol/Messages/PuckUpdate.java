package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by clayton on 2015-06-06.
 */
public class PuckUpdate extends Message {
    private Vector position = new Vector();

    // For receiving the update.
    public PuckUpdate (BufferedReader reader) {
        super(MessageType.PUCK_UPDATE);
        try {
            String temp;
            temp = reader.readLine();
            position.x = Integer.parseInt(temp);
            temp = reader.readLine();
            position.y = Integer.parseInt(temp);
        } catch (IOException e) {
            // Just return the default position if there is a problem.
            position = new Vector();
        }
    }

    // For creating an update to send out.
    public PuckUpdate (Vector position) {
        super(MessageType.PUCK_UPDATE);
        this.position = new Vector(position);
    }

    @Override
    public void send(PrintWriter writer) throws IOException {
        super.send(writer);
        writer.println(position.x);
        writer.println(position.y);
        writer.flush();
    }

    public Vector getPosition() {
        return position;
    }
}
