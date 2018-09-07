package com.codenjoy.dojo.services.dao;

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


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.jdbc.SqliteConnectionThreadPoolFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerGameSaverTest {

    private static final long TIME = 1382702580000L;
    private PlayerGameSaver saver;

    @Before
    public void removeAll() {
        String dbFile = "target/saves.db" + new Random().nextInt();
        saver = new PlayerGameSaver(
                new SqliteConnectionThreadPoolFactory(dbFile,
                        new ContextPathGetter() {
                            @Override
                            public String getContext() {
                                return "context";
                            }
                        }));
    }

    @After
    public void cleanUp() {
        saver.removeDatabase();
    }

    @Test
    public void shouldWorks_saveLoadPlayerGame() {
        PlayerScores scores = getScores(10);
        Information info = getInfo("Some info");
        GameService gameService = getGameService(scores);
        Player player = new Player("vasia", "http://127.0.0.1:8888", PlayerTest.mockGameType("game"), scores, info);

        saver.saveGame(player, "{'key':'value'}");

        PlayerSave loaded = saver.loadGame("vasia");
        assertEqualsProperties(player, loaded);
        assertEquals("{'key':'value'}", loaded.getSave());

        saver.delete("vasia");

        assertEquals("[]", saver.getSavedList().toString());
    }

    private GameType getGameType(PlayerScores scores) {
        GameType gameType = mock(GameType.class);
        when(gameType.getPlayerScores(anyInt())).thenReturn(scores);
        return gameType;
    }

    private GameService getGameService(PlayerScores scores) {
        GameService gameService = mock(GameService.class);
        GameType gameType = getGameType(scores);
        when(gameService.getGame(anyString())).thenReturn(gameType);
        return gameService;
    }

    private Information getInfo(String string) {
        Information info = mock(Information.class);
        when(info.getMessage()).thenReturn(string);
        return info;
    }

    private PlayerScores getScores(int value) {
        PlayerScores scores = mock(PlayerScores.class);
        when(scores.getScore()).thenReturn(value);
        return scores;
    }

    private void assertEqualsProperties(Player expected, PlayerSave actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCallbackUrl(), actual.getCallbackUrl());
        assertEquals(expected.getScore(), actual.getScore());
    }

    @Test
    public void shouldWorks_getSavedList() {
        Player player1 = new Player("vasia", "http://127.0.0.1:8888", PlayerTest.mockGameType("game"), getScores(10), getInfo("Some other info"));
        Player player2 = new Player("katia", "http://127.0.0.3:7777", PlayerTest.mockGameType("game"), getScores(20), getInfo("Some info"));

        saver.saveGame(player1, "{'key':'value'}");
        saver.saveGame(player2, "{'key':'value'}");

        assertEquals("[vasia, katia]", saver.getSavedList().toString());
    }
}
