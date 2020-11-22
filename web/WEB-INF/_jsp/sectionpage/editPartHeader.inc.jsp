<%--
  Bandika JSON CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.page.SectionPageData" %>
<%@ page import="de.elbe5.page.SectionPartData" %>
<%@ page import="de.elbe5.layout.Layouts" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    SectionPageData contentData = rdata.getCurrentContent(SectionPageData.class);
    assert contentData != null;
    SectionPartData partData = rdata.get(RequestData.KEY_PART,SectionPartData.class);
    assert partData != null;
    List<String> layoutNames = Layouts.getLayoutNames(SectionPartData.LAYOUT_TYPE);
    List<String> partTypes = new ArrayList<>();
    contentData.collectPartTypes(partTypes);
%>
            <input type="hidden" name="<%=partData.getPartPositionName()%>" value="<%=partData.getPosition()%>" class="partPos"/>
            <div class="partEditButtons">
                <div class="btn-group btn-group-sm" role="group">
                    <div class="btn-group btn-group-sm" role="group">
                        <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=$SH("_newPart",locale)%>"></button>
                        <div class="dropdown-menu">
                            <% for (String partType : partTypes) {
                                String name = $SH("type." + partType, locale);
                                for (String layout : layoutNames){
                                    String layoutName = $SH("layout."+layout,locale);
                            %>
                            <a class="dropdown-item" href="" onclick="return addPart(<%=partData.getId()%>,'<%=$H(partData.getSectionName())%>','<%=partType%>','<%=$H(layout)%>');">
                                <%=name%> (<%=layoutName%>)
                            </a>
                            <%}%>
                            <a class="dropdown-item" href="" onclick="return addPart(<%=partData.getId()%>,'<%=$H(partData.getSectionName())%>','<%=partType%>');">
                                <%=name%>
                            </a>
                            <%}%>
                        </div>
                    </div>
                    <div class="btn-group btn-group-sm" role="group">
                        <button type="button" class="btn  btn-secondary dropdown-toggle fa fa-ellipsis-h" data-toggle="dropdown" title="<%=$SH("_more",locale)%>"></button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="" onclick="return movePart(<%=partData.getId()%>,-1);"><%=$SH("_up", locale)%>
                            </a>
                            <a class="dropdown-item" href="" onclick="return movePart(<%=partData.getId()%>,1);"><%=$SH("_down", locale)%>
                            </a>
                            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return deletePart(<%=partData.getId()%>);"><%=$SH("_delete", locale)%>
                            </a>
                        </div>
                    </div>
                </div>
            </div>








