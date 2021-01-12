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
<%@ page import="de.elbe5.content.*" %>
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="de.elbe5.contentcontrol.Breadcrumb" %>
<%@ page import="de.elbe5.contentcontrol.Footer" %>
<%@ page import="de.elbe5.contentcontrol.MainNav" %>
<%@ page import="de.elbe5.contentcontrol.SysNav" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentViewContext context = rdata.getViewContext();
    ContentData contentData;
    ViewType viewType = ViewType.show;
    if (context!=null){
        contentData = context.getContentData();
        viewType = context.getViewType();
    }
    else {
        contentData = contentContainer().getContentRoot();
    }
    UserData currentUser = rdata.getCurrentUser();
    String title = Application.getConfiguration().getApplicationName() + (contentData != null ? " | " + contentData.getDisplayName() : "");
    String keywords = contentData != null ? contentData.getKeywords() : title;
    String description = contentData != null ? contentData.getDescription() : "";
%>
<!DOCTYPE html>
<html lang="<%=locale.getLanguage()%>">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <title><%=$H(title)%>
    </title>
    <meta name="keywords" content="<%=$H(keywords)%>">
    <meta name="description" content="<%=$H(description)%>">
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="<%=Application.getConfiguration().getStyle()%>"/>
    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
    <script type="text/javascript" src="/static-content/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="/static-content/ckeditor/adapters/jquery.js"></script>
    <script type="text/javascript" src="/static-content/js/bandika.js"></script>
</head>
<body>
<div class="container">
    <header>
        <section class="sysnav">
            <%=SysNav.getHtml(contentData, viewType, currentUser, rdata.isLoggedIn(), locale, false)%>
        </section>
        <div class="menu row">
            <%=MainNav.getHtml(contentData, currentUser)%>
        </div>
        <div class="bc row">
            <%=Breadcrumb.getHtml(contentData)%>
        </div>
    </header>
    <main id="main" role="main">
        <div id="pageContainer">
            <% if (contentData != null) {
                try {
                    out.write(contentData.getContent(rdata));
                } catch (Exception ignore) {
                }
            }%>
        </div>
    </main>
</div>
<div class="container fixed-bottom">
    <footer class="footer">
        <%=Footer.getHtml(contentData, currentUser)%>
    </footer>
</div>
<div class="modal" id="modalDialog" tabindex="-1" role="dialog"></div>
</body>
</html>
