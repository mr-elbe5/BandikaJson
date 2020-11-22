<%--
  BandikaJson CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/_jsp/_include/functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.rights.SystemRights" %>
<%@ page import="de.elbe5.rights.ContentRights" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ContentData contentRoot = contentContainer().getContentRoot();
    assert(contentRoot !=null);
    Locale locale = rdata.getLocale();
    List<String> childTypes= new ArrayList<>();
    contentRoot.collectChildTypes(childTypes);
%>
            <section class="treeSection">
                <div><a href="/ctrl/content/clearClipboard"><%=$SH("_clearClipboard",locale)%></a></div>
                <ul class="tree pagetree">
                    <% if (ContentRights.hasUserReadRight(rdata.getCurrentUser(),ContentData.ID_ROOT)) {%>
                    <li class="open">
                        <span>
                            <%=$H(contentRoot.getDisplayName())%>
                        </span>
                        <% if (SystemRights.hasUserSystemRight(rdata.getCurrentUser(), SystemZone.CONTENTEDIT)) {
                            rdata.setCurrentRequestContent(contentRoot);%>
                        <div class="icons">
                            <a class="icon fa fa-eye" href="" onclick="return linkTo('/ctrl/<%=contentRoot.getTypeKey()%>/show/<%=contentRoot.getId()%>');" title="<%=$SH("_view",locale)%>"> </a>
                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ajax/<%=contentRoot.getTypeKey()%>/openEditContentData/<%=contentRoot.getId()%>');" title="<%=$SH("_edit",locale)%>"> </a>
                            <a class="icon fa fa-key" href="" onclick="return openModalDialog('/ajax/<%=contentRoot.getTypeKey()%>/openEditRights/<%=contentRoot.getId()%>');" title="<%=$SH("_rights",locale)%>"> </a>
                            <%if (contentRoot.hasChildren()){%>
                            <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/ajax/<%=contentRoot.getTypeKey()%>/openSortChildPages/<%=contentRoot.getId()%>');" title="<%=$SH("_sortChildPages",locale)%>"> </a>
                            <%}%>
                            <% if (rdata.hasClipboardData(RequestData.KEY_CONTENT)) {%>
                            <a class="icon fa fa-paste" href="/ctrl/<%=contentRoot.getTypeKey()%>/pasteContent?parentId=<%=ContentData.ID_ROOT%>" title="<%=$SH("_pasteContent",locale)%>"> </a>
                            <%
                                }
                                if (!childTypes.isEmpty()) {%>
                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=$SH("_newContent",locale)%>"></a>
                            <div class="dropdown-menu">
                                <%for (String pageType : childTypes) {
                                    String name = $SH("type." + pageType, locale);%>
                                <a class="dropdown-item" onclick="return openModalDialog('/ajax/<%=contentRoot.getTypeKey()%>/openCreateContentData?parentId=<%=ContentData.ID_ROOT%>&type=<%=pageType%>');"><%=name%>
                                </a>
                                <%
                                    }%>
                            </div>
                            <%
                                }%>
                        </div>
                        <ul>
                            <jsp:include page="/WEB-INF/_jsp/content/treeContentFiles.inc.jsp" flush="true" />
                            <% if (contentRoot.hasChildren()) {
                                for (ContentData childData : contentRoot.getChildren()) {
                                    if (ContentRights.hasUserReadRight(rdata.getCurrentUser(), childData)) {
                                        rdata.setCurrentRequestContent(childData); %>
                            <jsp:include page="/WEB-INF/_jsp/content/treeContent.inc.jsp" />
                                <% rdata.setCurrentRequestContent(contentRoot);
                                    }
                                }
                            }%>
                        </ul>
                        <% rdata.setCurrentRequestContent(contentRoot);
                        }%>
                </ul>
                <%}%>
            </section>
            <script type="text/javascript">
                let $current = $('.current','.pagetree');
                if ($current){
                    let $parents=$current.parents('li');
                    $parents.addClass("open");
                }
            </script>


