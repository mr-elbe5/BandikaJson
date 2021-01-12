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
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ContentData contentData = (ContentData) rdata.getRequestObject("treePage");
    assert contentData != null;
%>
<li class="open">
    <a id="<%=contentData.getId()%>" href="" onclick="return ckLinkCallback('/ajax/content/show/<%=contentData.getId()%>');"><%=contentData.getName()%>
    </a>
    <ul>
        <% if (!contentData.getChildren().isEmpty()) {
            List<ContentData> children = contentData.getChildren();
            for (ContentData subPage : children) {
                rdata.setRequestObject("treePage", subPage); %>
                <jsp:include page="/WEB-INF/_jsp/ckeditor/pageLinkBrowserFolder.inc.jsp" flush="true"/>
            <%}
            rdata.setRequestObject("treePage", contentData);
        }%>
    </ul>
</li>

