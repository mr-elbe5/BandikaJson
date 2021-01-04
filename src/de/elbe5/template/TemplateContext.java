package de.elbe5.template;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;
import de.elbe5.page.PageData;
import de.elbe5.request.RequestData;

public class TemplateContext {

    private final TemplateVariables vars = new TemplateVariables();
    private final RequestData requestData;
    private final PageData pageData;

    public TemplateContext(RequestData requestData, PageData pageData){
        this.requestData = requestData;
        this.pageData = pageData;
        addVariable("applicationName", Application.getConfiguration().getApplicationName());
        addVariable("stylesheet", Application.getConfiguration().getStyle());
        addVariable("title", Application.getConfiguration().getApplicationName());
    }

    public void addVariable(String key, String value){
        vars.put(key,value);
    }

    public String  getVariable(String key) {
        return vars.get(key);
    }

    public RequestData getRequestData() {
        return requestData;
    }

    public PageData getPageData() {
        return pageData;
    }

    public String replaceVariables(String src){
        String result = src;
        for (String key : vars.keySet()){
            String var = "%tpl-var-"+key+"%";
            //Log.log("replacing "+var);
            result = result.replaceAll(var, vars.get(key));
        }
        return result;
    }
}
