package com.tiy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by erronius on 12/15/2016.
 */
public class GameRecordTest {

    GameRecord record;
    @Before
    public void setUp() throws Exception {
        record = new GameRecord ("Jon", "Bob");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFullGameRecord () {
        record.addMove(5);
        assertEquals(true, record.toString().contains("Jon"));
        assertEquals(true, record.toString().contains("Bob"));
        assertEquals(true, record.toString().contains("5"));
    }

}