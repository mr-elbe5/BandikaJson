package de.elbe5.sectionpage;

import de.elbe5.request.RequestData;

public class TemplateContext {

    private final RequestData requestData;
    private final SectionPageData pageData;

    public TemplateContext(RequestData requestData, SectionPageData pageData){
        this.requestData = requestData;
        this.pageData = pageData;
    }

    public RequestData getRequestData() {
        return requestData;
    }

    public SectionPageData getPageData() {
        return pageData;
    }
}
