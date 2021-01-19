/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class JspContentData extends ContentData {

    protected String jsp = "";

    public JspContentData() {
    }

    public JspContentData(String jsp) {
        setJsp(jsp);
    }

    // base data

    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    @Override
    public String getSearchContent() {
        return "";
    }

    @Override
    public ContentViewContext createViewContext(ViewType viewType) {
        return new ContentViewContext(this, viewType);
    }

    // view

    public void displayContent(PageContext context, RequestData rdata) throws ServletException, IOException {
        context.include(jsp);
    }

}
