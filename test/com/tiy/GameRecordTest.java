package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameRecordTest {

    GameBoard board;
    @Before
    public void setUp() throws Exception {
        board = new GameBoard("Jack", "Jill");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testStandaloneGameRecord () {
        GameRecord record = new GameRecord ("Jon", "Bob");
        record.addMove(5);
        assertEquals(true, record.toString().contains("Jon"));
        assertEquals(true, record.toString().contains("Bob"));
    }

    @Test
    public void testWrappedGameRecord () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(1, 1, 'X');
        String record = board.getRecord().toString();
        board.getStatus();
        assertEquals(false, record.contains("Jack won"));
        board.placeToken(2, 2, 'X');
        board.getStatus();
        record = board.getRecord().toString();
        assertEquals(true, record.contains("Jack won"));
    }

    @Test
    public void testTiedGameRecord () throws InvalidMoveException {
        board.placeToken(0, 0, 'X');
        board.placeToken(0, 1, 'O');
        board.placeToken(0, 2, 'X');

        board.placeToken(1, 0, 'O');
        board.placeToken(1, 1, 'O');
        board.placeToken(1, 2, 'X');

        board.placeToken(2, 0, 'X');
        board.placeToken(2, 1, 'X');
        board.placeToken(2, 2, 'O');
        board.getStatus();
        String record = board.getRecord().toString();
        assertEquals (true, record.contains("tie"));
    }

    @Test
    public void testRecordReadingFromFile () {
        GameRecord.readAllResultsFromFile("test_results.txt");
        List<GameRecord> records = GameRecord.getRecords();
        assertEquals(3, records.size());
        GameRecord firstRecord = records.get(0);
        GameRecord secondRecord = records.get(1);
        GameRecord thirdRecord = records.get(2);
        assertEquals("Phob", firstRecord.getPlayer2());
        assertEquals("Paul", secondRecord.getPlayer1());
        assertEquals("Sally", thirdRecord.getPlayer1());
        assertEquals(4, firstRecord.getMoves()[2]); //Third move of the first game was 4
    }

    @Test
    public void testUserInfoPrinting () {
        GameRecord.readAllResultsFromFile("test_results.txt");
        GameRecord.displayRecordForUser("Paul");
        GameRecord.displayRecordForUser("Big Giant Head");
        GameRecord.displayRecordForUser("Sally");
        GameRecord.displayRecordForUser("Phob");
    }

}