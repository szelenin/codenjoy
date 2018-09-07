package com.codenjoy.dojo.services.hero;

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


import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class HeroDataImpl implements HeroData {

    private final Point coordinate;
    private final boolean isMultiplayer;
    private final Object additionalData;
    private final int level;
    private final List<Game> playersGroup;

    HeroDataImpl(int level, Point coordinate, boolean isMultiplayer, Object additionalData) {
        if (coordinate == null) {
            this.coordinate = pt(-1, -1);
        } else {
            this.coordinate = new PointImpl(coordinate);
        }
        this.level = level;
        this.isMultiplayer = isMultiplayer;
        this.additionalData = additionalData;
        this.playersGroup = null;
    }

    @Override
    public String toString() {
        return "HeroData[" +
                "coordinate=" + coordinate +
                ", level=" + level +
                ", multiplayer=" + isMultiplayer +
                ", additionalData=" + additionalData +
                ']';
    }

    public HeroDataImpl(int level, Point coordinate, boolean isMultiplayer) {
        this(level, coordinate, isMultiplayer, null);
    }

    public HeroDataImpl(int level, boolean isMultiplayer) {
        this(level, null, isMultiplayer, null);
    }

    public HeroDataImpl(Point coordinate, boolean isMultiplayer) {
        this(0, coordinate, isMultiplayer, null);
    }

    public HeroDataImpl(boolean isMultiplayer) {
        this(0, null, isMultiplayer, null);
    }

    @Override
    public Point getCoordinate() {
        return coordinate;
    }

    @Override
    public Object getAdditionalData() {
        return additionalData;
    }

    @Override
    public List<Game> playersGroup() {
        return playersGroup;
    }

    @Override
    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    @Override
    public int getLevel() {
        return level;
    }

}
