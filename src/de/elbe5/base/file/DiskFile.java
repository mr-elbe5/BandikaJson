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

public class DiskFile extends File {

    public DiskFile(String path) {
        super(path);
    }

    public DiskFile(String parent, String fileName) {
        super(parent, fileName);
    }

    public String getFileName() {
        return getName();
    }

    public String getExtension() {
        String fileName = getFileName();
        if (fileName== null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1)  {
            return null;
        }
        return fileName.substring(pos + 1).toLowerCase();
    }

    public String getFileNameWithoutExtension() {
        String fileName = getFileName();
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }
        return fileName.substring(0, pos);
    }

    public int getFileSize() {
        return (int) length();
    }

    public String readAsText() {
        StringBuilder sb = new StringBuilder();
        try {
            if (!exists()) {
                return "";
            }
            FileReader reader = new FileReader(this);
            char[] chars = new char[4096];
            int len = 4096;
            while (len > 0) {
                len = reader.read(chars, 0, 4096);
                if (len > 0) {
                    sb.append(chars, 0, len);
                }
            }
            reader.close();
        } catch (IOException e) {
            return "";
        }
        return sb.toString();
    }

    public byte[] readAsBinary() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (!exists()) {
                return null;
            }
            FileInputStream fin = new FileInputStream(this);
            byte[] bytes = new byte[4096];
            int len = 4096;
            while (len > 0) {
                len = fin.read(bytes, 0, 4096);
                if (len > 0) {
                    out.write(bytes, 0, len);
                }
            }
            out.flush();
            fin.close();
        } catch (IOException e) {
            return null;
        }
        return out.toByteArray();
    }

    public FileInputStream getBinaryStream(File f) {
        try {
            if (!f.exists()) {
                return null;
            }
            return new FileInputStream(f);
        } catch (IOException e) {
            return null;
        }
    }

    public boolean touch() throws IOException {
        long timestamp = System.currentTimeMillis();
        return touch(timestamp);
    }

    public boolean touch(long timestamp) throws IOException {
        if (!exists()) {
            new FileOutputStream(this).close();
        }
        return setLastModified(timestamp);
    }

    public boolean writeToDisk(String text) {
        try {
            if (exists() && !delete()) {
                Log.error("could not delete file " + getPath());
            }
            if (!createNewFile())
                throw new IOException("file create error");
            FileWriter fw = new FileWriter(this);
            fw.write(text);
            fw.flush();
            fw.close();
            return true;
        } catch (IOException e) {
            Log.error("could not write file " + getPath());
            return false;
        }
    }

    public boolean writeToDisk(byte[] bytes) {
        try {
            if (exists() && !delete()) {
                Log.error("could not delete file " + getPath());
                return false;
            }
            if (!createNewFile())
                throw new IOException("file create error");
            FileOutputStream fout = new FileOutputStream(this);
            fout.write(bytes);
            fout.flush();
            fout.close();
        } catch (IOException e) {
            Log.error("could not write file " + getPath());
            return false;
        }
        return true;
    }

    public boolean writeToDisk(MemoryFile file){
        return writeToDisk(file.getBytes());
    }

}
