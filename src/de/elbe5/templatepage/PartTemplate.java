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
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentData;
import de.elbe5.content.ViewType;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.template.Template;
import de.elbe5.template.TemplateCache;
import de.elbe5.template.TemplateException;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PartTemplate extends Template {

    protected String css = "";

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    static String editPartHtmlStart = """
        <div id="{1}" class="partWrapper {2}" title="{3}">
        """;
    static String viewPartHtmlStart ="""
        <div id="{1}" class="partWrapper {2}">
        """;
    static String htmlEnd ="</div>";

    @Override
    public void readAttributes(Elements element){
        super.readAttributes(element);
        try {
            setCss(element.attr("css"));
        }
        catch(Exception e){
            Log.error("could not read template", e);
        }
    }

    @Override
    public void processTemplate(StringBuilder sb, RequestData rdata) throws TemplateException {
        TemplatePageContext context = rdata.getViewContext(TemplatePageContext.class);
        TemplatePartData partData = context.currentPart;
        if (context.getViewType() == ViewType.edit) {
            sb.append(StringUtil.format(editPartHtmlStart,
                    context.currentPart.getPartWrapperId(),
                    StringUtil.toHtml(getCss()),
                    StringUtil.toHtml(partData.getEditTitle(rdata.getLocale()))
            ));
            appendEditPartHeader(sb, context, rdata);
        }
        else{
            sb.append(StringUtil.format(viewPartHtmlStart,
                    partData.getPartWrapperId(),
                    StringUtil.toHtml(getCss())
            ));
        }
        super.processTemplate(sb, rdata);
        sb.append(htmlEnd);
    }

    @Override
    public void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, RequestData rdata) throws TemplateException{
        TemplatePageContext context = rdata.getViewContext(TemplatePageContext.class);
        switch(type){
            case TextField.TYPE_KEY -> processTextField(sb, context.currentPart, attributes, content, context);
            case HtmlField.TYPE_KEY -> processHtmlField(sb, context.currentPart, attributes, content, context);
            case ScriptField.TYPE_KEY -> processScriptField(sb, context.currentPart, attributes, content, context);
        }
    }

    final String editPartHeaderStartHtml = """
                        <input type="hidden" name="{1}" value="{2}" class="partPos"/>
                        <div class="partEditButtons">
                            <div class="btn-group btn-group-sm" role="group">
                                <div class="btn-group btn-group-sm" role="group">
                                    <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{3}"></button>
                                    <div class="dropdown-menu">
            """;

    final String editPartHeaderEndHtml = """
                        
                                    </div>
                                </div>
                                <div class="btn-group btn-group-sm" role="group">
                                    <button type="button" class="btn  btn-secondary dropdown-toggle fa fa-ellipsis-h" data-toggle="dropdown" title="{1}"></button>
                                    <div class="dropdown-menu">
                                        <a class="dropdown-item" href="" onclick="return movePart({2},-1);">{3}
                                        </a>
                                        <a class="dropdown-item" href="" onclick="return movePart({4},1);">{5}
                                        </a>
                                        <a class="dropdown-item" href="" onclick="if (confirmDelete()) return deletePart({6});">{7}
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
            """;

    final String partLinkHtml = """
                                        <a class="dropdown-item" href="" onclick="return addPart({1},'{2}','{3}','{4}');">
                                             {5}
                                        </a>
            """;


    private void appendEditPartHeader(StringBuilder sb, TemplatePageContext context, RequestData rdata){
        List<String> partTypes = new ArrayList<>();
        context.getPage().collectPartTypes(partTypes);
        Locale locale = rdata.getLocale();
        int partId = context.currentPart.getId();
        sb.append(StringUtil.format(editPartHeaderStartHtml,
                context.currentPart.getPartPositionName(),
                Integer.toString(context.currentPart.getPosition()),
                Strings.html("_newPart",locale)
        ));
        for (String partType : partTypes) {
            for (PartTemplate template : TemplateCache.getPartTemplates().values()){
                sb.append(StringUtil.format(partLinkHtml,
                        Integer.toString(partId),
                        StringUtil.toHtml(context.currentPart.getSectionName()),
                        partType,
                        StringUtil.toHtml(template.getName()),
                        StringUtil.toHtml(template.getDisplayName())
                ));
            }
        }
        sb.append(StringUtil.format(editPartHeaderEndHtml,
                Strings.html("_more",locale),
                Integer.toString(partId),
                Strings.html("_up", locale),
                Integer.toString(partId),
                Strings.html("_down", locale),
                Integer.toString(partId),
                Strings.html("_delete", locale)
        ));
    }

    final String textAreaTag = """
            <textarea class="editField" name="{1}" rows="{2}">{3}</textarea>
            """;
    final String textLineTag = """
            <input type="text" class="editField" name="{1}" placeholder="{2}" value="{3}" />
            """;

    private void processTextField(StringBuilder sb, TemplatePartData partData, Map<String,String> attributes, String content, TemplatePageContext context){
        TextField field = partData.ensureTextField(attributes.get("name"));
        boolean editMode = context.getViewType().equals(ViewType.edit);
        if (editMode) {
            int rows = 1;
            try{ rows = Integer.parseInt(attributes.get("rows"));}catch (Exception ignore){}
            if (rows > 1)
                sb.append(StringUtil.format(textAreaTag,
                        field.getIdentifier(),
                        Integer.toString(rows),
                        StringUtil.toHtml(field.getContent().isEmpty() ? content : field.getContent())
                ));
            else
                sb.append(StringUtil.format(textLineTag,
                        field.getIdentifier(),
                        field.getIdentifier(),
                        StringUtil.toHtml(field.getContent().isEmpty() ? content : field.getContent())
                ));
        } else {
            if (content.length() == 0) {
                sb.append("&nbsp;");
            } else {
                sb.append(StringUtil.toHtmlMultiline(field.getContent()));
            }
        }
    }

    final String htmlTagScript = """
            <div class="ckeditField" id="{1}" contenteditable="true">{2}</div>
                  <input type="hidden" name="{3}" value="{4}" />
                  <script type="text/javascript">
                        $('#{5}').ckeditor({toolbar : 'Full',filebrowserBrowseUrl : '/ajax/ckeditor/openLinkBrowser?contentId={6}',filebrowserImageBrowseUrl : '/ajax/ckeditor/openImageBrowser?contentId={7}'});
                  </script>
            """;

    private void processHtmlField(StringBuilder sb, TemplatePartData partData, Map<String,String> attributes, String content, TemplatePageContext context){
        HtmlField field = partData.ensureHtmlField(attributes.get("name"));
        boolean editMode = context.getViewType().equals(ViewType.edit);
        if (editMode) {
            sb.append(StringUtil.format(htmlTagScript,
                    field.getIdentifier(),
                    field.getContent().isEmpty() ? StringUtil.toHtml(content) : field.getContent(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getContent()),
                    field.getIdentifier(),
                    Integer.toString(context.getPage().getId()),
                    Integer.toString(context.getPage().getId())
            ));
        } else {
            try {
                if (!field.getContent().isEmpty()) {
                    sb.append(field.getContent());
                }
            } catch (Exception ignored) {
            }
        }
    }

    final String scriptEditTag = """
            <textarea class="editField" name="{1}" rows="5" >{2}</textarea>
            """;
    final String scriptViewTag = """
            <script type="text/javascript">{1}</script>
            """;

    private void processScriptField(StringBuilder sb, TemplatePartData partData, Map<String,String> attributes, String content, TemplatePageContext context){
        ScriptField field = partData.ensureScriptField(attributes.get("name"));
        boolean editMode = context.getViewType().equals(ViewType.edit);
        if (editMode) {
            sb.append(StringUtil.format(scriptEditTag,
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getContent().isEmpty() ? content : field.getContent())
            ));
        } else {
            if (!field.getContent().isEmpty()) {
                sb.append(StringUtil.format(scriptViewTag,
                        StringUtil.toHtml(field.getContent().isEmpty() ? content : field.getContent())
                ));
            }
        }
    }

    final String newPartScript = """
            <script type="text/javascript">
                updatePartEditors($('#{1}'));
            </script> 
            """;

    public String getNewPartScript(TemplatePartData partData){
        return StringUtil.format(newPartScript, partData.getPartWrapperId());
    }

}
