package com.codenjoy.dojo.services.lock;

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


import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.hero.HeroData;

import java.util.concurrent.locks.ReadWriteLock;

public class LockedGame implements Game {
    private final LockedJoystick joystick;
    private ReadWriteLock lock;

    private Game game;

    public LockedGame(ReadWriteLock lock) {
        this.lock = lock;
        this.joystick = new LockedJoystick(lock);
    }

    public Game wrap(Game game) {
        this.game = game;
        return this;
    }

    @Override
    public Joystick getJoystick() {
        return joystick.wrap(game.getJoystick());
    }

    @Override
    public int getMaxScore() {
        lock.writeLock().lock();
        try {
            return game.getMaxScore();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getCurrentScore() {
        lock.writeLock().lock();
        try {
            return game.getCurrentScore();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isGameOver() {
        lock.writeLock().lock();
        try {
            return game.isGameOver();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void newGame() {
        lock.writeLock().lock();
        try {
            game.newGame();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Object getBoardAsString() {
        lock.writeLock().lock();
        try {
            return game.getBoardAsString();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void destroy() {
        lock.writeLock().lock();
        try {
            game.destroy();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void clearScore() {
        lock.writeLock().lock();
        try {
            game.clearScore();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public HeroData getHero() {
        lock.writeLock().lock();
        try {
            return game.getHero();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getSave() {
        lock.writeLock().lock();
        try {
            return game.getSave();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void tick() {
        synchronized (this) { // TODO это я с перепугу написал, потому как lock.writeLock().lock() глючит
            lock.writeLock().lock();
            try {
                game.tick();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public String toString() {
        lock.writeLock().lock();
        try {
            return game.toString();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
