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


import org.json.JSONArray;
import org.json.JSONObject;

public class GuiPlotColorDecoder {

    public static String GUI = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private Object[] values;

    public GuiPlotColorDecoder(Object[] values) {
        this.values = values;
    }

    private char getGuiChar(char consoleChar) {
        return GUI.charAt(getIndex(consoleChar));
    }

    private int getIndex(char ch) {
        for (int index = 0; index < values.length; index++) {
            if (values[index].toString().equals(String.valueOf(ch))) {
                return index;
            }
        }
        throw new IllegalArgumentException("Not enum symbol '" + ch + "'");
    }

    public String encodeForClient(Object board) {
        return board.toString().replaceAll("\n", "");
    }

    public Object encodeForBrowser(Object board) {
        if (board instanceof String) {
            return encode((String)board);
        }

        if (!(board instanceof JSONObject)) {
            throw new IllegalArgumentException("You can use only String or JSONObject as board");
        }

        JSONObject result = (JSONObject)board;

        // TODO а что если придумать другой формат и не делать этого двойного безобразия?
        String key = "layers";
        if (!result.has(key)) {
            return result;
        }

        JSONArray encoded = new JSONArray();
        for (Object layer : result.getJSONArray(key)) {
            encoded.put(encode((String)layer));
        }
        result.put(key, encoded);
        return result;
    }

    private String encode(String board) {
        char[] chars = board.replaceAll("\n", "").toCharArray();
        for (int index = 0; index < chars.length; index++) {
            chars[index] = getGuiChar(chars[index]);
        }
        return String.copyValueOf(chars);
    }
}
