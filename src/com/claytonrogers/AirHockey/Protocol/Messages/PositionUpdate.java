package com.claytonrogers.AirHockey.Protocol.Messages;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Used the send the position of one of the many objects.
 *
 * <br><br>Created by clayton on 2015-06-07.
 */
public class PositionUpdate extends Message{
    public enum ObjectType {
        PLAYER,
        OPPONENT,
        PUCK;

        public static ObjectType parse (String s) {
            int a = Integer.parseInt(s);
            return ObjectType.values()[a];
        }
    }

    private Vector position = new Vector();
    private ObjectType type;

    // For receiving the update.
    public PositionUpdate (BufferedReader reader) {
        super(MessageType.POSITION_UPDATE);
        try {
            String temp;
            temp = reader.readLine();
            position.x = Double.parseDouble(temp);
            temp = reader.readLine();
            position.y = Double.parseDouble(temp);
            temp = reader.readLine();
            type = ObjectType.parse(temp);
        } catch (IOException e) {
            // Just return the default position if there is a problem.
            position = new Vector();
        }
    }

    // For creating an update to send out.
    public PositionUpdate (Vector position, ObjectType type) {
        super(MessageType.POSITION_UPDATE);
        this.position = new Vector(position);
        this.type = type;
    }

    @Override
    public void send(PrintWriter writer) throws IOException {
        super.send(writer);
        writer.println(position.x);
        writer.println(position.y);
        writer.println(type.ordinal());
        writer.flush();
    }

    public Vector getPosition() {
        return position;
    }

    public ObjectType getType() {
        return type;
    }
}
