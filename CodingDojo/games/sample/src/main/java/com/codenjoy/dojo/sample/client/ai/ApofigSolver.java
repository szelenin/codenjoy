package com.codenjoy.dojo.sample.client.ai;

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


import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.sample.client.Board;
import com.codenjoy.dojo.sample.model.Elements;
import com.codenjoy.dojo.sample.services.GameRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;

import java.util.Arrays;
import java.util.List;

/**
 * Это алгоритм твоего бота. Он будет запускаться в игру с первым
 * зарегистрировавшимся игроком, чтобы ему не было скучно играть самому.
 * Реализуй его как хочешь, хоть на Random.
 * Для его запуска воспользуйся методом {@see ApofigSolver#main}
 */
public class ApofigSolver implements Solver<Board> {

    private DeikstraFindWay way;

    public ApofigSolver(Dice dice) {
        this.way = new DeikstraFindWay();
    }

    public DeikstraFindWay.Possible possible(final Board board) {
        return new DeikstraFindWay.Possible() {
            @Override
            public boolean possible(Point from, Direction where) {
                int x = from.getX();
                int y = from.getY();
                if (board.isBarrierAt(x, y)) return false;
                if (board.isBombAt(x, y)) return false;

                Point newPt = where.change(from);
                int nx = newPt.getX();
                int ny = newPt.getY();

                if (board.isOutOfField(nx, ny)) return false;

                if (board.isBarrierAt(nx, ny)) return false;
                if (board.isBombAt(nx, ny)) return false;

                return true;
            }

            @Override
            public boolean possible(Point atWay) {
                return true;
            }
        };
    }

    @Override
    public String get(final Board board) {
        if (board.isGameOver()) return "";
        List<Direction> result = getDirections(board);
        if (result.isEmpty()) return "";
        return result.get(0).toString() + getBombIfNeeded(board);
    }

    private String getBombIfNeeded(Board board) {
        Point me = board.getMe();
        if (me.getX() % 2 == 0 && me.getY() % 2 == 0) {
            return ", ACT";
        } else {
            return "";
        }
    }

    public List<Direction> getDirections(Board board) {
        int size = board.size();
        if (bombsNear(board)) {
            return Arrays.asList(Direction.random());
        }

        Point from = board.getMe();
        List<Point> to = board.get(Elements.GOLD);
        DeikstraFindWay.Possible map = possible(board);
        return way.getShortestWay(size, from, to, map);
    }

    // TODO fix Deikstra find way
    private boolean bombsNear(Board board) {
        Point me = board.getMe();
        Point atLeft = Direction.LEFT.change(me);
        Point atRight = Direction.RIGHT.change(me);
        Point atUp = Direction.UP.change(me);
        Point atDown = Direction.DOWN.change(me);

        return board.isAt(atLeft.getX(), atLeft.getY(), Elements.BOMB, Elements.WALL, Elements.OTHER_HERO) &&
                board.isAt(atRight.getX(), atRight.getY(), Elements.BOMB, Elements.WALL, Elements.OTHER_HERO) &&
                board.isAt(atUp.getX(), atUp.getY(), Elements.BOMB, Elements.WALL, Elements.OTHER_HERO) &&
                board.isAt(atDown.getX(), atDown.getY(), Elements.BOMB, Elements.WALL, Elements.OTHER_HERO);
    }

    public static void main(String[] args) {
//        LocalGameRunner.run(new GameRunner(),
//                new ApofigSolver(new RandomDice()),
//                new Board());
        start(WebSocketRunner.DEFAULT_USER, WebSocketRunner.Host.LOCAL);
    }

    public static void start(String name, WebSocketRunner.Host host) {
        WebSocketRunner.run(host,
                name,
                new ApofigSolver(new RandomDice()),
                new Board());
    }

}
