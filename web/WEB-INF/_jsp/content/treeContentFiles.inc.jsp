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
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.file.FileData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
    int fileId=rdata.getInt("fileId");
%>
        <li class="files open">
            <span>[<%=$SH("_files", locale)%>]</span>
            <%if (ContentRights.hasUserEditRight(rdata.getCurrentUser(), contentData)) {%>
            <div class="icons">
                <% if (rdata.hasClipboardData(RequestData.KEY_FILE)) {%>
                <a class="icon fa fa-paste" href="/ctrl/file/pasteFile?parentId=<%=contentData.getId()%>&parentVersion=<%=contentData.getVersion()%>" title="<%=$SH("_pasteFile",locale)%>"> </a>
                <%}%>
                <a class="icon fa fa-plus" onclick="return openModalDialog('/ajax/file/openCreateFile?parentId=<%=contentData.getId()%>');">
                </a>
            </div>
            <%}%>
            <ul>
                <%
                    List<FileData> files = contentData.getFiles();
                    for (FileData file : files) {%>
                <li class="<%=fileId==file.getId() ? "current" : ""%>">
                    <div class="treeline">
                        <span class="treeImage" id="<%=file.getId()%>">
                            <%=file.getDisplayName()%>
                            <% if (file.isImage()){%>
                            <span class="hoverImage">
                                <img src="<%=file.getPreviewURL()%>" alt="<%=$H(file.getFileName())%>"/>
                            </span>
                            <%}%>
                        </span>
                        <div class="icons">
                            <a class="icon fa fa-eye" href="<%=file.getURL()%>" target="_blank" title="<%=$SH("_view",locale)%>"> </a>
                            <a class="icon fa fa-download" href="<%=file.getURL()%>?download=true" title="<%=$SH("_download",locale)%>"> </a>
                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ajax/file/openEditFile/<%=file.getId()%>');" title="<%=$SH("_edit",locale)%>"> </a>
                            <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/file/cutFile/<%=file.getId()%>');" title="<%=$SH("_cut",locale)%>"> </a>
                            <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/file/copyFile/<%=file.getId()%>');" title="<%=$SH("_copy",locale)%>"> </a>
                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/file/deleteFile/<%=file.getId()%>');" title="<%=$SH("_delete",locale)%>"> </a>
                        </div>
                    </div>
                </li>
                <%
                    }%>
            </ul>
        </li>

