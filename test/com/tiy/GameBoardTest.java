package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameBoardTest {
    GameBoard board;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        board = new GameBoard();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBoardCreation () {
        assertEquals(Status.LIVE, board.getStatus());
        assertEquals(' ', board.getToken(1, 1));
    }

    @Test
    public void testTokenPlacement () throws InvalidMoveException {
        board.placeToken(1, 1, 'X');
        assertEquals('X', board.getToken(1, 1));
        board.placeToken(2, 2, 'O');
        assertEquals('O', board.getToken(2, 2));
    }

    @Test
    public void testBoardPrint () {
        System.out.println(board);
    }

    @Test
    public void testBadTokenPlacement () throws InvalidMoveException {
        board.placeToken(1, 1, 'X');
        expectedException.expect(InvalidMoveException.class);
        board.placeToken(1, 1, 'O'); //could be any token, doesn't matter
    }

    @Test
    public void testBoardWin () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(1, 1, 'X');
        board.placeToken(2, 2, 'X');
        assertEquals(Status.XWIN, board.getStatus());
    }

    @Test
    public void testBoardNotWon () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(1, 1, 'X');
        board.placeToken(2, 2, 'O');
        assertEquals(Status.LIVE, board.getStatus());
    }

    @Test
    public void testRowWinner () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(1, 0, 'X');
        board.placeToken(2, 0, 'X');
        assertEquals(' ', board.getRowWinner());
        board = new GameBoard();
        board.placeToken(0, 0, 'X');
        board.placeToken(0, 1, 'X');
        board.placeToken(0, 2, 'X');
        assertEquals('X', board.getRowWinner());
    }

    @Test
    public void testColWinner () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(0, 1, 'X');
        board.placeToken(0, 2, 'X');
        assertEquals(' ', board.getColWinner());
        board = new GameBoard();
        board.placeToken(0, 1, 'X');
        board.placeToken(1, 1, 'X');
        board.placeToken(2, 1, 'X');
        assertEquals('X', board.getColWinner());
    }

    @Test
    public void testDiagWinner () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(1, 0, 'X');
        board.placeToken(2, 0, 'X');
        assertEquals(' ', board.getRowWinner());
        board = new GameBoard();
        board.placeToken(0, 0, 'X');
        board.placeToken(1, 1, 'X');
        board.placeToken(2, 2, 'X');
        System.out.println(board);
        assertEquals('X', board.getDiagWinner());
    }

    @Test
    public void testBoardTie () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(0, 1, 'O');
        board.placeToken(0, 2, 'X');

        board.placeToken(1, 0, 'O');
        board.placeToken(1, 1, 'O');
        board.placeToken(1, 2, 'X');

        board.placeToken(2, 0, 'X');
        board.placeToken(2, 1, 'X');
        board.placeToken(2, 2, 'O');
        assertEquals(Status.TIE, board.getStatus());
    }

}