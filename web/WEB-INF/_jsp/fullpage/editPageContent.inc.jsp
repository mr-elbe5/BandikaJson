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
<%@ page import="de.elbe5.page.FullPageData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    FullPageData contentData = rdata.getCurrentContent(FullPageData.class);
    assert (contentData != null);
    %>

<form action="/ctrl/<%=contentData.getTypeKey()%>/savePage/<%=contentData.getId()%>" method="post" id="pageform" name="pageform" accept-charset="UTF-8">
    <div class="btn-group btn-group-sm pageEditButtons">
        <button type="submit" class="btn btn-sm btn-success" onclick="updateEditor();"><%=$SH("_savePage", locale)%></button>
        <button class="btn btn-sm btn-secondary" onclick="return linkTo('/ctrl/<%=contentData.getTypeKey()%>/cancelEditPage/<%=contentData.getId()%>');"><%=$SH("_cancel", locale)%></button>
    </div>
    <div class="paragraph">
        <div class="ckeditField\" id="content" contenteditable="true"><%=contentData.getContent()%></div>
    </div>
    <input type="hidden" name="content" value="<%=contentData.getContent()%>" />
</form>


<script type="text/javascript">
 $('#content').ckeditor({toolbar : 'Full',filebrowserBrowseUrl : '/ajax/ckeditor/openLinkBrowser?contentId=<%=contentData.getId()%>',filebrowserImageBrowseUrl : '/ajax/ckeditor/openImageBrowser?contentId=<%=contentData.getId()%>'});

 function updateEditor(){
     if (CKEDITOR) {
         $('input[name="content"]').val(CKEDITOR.instances['content'].getData());
     }
 }
</script>

