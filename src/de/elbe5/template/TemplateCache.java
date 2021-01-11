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
import de.elbe5.templatepage.PageTemplate;
import de.elbe5.templatepage.PartTemplate;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TemplateCache {

    private static final Map<String, PageTemplate> pageTemplates = new HashMap<>();
    private static final Map<String, PartTemplate> partTemplates = new HashMap<>();

    public static void loadTemplates(){
        File[] files = ApplicationPath.getTemplateDirectory().listFiles();
        if (files==null || files.length == 0){
            new DefaultTemplates().createDefaultTemplates();
            files = ApplicationPath.getTemplateDirectory().listFiles();
            assert files!=null;
        }
        for (File file : files){
            try {
                String xml = FileUtils.readFileToString(file);
                Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
                Elements element = doc.getElementsByTag("template");
                String type = (element.attr("type"));
                switch (type) {
                    case Template.TYPE_PAGE -> {
                        PageTemplate template = new PageTemplate();
                        template.readAttributes(element);
                        pageTemplates.put(template.getName(), template);
                    }
                    case Template.TYPE_PART -> {
                        PartTemplate template = new PartTemplate();
                        template.readAttributes(element);
                        partTemplates.put(template.getName(), template);
                    }
                }
            }
            catch(Exception e){
                Log.error("could not read template", e);
            }
        }
        Log.log(pageTemplates.size() + " page template(s) loaded");
        Log.log(partTemplates.size() + " part template(s) loaded");
    }

    public static Map<String, PageTemplate> getPageTemplates() {
        return pageTemplates;
    }

    public static Map<String, PartTemplate> getPartTemplates() {
        return partTemplates;
    }

    public static PageTemplate getPageTemplate(String name){
        if (pageTemplates.containsKey(name))
            return pageTemplates.get(name);
        return null;
    }

    public static PartTemplate getPartTemplate(String name){
        if (partTemplates.containsKey(name))
            return partTemplates.get(name);
        return null;
    }

}
