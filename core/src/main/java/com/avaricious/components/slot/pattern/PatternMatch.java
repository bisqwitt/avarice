// com.avaricious.components.slot.pattern.PatternMatch

package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Symbol;

import java.awt.Point;
import java.util.List;

public record PatternMatch(Symbol symbol, int length, List<Point> positions, PatternDirection direction) {
    public List<Point> getPositions() {
        return positions;
    }

    public Symbol getSymbol() {
        return symbol;
    }
}
