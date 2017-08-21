package com.codenjoy.dojo.snake.battle.model.board;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
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
import com.codenjoy.dojo.snake.battle.model.Player;
import com.codenjoy.dojo.snake.battle.model.hero.Hero;
import com.codenjoy.dojo.snake.battle.model.level.LevelImpl;
import com.codenjoy.dojo.snake.battle.model.objects.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author K.ilya
 */
@RunWith(Parameterized.class)
public class BoardAddObjectsTest {

    private SnakeBoard game;

    private Point additionObject;
    boolean shouldAdd;

    public BoardAddObjectsTest(Point additionObject, boolean shouldAdd) {
        this.additionObject = additionObject;
        this.shouldAdd = shouldAdd;
    }

    private void givenFl(String board) {
        LevelImpl level = new LevelImpl(board);
        List<Hero> heroes = level.getHero();
        Hero hero = heroes.isEmpty() ? null : heroes.get(0);

        game = new SnakeBoard(level, mock(Dice.class));
        game.debugMode = true;
        EventListener listener = mock(EventListener.class);
        Player player = new Player(listener);
        game.newGame(player);
        if (hero != null) {
            player.setHero(hero);
            hero.init(game);
        }
        Hero hero1 = game.getHeroes().get(0);
        hero1.setActive(true);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] params = new Object[][]{
                // нельзя ставить яблоки на яблоки,камни,таблетки,золото,стены
                {new Apple(2, 2), false},
                {new Apple(2, 1), false},
                {new Apple(3, 3), false},
                {new Apple(3, 2), false},
                {new Apple(3, 1), false},
                {new Apple(3, 0), false},
                // нельзя ставить камни на яблоки,камни,таблетки,золото,стены
                {new Stone(2, 2), false},
                {new Stone(2, 1), false},
                {new Stone(3, 3), false},
                {new Stone(3, 2), false},
                {new Stone(3, 1), false},
                {new Stone(3, 0), false},
                // нельзя ставить таблетки полёта на яблоки,камни,таблетки,золото,стены
                {new FlyingPill(2, 2), false},
                {new FlyingPill(2, 1), false},
                {new FlyingPill(3, 3), false},
                {new FlyingPill(3, 2), false},
                {new FlyingPill(3, 1), false},
                {new FlyingPill(3, 0), false},
                // нельзя ставить таблетки ярости на яблоки,камни,таблетки,золото,стены
                {new FuryPill(2, 2), false},
                {new FuryPill(2, 1), false},
                {new FuryPill(3, 3), false},
                {new FuryPill(3, 2), false},
                {new FuryPill(3, 1), false},
                {new FuryPill(3, 0), false},
                // нельзя ставить золото на яблоки,камни,таблетки,золото,стены
                {new Gold(2, 2), false},
                {new Gold(2, 1), false},
                {new Gold(3, 3), false},
                {new Gold(3, 2), false},
                {new Gold(3, 1), false},
                {new Gold(3, 0), false},
                // можно ставить яблоки,камни,таблетки и золото в пустое место
                {new Apple(4, 2), true},
                {new Stone(4, 2), true},
                {new FlyingPill(4, 2), true},
                {new FuryPill(4, 2), true},
                {new Gold(4, 2), true},
        };
        return Arrays.asList(params);
    }

    @Test
    public void oneOrLessObjectAtPoint() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼ →►  ☼" +
                "☼     ☼" +
                "☼  %  ☼" +
                "☼ @○  ☼" +
                "☼ $●  ☼" +
                "☼☼☼☼☼☼☼");
        int objectsBefore = 1;
        Point objOnPoint = game.getObjOn(additionObject);
        game.addToPoint(additionObject);
        game.tick();
        int objectsAfter = 0;
        String objType = additionObject.getClass().toString().replaceAll(".*\\.", "");
        switch (objType) {
            case "Apple":
                objectsAfter = game.getApples().size();
                break;
            case "Stone":
                objectsAfter = game.getStones().size();
                break;
            case "FlyingPill":
                objectsAfter = game.getFlyingPills().size();
                break;
            case "FuryPill":
                objectsAfter = game.getFuryPills().size();
                break;
            case "Gold":
                objectsAfter = game.getGold().size();
                break;
            default:
                fail("Отсутствуют действия на объект типа " + objType);
        }
        if (shouldAdd)
            assertEquals("Новый объект '" + objType + "' не был добавлен на поле!",
                    objectsBefore + 1, objectsAfter);
        else
            assertEquals("Добавился новый объект '" + objType + "'" + " поверх существующего объекта!" +
                            (objOnPoint == null ? null : objOnPoint.getClass()),
                    objectsBefore, objectsAfter);
    }

}
