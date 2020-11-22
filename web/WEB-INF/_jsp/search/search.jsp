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
<%@ page import="de.elbe5.search.SearchData" %>
<%@ page import="de.elbe5.search.SearchResultData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    SearchResultData pageResult = rdata.get("searchResultData", SearchResultData.class);
    assert(pageResult!=null);
%>
<div id="pageContent">
    <form:message/>
    <section class="contentTop">
        <h1>
            <%=$SH("_search",locale)%>
        </h1>
    </section>
    <section class="contentSection">
        <div class="paragraph">
            <form action="/ctrl/<%=SearchData.TYPE_KEY%>/search" method="post" id="searchboxform" name="searchboxform" accept-charset="UTF-8">
                <div class="input-group">
                    <label for="searchPattern"></label><input class="form-control mr-sm-2" id="searchPattern" name="searchPattern" maxlength="60" value="<%=$H(pageResult.getPattern())%>"/>
                    <button class="btn btn-outline-primary my-2 my-sm-0" type="submit"><%=$SH("_search",locale)%>
                    </button>
                </div>
            </form>
            <div class="searchResults">
                <% if (!pageResult.getResults().isEmpty()) {%>
                <h2><%=$SH("_searchResults",locale)%>
                </h2>
                <% for (SearchData data : pageResult.getResults()) {
                    String description = data.getDescriptionContext();
                    String content = data.getContentContext();%>
                <div class="searchResult">
                    <div class="searchTitle">
                        <a href="<%=data.getUrl()%>" title="<%=$SH("_show",locale)%>"><%=data.getNameContext()%>
                        </a>
                    </div>
                    <% if (!description.isEmpty()) {%>
                    <div class="searchDescription"><%=data.getDescriptionContext()%>
                    </div>
                    <% }
                        if (!content.isEmpty()) {%>
                    <div class="searchContent"><%=data.getContentContext()%>
                    </div>
                    <% }%>
                </div>
                <% }
                } else {%>
                <span><%=$SH("_noResults",locale)%></span>
                <%}%>
            </div>
        </div>
    </section>
</div>

