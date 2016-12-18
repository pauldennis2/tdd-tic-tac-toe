package com.tiy;

import java.util.Scanner;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameRunner {

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
        int userChoice = scanner.nextIntInRange(1, 4);

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
        System.out.println("Host or client? (h/c)");
        String response = scanner.nextStringSafe();
        if (response.contains("h")) {
            hostNetworkGame();
        } else if (response.contains("c")) {
            clientNetworkGame();
        } else {
            System.out.println("Problem reading your response. Must contain 'h' or 'c'");
            playNetworkGame();
        }
    }

    public void hostNetworkGame () {
        System.out.println("Enter username:");
        String userName = scanner.nextStringSafe();
        printNetworkGameInfo(true);
        //GameBoard board = new GameBoard(userName, clientName);
    }

    public void clientNetworkGame() {
        System.out.println("Enter username:");
        String userName = scanner.nextStringSafe();
        printNetworkGameInfo(false);
        //GameBoard board = new GameBoard(hostName, userName);
        boolean playing = true;
        while (playing) {
            System.out.println("");
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
