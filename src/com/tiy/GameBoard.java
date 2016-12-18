package com.tiy;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameBoard {
    char[][] board;
    GameRecord record;

    public GameBoard (String p1, String p2) {
        board = new char[][] {{' ',' ',' '}, {' ',' ',' '}, {' ',' ',' '}};
        record = new GameRecord (p1, p2);
    }

    public GameRecord getRecord () {
        return record;
    }

    public Status getStatus () {
        char rowWinner = this.getRowWinner();
        char colWinner = this.getColWinner();
        char diagWinner = this.getDiagWinner();
        char winningToken = ' ';
        //Again we can afford to do this sequentially/unrelated because under normal game circumstances
        //(Like Highlander, vis a vis wins) There can be only one
        if (rowWinner != ' ') {
            winningToken = rowWinner;
        }
        if (colWinner != ' ') {
            winningToken = colWinner;
        }
        if (diagWinner != ' ') {
            winningToken = diagWinner;
        }

        if (winningToken != ' ') {
            if (winningToken == 'X') {
                record.setStatus(Status.XWIN);
                return Status.XWIN;
            } else if (winningToken == 'O') {
                record.setStatus(Status.OWIN);
                return Status.OWIN;
            } else {
                System.out.println("You shouldn't be here. WTF is wrong with you?");
            }
        } else {
            if (this.isFull()) {
                record.setStatus(Status.TIE);
                return Status.TIE;
            }
            return Status.LIVE;
        }
        return null;
    }

    private boolean isFull () {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public char getRowWinner () {
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == ' ') {
                continue;
            } else if (board[row][1] == board[row][0]) {
                if (board[row][2] == board[row][0]) {
                    return board[row][0];
                }
            }
        }
        return ' ';
    }

    public char getColWinner () {
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == ' ') {
                continue;
            } else if (board[1][col] == board[0][col]) {
                if (board[2][col] == board[0][col]) {
                    return board[0][col];
                }
            }
        }
        return ' ';
    }

    public char getDiagWinner () {
        //From topleft
        if (board[0][0] != ' ') {
            if (board[1][1] == board[0][0]) {
                if (board[2][2] == board[0][0]) {
                    return board[0][0];
                }
            }
        }
        //From topright
        if (board[0][2] != ' ') {
            if (board[1][1] == board[0][2]) {
                if (board[2][0] == board[0][2]) {
                    return board[0][2];
                }
            }
        }
        return ' ';
    }

    public char getToken (int row, int col) {
        return board[row][col];
    }

    public void placeToken (int row, int col, char token) throws InvalidMoveException {
        if (board[row][col] == ' ') {
            board[row][col] = token;
            record.addMove(row*3 + col + 1);
        } else {
            throw new InvalidMoveException("Can't move there, already taken");
        }
    }

    public String toString () {
        return board[0][0] + "|" + board[0][1] + "|" + board[0][2] + "\n" +
               "-----\n" +
               board[1][0] + "|" + board[1][1] + "|" + board[1][2] + "\n" +
               "-----\n" +
               board[2][0] + "|" + board[2][1] + "|" + board[2][2];

    }

    public void writeToFile () {
        record.writeToFile();
    }
}
