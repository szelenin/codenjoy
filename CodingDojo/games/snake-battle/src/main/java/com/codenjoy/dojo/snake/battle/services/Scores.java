package com.codenjoy.dojo.snake.battle.services;

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


import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

public class Scores implements PlayerScores {

    private final Parameter<Integer> winScore;
    private final Parameter<Integer> stillAliveScore;
    private final Parameter<Integer> appleScore;
    private final Parameter<Integer> goldScore;
    private final Parameter<Integer> diePenalty;
    private final Parameter<Integer> stonePenalty;

    private volatile int score;

    public Scores(int startScore, Settings settings) {
        this.score = startScore;

        winScore = settings.addEditBox("Win score").type(Integer.class).def(30);
        stillAliveScore = settings.addEditBox("Alive score").type(Integer.class).def(10);
        appleScore = settings.addEditBox("Apple score").type(Integer.class).def(1);
        goldScore = settings.addEditBox("Gold score").type(Integer.class).def(5);
        diePenalty = settings.addEditBox("Die penalty").type(Integer.class).def(10);
        stonePenalty = settings.addEditBox("Stone penalty").type(Integer.class).def(1);
    }

    @Override
    public int clear() {
        return score = 0;
    }

    @Override
    public Integer getScore() {
        return score;
    }

    @Override
    public void event(Object event) {
        if (!(event instanceof Events))
            return;
        switch ((Events) event) {
            case WIN:
                score += winScore.getValue();
                break;
            case ALIVE:
                score += stillAliveScore.getValue();
                break;
            case APPLE:
                score += appleScore.getValue();
                break;
            case GOLD:
                score += goldScore.getValue();
                break;
            case DIE:
                score -= diePenalty.getValue();
                break;
            case STONE:
                score -= stonePenalty.getValue();
                break;
        }
        score = Math.max(0, score);
    }
}
