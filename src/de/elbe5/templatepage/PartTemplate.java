package de.elbe5.templatepage;

import de.elbe5.application.Strings;
import de.elbe5.base.data.StringUtil;
import de.elbe5.base.log.Log;
import de.elbe5.content.ContentData;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PartTemplate extends Template{

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
    public void processCode(StringBuilder sb, TemplateContext context) throws TemplateException {
        TemplatePartData partData = context.currentPart;
        if (context.pageData.isEditing()) {
            sb.append(StringUtil.format(editPartHtmlStart,
                    context.currentPart.getPartWrapperId(),
                    StringUtil.toHtml(getCss()),
                    StringUtil.toHtml(partData.getEditTitle(context.requestData.getLocale()))
            ));
            appendEditPartHeader(sb, context);
        }
        else{
            sb.append(StringUtil.format(viewPartHtmlStart,
                    partData.getPartWrapperId(),
                    StringUtil.toHtml(partData.getCssClass())
            ));
        }
        super.processCode(sb, context);
        sb.append(htmlEnd);
    }

    public void processTag(StringBuilder sb, String type, Map<String,String> attributes, String content, TemplateContext context) throws TemplateException{
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
                                             {5} ({6})
                                        </a>
            """;

    final String partLastLinkHtml = """
                                        <a class="dropdown-item" href="" onclick="return addPart({1},'{2}','{3}');">
                                                                                        {4}
                                                                                    </a>
            """;

    private void appendEditPartHeader(StringBuilder sb, TemplateContext context){
        List<String> partTypes = new ArrayList<>();
        context.pageData.collectPartTypes(partTypes);
        Locale locale = context.requestData.getLocale();
        int partId = context.currentPart.getId();
        sb.append(StringUtil.format(editPartHeaderStartHtml,
                context.currentPart.getPartPositionName(),
                Integer.toString(context.currentPart.getPosition()),
                Strings.html("_newPart",locale)
        ));
        for (String partType : partTypes) {
            String name = Strings.html("type." + partType, locale);
            for (String typeName : PartTypes.typeNames){
                String layoutName = Strings.html("layout."+typeName,locale);
                sb.append(StringUtil.format(partLinkHtml,
                        Integer.toString(partId),
                        StringUtil.toHtml(context.currentPart.getSectionName()),
                        partType,
                        StringUtil.toHtml(typeName),
                        name,
                        layoutName
                ));
            }
            sb.append(StringUtil.format(partLastLinkHtml,
                    Integer.toString(partId),
                    StringUtil.toHtml(context.currentPart.getSectionName()),
                    partType,
                    name
            ));
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

    private void processTextField(StringBuilder sb, TemplatePartData partData, Map<String,String> attributes, String content, TemplateContext context){
        TextField field = partData.ensureTextField(attributes.get("name"));
        boolean editMode = context.pageData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
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
                        StringUtil.toHtml(field.getContent())
                ));
        } else {
            if (content.length() == 0) {
                sb.append("&nbsp;");
            } else {
                sb.append(StringUtil.toHtmlMultiline(content));
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

    private void processHtmlField(StringBuilder sb, TemplatePartData partData, Map<String,String> attributes, String content, TemplateContext context){
        HtmlField field = partData.ensureHtmlField(attributes.get("name"));
        boolean editMode = context.pageData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
        if (editMode) {
            sb.append(StringUtil.format(htmlTagScript,
                    field.getIdentifier(),
                    field.getContent().isEmpty() ? StringUtil.toHtml(content) : field.getContent(),
                    field.getIdentifier(),
                    StringUtil.toHtml(field.getContent()),
                    field.getIdentifier(),
                    Integer.toString(context.pageData.getId()),
                    Integer.toString(context.pageData.getId())
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

    private void processScriptField(StringBuilder sb, TemplatePartData partData, Map<String,String> attributes, String content, TemplateContext context){
        ScriptField field = partData.ensureScriptField(attributes.get("name"));
        boolean editMode = context.pageData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
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

}
