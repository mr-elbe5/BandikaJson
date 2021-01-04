package de.elbe5.template;

import de.elbe5.application.Application;
import de.elbe5.base.log.Log;

import java.util.Map;

public class MasterTemplate extends Template{

    public void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, TemplateContext context) throws TemplateException {
        Log.log("master process tag");
        switch (type) {
            case "layout" -> {
                processLayoutTag(sb, context);
            }
            default ->{
                throw new TemplateException("unknown tag type: "+type);
            }
        }
    }

    public void processLayoutTag(StringBuilder sb, TemplateContext context) throws TemplateException{
        Log.log("master process layout");
        String layout = Application.getConfiguration().getLayoutName();
        LayoutTemplate template = TemplateCache.getLayoutTemplate(layout);
        assert(template!=null);
        template.processCode(sb, context);
    }
}
