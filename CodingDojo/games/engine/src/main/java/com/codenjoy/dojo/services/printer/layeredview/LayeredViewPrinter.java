package com.codenjoy.dojo.services.printer.layeredview;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */



import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.Printer;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class LayeredViewPrinter implements Printer<PrinterData> {

    private static final int BOUND_DEFAULT = 4;

    private BoardReader reader;
    private Supplier<Object> player;
    private int countLayers;

    private int viewSize;
    private int vx;
    private int vy;
    private int bound;

    private boolean needToCenter;
    private int size;

    public LayeredViewPrinter(BoardReader reader, Supplier<Object> player, int viewSize, int countLayers) {
        this.reader = reader;
        this.player = player;
        this.countLayers = countLayers;
        this.viewSize = Math.min(reader.size(), viewSize);

        if (this.viewSize == viewSize) {
            bound = BOUND_DEFAULT;
        }

        needToCenter = bound != 0;
    }

    @Override
    public PrinterData print(Object... parameters) {
        size = reader.size();
        Object player = this.player.get();

        centerPositionOnStart(player);

        StringBuilder[] builders = prepareLayers();
        fillLayers(player, builders);
        PrinterData result = getPrinterData(builders);

        return result;
    }

    private void fillLayers(Object player, StringBuilder[] builders) {
        BiFunction<Integer, Integer, State> elements = reader.elements();
        LengthToXY xy = new LengthToXY(size);
        for (int y = vy + viewSize - 1; y >= vy; --y) {
            for (int x = vx; x < vx + viewSize; ++x) {
                int index = xy.getLength(x, y);

                for (int j = 0; j < countLayers; ++j) {
                    State item = elements.apply(index, j);
                    Object[] inSameCell = reader.itemsInSameCell(item);
                    builders[j].append(makeState(item, player, inSameCell));
                }
            }
        }
    }

    private PrinterData getPrinterData(StringBuilder[] builders) {
        PrinterData result = new PrinterData();
        result.setOffset(pt(vx, vy));
        for (int i = 0; i < countLayers; ++i) {
            result.addLayer(builders[i].toString());
        }
        return result;
    }

    private StringBuilder[] prepareLayers() {
        StringBuilder[] builders = new StringBuilder[countLayers];
        for (int i = 0; i < countLayers; ++i) {
            builders[i] = new StringBuilder(viewSize * viewSize + viewSize);
        }
        return builders;
    }

    private void centerPositionOnStart(Object player) {
        Point pivot = reader.viewCenter(player);
        if (needToCenter) {
            needToCenter = false;
            moveToCenter(pivot);
        } else if (pivot != null) {
            moveTo(pivot);
        }
        adjustView(size);
    }

    private String makeState(State item, Object player, Object[] elements) {
        return (item == null) ? "-" : item.state(player, elements).toString();
    }

    private void moveTo(Point point) {
        int left = point.getX() - (vx + bound);
        left = fixToNegative(left);

        int right = point.getX() - (vx + viewSize - bound - 1);
        right = fixToPositive(right);

        int bottom = point.getY() - (vy + bound);
        bottom = fixToNegative(bottom);

        int up = point.getY() - (vy + viewSize - bound - 1);
        up = fixToPositive(up);

        vx += left + right;
        vy += up + bottom;
    }

    private int fixToPositive(int value) {
        if (value < 0) {
            return 0;
        }

        return value;
    }

    private int fixToNegative(int value) {
        if (value > 0) {
            return 0;
        }

        return value;
    }

    private void moveToCenter(Point point) {
        vx = (int) (point.getX() - Math.round((double) viewSize / 2));
        vy = (int) (point.getY() - Math.round((double) viewSize / 2));
    }

    private void adjustView(int size) {
        vx = fixToPositive(vx);
        if (vx + viewSize > size) {
            vx = size - viewSize;
        }

        vy = fixToPositive(vy);
        if (vy + viewSize > size) {
            vy = size - viewSize;
        }
    }

}
