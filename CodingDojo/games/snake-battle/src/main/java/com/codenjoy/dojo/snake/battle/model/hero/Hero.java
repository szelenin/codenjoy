package com.codenjoy.dojo.snake.battle.model.hero;

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
import com.codenjoy.dojo.services.multiplayer.PlayerHero;
import com.codenjoy.dojo.snake.battle.model.Player;
import com.codenjoy.dojo.snake.battle.model.board.Field;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.snake.battle.model.hero.BodyDirection.*;
import static com.codenjoy.dojo.snake.battle.model.DirectionUtils.getPointAt;
import static com.codenjoy.dojo.snake.battle.model.hero.TailDirection.*;

public class Hero extends PlayerHero<Field> implements State<LinkedList<Tail>, Player> {
    static final int reducedValue = 4;

    private LinkedList<Tail> elements;
    private boolean alive;
    private Direction direction;
    private int growBy;
    private boolean active;
    private int stonesCount;
    private int flyingCount;
    private int furyCount;

    public Hero(Point xy) {
        elements = new LinkedList<>();
        elements.add(new Tail(xy.getX() - 1, xy.getY(), this));
        elements.add(new Tail(xy, this));
        growBy = 0;
        direction = RIGHT;
        alive = true;
        active = false;
        stonesCount = 0;
        flyingCount = 0;
        furyCount = 0;
    }

    public List<Tail> getBody() {
        return elements;
    }

    public Point getTailPoint() {
        return elements.getFirst();
    }

    @Override
    public int getX() {
        return getHead().getX();
    }

    @Override
    public int getY() {
        return getHead().getY();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!(o instanceof Hero)) {
            throw new IllegalArgumentException("Must be Hero!");
        }

        return o == this;
    }

    public int size() {
        return elements == null ? 0 : elements.size();
    }

    public Point getHead() {
        if (elements.isEmpty())
            return pt(-1, -1);
        return elements.getLast();
    }

    public Point getNeck() {
        if (elements.size() < 2)
            return pt(-1, -1);
        return elements.get(elements.size() - 2);
    }

    @Override
    public void init(Field field) {
        this.field = field;
    }

    @Override
    public void down() {
        setDirection(DOWN);
    }

    @Override
    public void up() {
        setDirection(UP);
    }

    @Override
    public void left() {
        setDirection(LEFT);
    }

    @Override
    public void right() {
        setDirection(RIGHT);
    }

    private void setDirection(Direction d) {
        if (!isAlive() || !isActive())
            return;
        if (d.equals(direction.inverted()))
            return;
        direction = d;
    }

    @Override
    public void act(int... p) {
        if (!isActive())
            return;
        if (!alive)
            return;
        if (stonesCount > 0) {
            Point to = getTailPoint();
            if (field.setStone(to))
                stonesCount--;
        }
    }

    Direction getDirection() {
        return direction;
    }

    @Override
    public void tick() {
        if (!isActive())
            return;
        if (!alive) {
            clear();
            return;
        }
        reduceIfShould();
        count();

        Point next = getNextPoint();

        if (field.isApple(next))
            growBy(1);
        if (field.isStone(next) && !isFlying()) {
            stonesCount++;
            if (!isFury())
                reduce(reducedValue);
        }
        if (field.isFlyingPill(next))
            flyingCount += 10;
        if (field.isFuryPill(next))
            furyCount += 10;
        if (field.isBarrier(next))
            die();
        if (elements.contains(next) && !isFlying())
            selfReduce(next);

        if (growBy > 0)
            grow(next);
        else
            go(next);
    }

    private void count() {
        if (isFlying())
            flyingCount--;
        if (isFury())
            furyCount--;
    }

    private void reduceIfShould() {
        if (growBy < 0) {
            if (growBy < -elements.size())
                die();
            else
                elements = new LinkedList<>(elements.subList(-growBy, elements.size()));
            growBy = 0;
        }
    }

    private void selfReduce(Point from) {
        if (from.equals(getTailPoint()))
            return;
        elements = new LinkedList<>(elements.subList(elements.indexOf(from), elements.size()));
    }

    public void reduceFromPoint(Point from) {
        elements = new LinkedList<>(elements.subList(elements.indexOf(from) + 1, elements.size()));
    }

    public void reduce(int reducedValue) {
        if (size() < reducedValue + 2)
            die();
        else
            growBy = -reducedValue;
    }

    public Point getNextPoint() {
        return getPointAt(getHead(), direction);
    }

    private void grow(Point newLocation) {
        growBy--;
        elements.add(new Tail(newLocation, this));
    }

    private void go(Point newLocation) {
        elements.add(new Tail(newLocation, this));
        elements.removeFirst();
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public LinkedList<Tail> state(Player player, Object... alsoAtPoint) {
        return elements;
    }

    BodyDirection getBodyDirection(Tail curr) {
        int currIndex = elements.indexOf(curr);
        Point prev = elements.get(currIndex - 1);
        Point next = elements.get(currIndex + 1);

        BodyDirection nextPrev = orientation(next, prev);
        if (nextPrev != null) {
            return nextPrev;
        }

        if (orientation(prev, curr) == HORIZONTAL) {
            boolean clockwise = curr.getY() < next.getY() ^ curr.getX() > prev.getX();
            if (curr.getY() < next.getY()) {
                return (clockwise) ? TURNED_RIGHT_UP : TURNED_LEFT_UP;
            } else {
                return (clockwise) ? TURNED_LEFT_DOWN : TURNED_RIGHT_DOWN;
            }
        } else {
            boolean clockwise = curr.getX() < next.getX() ^ curr.getY() < prev.getY();
            if (curr.getX() < next.getX()) {
                return (clockwise) ? TURNED_RIGHT_DOWN : TURNED_RIGHT_UP;
            } else {
                return (clockwise) ? TURNED_LEFT_UP : TURNED_LEFT_DOWN;
            }
        }
    }

    private BodyDirection orientation(Point curr, Point next) {
        if (curr.getX() == next.getX()) {
            return VERTICAL;
        } else if (curr.getY() == next.getY()) {
            return HORIZONTAL;
        } else {
            return null;
        }
    }

    TailDirection getTailDirection() {
        Point body = elements.get(1);
        Point tail = getTailPoint();

        if (body.getX() == tail.getX()) {
            return (body.getY() < tail.getY()) ? VERTICAL_UP : VERTICAL_DOWN;
        } else {
            return (body.getX() < tail.getX()) ? HORIZONTAL_RIGHT : HORIZONTAL_LEFT;
        }
    }

    boolean itsMyHead(Point point) {
        return (getHead().itsMe(point));
    }

    boolean itsMyTail(Point point) {
        return getTailPoint().itsMe(point);
    }

    private void growBy(int val) {
        growBy += val;
    }

    private void clear() {
        elements = new LinkedList<>();
        growBy = 0;
    }

    public void die() {
        alive = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getStonesCount() {
        return stonesCount;
    }

    public int getFlyingCount() {
        return flyingCount;
    }

    public int getFuryCount() {
        return furyCount;
    }

    public boolean isFlying() {
        return flyingCount > 0;
    }

    public boolean isFury() {
        return furyCount > 0;
    }
}
