package com.claytonrogers.AirHockey.Client;

import com.claytonrogers.AirHockey.Protocol.MessageType;
import com.claytonrogers.AirHockey.Protocol.Messages.Message;
import com.claytonrogers.AirHockey.Protocol.Protocol;
import com.claytonrogers.AirHockey.Server.Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by clayton on 2015-06-06.
 */
public class ServerConnection {

    public BufferedReader reader;
    public BufferedWriter writer;
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
            Message message = Message.parseMessage(reader);
            if (message.getMessageType() == MessageType.NULL) {
                isGood = false;
            }
            serverMessages.add(message);
        });
    }

    public boolean isGood() {
        return isGood;
    }
}
