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
    public BufferedWriter writer;

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
        }

        // This thread will constantly watch for new messages from the player and place them
        // on the queue.
        new Thread(() -> {
            while (true) {
                Message message = Message.parseMessage(reader);
                if (message == null) {
                    System.out.println("Could not parse the message.");
                    return;
                }
                if (Protocol.NET_DEBUG) {
                    System.out.println("Received message from client: " + message.getMessageType());
                }
                messageQueue.add(message);
            }
        });
    }


    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
