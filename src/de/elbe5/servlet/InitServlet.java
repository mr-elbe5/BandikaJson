/*
 Bandika JSON CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.application.Application;
import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import de.elbe5.data.DataAccessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;

public class InitServlet extends WebServlet implements DataAccessor {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        ServletContext context=servletConfig.getServletContext();
        File catalinaAppDir = new File(context.getRealPath("/"));
        File catalinaAppBaseDir = catalinaAppDir.getParentFile();
        String logName = catalinaAppBaseDir.getName();
        Log.initLog(logName);
        ApplicationPath.initializePath(catalinaAppBaseDir, catalinaAppDir);
        Application.initialize();
    }

}
