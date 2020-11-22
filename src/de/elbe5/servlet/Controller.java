package de.elbe5.servlet;

import de.elbe5.application.Strings;
import de.elbe5.content.JspContentData;
import de.elbe5.data.*;
import de.elbe5.request.*;
import de.elbe5.response.IResponse;
import de.elbe5.response.ContentResponse;
import de.elbe5.response.ForwardResponse;

import javax.servlet.http.HttpServletResponse;

public abstract class Controller implements DataAccessor {

    public abstract String getKey();

    protected IResponse showHome() {
        return new ForwardResponse("/");
    }

    protected void checkRights(boolean hasRights){
        if (!hasRights)
            throw new ResponseException(HttpServletResponse.SC_UNAUTHORIZED);
    }

    protected void setSuccess(SessionRequestData rdata, String key) {
        rdata.setMessage(Strings.string(key,rdata.getLocale()), RequestData.MESSAGE_TYPE_SUCCESS);
    }

    protected void setError(SessionRequestData rdata, String key) {
        rdata.setMessage(Strings.string(key,rdata.getLocale()), RequestData.MESSAGE_TYPE_ERROR);
    }

    protected IResponse openAdminPage(SessionRequestData rdata, String jsp, String title) {
        rdata.put(RequestData.KEY_JSP, jsp);
        rdata.put(RequestData.KEY_TITLE, title);
        return new ForwardResponse("/WEB-INF/_jsp/administration/adminMaster.jsp");
    }

    protected IResponse showSystemAdministration(SessionRequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/systemAdministration.jsp", Strings.string("_systemAdministration",rdata.getLocale()));
    }

    protected IResponse showPersonAdministration(SessionRequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/personAdministration.jsp", Strings.string("_personAdministration",rdata.getLocale()));
    }

    protected IResponse showContentAdministration(SessionRequestData rdata) {
        return openAdminPage(rdata, "/WEB-INF/_jsp/administration/contentAdministration.jsp", Strings.string("_contentAdministration",rdata.getLocale()));
    }

    protected IResponse openJspPage(String jsp) {
        JspContentData contentData = new JspContentData();
        contentData.setJsp(jsp);
        return new ContentResponse(contentData);
    }
}
