package com.codenjoy.dojo.web.controller;

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


import com.codenjoy.dojo.services.dao.GameData;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

@Controller
public class GameDataController {

    @Autowired private GameData gameData;

    public GameDataController() {
    }

    //for unit test
    GameDataController(GameData gameData) {
        this.gameData = gameData;
    }

    @RequestMapping(value = "/settings/{gameType}/{key}", method = RequestMethod.GET)
    public @ResponseBody String get(@PathVariable("gameType") String gameType, @PathVariable("key") String key) {
        return gameData.get(gameType, key);
    }

    @RequestMapping(value = "/settings/{gameType}/{key}", method = RequestMethod.POST)
    public @ResponseBody String set(@PathVariable("gameType") String gameType, @PathVariable("key") String key, @RequestBody String value) {
        try {
            gameData.set(gameType, key, URLDecoder.decode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "{}";
    }
}
