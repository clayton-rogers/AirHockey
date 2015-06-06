package com.claytonrogers.AirHockey.Common;

/**
 * Created by clayton on 2015-06-06.
 */
public class Position {
    public int x = 0;
    public int y = 0;

    public Position() {}
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(Position p) {
        x = p.x;
        y = p.y;
    }

    public void add(Position p) {
        x += p.x;
        y += p.y;
    }

    public void sub(Position p) {
        x -= p.x;
        y -= p.y;
    }
}
