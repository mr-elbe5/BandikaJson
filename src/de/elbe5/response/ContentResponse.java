package de.elbe5.response;

import de.elbe5.content.ContentData;
import de.elbe5.content.ViewType;
import de.elbe5.request.RequestData;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentResponse implements IResponse {

    protected final ContentData contentData;
    protected ViewType viewType = ViewType.show;

    public ContentResponse(ContentData contentData) {
        this.contentData=contentData;
    }

    public ContentResponse(ContentData contentData, ViewType viewType) {
        this.contentData=contentData;
        this.viewType = viewType;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        //Log.log("process view");
        rdata.setViewContext(contentData.createViewContext(viewType));
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/_master/"+contentData.getMaster()+".jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
