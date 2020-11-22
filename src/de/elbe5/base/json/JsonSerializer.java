/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.json;

import de.elbe5.data.JsonData;
import de.elbe5.base.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonSerializer {

    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public String serializeObject(Object obj) {
        try {
            Object o = toJSONInstance(obj);
            if (o instanceof JSONObject || o instanceof JSONArray){
                return o.toString();
            }
            return "";
        }
        catch (Exception e){
            Log.warn("Unable to serialize object");
            return "";
        }
    }

    private Object toJSONInstance(Object object) {

        if (object == null) {
            return null;
        }

        if (object instanceof JSONObject || object instanceof JSONArray
                || object instanceof Byte || object instanceof Character
                || object instanceof Short || object instanceof Integer
                || object instanceof Long || object instanceof Boolean
                || object instanceof Float || object instanceof Double
                || object instanceof String || object instanceof BigInteger
                || object instanceof BigDecimal || object instanceof Enum) {
            return object;
        }

        if (object instanceof LocalDate) {
            object = LocalDateTime.of((LocalDate) object, LocalTime.of(0,0,0));
        }

        if (object instanceof LocalDateTime) {
            return ((LocalDateTime) object).format(DateTimeFormatter.ofPattern(ISO_8601_PATTERN));
        }

        if (object instanceof Calendar) {
            object = ((Calendar) object).getTime(); //sets object to date, will be converted in next if-statement:
        }

        if (object instanceof Date) {
            Date date = (Date) object;
            return new SimpleDateFormat(ISO_8601_PATTERN).format(date);
        }

        if (object instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) object);
        }

        if (object instanceof char[]) {
            return new String((char[]) object);
        }

        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            return toJSONObject(map);
        }

        if (object.getClass().isArray()) {
            Collection c = Arrays.asList(toObjectArray(object));
            return toJSONArray(c);
        }

        if (object instanceof Collection) {
            Collection<?> coll = (Collection<?>) object;
            return toJSONArray(coll);
        }

        if (object instanceof JsonData) {
            return toJSONObject(object);
        }

        Log.warn("Unable to serialize object of type " + object.getClass().getName());
        throw new RuntimeException();
    }

    private JSONObject toJSONObject(Object object) {
        JSONObject obj = new JSONObject();
        if (object instanceof JsonData){
            return ((JsonData)object).toJSONObject();
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private JSONObject toJSONObject(Map<?, ?> m) {

        JSONObject obj = new JSONObject();

        for (Map.Entry<?, ?> entry : m.entrySet()) {
            Object k = entry.getKey();
            Object value = entry.getValue();

            String key = String.valueOf(k);
            try {
                value = toJSONInstance(value);
                obj.put(key, value);
            }
            catch (Exception e){
                Log.warn("got no json value");
            }

        }

        return obj;
    }

    public static Object[] toObjectArray(Object source) {
        if (source instanceof Object[]) {
            return (Object[]) source;
        }
        if (source == null) {
            return new Object[0];
        }
        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        }
        int length = Array.getLength(source);
        if (length == 0) {
            return new Object[0];
        }
        Class wrapperType = Array.get(source, 0).getClass();
        Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
        for (int i = 0; i < length; i++) {
            newArray[i] = Array.get(source, i);
        }
        return newArray;
    }

    @SuppressWarnings("unchecked")
    private JSONArray toJSONArray(Collection c) {

        JSONArray array = new JSONArray();

        for (Object o : c) {
            try {
                o = toJSONInstance(o);
                array.put(o);
            }
            catch (Exception e){
                Log.warn("got no json value");
            }

        }

        return array;
    }

}
