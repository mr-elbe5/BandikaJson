/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public class StaticPageResponse implements IResponse {

    private PageData page;

    public StaticPageResponse(PageData page) {
        this.page = page;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response) {
        TemplateContext templateContext = new TemplateContext(rdata, page);
        String html = page.getHtml(templateContext);
        response.setContentType(MessageFormat.format("text/html;charset={0}", Application.ENCODING));
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Access-Control-Allow-Origin","*");
        try {
            OutputStream out = response.getOutputStream();
            if (html == null || html.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = html.getBytes(Application.ENCODING);
                response.setHeader("Content-Length", Integer.toString(bytes.length));
                out.write(bytes);
            }
            out.flush();
        } catch (IOException ioe) {
            Log.error("response error", ioe);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
