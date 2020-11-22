/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.data;

import de.elbe5.base.log.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DataFactory {

    public static final Object[] noObjects = null;
    @SuppressWarnings("rawtypes")
    public static final Class[] noClasses = null;

    private static final Map<String, Constructor<? extends IData>> constructorMap = new HashMap<>();
    private static final Map<String, Class<? extends IData>> classMap = new HashMap<>();

    public static <T extends IData> void addClass(String key, Class<T> cls){
        classMap.put(key, cls);
        constructorMap.put(cls.getName(), createDefaultConstructor(cls));
    }

    public static <T extends IData> T createObject(String key, Class<T> cls){
        try{
            Class<? extends IData> mappedClass  = classMap.get(key);
            Constructor<? extends IData> ctor = constructorMap.get(mappedClass.getName());
            return cls.cast(ctor.newInstance(noObjects));
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException  e){
            Log.error("could not create object");
        }
        return null;
    }

    public static <T> Constructor<T> createDefaultConstructor(Class<T> cls){
        try{
            return cls.getDeclaredConstructor(DataFactory.noClasses);
        }
        catch (NoSuchMethodException e){
            Log.error("could not create constructor");
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T getBlankCopy(T obj){
        try{
            Constructor ctor = obj.getClass().getDeclaredConstructor(DataFactory.noClasses);
            return (T) ctor.newInstance(noObjects);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException  e){
            Log.error("could not create copy");
        }
        return null;
    }

}
