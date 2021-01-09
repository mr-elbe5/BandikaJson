/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.application.Application;
import de.elbe5.application.Configuration;
import de.elbe5.base.log.Log;
import de.elbe5.request.*;
import de.elbe5.response.IResponse;
import de.elbe5.rights.ContentRights;
import de.elbe5.servlet.WebServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FileServlet extends WebServlet {

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Application.ENCODING);
        SessionRequestData rdata = new SessionRequestData(method, request, RequestType.file);
        request.setAttribute(RequestKeys.KEY_REQUESTDATA, rdata);
        RequestReader.readRequestParams(request, rdata, method.equals("POST"));
        rdata.initSession();
        String fileName = request.getPathInfo();
        boolean isPreview = false;
        if (fileName == null) {
            Log.error("no file requested");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8).substring(1);
        File file;
        if (fileName.equals("logo.png")){
            file = Configuration.getLogoFile();
        }
        else{
            String name = FileService.getFileNameWithoutExtension(fileName);
            if (name.startsWith("preview")){
                isPreview = true;
                name=name.substring(7);
            }
            int id = Integer.parseInt(name);
            FileData data = Application.getContent().getFile(id);
            assert(data!=null);
            if (!ContentRights.hasUserReadRight(rdata.getCurrentUser(),data.getParentId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (isPreview){
                file = data.getViewablePreviewFile(rdata);
            }
            else{
                file = data.getViewableFile(rdata);
            }
            fileName = data.getDisplayFileName();
        }
        // if not exists, create from database
        RangeInfo rangeInfo = null;
        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            rangeInfo = new RangeInfo(rangeHeader, file.length());
        }
        IResponse result = new FileResponse(file, fileName, rangeInfo);
        result.processResponse(getServletContext(), rdata, response);
    }

}