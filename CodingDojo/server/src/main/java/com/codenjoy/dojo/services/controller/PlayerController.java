package com.codenjoy.dojo.services.controller;

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


import com.codenjoy.dojo.services.BoardGameState;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Player;
import com.codenjoy.dojo.transport.ws.PlayerTransport;

import java.io.IOException;

public class PlayerController implements Controller<String, Joystick> {

    private PlayerTransport transport;

    @Override
    public void requestControlToAll(String board) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestControl(Player player, String board) throws IOException {
        transport.sendState(player.getName(), new BoardGameState(board));
    }

    @Override
    public void registerPlayerTransport(Player player, Joystick joystick) {
        transport.registerPlayerEndpoint(player.getName(),
                new PlayerResponseHandler(player, joystick));
    }

    @Override
    public void unregisterPlayerTransport(Player player) {
        transport.unregisterPlayerEndpoint(player.getName());
    }

    public void setTransport(PlayerTransport transport) {
        this.transport = transport;
        transport.setDefaultFilter(data -> data.toString());
    }
}
