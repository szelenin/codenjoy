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


import com.codenjoy.dojo.services.nullobj.NullGameType;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component("gameService")
public class GameServiceImpl implements GameService {

    @Autowired private TimerService timer;
    @Autowired private PlayerService players;

    private Map<String, GameType> cache = new TreeMap<>();

    public GameServiceImpl() {
        for (Class<? extends GameType> clazz : allGames()) {
            GameType gameType = loadGameType(clazz);
            cache.put(gameType.name(), gameType);
        }
    }

    private List<Class<? extends GameType>> allGames() {
        List<Class<? extends GameType>> result = new LinkedList<>();
        result.addAll(findInPackage("com"));
        result.addAll(findInPackage("org"));
        result.addAll(findInPackage("net"));

        Collections.sort(result, Comparator.comparing(Class::getName));

        result.remove(NullGameType.class);
        result.remove(AbstractGameType.class);

        // TODO исключить нерабочие игры
        // result.stream().filter(it -> it.getPackage().toString().contains("chess")).findFirst().ifPresent(result::remove);

        return result;
    }

    private Collection<? extends Class<? extends GameType>> findInPackage(String packageName) {
        return new Reflections(packageName).getSubTypesOf(GameType.class);
    }

    @Override
    public Set<String> getGameNames() {
        return cache.keySet();
    }

    @Override
    public Map<String, List<String>> getSprites() {
        return cache.entrySet().stream()
                .map(entry -> new HashMap.SimpleEntry<>(
                        entry.getValue().name(),
                        Arrays.stream(entry.getValue().getPlots())
                                .map(plot -> plot.name().toLowerCase())
                                .collect(toList())
                ))
                .collect(toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue()
                ));
    }

    private GameType loadGameType(Class<? extends GameType> gameType) {
        try {
            return gameType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameType getGame(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        }

        return NullGameType.INSTANCE;
    }
}
