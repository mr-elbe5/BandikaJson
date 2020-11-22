/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Application;
import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class WebServlet extends HttpServlet {

    public final static String GET = "GET";
    public final static String POST = "POST";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(GET,request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(POST, request, response);
    }

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    protected String getDefaultRoute(SessionRequestData rdata){
        ContentData contentData = Application.getContent().getContentRoot();
        String url;
        if (contentData!=null)
            url=contentData.getUrl();
        else if (rdata.isLoggedIn())
            url="/ctrl/admin/openSystemAdministration";
        else{
            url="/blank.jsp";
        }
        return url;
    }

}
