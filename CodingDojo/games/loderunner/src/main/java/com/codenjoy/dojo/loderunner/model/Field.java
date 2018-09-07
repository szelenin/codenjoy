package com.codenjoy.dojo.loderunner.model;

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


import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

import java.util.List;

public interface Field extends GameField<Player> {
    boolean isBarrier(int x, int y);

    boolean tryToDrill(Hero hero, int x, int y);

    boolean isPit(int x, int y);

    Point getFreeRandom();

    boolean isLadder(int x, int y);

    boolean isPipe(int x, int y);

    boolean isFree(int x, int y);

    boolean isFullBrick(int x, int y);

    boolean isHeroAt(int x, int y);

    boolean isBrick(int x, int y);

    boolean isEnemyAt(int x, int y);

    void leaveGold(int x, int y);

    int size();

    boolean isBorder(int x, int y);

    List<Hero> getHeroes(); // TODO не слишком ли я рассекретил?
}
