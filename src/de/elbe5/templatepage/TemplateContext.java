package de.elbe5.templatepage;

import de.elbe5.request.RequestData;

public class TemplateContext {

    public final RequestData requestData;
    public final TemplatePageData pageData;

    public SectionData currentSection = null;
    public TemplatePartData currentPart = null;

    public TemplateContext(RequestData requestData, TemplatePageData pageData){
        this.requestData = requestData;
        this.pageData = pageData;
    }

}
