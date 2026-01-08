package com.avaricious.components.slot;

import com.avaricious.Assets;
import com.avaricious.Main;
import com.avaricious.components.slot.pattern.PatternFinder;
import com.avaricious.components.slot.pattern.PatternMatch;
import com.avaricious.components.slot.pattern.SlotMatch;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Timer;

import java.util.*;

public class SlotMachine {

    // --- Layout ---
    private final int COLUMN_COUNT = 5;
    private final int ROW_COUNT = 3;
    private final float CELL_WIDTH = 1.1f;
    private final float CELL_HEIGHT = 1.1f;
    private final float SPACING_X = 0.4f;
    private final float SPACING_Y = 0.4f;

    private final float originX;
    private final float originY;

    // Visual cells (for selection pulse/scale)
    private final ObjectWithPopEffect[][] grid = new ObjectWithPopEffect[COLUMN_COUNT][ROW_COUNT];

    private final TextureRegion slotBox = new TextureRegion(Assets.I().getSlotBox());
    private final TextureRegion slotBoxShadow = new TextureRegion(Assets.I().getSlotBoxShadow());

    // Reels (one per column)
    private final List<Reel> reels = new ArrayList<>();

    boolean spinning = false;

    public SlotMachine(float worldWidth, float worldHeight) {
        // center the 5x3 grid within the world
        originX = ((worldWidth - COLUMN_COUNT * (CELL_WIDTH + SPACING_X)) / 2f) + 3f;
        originY = ((worldHeight - ROW_COUNT * (CELL_HEIGHT + SPACING_Y)) / 2f) + 1.5f;

        // build visual cells
        for (int c = 0; c < COLUMN_COUNT; c++) {
                grid[c][0] = new ObjectWithPopEffect(new Rectangle(
                    originX + c * (CELL_WIDTH + SPACING_X), originY + 2 * (CELL_HEIGHT + SPACING_Y),
                    CELL_WIDTH, CELL_HEIGHT
                ));
                grid[c][1] = new ObjectWithPopEffect(new Rectangle(
                    originX + c * (CELL_WIDTH + SPACING_X), originY + 1 * (CELL_HEIGHT + SPACING_Y),
                    CELL_WIDTH, CELL_HEIGHT
                ));
                grid[c][2] = new ObjectWithPopEffect(new Rectangle(
                    originX + c * (CELL_WIDTH + SPACING_X), originY + 0 * (CELL_HEIGHT + SPACING_Y),
                    CELL_WIDTH, CELL_HEIGHT
                ));
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
//        for(int i = 0; i < 4; i++) {
//            baseStrip.add(Symbol.IRON);
//            baseStrip.add(Symbol.DIAMOND);
//        }
//        baseStrip.add(Symbol.SEVEN);
//        baseStrip.add(Symbol.SEVEN);

        for (int c = 0; c < COLUMN_COUNT; c++) {
            reels.add(new Reel(baseStrip, ROW_COUNT));
        }
    }

    // --- drawing ---
    public void draw(Main app, float delta) {
        SpriteBatch batch = app.getBatch();
        // update reel motion
        for (int c = 0; c < COLUMN_COUNT; c++) {
            reels.get(c).update(delta);
        }

        // one big clip over the whole machine area
        Camera cam = app.getViewport().getCamera();
        cam.update();

        Rectangle area = getBounds(); // world-space
        area.setX(area.x - 0.3f);
        area.setWidth(area.width + 0.3f);
        area.setY(area.y - 0.15f);
        area.setHeight(area.height);
        Rectangle scissors = new Rectangle();
        ScissorStack.calculateScissors(cam, batch.getTransformMatrix(), area, scissors);

        batch.flush();
        ScissorStack.pushScissors(scissors);

        // render continuous rolling bands per column
        final float stepX = (CELL_WIDTH + SPACING_X);
        final float stepY = (CELL_HEIGHT + SPACING_Y);
        final float topY = originY + (ROW_COUNT - 1) * stepY; // y of the top grid cell

        for (int c = 0; c < COLUMN_COUNT; c++) {
            Reel reel = reels.get(c);
            float frac = reel.frac(); // 0..1 progress toward next symbol
            float drawX = originX + c * stepX;

            int drawFrom = 0;
            int drawTo   = ROW_COUNT;

            for (int k = drawFrom; k <= ROW_COUNT; k++) {


                Reel.SymbolInstance symbolSlot = reel.slotAtRow(k);

                float drawY = topY - (k + frac) * stepY;

                boolean hovered = false;
                float s = 1f;

                TextureRegion region;

                ObjectWithPopEffect objectWithPopEffect = grid[c][k];

                objectWithPopEffect.handleInput(delta);

                boolean isInGrid = k < ROW_COUNT;
                objectWithPopEffect.draw(batch, delta, isInGrid);
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
        for (int c = 0; c < COLUMN_COUNT; c++) {
            final int col = c;
            float startDelay = c * startStagger;
            float stopDelay  = COLUMN_COUNT * startStagger + c * stopStagger;

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

    // Returns each matching line as a List<Slot>
    public List<SlotMatch> findMatches() {
        Symbol[][] symbolMap = new Symbol[COLUMN_COUNT][ROW_COUNT];

        for (int c = 0; c < reels.size(); c++) {
            for (int row = 0; row < ROW_COUNT; row++) {
                symbolMap[c][row] = reels.get(c).slotAtRow(row).symbol();
            }
        }

        // Raw matches (symbol + positions)
        List<PatternMatch> matches = PatternFinder.findMatches(symbolMap);

        // Build final slot-based matches
        List<SlotMatch> result = new ArrayList<>();

        for (PatternMatch match : matches) {
            Symbol symbol = match.getSymbol();

            List<ObjectWithPopEffect> objectWithPopEffects = new ArrayList<>();
            for (java.awt.Point p : match.getPositions()) {
                objectWithPopEffects.add(grid[p.x][p.y]);
            }

            result.add(new SlotMatch(symbol, objectWithPopEffects));
        }

        return result;
    }

    public List<ObjectWithPopEffect> getAllSlots() {
        List<ObjectWithPopEffect> objectWithPopEffects = new ArrayList<>();
        Arrays.stream(grid).forEach(rows -> objectWithPopEffects.addAll(Arrays.asList(rows)));
        return objectWithPopEffects;
    }

    public Rectangle getBounds() {
        return new Rectangle(
            originX,
            originY,
            COLUMN_COUNT * (CELL_WIDTH + SPACING_X),
            ROW_COUNT * (CELL_HEIGHT + SPACING_Y)
        );
    }

    public List<Reel> getReels() {
        return reels;
    }

    public float getCellW() {
        return CELL_WIDTH;
    }

    public float getCellH() {
        return CELL_HEIGHT;
    }
}
