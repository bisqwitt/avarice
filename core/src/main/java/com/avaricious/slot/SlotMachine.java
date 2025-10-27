package com.avaricious.slot;

import com.avaricious.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SlotMachine {
    private final int cols = 5;
    private final int rows = 3;
    private final float cellW = 1.75f;
    private final float cellH = 1.75f;

    private final float originX;
    private final float originY;
    private final Slot[][] grid = new Slot[cols][rows];

    private List<Symbol> selection =  new ArrayList<>();
    private String scoreFormula = "";

    public SlotMachine(float worldWidth, float worldHeight) {
        // center the grid
        originX = (worldWidth - cols * cellW) / 2f;
        originY = (worldHeight - rows * cellH) / 2f;

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                grid[c][r] = new Slot(originX + c * cellW, originY + r * cellH);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Slot slot = grid[c][r];
                float drawX = slot.posX();
                float drawY = slot.posY();
                float drawW = cellW;
                float drawH = cellH;
                if(selection.contains(slot.type())) {
                    drawW *= 1.2f;
                    drawH *= 1.2f;
                    drawX -= (drawW - cellW) / 2f;
                    drawY -= (drawH - cellH) / 2f;
                }
                batch.draw(Assets.I().get(slot.type()), drawX, drawY, drawW, drawH);
            }
        }
    }

    public void spin() {
        Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .forEach(Slot::spin);

        clearSelection();
        updateScoreFormula();
    }

    public long applySelection() {
        long score = calcScore();
        Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(slot -> selection.contains(slot.type()))
            .forEach(Slot::spin);
        clearSelection();
        return score;
    }

    public long countSymbol(Symbol type) {
        return Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(slot -> slot.type() == type)
            .count();
    }

    public void selectSymbolAt(int col, int row) {
        Symbol type = grid[col][row].type();
        if(selection.contains(type)) {
            selection.remove(type);
        } else {
            long symbolCount = countSymbol(type);
            selection = selection.stream()
                .filter(t -> countSymbol(t) == symbolCount)
                .collect(Collectors.toList());
            selection.add(type);
        }
        updateScoreFormula();
    }

    private long calcScore() {
        int chips = selection.stream()
            .mapToInt(Symbol::baseValue)
            .sum();
        return chips * (selection.size() + countSymbol(selection.get(0)));
    }

    public void updateScoreFormula() {
        if(selection.isEmpty()) {
            scoreFormula = "";
            return;
        }

        StringBuilder sb = new StringBuilder("(");
        for(int i = 0; i < selection.size(); i++) {
            if(i != 0) sb.append(" + ");
            Symbol symbol = selection.get(i);
            sb.append(SymbolManager.I().getSymbolValue(symbol)).append("-").append(symbol.toString(), 0, 2);
        }
        sb.append(") * (").append(selection.size()).append(" + ").append(countSymbol(selection.get(0)));
        sb.append(") = ").append(calcScore());
        scoreFormula = sb.toString();
    }

    public String getScoreFormula() {
        return scoreFormula;
    }

    public void clearSelection() {
        selection.clear();
        scoreFormula = "";
    }

    public Rectangle getBounds() {
        return new Rectangle(originX, originY, cols * cellW, rows * cellH);
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public float getCellH() {
        return cellH;
    }

    public float getCellW() {
        return cellW;
    }

    /*

    SPIN VERSION WHERE SELECTION DOESN'T GET SPINNED

    public void spin() {
        Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(s -> !selection.contains(s.type()))
            .forEach(Slot::spin);

        if (!selection.isEmpty()) {
            Map<Symbol, Long> counts =
                Arrays.stream(grid)
                    .flatMap(Arrays::stream)
                    .map(Slot::type)
                    .filter(selection::contains)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            long max = counts.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

            selection.removeIf(t -> counts.getOrDefault(t, 0L) < max);
        }

        updateScoreFormula();
    }
    */
}
