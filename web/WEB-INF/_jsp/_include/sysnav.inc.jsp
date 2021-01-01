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
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ page import="de.elbe5.rights.SystemRights" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ContentData contentData = rdata.getCurrentContent();
    int contentId = contentData==null ? 0 : contentData.getId();
    Locale locale = rdata.getLocale();
    String userClass=rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
%>
<ul class="nav justify-content-end">
    <% if (rdata.isLoggedIn()){
        if (SystemRights.hasUserAnySystemRight(rdata.getCurrentUser())) {%>
    <li class="nav-item"><a class="nav-link fa fa-cog" href="/ctrl/admin/openContentAdministration" title="<%=$SH("_administration", locale)%>"></a></li>
        <%}
        if (contentData instanceof PageData && !contentData.isEditing() && ContentRights.hasUserEditRight(rdata.getCurrentUser(), contentData)) {%>
            <li class="nav-item"><a class="nav-link fa fa-edit" href="/ctrl/<%=contentData.getTypeKey()%>/openEditPage/<%=contentData.getId()%>" title="<%=$SH("_editPage", locale)%>"></a></li>
        <%
            if (contentData.hasUnpublishedDraft()) {
                if (contentData.isPublished()){
                    if (contentData.isPublishedView()){%>
        <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/ctrl/<%=contentData.getTypeKey()%>/showDraft/<%=contentId%>" title="<%=$SH("_showDraft", locale)%>" ></a></li>
            <%} else{%>
        <li class="nav-item"><a class="nav-link fa fa-eye" href="/ctrl/<%=contentData.getTypeKey()%>/showPublished/<%=contentId%>" title="<%=$SH("_showPublished", locale)%>"></a></li>
        <%}
            }
            if (ContentRights.hasUserApproveRight(rdata.getCurrentUser(), contentData)) {%>
        <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/ctrl/<%=contentData.getTypeKey()%>/publishPage/<%=contentId%>" title="<%=$SH("_publish", locale)%>"></a></li>
            <%}
            }
        }
    }%>
    <%--<li>
        <a class="nav-link fa fa-search" href="/ctrl/search/openSearch" title="<%=$SH("_search", locale)%>">
        </a>
    </li>--%>
    <li class="nav-item">
        <a class="nav-link fa <%=userClass%>" data-toggle="dropdown" title="<%=$SH("_user",locale)%>"></a>
        <div class="dropdown-menu">
            <% if (rdata.isLoggedIn()) {%>
            <a class="dropdown-item" href="/ctrl/user/openProfile"><%=$SH("_profile", locale)%>
            </a>
            <a class="dropdown-item" href="/ctrl/user/logout"><%=$SH("_logout", locale)%>
            </a>
            <% } else {%>
            <a class="dropdown-item" href="" onclick="return openModalDialog('/ajax/user/openLogin');"><%=$SH("_login", locale)%>
            </a>
            <%}%>
        </div>
    </li>
</ul>

