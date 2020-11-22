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
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentSessionContent();
    assert (contentData != null);
    %>
                <form:formerror/>
                <h3><%=$SH("_settings", locale)%>
                </h3>
                <form:line label="_idAndUrl"><%=$I(contentData.getId())%>
                </form:line>
                <form:line label="_creation"><%=$DT(contentData.getCreationDate(), locale)%> - <%=$H(userContainer().getUser(contentData.getCreatorId()).getName())%>
                </form:line>
                <% if (!contentData.isNew()){%>
                <form:line label="_lastChange"><%=$DT(contentData.getChangeDate(), locale)%> - <%=$H(userContainer().getUser(contentData.getChangerId()).getName())%>
                </form:line>
                <%}%>
                <form:text name="displayName" label="_name" required="true" value="<%=$H(contentData.getDisplayName())%>"/>
                <form:textarea name="description" label="_description" height="5em"><%=$H(contentData.getDescription())%></form:textarea>
                <form:select name="accessType" label="_accessType">
                    <option value="<%=ContentData.ACCESS_TYPE_OPEN%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_OPEN) ? "selected" : ""%>><%=$SH("_accessTypeOpen", locale)%>
                    </option>
                    <option value="<%=ContentData.ACCESS_TYPE_INHERITS%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_INHERITS) ? "selected" : ""%>><%=$SH("_accessTypeInherits", locale)%>
                    </option>
                    <option value="<%=ContentData.ACCESS_TYPE_INDIVIDUAL%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_INDIVIDUAL) ? "selected" : ""%>><%=$SH("_accessTypeIndividual", locale)%>
                    </option>
                </form:select>
                <form:select name="navType" label="_navType">
                    <option value="<%=ContentData.NAV_TYPE_NONE%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_NONE) ? "selected" : ""%>><%=$SH("_navTypeNone", locale)%>
                    </option>
                    <option value="<%=ContentData.NAV_TYPE_HEADER%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) ? "selected" : ""%>><%=$SH("_navTypeHeader", locale)%>
                    </option>
                    <option value="<%=ContentData.NAV_TYPE_FOOTER%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_FOOTER) ? "selected" : ""%>><%=$SH("_navTypeFooter", locale)%>
                    </option>
                </form:select>
                <form:line label="_active" padded="true">
                    <form:check name="active" value="true" checked="<%=contentData.isActive()%>"/>
                </form:line>



