/*
 BandikaJson CMS - A Java based Content Management System with JSON Database
 Copyright (C) 2009-2020 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.application.Strings;
import de.elbe5.base.data.StringUtil;
import de.elbe5.tag.MessageHtml;
import de.elbe5.template.Template;
import de.elbe5.template.TemplateCache;
import de.elbe5.template.TemplateContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PageTemplate extends Template {

    public void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, TemplateContext context){
        switch(type){
            case "message" -> {
                sb.append(MessageHtml.getHtml(context.requestData));
            }
            case "section" -> {
                SectionData sectionData = context.pageData.ensureSection(attributes.get("name"));
                if (sectionData != null) {
                    sectionData.setCssClass(attributes.get("css"));
                    context.currentSection = sectionData;
                    if (context.pageData.isEditing()) {
                        getEditSectionHtml(sb, sectionData, attributes, context);
                    } else {
                        getSectionHtml(sb, sectionData, context);
                    }
                    context.currentSection = null;
                }
            }
        }
    }

    final String editSectionHtmlStart = """
        <div class="section {1}" id="{2}" title="Section {3}">
            <div class="addPartButtons">
                <div class="btn-group btn-group-sm editheader">
                    <button class="btn  btn-primary dropdown-toggle fa fa-plus" data-toggle="dropdown"  title="{4}"></button>
                    <div class="dropdown-menu">
                    """;
    final String partTypeLink = """
                        <a class="dropdown-item" href="" onclick="return addPart(-1,'{1}','{2}','{3}');">
                            {4}
                        </a>
                        """;
    final String editSectionHtmlDropdownEnd = """
                    </div>
                 </div>
               </div>
                """;
    final String sectionEnd = """
          </div>
          """;

    private void getEditSectionHtml(StringBuilder sb, SectionData sectionData, Map<String,String> attributes, TemplateContext context){
        List<String> partTypes = new ArrayList<>();
        context.pageData.collectPartTypes(partTypes);
        Locale locale = context.requestData.getLocale();
        sb.append(StringUtil.format(editSectionHtmlStart,
            attributes.get("css"),
            sectionData.getSectionId(),
            StringUtil.toHtml(sectionData.getName()),
            Strings.html("_newPart",locale)
            ));
        for (String partType : partTypes) {
            for (PartTemplate template : TemplateCache.getPartTemplates().values()) {
                sb.append(StringUtil.format(partTypeLink,
                        StringUtil.toHtml(sectionData.getName()),
                        partType,
                        StringUtil.toHtml(template.getName()),
                        StringUtil.toHtml(template.getDisplayName())
                ));
            }
        }
        sb.append(editSectionHtmlDropdownEnd);
        for (TemplatePartData partData : sectionData.getParts()) {
            context.currentPart = partData;
            partData.appendHtml(sb, context);
            context.currentPart = null;
        }
        sb.append(sectionEnd);
    }

    final String sectionStart = """
            <div class="section {1}">
            """;

    private void getSectionHtml(StringBuilder sb, SectionData sectionData, TemplateContext context){
        sb.append(StringUtil.format(sectionStart,
                sectionData.getCssClass()
                ));
        for (TemplatePartData partData : sectionData.getParts()) {
            context.currentPart = partData;
            partData.appendHtml(sb, context);
            context.currentPart = null;
        }
        sb.append(sectionEnd);
    }

}
