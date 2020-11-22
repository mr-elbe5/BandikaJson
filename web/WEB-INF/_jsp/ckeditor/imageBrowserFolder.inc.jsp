<%--
  Bandika JSON CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ page import="de.elbe5.file.FileData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getRequestObject(RequestData.KEY_CONTENT,ContentData.class);
    assert contentData != null;
    List<Integer> parentIds=(List<Integer>) rdata.getRequestObject("parentIds");
    assert(parentIds!=null);
    boolean isParent=parentIds.contains(contentData.getId());
%>
<li class="<%=isParent ? "open" : ""%>">
    <a id="page_<%=contentData.getId()%>"><%=contentData.getName()%>
    </a>
    <ul id="page_ul_<%=contentData.getId()%>">
        <% if (
            ContentRights.hasUserReadRight(rdata.getCurrentUser(),contentData)){
            List<FileData> images = contentData.getFiles(FileData.class);
            for (FileData image : images) {
                if (image.isImage()){%>
        <li>
            <div class="treeline">
                <a id="<%=image.getId()%>" href="" onclick="return ckImgCallback('<%=image.getURL()%>');">
                    <img src="<%=image.getPreviewURL()%>" alt="<%=$H(image.getDisplayName())%>"/>
                    <%=$H(image.getDisplayName())%>
                </a>
                <a class="fa fa-eye" title="<%=$SH("_view",locale)%>" href="<%=image.getURL()%>" target="_blank"> </a>
            </div>
        </li>
        <%}}
        }
        for (ContentData subPage : contentData.getChildren()) {
            rdata.setRequestObject(RequestData.KEY_CONTENT, subPage);
        %>
        <jsp:include page="/WEB-INF/_jsp/ckeditor/imageBrowserFolder.inc.jsp" flush="true"/>
        <%}
        rdata.setRequestObject(RequestData.KEY_CONTENT, contentData);%>
    </ul>
</li>
