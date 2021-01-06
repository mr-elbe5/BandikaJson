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
<%@ page import="de.elbe5.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.templatepage.SectionData" %>
<%@ page import="de.elbe5.layout.Layouts" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.templatepage.TemplatePartData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    TemplatePageData contentData = rdata.getCurrentContent(TemplatePageData.class);
    assert contentData != null;
    SectionData sectionData = rdata.get("sectionData", SectionData.class);
    assert sectionData != null;
    List<String> partTypes = new ArrayList<>();
    contentData.collectPartTypes(partTypes);
    List<String> layoutNames = Layouts.getLayoutNames(TemplatePartData.LAYOUT_TYPE);
%>
<div class="section <%=sectionData.getCssClass()%>" id="<%=sectionData.getSectionId()%>" title="Section <%=sectionData.getName()%>">
    <%-- empty section --%>
    <div class="addPartButtons">
        <div class="btn-group btn-group-sm editheader" title="Section <%=$H(sectionData.getName())%>">
            <button class="btn  btn-primary dropdown-toggle fa fa-plus" data-toggle="dropdown" title="<%=$SH("_newPart",locale)%>"></button>
            <div class="dropdown-menu">
                <% for (String partType : partTypes) {
                    String name = $SH("type." + partType, locale);
                    for (String layout : layoutNames){
                        String layoutName = $SH("layout."+layout,locale);
                %>
                <a class="dropdown-item" href="" onclick="return addPart(-1,'<%=$H(sectionData.getName())%>','<%=partType%>','<%=$H(layout)%>');">
                    <%=name%> (<%=layoutName%>)
                </a>
                <%}%>
                <a class="dropdown-item" href="" onclick="return addPart(-1,'<%=$H(sectionData.getName())%>','<%=partType%>');"><%=name%>
                </a>
                <%}%>
            </div>
        </div>
    </div>
    <%-- parts exist --%>
    <%for (TemplatePartData partData : sectionData.getParts()) {
        rdata.put(RequestData.KEY_PART, partData);
        String include = partData.getEditPartInclude();
        if (include != null) {%>
            <jsp:include page="<%=include%>" flush="true"/>
        <%}
        request.removeAttribute(RequestData.KEY_PART);
    }%>
</div>







