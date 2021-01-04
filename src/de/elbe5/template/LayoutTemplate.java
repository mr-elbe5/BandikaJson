package de.elbe5.template;

import de.elbe5.base.log.Log;

import java.util.Map;

public class LayoutTemplate extends Template{

    public void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, TemplateContext context) throws TemplateException {
        switch (type) {
            case "sysnavLinks" -> {
                processSysnavTag(sb, attributes, context);
            }
            case "menuLinks" -> {
                processMenuTag(sb, attributes, context);
            }
            case "breadcrumbLinks" -> {
                processBreadcrumbTag(sb, attributes, context);
            }
            case "pageContent" -> {
                processPageTag(sb, context);
            }
            case "footerLinks" -> {
                processFooterTag(sb, attributes, context);
            }
            default ->{
                throw new TemplateException("unknown tag type: " + type);
            }
        }
    }

    public void processSysnavTag(StringBuilder sb, Map<String,String> attributes, TemplateContext context) throws TemplateException{
        Log.log("layout template process sysnav");
    }

    public void processMenuTag(StringBuilder sb, Map<String,String> attributes, TemplateContext context) throws TemplateException{
        Log.log("layout template process menu");
    }

    public void processBreadcrumbTag(StringBuilder sb, Map<String,String> attributes, TemplateContext context) throws TemplateException{
        Log.log("layout template process breadcrumb");
    }

    public void processPageTag(StringBuilder sb, TemplateContext context) throws TemplateException{
        Log.log("layout template process page content");
        context.getPageData().processContent(sb, context);
    }

    public void processFooterTag(StringBuilder sb, Map<String,String> attributes, TemplateContext context) throws TemplateException{
        Log.log("layout template process footer");
    }

}
