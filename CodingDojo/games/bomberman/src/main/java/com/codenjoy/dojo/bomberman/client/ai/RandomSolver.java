package com.codenjoy.dojo.bomberman.client.ai;

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


import com.codenjoy.dojo.bomberman.client.Board;
import com.codenjoy.dojo.bomberman.model.Elements;
import com.codenjoy.dojo.bomberman.services.GameRunner;
import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;

public class RandomSolver implements Solver<Board> {
    private Dice dice;

    public RandomSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        Direction direction = null;
        int toX = 0;
        int toY = 0;
        do {
            direction = Direction.values()[dice.next(5)];

            Point bomberman = board.getBomberman();
            toX = direction.changeX(bomberman.getX());
            toY = direction.changeY(bomberman.getY());

        } while (board.isAt(toX, toY, Elements.WALL) || board.isAt(toX, toY, Elements.DESTROY_WALL));

        String command = direction.toString();
        if (!command.equalsIgnoreCase("act") && !command.equalsIgnoreCase("stop")) {
            if (dice.next(5) == 0) {
                command = "act, " + command;
            }
        }
        return command;
    }

    public static void main(String[] args) {
//        LocalGameRunner.run(new GameRunner(),
//                new PongSolver(new RandomDice()),
//                new Board());
        start(WebSocketRunner.DEFAULT_USER, WebSocketRunner.Host.LOCAL);
    }

    public static void start(String name, WebSocketRunner.Host host) {
        WebSocketRunner.run(host,
                name,
                new RandomSolver(new RandomDice()),
                new Board());
    }
}
