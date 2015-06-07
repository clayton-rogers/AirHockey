package com.claytonrogers.AirHockey.Client;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.MessageType;
import com.claytonrogers.AirHockey.Protocol.Messages.*;
import com.claytonrogers.AirHockey.Protocol.Protocol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by clayton on 2015-06-06.
 */
public class Client extends JFrame implements MouseMotionListener {

    private static int FRAME_TIME = 17; // just a bit slower than 60 fps

    private Connection serverConnection;
    private final Vector mousePosition = new Vector();

    private BufferedImage puckSprite;
    private BufferedImage playerSprite;
    private BufferedImage opponentSprite;
    private BufferedImage background;

    public Client(String hostname) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize (Protocol.FIELD_WIDTH, Protocol.FIELD_HEIGHT);
        setVisible(true);

        // This sleep is here because otherwise the create buffer will sometimes fail.
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("This should never happen.");
        }

        createBufferStrategy(2);

        // Connect to the server.
        try {
            Socket socket = new Socket(hostname, Protocol.PORT_NUMBER);
            serverConnection = new Connection(socket);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to the server.");
            // Close since the connection is no longer good. if the connection failed.
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }


        // Render all the sprites
        Graphics2D g;
        puckSprite = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        g = puckSprite.createGraphics();
        g.setColor(Color.BLUE);
        g.drawOval(0,0,20,20); // draw blue circle

        playerSprite = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        g = playerSprite.createGraphics();
        g.setColor(Color.BLACK);
        g.drawOval(0,0,30,30); // draw black circle

        opponentSprite = new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
        g = opponentSprite.createGraphics();
        g.setColor(Color.RED);
        g.drawOval(0,0,30,30); // draw red circle

        background = new BufferedImage(Protocol.FIELD_WIDTH, Protocol.FIELD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = background.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, Protocol.FIELD_WIDTH, Protocol.FIELD_HEIGHT);
    }

    private void run() {

        Message message = null;
        while (message == null) {
            message = serverConnection.receivedMessages.poll();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (message.getMessageType() == MessageType.VERSION_REQUEST) {
            Message versionResponse = new VersionResponse(Protocol.PROTOCOL_VERSION);
            serverConnection.send(versionResponse);
        } else {
            System.out.println("Got something other than a version request as the first message.");
            return;
        }

        Vector playerPosition = new Vector();
        Vector puckPosition = new Vector();
        Vector opponentPosition = new Vector();

        boolean gameIsGood = true;
        int winner = 0;

        // Main graphics loop
        while (serverConnection.isGood() && gameIsGood) {
            long frameStartTime = System.currentTimeMillis();

            // Get the new position of the player
            synchronized (mousePosition) {
                playerPosition.assign(mousePosition);
            }

            // Send the player position to the server
            Message playerPositionMessage = new PositionUpdate(playerPosition, PositionUpdate.ObjectType.PLAYER);
            serverConnection.send(playerPositionMessage);

            // Process any messages from the server
            while (true) {
                message = serverConnection.receivedMessages.peek();
                if (message == null) {
                    break;
                }

                switch (message.getMessageType()) {
                    case POSITION_UPDATE:
                        PositionUpdate positionUpdate = (PositionUpdate) message;
                        switch (positionUpdate.getType()) {
                            case OPPONENT:
                                opponentPosition.assign(positionUpdate.getPosition());
                                break;
                            case PUCK:
                                puckPosition.assign(positionUpdate.getPosition());
                                break;
                        }
                        break;
                    case DISCONNECT:
                        gameIsGood = false;
                        break;
                    case GAME_END:
                        winner = ((GameEnd)message).getWinner();
                        gameIsGood = false;
                        break;
                }
                serverConnection.receivedMessages.remove();
            }

            // Draw the screen
            BufferStrategy bf = getBufferStrategy();
            Graphics g = null;
            try {
                g = bf.getDrawGraphics();
                Graphics2D g2 = (Graphics2D) g;

                g2.drawImage(background, null, 0, 0);
                g2.drawImage(puckSprite, null, puckPosition.x, puckPosition.y);

                // TODO finish drawing things.


            } finally {
                if (g != null) {
                    g.dispose();
                }
            }
            bf.show();
            Toolkit.getDefaultToolkit().sync();

            // Wait around for the next frame
            long frameEndTime = System.currentTimeMillis();
            long waitTime = FRAME_TIME - (frameEndTime - frameStartTime);
            if (waitTime < 0L) {
                waitTime = 0L;
            }

            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                // TODO don't really care
            }
        }

        // Tell which player won.
        JOptionPane.showMessageDialog(null, "Player # " + String.valueOf(winner) + " won.");

        // Close since the connection is no longer good. if the connection failed.
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public static void main (String[] args) {
        String hostname = JOptionPane.showInputDialog("Enter server IP:");
        if (hostname.equals("")) {
            hostname = "localhost";
        }
        Client client = new Client(hostname);
        client.run();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mousePosition) {
            Point point = e.getLocationOnScreen();
            mousePosition.x = point.x;
            mousePosition.y = point.y;
        }
    }
}
