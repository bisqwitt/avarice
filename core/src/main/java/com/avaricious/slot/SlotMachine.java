package com.avaricious.slot;

import com.avaricious.Assets;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SlotMachine {
    private final int cols = 5;
    private final int rows = 3;
    private final float cellW = 1.5f;
    private final float cellH = 1.5f;
    private final float spacingX = 0.2f;
    private final float spacingY = 0.2f;

    private final float originX;
    private final float originY;
    private final Slot[][] grid = new Slot[cols][rows];

    private Symbol hover;
    private List<Symbol> selection =  new ArrayList<>();
    private String scoreFormula = "";
    private String patternText = "";

    public SlotMachine(float worldWidth, float worldHeight) {
        // center the grid
        originX = (worldWidth - cols * cellW) / 2f;
        originY = (worldHeight - rows * cellH) / 2f;

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                grid[c][r] = new Slot(
                    originX + c * (cellW + spacingX),
                    originY + r * (cellH + spacingY));
            }
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Slot slot = grid[c][r];
                if(slot.isSpinning()) slot.changeSymbol();
                float drawX = slot.posX();
                float drawY = slot.posY();
                float drawW = cellW;
                float drawH = cellH;
                if(hover == slot.type()) {
                    drawW *= 1.1f;
                    drawH *= 1.1f;
                    drawX -= (drawW - cellW) / 2f;
                    drawY -= (drawH - cellH) / 2f;
                }
                if(selection.contains(slot.type())) {
                    drawW *= 1.2f;
                    drawH *= 1.2f;
                    drawX -= (drawW - cellW) / 2f;
                    drawY -= (drawH - cellH) / 2f;
                }
                batch.draw(slot.getFrame(selection.contains(slot.type()), delta), drawX, drawY, drawW, drawH);
            }
        }
    }

    public void spin() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {

                float duration = 1f
                    + (i * 0.3f)
                    + ((grid[i].length - 1 - j) * 0.1f);
                grid[i][j].spin(duration);
            }
        }

        clearSelection();
    }

    public long applySelection() {
        long score = calcScore();
        Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(slot -> selection.contains(slot.type()))
            .forEach(Slot::changeSymbol);
        clearSelection();
        return score;
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
        updateDisplayTexts();
    }

    public void hoveringAt(Vector3 mouse) {
        Rectangle bounds = getBounds();
        if(!bounds.contains(mouse.x, mouse.y)) {
            hover = null;
            return;
        }
        int col = (int)((mouse.x - bounds.x) / (cellW + spacingX));
        int row = (int)((mouse.y - bounds.y) / (cellH + spacingY));

        if (col >= 0 && col < getCols() &&
            row >= 0 && row < getRows()) {
            hover = grid[col][row].type();
        }
    }

    public void updateDisplayTexts() {
        if(selection.isEmpty()) {
            scoreFormula = "";
            patternText = "";
            return;
        }

        long numOfAKind = countSymbol();
        long chips = selection.stream()
            .mapToLong(symbol -> numOfAKind * SymbolManager.I().getSymbolValue(symbol))
            .sum();

        long count = countSymbol();
        scoreFormula = (chips + UpgradesManager.I().chipAdditions(selection, count))
            + " x " + ((numOfAKind * selection.size() + UpgradesManager.I().multAdditions(selection, count)));

        patternText = selection.size() + " x " + numOfAKind + "-of-a-kind";
    }

    private long calcScore() {
        String[] parts = scoreFormula.split(" x ");
        return Long.parseLong(parts[0]) * Long.parseLong(parts[1]);
    }

    public long countSymbol() {
        return Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(slot -> slot.type() == selection.get(0))
            .count();
    }

    public long countSymbol(Symbol type) {
        return Arrays.stream(grid)
            .flatMap(Arrays::stream)
            .filter(slot -> slot.type() == type)
            .count();
    }

    public void clearSelection() {
        selection.clear();
        scoreFormula = "";
        patternText = "";
    }

    public Slot[][] getGrid() {
        return grid;
    }

    public Symbol getHover() {
        return hover;
    }

    public String getScoreFormula() {
        return scoreFormula;
    }

    public String getPatternText() {
        return patternText;
    }

    public Rectangle getBounds() {
        return new Rectangle(
            originX,
            originY,
            cols * (cellW + spacingX),
            rows * (cellH + spacingY)
        );
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

    public float getSpacingX() {
        return spacingX;
    }

    public float getSpacingY() {
        return spacingY;
    }

    /*

    ----- OLD SCORE CALCULATION ------

    private long calcScore() {
        int chips = selection.stream()
            .mapToInt(symbol -> SymbolManager.I().getSymbolValue(symbol))
            .sum();
        return chips * (selection.size() + countSymbol(selection.get(0)));
    }

    private void updateScoreFormula() {
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
        sb.append(") * (").append(selection.size()).append(" + ").append(countSymbol(selection.get(0))).append(")");
        scoreFormula = sb.toString();
    }

    */

}
