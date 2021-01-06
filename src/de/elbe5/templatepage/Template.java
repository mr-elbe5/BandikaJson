package de.elbe5.templatepage;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import org.apache.commons.io.FileUtils;
import org.jsoup.select.Elements;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

abstract public class Template {

    protected String fileStart = """
            <?xml version="1.0" encoding="UTF-8"?>
            <template type="%s" name="%s">
            """;
    protected String fileEnd = "\n</template>";

    public static final String TYPE_PAGE = "page";
    public static final String TYPE_PART = "part";

    public static final String STARTTAG_START = "<tpl ";
    public static final String END_TAG = "</tpl>";

    public static String getTemplatePath(String name){
        return ApplicationPath.getAppTemplatePath() + "/" + name + ".xml";
    }

    protected String type = "";
    protected String name = "";
    protected String code = "";

    public Template() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return getTemplatePath(getName());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void readAttributes(Elements element){
        try {
            setType(element.attr("type"));
            setName(element.attr("name"));
            setCode(element.html());
        }
        catch(Exception e){
            Log.error("could not read template", e);
        }
    }

    public void save(){
        try {
            File file = new File(getTemplatePath(getName()));
            String str = String.format(fileStart, getType(), getName()) +
                    getCode() +
                    fileEnd;
            FileUtils.writeStringToFile(file, str);
        }
        catch(Exception e){
            Log.error("could not save template", e);
        }
    }

    public String processTemplate(TemplateContext context){
        //Log.log("template process template");
        StringBuilder sb = new StringBuilder();
        try {
            processCode(sb, context);
            return sb.toString();
        }
        catch (TemplateException e){
            Log.error("could not process template", e);
        }
        return "";
    }

    public void processCode(StringBuilder sb, TemplateContext context) throws TemplateException {
        //Log.log("template process code");
        processString(code, sb, context);
    }

    public void processString(String src, StringBuilder sb, TemplateContext context) throws TemplateException {
        int pos1;
        int pos2 = 0;
        boolean shortTag;
        while (true) {
            pos1 = src.indexOf(STARTTAG_START, pos2);
            if (pos1 == -1) {
                sb.append(src.substring(pos2));
                break;
            }
            sb.append(src.substring(pos2, pos1));
            pos2 = getTagEnd(pos1 + STARTTAG_START.length());
            if (pos2 == -1)
                throw new TemplateException("no tag end");
            String startTag=src.substring(pos1,pos2);
            shortTag=false;
            if (startTag.endsWith("/")) {
                startTag = startTag.substring(0, startTag.length() - 1);
                shortTag=true;
            }
            Map<String, String> attributes = getAttributes(startTag);
            if (!attributes.containsKey("type"))
                throw new TemplateException("tag without type");
            String type= attributes.get("type");
            attributes.remove("type");
            //no content
            if (shortTag) {
                processTag(sb, type, attributes, null, context);
                pos2++;
                continue;
            }
            pos2++;
            pos1 = src.indexOf(END_TAG, pos2);
            if (pos1 == -1)
                throw new TemplateException("no end tag ");
            String content = src.substring(pos2, pos1).trim();
            pos2 = pos1 + END_TAG.length();
            processTag(sb, type, attributes, content, context);
        }
    }

    public abstract void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, TemplateContext context) throws TemplateException;
    
    protected Map<String, String> getAttributes(String src){
        //Log.log("template get attributes");
        Map<String, String> map = new HashMap<>();
        boolean inString = false;
        char ch;
        int lastBlank = 0;
        for (int i = 0; i < src.length(); i++) {
            ch = src.charAt(i);
            if (ch == '\"')
                inString = !inString;
            if ((ch == ' ' && !inString) || i == src.length() - 1) {
                String attributesString = (i == src.length() - 1 ? src.substring(lastBlank) : src.substring(lastBlank, i));
                int pos = attributesString.indexOf('=');
                if (pos != -1) {
                    String key = attributesString.substring(0, pos).trim();
                    String value = attributesString.substring(pos + 1).trim();
                    if (value.startsWith("\""))
                        value = value.substring(1);
                    if (value.endsWith("\""))
                        value = value.substring(0, value.length() - 1);
                    map.put(key, value.trim());
                }
                lastBlank = i;
            }
        }
        //Log.log("template attributes = " + map.toString());
        return map;
    }

    protected int getTagEnd(int from){
        boolean inQuotes = false;
        char ch;
        for (int i = from; i<code.length(); i++){
            ch = code.charAt(i);
            switch (ch) {
                case '"':
                    inQuotes = !inQuotes;
                    break;
                case '>':
                    if (!inQuotes)
                        return i;
                    break;
            }
        }
        return -1;
    }

}
