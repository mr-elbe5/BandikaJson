/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

import de.elbe5.data.BaseData;
import de.elbe5.application.Strings;
import de.elbe5.content.ContentData;
import de.elbe5.application.Application;
import de.elbe5.user.UserData;

import javax.servlet.http.*;
import java.util.*;

public class SessionRequestData extends RequestData {

    public static SessionRequestData getRequestData(HttpServletRequest request) {
        return (SessionRequestData) request.getAttribute(RequestData.KEY_REQUESTDATA);
    }

    public SessionRequestData(String method, HttpServletRequest request, RequestType type) {
        super(method, request);
        this.type = type;
    }

    /************ user ****************/

    @Override
    public UserData getCurrentUser() {
        return getSessionUser();
    }

    @Override
    public Locale getLocale() {
        return getSessionLocale();
    }

    /************ form error *************/

    public boolean hasFormErrors() {
        if (formError == null)
            return false;
        if (formError.isFormIncomplete())
            formError.addFormError(Strings.string("_notComplete", getLocale()));
        return !formError.isEmpty();
    }

    /************** session attributes ***************/

    public void initSession() {
        HttpSession session = request.getSession(true);
        if (session.isNew()) {
            Locale requestLocale = request.getLocale();
            if (Application.hasLanguage(requestLocale))
                setSessionLocale(requestLocale);
            StringBuffer url = request.getRequestURL();
            String uri = request.getRequestURI();
            String host = url.substring(0, url.indexOf(uri));
            setSessionHost(host);
        }
    }

    public void setSessionObject(String key, Object obj) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.setAttribute(key, obj);
    }

    public Object getSessionObject(String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }
        return session.getAttribute(key);
    }

    private void removeAllSessionObjects(){
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        Enumeration<String>  keys = session.getAttributeNames();
        while (keys.hasMoreElements()){
            String key=keys.nextElement();
            session.removeAttribute(key);
        }
    }

    public <T> T getSessionObject(String key, Class<T> cls) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }
        try {
            return cls.cast(request.getSession().getAttribute(key));
        }
        catch (NullPointerException | ClassCastException e){
            return null;
        }
    }

    public void removeSessionObject(String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.removeAttribute(key);
    }

    public ClipboardData getClipboard() {
        ClipboardData data = getSessionObject(RequestData.KEY_CLIPBOARD,ClipboardData.class);
        if (data==null){
            data=new ClipboardData();
            setSessionObject(RequestData.KEY_CLIPBOARD,data);
        }
        return data;
    }

    public void setClipboardData(String key, BaseData data){
        getClipboard().putData(key,data);
    }

    public boolean hasClipboardData(String key){
        return getClipboard().hasData(key);
    }

    public <T> T getClipboardData(String key,Class<T> cls) {
        try {
            return cls.cast(getClipboard().getData(key));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public void removeClipboardData(String key){
        getClipboard().clearData(key);
    }

    public void setSessionUser(UserData data) {
        setSessionObject(RequestData.KEY_LOGIN, data);
    }

    public UserData getSessionUser() {
        return (UserData) getSessionObject(RequestData.KEY_LOGIN);
    }

    public ContentData getCurrentContent() {
        return getCurrentContent(ContentData.class);
    }

    public <T extends ContentData> T getCurrentContent(Class<T> cls) {
        try {
            Object obj=getRequestObject(RequestData.KEY_CONTENT);
            if (obj==null)
                obj=getSessionObject(RequestData.KEY_CONTENT);
            assert(obj!=null);
            //Log.log("current request content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException | AssertionError e){
            return null;
        }
    }

    public void setCurrentSessionContent(ContentData data) {
        //Log.log("set current session content: " + data.getClass().getSimpleName());
        setSessionObject(RequestData.KEY_CONTENT, data);
    }

    public void removeCurrentSessionContent() {
        removeSessionObject(RequestData.KEY_CONTENT);
    }

    public ContentData getCurrentSessionContent() {
        return getCurrentSessionContent(ContentData.class);
    }

    public <T extends ContentData> T getCurrentSessionContent(Class<T> cls) {
        try {
            Object obj=getSessionObject(RequestData.KEY_CONTENT);
            //Log.log("current session content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException e){
            return null;
        }
    }

    public void setSessionLocale() {
        setSessionLocale(Application.getDefaultLocale());
    }

    public Locale getSessionLocale() {
        Locale locale = getSessionObject(RequestData.KEY_LOCALE,Locale.class);
        if (locale == null) {
            return Application.getDefaultLocale();
        }
        return locale;
    }

    public void setSessionLocale(Locale locale) {
        if (Application.hasLanguage(locale)) {
            setSessionObject(RequestData.KEY_LOCALE, locale);
        } else {
            setSessionObject(RequestData.KEY_LOCALE, Application.getDefaultLocale());
        }
    }

    public void setSessionHost(String host) {
        setSessionObject(RequestData.KEY_HOST, host);
    }

    public String getSessionHost() {
        return getSessionObject(RequestData.KEY_HOST,String.class);
    }

    public void resetSession() {
        Locale locale = getLocale();
        removeAllSessionObjects();
        request.getSession(true);
        setSessionLocale(locale);
    }

}
