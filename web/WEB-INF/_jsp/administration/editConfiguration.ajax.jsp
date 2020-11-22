<%--
  BandikaJson CMS - A Java based Content Management System with JSON Database
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/functions.inc.jsp" %>
<%@ page import="de.elbe5.base.mail.MailSender" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    Configuration configuration = (Configuration) rdata.getSessionObject("config");
    assert (configuration != null);
    MailSender.SmtpConnectionType[] connectionTypes = MailSender.SmtpConnectionType.values();
    String url = "/ajax/admin/saveConfiguration";
%>
<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_settings", locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="appform" ajax="true">
            <div class="modal-body">
                <form:message/>
                <form:text name="applicationName" label="_applicationName" value="<%=configuration.getApplicationName()%>">
                </form:text>
                <form:select name="theme" label="_theme">
                    <% for (String themeName : Configuration.THEME_NAMES){
                        String key = "theme." + themeName;
                    %>
                    <option value="<%=themeName%>" <%=configuration.getTheme().equals(themeName) ? "selected" : ""%>><%=$SH(key, locale)%>
                    </option>
                    <%}%>
                </form:select>
                <% String label = $SH("_logo",locale) + " (" + $SH("_logoInfo",locale) + ")"; %>
                <form:file name="logo" label="<%=label%>" required="false"/>
                <form:text name="copyright" label="_copyright" value="<%=configuration.getCopyright()%>">
                </form:text>
                <form:text name="smtpHost" label="_smtpHost" value="<%=configuration.getSmtpHost()%>">
                </form:text>
                <form:text name="smtpPort" label="_smtpPort" value="<%=$I(configuration.getSmtpPort())%>">
                </form:text>
                <form:select name="smtpConnectionType" label="_smtpConnectionType">
                    <% for (MailSender.SmtpConnectionType ctype : connectionTypes) {%>
                    <option value="<%=ctype.name()%>" <%=ctype.equals(configuration.getSmtpConnectionType()) ? "selected" : ""%>><%=ctype.name()%>
                    </option>
                    <%}%>
                </form:select>
                <form:text name="smtpUser" label="_smtpUser" value="<%=configuration.getSmtpUser()%>">
                </form:text>
                <form:text name="smtpPassword" label="_password" value="<%=configuration.getSmtpPassword()%>">
                </form:text>
                <form:text name="mailSendingUser" label="_emailSender" value="<%=configuration.getMailSendingUser()%>">
                </form:text>
                <form:text name="mailReceivingUser" label="_emailReceiver" value="<%=configuration.getMailReceivingUser()%>">
                </form:text>
                <form:text name="timerInterval" label="_timerInterval" value="<%=$I(configuration.getTimerInterval())%>">
                </form:text>
                <form:text name="serviceInterval" label="_serviceInterval" value="<%=$I(configuration.getServiceInterval())%>">
                </form:text>
                <form:text name="cleanupInterval" label="_cleanupInterval" value="<%=$I(configuration.getCleanupInterval())%>">
                </form:text>
                <form:text name="indexInterval" label="_indexInterval" value="<%=$I(configuration.getIndexInterval())%>">
                </form:text>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=$SH("_close", locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save", locale)%>
                </button>
            </div>
        </form:form>
    </div>
</div>

