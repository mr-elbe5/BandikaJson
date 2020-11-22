/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.base.data.StringUtil;
import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestType;
import de.elbe5.response.AjaxResponse;
import de.elbe5.response.IResponse;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.RedirectResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 50 * 5)
public class ControllerServlet extends WebServlet {

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Application.ENCODING);
        String uri = request.getRequestURI();
        if (uri.startsWith("/ctrl/")){
            processControllerRequest(method, request, response, RequestType.page);
        }
        else if (uri.startsWith("/ajax/")){
            processControllerRequest(method, request, response, RequestType.ajax);
        }
        else{
            processDefaultRequest(method, request, response);
        }
    }

    protected void processControllerRequest(String method, HttpServletRequest request, HttpServletResponse response, RequestType type) throws IOException {
        request.setCharacterEncoding(Application.ENCODING);
        SessionRequestData rdata = new SessionRequestData(method, request, type);
        request.setAttribute(RequestData.KEY_REQUESTDATA, rdata);
        String uri = request.getRequestURI();
        // skip "/ctrl/" or "/ajax/"
        StringTokenizer stk = new StringTokenizer(uri.substring(6), "/", false);
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
        rdata.readRequestParams();
        rdata.initSession();
        try {
            IResponse result = getView(controller, methodName, rdata);
            if (rdata.hasCookies())
                rdata.setCookies(response);
            result.processResponse(getServletContext(), rdata, response);
        } catch (ResponseException ce) {
            handleException(request, response, ce, rdata.getType());
        } catch (Exception | AssertionError e) {
            handleException(request, response, new ResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), rdata.getType());
        }
    }

    public IResponse getView(Controller controller, String methodName, SessionRequestData rdata) {
        if (controller==null)
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        try {
            Method controllerMethod = controller.getClass().getMethod(methodName, SessionRequestData.class);
            Object result = controllerMethod.invoke(controller, rdata);
            if (result instanceof IResponse) {
                if (rdata.isAjaxRequest() && !(result instanceof AjaxResponse)){
                    Log.warn("ajax request but no ajax response");
                }
                if (rdata.isPageRequest() && (result instanceof AjaxResponse)){
                    Log.warn("page request but ajax response");
                }
                return (IResponse) result;
            }
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchMethodException | InvocationTargetException e){
            throw new ResponseException(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (IllegalAccessException e) {
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    protected void processDefaultRequest(String method, HttpServletRequest request, HttpServletResponse response) {
        SessionRequestData rdata = new SessionRequestData(method, request, RequestType.page);
        request.setAttribute(RequestData.KEY_REQUESTDATA, rdata);
        try {
            IResponse result = new RedirectResponse(getDefaultRoute(rdata));
            if (rdata.hasCookies())
                rdata.setCookies(response);
            result.processResponse(getServletContext(), rdata, response);
        } catch (ResponseException ce) {
            handleException(request, response, ce, RequestType.page);
        } catch (Exception | AssertionError e) {
            handleException(request, response, new ResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), RequestType.page);
        }
    }

    protected void handleException(HttpServletRequest request, HttpServletResponse response, ResponseException ce, RequestType requestType){
        RequestDispatcher rd = request.getServletContext().getRequestDispatcher("/WEB-INF/_jsp/exception.jsp");
        try {
            request.setAttribute("exception",ce);
            rd.forward(request, response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
