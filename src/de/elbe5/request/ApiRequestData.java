/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

import de.elbe5.base.log.Log;
import de.elbe5.application.Application;
import de.elbe5.data.DataAccessor;
import de.elbe5.servlet.ResponseException;
import de.elbe5.user.ApiWebToken;
import de.elbe5.user.UserData;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApiRequestData extends RequestData implements DataAccessor {

    private String token;
    private UserData user=null;

    public static ApiRequestData getRequestData(HttpServletRequest request) {
        return (ApiRequestData) request.getAttribute(RequestData.KEY_REQUESTDATA);
    }

    public ApiRequestData(String method, HttpServletRequest request) {
        super(method, request);
        type = RequestType.api;
        token = request.getHeader("Authentication");
        if (token==null)
            token="";
    }

    public int checkLogin() {
        if (token.isEmpty()){
            Log.warn("token is empty");
            return HttpServletResponse.SC_UNAUTHORIZED;
        }
        try {
            user=ApiWebToken.verifyToken(token);
        }
        catch (ResponseException e){
            return e.getResponseCode();
        }
        catch (Exception e){
            return HttpServletResponse.SC_FORBIDDEN;
        }
        return HttpServletResponse.SC_OK;
    }

    @Override
    public UserData getCurrentUser() {
        return user;
    }

    @Override
    public Locale getLocale() {
        return Application.getDefaultLocale();
    }

    /*************** cookie methods ***************/

    public void addLoginCookie(String name, String value, int expirationDays){
        Cookie cookie=new Cookie("bandika_"+name,value);
        //todo
        cookie.setPath("/api/user/login");
        cookie.setMaxAge(expirationDays*24*60*60);
        cookies.put(cookie.getName(),cookie);
    }

    public Map<String,String> readLoginCookies(){
        Map<String, String> map=new HashMap<>();
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().startsWith("bandika_")) {
                map.put(cookie.getName().substring(8), cookie.getValue());
            }
        }
        return map;
    }

}
