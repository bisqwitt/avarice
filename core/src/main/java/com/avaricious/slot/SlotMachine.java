package com.avaricious.slot;

import com.avaricious.Assets;
import com.avaricious.Main;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Timer;

import java.util.*;
import java.util.stream.Collectors;

public class SlotMachine {

    private final ShapeRenderer shapeRenderer;

    // --- Layout ---
    private final int cols = 5;
    private final int rows = 3;
    private final float cellW = 1.25f;
    private final float cellH = 1.25f;
    private final float spacingX = 0.2f;
    private final float spacingY = 0.2f;

    private final float originX;
    private final float originY;

    // Visual cells (for selection pulse/scale)
    private final Slot[][] grid = new Slot[cols][rows];

    // Reels (one per column)
    private final List<Reel> reels = new ArrayList<>();

    boolean spinning = false;

    // UI state
    private Symbol hover;
    private List<Symbol> selection = new ArrayList<>();
    private Map<Symbol, Long> picks = new LinkedHashMap<>();
    private String scoreFormula = "";
    private String patternText = "";

    public SlotMachine(float worldWidth, float worldHeight) {
        shapeRenderer = new ShapeRenderer();
        Arrays.stream(Symbol.values()).forEach(symbol -> picks.put(symbol, 0L));

        // center the 5x3 grid within the world
        originX = ((worldWidth - cols * (cellW + spacingX)) / 2f);
        originY = ((worldHeight - rows * (cellH + spacingY)) / 2f);

        // build visual cells
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                grid[c][r] = new Slot(
                    originX + c * (cellW + spacingX),
                    originY + r * (cellH + spacingY));
            }
        }

        // build basic reel strips (repeat symbol set to avoid short cycles)
        List<Symbol> baseStrip = new ArrayList<>();
        baseStrip.addAll(Arrays.asList(Symbol.values()));
        baseStrip.addAll(Arrays.asList(Symbol.values()));
        baseStrip.addAll(Arrays.asList(Symbol.values()));

        for (int c = 0; c < cols; c++) {
            reels.add(new Reel(baseStrip, rows));
        }
        reels.get(reels.size() -1).setOnSpinFinished(() -> spinning = false);
    }

    // --- drawing ---
    public void draw(Main app, float delta) {
        SpriteBatch batch = app.getBatch();
        // update reel motion
        for (int c = 0; c < cols; c++) {
            reels.get(c).update(delta);
        }

        // one big clip over the whole machine area
        Camera cam = app.getViewport().getCamera();
        cam.update();

        Rectangle area = getBounds(); // world-space
        Rectangle scissors = new Rectangle();
        ScissorStack.calculateScissors(cam, batch.getTransformMatrix(), area, scissors);

        batch.flush();
        ScissorStack.pushScissors(scissors);

        // render continuous rolling bands per column
        final float stepX = (cellW + spacingX);
        final float stepY = (cellH + spacingY);
        final float topY = (originY + (rows - 1) * stepY) + 0.1f; // y of the top grid cell

        for (int c = 0; c < cols; c++) {
            Reel reel = reels.get(c);
            float frac = reel.frac(); // 0..1 progress toward next symbol
            float colX = (originX + c * stepX) + 0.1f;

            int extraAbove = 1;
            int extraBelow = 1;
            int drawFrom = -extraAbove;
            int drawTo   = rows - 1 + extraBelow;

            for (int k = drawFrom; k <= drawTo; k++) {
                boolean isInGrid = (k >= 0 && k < rows);
                Symbol sym = reel.symbolAtRow(k);

                float drawX = colX;
                float drawY = topY - (k + frac) * stepY;

                boolean selected = false;
                boolean highlighted = false;
                boolean hovered = false;
                float s = 1f;

                TextureRegion region;

                if (isInGrid) {
                    selected = selection.contains(sym) && !reel.isSpinning();
                    hovered = (hover == sym);              // NEW: pure hover state
                    highlighted = (hovered || selected);   // same visual intent

                    Slot slot = grid[c][k];
                    slot.targetScale = highlighted ? 1.125f : 1f;

                    slot.updatePulse(selected, delta);
                    slot.tickScale(delta);

                    // wobble on HOVER entry even if selected
                    slot.updateHoverWobble(hovered, delta);

                    s = slot.scale * slot.pulseScale() * slot.wobbleScale();
                }

                float drawW = cellW * s;
                float drawH = cellH * s;
                float adjX = drawX - (drawW - cellW) / 2f;
                float adjY = drawY - (drawH - cellH) / 2f;

                // choose frame (keeps your animated border when selected)
                region = isInGrid
                    ? grid[c][k].getFrame(sym, selected, delta)
                    : Assets.I().getBase(sym);

                // NEW: rotate around center using current wobble angle
                float rotation = isInGrid ? grid[c][k].wobbleAngleDeg() : 0f;

                // Draw with origin at the center, width/height already scaled
                batch.draw(
                    region,
                    adjX, adjY,
                    drawW / 2f, drawH / 2f,   // originX, originY
                    drawW, drawH,
                    1f, 1f,                   // scale already baked into drawW/H
                    rotation
                );
            }
        }

        batch.flush();
        ScissorStack.popScissors();
    }

    // --- spin control (organic staggered start/stop, aligned to center row) ---
    public void spin() {
        spinning = true;
        // tuning knobs
        float startSpeed = 16f;      // symbols/sec target cruise
        float startStagger = 0.15f;  // delay between reel starts
        float stopStagger  = 0.35f;  // delay between reel stops (after starts)

        // start + schedule stop per reel
        for (int c = 0; c < cols; c++) {
            final int col = c;
            float startDelay = c * startStagger;
            float stopDelay  = cols * startStagger + c * stopStagger;

            Timer.schedule(new Timer.Task() {
                @Override public void run() { reels.get(col).start(startSpeed); }
            }, startDelay);

            Timer.schedule(new Timer.Task() {
                @Override public void run() {
                    reels.get(col).stopSoonAlignCenter();
                }
            }, stopDelay);
        }

        clearSelection();
    }

    // --- apply selection (score + quick nudge feedback) ---
    public void applySelection() {
        Arrays.stream(Symbol.values())
            .filter(s -> selection.contains(s))
            .forEach(s -> picks.put(s, picks.get(s) + countSymbol(s)));
        clearSelection();
        spin();
    }

    // --- input helpers ---
    public void selectSymbolAt(int col, int rowFromBottom) {
        if(spinning) return;
        // clamp inputs first
        col = Math.max(0, Math.min(cols - 1, col));
        rowFromBottom = Math.max(0, Math.min(rows - 1, rowFromBottom));

        // convert: bottom-indexed (0=bottom) -> top-indexed (0=top)
        int rowFromTop = rows - 1 - rowFromBottom;

        Symbol type = getSymbolAt(col, rowFromTop);
        if (selection.contains(type)) {
            selection.remove(type);
        } else {
//            long symbolCount = countSymbol(type);
////            selection = selection.stream()
////                .filter(t -> countSymbol(t) == symbolCount)
////                .collect(Collectors.toList());
            selection.add(type);
        }
        updateDisplayTexts();
    }

    public void hoveringAt(Vector3 mouse) {
        if(spinning) return;
        Rectangle bounds = getBounds();
        if (!bounds.contains(mouse.x, mouse.y)) {
            hover = null;
            return;
        }

        float stepX = (cellW + spacingX);
        float stepY = (cellH + spacingY);

        int col = (int)((mouse.x - bounds.x) / stepX);
        int rowFromBottom = (int)((mouse.y - bounds.y) / stepY);

        // clamp to grid
        if (col < 0 || col >= cols || rowFromBottom < 0 || rowFromBottom >= rows) {
            hover = null;
            return;
        }

        // convert to top-indexed row for rendering/query
        int rowFromTop = rows - 1 - rowFromBottom;
        hover = getSymbolAt(col, rowFromTop);
    }


    // --- scoring/labels ---
    public void updateDisplayTexts() {
        if (selection.isEmpty()) {
            scoreFormula = "";
            patternText = "";
            return;
        }

        long numOfAKind = countSymbol();
        long chips = selection.stream()
            .mapToLong(symbol -> numOfAKind * SymbolManager.I().getSymbolValue(symbol))
            .sum();

        Assets assetManager = Assets.I();
        scoreFormula = (chips + UpgradesManager.I().chipAdditions(selection, numOfAKind))
            + " x " + (numOfAKind * selection.size() + UpgradesManager.I().multAdditions(selection, numOfAKind));

        patternText = (selection.size() + " x " + numOfAKind) + " of a kind";
    }

    private long calcScore() {
        if (scoreFormula == null || scoreFormula.isEmpty()) return 0L;
        String[] parts = scoreFormula.split(" x ");
        return Long.parseLong(parts[0]) * Long.parseLong(parts[1]);
    }

    // --- symbol queries on current visible grid ---
    public Symbol getSymbolAt(int col, int row) {
        return reels.get(col).symbolAtRow(row);
    }

    public long countSymbol() {
        Symbol target = selection.get(0);
        return countSymbol(target);
    }

    public long countSymbol(Symbol type) {
        long count = 0;
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (getSymbolAt(c, r) == type) count++;
            }
        }
        return count;
    }

    public void clearSelection() {
        selection.clear();
        scoreFormula = "";
        patternText = "";
    }

    public Map<Symbol, Long> getPicks() {
        return picks;
    }

    // --- getters ---
    public Slot[][] getGrid() { return grid; }
    public Symbol getHover() { return hover; }
    public String getScoreFormula() { return scoreFormula; }
    public String getPatternText() { return patternText; }

    public Rectangle getBounds() {
        return new Rectangle(
            originX,
            originY,
            cols * (cellW + spacingX),
            rows * (cellH + spacingY)
        );
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
    public float getCellH() { return cellH; }
    public float getCellW() { return cellW; }
    public float getSpacingX() { return spacingX; }
    public float getSpacingY() { return spacingY; }

    public List<Reel> getReels() {
        return reels;
    }
}
