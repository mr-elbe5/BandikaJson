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
import de.elbe5.content.ContentData;
import de.elbe5.user.UserData;
import io.jsonwebtoken.orgjson.io.OrgJsonDeserializer;
import org.json.JSONObject;

import javax.servlet.http.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class RequestData extends KeyValueMap {

    public static final String KEY_REQUESTDATA = "$REQUESTDATA";
    public static final String KEY_URL = "$URL";
    public static final String KEY_LOCALE = "$LOCALE";
    public static final String KEY_HOST = "$HOST";
    public static final String KEY_JSP = "$JSP";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_MESSAGETYPE = "$MESSAGETYPE";
    public static final String KEY_TARGETID = "$TARGETID";
    public static final String KEY_CLIPBOARD = "$CLIPBOARD";
    public static final String KEY_TITLE = "$TITLE";
    public static final String KEY_LOGIN = "$LOGIN";
    public static final String KEY_CONTENT = "contentData";
    public static final String KEY_FILE = "fileData";
    public static final String KEY_GROUP = "groupData";
    public static final String KEY_USER = "userData";
    public static final String KEY_PART = "partData";
    public static final String MESSAGE_TYPE_INFO = "info";
    public static final String MESSAGE_TYPE_SUCCESS = "success";
    public static final String MESSAGE_TYPE_ERROR = "danger";

    public static RequestData getRequestData(HttpServletRequest request) {
        return (RequestData) request.getAttribute(KEY_REQUESTDATA);
    }

    protected HttpServletRequest request;

    private int id = 0;

    private final String method;

    protected RequestType type = RequestType.none;

    protected FormError formError = null;

    protected final Map<String, Cookie> cookies = new HashMap<>();

    public RequestData(String method, HttpServletRequest request) {
        this.request = request;
        this.method = method;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract Locale getLocale();

    public boolean isPostback() {
        return method.equals("POST");
    }

    public RequestType getType() {
        return type;
    }

    public boolean isFileRequest(){
        return type.equals(RequestType.file);
    }

    public boolean isPageRequest(){
        return type.equals(RequestType.page);
    }

    public boolean isAjaxRequest(){
        return type.equals(RequestType.ajax);
    }

    public boolean isApiRequest(){
        return type.equals(RequestType.api);
    }

    /************ user ****************/

    public abstract UserData getCurrentUser();

    public int getUserId() {
        UserData user = getCurrentUser();
        return user == null ? 0 : user.getId();
    }

    public boolean isLoggedIn() {
        UserData user = getCurrentUser();
        return user != null;
    }

    /************ form error *************/

    public FormError getFormError(boolean create) {
        if (formError == null && create)
            formError = new FormError();
        return formError;
    }

    public void addFormError(String s) {
        getFormError(true).addFormError(s);
    }

    public void addFormField(String field) {
        getFormError(true).addFormField(field);
    }

    public void addIncompleteField(String field) {
        getFormError(true).addFormField(field);
        getFormError(false).setFormIncomplete();
    }

    public boolean hasFormError() {
        return formError != null && !formError.isEmpty();
    }

    public boolean hasFormErrorField(String name) {
        if (formError == null)
            return false;
        return formError.hasFormErrorField(name);
    }

    /************** request attributes *****************/

    public void readRequestParams() {
        if (isPostback()) {
            String type = request.getContentType();
            if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
                getMultiPartParams();
            } else if (type != null && type.toLowerCase().equals("application/octet-stream")) {
                getSinglePartParams();
                getByteStream();
            } else if (type != null && type.toLowerCase().equals("application/json")) {
                getSinglePartParams();
                getJsonStream();
            } else {
                getSinglePartParams();
            }
        } else {
            getSinglePartParams();
        }
    }

    private void getByteStream(){
        try {
            InputStream in = request.getInputStream();
            MemoryFile file=new MemoryFile();
            file.setBytesFromStream(in);
            file.setFileSize(file.getBytes().length);
            file.setFileName(request.getHeader("fileName"));
            file.setContentType(request.getHeader("contentType"));
            put("file", file);
        }
        catch (IOException ioe){
            Log.error("input stream error", ioe);
        }
    }

    private void getJsonStream(){
        try {
            InputStream in = request.getInputStream();
            try {
                JSONObject json = (JSONObject) new OrgJsonDeserializer().deserialize(in);
                for (String key : json.keySet()){
                    put(key, json.get(key));
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

    private void getSinglePartParams() {
        Enumeration<?> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            put(key, strings);
        }
    }

    private void getMultiPartParams() {
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
                put(key, list.get(0));
            } else {
                String[] strings = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strings[i] = list.get(i);
                }
                put(key, strings);
            }
        }
        for (String key : fileParams.keySet()) {
            List<MemoryFile> list = fileParams.get(key);
            if (list.size() == 1) {
                put(key, list.get(0));
            } else {
                MemoryFile[] files = new MemoryFile[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    files[i] = list.get(i);
                }
                put(key, files);
            }
        }
    }

    private String getMultiPartParameter(Part part) {
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

    private MemoryFile getMultiPartFile(Part part, String fileName) {
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

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /************** request attributes ***************/

    public void setRequestObject(String key, Object obj){
        request.setAttribute(key, obj);
    }

    public Object getRequestObject(String key){
        return request.getAttribute(key);
    }

    public <T> T getRequestObject(String key, Class<T> cls) {
        try {
            return cls.cast(request.getAttribute(key));
        }
        catch (NullPointerException | ClassCastException e){
            return null;
        }
    }

    public void removeRequestObject(String key){
        request.removeAttribute(key);
    }

    public <T extends ContentData> T getCurrentContent(Class<T> cls) {
        try {
            Object obj=getRequestObject(KEY_CONTENT);
            assert(obj!=null);
            //Log.log("current request content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException | AssertionError e){
            return null;
        }
    }

    public void setCurrentRequestContent(ContentData data) {
        //Log.log("set current request content: " + data.getClass().getSimpleName());
        setRequestObject(KEY_CONTENT, data);
    }

    /*************** cookie methods ***************/

    public boolean hasCookies(){
        return !(cookies.isEmpty());
    }

    public void setCookies(HttpServletResponse response){
        for (Cookie cookie : cookies.values()){
            response.addCookie(cookie);
        }
    }

}


