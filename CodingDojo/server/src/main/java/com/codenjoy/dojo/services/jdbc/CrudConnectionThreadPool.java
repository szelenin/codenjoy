package com.codenjoy.dojo.services.jdbc;

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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CrudConnectionThreadPool extends ConnectionThreadPool {

    public CrudConnectionThreadPool(int count, final Get get) {
       super(count, get);
    }

    public <T> T select(final String query, final Object[] parameters, final ObjectMapper<T> mapper) {
        return run(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                for (int index = 0; index < parameters.length; index++) {
                    stmt.setObject(index + 1, parameters[index]);
                }
                ResultSet resultSet = stmt.executeQuery();
                return mapper.mapFor(resultSet);
            } catch (SQLException e) {
                throw new RuntimeException(String.format("Error when select '%s': %s", query, e));
            }
        });
    }

    public <T> T select(final String query, final ObjectMapper<T> mapper) {
        return select(query, new Object[0], mapper);
    }

    public void update(final String query) {
        update(query, new Object[0]);
    }

    public void update(final String query, final Object[] parameters) {
        run((For<Void>) connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                for (int index = 0; index < parameters.length; index++) {
                    stmt.setObject(index + 1, parameters[index]);
                }
                stmt.execute();
            } catch (SQLException e) {
                throw new RuntimeException(String.format("Error when update '%s': %s", query, e));
            }
            return null;
        });
    }

    public <T> void batchUpdate(final String query, final List<T> parameters, final ForStmt<T> forStmt) {
        run((For<Void>) connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                for (T parameter : parameters) {
                    if (forStmt.run(stmt, parameter)) {
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(String.format("Error when update '%s': %s", query, e));
            }
            return null;
        });
    }
}
