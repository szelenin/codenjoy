package com.codenjoy.dojo.snake.battle.model.hero;

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


import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.battle.model.board.SnakeBoard;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static com.codenjoy.dojo.snake.battle.model.hero.Hero.reducedValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Kors
 */
public class SnakeHeroTest {

    private SnakeBoard game;
    private Hero hero;

    @Before
    public void setup() {
        hero = new Hero(new PointImpl(0, 0));
        game = mock(SnakeBoard.class);
        hero.init(game);
        hero.setActive(true);
        checkStartValues();
    }

    private void checkStartValues() {
        assertTrue("Змейка мертва!", hero.isAlive());
        assertTrue("Змейка не активна!", hero.isActive());
    }

    private void snakeEncreasing(int additionLength) {
        for (int i = 0; i < additionLength; i++)
            snakeEncreasing();
    }

    // Проверка что змейка увеличивается
    @Test
    public void snakeEncreasing() {
        int before = hero.size();
        applesAtAllPoints(true);// впереди яблоко -> увеличиваем змейку
        hero.tick();
        applesAtAllPoints(false);
        assertEquals("Змейка не увеличилась!", before + 1, hero.size());
    }

    // Проверка что неактивная змейка ничего не делает
    @Test
    public void snakeInactive() {
        hero.setActive(false);
        LinkedList<Tail> startBody = new LinkedList<>(hero.getBody());
        // просто тик
        hero.tick();
        assertEquals("Неактивная змейка изменилась!", startBody, hero.getBody());
        assertTrue("Змейка мертва!", hero.isAlive());
        // если яблоко
        applesAtAllPoints(true);
        hero.tick();
        applesAtAllPoints(false);
        assertEquals("Неактивная змейка изменилась!", startBody, hero.getBody());
        assertTrue("Змейка мертва!", hero.isAlive());
        // если камень
        stonesAtAllPoints(true);
        hero.tick();
        stonesAtAllPoints(false);
        assertEquals("Неактивная змейка изменилась!", startBody, hero.getBody());
        assertTrue("Змейка мертва!", hero.isAlive());
        // если стена
        wallsAtAllPoints(true);
        hero.tick();
        wallsAtAllPoints(false);
        assertEquals("Неактивная змейка изменилась!", startBody, hero.getBody());
        assertTrue("Змейка мертва!", hero.isAlive());
    }

    // Змейка погибает при столкновении со стеной
    @Test
    public void diedByWall() {
        int before = hero.size();
        wallsAtAllPoints(true);// впереди яблоко -> увеличиваем змейку
        hero.tick();
        wallsAtAllPoints(false);
        assertTrue("Змейка не погибла от препядствия!", !hero.isAlive());
    }

    // тест что короткая змейка погибает от камня
    @Test
    public void diedByStone() {
        snakeEncreasing(reducedValue - 1);
        stonesAtAllPoints(true);// впереди камень
        hero.tick();
        stonesAtAllPoints(false);
        assertTrue("Маленькая змейка не погибла от камня!", !hero.isAlive());
    }

    // тест что большая змейка уменьшается от камня, но не погибает
    @Test
    public void reduceByStone() {
        snakeEncreasing(reducedValue);
        int before = hero.size();
        stonesAtAllPoints(true);// впереди камень
        hero.tick();
        stonesAtAllPoints(false);
        assertTrue("Большая змейка погибла от камня!", hero.isAlive());
        assertEquals("Змейка укоротилась раньше чем должна!", before, hero.size());
        hero.tick();
        assertTrue("Большая змейка погибла от камня!", hero.isAlive());
        assertEquals("Змейка не укоротилась на предполагаемую длину!", before - reducedValue, hero.size());
    }

    // змейка может откусить себе хвост
    @Test
    public void reduceItself() {
        int additionLength = 5;
        snakeEncreasing(additionLength);
        assertEquals("Змейка не удлиннилась!", additionLength + 2, hero.size());
        hero.down();
        hero.tick();
        hero.left();
        hero.tick();
        hero.up();
        hero.tick();
        assertTrue("Змейка погибла укусив свой хвост!", hero.isAlive());
        assertEquals("Укусив свой хвост, змейка не укоротилась!", 4, hero.size());
    }

    // если змейка съела камень, камень внутри неё
    // и она может вернуть его на поле
    @Test
    public void eatStone() {
        int additionLength = 4;
        int stonesCount = 0;
        for (int i = 0; i < 4; i++) {
            snakeEncreasing(additionLength);
            stonesAtAllPoints(true);
            hero.tick();
            stonesAtAllPoints(false);
            hero.tick();
            assertTrue("Змейка погибла!", hero.isAlive());
            assertEquals("Съев камень, он не появился внутри змейки!", ++stonesCount, hero.getStonesCount());
        }
        // возврат камней
        // невозможно поставить
        canSetStone(false);
        for (int i = 0; i < 4; i++) {
            hero.act();
            assertTrue("Змейка погибла!", hero.isAlive());
            assertEquals("Количество камней в змейке уменьшилось!", stonesCount, hero.getStonesCount());
        }
        // возможно поставить
        canSetStone(true);
        for (int i = 0; i < 4; i++) {
            hero.act();
            assertTrue("Змейка погибла!", hero.isAlive());
            assertEquals("Количество камней в змейке не уменьшилось!", --stonesCount, hero.getStonesCount());
        }
    }

    // если змейка съела пилюлю полёта, 10 ходов она действует
    @Test
    public void eatFlyingPill() {
        flyingPillsAtAllPoints(true);
        hero.tick();
        flyingPillsAtAllPoints(false);
        for (int i = 1; i <= 10; i++) {
            hero.tick();
            assertEquals("Оставшееся количество ходов полёта не соответствует ожидаемому.",
                    10 - i, hero.getFlyingCount());
        }
        assertEquals("Количество ходов полёта не может быть меньше 0.", 0, hero.getFuryCount());
    }

    // если змейка съела пилюлю ярости, 10 ходов она действует
    @Test
    public void eatFuryPill() {
        furyPillsAtAllPoints(true);
        hero.tick();
        furyPillsAtAllPoints(false);
        for (int i = 0; i <= 10; i++) {
            assertEquals("Оставшееся количество ходов ярости не соответствует ожидаемому.",
                    10 - i, hero.getFuryCount());
            hero.tick();
        }
        assertEquals("Количество ходов ярости не может быть меньше 0.", 0, hero.getFuryCount());
    }

    private void applesAtAllPoints(boolean enable) {
        when(game.isApple(any(Point.class))).thenReturn(enable);// впереди яблоко
    }

    private void flyingPillsAtAllPoints(boolean enable) {
        when(game.isFlyingPill(any(Point.class))).thenReturn(enable);// впереди пилюля полёта
    }

    private void furyPillsAtAllPoints(boolean enable) {
        when(game.isFuryPill(any(Point.class))).thenReturn(enable);// впереди пилюля ярости
    }

    private void goldAtAllPoints(boolean enable) {
        when(game.isGold(any(Point.class))).thenReturn(enable);// впереди золото
    }

    private void stonesAtAllPoints(boolean enable) {
        when(game.isStone(any(Point.class))).thenReturn(enable);// впереди камень
    }

    private void wallsAtAllPoints(boolean enable) {
        when(game.isBarrier(any(Point.class))).thenReturn(enable);// впереди стена
    }

    // установка камней
    private void canSetStone(boolean enable) {
        when(game.setStone(any(Point.class))).thenReturn(enable);
    }


}
