package com.claytonrogers.AirHockey.Server;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by clayton on 2015-06-06.
 */
public class Player implements Closeable {

    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private volatile boolean isGood = true;

    public Vector position = new Vector();

    public final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();

    public Player(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            reader = null;
            writer = null;
            e.printStackTrace();
            System.out.println("Could not create player reader and writer.");
            return;
        }

        // This thread will constantly watch for new messages from the player and place them
        // on the queue.
        new Thread(() -> {
            while (true) {
                Message message = Message.parseMessage(reader);
                if (message == null) {
                    System.out.println("Could not parse the message. Not listening anymore.");
                    isGood = false;
                    return;
                }
                if (Protocol.NET_DEBUG) {
                    System.out.println("Received message from client: " + message.getMessageType());
                }
                messageQueue.add(message);
            }
        }).start();
    }

    public void send (Message message) {
        if (!isGood) {
            return;
        }
        try {
            message.send(writer);
            if (Protocol.NET_DEBUG) {
                System.out.println("Sending message to player: " + message.getMessageType());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an issue sending a message to the player.");
            e.printStackTrace();
            isGood = false;
        }
    }

    public boolean isGood() {
        return isGood;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
