/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

import de.elbe5.content.ContentData;
import de.elbe5.content.ContentViewContext;
import de.elbe5.user.UserData;

import javax.servlet.http.*;
import java.util.*;

public abstract class RequestData extends KeyValueMap {

    public static RequestData getRequestData(HttpServletRequest request) {
        return (RequestData) request.getAttribute(RequestKeys.KEY_REQUESTDATA);
    }

    protected HttpServletRequest request;

    private int id = 0;
    private final String method;
    protected RequestType type = RequestType.none;
    protected FormError formError = null;
    protected ContentViewContext viewContext = null;

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

    /*********** message *********/

    public boolean hasMessage() {
        return containsKey(RequestKeys.KEY_MESSAGE);
    }

    public void setMessage(String msg, String type) {
        put(RequestKeys.KEY_MESSAGE, msg);
        put(RequestKeys.KEY_MESSAGETYPE, type);
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
            Object obj=getRequestObject(RequestKeys.KEY_CONTENT);
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
        setRequestObject(RequestKeys.KEY_CONTENT, data);
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

    /*************** view context ***************/

    public ContentViewContext getViewContext() {
        return viewContext;
    }

    public <T extends ContentViewContext> T getViewContext(Class<T> cls) {
        try {
            return cls.cast(viewContext);
        }
        catch (ClassCastException | AssertionError e){
            return null;
        }
    }

    public void setViewContext(ContentViewContext viewContext) {
        this.viewContext = viewContext;
    }
}


