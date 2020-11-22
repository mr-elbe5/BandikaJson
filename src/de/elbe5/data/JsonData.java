/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.base.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface JsonData {

    String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    JSONObject toJSONObject();

    void fromJSONObject(JSONObject obj) throws JSONException;

    // default methods

    // Date and Time
    default String jsonString(LocalDateTime date){
        return date == null ? "null" : date.format(DateTimeFormatter.ofPattern(ISO_8601_PATTERN));
    }

    default String jsonString(LocalDate date){
        if (date==null)
            return JSONObject.NULL.toString();
        LocalDateTime dt = LocalDateTime.of(date, LocalTime.of(0,0,0));
        return jsonString(dt);
    }

    default LocalDateTime getLocalDateTime(String s){
        if (s==null || s.isEmpty() || s.equals(JSONObject.NULL.toString()))
            return null;
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(ISO_8601_PATTERN));
    }

    default LocalDate getLocalDate(String s){
        if (s==null || s.isEmpty() || s.equals(JSONObject.NULL.toString()))
            return null;
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(ISO_8601_PATTERN));
    }

    // JsonData

    default boolean fromParentObject(JSONObject parentObject, String name){
        try {
            JSONObject obj = parentObject.optJSONObject(name);
            if (obj ==null){
                return false;
            }
            fromJSONObject(obj);
            return true;
        } catch (JSONException | NullPointerException e){
            Log.error("could not get json from parent object: "+name, e);
            return false;
        }
    }

    // Lists

    default <T extends JsonData> JSONArray createJSONArray(List<T> list){
        JSONArray arr = new JSONArray();
        for (T data : list){
            arr.put(data.toJSONObject());
        }
        return arr;
    }

    default <T extends IData> List<T> getList(JSONObject obj, String key, Class<T> listClass){
        List<T> list = new ArrayList<>();
        try{
            if (obj.has(key)) {
                JSONArray arr = obj.optJSONArray(key);
                if (arr == null){
                    return list;
                }
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jo = arr.optJSONObject(i);
                    if (jo==null)
                        continue;
                    String typeKey = jo.optString(IData.typeKey);
                    T data = DataFactory.createObject(typeKey, listClass);
                    if (data!=null) {
                        data.fromJSONObject(jo);
                        list.add(data);
                    }
                }
            }
        }catch (JSONException e) {
            Log.error("could not read json array", e);
        }
        return list;
    }

    // Maps

    default <T extends JsonData> JSONObject createJSONObjectFromIntMap(Map<Integer,T> map){
        JSONObject obj = new JSONObject();
        for (Integer i : map.keySet()){
            obj.put(i.toString(),map.get(i).toJSONObject());
        }
        return obj;
    }

    default <T extends JsonData> JSONObject createJSONObjectFromStringMap(Map<String,T> map){
        JSONObject obj = new JSONObject();
        for (String key : map.keySet()){
            obj.put(key,map.get(key).toJSONObject());
        }
        return obj;
    }

    default <T extends IData> Map<Integer,T> getIntMap(JSONObject obj, String key, Class<T> mapClass){
        Map<Integer,T> map = new HashMap<>();
        try{
            if (obj.has(key)) {
                JSONObject mapObject = obj.optJSONObject(key);
                if (mapObject==null){
                    return map;
                }
                for (String okey : mapObject.keySet()) {
                    JSONObject jo = mapObject.optJSONObject(okey);
                    if (jo==null){
                        continue;
                    }
                    String typeKey = jo.optString(IData.typeKey);
                    T data = DataFactory.createObject(typeKey, mapClass);
                    if (data!=null) {
                        data.fromJSONObject(jo);
                        map.put(Integer.parseInt(okey), data);
                    }
                }
            }
        }catch (JSONException e) {
            Log.error("could not read json array", e);
        }
        return map;
    }

    default <T extends IData> Map<String,T> getStringMap(JSONObject obj, String key, Class<T> mapClass){
        Map<String,T> map = new HashMap<>();
        try{
            if (obj.has(key)) {
                JSONObject mapObject = obj.optJSONObject(key);
                if (mapObject == null){
                    return map;
                }
                for (String okey : mapObject.keySet()) {
                    JSONObject jo = mapObject.optJSONObject(okey);
                    if (jo == null){
                        continue;
                    }
                    String typeKey = jo.optString(IData.typeKey);
                    T data = DataFactory.createObject(typeKey, mapClass);
                    if (data!=null) {
                        data.fromJSONObject(jo);
                        map.put(okey, data);
                    }
                }
            }
        }catch (JSONException e) {
            Log.error("could not read json array", e);
        }
        return map;
    }

    default void dump(){
        JSONObject obj = toJSONObject();
        Log.log(obj.toString(2));
    }

}
