package com.tiy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.tiy.Status.OWIN;
import static com.tiy.Status.TIE;
import static com.tiy.Status.XWIN;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameRecord {
    String player1;
    String player2;

    int[] moves;
    int moveNum = 0;

    Status result;

    private static List<GameRecord> records = new ArrayList<>();

    public GameRecord (String p1, String p2) {
        player1 = p1;
        player2 = p2;
        moves = new int[9];
        Arrays.fill (moves, 0);
        result = Status.LIVE;
    }

    public void addMove (int move) {
        moves[moveNum] = move;
        moveNum++;
        //I'm trusting no idiot programmer will call me more than 9 times and cause an IndexArrayOutOfBoundsException...
    }

    public void setStatus (Status status) {
        this.result = status;
    }

    public void writeToFile () {
        try {
            File resultsFile = new File("game_results.txt");
            FileWriter fileWriter = new FileWriter(resultsFile);
            fileWriter.write(player1);
            fileWriter.write(player2);

            for (int i : moves) {
                if (i > 0) {
                    fileWriter.write(i + " ");
                }
            }
            fileWriter.write(result.toString());
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println("Problem while writing to file");
            ex.printStackTrace();
        }
    }

    public static void readAllResultsFromFile () {
        try {
            File resultsFile = new File("game_results.txt");
            Scanner fileScanner = new Scanner(resultsFile);
            while (fileScanner.hasNext()) {
                String player1name = fileScanner.nextLine();
                String player2name = fileScanner.nextLine();
                GameRecord record = new GameRecord(player1name, player2name);

                String[] moveStrings = fileScanner.nextLine().split(" ");
                for (String move : moveStrings) {
                    record.addMove(Integer.parseInt(move));
                }
                String result = fileScanner.nextLine();
                Status gameResult = null;
                switch (result) {
                    case "XWIN":
                        gameResult = XWIN;
                        break;
                    case "OWIN":
                        gameResult = OWIN;
                        break;
                    case "TIE":
                        gameResult = TIE;
                        break;
                    default:
                        System.out.println("error");
                        break;
                }
                record.setStatus(gameResult);
                records.add(record);
            }
        } catch (IOException ex) {
            System.out.println("Problem reading from file");
            ex.printStackTrace();
        }
    }

    public static void displayRecordForUser (String userName) {
        for (GameRecord record : records) {
            if (record.player1.equals(userName) || record.player2.equals(userName)) {
                //found a match
                System.out.println(record);
            }
        }
    }

    public String toString () {
        String response = "";
        response += player1 + " vs " + player2 + "\n";
        /*
        if (result == XWIN) {
            response += player1 + " won.";
        } else if (result == OWIN) {
            response += player2 + " won.";
        } else if (result == TIE) {
            response += "Game was a tie.";
        } else {
            System.out.println("Error in gameRecord.toString()");
        }*/

        switch (result) {
            case XWIN:
                response += player1 + " won.";
                break;
            case OWIN:
                response += player2 + " won.";
                break;
            case TIE:
                response += "Game was a tie.";
                break;
            case LIVE:
                response += "Game is still live.";
                break;
            default:
                System.out.println("Error in gameRecord.toString()");
                break;
        }
        return response;
    }
}
