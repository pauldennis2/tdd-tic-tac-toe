package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by dbashizi on 12/15/16.
 */
public class SimpleServerTest {

    private SimpleServer myServer;
    private SimpleClient myClient;
    private String testMessage = "sample test message";
    private final String ECHO_TOKEN = "ECHO::";
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 8005;

    @Before
    public void setUp() throws Exception {
        myServer = new SimpleServer();
        myServer.startServer();
        myClient = new SimpleClient(SERVER_IP, SERVER_PORT);
    }

    @After
    public void tearDown() throws Exception {
        myClient.closeClient();
        myServer.stopServer();
    }

    @Test
    public void testSendingSingleMessage() throws IOException {
        String serverResponse = myClient.sendMessage(testMessage);
        assertEquals(ECHO_TOKEN + testMessage, serverResponse);
    }

    @Test
    public void testSendMultipleMessages() throws IOException {
        String serverResponse = myClient.sendMessage(testMessage);
        assertEquals(ECHO_TOKEN + testMessage, serverResponse);

        serverResponse = myClient.sendMessage(testMessage + "2");
        assertEquals(ECHO_TOKEN + testMessage + "2", serverResponse);
    }

    @Test
    public void testMultipleClients() throws IOException {
        String serverResponse = myClient.sendMessage(testMessage);
        assertEquals(ECHO_TOKEN + testMessage, serverResponse);

        SimpleClient secondClient = new SimpleClient(SERVER_IP, SERVER_PORT);
        serverResponse = secondClient.sendMessage(testMessage + "2");
        assertEquals(ECHO_TOKEN + testMessage + "2", serverResponse);
    }
}