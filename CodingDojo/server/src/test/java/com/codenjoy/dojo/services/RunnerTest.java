package com.codenjoy.dojo.services;

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


import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RunnerTest {

	private Runner runner;
    private Game game;
    private Console console;
    private Joystick hero;
    private List<String> calls = new LinkedList<String>();

    @Before
	public void initMocks() {
		game = mock(Game.class);
		console = mock(Console.class);
        hero = mock(Joystick.class);
        when(game.getJoystick()).thenReturn(hero);
        when(game.getBoardAsString()).thenReturn("board");

		runner = new Runner(game, console);
	}

	// проверяем что доска будет изъята из принтера и напечатана на консоли
    // в принтер передаются все артефакты доски чтобы с них напечатать все что надо
	@Test
	public void shouldPrintBoardWhenStartGame() {
		// given
        when(game.getBoardAsString()).thenReturn("game");
        when(game.isGameOver()).thenReturn(false, true);
        when(console.read()).thenReturn("");

		// when
		runner.playGame();
		
		// then
//        verify(game, times(3)).toString();
        verify(console, times(3)).print("game");
        verify(console).print("Game over!");
	}

	// хочу проверить что после каждого цикла будет вызван метод tick()
	@Test
	public void shouldCallTactOnEachCycle() {
		// given
        when(game.isGameOver()).thenReturn(false, false, false, true);
        when(console.read()).thenReturn("");

		// when
		runner.playGame();

		// then
		verify(game, times(4)).tick();
	}

	// хочу проверить что при нажатии на S/Ы вызовется метод down бомбермена
	@Test
	public void shouldCallDownWhenPressSButton() {
		// given
        when(game.isGameOver()).thenReturn(false, true);
        when(console.read()).thenReturn("s", "ы");

		// when
		runner.playGame();

		// then
        verify(hero, times(2)).down();
	}

	// хочу проверить что при нажатии на A/Ф вызовется метод left бомбермена
	@Test
	public void shouldCallLeftWhenPressAButton() {
		// given
        when(game.isGameOver()).thenReturn(false, true);
        when(console.read()).thenReturn("a", "ф");

		// when
		runner.playGame();

		// then
        verify(hero, times(2)).left();
	}

	// хочу проверить что при нажатии на D/В вызовется метод right бомбермена
	@Test
	public void shouldCallRightWhenPressDButton() {
		// given
        when(game.isGameOver()).thenReturn(false, true);
        when(console.read()).thenReturn("d", "в");

		// when
		runner.playGame();

		// then
        verify(hero, times(2)).right();
	}

	// хочу проверить что при нажатии на W/Ц вызовется метод up бомбермена
	@Test
	public void shouldCallUpWhenPressWButton() {
		// given
        when(game.isGameOver()).thenReturn(false, true);
        when(console.read()).thenReturn("w", "ц");

		// when
		runner.playGame();

		// then
        verify(hero, times(2)).up();
	}

    // хочу проверить что при нажатии на пробел вызовется метод act бомбермена
    @Test
    public void shouldCallBombWhenPressSpaceButton() {
        // given
        when(game.isGameOver()).thenReturn(true);
        when(console.read()).thenReturn(" ");

        // when
        runner.playGame();

        // then
        verify(hero).act();
    }

    @Test
    public void shouldWorkWithManyCommands1() {
        String pattern = " * *";
        with(pattern, 's', "[act, down, tick, act, down, tick, gameover]");
        with(pattern, 'a', "[act, left, tick, act, left, tick, gameover]");
        with(pattern, 'd', "[act, right, tick, act, right, tick, gameover]");
        with(pattern, 'w', "[act, up, tick, act, up, tick, gameover]");
    }

    private void with(String pattern, char command, String expected) {
        // given
        when(console.read()).thenReturn(pattern.replace('*', command));

        calls.clear();
        init(game, "tick", null).tick();
        init(game, "gameover", true).isGameOver();
        init(hero, "right", null).right();
        init(hero, "left", null).left();
        init(hero, "down", null).down();
        init(hero, "up", null).up();
        init(hero, "act", null).act();

        // when
        runner.playGame();

        // then
        assertEquals(expected, calls.toString());
    }

    private <T> T init(T mock, final String method, final Object result) {
        return doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                calls.add(method);
                return result;
            }
        }).when(mock);
    }

    @Test
    public void shouldWorkWithManyCommands2() {
        String pattern = "* * ";
        with(pattern, 's', "[down, act, tick, down, act, tick, gameover]");
        with(pattern, 'a', "[left, act, tick, left, act, tick, gameover]");
        with(pattern, 'd', "[right, act, tick, right, act, tick, gameover]");
        with(pattern, 'w', "[up, act, tick, up, act, tick, gameover]");
    }

    @Test
    public void shouldWorkWithManyCommands3() {
        String pattern = "** *";
        with(pattern, 's', "[down, tick, down, act, tick, down, tick, gameover]");
        with(pattern, 'a', "[left, tick, left, act, tick, left, tick, gameover]");
        with(pattern, 'd', "[right, tick, right, act, tick, right, tick, gameover]");
        with(pattern, 'w', "[up, tick, up, act, tick, up, tick, gameover]");
    }

    @Test
    public void shouldWorkWithManyCommands4() {
        String pattern = " ** ";
        with(pattern, 's', "[act, down, tick, down, act, tick, gameover]");
        with(pattern, 'a', "[act, left, tick, left, act, tick, gameover]");
        with(pattern, 'd', "[act, right, tick, right, act, tick, gameover]");
        with(pattern, 'w', "[act, up, tick, up, act, tick, gameover]");
    }
	
	
}
