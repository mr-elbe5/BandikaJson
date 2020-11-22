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
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.file.FileData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    FileData fileData = rdata.getSessionObject(RequestData.KEY_FILE,FileData.class);
    assert (fileData != null);
    String url = "/ajax/file/saveFile/" + fileData.getId();
    boolean fileRequired= fileData.isNew();
%>
<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editFile",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="fileform" ajax="true" multi="true">
            <div class="modal-body">
                <form:formerror/>
                <form:line label="_idAndFileName"><%=$I(fileData.getId())%> - <%=$H(fileData.getFileName())%>
                </form:line>
                <form:line label="_creation"><%=$DT(fileData.getCreationDate(), locale)%> - <%=$H(userContainer().getUser(fileData.getCreatorId()).getName())%>
                </form:line>
                <form:line label="_lastChange"><%=$DT(fileData.getChangeDate(), locale)%> - <%=$H(userContainer().getUser(fileData.getChangerId()).getName())%>
                </form:line>

                <form:file name="file" label="_file" required="<%=fileRequired%>"/>
                <form:text name="displayName" label="_name" value="<%=$H(fileData.getDisplayName())%>"/>
                <form:textarea name="description" label="_description" height="3em"><%=$H(fileData.getDescription())%></form:textarea>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save",locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>


