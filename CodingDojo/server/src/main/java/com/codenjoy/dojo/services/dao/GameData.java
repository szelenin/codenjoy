package com.codenjoy.dojo.services.dao;

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


import com.codenjoy.dojo.services.jdbc.ConnectionThreadPoolFactory;
import com.codenjoy.dojo.services.jdbc.CrudConnectionThreadPool;
import com.codenjoy.dojo.services.jdbc.ObjectMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GameData {

    private CrudConnectionThreadPool pool;

    public GameData(ConnectionThreadPoolFactory factory) {
        pool = factory.create(
                "CREATE TABLE IF NOT EXISTS game_settings (" +
                        "game_type varchar(255), " +
                        "key varchar(255), " +
                        "value varchar(10000));");
    }

    public String get(final String gameType, final String key) {
        return pool.select("SELECT value FROM game_settings WHERE game_type = ? AND key = ?;",
                new Object[]{gameType, key},
                new ObjectMapper<String>() {
                    @Override
                    public String mapFor(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            return resultSet.getString("value");
                        } else {
                            return null;
                        }
                    }
                }
        );
    }

    public boolean exists(final String gameType, final String key) {
        return pool.select("SELECT count(*) AS count FROM game_settings WHERE game_type = ? AND key = ?;",
                new Object[]{gameType, key},
                new ObjectMapper<Boolean>() {
                    @Override
                    public Boolean mapFor(ResultSet resultSet) throws SQLException {
                        if (resultSet.next()) {
                            return resultSet.getInt("count") > 0;
                        } else {
                            return false;
                        }
                    }
                }
        );
    }

    public void set(final String gameType, final String key, final String value) {
        if (exists(gameType, key)) {
            pool.update("UPDATE game_settings SET value = ? WHERE game_type = ? AND key = ?;",
                    new Object[]{value, gameType, key});
        } else {
            pool.update("INSERT INTO game_settings (game_type, key, value) VALUES (?,?,?);",
                    new Object[] {gameType, key, value});
        }
    }
}