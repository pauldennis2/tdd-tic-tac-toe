package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by erronius on 12/18/2016.
 */
public class TTTMoveCommunicationTest {

    private SimpleServer myServer;
    private SimpleClient myClient;

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
    public void testMoveCommunication () throws Exception {
        for (int i = 0; i < 20; i++) {
            String serverResponse = myClient.sendMessage("3");
            //assertEquals(ECHO_TOKEN + testMessage, serverResponse);
            System.out.println("My move was 5. Server's move was " + serverResponse);
            assertNotEquals(5, Integer.parseInt(serverResponse));
        }
    }

}