package de.elbe5.sectionpage;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TemplateCache {

    private static final Map<String,PageTemplate> pageTemplates = new HashMap<>();
    private static final Map<String,PartTemplate> partTemplates = new HashMap<>();

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
        Log.log(pageTemplates.size() + " page templates loaded");
        Log.log(partTemplates.size() + " part templates loaded");
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
