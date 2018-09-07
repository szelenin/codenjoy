package com.codenjoy.dojo.snake.model.artifacts;

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
import com.codenjoy.dojo.snake.model.Field;
import com.codenjoy.dojo.snake.model.Hero;
import com.codenjoy.dojo.snake.model.Player;
import com.codenjoy.dojo.snake.model.Walls;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomArtifactGeneratorTest {

    private RandomArtifactGenerator generator;
    private Hero snake;
    private Stone stone;
    private Apple apple;
    private Field board;
    private Walls walls;

    private static final int BOARD_SIZE = 5;

    @Before
    public void initGenerator() {
        generator = new RandomArtifactGenerator(new RandomDice()); // TODO использовать моки

        initBoardMock();
        initWallsMock();

        int xy = (BOARD_SIZE - 1)/2;
        snake = new Hero(xy, xy);

        initSnake();

        stone = new Stone(0, 0);
        apple = new Apple(1, 1);
    }

    private void initSnake() {
        when(board.createSnake()).thenReturn(snake);
        EventListener listener = mock(EventListener.class);
        Player player = new Player(listener);
        player.newHero(board);
    }

    // делаем так, чтобы стенка была только слева от поля (x=0, y)
    private void initWallsMock() {
        walls = new Walls();
        for (int y = 0; y < BOARD_SIZE; y++) {
            walls.add(0, y);
        }
    }

    private void initFullWallsMock() {
        walls = new Walls();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (y == 0 || y == BOARD_SIZE || x == 0 || x == BOARD_SIZE){
                    walls.add(x, y);
                }
            }
        }
    }
    /**
     * Создаем мок доски, чтобы по ней могла двигаться змейка
     */
    private void initBoardMock() {
        board = mock(Field.class);

        // на пути змейки всегда будет пустое место
        when(board.getAt(any(Point.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Point point = (Point)invocation.getArguments()[0];
                return new EmptySpace(point);
            }
        });
    }

    // Я бы хотел потестить другой момент, что камень при каждой новой игре размещается в новом месте
    @Test
    public void shouldStoneHasRandomPositionsWhenNewGameStarted() {
        Stone firstStone = getNewStone();
        Stone secondStone = getNewStone();
        Stone thirdStone = getNewStone();

        assertPointChangePosition(firstStone, secondStone, thirdStone);
    }

    private Stone getNewStone() {
        return generator.generateStone(snake, apple, walls, BOARD_SIZE);
    }

    /**
     * метод проверяет что хоть какая-то пара артефактов из переданных в качестве аргументов находится на разных местах.
     * @param points артефакты
     */
    private void assertPointChangePosition(Point... points) {
        boolean atSame = isPointsAtSamePosition(points);
        assertFalse(String.format("Все камни за количество игр равное %s были в одной и той же позиции (%s)",
                points.length, Arrays.toString(points)),
                atSame);
    }

    /**
     * Метод говорит что какие-то из артефактов находятся на разных позициях.
     * @param points артефакты
     * @return true, если хоть два артефакта находятся на разных позициях.
     */
    private boolean isPointsAtSamePosition(Point... points) {
        for (int pointIndex = 0; pointIndex < (points.length - 1); pointIndex ++) {
            Point one = points[pointIndex];
            Point another = points[pointIndex + 1];

            if ((one.getX() != another.getX()) || one.getY() != another.getY()) {
                return false;
            }
        }
        return true;
    }

    // камень не может быть за пределами доски
    @Test
    public void shouldStoneAlwaysAtTheBoard() {
        // тут поставил цикл, чтобы проверить что никогда
        // камень не генерится за пределами доски
        for (int countRun = 0; countRun < 100000; countRun ++) {
            Stone stone = getNewStone();

            assertTrue("камень должен быть в перделах доски по оси X", stone.getX() < BOARD_SIZE);
            assertTrue("камень должен быть в перделах доски по оси Y", stone.getY() < BOARD_SIZE);
            assertTrue("камень должен быть в перделах доски по оси X", stone.getX() >= 0);
            assertTrue("камень должен быть в перделах доски по оси Y", stone.getY() >= 0);
        }
    }

    // но кажется я допустил еще одну ошибку при использовании Random. Надо проверить что камень когданибудь но
    // все же появится возле стенок доски. Да или вообще можно проверить что камень будет везде на поле,
    // если мы переберем достаточное количество игр
    @Test
    public void testRandomStonePosition() {
        int snakeHeadX = snake.getX();
        int snakeHeadY = snake.getY();
        int snakeTailX = snakeHeadX - 1;

        for (int y = 0; y < BOARD_SIZE; y ++) {
            for (int x = 0; x < BOARD_SIZE; x ++) {
                if (y == snakeHeadY && x >= snakeTailX) { // камень не должен появляться ни на змее, ни на ее пути ни на яблоке
                    continue;
                }
                if (x == 1 && y == 1) { // камень не должен появляться на яблоке
                    continue;
                }
                if (x == 0) { // камень не должен появляться на стене (см. initWallsMock)
                    continue;
                }
                if (x == apple.getX() + 1 && y == apple.getY() || // камень не должен создавать с яблоком и стеной тупик
                    x == apple.getX() && y == apple.getY() + 1) {
                    continue;
                }
                assertStoneInSomeGameAt(x, y);
            }
        }
    }

    /**
     * Метод проверяет что за больше число запусков игр камень будет в заданной позиции хоть один раз.
     * @param x координата x
     * @param y координата y
     */
    private void assertStoneInSomeGameAt(int x, int y) {
        boolean found = isStonePresentInSomeGameAt(x, y);
        assertTrue(String.format("Должен был быть найден камень в позиции x:%s y:%s", x, y), found);
    }

    /**
     * Метод говорит, что за больше число запусков игр камень будет в заданной позиции хоть один раз.
     * @param x координата x
     * @param y координата y
     * @return true - если камень в этой координате появлялся
     */
    private boolean isStonePresentInSomeGameAt(int x, int y) {
        boolean found = false;
        for (int countRun = 0; countRun < 100000; countRun ++) {
            Stone stone = getNewStone();

            found |= (x == stone.getX()) & (y == stone.getY());
            if (found) {
                break;
            }
        }
        return found;
    }

    // еще камень никогда не должен находиться в трех местах - на змейке размером в два поля
    // и непосредственно на пути ее движения (прямо перед носом, а то не дай бог скорость будет
    // большой и что тогда? игрок может не успеть)
    @Test
    public void shouldNotStoneAtSnakeWayWhenGoRight() {
        assertEquals(Direction.RIGHT, snake.getDirection());
        int snakeTailX = snake.getX() - 1;

        for (int x = snakeTailX; x <= BOARD_SIZE; x ++) {
            assertStoneNotFoundAt(x, snake.getY());
        }
    }

    // так же как и в прошлом тесте shouldNotStoneAtSnakeWayWhenGoRight, только змейка будет двигаться вниз.
    @Test
    public void shouldNotStoneAtSnakeWayWhenGoDown() {
        snake.down();
        snake.walk(board);
        assertEquals(Direction.DOWN, snake.getDirection());
        int snakeTailY = snake.getY() + 1;

        for (int y = snakeTailY; y <= BOARD_SIZE; y ++) {
            assertStoneNotFoundAt(snake.getX(), y);
        }
    }

    // так же как и в прошлом тесте shouldNotStoneAtSnakeWayWhenGoDown, только змейка будет двигаться влево.
    @Test
    public void shouldNotStoneAtSnakeWayWhenGoLeft() {
        snake.left();
        snake.walk(board);
        assertEquals(Direction.LEFT, snake.getDirection());
        int snakeTailX = snake.getX() + 1;

        for (int x = 0; x <= snakeTailX; x ++) {
            assertStoneNotFoundAt(x, snake.getY());
        }
    }


    // так же как и в прошлом тесте shouldNotStoneAtSnakeWayWhenGoLeft, только змейка будет двигаться вверх.
    @Test
    public void shouldNotStoneAtSnakeWayWhenGoUp() {
        snake.up();
        snake.walk(board);
        assertEquals(Direction.UP, snake.getDirection());
        int snakeTailY = snake.getY() - 1;

        for (int y = 0; y <= snakeTailY; y ++) {
            assertStoneNotFoundAt(snake.getX(), y);
        }
    }

    /**
     * Метод проверяет что за больше число запусков игр камень не будет в заданной позиции никогда.
     * @param x координата x
     * @param y координата y
     */
    private void assertStoneNotFoundAt(int x, int y) {
        boolean found = isStonePresentInSomeGameAt(x, y);
        assertFalse(String.format("Камень никогда не должен был появляться в позиции x:%s y:%s", x, y), found);
    }

    // На поле случайным образом во времени и пространстве появляются яблоки.
    // тут я не буду тестить того, что яблоки будут в каждой клетке и так далее.
    @Test
    public void shouldAppleHasRandomPositionsWhenNewGameStarted() {
        Apple firstApple = getNewApple();
        Apple secondApple = getNewApple();
        Apple thirdApple = getNewApple();

        assertPointChangePosition(firstApple, secondApple, thirdApple);
    }

    private Apple getNewApple() {
        return generator.generateApple(snake, apple, stone, walls, BOARD_SIZE);
    }

    // аблоко не может быть за пределами доски
    @Test
    public void shouldAppleAlwaysAtTheBoard() {
        // тут поставил цикл, чтобы проверить что никогда
        // яблоко не генерится за пределами доски
        for (int countRun = 0; countRun < 100000; countRun ++) {
            Apple apple = getNewApple();

            assertTrue("яблоко должно быть в перделах доски по оси X", apple.getX() < BOARD_SIZE);
            assertTrue("яблоко должно быть в перделах доски по оси Y", apple.getY() < BOARD_SIZE);
            assertTrue("яблоко должно быть в перделах доски по оси X", apple.getX() >= 0);
            assertTrue("яблоко должно быть в перделах доски по оси Y", apple.getY() >= 0);
        }
    }

    // проверим что яблоки могут побывать везде на поле
    @Test
    public void testRandomApplePosition() {
        int snakeHeadX = snake.getX();
        int snakeHeadY = snake.getY();
        int snakeTailX = snakeHeadX - 1;

        for (int y = 0; y < BOARD_SIZE; y ++) {
            for (int x = 0; x < BOARD_SIZE; x ++) {
                // яблоко не должно появляться на змее (она у нас 2 квадратика (голова и хвост))
                if (y == snakeHeadY && (x == snakeTailX || x == snakeHeadY)) {
                    continue;
                }
                // так же яблоко не может появитсья на камне
                if (y == 0 && x == 0) {
                    continue;
                }
                // так же яблоко не может появиться на стене (см. initWallsMock)
                if (x == 0) {
                    continue;
                }
                // так же яблоко не может появиться месте старого яблока
                if (x == apple.getX() && y == apple.getY()) {
                    continue;
                }
                assertAppleInSomeGameAt(x, y);
            }
        }
    }

    /**
     * Метод проверяет что за больше число запусков игр яблоко будет в заданной позиции хоть один раз.
     * @param x координата x
     * @param y координата y
     */
    private void assertAppleInSomeGameAt(int x, int y) {
        boolean found = isApplePresentInSomeGameAt(x, y);
        assertTrue(String.format("Должен был быть найдено яблоко в позиции x:%s y:%s", x, y), found);
    }

    /**
     * Метод говорит, что за больше число запусков игр яблоко будет в заданной позиции хоть один раз.
     * @param x координата x
     * @param y координата y
     * @return true - если яблоко в этой координате появлялся
     */
    private boolean isApplePresentInSomeGameAt(int x, int y) {
        boolean found = false;
        for (int countRun = 0; countRun < 100000; countRun ++) {
            Apple apple = getNewApple();

            found |= (x == apple.getX()) & (y == apple.getY());
            if (found) {
                break;
            }
        }
        return found;
    }

    // яблоко не может появиться на змейке.
    @Test
    public void shouldNotAppleAtSnakeWay() {
        int snakeHeadX = snake.getX();
        int snakeHeadY = snakeHeadX;
        int snakeTailX = snakeHeadX - 1;

        assertAppleNotFoundAt(snakeHeadX, snakeHeadY);
        assertAppleNotFoundAt(snakeTailX, snakeHeadY);
    }

    /**
     * Метод проверяет что за больше число запусков игр яблоко не будет в заданной позиции никогда.
     * @param x координата x
     * @param y координата y
     */
    private void assertAppleNotFoundAt(int x, int y) {
        boolean found = isApplePresentInSomeGameAt(x, y);
        assertFalse(String.format("Яблоко никогда не должно появляться в позиции x:%s y:%s", x, y), found);
    }

    // Яблоко не может появиться на камнe.
    @Test
    public void shouldNotAppleAtStonePlace() {
        assertAppleNotFoundAt(stone.getX(), stone.getY());
    }

    // Камень не может появиться на яблоке.
    @Test
    public void shouldNotStoneAtApplePlace() {
        assertStoneNotFoundAt(apple.getX(), apple.getY());
    }

    final int D = 1; // ширина стены
    final Point a1 = pt(0 + D, 0 + D);
    final Point a2 = pt(0 + D, BOARD_SIZE - 1 - D);
    final Point a3 = pt(BOARD_SIZE - 1 - D, 0 + D);
    final Point a4 = pt(BOARD_SIZE - 1 - D, BOARD_SIZE - 1 - D);


    // ха, только что нашел один момент, когда камень и яблоки взаиморасполагаются так, чтобы загнать змейку в тупик.
    // тут я проверю, что если яблоки стоят в углу то камни не могут быть рядом с ними
    @Test
    public void shouldNotStandstillWhenGenerateStone() {
        walls = new BasicWalls(BOARD_SIZE);

        apple = new Apple(a1.getX(), a1.getY()); // угловая координата, если стены BasicWalls
        assertStoneNotFoundAt(apple.getX() + 1, apple.getY());
        assertStoneNotFoundAt(apple.getX(), apple.getY() + 1);

        apple = new Apple(a2.getX(), a2.getY());
        assertStoneNotFoundAt(apple.getX() + 1, apple.getY());
        assertStoneNotFoundAt(apple.getX(), apple.getY() - 1);

        apple = new Apple(a3.getX(), a3.getY());
        assertStoneNotFoundAt(apple.getX() - 1, apple.getY());
        assertStoneNotFoundAt(apple.getX(), apple.getY() + 1);

        apple = new Apple(a4.getX(), a4.getY());
        assertStoneNotFoundAt(apple.getX() - 1, apple.getY());
        assertStoneNotFoundAt(apple.getX(), apple.getY() - 1);
    }

    // теперь то же самое, что в прошлом тесте, только теперь при генерации яблока
    @Test
    public void shouldNotStandstillWhenGenerateApple() {
        int LEFT = a1.getX();
        int TOP = a1.getY();
        int RIGHT = a4.getX();
        int BOTTOM = a4.getY();

        stone = new Stone(LEFT + 1, TOP);
        assertAppleNotFoundAt(LEFT, TOP);

        stone = new Stone(LEFT, TOP + 1);
        assertAppleNotFoundAt(LEFT, TOP);


        stone = new Stone(RIGHT - 1, TOP);
        assertAppleNotFoundAt(RIGHT, TOP);

        stone = new Stone(RIGHT, TOP + 1);
        assertAppleNotFoundAt(RIGHT, TOP);


        stone = new Stone(TOP + 1, BOTTOM);
        assertAppleNotFoundAt(TOP, BOTTOM);

        stone = new Stone(TOP, BOTTOM - 1);
        assertAppleNotFoundAt(TOP, BOTTOM);


        stone = new Stone(RIGHT - 1, BOTTOM);
        assertAppleNotFoundAt(RIGHT, BOTTOM);

        stone = new Stone(RIGHT, BOTTOM - 1);
        assertAppleNotFoundAt(RIGHT, BOTTOM);
    }

    // Яблоко не может появиться нигде на стенке
    @Test
    public void shouldNotAppleAtWallPlace() {
        for (int y = 0; y < BOARD_SIZE; y ++) {
            assertAppleNotFoundAt(0, y);
        }
    }

    // Камень не может появиться нигде на стенке
    @Test
    public void shouldNotStoneAtWallPlace() {
        for (int y = 0; y < BOARD_SIZE; y ++) {
            assertStoneNotFoundAt(0, y);
        }
    }

    // Яблоко не может появиться на месте прошлого яблока,
    // потому как там за следующим тактом будет голова змеи
    @Test
    public void shouldNotAppleAtOldApplePlace() {
        for (int y = 0; y < BOARD_SIZE; y ++) {
            assertAppleNotFoundAt(1, 1);
        }
    }

    // заполним все поле
    @Test
    public void testMaxApplePosition() {
        generator = new RandomArtifactGenerator(new RandomDice()); // TODO использовать моки

        initBoardMock();
        initFullWallsMock();

        snake = new Hero(3, 1);
        initSnake();
        stone = new Stone(1, 1);
        apple = new Apple(4, 1);

        int x = 1;
        boolean growX = true;
        for (int y = 1; y < BOARD_SIZE; y++) {
            while (x <= BOARD_SIZE) {
                Point xy = pt(x, y);
                if (stone.itsMe(xy) || snake.itsMe(xy) || walls.itsMe(x, y)) {
                    if (growX) {
                        x++;
                    } else {
                        x--;
                    }
                    continue;
                }

                snake.grow();
                snake.move(x, y);

                if (growX) {
                    x++;
                } else {
                    x--;
                }

                if (x == BOARD_SIZE) {
                    growX = false;
                    x--;
                    break;
                }

                if (x == 0) {
                    growX = true;
                    x++;
                    break;
                }

                if (x == BOARD_SIZE - 1 && y == BOARD_SIZE - 1) {
                    break;
                }
            }
        }
        Apple newApple = generator.generateApple(snake, apple, stone, walls, BOARD_SIZE);
        assertEquals(new Apple(-1, -1), newApple);
    }
}
