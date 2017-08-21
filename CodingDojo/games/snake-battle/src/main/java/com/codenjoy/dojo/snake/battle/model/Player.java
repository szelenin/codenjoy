package com.codenjoy.dojo.snake.battle.model;

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


import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.battle.model.board.Field;
import com.codenjoy.dojo.snake.battle.model.hero.Hero;
import com.codenjoy.dojo.snake.battle.services.Events;

/**
 * Класс игрока. Тут кроме героя может подсчитываться очки. Тут же ивенты передабтся лиснеру фреймворка.
 */
public class Player {

    private EventListener listener;
    private int maxScore;
    private int score;
    private Hero hero;

    /**
     * @param listener Это шпийон от фреймоврка. Ты должен все ивенты которые касаются конкретного пользователя сормить ему.
     */
    public Player(EventListener listener) {
        this.listener = listener;
        clearScore();
    }

    private void increaseScore() {
        score = score + 1;
        maxScore = Math.max(maxScore, score);
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getScore() {
        return score;
    }

    /**
     * Борда может файрить ивенты юзера с помощью этого метода
     *
     * @param event тип ивента
     */
    public void event(Events event) {
        switch (event) {
            case START:
                start();
                break;
        }

        if (listener != null) {
            listener.event(event);
        }
    }

    private void start() {
        hero.setActive(true);
    }

    public void clearScore() {
        score = 0;
        maxScore = 0;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    /**
     * Когда создается новая игра для пользователя, кто-то должен создать героя
     *
     * @param field борда
     */
    public void newHero(Field field) {
        Point pt = field.getFreeStart();
        hero = new Hero(pt);
        hero.init(field);
    }

    public boolean isAlive() {
        return isActive() && hero.isAlive();
    }

    public boolean isActive() {
        return hero != null && hero.isActive();
    }
}