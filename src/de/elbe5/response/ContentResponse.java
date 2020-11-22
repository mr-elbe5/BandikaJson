package de.elbe5.response;

import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ContentResponse extends MasterResponse {

    private final ContentData data;

    public ContentResponse(ContentData data) {
        this.data=data;
    }

    public ContentResponse(ContentData data, String master) {
        super(master);
        this.data=data;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        //Log.log("process view");
        rdata.setCurrentRequestContent(data);
        super.processResponse(context, rdata, response);
    }
}
