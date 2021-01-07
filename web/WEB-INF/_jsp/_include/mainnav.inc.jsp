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
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ page import="java.util.ArrayList" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ContentData home = contentContainer().getContentRoot();
    ContentData currentContent = rdata.getCurrentContent();
    if (currentContent == null)
        currentContent = home;
%>

            <section class="col-12 menu">
                <nav class="navbar navbar-expand-lg navbar-light">
                    <a class="navbar-brand" href="/"><img src="/files/logo.png"
                                                          alt="<%=$H(Application.getConfiguration().getApplicationName())%>"/></a>
                    <button class="navbar-toggler" type="button" data-toggle="collapse"
                            data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                            aria-expanded="false" aria-label="Toggle navigation">
                        <span class="fa fa-bars"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
                        <ul class="navbar-nav mr-auto">
                            <%
                                if (home != null) {
                                    List<Integer> activeIds = contentContainer().collectParentIds(currentContent.getId());
                                    activeIds.add(currentContent.getId());
                                    for (ContentData contentData : home.getChildren()) {
                                        if (contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) && ContentRights.hasUserReadRight(rdata.getCurrentUser(), contentData)) {
                                            List<ContentData> children = new ArrayList<>();
                                            for (ContentData child : contentData.getChildren()) {
                                                if (child.getNavType().equals(ContentData.NAV_TYPE_HEADER) && ContentRights.hasUserReadRight(rdata.getCurrentUser(), child))
                                                    children.add(child);
                                            }
                                            if (!children.isEmpty()) {
                            %>
                            <li class="nav-item dropdown">
                                <a class="nav-link <%=activeIds.contains(contentData.getId())? "active" : ""%> dropdown-toggle"
                                   data-toggle="dropdown" href="<%=contentData.getUrl()%>" role="button"
                                   aria-haspopup="true" aria-expanded="false"><%=$H(contentData.getDisplayName())%>
                                </a>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item <%=activeIds.contains(contentData.getId())? "active" : ""%>"
                                       href="<%=contentData.getUrl()%>"><%=$H(contentData.getDisplayName())%>
                                    </a>
                                    <% for (ContentData child : children) {%>
                                    <a class="dropdown-item <%=activeIds.contains(contentData.getId())? "active" : ""%>"
                                       href="<%=child.getUrl()%>"><%=$H(child.getDisplayName())%>
                                    </a>
                                    <%}%>
                                </div>
                            </li>
                            <%} else {%>
                            <li class="nav-item <%=activeIds.contains(contentData.getId())? "active" : ""%>">
                                <a class="nav-link <%=activeIds.contains(contentData.getId())? "active" : ""%>"
                                   href="<%=contentData.getUrl()%>"><%=$H(contentData.getDisplayName())%>
                                </a>
                            </li>
                            <%
                                            }
                                        }
                                    }
                                }
                            %>
                        </ul>
                    </div>
                </nav>
            </section>

