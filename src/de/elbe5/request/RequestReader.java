/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

import de.elbe5.application.Application;
import de.elbe5.base.file.MemoryFile;
import de.elbe5.base.log.Log;
import io.jsonwebtoken.orgjson.io.OrgJsonDeserializer;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class RequestReader {

    public static void readRequestParams(HttpServletRequest request, KeyValueMap map, boolean isPostback) {
        if (isPostback) {
            String type = request.getContentType();
            if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
                getMultiPartParams(request, map);
            } else if (type != null && type.toLowerCase().equals("application/octet-stream")) {
                getSinglePartParams(request, map);
                getByteStream(request, map);
            } else if (type != null && type.toLowerCase().equals("application/json")) {
                getSinglePartParams(request, map);
                getJsonStream(request, map);
            } else {
                getSinglePartParams(request, map);
            }
        } else {
            getSinglePartParams(request, map);
        }
    }

    private static void getByteStream(HttpServletRequest request, KeyValueMap map){
        try {
            InputStream in = request.getInputStream();
            MemoryFile file=new MemoryFile();
            file.setBytesFromStream(in);
            file.setFileSize(file.getBytes().length);
            file.setFileName(request.getHeader("fileName"));
            file.setContentType(request.getHeader("contentType"));
            map.put("file", file);
        }
        catch (IOException ioe){
            Log.error("input stream error", ioe);
        }
    }

    private static void getJsonStream(HttpServletRequest request, KeyValueMap map){
        try {
            InputStream in = request.getInputStream();
            try {
                JSONObject json = (JSONObject) new OrgJsonDeserializer().deserialize(in);
                for (String key : json.keySet()){
                    map.put(key, json.get(key));
                }
            }
            catch (Exception e){
                Log.error("unable to get params from json");
            }
            in.close();
        }
        catch (IOException ioe){
            Log.error("json input stream error", ioe);
        }
    }

    private static void getSinglePartParams(HttpServletRequest request, KeyValueMap map) {
        Enumeration<?> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            map.put(key, strings);
        }
    }

    private static void getMultiPartParams(HttpServletRequest request, KeyValueMap map) {
        Map<String, List<String>> params = new HashMap<>();
        Map<String, List<MemoryFile>> fileParams = new HashMap<>();
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String fileName = getFileName(part);
                if (fileName != null) {
                    if (fileName.isEmpty())
                        continue;
                    MemoryFile file = getMultiPartFile(part, fileName);
                    if (file != null) {
                        List<MemoryFile> values;
                        if (fileParams.containsKey(name))
                            values = fileParams.get(name);
                        else {
                            values = new ArrayList<>();
                            fileParams.put(name, values);
                        }
                        values.add(file);
                    }
                } else {
                    String param = getMultiPartParameter(part);
                    if (param != null) {
                        List<String> values;
                        if (params.containsKey(name))
                            values = params.get(name);
                        else {
                            values = new ArrayList<>();
                            params.put(name, values);
                        }
                        values.add(param);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("error while parsing multipart params", e);
        }
        for (String key : params.keySet()) {
            List<String> list = params.get(key);
            if (list.size() == 1) {
                map.put(key, list.get(0));
            } else {
                String[] strings = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strings[i] = list.get(i);
                }
                map.put(key, strings);
            }
        }
        for (String key : fileParams.keySet()) {
            List<MemoryFile> list = fileParams.get(key);
            if (list.size() == 1) {
                map.put(key, list.get(0));
            } else {
                MemoryFile[] files = new MemoryFile[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    files[i] = list.get(i);
                }
                map.put(key, files);
            }
        }
    }

    private static String getMultiPartParameter(Part part) {
        try {
            byte[] bytes = new byte[(int) part.getSize()];
            int read = part.getInputStream().read(bytes);
            if (read > 0) {
                return new String(bytes, Application.ENCODING);
            }
        } catch (Exception e) {
            Log.error("could not extract parameter from multipart", e);
        }
        return null;
    }

    private static MemoryFile getMultiPartFile(Part part, String fileName) {
        try {
            MemoryFile file = new MemoryFile();
            file.setFileName(fileName);
            file.setContentType(part.getContentType());
            file.setFileSize((int) part.getSize());
            InputStream in = part.getInputStream();
            if (in == null) {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(file.getFileSize());
            byte[] buffer = new byte[8096];
            int len;
            while ((len = in.read(buffer, 0, 8096)) != -1) {
                out.write(buffer, 0, len);
            }
            file.setBytes(out.toByteArray());
            return file;
        } catch (Exception e) {
            Log.error("could not extract file from multipart", e);
            return null;
        }
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}


