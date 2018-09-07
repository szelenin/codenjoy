package com.codenjoy.dojo.services.multiplayer;

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

import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.GameType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

public class Spreader {

    private Map<String, List<Room>> rooms = new HashMap<>();

    public GameField getField(GamePlayer player, String gameType, int count, Supplier<GameField> supplier) {
        Room room = findUnfilled(gameType);
        if (room == null) {
            room = new Room(supplier.get(), count);
            add(gameType, room);
        }

        GameField field = room.getField(player);
        return field;
    }

    private void add(String gameType, Room room) {
        List<Room> rooms = getRooms(gameType);
        rooms.add(room);
    }

    private Room findUnfilled(String gameType) {
        List<Room> rooms = getRooms(gameType);
        if (rooms.isEmpty()) {
            return null;
        }
        return rooms.stream()
                .filter(Room::isFree)
                .findFirst()
                .orElse(null);
    }

    private List<Room> getRooms(String gameType) {
        List<Room> result = rooms.get(gameType);
        if (result == null) {
            rooms.put(gameType, result = new LinkedList<>());
        }
        return result;
    }

    public List<GamePlayer> remove(Game game) {
        List<GamePlayer> removed = new LinkedList<>();

        GamePlayer player = game.getPlayer();
        List<Room> playerRooms = roomsFor(player);

        playerRooms.forEach(room -> {
            List<GamePlayer> players = room.getPlayers();
            players.remove(player);

            if (players.size() == 1) {
                GamePlayer lastPlayer = players.iterator().next();
                removed.add(lastPlayer);
                players.remove(lastPlayer);
            }
            if (players.isEmpty()) {
                rooms.values().forEach(it -> it.remove(room));
            }
        });

        return removed;
    }

    private List<Room> roomsFor(GamePlayer player) {
        return allRooms().stream()
                    .filter(r -> r.contains(player))
                    .collect(toList());
    }

    private List<Room> allRooms() {
        return rooms.values().stream()
                .flatMap(List::stream)
                .collect(toList());
    }

    public void play(Game game, GameType gameType) {
        GameField field = getField(game.getPlayer(),
                gameType.name(),
                gameType.getMultiplayerType().getCount(),
                gameType::createGame);

        game.on(field);
    }

    public boolean contains(Game game) {
        return !roomsFor(game.getPlayer()).isEmpty();
    }
}
