package com.avaricious;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotGrid {
    private final int cols = 5;
    private final int rows = 3;
    private final float cellW = 0.75f;
    private final float cellH = 0.75f;

    private final float originX;
    private final float originY;
    private final Symbols[][] grid = new Symbols[cols][rows];

    public SlotGrid(float worldWidth, float worldHeight) {
        // center the grid
        originX = (worldWidth - cols * cellW) / 2f;
        originY = ((worldHeight - rows * cellH) / 2f) + 1;

        randomizeAll();
    }

    public void draw(SpriteBatch batch) {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Texture tex = Assets.I().get(grid[c][r]);
                float x = originX + c * cellW;
                float y = originY + r * cellH;
                batch.draw(tex, x, y, cellW, cellH);
            }
        }
    }

    public void randomizeAll() {
        Symbols[] vals = Symbols.values();
        for (int c = 0; c < cols; c++)
            for (int r = 0; r < rows; r++)
                grid[c][r] = vals[MathUtils.random(vals.length - 1)];
    }

    public List<PatternHit> findPatternHits() {
        List<PatternHit> matches = new ArrayList<>();

        // ----- Horizontal 3, 4, or 5 -----
        for (int y = 0; y < rows; y++) {
            int x = 0;
            while (x < cols) {
                Symbols s = grid[x][y];
                int len = 1;
                int k = x + 1;
                while (k < cols && grid[k][y] == s) { len++; k++; }
                if (len >= 3) { // 3–5 valid
                    matches.add(new PatternHit(s, len));
                }
                x += len;
            }
        }

        // ----- Vertical -----
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y <= rows - 3; y++) {
                if (grid[x][y] == grid[x][y + 1] && grid[x][y] == grid[x][y + 2]) {
                    matches.add(new PatternHit(grid[x][y], PatternType.VERT));
                }
            }
        }

        // ----- Diagonal ↘ -----
        for (int x = 0; x <= cols - 3; x++) {
            for (int y = 0; y <= rows - 3; y++) {
                if (grid[x][y] == grid[x + 1][y + 1] && grid[x][y] == grid[x + 2][y + 2]) {
                    matches.add(new PatternHit(grid[x][y], PatternType.DIAG));
                }
            }
        }

        // ----- Diagonal ↗ -----
        for (int x = 0; x <= cols - 3; x++) {
            for (int y = 2; y < rows; y++) {
                if (grid[x][y] == grid[x + 1][y - 1] && grid[x][y] == grid[x + 2][y - 2]) {
                    matches.add(new PatternHit(grid[x][y], PatternType.DIAG));
                }
            }
        }

        // ----- Complex patterns -----
        Arrays.stream(PatternType.values())
            .filter((patternType -> patternType.mask() != null))
            .forEach((patternType -> {
                boolean found = true;
                Symbols firstSymbol = null;

                outer:
                for (int y = 0; y < patternType.mask().length; y++) {
                    for (int x = 0; x < patternType.mask()[0].length; x++) {
                        if (!patternType.mask()[y][x]) continue;
                        Symbols current = grid[x][y];
                        if (firstSymbol == null) firstSymbol = current;
                        else if (current != firstSymbol) {
                            found = false;
                            break outer;
                        }
                    }
                }

                if (found && firstSymbol != null) matches.add(new PatternHit(firstSymbol, patternType));
            }));

        // ----- Jackpot -----
        if(Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .allMatch(s -> s == grid[0][0]))
            matches.add(new PatternHit(grid[0][0], PatternType.JACKPOT));

        return matches;
    }

    public Rectangle getBounds() {
        return new Rectangle(originX, originY, cols * cellW, rows * cellH);
    }


}
