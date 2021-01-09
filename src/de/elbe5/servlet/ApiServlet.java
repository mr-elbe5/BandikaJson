/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Application;
import de.elbe5.base.data.StringUtil;
import de.elbe5.request.ApiRequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.request.RequestReader;
import de.elbe5.response.IResponse;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 50 * 5)
public class ApiServlet extends WebServlet {

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Application.ENCODING);
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/")){
            processApiRequest(method,request,response);
        }
    }

    protected void processApiRequest(String method, HttpServletRequest request, HttpServletResponse response) {
        ApiRequestData rdata = new ApiRequestData(method, request);
        request.setAttribute(RequestKeys.KEY_REQUESTDATA, rdata);
        String uri = request.getRequestURI();
        // skip "/api/"
        StringTokenizer stk = new StringTokenizer(uri.substring(5), "/", false);
        String methodName = "";
        Controller controller = null;
        if (stk.hasMoreTokens()) {
            String controllerName = stk.nextToken();
            if (stk.hasMoreTokens()) {
                methodName = stk.nextToken();
                if (stk.hasMoreTokens()) {
                    rdata.setId(StringUtil.toInt(stk.nextToken()));
                }
            }
            controller = ControllerCache.getController(controllerName);
        }
        RequestReader.readRequestParams(request, rdata, method.equals("POST"));
        try {
            IResponse result = getResponse(controller, methodName, rdata);
            result.processResponse(getServletContext(), rdata, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public IResponse getResponse(Controller controller, String methodName, ApiRequestData rdata) {
        if (controller==null)
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        try {
            Method controllerMethod = controller.getClass().getMethod(methodName, ApiRequestData.class);
            Object response = controllerMethod.invoke(controller, rdata);
            if (response instanceof IResponse)
                return (IResponse) response;
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchMethodException | InvocationTargetException e){
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (IllegalAccessException e) {
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
