package com.avaricious.components.slot.pattern;

import com.avaricious.components.slot.Slot;
import com.avaricious.components.slot.Symbol;

import java.util.List;

public record SlotMatch(Symbol symbol, List<Slot> slots) { }
