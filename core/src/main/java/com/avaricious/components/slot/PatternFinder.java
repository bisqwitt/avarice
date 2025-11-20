package com.avaricious.components.slot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

// Optional: adjust as you like
enum Direction {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL_DOWN,   // top-left -> bottom-right
    DIAGONAL_UP      // bottom-left -> top-right
}

class Match {
    public final Symbol symbol;
    public final int length;
    public final List<Point> positions; // x = col, y = row
    public final Direction direction;

    public Match(Symbol symbol, int length, List<Point> positions, Direction direction) {
        this.symbol = symbol;
        this.length = length;
        this.positions = positions;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Match{" +
            "symbol=" + symbol +
            ", length=" + length +
            ", direction=" + direction +
            ", positions=" + positions +
            '}';
    }
}

public class PatternFinder {

    /**
     * Finds all matches of length >= 3 in the given symbol grid.
     * Assumes symbolMap[x][y] => x = column, y = row.
     */
    public static List<Match> findMatches(Symbol[][] symbolMap) {
        List<Match> matches = new ArrayList<>();

        int cols = symbolMap.length;
        if (cols == 0) return matches;
        int rows = symbolMap[0].length;

        // Directions: right, down, down-right, up-right
        int[][] dirs = {
            { 1,  0}, // horizontal
            { 0,  1}, // vertical
            { 1,  1}, // diagonal down
            { 1, -1}  // diagonal up
        };
        Direction[] dirEnums = {
            Direction.HORIZONTAL,
            Direction.VERTICAL,
            Direction.DIAGONAL_DOWN,
            Direction.DIAGONAL_UP
        };

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Symbol base = symbolMap[x][y];
                if (base == null) continue; // skip empty

                for (int d = 0; d < dirs.length; d++) {
                    int dx = dirs[d][0];
                    int dy = dirs[d][1];

                    // Only start a run if the previous cell in this direction
                    // is out of bounds or a different symbol.
                    int prevX = x - dx;
                    int prevY = y - dy;
                    if (inBounds(prevX, prevY, cols, rows) &&
                        equalsSymbol(symbolMap[prevX][prevY], base)) {
                        continue; // this run was/will be counted from an earlier cell
                    }

                    // Walk forward and count how many same symbols we have
                    List<Point> positions = new ArrayList<>();
                    int cx = x;
                    int cy = y;

                    while (inBounds(cx, cy, cols, rows) &&
                        equalsSymbol(symbolMap[cx][cy], base)) {
                        positions.add(new Point(cx, cy));
                        cx += dx;
                        cy += dy;
                    }

                    int length = positions.size();
                    if (length >= 3) {
                        matches.add(new Match(base, length, positions, dirEnums[d]));
                    }
                }
            }
        }

        return matches;
    }

    private static boolean inBounds(int x, int y, int cols, int rows) {
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

    /**
     * Adjust this depending on your Symbol type:
     *  - If Symbol is an enum, '==' is fine.
     *  - If it's a class, you likely want equals().
     */
    private static boolean equalsSymbol(Symbol a, Symbol b) {
        if (a == null || b == null) return false;
        // If Symbol is enum:
        return a == b;
        // If Symbol is not enum, use:
        // return a.equals(b);
    }
}

