package com.codenjoy.dojo.pong.client.ai;

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

import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.pong.client.Board;
import com.codenjoy.dojo.pong.services.GameRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;

import java.util.List;
import java.util.Random;

public class PongSolver implements Solver<Board> {

    int previousBallPosition;
    int verticalBallDirection;

    public PongSolver(Dice dice) {

    }

    @Override
    public String get(final Board board) {
        Point ball = board.getBall();

        Random random = new Random();
        int rateCoefficient = random.nextInt(2);

        if (ball != null) {
            verticalBallDirection = ball.getY() - previousBallPosition + rateCoefficient;
            previousBallPosition = ball.getY();
            List<Point> me = board.getMe();
            String direction = getDirectionString(ball, me);
            return direction;
        }
        return "";
    }

    private String getDirectionString(Point ball, List<Point> me) {
        String direction = "";
        int ballY = ball.getY();
        Point myPosition;
        if (verticalBallDirection > 0) {
            myPosition = me.get(0);
        } else if (verticalBallDirection < 0){
            myPosition = me.get(me.size() -1);
        } else {
            myPosition = me.get(me.size()/2);
        }
        int myY = myPosition.getY();
        if (myY < ballY) {
            direction =  Direction.DOWN.toString();
        } else if (myY > ballY) {
            direction =  Direction.UP.toString();
        }
        return direction;
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
                new PongSolver(new RandomDice()),
                new Board());
    }

}
