package com.tiy;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameRunner {

    public static final boolean DEBUG = true;

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8005;

    private SimpleServer myServer;
    private SimpleClient myClient;
    private String hostUserName;
    private String clientUserName;
    private GameBoard networkBoard;
    private boolean hosting;

    SafeScanner scanner;

    public static void main(String[] args) {
        new GameRunner().mainMenu();
    }

    public GameRunner () {
        scanner = new SafeScanner(System.in);
    }

    public void mainMenu () {
        System.out.println("Welcome to Tic Tac Toe Main Menu");
        System.out.println("Options:");
        System.out.println("1. Play a game of Tic Tac Toe");
        System.out.println("2. Look up match history");
        System.out.println("3. Play network game");
        System.out.println("4. Exit");
        //int userChoice = scanner.nextIntInRange(1, 4);
        int userChoice = 3;

        switch (userChoice) {
            case 1:
                startGame();
                mainMenu();
                break;
            case 2:
                System.out.println("Please enter username (must be an exact match to previous records)");
                String userName = scanner.nextStringSafe();
                //String userName = new Scanner(System.in).nextLine();
                GameRecord.readAllResultsFromFile("game_results.txt");
                GameRecord.displayRecordForUser(userName);
                mainMenu();
                break;
            case 3:
                playNetworkGame();
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    public void startGame () {
        Player player1;
        Player player2;

        System.out.println("Player 1 (X) please enter name");
        String p1name = scanner.nextStringSafe();
        System.out.println("Player 2 (O) please enter name");
        String p2name = scanner.nextStringSafe();

        player1 = new Player(p1name, 'X');
        player2 = new Player(p2name, 'O');

        System.out.println("Key:");
        System.out.println("123");
        System.out.println("456");
        System.out.println("789");

        System.out.println("Game between " + player1.getName() + "(" + player1.getToken() + ") and "
                + player2.getName() + "(" + player2.getToken() + ") ready to start. Commence?");
        scanner.waitForInput();
        playGame(player1, player2);
    }

    public void playNetworkGame () {
        System.out.println("Enter username:");
        String userName = scanner.nextStringSafe();
        System.out.println("Host or client? (h/c)");
        String response = scanner.nextStringSafe().toLowerCase();
        if (response.contains("h")) {
            hostUserName = userName;
            hosting = true;
            printNetworkGameInfo(true);
            myServer = new SimpleServer(this);
            myServer.startServer();
        } else if (response.contains("c")) {
            clientUserName = userName;
            hosting = false;
            printNetworkGameInfo(false);
            myClient = new SimpleClient(SERVER_IP, SERVER_PORT, this);
            try {
                myClient.sendMessage(SimpleServer.CONNECTION_START);
                myClient.sendMessage(clientUserName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Problem reading your response. Must contain 'h' or 'c'");
            playNetworkGame();
        }
    }

    public String receiveInfoFromNetworkAndRespond (String info) {
        if (DEBUG) {
            System.out.println("Received info: " + info);
        }
        try {
            int receivedMoveLocation = Integer.parseInt(info);
            int row = (receivedMoveLocation - 1) / 3;
            int col = (receivedMoveLocation - 1) % 3;
            try {
                char token;
                //We want the other player's token here
                if (hosting) {
                    token = 'O';
                } else {
                    token = 'X';
                }
                networkBoard.placeToken(row, col, token);
                checkNetworkGameStatus();
                System.out.println(networkBoard);
                System.out.println("Waiting for other player...");
            } catch (InvalidMoveException ex) {
                ex.printStackTrace();
                //Illegal moves should already have been screened out.
                //If we're here, we're in a bad place.
                throw new AssertionError("Invalid move from network. Exiting");
            }
            System.out.println(networkBoard);

            boolean moveIsGood = false;
            int myMoveLocation = -1;
            while (!moveIsGood) {
                System.out.println("Where would you like to move?");
                myMoveLocation = scanner.nextIntInRange(1, 9);
                int myRow = (myMoveLocation - 1) / 3;
                int myCol = (myMoveLocation - 1) % 3;

                try {
                    char token;
                    if (hosting) {
                        token = 'X';
                    } else {
                        token = 'O';
                    }
                    networkBoard.placeToken(myRow, myCol, token);
                    checkNetworkGameStatus();
                    System.out.println(networkBoard);
                    System.out.println("Waiting for other player...");
                    moveIsGood = true;
                } catch (InvalidMoveException ex) {
                    System.out.println(ex);
                }
            }
            if (!hosting) {
                try {
                    myClient.sendMessage("" + myMoveLocation);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return "" + myMoveLocation;
        } catch (NumberFormatException ex) {
            if (info.equals(SimpleServer.CONNECTION_START)) {
                return hostUserName;
            }
            //Should only be a String username at the first step
            if (hosting) {
                clientUserName = info;
            } else {
                hostUserName = info;
            }
            System.out.println("Starting a game between " + clientUserName + " (client) and " + hostUserName + " (host)");
            networkBoard = new GameBoard(clientUserName, hostUserName);
            //Make a move
            //System.out.println("Where would you like to move?");
            if (hosting) {
                System.out.println(networkBoard);
                System.out.println("Enter first move");
                boolean moveIsGood = false;
                int myMoveLocation = -1;
                while (!moveIsGood) {
                    myMoveLocation = scanner.nextIntInRange(1, 9);
                    int row = (myMoveLocation - 1) / 3;
                    int col = (myMoveLocation - 1) % 3;
                    char token;
                    if (hosting) {
                        token = 'X';
                    } else {
                        token = 'O';
                    }
                    try {
                        networkBoard.placeToken(row, col, token);
                        moveIsGood = true;
                        System.out.println(networkBoard);
                        System.out.println("Waiting for other player...");
                        return "" + myMoveLocation;
                    } catch (InvalidMoveException exception) {
                        System.out.println(exception);
                    }
                }
            }
            return null;
            //return "" + movemove
        }
    }

    public void checkNetworkGameStatus () {
        Status status = networkBoard.getStatus();

        switch (status) {
            case LIVE:
                break;
            case XWIN:
                if (hosting) {
                    System.out.println("Congrats, you win!");
                    myServer.stopServer();
                } else {
                    System.out.println("Sorry, you lost. Loser.");
                    try {
                        myClient.closeClient();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case OWIN:
                if (hosting) {
                    System.out.println("Congrats, you win - NOT! You lose.");
                    myServer.stopServer();
                } else {
                    System.out.println("Huh, looks like you won.");
                    try {
                        myClient.closeClient();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case TIE:
                System.out.println("Tie game");
                if (hosting) {
                    myServer.stopServer();
                } else {
                    try {
                        myClient.closeClient();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
        }
    }

    public static void printNetworkGameInfo(boolean hosting) {
        System.out.println("Network games require one person to host and one player to be the client.");
        System.out.println("Host goes first and plays as X");
        System.out.println("(Client goes second and plays as O)");
        if (hosting) {
            System.out.println("You are hosting this game.");
        } else {
            System.out.println("You are the client. Other player is hosting.");
        }
    }

    public void playGame (Player player1, Player player2) {
        Player activePlayer = player1;
        Player nonActivePlayer = player2;
        GameBoard board = new GameBoard(player1.getName(), player2.getName());
        boolean playing = true;
        while (playing) {
            System.out.println(activePlayer.getName() + "'s turn (" + activePlayer.getToken() + ")");
            System.out.println(board);
            System.out.println("Enter move");
            int move = scanner.nextIntInRange(1, 9);
            int row = (move - 1) / 3;
            int col = (move - 1) % 3;
            boolean moveIsGood = false;
            while (!moveIsGood) {
                try {
                    board.placeToken(row, col, activePlayer.getToken());
                    moveIsGood = true;
                } catch (InvalidMoveException ex) {
                    System.out.println(ex);
                }
            }
            switch (board.getStatus()) {
                case XWIN:
                    System.out.println(player1.getName() + " wins");
                    System.out.println(board);
                    board.writeToFile();
                    playing = false;
                    break;
                case OWIN:
                    System.out.println(player2.getName() + " wins");
                    System.out.println(board);
                    board.writeToFile();
                    playing = false;
                    break;
                case TIE:
                    System.out.println("Game tied");
                    System.out.println(board);
                    board.writeToFile();
                    playing = false;
                    break;
                case LIVE:
                    System.out.println("Next round!");
                    break;
            }
            //Swap players
            activePlayer = nonActivePlayer;
            if (activePlayer.equals(player1)) {
                nonActivePlayer = player2;
            } else {
                nonActivePlayer = player1;
            }
        } //End game loop
        System.out.println("Play again?");
        boolean playAgain = scanner.nextYesNoAnswer();
        if (playAgain) {
            playGame(player1, player2);
        }
    } //End playGame()


}
