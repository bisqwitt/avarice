package com.avaricious.components.slot;

import box2dLight.RayHandler;
import com.avaricious.Assets;
import com.avaricious.Main;
import com.avaricious.components.slot.pattern.PatternFinder;
import com.avaricious.components.slot.pattern.PatternMatch;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.avaricious.upgrades.UpgradesManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Timer;

import java.util.*;
import java.util.stream.Collectors;

public class SlotMachine {

    // --- Layout ---
    private final int cols = 5;
    private final int rows = 3;
    private final float cellW = 1.3f;
    private final float cellH = 1.3f;
    private final float spacingX = 0.05f;
    private final float spacingY = 0.1f;

    private final float originX;
    private final float originY;

    // Visual cells (for selection pulse/scale)
    private final Slot[][] grid = new Slot[cols][rows];

    // Reels (one per column)
    private final List<Reel> reels = new ArrayList<>();

    boolean spinning = false;
    private List<Symbol> selection = new ArrayList<>();

    public SlotMachine(float worldWidth, float worldHeight, RayHandler rayHandler) {
        // center the 5x3 grid within the world
        originX = ((worldWidth - cols * (cellW + spacingX)) / 2f) + 3f;
        originY = ((worldHeight - rows * (cellH + spacingY)) / 2f) + 1.5f;

        // build visual cells
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if(r == 0) grid[c][r] = new Slot(new Vector2(
                    originX + c * (cellW + spacingX),
                    originY + 2 * (cellH + spacingY)
                ));
                if(r == 1) grid[c][r] = new Slot(new Vector2(
                    originX + c * (cellW + spacingX),
                    originY + 1 * (cellH + spacingY)
                ));
                if(r == 2) grid[c][r] = new Slot(new Vector2(
                    originX + c * (cellW + spacingX),
                    originY + 0 * (cellH + spacingY)
                ));
            }
        }

        // build basic reel strips (repeat symbol set to avoid short cycles)
        List<Symbol> baseStrip = new ArrayList<>();

        for(int i = 0; i < 12; i++) {
            baseStrip.add(Symbol.LEMON);
            baseStrip.add(Symbol.CHERRY);
        }
        for(int i = 0; i < 8; i++) {
            baseStrip.add(Symbol.CLOVER);
            baseStrip.add(Symbol.BELL);
        }
        for(int i = 0; i < 4; i++) {
            baseStrip.add(Symbol.IRON);
            baseStrip.add(Symbol.DIAMOND);
        }
        baseStrip.add(Symbol.SEVEN);
        baseStrip.add(Symbol.SEVEN);
//        baseStrip.addAll(Arrays.asList(Symbol.values()));
//        baseStrip.addAll(Arrays.asList(Symbol.values()));
//        baseStrip.addAll(Arrays.asList(Symbol.values()));

        for (int c = 0; c < cols; c++) {
            reels.add(new Reel(baseStrip, rows));
        }
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
        area.setY(area.y - 0.15f);
        area.setHeight(area.height);
        Rectangle scissors = new Rectangle();
        ScissorStack.calculateScissors(cam, batch.getTransformMatrix(), area, scissors);

        batch.flush();
        ScissorStack.pushScissors(scissors);

        // render continuous rolling bands per column
        final float stepX = (cellW + spacingX);
        final float stepY = (cellH + spacingY);
        final float topY = originY + (rows - 1) * stepY; // y of the top grid cell

        for (int c = 0; c < cols; c++) {
            Reel reel = reels.get(c);
            float frac = reel.frac(); // 0..1 progress toward next symbol
            float colX = originX + c * stepX;

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
    }

    public List<Symbol> getSymbols() {
        List<Symbol> symbols = new ArrayList<>();
        reels.forEach(reel -> {
            for (int row = 0; row < rows; row++) {
                symbols.add(reel.symbolAtRow(row));
            }
        });
        return symbols;
    }

    // Returns each matching line as a List<Slot>
    public List<SlotMatch> findMatches() {
        Symbol[][] symbolMap = new Symbol[cols][rows];

        for (int c = 0; c < reels.size(); c++) {
            for (int row = 0; row < rows; row++) {
                symbolMap[c][row] = reels.get(c).symbolAtRow(row);
            }
        }

        // Raw matches (symbol + positions)
        List<PatternMatch> matches = PatternFinder.findMatches(symbolMap);

        // Build final slot-based matches
        List<SlotMatch> result = new ArrayList<>();

        for (PatternMatch match : matches) {
            Symbol symbol = match.getSymbol();

            List<Slot> slots = new ArrayList<>();
            for (java.awt.Point p : match.getPositions()) {
                slots.add(grid[p.x][p.y]);
            }

            result.add(new SlotMatch(symbol, slots));
        }

        return result;
    }

    public List<Slot> getAllSlots() {
        List<Slot> slots = new ArrayList<>();
        Arrays.stream(grid).forEach(rows -> slots.addAll(Arrays.asList(rows)));
        return slots;
    }

    public Rectangle getBounds() {
        return new Rectangle(
            originX,
            originY,
            cols * (cellW + spacingX),
            rows * (cellH + spacingY)
        );
    }

    public List<Reel> getReels() {
        return reels;
    }

}
