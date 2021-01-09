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
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.rights.SystemRights" %>
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.request.RequestKeys" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    int callbackNum = rdata.getInt("CKEditorFuncNum", -1);
%>
<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_selectLink",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <form:message/>
            <ul class="nav nav-tabs" id="selectTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="pages-tab" data-toggle="tab" href="#pages" role="tab" aria-controls="pages" aria-selected="true"><%=$SH("_pages",locale)%>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="files-tab" data-toggle="tab" href="#files" role="tab" aria-controls="files" aria-selected="false"><%=$SH("_files",locale)%>
                    </a>
                </li>
            </ul>

            <div class="tab-content" id="pageTabContent">
                <div class="tab-pane fade show active" id="pages" role="tabpanel" aria-labelledby="pages-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
                            <%rdata.setRequestObject("treePage", contentContainer().getContentRoot());%>
                            <jsp:include page="/WEB-INF/_jsp/ckeditor/pageLinkBrowserFolder.inc.jsp" flush="true"/>
                            <%rdata.removeRequestObject("treePage");%>
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="files" role="tabpanel" aria-labelledby="files-tab">
                    <section class="treeSection">
                        <% if (SystemRights.hasUserSystemRight(rdata.getCurrentUser(), SystemZone.CONTENTEDIT)) { %>
                        <ul class="tree filetree">
                            <%rdata.setRequestObject(RequestKeys.KEY_CONTENT, contentContainer().getContentRoot());%>
                            <jsp:include page="/WEB-INF/_jsp/ckeditor/fileLinkBrowserFolder.inc.jsp" flush="true"/>
                        </ul>
                        <%rdata.removeRequestObject(RequestKeys.KEY_CONTENT); }%>
                    </section>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_cancel",locale)%>
            </button>
        </div>
    </div>
    <script type="text/javascript">
        $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
        function ckLinkCallback(url) {
            if (CKEDITOR)
                CKEDITOR.tools.callFunction(<%=callbackNum%>, url);
            return closeModalDialog();
        }
    </script>
</div>
