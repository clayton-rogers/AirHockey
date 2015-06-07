package com.claytonrogers.AirHockey.Common;

/**
 * Created by clayton on 2015-06-06.
 */
public class Vector {
    public int x = 0;
    public int y = 0;

    public Vector() {}
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Vector(Vector p) {
        x = p.x;
        y = p.y;
    }

    public void add(Vector p) {
        x += p.x;
        y += p.y;
    }

    public void sub(Vector p) {
        x -= p.x;
        y -= p.y;
    }
}
