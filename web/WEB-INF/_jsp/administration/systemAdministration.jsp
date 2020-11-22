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
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.search.SearchData" %>
<%@ page import="de.elbe5.rights.SystemRights" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();%>
<div id="pageContent">
    <form:message/>
    <section class="treeSection">
        <ul class="tree">
            <li class="open">
                <a class="treeRoot"><%=$SH("_system",locale)%>
                </a>
                <ul>
                    <%if (SystemRights.hasUserSystemRight(rdata.getCurrentUser(),SystemZone.SYSTEM)) {%>
                    <li>
                        <a href="" onclick="if (confirmExecute()) return linkTo('/ctrl/admin/restart');"><%=$SH("_restart",locale)%>
                        </a>
                    </li>
                    <li>
                        <a href="/ctrl/admin/checkChanges"><%=$SH("_checkChanges",locale)%>
                        </a>
                    </li>
                    <li>
                        <a href="/ctrl/admin/runCleanup"><%=$SH("_runCleanup",locale)%>
                        </a>
                    </li>
                    <li>
                        <a href="/ctrl/<%=SearchData.TYPE_KEY%>/indexAllContent" ><%=$SH("_indexAllContent",locale)%></a>
                    </li>
                    <li>
                        <a href="/ctrl/admin/createBackup"><%=$SH("_createBackup",locale)%>
                        </a>
                    </li>
                    <li>
                        <a href="" onclick="return openModalDialog('/ajax/admin/openRestoreBackup');"><%=$SH("_restoreBackup",locale)%>
                        </a>
                    </li>
                    <li>
                        <a href="" onclick="return openModalDialog('/ajax/admin/openEditConfiguration');"><%=$SH("_configuration",locale)%></a>
                    </li>
                    <%}%>
                </ul>
            </li>
        </ul>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>
