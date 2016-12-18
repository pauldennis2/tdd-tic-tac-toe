package com.tiy;

import java.io.*;
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

    public static final boolean DEBUG = false;

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
        /*try {
            File resultsFile = new File("game_results.txt");

            FileWriter fileWriter = new FileWriter(resultsFile);

            fileWriter.write(player1 + "\n");
            fileWriter.write(player2 + "\n");

            for (int i : moves) {
                if (i > 0) {
                    fileWriter.write(i + " ");
                }
            }
            fileWriter.write("\n");
            fileWriter.write(result.toString());
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println("Problem while writing to file");
            ex.printStackTrace();
        }*/

        try {
            RandomAccessFile fileWriter = new RandomAccessFile("game_results.txt", "rw"); //"rw" = mode, "read and write"
            fileWriter.seek(fileWriter.length());
            fileWriter.writeUTF(player1 + "\n");
            fileWriter.writeUTF(player2 + "\n");

            for (int i : moves) {
                if (i > 0) {
                    fileWriter.writeUTF(i + " ");
                }
            }
            fileWriter.writeUTF("\n");
            fileWriter.writeUTF(result.toString() + "\n");
            fileWriter.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static List<GameRecord> getRecords () {
        return records;
    }

    public static List<GameRecord> readAllResultsFromFile (String filename) {
        try {
            File resultsFile = new File(filename);
            Scanner fileScanner = new Scanner(resultsFile);
            while (fileScanner.hasNext()) {
                String player1name = fileScanner.nextLine().trim();
                String player2name = fileScanner.nextLine().trim();
                GameRecord record = new GameRecord(player1name, player2name);
                //[ \t]+ regex to match any non newline whitespace
                String[] moveStrings = fileScanner.nextLine().split("[ \t]+");
                for (String move : moveStrings) {
                    try {
                        record.addMove(Integer.parseInt(move.trim()));
                    } catch (NumberFormatException ex) {
                        if (DEBUG) {
                            System.out.println("Error parsing token to int. Token: *" + move + "*");
                        }
                    }
                }
                String result = fileScanner.nextLine().trim();
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
        return records;
    }

    public static void displayRecordForUser (String userName) {
        System.out.println("Here are all the records for " + userName + ":");
        boolean atLeastOneRecordFound = false;
        for (GameRecord record : records) {
            if (record.player1.equals(userName) || record.player2.equals(userName)) {
                atLeastOneRecordFound = true;
                System.out.println(record);
            }
        }
        if (!atLeastOneRecordFound) {
            System.out.println("(No records found)");
        }
    }

    public String toString () {
        String response = "";
        response += player1 + " vs " + player2 + "\n";

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

    public int[] getMoves () {
        return moves;
    }

    public String getPlayer1 () {
        return player1;
    }
    public String getPlayer2 () {
        return player2;
    }
    public Status getResult () {
        return result;
    }
}
