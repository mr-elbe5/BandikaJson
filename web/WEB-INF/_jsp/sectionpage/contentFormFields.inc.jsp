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
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.page.SectionPageData" %>
<%@ page import="de.elbe5.layout.Layouts" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    SectionPageData contentData = rdata.getCurrentContent(SectionPageData.class);
    assert (contentData != null);
    List<String> pageNames = Layouts.getLayoutNames(SectionPageData.PAGE_TYPE);
%>
                <form:select name="layout" label="_pageLayout" required="true">
                    <option value="" <%=contentData.getLayout().isEmpty() ? "selected" : ""%>><%=$SH("_pleaseSelect",locale)%>
                    </option>
                    <% for (String layout : pageNames) {
                        String layoutName = $SH("layout." + layout,locale);
                    %>
                    <option value="<%=$H(layout)%>" <%=layout.equals(contentData.getLayout()) ? "selected" : ""%>><%=layoutName%>
                    </option>
                    <%}%>
                </form:select>


