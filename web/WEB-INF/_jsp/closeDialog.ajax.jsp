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
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.request.RequestKeys" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    String url = rdata.getString(RequestKeys.KEY_URL);
    String msg = rdata.getString(RequestKeys.KEY_MESSAGE);
    String msgType = rdata.getString(RequestKeys.KEY_MESSAGETYPE);
    %>
<div id="pageContent">
    <form action="<%=url%>" method="POST" id="forwardform" accept-charset="UTF-8">
        <%if (!msg.isEmpty()) {%>
        <input type="hidden" name="<%=RequestKeys.KEY_MESSAGE%>" value="<%=$H(msg)%>"/>
        <input type="hidden" name="<%=RequestKeys.KEY_MESSAGETYPE%>" value="<%=$H(msgType)%>"/>
        <%}%>
    </form>
</div>
<script type="text/javascript">
    $('#forwardform').submit();
</script>
