<%--
  BandikaJson CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%@ taglib uri="/WEB-INF/sectiontags.tld" prefix="section" %>
        <form:message />
        <section class="contentSection" id="content">
            <section:section name="main" cssClass="row"></section:section>
        </section>

