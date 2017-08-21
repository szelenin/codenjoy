package com.codenjoy.dojo.kata.client;

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


import com.codenjoy.dojo.client.OneCommandSolver;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.kata.model.Elements;

/**
 * User: your name
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver extends AbstractTextSolver {

    private static final String USER_NAME = "user@gmail.com";

    @Override
    public Strings getAnswers(int level, Strings questions) {
        Strings answers = new Strings();
        for (String question : questions) {
            answers.add(algorithm(level, question));
        }
        return answers;
    }

    private String algorithm(int level, String question) {
        if (level == 0) {
            if (question.equals("hello")) {
                return "world";
            }

            if (question.equals("world")) {
                return "hello";
            }
            
            return question;
        } else {
            return "your answer"; // TODO implement me 
        }
    }

    public static void main(String[] args) {
        run(new YourSolver());
    }

    private static void run(Solver solver) {
//        WebSocketRunner.runOnServer("192.168.1.1:8080", // to use for local server
        WebSocketRunner.run(WebSocketRunner.Host.REMOTE,  // to use for codenjoy.com server
                USER_NAME,
                solver,
                new Board());
    }
    
    public static class StartNextLevel {
        public static void main(String[] args) {
            run(new OneCommandSolver<Board>("message('" + Elements.START_NEXT_LEVEL + "')"));
        }
    }

    public static class SkipThisLevel extends YourSolver {
        public static void main(String[] args) {
            run(new OneCommandSolver<Board>("message('" + Elements.SKIP_THIS_LEVEL + "')"));
        }
    }
}
