/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.file;

import de.elbe5.base.log.Log;
import java.io.*;

public class DiskDirectory extends File{

    public DiskDirectory(String path){
        super(path);
    }

    public DiskDirectory(DiskDirectory parent, String name){
        super(parent, name);
    }

    public boolean ensureExists() {
        if (!exists()) {
            if (!mkdir()) {
                Log.error("could not create directory " + getPath());
                return false;
            }
        }
        return true;
    }

    public boolean ensureEmpty() {
        if (!exists()) {
            if (!mkdir()) {
                Log.error("could not create directory " + getPath());
                return false;
            }
        }
        return clearDirectory();
    }

    public boolean clearDirectory() {
        return clearDirectory(this);
    }

    private boolean clearDirectory(File dir){
        boolean success = true;
        if (!dir.exists() || !dir.isDirectory()) {
            Log.error("no valid directory " + dir.getPath());
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()){
                    success &= clearDirectory(f);
                }
                else if (!f.delete()) {
                    Log.error("could delete file " + f.getName());
                    success = false;
                }
            }
        }
        return success;
    }

}
