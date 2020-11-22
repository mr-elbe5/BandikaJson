/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.layout;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;

import java.io.File;
import java.util.*;

public class Layouts {

    private static final Map<String, List<String>> typeMap = new HashMap<>();

    public static void addType(String type){
        typeMap.put(type,new ArrayList<>());
    }

    public static synchronized void load() {
        List<String> layouts = getAllLayouts();
        for (List<String> list : typeMap.values())
            list.clear();
        for (String layoutName : layouts) {
            for (String type : getTypes()) {
                if (layoutName.endsWith(type))
                    typeMap.get(type).add(layoutName);
            }
        }
        for (String type : typeMap.keySet()) {
            List<String> list=typeMap.get(type);
            Collections.sort(list);
            Log.log("found "+list.size()+" layouts of type "+type);
        }
    }

    public static Set<String> getTypes(){
        return typeMap.keySet();
    }

    public static List<String> getLayoutNames(String type){
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        }
        return new ArrayList<>();
    }

    private static List<String> getAllLayouts(){
        List<String> list=new ArrayList<>();
        File[] files = ApplicationPath.getLayoutDirectory().listFiles();
        if (files!=null){
            for (File f : files){
                String name = f.getName();
                list.add(name.substring(0,name.lastIndexOf('.')));
            }
        }
        return list;
    }
}
