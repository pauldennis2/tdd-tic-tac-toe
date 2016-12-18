package com.tiy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dbashizi on 12/14/16.
 */
public class SimpleServer implements Runnable {

    final static int PORT_NUMBER = 8005;
    final static String SHUTDOWN_MESSAGE = "[[SERVER_SHUTDOWN]]";
    final static String CONNECTION_START = "[[CONN_START]]";
    final static String CONNECTION_READY = "[[CONN_READY]]";
    final static String CONNECTION_END = "[[CONN_END]]";
    final static String CONNECTION_INVALID_START = "[[CONN_INVALID_START]]";
    ServerSocket serverListener;

    public SimpleServer() {
        try {
            serverListener = new ServerSocket(PORT_NUMBER);
        } catch (IOException exception) {
            exception.printStackTrace();
            serverListener = null;
        }
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Waiting for connection ...");
                Socket clientSocket = serverListener.accept();

                BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                String firstLine = inputFromClient.readLine();
                if (firstLine == null || firstLine.equals(SHUTDOWN_MESSAGE)) {
                    outputToClient.println("!!! Server Shutting Down !!!");
                    break;
                } else if (firstLine.equals(CONNECTION_START)){
                    outputToClient.println(CONNECTION_READY);
                } else {
                    outputToClient.println(CONNECTION_INVALID_START);
                    // TODO
//                    throw new Exception("create a new unit test to make sure this actually continues and doesn't start a new thread");
                }

                new Thread(
                        new ConnectionHandler(clientSocket, inputFromClient, outputToClient)
                ).start();
            }
            System.out.println("**** Done waiting for connections - shutting down");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void startServer() {
        new Thread(this).start();
    }

    public void stopServer() {
        try {
            // note: a SimpleClient that's been created just for
            // closing down the server doesn't need to closed itself
            // so no call to client.closeClient() here
            SimpleClient client = new SimpleClient("127.0.0.1", PORT_NUMBER, true);
            serverListener.close();
        } catch (IOException exception) {
            System.out.println("***** Unable to close server");
        }
    }
}
