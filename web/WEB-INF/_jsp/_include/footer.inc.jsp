<%--
  BandikaJson CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ContentData currentContent = rdata.getCurrentContent();
    if (currentContent == null)
        currentContent = contentContainer().getContentRoot();;
%>

        <ul class="nav">
            <li class="nav-item">
                <a class="nav-link">&copy; <%=Application.getConfiguration().getCopyright()%>
                </a>
            </li>
            <% for (ContentData data : contentContainer().getContentRoot().getChildren()) {
                if (data.getNavType().equals(ContentData.NAV_TYPE_FOOTER) && ContentRights.hasUserReadRight(rdata.getCurrentUser(), currentContent)) {%>
            <li class="nav-item">
                <a class="nav-link" href="<%=data.getUrl()%>"><%=$H(data.getDisplayName())%>
                </a>
            </li>
            <%
                    }
                }
            %>
        </ul>

