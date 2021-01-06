package de.elbe5.templatepage;

import de.elbe5.application.ApplicationPath;
import de.elbe5.base.log.Log;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;

class DefaultTemplates {

    public void createDefaultTemplates() {
        try {
            File file = new File(ApplicationPath.getAppPath() + "/WEB-INF/defaultTemplates.xml");
            String xml = FileUtils.readFileToString(file);
            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            Elements elements = doc.getElementsByTag("template");
            for (Element element : elements) {
                String name = element.attr("name");
                File templateFile = new File(Template.getTemplatePath(name));
                FileUtils.writeStringToFile(templateFile, element.toString());
            }
        } catch (Exception e) {
            Log.error("could not read defaultTemplates.xml", e);
        }
    }

}
