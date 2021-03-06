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
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    UserData user = userContainer().getUser(rdata.getCurrentUser().getId());
%>
<div id="pageContent">
    <form:message/>
    <h1 class="title">
        <%=$SH("_profile",locale)%>
    </h1>
    <div class="row">
        <section class="col-md-8 contentSection">
            <div class="paragraph form">
                <form:line label="_id"><%=$I(user.getId())%>
                </form:line>
                <form:line label="_login"><%=$H(user.getLogin())%>
                </form:line>
                <form:line label="_title"><%=$H(user.getTitle())%>
                </form:line>
                <form:line label="_firstName"><%=$H(user.getFirstName())%>
                </form:line>
                <form:line label="_lastName"><%=$H(user.getLastName())%>
                </form:line>
                <h3><%=$SH("_address",locale)%>
                </h3>
                <form:line label="_street"><%=$H(user.getStreet())%>
                </form:line>
                <form:line label="_zipCode"><%=$H(user.getZipCode())%>
                </form:line>
                <form:line label="_city"><%=$H(user.getCity())%>
                </form:line>
                <form:line label="_country"><%=$H(user.getCountry())%>
                </form:line>
                <h3><%=$SH("_contact",locale)%>
                </h3>
                <form:line label="_email"><%=$H(user.getEmail())%>
                </form:line>
                <form:line label="_phone"><%=$H(user.getPhone())%>
                </form:line>
            </div>
        </section>
        <aside class="col-md-4 asideSection">
            <div class="section">
                <div class="paragraph form">
                    <div>
                        <a class="link" href="#" onclick="return openModalDialog('/ajax/user/openChangePassword');"><%=$SH("_changePassword",locale)%>
                        </a>
                    </div>
                    <div>
                        <a class="link" href="#" onclick="return openModalDialog('/ajax/user/openChangeProfile');"><%=$SH("_changeProfile",locale)%>
                        </a>
                    </div>
                </div>
            </div>
        </aside>
    </div>
</div>
