/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Application;
import de.elbe5.page.PageController;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestType;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.ForwardResponse;
import de.elbe5.response.IResponse;
import de.elbe5.response.RedirectResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentServlet extends WebServlet {

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Application.ENCODING);
        SessionRequestData rdata = new SessionRequestData(method, request, RequestType.page);
        request.setAttribute(RequestData.KEY_REQUESTDATA, rdata);
        rdata.readRequestParams();
        rdata.initSession();
        String url=request.getRequestURI().toLowerCase();
        try {
            IResponse result;
            if (url.endsWith(".html")) {
                result = PageController.getInstance().show(request.getRequestURI(), rdata);
            }
            else{
                result = new RedirectResponse(getDefaultRoute(rdata));
            }
            if (rdata.hasCookies())
                rdata.setCookies(response);
            result.processResponse(getServletContext(), rdata, response);
        }
        catch (ResponseException ce){
            handleException(request,response, ce);
        }
        catch (Exception e){
            handleException(request,response, new ResponseException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
        }
    }

    protected void handleException(HttpServletRequest request, HttpServletResponse response, ResponseException ce){
        RequestDispatcher rd = request.getServletContext().getRequestDispatcher("/WEB-INF/_jsp/exception.jsp");
        try {
            request.setAttribute("exception",ce);
            rd.forward(request, response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


}
