package com.codenjoy.dojo.utils;

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


import com.cedarsoftware.util.io.JsonWriter;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.SortedJSONArray;
import org.json.SortedJSONObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by indigo on 2017-03-04.
 */
public class UnicodeUtils {

    public static String unescapeJava(String data) {
        return StringEscapeUtils.unescapeJava(data.replaceAll("\\\\u", "\\u"));
    }
}
