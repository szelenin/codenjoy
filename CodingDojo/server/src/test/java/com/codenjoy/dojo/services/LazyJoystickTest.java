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
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

/**
 * Created by indigo on 2016-11-23.
 */
public class LazyJoystickTest {
    private Game game;
    private PlayerSpy playerSpy;
    private LazyJoystick lazy;
    private Joystick original;

    @Before
    public void setup() {
        original = mock(Joystick.class);
        game = mock(Game.class);
        when(game.getJoystick()).thenReturn(original);
        playerSpy = new PlayerSpy();
        lazy = new LazyJoystick(game, playerSpy);
    }

    @Test
    public void testSendMessageAfterTick() {
        // when
        lazy.message("message");

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).message("message");

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendUpDirectionAfterTick() {
        // when
        lazy.up();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).up();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendDownDirectionAfterTick() {
        // when
        lazy.down();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).down();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendLeftDirectionAfterTick() {
        // when
        lazy.left();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).left();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendRightDirectionAfterTick() {
        // when
        lazy.right();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).right();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendOnlyLastDirectionAfterTick() {
        // when
        lazy.up();
        lazy.down();
        lazy.left();
        lazy.right();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).right();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendDirectionBeforeAct() {
        // when
        lazy.up();
        lazy.act(1);

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        InOrder inOrder = inOrder(original);
        inOrder.verify(original).up();
        inOrder.verify(original).act(1);

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendDirectionAfterAct() {
        // when
        lazy.act(1);
        lazy.up();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        InOrder inOrder = inOrder(original);
        inOrder.verify(original).act(1);
        inOrder.verify(original).up();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendDirectionAfterActWhenSeveralParameters() {
        // when
        lazy.act(1, 2, 3);
        lazy.up();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        InOrder inOrder = inOrder(original);
        inOrder.verify(original).act(1, 2, 3);
        inOrder.verify(original).up();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendDirectionBeforeActWhenNoParameters() {
        // when
        lazy.up();
        lazy.act();

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        InOrder inOrder = inOrder(original);
        inOrder.verify(original).up();
        inOrder.verify(original).act();

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testSendOnlyAct() {
        // when
        lazy.act(2);

        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verify(original).act(2);

        verifyNoMoreInteractions(original);
    }

    @Test
    public void testNoMartiniNoParty() {
        // when
        verifyNoMoreInteractions(original);

        // when
        lazy.tick();

        // then
        verifyNoMoreInteractions(original);
    }
}