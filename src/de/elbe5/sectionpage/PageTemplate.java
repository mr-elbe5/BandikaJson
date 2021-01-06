package de.elbe5.sectionpage;

import de.elbe5.application.Strings;
import de.elbe5.base.data.StringUtil;
import de.elbe5.layout.Layouts;
import de.elbe5.tag.MessageHtml;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PageTemplate extends Template{

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
                    <div class="btn-group btn-group-sm editheader" title="Section {4}">
                        <button class="btn  btn-primary dropdown-toggle fa fa-plus" data-toggle="dropdown" title="{5}"></button>
                        <div class="dropdown-menu">
                        """;
    final String partTypeLink = """
                            <a class="dropdown-item" href="" onclick="return addPart(-1,'{1}','{2}','{3}');">
                                {4} ({5})
                            </a>
                            """;
    final String partTypeLastLink = """
                            <a class="dropdown-item" href="" onclick="return addPart(-1,'{1}','{2}');">
                                {3}
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
        List<String> layoutNames = Layouts.getLayoutNames(SectionPartData.LAYOUT_TYPE);
        Locale locale = context.requestData.getLocale();
        sb.append(StringUtil.format(editSectionHtmlStart,
                attributes.get("css"),
                sectionData.getSectionId(),
                sectionData.getName(),
                StringUtil.toHtml(sectionData.getName()),
                Strings.html("_newPart", locale)
                ));
        for (String partType : partTypes) {
            String name = Strings.html("type." + partType, locale);
            for (String layout : layoutNames) {
                String layoutName = Strings.html("layout." + layout, locale);
                sb.append(StringUtil.format(partTypeLink,
                        StringUtil.toHtml(sectionData.getName()),
                        partType,
                        StringUtil.toHtml(layout),
                        name,
                        layoutName
                ));
            }
            sb.append(StringUtil.format(partTypeLastLink,
                    StringUtil.toHtml(sectionData.getName()),
                    partType,
                    name
            ));
        }
        sb.append(editSectionHtmlDropdownEnd);
        for (SectionPartData partData : sectionData.getParts()) {
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
        for (SectionPartData partData : sectionData.getParts()) {
            context.currentPart = partData;
            partData.appendHtml(sb, context);
            context.currentPart = null;
        }
        sb.append(sectionEnd);
    }

}
