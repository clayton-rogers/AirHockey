package com.claytonrogers.AirHockey.Common;

import static java.lang.Math.toDegrees;
import static java.lang.Math.atan2;

/**
 * Two dimensional vector class which supports all the basic operations. Some operations come in two
 * forms:
 * <br><br>
 * {@code c = a.add(b); }
 * <br>
 * - 'c' is now a new vector which is the sum of 'a' and 'b', but 'a' and 'b' have not been
 * modified.
 * <br><br>
 * {@code a.addInPlace(b) };
 * <br>
 * - Here 'a' is modified to be the sum of 'a' and 'b'. This will generally be more efficient since
 * it does not involve creating a new vector.
 * <br><br>
 * Created by clayton on 2015-06-06.
 */
public class Vector {
    public double x;
    public double y;

    public Vector() {}
    public Vector(double x, double y) {
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

    public Vector scalarMultiply (double scalar) {
        Vector vector = new Vector();
        vector.x = x * scalar;
        vector.y = y * scalar;

        return vector;
    }

    public Vector scalarDivide (double scalar) {
        Vector vector = new Vector(this);
        vector.x /= scalar;
        vector.y /= scalar;

        return vector;
    }

    public double magnitude() {
        double mag = 0;
        mag += x * x;
        mag += y * y;
        mag = Math.sqrt(mag);
        return mag;
    }

    public double dotProduct (Vector v) {
        double sum = 0;
        sum += x * v.x;
        sum += y * v.y;
        return sum;
    }

    public Vector normal() {
        return new Vector(y, -x);
    }

    public double angle() {
        return atan2(y, x);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + String.format("%.1f", x) +
                ", y=" + String.format("%.1f", y) +
                ", len=" + String.format("%.1f", magnitude()) +
                ", ang=" + String.format("%.1f", toDegrees(angle())) +
                '}';
    }
}
