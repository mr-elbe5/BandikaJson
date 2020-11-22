<%--
  BandikaJson CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
    List<String> childTypes= new ArrayList<>();
    contentData.collectChildTypes(childTypes);
%>
<li class="open">
    <span>
        <%=$H(contentData.getDisplayName())%>
    </span>
    <%if (ContentRights.hasUserEditRight(rdata.getCurrentUser(), contentData)) {%>
    <div class="icons">
        <a class="icon fa fa-eye" href="" onclick="return linkTo('/ctrl/<%=contentData.getTypeKey()%>/show/<%=contentData.getId()%>');" title="<%=$SH("_view",locale)%>"> </a>
        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ajax/<%=contentData.getTypeKey()%>/openEditContentData/<%=contentData.getId()%>');" title="<%=$SH("_edit",locale)%>"> </a>
        <a class="icon fa fa-key" href="" onclick="return openModalDialog('/ajax/<%=contentData.getTypeKey()%>/openEditRights/<%=contentData.getId()%>');" title="<%=$SH("_rights",locale)%>"> </a>
        <% if (contentData.getId()!=ContentData.ID_ROOT){%>
        <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/<%=contentData.getTypeKey()%>/cutContent/<%=contentData.getId()%>');" title="<%=$SH("_cut",locale)%>"> </a>
        <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/<%=contentData.getTypeKey()%>/copyContent/<%=contentData.getId()%>');" title="<%=$SH("_copy",locale)%>"> </a>
        <%}%>
        <%if (contentData.hasChildren()){%>
        <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/ajax/<%=contentData.getTypeKey()%>/openSortChildPages/<%=contentData.getId()%>');" title="<%=$SH("_sortChildPages",locale)%>"> </a>
            <%}%>
        <% if (contentData.getId()!=ContentData.ID_ROOT){%>
        <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/<%=contentData.getTypeKey()%>/deleteContent/<%=contentData.getId()%>');" title="<%=$SH("_delete",locale)%>"> </a>
        <%}%>
        <% if (rdata.hasClipboardData(RequestData.KEY_CONTENT)) {%>
        <a class="icon fa fa-paste" href="/ctrl/<%=contentData.getTypeKey()%>/pasteContent?parentId=<%=contentData.getId()%>&parentVersion=<%=contentData.getVersion()%>" title="<%=$SH("_pasteContent",locale)%>"> </a>
        <%
        }
        if (!childTypes.isEmpty()) {%>
        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=$SH("_newContent",locale)%>"></a>
        <div class="dropdown-menu">
            <%for (String pageType : childTypes) {
                String name = $SH("type." + pageType, locale);%>
            <a class="dropdown-item" onclick="return openModalDialog('/ajax/<%=contentData.getTypeKey()%>/openCreateContentData?parentId=<%=contentData.getId()%>&type=<%=pageType%>');"><%=name%>
            </a>
            <%
                }%>
        </div>
        <%
        }%>
    </div>
    <%}%>
    <ul>
        <jsp:include page="/WEB-INF/_jsp/content/treeContentFiles.inc.jsp" flush="true" />
        <% if (contentData.hasChildren()) {
            for (ContentData childData : contentData.getChildren()) {
                if (ContentRights.hasUserReadRight(rdata.getCurrentUser(), childData)) {
                    rdata.setCurrentRequestContent(childData); %>
        <jsp:include page="/WEB-INF/_jsp/content/treeContent.inc.jsp" />
                <% rdata.setCurrentRequestContent(contentData);
                }
            }
        }%>
    </ul>
</li>

