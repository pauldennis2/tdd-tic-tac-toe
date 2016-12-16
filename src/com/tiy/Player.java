package com.tiy;

/**
 * Created by erronius on 12/15/2016.
 */
public class Player {

    String name;
    char token;

    public Player (String name, char token) {
        this.name = name;
        this.token = token;
    }

    public String getName () {
        return name;
    }

    public char getToken () {
        return token;
    }
}
