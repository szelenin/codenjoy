package com.codenjoy.dojo.pong;

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


import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.pong.client.Board;
import com.codenjoy.dojo.pong.client.YourSolver;
import com.codenjoy.dojo.pong.client.ai.AISolver;
import com.codenjoy.dojo.pong.services.GameRunner;
import com.codenjoy.dojo.services.Dice;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SmokeTest {

    private Dice dice;

    @Test
    public void test() {
        // given
        List<String> messages = new LinkedList<>();

        LocalGameRunner.timeout = 0;
        LocalGameRunner.out = (e) -> messages.add(e);
        LocalGameRunner.countIterations = 15;

        Dice dice = LocalGameRunner.getDice(
                0, 1, // ball horizontal and vertical init speed vector
                0, 0, // solvers rateCoefficients
                0, 0, // -- " --
                0, 0,
                0, 0,
                0, 0);

        GameRunner gameType = new GameRunner() {
            @Override
            public Dice getDice() {
                return dice;
            }

            @Override
            protected String getMap() {
                return  "           " +
                        "-----------" +
                        "           " +
                        "           " +
                        "           " +
                        "     o     " +
                        "           " +
                        "           " +
                        "           " +
                        "-----------" +
                        "           ";
            }
        };

        // when
        LocalGameRunner.run(gameType,
                new ArrayList<Solver>() {{
                    add(new AISolver(dice));
                    add(new YourSolver(dice));
                }},
                new ArrayList<ClientBoard>() {{
                    add(new Board());
                    add(new Board());
                }});

        // then
        assertEquals("DICE:0\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #       H \n" +
                        "1: #   o   H \n" +
                        "1: #       H \n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:1\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H       # \n" +
                        "2: H   o   # \n" +
                        "2: H       # \n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1: #  o    H \n" +
                        "1:         H \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2: H  o    # \n" +
                        "2:         # \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1:   o     H \n" +
                        "1:         H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2:   o     # \n" +
                        "2:         # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1:         H \n" +
                        "1:  o      H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2:         # \n" +
                        "2:  o      # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1: o       H \n" +
                        "1:         H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: UP\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2: o       # \n" +
                        "2:         # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "Fire Event: LOOSE\n" +
                        "Fire Event: WIN\n" +
                        "DICE:0\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #   o   H \n" +
                        "1: #       H \n" +
                        "1:         H \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: UP\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H   o   # \n" +
                        "2: H       # \n" +
                        "2:         # \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #       H \n" +
                        "1: #       H \n" +
                        "1: #  o    H \n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H       # \n" +
                        "2: H       # \n" +
                        "2: H  o    # \n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1: #       H \n" +
                        "1:   o     H \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2: H       # \n" +
                        "2:   o     # \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1:         H \n" +
                        "1:  o      H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2:         # \n" +
                        "2:  o      # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1: o       H \n" +
                        "1:         H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: UP\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2: o       # \n" +
                        "2:         # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "Fire Event: LOOSE\n" +
                        "Fire Event: WIN\n" +
                        "DICE:0\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #   o   H \n" +
                        "1: #       H \n" +
                        "1:         H \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: UP\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H   o   # \n" +
                        "2: H       # \n" +
                        "2:         # \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #       H \n" +
                        "1: #       H \n" +
                        "1: #  o    H \n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H       # \n" +
                        "2: H       # \n" +
                        "2: H  o    # \n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1: #       H \n" +
                        "1:   o     H \n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2: H       # \n" +
                        "2:   o     # \n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1:         H \n" +
                        "1:  o      H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: DOWN\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2:         # \n" +
                        "2:  o      # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "------------------------------------------\n" +
                        "1:Board:\n" +
                        "1:           \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:           \n" +
                        "1: #         \n" +
                        "1: #         \n" +
                        "1: #       H \n" +
                        "1: o       H \n" +
                        "1:         H \n" +
                        "1:-----------\n" +
                        "1:           \n" +
                        "1:\n" +
                        "DICE:0\n" +
                        "1:Answer: UP\n" +
                        "2:Board:\n" +
                        "2:           \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:           \n" +
                        "2: H         \n" +
                        "2: H         \n" +
                        "2: H       # \n" +
                        "2: o       # \n" +
                        "2:         # \n" +
                        "2:-----------\n" +
                        "2:           \n" +
                        "2:\n" +
                        "2:Answer: STOP\n" +
                        "Fire Event: LOOSE\n" +
                        "Fire Event: WIN\n" +
                        "DICE:0\n" +
                        "------------------------------------------",
                String.join("\n", messages));

    }
}
