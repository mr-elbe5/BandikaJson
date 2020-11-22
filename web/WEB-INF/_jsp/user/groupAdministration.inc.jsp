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
<%@ page import="de.elbe5.user.GroupData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    List<GroupData> groups = null;
    try {
        groups = userContainer().getGroups();
    } catch (Exception ignore) {
    }
    int groupId = rdata.getInt("groupId");
%><!--groups-->
<li class="open">
    <span><%=$SH("_groups",locale)%></span>
    <div class="icons">
        <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ajax/user/openCreateGroup');" title="<%=$SH("_new",locale)%>"> </a>
    </div>
    <ul>
        <%
            if (groups != null) {
                for (GroupData group : groups) {
        %>
        <li class="<%=groupId==group.getId() ? "open" : ""%>">
            <span><%=$H(group.getName())%></span>
            <div class="icons">
                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ajax/user/openEditGroup/<%=group.getId()%>');" title="<%=$SH("_edit",locale)%>"></a>
                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/user/deleteGroup/<%=group.getId()%>?version=<%=group.getVersion()%>');" title="<%=$SH("_delete",locale)%>"></a>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>


