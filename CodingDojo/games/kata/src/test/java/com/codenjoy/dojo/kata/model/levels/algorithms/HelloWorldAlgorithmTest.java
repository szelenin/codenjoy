package com.codenjoy.dojo.kata.model.levels.algorithms;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2017 Codenjoy
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


import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * User: oleksandr.baglai
 * Date: 2/13/13
 * Time: 11:42 PM
 */
public class HelloWorldAlgorithmTest {

    @Test
    public void shouldWork() {
        HelloWorldAlgorithm algorithm = new HelloWorldAlgorithm();
        assertEquals("hello", algorithm.get("world"));
        assertEquals("world", algorithm.get("hello"));
        assertEquals("qwe", algorithm.get("qwe"));
        assertEquals("asd", algorithm.get("asd"));
    }
}
