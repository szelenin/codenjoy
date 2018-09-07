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


import com.codenjoy.dojo.services.mocks.MockSaveService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 21:14
 */
@ContextConfiguration(classes = {
        AutoSaver.class,
        MockSaveService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AutoSaverTest {

    @Autowired private AutoSaver autoSaver;
    @Autowired private SaveService save;

    @Test
    public void testSaveEachNTicks() throws InterruptedException {
        verifyNoMoreInteractions(save);

        autoSaver.tick();

        verify(save, only()).loadAll();
        reset(save);

        for (int count = 0; count < AutoSaver.TICKS - 2; count++) {
            autoSaver.tick();
        }

        verifyNoMoreInteractions(save);

        autoSaver.tick();

        Thread.sleep(1000);

        verify(save, only()).saveAll();
    }

}
