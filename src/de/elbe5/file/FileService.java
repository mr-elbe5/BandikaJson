/*
 BandikaJson CMS - A Java based modular File Management System
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileService {

    public static final String JPEG_TYPE = "image/jpeg";
    public static final String JPEG_EXT = ".jpg";

    public static void touch(File file) throws IOException {
        long timestamp = System.currentTimeMillis();
        touch(file, timestamp);
    }

    public static void touch(File file, long timestamp) throws IOException {
        if (!file.exists()) {
            new FileOutputStream(file).close();
        }
        //noinspection ResultOfMethodCallIgnored
        file.setLastModified(timestamp);
    }

    public static String getFileNameFromPath(String path) {
        if (path == null) {
            return null;
        }
        String uri = path.replace('\\', '/');
        int pos = uri.lastIndexOf('/');
        if (pos == -1) {
            return uri;
        }
        return uri.substring(pos + 1);
    }

    // includes '.'
    public static String getExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return null;
        }
        return fileName.substring(pos).toLowerCase();
    }

    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }
        return fileName.substring(0, pos);
    }

    public static boolean copyFile(File srcFile, File destFile){
        //Log.log("copying from " + srcFile.getPath() + " to " + destFile.getPath());
        try {
            if (!srcFile.exists() || !srcFile.isFile()){
                Log.error("file does not exist: " + srcFile.getPath());
                return false;
            }
            if (destFile.exists() && !destFile.delete()) {
                Log.error("could not delete file " + destFile.getPath());
                return false;
            }
            if (!destFile.createNewFile())
                throw new IOException("file create error");
            FileInputStream fin = new FileInputStream(srcFile);
            FileOutputStream fout = new FileOutputStream(destFile);
            byte[] bytes = new byte[0x4000];
            int len = 0x4000;
            while (len > 0) {
                len = fin.read(bytes, 0, 0x4000);
                if (len > 0) {
                    fout.write(bytes, 0, len);
                }
            }
            fout.flush();
            fout.close();
        } catch (IOException e) {
            Log.error("could not write file " + destFile.getPath());
            return false;
        }
        return true;
    }

    public static boolean moveFile(File srcFile, File destFile) {
        if (!copyFile(srcFile, destFile)){
            return false;
        }
        return srcFile.delete();
    }

    public static boolean copyDir(File srcDir, File destDir){
        //Log.log("copying from " + srcDir.getPath() + " to " + destDir.getPath());
        if (!srcDir.exists() || !srcDir.isDirectory() || (!destDir.exists() && !destDir.mkdir()) || !destDir.isDirectory()){
            return false;
        }
        boolean success = true;
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    success &= copyDir(file, destFile);
                } else {
                    success &= copyFile(file, destFile);
                }
            }
        }
        return success;
    }

    public static String setJpegExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }
        return fileName.substring(0, pos) + JPEG_EXT;
    }
}
