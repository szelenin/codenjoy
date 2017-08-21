package com.codenjoy.dojo.kata.model;

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


import com.codenjoy.dojo.kata.model.levels.Level;
import com.codenjoy.dojo.kata.model.levels.LevelsPool;
import com.codenjoy.dojo.kata.model.levels.LevelsPoolImpl;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.PrinterFactory;
import com.codenjoy.dojo.services.hero.GameMode;
import com.codenjoy.dojo.services.hero.HeroData;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;

import java.util.List;

/**
 * А вот тут немного хак :) Дело в том, что фреймворк изначально не поддерживал игры типа "все на однмо поле", а потому
 * пришлось сделать этот декоратор. Борда (@see Sample) - одна на всех, а игры (@see Single) у каждого своя. Кода тут не много.
 */
public class Single implements Game {

    private Player player;
    private Kata game;

    public Single(Kata game, EventListener listener, PrinterFactory factory, List<Level> levels) {
        this.game = game;
        LevelsPool pool = new LevelsPoolImpl(levels);
        this.player = new Player(listener, pool);
    }

    @Override
    public Joystick getJoystick() {
        return player.getHero();
    }

    @Override
    public int getMaxScore() {
        return player.getMaxScore();
    }

    @Override
    public int getCurrentScore() {
        return 0;
    }

    @Override
    public boolean isGameOver() {
        return !player.hero.isAlive();
    }

    @Override
    public void newGame() {
        game.newGame(player);
    }

    @Override
    public JSONObject getBoardAsString() {
        JSONObject result = new JSONObject();

        result.put("description", StringEscapeUtils.escapeJava(player.getDescription()));
        result.put("level", player.getLevel());
        result.put("questions", player.getQuestions());
        result.put("nextQuestion", player.getNextQuestion());
        result.put("history", player.getLastHistory());

        return result;
    }

    @Override
    public void destroy() {
        game.remove(player);
    }

    @Override
    public void clearScore() {
        player.clearScore();
    }

    @Override
    public HeroData getHero() {
        return GameMode.heroOnTheirOwnBoard(player.getLevel() - 1);
    }

    @Override
    public String getSave() {
        return null;
    }

    @Override
    public void tick() {
        game.tick();
    }

    @Override
    public String toString() {
        return player.toString();
    }
}
