/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
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
