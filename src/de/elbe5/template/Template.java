/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.template;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import de.elbe5.request.RequestData;
import org.apache.commons.io.FileUtils;
import org.jsoup.select.Elements;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

abstract public class Template {

    protected String fileStart = """
            <?xml version="1.0" encoding="UTF-8"?>
            <template type="%s" name="%s" displayName="%s">
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
    protected String displayName = "";
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
            setDisplayName(element.attr("displayName"));
            setCode(element.html());
        }
        catch(Exception e){
            Log.error("could not read template", e);
        }
    }

    public void save(){
        try {
            File file = new File(getTemplatePath(getName()));
            String str = String.format(fileStart, getType(), getName(), getDisplayName()) +
                    getCode() +
                    fileEnd;
            FileUtils.writeStringToFile(file, str);
        }
        catch(Exception e){
            Log.error("could not save template", e);
        }
    }

    public void processTemplate(StringBuilder sb, RequestData rdata){
        //Log.log("template process template");
        try {
            processString(code, sb, rdata);
        }
        catch (TemplateException e){
            Log.error("could not process template", e);
        }
    }

    public void processString(String src, StringBuilder sb, RequestData rdata) throws TemplateException {
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
                processTag(sb, type, attributes, null, rdata);
                pos2++;
                continue;
            }
            pos2++;
            pos1 = src.indexOf(END_TAG, pos2);
            if (pos1 == -1)
                throw new TemplateException("no end tag ");
            String content = src.substring(pos2, pos1).trim();
            pos2 = pos1 + END_TAG.length();
            processTag(sb, type, attributes, content, rdata);
        }
    }

    public abstract void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, RequestData rdata) throws TemplateException;
    
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
