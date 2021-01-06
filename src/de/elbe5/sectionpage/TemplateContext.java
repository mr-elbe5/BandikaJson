package de.elbe5.sectionpage;

import de.elbe5.request.RequestData;

public class TemplateContext {

    public final RequestData requestData;
    public final SectionPageData pageData;

    public SectionData currentSection = null;
    public SectionPartData currentPart = null;

    public TemplateContext(RequestData requestData, SectionPageData pageData){
        this.requestData = requestData;
        this.pageData = pageData;
    }

}
