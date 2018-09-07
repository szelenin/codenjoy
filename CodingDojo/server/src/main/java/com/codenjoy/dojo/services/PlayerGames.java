package com.codenjoy.dojo.services;

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


import com.codenjoy.dojo.services.lock.LockedGame;
import com.codenjoy.dojo.services.multiplayer.*;
import com.codenjoy.dojo.services.nullobj.NullPlayerGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import static com.codenjoy.dojo.services.PlayerGame.by;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class PlayerGames implements Iterable<PlayerGame>, Tickable {

    private static Logger logger = LoggerFactory.getLogger(PlayerGames.class);

    private List<PlayerGame> playerGames = new LinkedList<>();

    private Consumer<PlayerGame> onAdd;
    private Consumer<PlayerGame> onRemove;
    private ReadWriteLock lock;
    private Spreader spreader = new Spreader();

    public void onAdd(Consumer<PlayerGame> consumer) {
        this.onAdd = consumer;
    }

    public void onRemove(Consumer<PlayerGame> consumer) {
        this.onRemove = consumer;
    }

    public void init(ReadWriteLock lock) {
        this.lock = lock;
    }

    public PlayerGames() {
        lock = new ReentrantReadWriteLock();
    }

    public void remove(Player player) {
        int index = playerGames.indexOf(player);
        if (index == -1) return;
        PlayerGame toRemove = playerGames.remove(index);

        List<PlayerGame> gamePlayers = remove(toRemove.getGame());
        gamePlayers.forEach(gp -> spreader.play(gp.getGame(), gp.getGameType()));

        toRemove.remove(onRemove);
        toRemove.getGame().on(null);
    }

    public PlayerGame get(String playerName) {
        return playerGames.stream()
                .filter(pg -> pg.getPlayer().getName().equals(playerName))
                .findFirst()
                .orElse(NullPlayerGame.INSTANCE);
    }

    public PlayerGame get(GamePlayer player) {
        return playerGames.stream()
                .filter(pg -> pg.getGame().getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    public PlayerGame add(Player player, PlayerSave save) {
        GameType gameType = player.getGameType();

        GamePlayer gamePlayer = gameType.createPlayer(player.getEventListener(),
                (save == null) ? "" : save.getSave(), player.getName());

        Single single = new Single(gamePlayer,
                gameType.getPrinterFactory(),
                gameType.getMultiplayerType());

        spreader.play(single, gameType);

        Game game = new LockedGame(lock).wrap(single);

        PlayerGame playerGame = new PlayerGame(player, game);
        if (onAdd != null) {
            onAdd.accept(playerGame);
        }
        playerGames.add(playerGame);
        return playerGame;
    }

    private List<PlayerGame> remove(Game toRemove) {
        if (spreader.contains(toRemove)) {
            List<GamePlayer> removed = spreader.remove(toRemove);
            List<PlayerGame> result = removed.stream()
                    .map(p -> get(p))
                    .collect(toList());
            while (result.contains(null)) { // TODO как-то странно так делать
                result.remove(null);
            }
            result.forEach(pg -> pg.getGame().on(null));
            return result;
        } else {
            return Arrays.asList();
        }
    }

    public boolean isEmpty() {
        return playerGames.isEmpty();
    }

    @Override
    public Iterator<PlayerGame> iterator() {
        return playerGames.iterator();
    }

    public List<Player> players() {
        return playerGames.stream()
                .map(PlayerGame::getPlayer)
                .collect(toList());
    }

    public int size() {
        return playerGames.size();
    }

    public void clear() {
        players().forEach(this::remove);
    }

    public List<PlayerGame> getAll(String gameType) {
        return playerGames.stream()
                .filter(pg -> pg.getPlayer().getGameName().equals(gameType))
                .collect(toList());
    }

    public List<GameType> getGameTypes() {
        List<GameType> result = new LinkedList<>();

        for (PlayerGame playerGame : playerGames) {
            GameType gameType = playerGame.getGameType();
            if (!result.contains(gameType)) {
                result.add(gameType);
            }
        }

        return result;
    }

    @Override
    public void tick() {
        // по всем джойстикам отправили сообщения играм
        playerGames.forEach(PlayerGame::quietTick);

        // создаем новые игры для тех, кто уже game over
        // TODO если игра игрока многопользовательская то
        // он должен добавиться в новую борду
        // ну и последний играющий игрок на борде так же должен ее покинуть
        for (PlayerGame playerGame : playerGames) {
            Game game = playerGame.getGame();
            if (game.isGameOver()) {
                quiet(() -> {
                    GameType gameType = getPlayer(game).getGameType();
// TODO так не сработает потому что оно всегда будет удалять только что связанных с бордой плееров
//                    spreader.replay(game, gameType);
                    game.newGame();
                });
            }
        }

        // собираем все уникальные борды
        // независимо от типа игры нам нужно тикнуть все
        playerGames.stream()
                .map(PlayerGame::getField)
                .collect(toSet())
                .forEach(GameField::quietTick);

        // ну и тикаем все GameRunner мало ли кому надо на это подписаться
        getGameTypes().forEach(GameType::quietTick);
    }

    private Player getPlayer(Game game) {
        return playerGames.stream()
                .filter(pg -> pg.equals(by(game)))
                .findFirst()
                .orElseThrow(IllegalStateException::new)
                .getPlayer();
    }

    private void quiet(Runnable runnable) {
        ((Tickable)() -> runnable.run()).quietTick();
    }


    public void clean() {
        new LinkedList<>(playerGames)
                .forEach(pg -> remove(pg.getPlayer()));
    }

    public List<Player> getPlayers(String gameName) {
        return playerGames.stream()
                .map(playerGame -> playerGame.getPlayer())
                .filter(player -> player.getGameName().equals(gameName))
                .collect(toList());
    }
}
