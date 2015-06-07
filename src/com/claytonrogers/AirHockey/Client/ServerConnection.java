package com.claytonrogers.AirHockey.Client;

import com.claytonrogers.AirHockey.Protocol.MessageType;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Protocol;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by clayton on 2015-06-06.
 */
public class ServerConnection {

    private BufferedReader reader;
    private BufferedWriter writer;
    public Queue<Message> serverMessages = new ConcurrentLinkedQueue<>();

    private Socket socket;

    private volatile boolean isGood = true;

    public ServerConnection (String hostname) {
        try {
            socket = new Socket(hostname, Protocol.PORT_NUMBER);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There was a problem connecting to the server.");
            isGood = false;
        }

        new Thread(() -> {
            while (true) {
                Message message = Message.parseMessage(reader);
                if (message == null) {
                    System.out.println("Could not parse the message.");
                    return;
                }
                if (Protocol.NET_DEBUG) {
                    System.out.println("Received message from server: " + message.getMessageType());
                }
                serverMessages.add(message);
            }
        }).start();
    }

    public boolean isGood() {
        return isGood;
    }

    public void send (Message message) {
        try {
            message.send(writer);
            if (Protocol.NET_DEBUG) {
                System.out.println("Sending message to server: " + message.getMessageType());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an issue sending a message to the server.");
        }
    }
}
