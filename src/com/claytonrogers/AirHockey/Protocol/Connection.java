package com.claytonrogers.AirHockey.Protocol;

import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Server.Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by clayton on 2015-06-06.
 */
public class Connection {

    private BufferedReader reader;
    private BufferedWriter writer;
    public Queue<Message> serverMessages = new ConcurrentLinkedQueue<>();

    private final Socket socket;

    private volatile boolean isGood = true;

    public Connection (Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("There was a problem making the connection.");
            isGood = false;
            return;
        }

        new Thread(() -> {
            while (isGood()) {
                Message message = Message.parseMessage(reader);
                if (message == null) {
                    System.out.println("Could not parse the message. Not listening anymore.");
                    isGood = false;
                    return;
                }
                if (Protocol.NET_DEBUG) {
                    System.out.println("Received: " + message.getMessageType());
                }
                serverMessages.add(message);
            }
        }).start();
    }

    public void send (Message message) {
        if (!isGood()) {
            System.out.println("Tried sending a message when not good.");
            return;
        }
        try {
            message.send(writer);
            if (Protocol.NET_DEBUG) {
                System.out.println("Sending: " + message.getMessageType());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an issue sending the message.");
            isGood = false;
        }
    }

    public boolean isGood() {
        return isGood;
    }
}
