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

    public void assign(Vector p) {
        x = p.x;
        y = p.y;
    }

    public void addInPlace(Vector p) {
        x += p.x;
        y += p.y;
    }

    public void subInPlace(Vector p) {
        x -= p.x;
        y -= p.y;
    }

    public Vector scalarMultiply (int scalar) {
        Vector vector = new Vector();
        vector.x = x * scalar;
        vector.y = y * scalar;

        return vector;
    }

    public int magnitude() {
        double mag = 0;
        mag += x * x;
        mag += y * y;
        mag = Math.sqrt(mag);
        return (int) mag;
    }

    public int dotProduct (Vector v) {
        int sum = 0;
        sum += x * v.x;
        sum += y * v.y;
        return sum;
    }
}
