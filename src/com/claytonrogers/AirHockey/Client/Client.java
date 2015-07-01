package com.claytonrogers.AirHockey.Client;

import com.claytonrogers.AirHockey.Common.Vector;
import com.claytonrogers.AirHockey.Protocol.Connection;
import com.claytonrogers.AirHockey.Protocol.MessageType;
import com.claytonrogers.AirHockey.Protocol.Messages.*;
import com.claytonrogers.AirHockey.Protocol.Messages.PositionUpdate.ObjectType;
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
import java.net.UnknownHostException;

/**
 * Main client class which creates the window, gets mouse position, updates entities on the screen,
 * and communicates with the server for updates.
 *
 * <br><br> Created by clayton on 2015-06-06.
 */
final class Client extends JFrame implements MouseMotionListener {

    private static final int FRAME_TIME = 17; // just a bit slower than 60 fps
    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static final Color CLEAR = new Color(0,0,0,0);
    private static final int BORDER_SIZE = 18; // pixels

    private Connection serverConnection;
    private final Vector mousePosition = new Vector();

    private final BufferedImage puckSprite;
    private final BufferedImage playerSprite;
    private final BufferedImage opponentSprite;
    private final BufferedImage background;

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize (Protocol.FIELD_WIDTH+BORDER_SIZE, Protocol.FIELD_HEIGHT+BORDER_SIZE);
        setVisible(true);
        addMouseMotionListener(this);

        // This sleep is here because otherwise the create buffer will sometimes fail.
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("This should never happen.");
        }

        createBufferStrategy(2);

        // Render all the sprites
        Graphics2D g;
        puckSprite = new BufferedImage(Protocol.PUCK_RADIUS*2,
                                       Protocol.PUCK_RADIUS*2,
                                       BufferedImage.TYPE_INT_ARGB);
        g = puckSprite.createGraphics();
        g.setColor(CLEAR);
        g.fillRect(0,0,
                Protocol.PUCK_RADIUS*2,
                Protocol.PUCK_RADIUS*2);
        g.setColor(Color.BLUE);
        g.fillOval(0, 0,
                Protocol.PUCK_RADIUS*2,
                Protocol.PUCK_RADIUS*2); // draw blue circle

        playerSprite = new BufferedImage(Protocol.PLAYER_RADIUS*2,
                                         Protocol.PLAYER_RADIUS*2,
                                         BufferedImage.TYPE_INT_ARGB);
        g = playerSprite.createGraphics();
        g.setColor(CLEAR);
        g.fillRect(0,0,
                Protocol.PLAYER_RADIUS*2,
                Protocol.PLAYER_RADIUS*2);
        g.setColor(Color.BLACK);
        g.fillOval(0, 0,
                Protocol.PLAYER_RADIUS*2,
                Protocol.PLAYER_RADIUS*2); // draw black circle

        opponentSprite = new BufferedImage(Protocol.PLAYER_RADIUS*2,
                                           Protocol.PLAYER_RADIUS*2,
                                           BufferedImage.TYPE_INT_ARGB);
        g = opponentSprite.createGraphics();
        g.setColor(CLEAR);
        g.fillRect(0,0,
                Protocol.PLAYER_RADIUS*2,
                Protocol.PLAYER_RADIUS*2);
        g.setColor(Color.RED);
        g.fillOval(0, 0,
                Protocol.PLAYER_RADIUS*2,
                Protocol.PLAYER_RADIUS*2); // draw red circle

        background = new BufferedImage(Protocol.FIELD_WIDTH+BORDER_SIZE,
                                       Protocol.FIELD_HEIGHT+BORDER_SIZE,
                                       BufferedImage.TYPE_INT_RGB);
        g = background.createGraphics();
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0,
                Protocol.FIELD_WIDTH+BORDER_SIZE,
                Protocol.FIELD_HEIGHT+BORDER_SIZE);
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
            PositionUpdate player = new PositionUpdate(playerPosition, ObjectType.PLAYER);
            serverConnection.send(player);

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
                g2.drawImage(puckSprite, null, (int)puckPosition.x, (int)puckPosition.y);
                g2.drawImage(playerSprite, null, (int)playerPosition.x, (int)playerPosition.y);
                g2.drawImage(opponentSprite, null, (int)opponentPosition.x, (int)opponentPosition.y);

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
        if (winner == 0) {
            JOptionPane.showMessageDialog(this, "Disconnected from server. There was no winner.");
        } else {
            JOptionPane.showMessageDialog(null, "Player # " + winner + " won.");
        }

        // Close since the connection is no longer good. if the connection failed.
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void connect (String hostname) {
        // Connect to the server.
        try {
            Socket socket = new Socket(hostname, Protocol.PORT_NUMBER);
            serverConnection = new Connection(socket);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "Could not find the server.");
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not connect to the server.");
            // Close since the connection is no longer good. if the connection failed.
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    public static void main (String[] args) {
        String hostname = JOptionPane.showInputDialog("Enter server IP:");
        if (hostname.isEmpty()) {
            hostname = "localhost";
        }
        Client client = new Client();
        client.connect(hostname);
        client.run();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mousePosition) {
            mousePosition.x = e.getX() - Protocol.PLAYER_RADIUS;
            mousePosition.y = e.getY() - Protocol.PLAYER_RADIUS;
        }
    }
}
